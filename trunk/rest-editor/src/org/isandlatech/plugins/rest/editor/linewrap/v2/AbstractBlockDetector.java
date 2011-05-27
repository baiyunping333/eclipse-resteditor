/**
 * File:   AbstractBlockDetector.java
 * Author: Thomas Calmant
 * Date:   27 mai 2011
 */
package org.isandlatech.plugins.rest.editor.linewrap.v2;

import org.eclipse.jface.text.IDocument;

/**
 * @author Thomas Calmant
 */
public abstract class AbstractBlockDetector implements IBlockDetector {

	/** Line utility singleton */
	protected final LineUtil pLineUtil = LineUtil.get();

	/**
	 * Find the last line with the same indentation, content type, ... starting
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
	public abstract int findLastSimilarLine(final IDocument aDocument,
			final int aBaseLine, final int aDirection);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockDetector#getBlock
	 * (org.eclipse.jface.text.IDocument, int, int)
	 */
	@Override
	public BlockInformation getBlock(final IDocument aDocument,
			final int aBaseFirstLine, final int aBaseLastLine) {

		final String baseLineContent = pLineUtil.getLine(aDocument,
				aBaseFirstLine, false);
		if (baseLineContent == null) {
			return null;
		}

		// Search for first line
		int beginLine = findLastSimilarLine(aDocument, aBaseFirstLine, -1);
		if (beginLine < 0) {
			return null;
		}

		// Search for last line
		int lastLine = findLastSimilarLine(aDocument, aBaseLastLine, +1);
		if (lastLine < 0) {
			return null;
		}

		return new BlockInformation(beginLine, lastLine);
	}
}
