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

package org.isandlatech.plugins.rest.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.isandlatech.plugins.rest.RestPlugin;
import org.isandlatech.plugins.rest.editor.linewrap.LineWrapUtil.LineWrapMode;

/**
 * Sets the default values for each preference entry.
 * 
 * @author Thomas Calmant
 */
public class EditorPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = RestPlugin.getDefault().getPreferenceStore();

		// Save actions
		store.setDefault(IEditorPreferenceConstants.EDITOR_SAVE_FORMAT, true);
		store.setDefault(IEditorPreferenceConstants.EDITOR_SAVE_TRIM, false);

		// Markers (Python standard)
		store.setDefault(IEditorPreferenceConstants.EDITOR_SAVE_RESET_MARKERS,
				false);
		store.setDefault(IEditorPreferenceConstants.EDITOR_SECTION_MARKERS,
				"#*=-^\"");

		// Tabulations
		store.setDefault(IEditorPreferenceConstants.EDITOR_TABS_LENGTH, 3);
		store.setDefault(IEditorPreferenceConstants.EDITOR_TABS_TO_SPACES, true);

		// Line wrap
		store.setDefault(IEditorPreferenceConstants.EDITOR_LINEWRAP_MODE,
				LineWrapMode.NONE.toString());
		store.setDefault(IEditorPreferenceConstants.EDITOR_LINEWRAP_LENGTH, 80);
	}
}
