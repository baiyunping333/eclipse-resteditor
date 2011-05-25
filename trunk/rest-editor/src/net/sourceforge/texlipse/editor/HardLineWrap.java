/*
 * $Id: HardLineWrap.java,v 1.11 2009/05/28 17:18:00 borisvl Exp $
 *
 * Copyright (c) 2004-2005 by the TeXlapse Team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.sourceforge.texlipse.editor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;

/**
 * This class handles the line wrapping. Modified version of the Texclipse hard
 * line wrap class.
 * 
 * @author Antti Pirinen
 * @author Oskar Ojala
 * @author Boris von Loesch
 * 
 * @author Thomas Calmant
 */
public class HardLineWrap {

	/**
	 * Document block information storage
	 * 
	 * @author Thomas Calmant
	 */
	public class BlockInformation {

		/** Offset of the base line in this block */
		private int pBaseLineOffset;

		/** Block begin line number */
		private int pBeginLine;

		/** Block content */
		private String pContent;

		/** Block end line number */
		private int pEndLine;

		/** List of the offsets of found lines */
		private List<Integer> pLinesOffsets;

		/** Re-based offset */
		private int pOffset;

		/**
		 * Sets up the read-only structure
		 * 
		 * @param aBeginLine
		 *            First line of the block
		 * @param aEndLine
		 *            Last line of the block
		 * @param aContent
		 *            Block content
		 * @param aBaseLineOffset
		 *            Offset of the base line in this block
		 */
		public BlockInformation(final int aBeginLine, final int aEndLine,
				final String aContent, final int aBaseLineOffset) {

			pBeginLine = aBeginLine;
			pEndLine = aEndLine;
			pContent = aContent;
			pBaseLineOffset = aBaseLineOffset;
			pLinesOffsets = new ArrayList<Integer>();
		}

		/**
		 * Sets up the read-only structure
		 * 
		 * @param aContent
		 *            Block content
		 */
		public BlockInformation(final String aContent) {
			this(-1, -1, aContent, -1);
		}

		/**
		 * Retrieves the base line offset
		 * 
		 * @return the base line offset
		 */
		public int getBaseLineOffset() {
			return pBaseLineOffset;
		}

		/**
		 * Retrieves the block begin line
		 * 
		 * @return the block begin line
		 */
		public int getBeginLine() {
			return pBeginLine;
		}

		/**
		 * Retrieves the block content
		 * 
		 * @return the block content
		 */
		public String getContent() {
			return pContent;
		}

		/**
		 * Retrieves the block end line
		 * 
		 * @return the block end line
		 */
		public int getEndLine() {
			return pEndLine;
		}

		/**
		 * Retrieves the lines offsets
		 * 
		 * @return the lines offsets
		 */
		public List<Integer> getLinesOffsets() {
			return pLinesOffsets;
		}

		/**
		 * Retrieves the stored offset
		 * 
		 * @return the stored offset
		 */
		public int getStoredOffset() {
			return pOffset;
		}
	}

	/**
	 * Represents the modification of a text block.
	 * 
	 * @author Thomas Calmant
	 */
	protected class BlockModificationResult {
		/** New block content */
		public StringBuilder content;

		/** Modified offset */
		public int storedOffset;
	}

	/**
	 * Apply the given command to the given text. The command offset must be
	 * relative to the beginning of the given text.
	 * 
	 * @param aCommand
	 *            Command to be applied
	 * @param aText
	 *            Text to be modified
	 * @return The modified text
	 */
	public String applyCommand(final DocumentCommand aCommand,
			final String aText) {

		StringBuilder modifiedString = new StringBuilder(aText.length()
				+ aCommand.text.length());

		// Insert first part (not modified)
		modifiedString.append(aText.substring(0, aCommand.offset));

		// Insert the new text
		modifiedString.append(aCommand.text);

		// Insert the rest, avoiding deleted parts
		int postDeletionOffset = aCommand.offset + aCommand.length;
		if (postDeletionOffset < aText.length()) {
			modifiedString.append(aText.substring(postDeletionOffset));
		}

		return modifiedString.toString();
	}

