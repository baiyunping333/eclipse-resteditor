/**
 * File:   GridTableFormattingStrategy.java
 * Author: Thomas Calmant
 * Date:   2 févr. 2011
 */
package org.isandlatech.plugins.rest.editor.formatters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.isandlatech.plugins.rest.parser.RestLanguage;

/**
 * reStructuredText grid tables formatter. Automatically aligns columns.
 * 
 * @author Thomas Calmant
 */
public class GridTableFormattingStrategy extends AbstractFormattingStrategy {

	/**
	 * Kind of separation line
	 * 
	 * @author Thomas Calmant
	 */
	public enum KindOfGridLine {
		/** Grid line with '-' */
		SIMPLE_SEPARATOR,
		/** Grid line with '=' */
		DOUBLE_SEPARATOR,
		/** Line content */
		CONTENT,
	}

	/**
	 * Extracts columns content of the given line
	 * 
	 * @param aLine
	 *            A grid table line ('|' column separation)
	 * @return A column-content mapping
	 */
	protected Map<Integer, String> extractLineContent(final String aLine) {

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

					// TODO If the pipe is followed immediately by a letter,
					// then
					// it's a substitution
					// if (token.length() > 1
					// && Character.isLetterOrDigit(token.charAt(1))) {
					// System.out.println("Substitution found : " + token);
					// continue;
					// }

					// Surround with spaces (for eye candy grids)
					String currentPartString = ' ' + currentPart.toString()
							.trim();

					if (currentPartString.length() > 1) {
						currentPartString += ' ';
					}

					lineContent.put(column, currentPartString);
					currentPart = new StringBuffer();
				}
				column++;
			}
		}

		return lineContent;
	}

	/**
	 * Formats the complete grid table.
	 */
	@Override
	public String format(final String aContent, final boolean aIsLineStart,
			final String aIndentation, final int[] aPositions) {

		String[] tableLines = getLines(normalizeEndOfLines(aContent));

		int maxCols = 0;
		int nbLines = 0;

		Map<Integer, Map<Integer, String>> tableContent = new HashMap<Integer, Map<Integer, String>>();
		List<KindOfGridLine> kindOfLine = new LinkedList<KindOfGridLine>();

		// Analyze the grid
		for (String line : tableLines) {

			if (line.charAt(0) == RestLanguage.GRID_TABLE_MARKER) {
				// Grid line

				// Look for the character used for separation ('-' or '=' in
				// theory...)
				char separator = RestLanguage.GRID_TABLE_MARKER;
				for (int i = 1; separator == RestLanguage.GRID_TABLE_MARKER
						&& i < line.length(); i++) {

					if (line.charAt(i) != RestLanguage.GRID_TABLE_MARKER) {
						separator = line.charAt(i);
					}
				}

				// Force normalization
				if (separator != '=') {
					separator = '-';
				}

				// Grid border
				int nbCols = countOccurrences(RestLanguage.GRID_TABLE_MARKER,
						line);

				if (nbCols > maxCols) {
					maxCols = nbCols;
				}

				if (separator == '=') {
					kindOfLine.add(KindOfGridLine.DOUBLE_SEPARATOR);
				} else {
					kindOfLine.add(KindOfGridLine.SIMPLE_SEPARATOR);
				}

			} else {
				// Row content
				Map<Integer, String> lineMap = extractLineContent(line);
				tableContent.put(nbLines++, lineMap);

				// We want the outer limit of the column count
				// (like if you count the gaps or the poles)
				int nbCols = lineMap.size() + 1;
				if (nbCols > maxCols) {
					maxCols = nbCols;
				}

				kindOfLine.add(KindOfGridLine.CONTENT);
			}
		}

		// Set up a new grid
		return generateGrid(tableContent, maxCols, kindOfLine);
	}

	/**
	 * Generate the grid table
	 * 
	 * @param aTableContent
	 *            Line -> Column -> Content mapping
	 * @param aMaxNbColumns
	 *            Maximum number of column for a line
	 * @param aKindOfLine
	 *            For each line, the kind of treatment to applied
	 * @return The formatted grid table string
	 */
	protected String generateGrid(
			final Map<Integer, Map<Integer, String>> aTableContent,
			final int aMaxNbColumns, final List<KindOfGridLine> aKindOfLine) {

		StringBuffer content = new StringBuffer();

		// Get columns maximum sizes
		int[] columnsSizes = getColumnsSizes(aTableContent, aMaxNbColumns);

		// Generate marker lines
		String markerLineSimple = generateMarkerLine(aMaxNbColumns,
				columnsSizes, '-');
		String markerLineDouble = generateMarkerLine(aMaxNbColumns,
				columnsSizes, '=');

		// -1 : the line break doesn't count
		final int markerLineLength = markerLineSimple.length() - 1;

		int currentLine = 0;
		for (KindOfGridLine kindOfLine : aKindOfLine) {
			switch (kindOfLine) {
			case SIMPLE_SEPARATOR:
				content.append(markerLineSimple);
				break;

			case DOUBLE_SEPARATOR:
				content.append(markerLineDouble);
				break;

			case CONTENT:
				generateLine(content, aTableContent.get(currentLine++),
						columnsSizes, markerLineLength);
				break;

			default:
				// Do nothing
				break;
			}
		}

		return content.toString();
	}

	/**
	 * Generates a grid line with the given cells content. Appends the line to
	 * the working String buffer. Adds the end line character.
	 * 
	 * @param aContent
	 *            Current grid content
	 * @param aCellsContent
	 *            A mapping : column number -> column content (for the current
	 *            line)
	 * @param aColumnsSizes
	 *            An array containing the width of each column
	 * @param aMarkerLineLength
	 *            The length of the marker lines, i.e. the width of the grid
	 *            table
	 */
	protected void generateLine(final StringBuffer aContent,
			final Map<Integer, String> aCellsContent,
			final int[] aColumnsSizes, final int aMarkerLineLength) {

		if (aCellsContent == null) {
			return;
		}

		int column = 0;
		int linePos = 1;

		aContent.append(RestLanguage.GRID_TABLE_ROW_MARKER);

		for (String columnContent : aCellsContent.values()) {

			// Surround with spaces
			aContent.append(columnContent);
			linePos += columnContent.length();

			for (int i = columnContent.length(); i < aColumnsSizes[column]; i++) {
				aContent.append(' ');
				linePos++;
			}

			aContent.append(RestLanguage.GRID_TABLE_ROW_MARKER);
			column++;
			linePos++;
		}

		// Insufficient number of columns : span the last one
		if (linePos < aMarkerLineLength) {
			int len = aMarkerLineLength - linePos - 1;
			char[] padding = new char[len];
			Arrays.fill(padding, ' ');

			aContent.append(padding);
			aContent.append(RestLanguage.GRID_TABLE_ROW_MARKER);
		}

		aContent.append('\n');
	}

	/**
	 * Generates a grid separation line
	 * 
	 * @param aMaxNbColumns
	 *            Maximum number of columns
	 * @param aColumnsSizes
	 *            An array containing column sizes
	 * @param aSeparator
	 *            The character to be used to fill the line
	 * @return The corresponding separation line
	 */
	private String generateMarkerLine(final int aMaxNbColumns,
			final int[] aColumnsSizes, final char aSeparator) {

		// Generate marker lines
		int maxWidth = aMaxNbColumns;
		for (int columnWidth : aColumnsSizes) {
			maxWidth += columnWidth;
		}

		char[] markerLineChars = new char[maxWidth + 1];
		Arrays.fill(markerLineChars, aSeparator);

		int nextMarker = 0;
		for (int columnWidth : aColumnsSizes) {
			markerLineChars[nextMarker] = RestLanguage.GRID_TABLE_MARKER;
			nextMarker += columnWidth + 1;
		}

		markerLineChars[maxWidth] = '\n';
		return new String(markerLineChars);
	}

	/**
	 * Retrieves the maximum size of each grid column
	 * 
	 * @param aTableContent
	 *            Grid table content
	 * @param aMaxNbColumns
	 *            Maximum amount of columns
	 * @return An array containing the maximum size of each column
	 */
	private int[] getColumnsSizes(
			final Map<Integer, Map<Integer, String>> aTableContent,
			final int aMaxNbColumns) {

		int[] columnsSizes = new int[aMaxNbColumns];
		Arrays.fill(columnsSizes, 0);

		// complete pass to get columns sizes
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

		return columnsSizes;
	}
}
