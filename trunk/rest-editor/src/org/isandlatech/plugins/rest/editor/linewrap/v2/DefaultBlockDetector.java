/**
 * File:   DefaultBlockDetector.java
 * Author: Thomas Calmant
 * Date:   27 mai 2011
 */
package org.isandlatech.plugins.rest.editor.linewrap.v2;

import org.eclipse.jface.text.IDocument;

/**
 * Detects a block that is separated by an empty line or by elements that have a
 * different indentation.
 * 
 * 
 * @author Thomas Calmant
 */
public class DefaultBlockDetector extends AbstractBlockDetector {

	/** Partitioning used by the document scanner */
	private String pPartitioning;

	/**
	 * Default constructor
	 */
	public DefaultBlockDetector() {
		this(null);
	}

	/**
	 * Sets up the partitioning test
	 * 
	 * @param aPartitioning
	 *            The partitioning to be tested (can be null)
	 */
	public DefaultBlockDetector(final String aPartitioning) {
		pPartitioning = aPartitioning;
	}

	/**
	 * Find the last line with the same indentation and content type, starting
	 * from the base line number and incrementing by direction.
	 * 
	 * @param aDocument
	 *            Document to read
	 * @param aBaseLine
	 *            Reference line
	 * @param aDirection
	 *            +1 or -1 : increment to use during the research
	 * @return The last similar line, -1 on error.
	 */
	@Override
	public int findLastSimilarLine(final IDocument aDocument,
			final int aBaseLine, final int aDirection) {

		final String baseLineContent = pLineUtil.getLine(aDocument, aBaseLine,
				false);

		if (baseLineContent == null) {
			return -1;
		}

		final String baseLineContentType = pLineUtil.getContentType(aDocument,
				aBaseLine, pPartitioning);

		final String baseLineIndent = pLineUtil.getIndentation(baseLineContent);
		final int baseLineIndentLen = baseLineIndent.length();

		// Search for first line
		final int nbLines = aDocument.getNumberOfLines();
		int searchLine = aBaseLine;
		for (int i = aBaseLine; i >= 0 && i < nbLines; i += aDirection) {

			String line = pLineUtil.getLine(aDocument, i, false);
			if (line.trim().isEmpty()) {
				break;
			}

			String lineIndent = pLineUtil.getIndentation(line);
			if (lineIndent.length() != baseLineIndentLen) {
				break;
			}

			String lineContentType = pLineUtil.getContentType(aDocument, i,
					pPartitioning);
			if (baseLineContentType != null
					&& !baseLineContentType.equals(lineContentType)) {
				break;
			}

			searchLine = i;
		}

		return searchLine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockDetector#getHandlerType
	 * ()
	 */
	@Override
	public String getHandlerType() {
		return IBlockWrappingHandler.DEFAULT_HANDLER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockDetector#getPriority
	 * ()
	 */
	@Override
	public int getPriority() {
		return Integer.MAX_VALUE - 1;
	}
}
