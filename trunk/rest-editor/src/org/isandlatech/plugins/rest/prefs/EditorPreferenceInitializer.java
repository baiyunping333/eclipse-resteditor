package org.isandlatech.plugins.rest.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.isandlatech.plugins.rest.RestPlugin;

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

	}
}