	/**
	 * Converts the given text block into a single line. Conserves the
	 * indentation of the first line only.
	 * 
	 * @param aText
	 *            Text block to be converted
	 * @param aLineDelimiter
	 *            Line delimiter used by the document
	 * @param aBlockOffset
	 *            An offset in the block that will be made relative to the line
	 *            and stored in the result
	 * @return A structure containing the generated line and the modified offset
	 */
	protected BlockModificationResult convertBlockInLine(final String aText,
			final String aLineDelimiter, final int aBlockOffset) {

		final int delimLen = aLineDelimiter.length();

		// The result builder
		StringBuilder resultLine = new StringBuilder(aText.length());

		// Offset treatment variables
		int previousOffsetInOrigin = 0;
		int currentOffsetInOrigin = 0;
		int currentOffsetInResult = 0;
		int newOffset = 0;

		// Read the block
		BufferedReader reader = new BufferedReader(new StringReader(aText));
		String currentLine;

		// Insert indentation first (and update currentOffsetInResult)
		String indent = getIndentation(aText);
		resultLine.append(indent);
		currentOffsetInResult += indent.length();

		try {
			while ((currentLine = reader.readLine()) != null) {

				String trimmedLine = ltrim(currentLine);
				resultLine.append(trimmedLine).append(' ');

				// Don't forget the added space
				currentOffsetInResult += trimmedLine.length() + 1;
				currentOffsetInOrigin += currentLine.length() + delimLen;

				if (aBlockOffset > previousOffsetInOrigin
						&& aBlockOffset < currentOffsetInOrigin) {
					newOffset = aBlockOffset - currentOffsetInOrigin;
					newOffset += currentOffsetInResult;
				}

				previousOffsetInOrigin = currentOffsetInOrigin;
			}

		} catch (IOException e) {
			// Really ?
			e.printStackTrace();
		}

		// Remove the last added trailing space
		resultLine.deleteCharAt(resultLine.length() - 1);

		BlockModificationResult result = new BlockModificationResult();
		result.content = resultLine;
		result.storedOffset = newOffset;
		return result;
	}

	/**
	 * Retrieves the given line delimiter, or the document default one if not
	 * readable
	 * 
	 * @param aDocument
	 *            Document to use
	 * @param aLineNumber
	 *            Line number in document
	 * @return The line delimiter (not null)
	 * @throws BadLocationException
	 *             The line is outside the document
	 */
	public String generateLineDelimiter(final IDocument aDocument,
			final int aLineNumber) throws BadLocationException {

		String delim = aDocument.getLineDelimiter(aLineNumber);
		if (delim == null) {
			// This is the last line in the document
			if (aLineNumber > 0) {
				delim = aDocument.getLineDelimiter(aLineNumber - 1);
			} else {
				// Last chance
				TextUtilities.getDefaultLineDelimiter(aDocument);
			}
		}

		return delim;
	}

	/**
	 * Returns the indentation of the given string
	 * 
	 * @param aText
	 *            Text to be treated
	 * @return The indentation of the line
	 */
	public String getIndentation(final String aText) {

		int i = 0;

		// Loop until we found the first non white space character
		for (char character : aText.toCharArray()) {

			if (!isSpace(character)) {
				break;
			}

			i++;
		}

		return aText.substring(0, i);
	}

	/**
	 * Retrieves the last line similar to the given one if
	 * 
	 * @param aDocument
	 *            Document to read
	 * @param aBaseLine
	 *            Base search line number
	 * @param aIndent
	 *            Base line indentation (defines the block)
	 * @param aDirection
	 *            +1 or -1 : line iteration
	 * @return The line number of the last line of the base line's block
	 * @throws BadLocationException
	 *             Out of bounds research
	 */
	protected int getLastSimilarLine(final IDocument aDocument,
			final int aBaseLine, final String aIndent, final int aDirection)
			throws BadLocationException {

		// Block test
		int indentLen = 0;
		if (aIndent != null) {
			indentLen = aIndent.length();
		}

		// Prepare upper search bound
		int nbLines = aDocument.getNumberOfLines();

		int searchLine = aBaseLine + aDirection;
		while (searchLine >= 0 && searchLine < nbLines) {

			String lineContent = getLine(aDocument, searchLine, false);
			String indent = getIndentation(lineContent);

			// Not the same indentation, stop there
			if (indent.length() != indentLen) {
				break;
			}

			// Line to be ignored : stop there
			if (ignoreLine(lineContent)) {
				break;
			}

			searchLine += aDirection;
		}

		// +1 : we started at aBaseLine - 1 (and vice-versa)
		return searchLine - aDirection;
	}

