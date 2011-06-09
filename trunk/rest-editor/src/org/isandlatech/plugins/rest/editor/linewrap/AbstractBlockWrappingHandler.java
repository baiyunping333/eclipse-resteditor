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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;

/**
 * @author Thomas Calmant
 * 
 */
public abstract class AbstractBlockWrappingHandler implements
		IBlockWrappingHandler {

	/** Internal end-of-line representation */
	public static final String INTERNAL_LINE_FEED = "\u00B6";

	/** Working block content */
	private String pBlockContent;

	/** Modified block */
	private BlockInformation pDocBlock;

	/** Handled document */
	private IDocument pDocument;

	/** Applied command */
	private DocumentCommand pAppliedCommand;

	/** Line delimiter for the current document */
	protected String pLineDelimiter;

	/** Line utility singleton */
	protected final LineUtil pLineUtil = LineUtil.get();

	/** Reference offset */
	protected int pReferenceOffset;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #applyCommand(org.eclipse.jface.text.DocumentCommand)
	 */
	@Override
	public String applyCommand(final DocumentCommand aCommand) {

		// Make offsets relative to the block
		final int blockRelativeCommandOffset = aCommand.offset
				- getDocBlock().getOffset();
		int blockRelativeReferenceOffset = pReferenceOffset
				- getDocBlock().getOffset();

		StringBuilder modifiedString = new StringBuilder(pBlockContent.length()
				+ aCommand.text.length());

		// Insert first part (not modified)
		modifiedString.append(pBlockContent.substring(0,
				blockRelativeCommandOffset));

		// Insert the new text
		String addedText = aCommand.text;
		if (TextUtilities.startsWith(TextUtilities.DELIMITERS, aCommand.text) != -1) {
			addedText = INTERNAL_LINE_FEED;
		}

		modifiedString.append(addedText);

		// Insert the rest, avoiding deleted parts
		int postDeletionOffset = blockRelativeCommandOffset + aCommand.length;
		if (postDeletionOffset < pBlockContent.length()) {
			modifiedString.append(pBlockContent.substring(postDeletionOffset));
		}

		// Update the reference offset as needed
		if (blockRelativeReferenceOffset >= blockRelativeCommandOffset) {

			// On pure deletion, the base offset is already at the good place
			pReferenceOffset += Math.max(aCommand.text.length()
					- aCommand.length, 0);
		}

		pAppliedCommand = aCommand;
		pBlockContent = modifiedString.toString();
		return pBlockContent;
	}

	/**
	 * Converts the given text block into a single line. Conserves the
	 * indentation of the first line only.
	 * 
	 * After that point, {@link #getReferenceOffset()} is relative to the
	 * beginning of the result line.
	 * 
	 * Should be called only once per wrapping operation.
	 * 
	 * @param aText
	 *            Text block to be converted
	 * @return The block in one line
	 */
	@Override
	public StringBuilder convertBlockInLine(final String aText) {

		final int delimLen = pLineDelimiter.length();

		// The result builder
		StringBuilder resultLine = new StringBuilder(aText.length());

		// Offset treatment variables
		int previousOffsetInOrigin = 0;
		int currentOffsetInOrigin = 0;
		int currentOffsetInResult = 0;
		int newOffset = 0;
		int blockRelativeReferenceOffset = pReferenceOffset
				- getDocBlock().getOffset();

		// Read the block
		BufferedReader reader = new BufferedReader(new StringReader(aText));
		String currentLine;

		// Insert indentation first (and update currentOffsetInResult)
		String indent = pLineUtil.getIndentation(aText);
		resultLine.append(indent);
		currentOffsetInResult += indent.length();

		boolean deleteLastSpace = false;

		try {
			while ((currentLine = reader.readLine()) != null) {

				String trimmedLine = pLineUtil.ltrim(currentLine);
				if (trimmedLine.isEmpty()) {
					// Ignore blank lines
					// TODO see if we shouldn't return null here...
					continue;
				}

				resultLine.append(trimmedLine);
				currentOffsetInResult += trimmedLine.length();

				// If the line doesn't end with a space, add it
				if (!pLineUtil
						.isSpace(trimmedLine.charAt(trimmedLine.length() - 1))) {

					// Add trailing space, if needed
					resultLine.append(' ');
					deleteLastSpace = true;

				} else {
					// Keep string as is
					deleteLastSpace = false;
				}

				// Take the trailing space into account in all cases...
				currentOffsetInResult++;

				currentOffsetInOrigin += currentLine.length() + delimLen;

				// Update the reference offset if it is in the current line
				if (blockRelativeReferenceOffset >= previousOffsetInOrigin
						&& blockRelativeReferenceOffset < currentOffsetInOrigin) {

					newOffset = blockRelativeReferenceOffset
							- currentOffsetInOrigin;
					newOffset += currentOffsetInResult;
				}

				previousOffsetInOrigin = currentOffsetInOrigin;
			}

		} catch (IOException e) {
			// Really ?
			e.printStackTrace();
		}

		// Remove the last added trailing space
		if (deleteLastSpace && resultLine.length() > 0) {
			resultLine.deleteCharAt(resultLine.length() - 1);
		}

		pReferenceOffset = newOffset;
		return resultLine;
	}

	/**
	 * Retrieves the last applied command
	 * 
	 * @return the last applied command
	 */
	public DocumentCommand getAppliedCommand() {
		return pAppliedCommand;
	}

	/**
	 * @return the working block content
	 */
	public String getBlockContent() {
		return pBlockContent;
	}

	/**
	 * Retrieves the current block information
	 * 
	 * @return the current block information
	 */
	public BlockInformation getBlockInformation() {
		return getDocBlock();
	}

	/**
	 * @return the working document block information
	 */
	protected BlockInformation getDocBlock() {
		return pDocBlock;
	}

	/**
	 * Retrieves the document currently modified by this handler
	 * 
	 * @return the document modified by this handler
	 */
	public IDocument getDocument() {
		return pDocument;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #getReferenceOffset()
	 */
	@Override
	public int getReferenceOffset() {
		return pReferenceOffset;
	}

	/**
	 * Debug print of the current block content with a pipe indicating the
	 * current reference offset position
	 */
	protected void printOffset() {

		int docReference = pReferenceOffset - getDocBlock().getOffset();
		int usedReference = docReference;

		if (usedReference < 0) {
			usedReference = pReferenceOffset;
		}

		System.out.println("Insert @" + usedReference + " / "
				+ (pReferenceOffset - getDocBlock().getOffset()) + " / "
				+ pReferenceOffset);

		printStringOffset("BlockContent", pBlockContent, usedReference);
	}

	/**
	 * Prints the given string with an offset indicator
	 * 
	 * @param aLabel
	 *            Line label (':' not needed)
	 * @param aString
	 *            String to display
	 * @param aOffset
	 *            Offset to display in the string
	 */
	protected void printStringOffset(final CharSequence aLabel,
			final CharSequence aString, final int aOffset) {

		try {
			StringBuilder modifiedString = new StringBuilder(aString);
			modifiedString.insert(aOffset, '|');
			System.out.println(aLabel + " : '" + modifiedString + "'\n");

		} catch (Exception ex) {
			System.out.println(aLabel + " : [Reference error - " + ex
					+ "] - offset = " + aOffset);
		}
	}

	/**
	 * Replaces internal line feed markers by document line delimiters.
	 * 
	 * Don't forget to call {@link #setBlockContent(String)} before calling this
	 * method
	 */
	protected String replaceInternalLineMarkers() {

		int relativeReference = pReferenceOffset - getDocBlock().getOffset();
		int delta = pLineDelimiter.length() * 2 - INTERNAL_LINE_FEED.length();

		// Find internal line feeds and increment reference as needed
		int index = pBlockContent.indexOf(INTERNAL_LINE_FEED);
		while (index != -1) {

			if (index <= relativeReference) {
				relativeReference += delta;
			} else {
				// Don't need to go further
				break;
			}

			index = pBlockContent.indexOf(INTERNAL_LINE_FEED, index + 1);
		}

		pBlockContent = pBlockContent.replace(INTERNAL_LINE_FEED,
				pLineDelimiter + pLineDelimiter);

		pReferenceOffset = Math.max(relativeReference, 0)
				+ getDocBlock().getOffset();

		return pBlockContent;
	}

	/**
	 * @param aBlockContent
	 *            the working block content
	 */
	public void setBlockContent(final String aBlockContent) {
		pBlockContent = aBlockContent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #setReferenceOffset(int)
	 */
	@Override
	public void setReferenceOffset(final int aOffset) {
		pReferenceOffset = aOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #setUp(org.eclipse.jface.text.IDocument,
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.BlockInformation)
	 */
	@Override
	public boolean setUp(final IDocument aDocument,
			final BlockInformation aBlock) {

		pDocument = aDocument;
		pDocBlock = aBlock;
		pBlockContent = null;
		pLineDelimiter = TextUtilities.getDefaultLineDelimiter(aDocument);

		if (!getDocBlock().computeOffsets(aDocument)) {
			return false;
		}

		// Store the block content
		try {
			pBlockContent = aDocument.get(getDocBlock().getOffset(),
					getDocBlock().getLength());

		} catch (BadLocationException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BlockWrappingHandler(" + getType() + ", "
				+ getClass().getCanonicalName() + ")";
	}

	/**
	 * Wraps the given line adding document line delimiter where necessary and
	 * keeping the base indentation.
	 * 
	 * After that point, {@link #getReferenceOffset()} is relative to the
	 * document.
	 * 
	 * @param aLine
	 *            Line to wrap
	 * @param aMaxLen
	 *            Maximum length of a line
	 * @param aOffsetInLine
	 *            An offset in the entry line that will be updated, visible in
	 *            the result offset field
	 * @return The wrapped line
	 */
	protected StringBuilder wrapLine(final String aLine, final int aMaxLen) {

		// Store indentation
		final String indent = pLineUtil.getIndentation(aLine);
		final int indentLen = indent.length();

		// Line delimiter
		final int delimLen = pLineDelimiter.length();

		StringBuilder wrappedLine = new StringBuilder(
				(int) (aLine.length() * 1.2));

		int breakPos = 0;
		int oldBreakPos = 0;

		int currentOffsetInResult = 0;
		int newOffset = -1;

		while ((breakPos = pLineUtil.getLineBreakPosition(aLine, breakPos,
				aMaxLen - indentLen)) != -1) {

			String subLine = aLine.substring(oldBreakPos, breakPos);
			String trimmedSubline = pLineUtil.ltrim(subLine);

			wrappedLine.append(indent);
			wrappedLine.append(trimmedSubline);
			wrappedLine.append(pLineDelimiter);

			// If the offset is in the currently modified block...
			if (pReferenceOffset >= oldBreakPos && pReferenceOffset < breakPos) {

				// Set it to be relative to the new line
				newOffset = pReferenceOffset - oldBreakPos;

				/*
				 * FIXME cursor position problems when working at the end of a
				 * line come from here
				 */

				// Correct getLineBreakPosition indentation forgiveness
				if (oldBreakPos > 0) {
					newOffset -= (subLine.length() - trimmedSubline.length());

					// If we're not moving inside the indentation, move
					if (newOffset >= 0) {
						newOffset += indentLen;
					}
				}

				// Make it relative to the current block state
				newOffset += currentOffsetInResult;
			}

			currentOffsetInResult += indentLen + trimmedSubline.length()
					+ delimLen;

			oldBreakPos = breakPos;
		}

		// Remove the last delimiter
		int wrappedLineLen = wrappedLine.length();
		if (wrappedLineLen - delimLen > 0) {
			wrappedLine.delete(wrappedLineLen - delimLen, wrappedLineLen);
			wrappedLineLen -= delimLen;
		}

		// Find a valid offset
		if (newOffset == -1) {
			// Put it at the end of the wrapped line
			newOffset = wrappedLineLen;
		}

		pReferenceOffset = newOffset + getDocBlock().getOffset();
		return wrappedLine;
	}
}
