/**
 * File:   DefaultTextFormattingStrategy.java
 * Author: Thomas Calmant
 * Date:   12 mai 2011
 */
package org.isandlatech.plugins.rest.editor.formatters;

/**
 * Default text formatter : deletes trailing white spaces
 * 
 * @author Thomas Calmant
 */
public class DefaultTextFormattingStrategy extends AbstractFormattingStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.formatters.AbstractFormattingStrategy
	 * #format(java.lang.String, boolean, java.lang.String, int[])
	 */
	@Override
	public String format(final String aContent, final boolean aIsLineStart,
			final String aIndentation, final int[] aPositions) {

		String normalizedContent = normalizeEndOfLines(aContent);
		StringBuilder newContent = new StringBuilder(aContent.length());

		// Standard line breaks
		String[] lines = getLines(normalizedContent);
		for (String line : lines) {

			// Trim right
			newContent.append(line.replaceAll("[\\s]+$", ""));

			// Reset line break
			if (line.isEmpty()) {
				newContent.append(NORMALIZED_LINE_BREAK);
			}
		}

		return resetEndOfLines(newContent.toString());
	}
}
