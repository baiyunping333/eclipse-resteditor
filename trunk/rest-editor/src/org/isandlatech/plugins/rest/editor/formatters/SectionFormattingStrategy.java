/**
 * File:   SectionFormattingStrategy.java
 * Author: Thomas Calmant
 * Date:   2 févr. 2011
 */
package org.isandlatech.plugins.rest.editor.formatters;

import java.util.Arrays;

import org.isandlatech.plugins.rest.editor.rules.DecoratedLinesRule;

/**
 * Automatically sets the length of section markers (upper and under lines) to
 * the title length
 * 
 * @author Thomas Calmant
 */
public class SectionFormattingStrategy extends AbstractFormattingStrategy {

	@Override
	public String format(final String aContent, final boolean aIsLineStart,
			final String aIndentation, final int[] aPositions) {

		boolean upperline = false;
		String title = null;
		String decoration = null;

		String[] lines = getLines(normalizeEndOfLines(aContent));

		// Analyze the content
		for (String token : lines) {

			// Ignore empty lines
			if (token.isEmpty()) {
				continue;
			}

			if (title == null && !DecoratedLinesRule.isDecorativeLine(token)) {
				title = token;
			}

			if (decoration == null
					&& DecoratedLinesRule.isDecorativeLine(token)) {
				decoration = token;

				// If we found a decoration line before the title, then the
				// title is upper lined
				if (title == null) {
					upperline = true;
				}
			}

			// Don't waste our time
			if (title != null && decoration != null) {
				break;
			}
		}

		if (title == null || decoration == null) {
			// Oups :o
			return aContent;
		}

		// Write a new section
		title = title.trim();

		// Fit the decoration
		char[] decorationArray = new char[title.length()];
		Arrays.fill(decorationArray, decoration.charAt(0));
		decoration = new String(decorationArray);

		StringBuffer content = new StringBuffer(aContent.length());
		if (upperline) {
			content.append(decoration).append(NORMALIZED_LINE_BREAK);
		}

		content.append(title).append(NORMALIZED_LINE_BREAK);
		content.append(decoration).append(NORMALIZED_LINE_BREAK);

		return resetEndOfLines(content.toString());
	}
}
