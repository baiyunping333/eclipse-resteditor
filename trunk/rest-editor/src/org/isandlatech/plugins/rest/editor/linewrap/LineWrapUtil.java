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
		/** Hard wrapping (end of line sequences added) */
		HARD,
		/** No wrapping */
		NONE,
		/** Virtual wrapping (text view only) */
		SOFT,
	}

	/** Minimal line length, under which no wrapping may be done */
	public static final int MINIMAL_LINE_LENGTH = 20;

	/** Lien wrap utility singleton */
	private static LineWrapUtil sSingleton;

	/**
	 * Grabs an instance of the utility singleton
	 * 
	 * @return An instance of LineWrapUtil
	 */
	public static LineWrapUtil get() {
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
	 * Indicates if the given wrapping mode is enabled
	 * 
	 * @param aMode
	 *            The wrapping mode to be tested
	 * @return True if the current wrapping mode is aMode, else false
	 */
	public boolean isActiveMode(final LineWrapMode aMode) {

		return pPreferenceStore.getString(
				IEditorPreferenceConstants.EDITOR_LINEWRAP_MODE).equals(
				aMode.toString());
	}

	/**
	 * Indicates if line wrapping is activated
	 * 
	 * @return True if line wrapping is activated, else false
	 */
	public boolean isWrappingEnabled() {
		return !isActiveMode(LineWrapMode.NONE);
	}
}
