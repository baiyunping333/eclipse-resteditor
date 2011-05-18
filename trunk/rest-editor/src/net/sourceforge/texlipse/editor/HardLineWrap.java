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
	public void doWrapB(final IDocument aDocument,
			final DocumentCommand aCommand) {

		final int maxLineLength = LineWrapUtil.getInstance().getMaxLineLength();

		try {
			// Get the line of the command excluding delimiter
			IRegion commandRegion = aDocument
					.getLineInformationOfOffset(aCommand.offset);

			// Ignore texts with line endings
			if (commandRegion.getLength() + aCommand.text.length() <= maxLineLength
					|| LineWrapUtil.getInstance().containsLineDelimiter(
							aCommand.text)) {
				return;
			}

			String docLine = aDocument.get(commandRegion.getOffset(),
					commandRegion.getLength());
			int docLineLength = docLine.length();

			int docLineNumber = aDocument.getLineOfOffset(aCommand.offset);
			final int offsetOnLine = aCommand.offset
					- commandRegion.getOffset();

			// Create the newLine, we rewrite the whole current line
			StringBuffer newLineBuf = new StringBuffer();

			newLineBuf.append(docLine.substring(0, offsetOnLine));
			newLineBuf.append(aCommand.text);
			newLineBuf.append(rtrim(docLine.substring(offsetOnLine)));

			// Special case if there are white spaces at the end of the line
			if (rtrim(newLineBuf.toString()).length() <= maxLineLength) {
				System.out.println("No need to wrap : white spaces...");
				return;
			}

			String delim = aDocument.getLineDelimiter(docLineNumber);
			boolean isLastLine = false;
			if (delim == null) {
				// This is the last line in the document
				isLastLine = true;
				if (docLineNumber > 0) {
					delim = aDocument.getLineDelimiter(docLineNumber - 1);
				} else {
					// Last chance
					String delims[] = aDocument.getLegalLineDelimiters();
					delim = delims[0];
				}
			}

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

			int breakpos = getLineBreakPosition(newLine, maxLineLength);
			if (breakpos < 0) {
				return;
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

		} catch (BadLocationException e) {
			DebugPlugin.logMessage("Problem with hard line wrap", e);
		}
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
			if (!aIncludeDelimiters) {
				int delimIndex = TextUtilities.endsWith(
						TextUtilities.DELIMITERS, line);

				if (delimIndex >= 0) {
					line = line.substring(0, line.length()
							- TextUtilities.DELIMITERS[delimIndex].length());
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
	 * @param aMaxLineLength
	 *            Maximum line length
	 * @return The best position to break the line
	 */
	private int getLineBreakPosition(final String aLine,
			final int aMaxLineLength) {

		int offset = 0;
		// Ignore indentation
		while (offset < aLine.length()
				&& Character.isWhitespace(aLine.charAt(offset))) {
			offset++;
		}

		int breakOffset = -1;
		while (offset < aLine.length()) {
			if (offset > aMaxLineLength && breakOffset != -1) {
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

		if (aLine.length() == 0) {
			return true;
		}

		// Literal lines are ignored
		if (aLine.startsWith(".. ")) {
			return true;
		}

		return false;
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
}
