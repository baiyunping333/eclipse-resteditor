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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.isandlatech.plugins.rest.RestPlugin;
import org.isandlatech.plugins.rest.prefs.IEditorPreferenceConstants;

/**
 * Line wrapping utility class
 * 
 * @author Thomas Calmant
 */
public class LineWrapUtil {

	/**
	 * Line wrap modes definition
	 * 
	 * @author Thomas Calmant
	 */
	public enum LineWrapMode {
		/** No wrapping */
		NONE,
		/** Virtual wrapping (text view only) */
		SOFT,
		/** Hard wrapping (end of line sequences added) */
		HARD,
	}

	/** Minimal line length, under which no wrapping may be done */
	public static final int MINIMAL_LINE_LENGTH = 10;

	/** Lien wrap utility singleton */
	private static LineWrapUtil sSingleton;

	/**
	 * Grabs an instance of the utility singleton
	 * 
	 * @return An instance of LineWrapUtil
	 */
	public static LineWrapUtil getInstance() {
		if (sSingleton == null) {
			sSingleton = new LineWrapUtil();
		}

		return sSingleton;
	}

	/** Plug-in preference store */
	private IPreferenceStore pPreferenceStore;

	/**
	 * Stores an instance to the preference store
	 */
	private LineWrapUtil() {
		pPreferenceStore = RestPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Tests if the given text contains a line delimiter. Based on
	 * {@link TextUtilities#DELIMITERS}
	 * 
	 * @param aText
	 *            Text to be tested
	 * @return True if the text contains a line delimiter, else false
	 */
	public boolean containsLineDelimiter(final String aText) {

		for (String delimiter : TextUtilities.DELIMITERS) {
			if (aText.contains(delimiter)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Retrieves the maximum length of a line before it gets wrapped. The
	 * minimum value accepted is {@link #MINIMAL_LINE_LENGTH} =
	 * {@value #MINIMAL_LINE_LENGTH}
	 * 
	 * @return The maximum length of a line
	 */
	public int getMaxLineLength() {

		int maxLen = pPreferenceStore
				.getInt(IEditorPreferenceConstants.EDITOR_LINEWRAP_LENGTH);

		if (maxLen <= MINIMAL_LINE_LENGTH) {
			maxLen = pPreferenceStore
					.getDefaultInt(IEditorPreferenceConstants.EDITOR_LINEWRAP_LENGTH);
		}

		return Math.max(maxLen, MINIMAL_LINE_LENGTH);
	}

	/**
	 * Hards wrap the current line if its length goes over the preferred limit.
	 * 
	 * Inspired by Texlipse hard line wrap.
	 * 
	 * @param aDocument
	 *            Currently edited document
	 * @param aCommand
	 *            Document customization command
	 * @return The number of characters added to the document
	 * 
	 * @throws BadLocationException
	 *             Document command gives out of bound values
	 */
	public int hardWrapLine(final IDocument aDocument, final int aOffset)
			throws BadLocationException {

		// Get the line
		IRegion commandRegion = aDocument.getLineInformationOfOffset(aOffset);

		System.out.println("Region : " + commandRegion);
		System.out.println("Region text : '"
				+ aDocument.get(commandRegion.getOffset(),
						commandRegion.getLength()) + "'");

		return 0;

		// final String endOfLine = TextUtilities
		// .getDefaultLineDelimiter(aDocument);
		//
		// final int maxLen = getMaxLineLength();
		//
		// int nbNewLines = 0;
		// int line = aDocument.getLineOfOffset(aOffset);
		// int lineOffset = aDocument.getLineOffset(line);
		// int lineLen = aDocument.getLineLength(line);
		//
		// if (lineLen < maxLen) {
		// return 0;
		// }
		//
		// String beforeNewEnd;
		// String afterNewEnd;
		// do {
		// final String oldLineContent = aDocument.get(lineOffset, lineLen);
		// beforeNewEnd = oldLineContent.substring(0, maxLen);
		// afterNewEnd = oldLineContent.substring(maxLen);
		//
		// aDocument.replace(lineOffset, lineLen, beforeNewEnd + endOfLine
		// + afterNewEnd);
		//
		// nbNewLines++;
		// line++;
		// lineOffset = aDocument.getLineOffset(line);
		// lineLen = aDocument.getLineLength(line);
		//
		// } while (lineLen > maxLen);
		//
		// return nbNewLines * endOfLine.length();
	}
}
