/**
 * File:   WrapUtil.java
 * Author: Thomas Calmant
 * Date:   27 mai 2011
 */
package org.isandlatech.plugins.rest.editor.linewrap.v2;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IRegion;

/**
 * @author Thomas Calmant
 * 
 */
public class LineUtil {

	/** The singleton */
	private static LineUtil sSingleton;

	/**
	 * Get the line utility singleton
	 * 
	 * @return the line utility singleton
	 */
	public static LineUtil get() {
		if (sSingleton == null) {
			sSingleton = new LineUtil();
		}

		return sSingleton;
	}

	/**
	 * Singleton
	 */
	private LineUtil() {
		// Singleton
	}

	/**
	 * Retrieves the content type of the first column of the given line
	 * 
	 * @param aDocument
	 *            Document containing the line
	 * @param aLineNumber
	 *            Number of the line to read
	 * @param aPartitionning
	 *            Partitioner to be used
	 * @return The content type, null on error
	 */
	public String getContentType(final IDocument aDocument,
			final int aLineNumber, final String aPartitionning) {

		if (aPartitionning == null) {
			return null;
		}

		if (aDocument instanceof IDocumentExtension3) {
			try {

				int baseLineOffset = aDocument.getLineOffset(aLineNumber);

				return ((IDocumentExtension3) aDocument).getPartition(
						aPartitionning, baseLineOffset, false).getType();

			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (BadPartitioningException e) {
				e.printStackTrace();
			}
		}

		return null;
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
	 * Retrieves the given line content, with or without its end delimiter.
	 * Returns null if the line is out of the document.
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
			return null;
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
			return null;
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
	 * @return The best position to break the line, -1 on error / on stop
	 */
	public int getLineBreakPosition(final String aLine, final int aBaseOffset,
			final int aMaxLineLength) {

		if (aBaseOffset >= aLine.length()) {
			return -1;
		}

		if (aLine.length() < aMaxLineLength) {
			return aLine.length();
		}

		int offset = aBaseOffset;
		// Ignore indentation
		while (offset < aLine.length() && isSpace(aLine.charAt(offset))) {
			offset++;
		}

		int lastSpaceOffset = -1;
		int breakOffset = aLine.length();

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
}
