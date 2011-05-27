/**
 * File:   DefaultBlockWrappingHandler.java
 * Author: Thomas Calmant
 * Date:   27 mai 2011
 */
package org.isandlatech.plugins.rest.editor.linewrap.v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Default block wrapper : tries to conserve the indentation
 * 
 * @author Thomas Calmant
 */
public class DefaultBlockWrappingHandler extends AbstractBlockWrappingHandler {

	/**
	 * Converts the given text block into a single line. Conserves the
	 * indentation of the first line only.
	 * 
	 * After that point, {@link #getReferenceOffset()} is relative to the
	 * beginning of the result line
	 * 
	 * @param aText
	 *            Text block to be converted
	 * @param aLineDelimiter
	 *            Line delimiter used by the document
	 * @param aBlockOffset
	 *            An offset in the block that will be made relative to the line
	 *            and stored in the result
	 * @return The block in one line
	 */
	protected StringBuilder convertBlockInLine(final String aText) {

		final int delimLen = pLineDelimiter.length();

		// The result builder
		StringBuilder resultLine = new StringBuilder(aText.length());

		// Offset treatment variables
		int previousOffsetInOrigin = 0;
		int currentOffsetInOrigin = 0;
		int currentOffsetInResult = 0;
		int newOffset = 0;
		int blockRelativeReferenceOffset = pReferenceOffset
				- pDocBlock.getOffset();

		// Read the block
		BufferedReader reader = new BufferedReader(new StringReader(aText));
		String currentLine;

		// Insert indentation first (and update currentOffsetInResult)
		String indent = pLineUtil.getIndentation(aText);
		resultLine.append(indent);
		currentOffsetInResult += indent.length();

		try {
			while ((currentLine = reader.readLine()) != null) {

				String trimmedLine = pLineUtil.ltrim(currentLine);
				if (trimmedLine.isEmpty()) {
					continue;
				}

				resultLine.append(trimmedLine);
				currentOffsetInResult += trimmedLine.length();

				// Add trailing space, if needed
				if (!pLineUtil
						.isSpace(trimmedLine.charAt(trimmedLine.length() - 1))) {

					resultLine.append(' ');
					currentOffsetInResult++;
				}

				currentOffsetInOrigin += currentLine.length() + delimLen;

				if (blockRelativeReferenceOffset >= previousOffsetInOrigin
						&& blockRelativeReferenceOffset < currentOffsetInOrigin) {

					newOffset = blockRelativeReferenceOffset
							- currentOffsetInOrigin;
					newOffset += currentOffsetInResult;
				}

				previousOffsetInOrigin = currentOffsetInOrigin;
			}

		} catch (IOException e) {
			// Really ?
			e.printStackTrace();
		}

		// Remove the last added trailing space
		if (resultLine.length() > 0) {
			resultLine.deleteCharAt(resultLine.length() - 1);
		}

		pReferenceOffset = newOffset;
		return resultLine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #getType()
	 */
	@Override
	public String getType() {
		return DEFAULT_HANDLER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #wrap()
	 */
	@Override
	public String wrap(final int aMaxLen) {

		// Transform the block in a line
		StringBuilder blockInLine = convertBlockInLine(pBlockContent);

		// Do the wrapping
		pBlockContent = wrapLine(blockInLine.toString(), aMaxLen).toString();

		// Convert internals EOL into document ones
		replaceInternalLineMarkers();
		return pBlockContent;
	}

	/**
	 * Wraps the given line adding document line delimiter where necessary and
	 * keeping the base indentation.
	 * 
	 * After that point, {@link #getReferenceOffset()} is relative to the
	 * document.
	 * 
	 * @param aLine
	 *            Line to wrap
	 * @param aMaxLen
	 *            Maximum length of a line
	 * @param aOffsetInLine
	 *            An offset in the entry line that will be updated, visible in
	 *            the result offset field
	 * @return The wrapped line
	 */
	protected StringBuilder wrapLine(final String aLine, final int aMaxLen) {

		// Store indentation
		final String indent = pLineUtil.getIndentation(aLine);
		final int indentLen = indent.length();

		// Line delimiter
		final int delimLen = pLineDelimiter.length();

		StringBuilder wrappedLine = new StringBuilder(
				(int) (aLine.length() * 1.2));

		int breakPos = 0;
		int oldBreakPos = 0;

		int currentOffsetInResult = 0;
		int newOffset = -1;

		while ((breakPos = pLineUtil.getLineBreakPosition(aLine, breakPos,
				aMaxLen - indentLen)) != -1) {

			String subLine = aLine.substring(oldBreakPos, breakPos);
			String trimmedSubline = pLineUtil.ltrim(subLine);

			wrappedLine.append(indent);
			wrappedLine.append(trimmedSubline);
			wrappedLine.append(pLineDelimiter);

			// If the offset is in the currently modified block...
			if (pReferenceOffset >= oldBreakPos && pReferenceOffset < breakPos) {

				// Set it to be relative to the new line
				newOffset = pReferenceOffset - oldBreakPos;

				// Correct getLineBreakPosition indentation forgiveness
				if (oldBreakPos > 0) {
					newOffset -= (subLine.length() - trimmedSubline.length());
					newOffset += indentLen;
				}

				// Make it relative to the current block state
				newOffset += currentOffsetInResult;
			}

			currentOffsetInResult += indentLen + trimmedSubline.length()
					+ delimLen;

			oldBreakPos = breakPos;
		}

		// Remove the last delimiter
		int wrappedLineLen = wrappedLine.length();
		if (wrappedLineLen - delimLen > 0) {
			wrappedLine.delete(wrappedLineLen - delimLen, wrappedLineLen);
		}

		pReferenceOffset = newOffset + pDocBlock.getOffset();
		return wrappedLine;
	}
}
