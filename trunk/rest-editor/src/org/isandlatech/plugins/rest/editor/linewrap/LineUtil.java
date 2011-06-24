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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IRegion;
import org.isandlatech.plugins.rest.RestPlugin;

/**
 * Utility methods for line treatments
 * 
 * @author Thomas Calmant
 */
public final class LineUtil {

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
				RestPlugin.logError("Error while retrieving line content type",
						e);
			} catch (BadPartitioningException e) {
				RestPlugin.logError(
						"Error while retrieve document partitionner", e);
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
			RestPlugin.logError("Error while retrieving line content", e);
			return null;
		}

		return line;
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
