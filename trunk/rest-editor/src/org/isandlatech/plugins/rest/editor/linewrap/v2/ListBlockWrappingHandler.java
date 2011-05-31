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
		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}

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

		StringBuilder completeBlock = new StringBuilder();
		completeBlock.append(pFirstLineContent);
		completeBlock.append(pLineDelimiter);
		completeBlock.append(pListBlockContent);

		StringBuilder completeBlockInLine = convertBlockInLine(completeBlock
				.toString());

		// Wrap the first line
		int firstBreakPos = wrapFirstLine(completeBlockInLine.toString(),
				aMaxLen);

		StringBuilder result = new StringBuilder();
		result.append(pFirstLineContent);

		if (firstBreakPos > 0) {

			int localReferenceOffset = pReferenceOffset;

			// Re-calculate the in-line block
			StringBuilder nextBlockInLine = new StringBuilder();
			nextBlockInLine.append(pBlockIndent);
			nextBlockInLine.append(completeBlockInLine
					.substring(firstBreakPos + 1));

			result.append(pLineDelimiter);
			result.append(wrapLine(nextBlockInLine.toString(), aMaxLen));

			if (localReferenceOffset < pFirstLineContent.length()) {
				pReferenceOffset = localReferenceOffset + blockOffset;
			}

		} else {
			// The reference offset must be relative to the document now
			pReferenceOffset += blockOffset;
		}

		setBlockContent(result.toString());
		printOffset();

		// Don't forget to replace markers
		return replaceInternalLineMarkers();
	}

	/**
	 * Wraps the first line and returns the break position. Returns -1 if the
	 * block didn't need to be wrapped
	 * 
	 * @param aBlock
	 *            Block to be wrapped
	 * @param aMaxLineLength
	 *            Maximum line length
	 * @return The break position, -1 if break isn't needed
	 */
	private int wrapFirstLine(final String aBlock, final int aMaxLineLength) {

		int breakPos = pLineUtil
				.getLineBreakPosition(aBlock, 0, aMaxLineLength);
		if (breakPos < 0 || breakPos == aBlock.length()) {
			return -1;
		}

		pFirstLineContent = aBlock.substring(0, breakPos);
		return breakPos;
	}
}
