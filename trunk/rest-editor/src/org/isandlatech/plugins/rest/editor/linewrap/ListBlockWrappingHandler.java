/*******************************************************************************
 * Copyright (c) 2011 isandlaTech, Thomas Calmant
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Thomas Calmant (isandlaTech) - initial API and implementation
 *******************************************************************************/

package org.isandlatech.plugins.rest.editor.linewrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Set;

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
	 * Sets up the marker set to take ReST markers into account
	 */
	public ListBlockWrappingHandler() {

		// Take care of ReST markers
		Set<String> markersSet = getMarkersSet();
		markersSet.add(RestLanguage.BOLD_MARKER);
		markersSet.add(RestLanguage.EMPHASIS_MARKER);
		markersSet.add(RestLanguage.INLINE_LITERAL_MARKER);
		markersSet.add(RestLanguage.LINK_BEGIN);
	}

	/**
	 * Computes the first line and sub lines indentation to be used
	 * 
	 * {@link #extractBlock()} must be called before this method.
	 */
	private void computeIndentations() {

		// Retrieve its indentation
		pFirstLineIndent = pLineUtil.getIndentation(pFirstLineContent);
		int indentationLength = pFirstLineIndent.length() + pBullet.length();

		// Compute the indentation of other lines
		char[] sublineIndentArray = new char[indentationLength];
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

		// Retrieves its marker
		pBullet = getBulletMarker(pFirstLineContent);
		if (pBullet == null) {
			System.out.println("No bullet in '" + pFirstLineContent + "'");
			return false;
		}

		// Extract the sub lines
		StringBuilder builder = new StringBuilder();
		String line;

		try {
			while ((line = strReader.readLine()) != null) {
				builder.append(line);
				builder.append(pLineDelimiter);
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

		// Extract block lines
		if (!extractBlock()) {
			return null;
		}

		// Compute indentations to be used
		computeIndentations();

		final int prefixLen = pFirstLineIndent.length() + pBullet.length();

		StringBuilder completeBlock = new StringBuilder();
		completeBlock.append(pBlockIndent);
		completeBlock.append(pFirstLineContent.substring(prefixLen));
		completeBlock.append(pLineDelimiter);
		completeBlock.append(pListBlockContent);

		// Update the reference offset as needed
		pReferenceOffset = pReferenceOffset - prefixLen + pBlockIndent.length();

		StringBuilder completeBlockInLine = convertBlockInLine(completeBlock
				.toString());

		StringBuilder result = wrapLine(completeBlockInLine.toString(), aMaxLen);

		result.delete(0, pBlockIndent.length());
		result.insert(0, pFirstLineIndent + pBullet);

		pReferenceOffset = pReferenceOffset + prefixLen - pBlockIndent.length();
		setBlockContent(result.toString());

		// Don't forget to replace markers
		return replaceInternalLineMarkers();
	}
}
