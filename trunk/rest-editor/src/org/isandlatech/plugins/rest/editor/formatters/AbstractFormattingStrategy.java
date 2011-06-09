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

package org.isandlatech.plugins.rest.editor.formatters;

import java.util.Arrays;
import java.util.StringTokenizer;

import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.formatter.IFormattingStrategy;

/**
 * Some common utility / overriden methods to provide a formatting strategy
 * 
 * @author Thomas Calmant
 */
public abstract class AbstractFormattingStrategy implements IFormattingStrategy {

	/** Well known line breaker, for internal string representation */
	public static final String NORMALIZED_LINE_BREAK = "\n";

	/**
	 * Counts the occurrences of the given character in the given text
	 * 
	 * @param aCharacter
	 *            Character to count
	 * @param aText
	 *            Text to analyze
	 * @return The number of occurrences
	 */
	public static int countOccurrences(final char aCharacter, final String aText) {
		int occurrences = 0;

		if (aText != null) {
			for (char character : aText.toCharArray()) {
				if (character == aCharacter) {
					occurrences++;
				}
			}
		}

		return occurrences;
	}

	/**
	 * Retrieves an of all lines extracted from the given normalized string.
	 * 
	 * The given string should be the result of
	 * {@link #normalizeEndOfLines(String)}.
	 * 
	 * @param aNormalizedText
	 *            Source text, after having been normalized.
	 * @return An array of lines
	 */
	public static String[] getLines(final String aNormalizedText) {

		StringTokenizer tokenizer = new StringTokenizer(aNormalizedText,
				NORMALIZED_LINE_BREAK, true);

		String[] result = new String[tokenizer.countTokens()];

		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken();

			// Remove the token, this way, we can handle empty lines.
			if (line.equals(NORMALIZED_LINE_BREAK)) {
				line = "";
			}

			result[i++] = line;
		}

		return result;
	}

	/**
	 * Converts leading tabulations to spaces
	 * 
	 * @param aText
	 *            Text to be converted
	 * @param tabWidth
	 *            Number of spaces for a tabulation
	 * @return A converted copy of the given text
	 */
	public static String leadingTabsToSpaces(final String aText,
			final int tabWidth) {

		StringBuffer result = new StringBuffer(aText.length());

		char[] tabSpaces = new char[tabWidth];
		Arrays.fill(tabSpaces, ' ');
		final String tabString = new String(tabSpaces);

		boolean leadingSpace = true;
		for (char character : aText.toCharArray()) {

			if (leadingSpace && character == '\t') {
				// Replace leading tabulation
				result.append(tabString);

			} else {
				// Copy the rest of the line
				if (character == '\n') {
					leadingSpace = true;
				} else if (!Character.isWhitespace(character)) {
					leadingSpace = false;
				}

				result.append(character);
			}
		}

		return result.toString();
	}

	/** Initial indentation */
	private String pInitialIndentation = "";

	/** Document end of line character(s) */
	private String pEndOfLine;

	@Override
	public abstract String format(String aContent, boolean aIsLineStart,
			String aIndentation, int[] aPositions);

	@Override
	public void formatterStarts(final String aInitialIndentation) {
		pInitialIndentation = aInitialIndentation;
	}

	@Override
	public void formatterStops() {
		pInitialIndentation = "";
	}

	/**
	 * Retrieves the block initial indentation
	 * 
	 * @return The initial indentation
	 * @see IFormattingStrategy#formatterStarts(String)
	 */
	protected String getInitialIndentation() {
		return pInitialIndentation;
	}

	/**
	 * Converts all lines ending into "\n"
	 * 
	 * @param aText
	 *            Source text
	 * @return A converted copy of the source text
	 */
	public String normalizeEndOfLines(final String aText) {

		if (aText == null) {
			return null;
		}

		// Replace current line break by a well known one
		pEndOfLine = TextUtilities.determineLineDelimiter(aText,
				NORMALIZED_LINE_BREAK);
		return aText.replace(pEndOfLine, NORMALIZED_LINE_BREAK);
	}

	/**
	 * Reset end of line characters to document ones. May be called after
	 * treatments on a {@link #normalizeEndOfLines(String)} result.
	 * 
	 * @param aNormalizedText
	 *            Normalized text
	 * @return A text with document end of lines
	 */
	public String resetEndOfLines(final String aNormalizedText) {

		if (aNormalizedText == null) {
			return null;
		}

		// Replace current line break by a well known one
		if (pEndOfLine == null) {
			pEndOfLine = TextUtilities.determineLineDelimiter(aNormalizedText,
					NORMALIZED_LINE_BREAK);
		}

		return aNormalizedText.replace(NORMALIZED_LINE_BREAK, pEndOfLine);
	}
}