	/**
	 * Retrieves the given line content, with or without its end delimiter
	 * 
	 * @param aDocument
	 *            Document to use
	 * @param aLineNumber
	 *            Number of the grabbed line
	 * @param aIncludeDelimiters
	 *            Keep end delimiter in the result string
	 * @return The line content
	 */
	public String getLine(final IDocument aDocument, final int aLineNumber,
			final boolean aIncludeDelimiters) {

		String line = "";

		if (aLineNumber < 0 || aLineNumber >= aDocument.getNumberOfLines()) {
			return "";
		}

		try {
			IRegion lineInfo = aDocument.getLineInformation(aLineNumber);
			line = aDocument.get(lineInfo.getOffset(), lineInfo.getLength());

			// Remove trailing delimiter, if needed
			if (aIncludeDelimiters) {
				String delim = aDocument.getLineDelimiter(aLineNumber);
				if (delim != null) {
					line = line + delim;
				}
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
			return "";
		}

		return line;
	}

	/**
	 * Converts all lines of the same block into a one-line string. Removes
	 * indentation, line delimiters, ...
	 * 
	 * @param aDocument
	 *            Document to read
	 * @param aLineNumber
	 *            Base line number
	 * @param aDocumentOffset
	 *            An offset that will be stored in a block content relative
	 *            value
	 * @return A one-line string
	 * @throws BadLocationException
	 *             Line number out of bounds
	 */
	public BlockInformation getLineBlock(final IDocument aDocument,
			final int aLineNumber, final int aDocumentOffset)
			throws BadLocationException {

		// Get base line informations
		final String baseLineContent = getLine(aDocument, aLineNumber, false);
		final String baseIndent = getIndentation(baseLineContent);

		// Search the borders of the paragraph
		int lineBlockBegin = getLastSimilarLine(aDocument, aLineNumber,
				baseIndent, -1);

		int lineBlockEnd = getLastSimilarLine(aDocument, aLineNumber,
				baseIndent, +1);

		// Extract line block
		StringBuilder lineBlock = new StringBuilder();

		// Keep the indentation information
		lineBlock.append(baseIndent);

		// Keep lines offsets information
		int baseLineOffset = 0;
		List<Integer> linesOffsets = new ArrayList<Integer>(lineBlockEnd
				- lineBlockBegin + 1);
		int rebasedOffset = 0;

		// Append all lines of the block, without the line delimiter
		for (int i = lineBlockBegin; i <= lineBlockEnd; i++) {
			String line = getLine(aDocument, i, false);

			linesOffsets.add(lineBlock.length());
			if (i == aLineNumber) {
				baseLineOffset = lineBlock.length();
			}

			// Update offset
			IRegion region = aDocument.getLineInformation(i);

			if (region.getOffset() < aDocumentOffset
					&& aDocumentOffset < region.getOffset()
							+ region.getLength()) {

				int offsetInLine = aDocumentOffset - region.getOffset();
				String ltrimmedLine = ltrim(line);
				int removedLen = line.length() - ltrimmedLine.length();

				rebasedOffset = lineBlock.length();
				if (offsetInLine < removedLen) {
					rebasedOffset -= 1;
				} else {
					rebasedOffset += offsetInLine - removedLen;
				}
			}

			lineBlock.append(line.trim() + ' ');
		}

		// Remove last space character
		lineBlock.deleteCharAt(lineBlock.length() - 1);

		// Store data
		BlockInformation blockInfo = new BlockInformation(lineBlockBegin,
				lineBlockEnd, lineBlock.toString(), baseLineOffset);
		blockInfo.pLinesOffsets = linesOffsets;
		blockInfo.pOffset = rebasedOffset;

		return blockInfo;
	}

	/**
	 * Finds the best position in the given String to make a line break
	 * 
	 * @param aLine
	 *            Line to break
	 * @param aBaseOffset
	 *            Search start offset
	 * @param aMaxLineLength
	 *            Maximum line length
	 * @return The best position to break the line
	 */
	protected int getLineBreakPosition(final String aLine,
			final int aBaseOffset, final int aMaxLineLength) {

		if (aLine.length() < aMaxLineLength) {
			return -1;
		}

		int offset = aBaseOffset;
		// Ignore indentation
		while (offset < aLine.length() && isSpace(aLine.charAt(offset))) {
			offset++;
		}

		int lastSpaceOffset = -1;
		int breakOffset = -1;

		while (offset < aLine.length()) {

			if (offset - aBaseOffset > aMaxLineLength && lastSpaceOffset != -1) {
				breakOffset = lastSpaceOffset;
				break;
			}

			if (Character.isWhitespace(aLine.charAt(offset))) {
				lastSpaceOffset = offset;
			}

			offset++;
		}

		return breakOffset;
	}

	/**
	 * Retrieves the complete block (paragraph, ...) containing the given line
	 * 
	 * @param aDocument
	 *            Document containing the block
	 * @param aLineNumber
	 *            Reference line in the document
	 * @return Information about the block containing the given line
	 * @throws BadLocationException
	 *             The line is outside the document
	 */
	public BlockInformation getRawBlock(final IDocument aDocument,
			final int aLineNumber) throws BadLocationException {

		// Get base line informations
		final String baseLineContent = getLine(aDocument, aLineNumber, false);
		final String baseIndent = getIndentation(baseLineContent);

		// Search the borders of the paragraph
		int lineBlockBegin = getLastSimilarLine(aDocument, aLineNumber,
				baseIndent, -1);

		int lineBlockEnd = getLastSimilarLine(aDocument, aLineNumber,
				baseIndent, +1);

		// Extract line block
		StringBuilder lineBlock = new StringBuilder();

		// Keep lines offsets information
		int baseLineOffset = 0;
		List<Integer> linesOffsets = new ArrayList<Integer>(lineBlockEnd
				- lineBlockBegin + 1);

		// Append all lines of the block, without the line delimiter
		for (int i = lineBlockBegin; i <= lineBlockEnd; i++) {
			String line = getLine(aDocument, i, true);

			linesOffsets.add(lineBlock.length());
			if (i == aLineNumber) {
				baseLineOffset = lineBlock.length();
			}

			lineBlock.append(line);
		}

		// Remove last space character
		lineBlock.deleteCharAt(lineBlock.length() - 1);

		// Store data
		BlockInformation blockInfo = new BlockInformation(lineBlockBegin,
				lineBlockEnd, lineBlock.toString(), baseLineOffset);
		blockInfo.pLinesOffsets = linesOffsets;
		return blockInfo;
	}

	/**
	 * This method checks, whether <i>line</i> should stay alone on one line.
	 * 
	 * @param aLine
	 *            Line to be tested
	 * @return True if the can be ignored
	 */
	protected boolean ignoreLine(final String aLine) {

		if (aLine.trim().isEmpty()) {
			return true;
		}

		// TODO: ignore bullets, ...

		// Literal lines are ignored
		if (aLine.startsWith(".. ")) {
			return true;
		}

		return false;
	}

	/**
	 * Tests if an offset is in the given region
	 * 
	 * @param aRegion
	 *            A document region
	 * @param aOffset
	 *            A document offset
	 * @return True if the offset is contained by the region, else false
	 */
	public boolean isInRegion(final IRegion aRegion, final int aOffset) {
		return aOffset >= aRegion.getOffset()
				&& aOffset < (aRegion.getOffset() + aRegion.getLength());
	}

	/**
	 * Tests if the given parameter is an horizontal space, therefore excluding
	 * line feed, etc
	 * 
	 * @param aCharacter
	 *            Character to be tested
	 * @return True if the given character is a space
	 */
	public boolean isSpace(final char aCharacter) {
		return Character.getType(aCharacter) == Character.SPACE_SEPARATOR;
	}

	/**
	 * Removes all white spaces from the beginning of the String
	 * 
	 * @param aString
	 *            The string to wrap
	 * @return trimmed version of the string
	 */
	public String ltrim(final String aString) {
		return aString.replaceAll("^\\s+", "");
	}

	/**
	 * Removes all white spaces from the end of the String
	 * 
	 * @param aString
	 *            The string to wrap
	 * @return trimmed version of the string
	 */
	public String rtrim(final String aString) {
		return aString.replaceAll("\\s+$", "");
	}

	/**
	 * Wraps the given line adding document line delimiter where necessary and
	 * keeping the base indentation.
	 * 
	 * @param aDocument
	 *            Modified document
	 * @param aLine
	 *            Line to wrap
	 * @param aMaxLen
	 *            Maximum length of a line
	 * @param aOffsetInLine
	 *            An offset in the entry line that will be updated, visible in
	 *            the result offset field
	 * @return The wrapped line
	 */
	protected BlockModificationResult wrapLine(final IDocument aDocument,
			final String aLine, final int aMaxLen, final int aOffsetInLine) {

		// Store indentation
		final String indent = getIndentation(aLine);
		final int indentLen = indent.length();

		// Line delimiter
		final String delim = TextUtilities.getDefaultLineDelimiter(aDocument);
		final int delimLen = delim.length();

		StringBuilder wrappedLine = new StringBuilder(
				(int) (aLine.length() * 1.2));

		int breakPos = 0;
		int oldBreakPos = 0;

		int currentOffsetInOrigin = 0;
		int currentOffsetInResult = 0;
		int newOffset = -1;

		while ((breakPos = getLineBreakPosition(aLine, breakPos, aMaxLen)) != -1) {

			String subLine = aLine.substring(oldBreakPos, breakPos);
			String trimmedSubline = ltrim(subLine);

			wrappedLine.append(indent);
			wrappedLine.append(trimmedSubline);
			wrappedLine.append(delim);

			// If the offset is in the currently modified block...
			if (aOffsetInLine >= oldBreakPos && aOffsetInLine < breakPos) {

				// Set it to be relative to the new line
				newOffset = aOffsetInLine - oldBreakPos;

				// Make it relative to the block
				newOffset += currentOffsetInResult;
			}

			currentOffsetInOrigin += subLine.length();
			currentOffsetInResult += trimmedSubline.length() + indentLen;

			// Don't add the delimiter length for the first line
			if (oldBreakPos > 0) {
				currentOffsetInResult += delimLen;
			}

			oldBreakPos = breakPos;
		}

		// Append last line segment
		wrappedLine.append(ltrim(aLine.substring(oldBreakPos)));

		if (newOffset == -1) {

			// If the offset is in the last segment...
			if (aOffsetInLine >= oldBreakPos) {

				// Set it to be relative to the new line
				newOffset = aOffsetInLine - oldBreakPos;

				// Make it relative to the block
				newOffset += currentOffsetInResult;

			} else {
				// I don't know why sometimes we get here...
				newOffset = wrappedLine.length() - 1;
			}
		}

		// Result preparation
		BlockModificationResult result = new BlockModificationResult();
		result.content = wrappedLine;
		result.storedOffset = newOffset;
		return result;
	}

	/**
	 * Wraps a paragraph in the document
	 * 
	 * @param aDocument
	 *            Modified document
	 * @param aCommand
	 *            Document replace command
	 * @param aMaxLen
	 *            Maximum line length
	 * @return True on modification, else false
	 * @throws BadLocationException
	 *             The replace command contains weird data
	 */
	public boolean wrapRegion(final IDocument aDocument,
			final DocumentCommand aCommand, final int aMaxLen)
			throws BadLocationException {

		if (!aCommand.doit) {
			return false;
		}

		// Store some information
		final int initialOffset = aCommand.offset;
		final int baseDocLineNr = aDocument.getLineOfOffset(aCommand.offset);
		final String lineDelimiter = generateLineDelimiter(aDocument,
				baseDocLineNr);

		// Get the whole paragraph
		BlockInformation rawBlockInfo = getRawBlock(aDocument, baseDocLineNr);
		final int blockOffset = aDocument.getLineOffset(rawBlockInfo
				.getBeginLine());

		// Make the offset relative to the block
		aCommand.offset = initialOffset - blockOffset;
		String modifiedBlockContent = applyCommand(aCommand,
				rawBlockInfo.getContent());

		// Update the offset : put it after the modification
		aCommand.caretOffset = aCommand.offset + aCommand.text.length()
				- aCommand.length;

		// Convert the block into a single line
		BlockModificationResult inlineBlockInfo = convertBlockInLine(
				modifiedBlockContent, lineDelimiter, aCommand.caretOffset);
		aCommand.caretOffset = inlineBlockInfo.storedOffset;

		// Wrap the modified block
		BlockModificationResult wrapResult = wrapLine(aDocument,
				inlineBlockInfo.content.toString(), aMaxLen,
				aCommand.caretOffset);

		// Set up the result
		aCommand.caretOffset = blockOffset + wrapResult.storedOffset;
		aCommand.shiftsCaret = false;

		aCommand.length = rawBlockInfo.getContent().length();
		aCommand.offset = blockOffset;
		aCommand.text = wrapResult.content.toString();

		return true;
	}
}
