/**
 * File:   AbstractFormattingStrategy.java
 * Author: Thomas Calmant
 * Date:   2 févr. 2011
 */
package org.isandlatech.plugins.rest.editor.formatters;

import java.util.Arrays;
import java.util.StringTokenizer;

import org.eclipse.jface.text.formatter.IFormattingStrategy;

/**
 * @author Thomas Calmant
 * 
 */
public abstract class AbstractFormattingStrategy implements IFormattingStrategy {

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
	 * Retrieves an of all lines extracted from the given string
	 * 
	 * @param aText
	 *            Source text
	 * @return An array of lines
	 */
	public static String[] getLines(final String aText) {

		StringTokenizer tokenizer = new StringTokenizer(aText, "\n");
		String[] result = new String[tokenizer.countTokens()];

		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			result[i++] = tokenizer.nextToken();
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

	/**
	 * Converts all lines ending into "\n"
	 * 
	 * @param aText
	 *            Source text
	 * @return A converted copy of the source text
	 */
	public static String normalizeEndOfLines(final String aText) {
		if (aText == null) {
			return null;
		}

		return aText.replace('\r', '\n').replace("\n\n", "\n");
	}

	/** Initial indentation */
	private String pInitialIndentation = "";

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

}
