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

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.isandlatech.plugins.rest.editor.linewrap.LineWrapUtil;

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
	 * Improved information on line wrapping treatment
	 * 
	 * @author Thomas Calmant
	 */
	public enum WrapAction {
		LINE_DELETED, NEW_LINE, NONE
	}

	/**
	 * New line wrapping strategy. The actual wrapping method. Based on the
	 * <code>IDocument d</code> and <code>DocumentCommand c</code> the method
	 * determines how the line must be wrapped. <p> If there is more than
	 * <code>MAX_LENGTH</code> characters at the line, the method tries to
	 * detect the last white space before <code> MAX_LENGTH</code>. In case
	 * there is none, the method finds the first white space after <code>
	 * MAX_LENGTH</code>. Normally it adds the rest of the currentline to the
	 * next line. Exceptions are empty lines, commandlines, commentlines, and
	 * special lines like \\ or \[.
	 * 
	 * @param aDocument
	 *            Modified document
	 * @param aCommand
	 *            Applied modification
	 */
	public WrapAction doLineWrap(final IDocument aDocument,
			final DocumentCommand aCommand) {

		final int maxLineLength = LineWrapUtil.getInstance().getMaxLineLength();
		final int suppressionLength = aCommand.length;
		final boolean isInsertion = (suppressionLength == 0);

		try {
			// Get the line of the command excluding delimiter
			IRegion commandRegion = aDocument
					.getLineInformationOfOffset(aCommand.offset);

			// Ignore texts with line endings
			if (isInsertion
					&& (commandRegion.getLength() + aCommand.text.length() <= maxLineLength || LineWrapUtil
							.getInstance().containsLineDelimiter(aCommand.text))) {
				return WrapAction.NONE;
			}

			// Get modified document line informations
			String docLine = aDocument.get(commandRegion.getOffset(),
					commandRegion.getLength());
			int docLineLength = docLine.length();

			int docLineNumber = aDocument.getLineOfOffset(aCommand.offset);
			final boolean isLastLine = (docLineNumber == aDocument
					.getNumberOfLines());

			final int offsetOnLine = aCommand.offset
					- commandRegion.getOffset();

			// Special case : we are deleting the end of line
			boolean movingLineUp = false;
			if (offsetOnLine + suppressionLength >= docLine.length()
					&& !isInsertion) {
				movingLineUp = true;
			}

			// Create the newLine, we rewrite the whole current line
			StringBuffer newLineBuf = new StringBuffer();

			newLineBuf.append(docLine.substring(0, offsetOnLine));

			if (isInsertion || movingLineUp) {
				// Insert new text at given offset
				newLineBuf.append(aCommand.text);
				newLineBuf.append(rtrim(docLine.substring(offsetOnLine)));

			} else {
				// Skip deleted sub-section
				newLineBuf.append(rtrim(docLine.substring(offsetOnLine
						+ suppressionLength)));
			}

			// Special case if there are white spaces at the end of the line
			if (isInsertion
					&& rtrim(newLineBuf.toString()).length() <= maxLineLength) {
				return WrapAction.NONE;
			}

			String delim = generateLineDelimiter(aDocument, docLineNumber);

			// Conserve indentation
			String indent = getIndentation(docLine);

			String nextline = getLine(aDocument, docLineNumber + 1, false);
			String nextTrimLine = nextline.trim();
			boolean isWithNextline = false;

			if (!ignoreLine(nextTrimLine)) {
				// Add the whole next line
				newLineBuf.append(' ');
				newLineBuf.append(ltrim(nextline));
				docLineLength += nextline.length();
				isWithNextline = true;

			} else {
				newLineBuf.append(delim);
			}

			if (!isLastLine) {
				docLineLength += delim.length();
			}

			String newLine = newLineBuf.toString();

			int breakpos = getLineBreakPosition(newLine, 0, maxLineLength);
			if (breakpos < 0) {
				return WrapAction.NONE;
			}

			aCommand.length = docLineLength;

			aCommand.shiftsCaret = false;
			aCommand.caretOffset = aCommand.offset + aCommand.text.length()
					+ indent.length();
			if (breakpos >= offsetOnLine + aCommand.text.length()) {
				aCommand.caretOffset -= indent.length();
			}
			if (breakpos < offsetOnLine + aCommand.text.length()) {
				// Line delimiter - one white space
				aCommand.caretOffset += delim.length() - 1;
			}

			aCommand.offset = commandRegion.getOffset();

			StringBuffer buf = new StringBuffer();
			buf.append(newLine.substring(0, breakpos));
			buf.append(delim);
			buf.append(indent);
			buf.append(ltrim(newLine.substring(breakpos)));

			// Remove unnecessary characters from buf
			int i = 0;
			while (i < docLine.length() && docLine.charAt(i) == buf.charAt(i)) {
				i++;
			}
			buf.delete(0, i);
			aCommand.offset += i;
			aCommand.length -= i;
			if (isWithNextline) {
				i = 0;
				while (i < nextline.length()
						&& nextline.charAt(nextline.length() - i - 1) == buf
								.charAt(buf.length() - i - 1)) {
					i++;
				}
				buf.delete(buf.length() - i, buf.length());
				aCommand.length -= i;
			}

			aCommand.text = buf.toString();

			if (movingLineUp && nextline.isEmpty()) {
				return WrapAction.LINE_DELETED;
			}

			return WrapAction.NEW_LINE;

		} catch (BadLocationException e) {
			DebugPlugin.logMessage("Problem with hard line wrap", e);
		}

		return WrapAction.NONE;
	}

	public WrapAction doParagraphWrap(final IDocument aDocument,
			final int aLineNumber) {
		return WrapAction.NONE;
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

			if (!Character.isWhitespace(character)) {
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
	private int getLineBreakPosition(final String aLine, final int aBaseOffset,
			final int aMaxLineLength) {

		int offset = aBaseOffset;
		// Ignore indentation
		while (offset < aLine.length()
				&& Character.isWhitespace(aLine.charAt(offset))) {
			offset++;
		}

		int breakOffset = -1;
		while (offset < aLine.length()) {
			if (offset - aBaseOffset > aMaxLineLength && breakOffset != -1) {
				break;
			}
			if (Character.isWhitespace(aLine.charAt(offset))) {
				breakOffset = offset;
			}

			offset++;
		}
		return breakOffset;
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
	 * Converts all lines of the same block into a one-line string. Removes
	 * indentation, line delimiters, ...
	 * 
	 * @param aDocument
	 *            Document to read
	 * @param aLineNumber
	 *            Base line number
	 * @return A one-line string
	 * @throws BadLocationException
	 *             Line number out of bounds
	 */
	public String lineBlockToLine(final IDocument aDocument,
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
		StringBuffer lineBlock = new StringBuffer();

		// Keep the indentation information
		lineBlock.append(baseIndent);

		// Append all lines of the block, without the line delimiter
		for (int i = lineBlockBegin; i <= lineBlockEnd; i++) {
			String line = getLine(aDocument, i, false);
			lineBlock.append(line.trim() + ' ');
		}

		// Remove last space character
		lineBlock.deleteCharAt(lineBlock.length() - 1);
		return lineBlock.toString();
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
	 * @return The wrapped line
	 */
	public String wrapLine(final IDocument aDocument, final String aLine,
			final int aMaxLen) {

		// Store indentation
		final String indent = getIndentation(aLine);

		// Line delimiter
		final String delim = TextUtilities.getDefaultLineDelimiter(aDocument);

		StringBuffer wrappedLine = new StringBuffer(
				(int) (aLine.length() * 1.2));

		int breakPos = 0;
		int oldBreakPos = 0;

		while ((breakPos = getLineBreakPosition(aLine, breakPos, aMaxLen)) != -1) {

			wrappedLine.append(aLine.substring(oldBreakPos, breakPos).trim());
			wrappedLine.append(delim);
			wrappedLine.append(indent);

			oldBreakPos = breakPos;
		}

		// Append last line segment
		wrappedLine.append(aLine.substring(oldBreakPos).trim());

		return wrappedLine.toString();
	}
}
