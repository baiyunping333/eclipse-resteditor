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
import org.eclipse.jface.text.TextUtilities;
import org.isandlatech.plugins.rest.parser.RestLanguage;

/**
 * Detects list blocks (separated by empty lines, other list entries, ...)
 * 
 * @author Thomas Calmant
 */
public class ListBlockDetector extends AbstractBlockDetector {

	/** Detector type */
	public static final String DETECTOR_TYPE = "__list_block_detector__";

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

		final String baseLineIndent = pLineUtil.getIndentation(baseLineContent);
		final int baseLineIndentLen = baseLineIndent.length();

		// Search for first line
		final int nbLines = aDocument.getNumberOfLines();
		boolean bulletFound = false;
		int searchLine = aBaseLine;

		// Line where to start the research
		int searchStartLine = aBaseLine;
		if (aDirection > 0) {
			// Don't stop at the current line if we are looking down
			searchStartLine++;
		}

		for (int i = searchStartLine; i >= 0 && i < nbLines; i += aDirection) {

			String line = pLineUtil.getLine(aDocument, i, false);
			String trimmedLine = line.trim();
			if (trimmedLine.isEmpty()) {
				break;
			}

			String lineIndent = pLineUtil.getIndentation(line);
			if (lineIndent.length() > baseLineIndentLen && aDirection < 0) {
				// Indented block found while moving up
				break;
			}

			if (lineIndent.length() < baseLineIndentLen && aDirection > 0) {
				// Un-indented block found while moving down
				break;
			}

			if (TextUtilities
					.startsWith(RestLanguage.LIST_MARKERS, trimmedLine) != -1) {
				// We found the beginning of a list item

				if (aDirection < 0) {
					// If we are moving up, we must include this line
					searchLine = i;
				}

				bulletFound = true;
				break;
			}

			searchLine = i;
		}

		if (!bulletFound && aDirection < 0) {
			// No bullet found while moving up : we're not in a list
			return -1;
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
		return ListBlockWrappingHandler.HANDLER_TYPE;
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
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.IBlockDetector#getType()
	 */
	@Override
	public String getType() {
		return DETECTOR_TYPE;
	}

}
