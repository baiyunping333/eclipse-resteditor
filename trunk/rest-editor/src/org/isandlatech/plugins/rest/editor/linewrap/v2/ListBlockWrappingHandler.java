/**
 * File:   ListBlockWrappingHandler.java
 * Author: Thomas Calmant
 * Date:   27 mai 2011
 */
package org.isandlatech.plugins.rest.editor.linewrap.v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import org.eclipse.jface.text.TextUtilities;
import org.isandlatech.plugins.rest.parser.RestLanguage;

/**
 * @author Thomas Calmant
 * 
 */
public class ListBlockWrappingHandler extends AbstractBlockWrappingHandler {

	/** Handler type */
	public static String HANDLER_TYPE = "__list_block_handler__";

	/** The block indentation */
	private String pBlockIndent;

	/** List marker */
	private String pBullet;

	/** First line content */
	private String pFirstLineContent;

	/** The base line indentation */
	private String pFirstLineIndent;

	/** Block content, without the first line */
	private String pListBlockContent;

	/**
	 * Computes the first line and sub lines indentation to be used
	 * 
	 * {@link #extractBlock()} must be called before this method.
	 */
	private void computeIndentations() {

		// Retrieve its indentation
		pFirstLineIndent = pLineUtil.getIndentation(pFirstLineContent);

		// Retrieves its marker
		pBullet = getBulletMarker(pFirstLineContent);

		// Compute the indentation of other lines
		char[] sublineIndentArray = new char[pFirstLineIndent.length()
				+ pBullet.length()];
		Arrays.fill(sublineIndentArray, ' ');
		pBlockIndent = new String(sublineIndentArray);
	}

	/**
	 * Extracts the lines of block in 2 parts : the first line, with the base
	 * indentation, and the rest of the block, with a bigger indentation.
	 * 
	 * The internal list block content is left-trimmed.
	 * 
	 * @return True on success, False on error
	 */
	private boolean extractBlock() {

		String blockContent = getBlockContent();

		BufferedReader strReader = new BufferedReader(new StringReader(
				blockContent));

		try {
			pFirstLineContent = strReader.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}

		if (pFirstLineContent == null) {
			return false;
		}

		// Extract the sub lines
		StringBuilder builder = new StringBuilder();
		String line;

		try {
			while ((line = strReader.readLine()) != null) {
				builder.append(pLineUtil.ltrim(line));
				builder.append(' ');
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		// Don't forget to remove the last added space
		builder.deleteCharAt(builder.length() - 1);

		pListBlockContent = builder.toString();
		return true;
	}

	/**
	 * Retrieves the bullet marker used by this line. Null if unknown. Based on
	 * the array {@link RestLanguage#LIST_MARKERS}
	 * 
	 * @param aListLine
	 *            Line containing a bullet
	 * @return The marker used on the line, null if unknown
	 */
	protected String getBulletMarker(final String aListLine) {

		String trimmedLine = aListLine.trim();

		int marker = TextUtilities.startsWith(RestLanguage.LIST_MARKERS,
				trimmedLine);
		if (marker == -1) {
			return null;
		}

		return RestLanguage.LIST_MARKERS[marker];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #getType()
	 */
	@Override
	public String getType() {
		return HANDLER_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #wrap(int)
	 */
	@Override
	public String wrap(final int aMaxLen) {

		final int blockOffset = getBlockInformation().getOffset();

		// Extract block lines
		if (!extractBlock()) {
			return null;
		}

		// Compute indentations to be used
		computeIndentations();

		// Wrap the first line
		wrapFirstLine(aMaxLen);

		StringBuilder blockResult = new StringBuilder();

		if (!pListBlockContent.isEmpty()) {
			// Wrap the list block

			int localReferenceOffset = pReferenceOffset - blockOffset;

			StringBuilder listBlockInLine = new StringBuilder();

			// Set the indentation to be used
			listBlockInLine.append(pBlockIndent);
			listBlockInLine.append(convertBlockInLine(pListBlockContent));

			blockResult.append(pLineDelimiter);
			blockResult.append(wrapLine(listBlockInLine.toString(), aMaxLen));

			if (localReferenceOffset < pFirstLineContent.length()) {
				pReferenceOffset = localReferenceOffset + blockOffset;
			}
		}

		StringBuilder result = new StringBuilder();
		result.append(pFirstLineContent);
		result.append(blockResult);

		setBlockContent(result.toString());
		printOffset();

		// Don't forget to replace markers
		return replaceInternalLineMarkers();
	}

	/**
	 * Wraps the first to have the right length. Moves the end of the line in
	 * the beginning of the block
	 * 
	 * @param aMaxLen
	 *            Maximum line length
	 * 
	 * @return True if the list block has been modified, else false
	 */
	private boolean wrapFirstLine(final int aMaxLen) {

		// Use a left trimmed line, to be sure that the break position is not in
		// the indentation nor just after the bullet
		int delta = pFirstLineIndent.length() + pBullet.length();
		String trimmedLine = pFirstLineContent.substring(delta);

		int breakPos = pLineUtil.getLineBreakPosition(trimmedLine, 0, aMaxLen
				- delta);

		// On error / on stop, do nothing
		if (breakPos < 0) {
			return false;
		}

		// If the break pos equals the length of the string, stop here
		if (breakPos == trimmedLine.length()) {
			return false;
		}

		// Do the wrapping
		breakPos += delta;

		String wrappedFirstLine = pFirstLineContent.substring(0, breakPos);
		String movedPart = pFirstLineContent.substring(breakPos + 1);

		pFirstLineContent = wrappedFirstLine;

		// Move the end of the line to the beginning of the block, if needed
		if (movedPart.trim().isEmpty()) {
			return false;
		}

		StringBuilder newListBlock = new StringBuilder(
				pListBlockContent.length() + movedPart.length());

		newListBlock.append(pLineUtil.rtrim(movedPart));
		newListBlock.append(' ');
		newListBlock.append(pListBlockContent);

		// Apply block wrapping
		pListBlockContent = newListBlock.toString();

		return true;
	}
}
