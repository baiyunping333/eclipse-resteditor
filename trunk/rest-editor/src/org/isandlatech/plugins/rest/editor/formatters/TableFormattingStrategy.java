/**
 * File:   TableFormattingStrategy.java
 * Author: Thomas Calmant
 * Date:   2 févr. 2011
 */
package org.isandlatech.plugins.rest.editor.formatters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.isandlatech.plugins.rest.parser.RestLanguage;

/**
 * @author Thomas Calmant
 * 
 */
public class TableFormattingStrategy extends AbstractFormattingStrategy {

	/**
	 * Extract columns content of the given line
	 * 
	 * @param aLine
	 *            A grid table line ('|' column separation)
	 * @return A column-content mapping
	 */
	private Map<Integer, String> extractLineContent(final String aLine) {

		Map<Integer, String> lineContent = new HashMap<Integer, String>();

		int column = 0;
		StringBuffer currentPart = new StringBuffer();

		for (String token : aLine.split("\\|")) {

			if (token.length() != 0
					&& token.charAt(token.length() - 1) == RestLanguage.ESCAPE_CHARACTER) {
				currentPart.append(token).append(
						RestLanguage.GRID_TABLE_ROW_MARKER);

			} else {
				if (column > 0) {

					currentPart.append(token);

					// Surround with spaces (for eye candy grids)
					String currentPartString = ' ' + currentPart.toString()
							.trim() + ' ';

					lineContent.put(column, currentPartString);
					currentPart = new StringBuffer();
				}
				column++;
			}
		}

		return lineContent;
	}

	@Override
	public String format(final String aContent, final boolean aIsLineStart,
			final String aIndentation, final int[] aPositions) {

		String[] tableLines = getLines(normalizeEndOfLines(aContent));

		int maxCols = 0;
		int nbLines = -1;

		Map<Integer, Map<Integer, String>> tableContent = new HashMap<Integer, Map<Integer, String>>();

		// Analyze the grid
		for (String line : tableLines) {

			if (line.charAt(0) == RestLanguage.GRID_TABLE_MARKER) {
				// Grid border
				int nbCols = countOccurrences(RestLanguage.GRID_TABLE_MARKER,
						line);

				if (nbCols > maxCols) {
					maxCols = nbCols;
				}

				nbLines++;

			} else {
				// Row content
				Map<Integer, String> oldMap = tableContent.get(nbLines);
				Map<Integer, String> newMap = extractLineContent(line);

				if (oldMap == null) {
					oldMap = newMap;
				} else {
					mergeMaps(oldMap, newMap);
				}

				// Should not be necessary
				tableContent.put(nbLines, oldMap);
			}
		}

		// Set up a new grid
		return generateGrid(tableContent, maxCols);
	}

	private String generateGrid(
			final Map<Integer, Map<Integer, String>> aTableContent,
			final int aMaxNbColumns) {

		StringBuffer content = new StringBuffer();

		int[] columnsSizes = new int[aMaxNbColumns];
		Arrays.fill(columnsSizes, 0);

		// 1st pass : columns sizes
		for (Map<Integer, String> lineContent : aTableContent.values()) {
			int column = 0;
			for (String columnContent : lineContent.values()) {
				int columnLength = columnContent.length();

				if (columnLength > columnsSizes[column]) {
					columnsSizes[column] = columnLength;
				}

				column++;
			}
		}

		// Generate marker line
		int maxWidth = aMaxNbColumns;
		for (int columnWidth : columnsSizes) {
			maxWidth += columnWidth;
		}

		char[] markerLineChars = new char[maxWidth + 1];
		Arrays.fill(markerLineChars, '-');

		int nextMarker = 0;
		for (int columnWidth : columnsSizes) {
			markerLineChars[nextMarker] = '+';
			nextMarker += columnWidth + 1;
		}
		markerLineChars[maxWidth] = '\n';

		final String markerLine = new String(markerLineChars);
		// -1 : the line break does'nt count
		final int markerLineLength = markerLine.length() - 1;

		// 2nd pass : fill the grid
		for (Map<Integer, String> lineContent : aTableContent.values()) {

			// add starting marker
			content.append(markerLine).append('|');

			// 1 for the starting pipe
			int linePos = 1;

			int column = 0;
			for (String columnContent : lineContent.values()) {

				// Surround with spaces
				content.append(columnContent);
				linePos += columnContent.length();

				for (int i = columnContent.length(); i < columnsSizes[column]; i++) {
					content.append(' ');
					linePos++;
				}

				content.append('|');
				column++;
				linePos++;
			}

			// Insufficient number of columns : span the last one
			if (linePos < markerLineLength) {
				int len = markerLineLength - linePos;
				char[] padding = new char[len];

				Arrays.fill(padding, ' ');
				padding[len - 1] = '|';

				content.append(padding);
			}

			content.append('\n');
		}

		// add ending marker line
		content.append(markerLine);

		return content.toString();
	}

	private Map<Integer, String> mergeMaps(final Map<Integer, String> aMapOrg,
			final Map<Integer, String> aMapAdd) {

		List<Integer> treated = new ArrayList<Integer>(aMapOrg.size());

		for (Integer column : aMapOrg.keySet()) {
			String toAdd = aMapAdd.get(column);

			if (toAdd != null) {
				// Surround with spaces (for eye candy grids)
				StringBuffer newContent = new StringBuffer();
				newContent.append(' ').append(aMapOrg.get(column).trim())
						.append(' ').append(toAdd.trim()).append(' ');

				aMapOrg.put(column, newContent.toString());
			}

			treated.add(column);
		}

		for (Integer column : aMapAdd.keySet()) {
			if (!treated.contains(column)) {
				// If we have not treated this column, then it is a valid one
				// not present in aMapOrg
				aMapOrg.put(column, aMapAdd.get(column));
			}
		}

		return aMapOrg;
	}

}
