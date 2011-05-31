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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.AbstractBlockDetector
	 * #findLastSimilarLine(org.eclipse.jface.text.IDocument, int, int)
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
