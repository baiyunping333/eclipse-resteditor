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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.spelling.SpellingEngineDescriptor;
import org.eclipse.ui.texteditor.spelling.SpellingService;
import org.isandlatech.plugins.rest.RestPlugin;
import org.isandlatech.plugins.rest.editor.linewrap.LineWrapUtil;
import org.isandlatech.plugins.rest.editor.providers.IThemeConstants;
import org.isandlatech.plugins.rest.i18n.Messages;
import org.osgi.service.prefs.BackingStoreException;

/**
 * ReST Editor preferences page
 * 
 * @author Thomas Calmant
 */
public class EditorPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/** Formatting on file save activation */
	private BooleanFieldEditor pFormatOnSave;

	/** Maximum line length before wrapping */
	private IntegerFieldEditor pLineWrapLength;

	/** Active line wrap mode */
	private ComboFieldEditor pLineWrapMode;

	/** Reset section markers on save */
	private BooleanFieldEditor pResetMarkersOnsave;

	/** Preferred section markers */
	private StringFieldEditor pSectionMarkers;

	/** Spelling service activation */
	private BooleanFieldEditor pSpellingServiceEnabledField;

	/** Spelling service ID */
	private ComboFieldEditor pSpellingServiceIdField;

	/** Tabs length field */
	private IntegerFieldEditor pTabsLengthField;

	/** Tabs to space activation field */
	private BooleanFieldEditor pTabsToSpaceField;

	/** Right trim text on save */
	private BooleanFieldEditor pTrimOnSave;

	/**
	 * Prepares the preference store
	 */
	public EditorPreferencePage() {
		super(GRID);
		setPreferenceStore(RestPlugin.getDefault().getPreferenceStore());
		setDescription("General setting for the ReST Editor");
	}

	/**
	 * Creates the page fields.
	 */
	@Override
	public void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		/* Save actions */
		pFormatOnSave = new BooleanFieldEditor(
				IEditorPreferenceConstants.EDITOR_SAVE_FORMAT,
				Messages.getString("preferences.save.format"), parent);
		addField(pFormatOnSave);

		pTrimOnSave = new BooleanFieldEditor(
				IEditorPreferenceConstants.EDITOR_SAVE_TRIM,
				Messages.getString("preferences.save.trim"), parent);
		addField(pTrimOnSave);

		/* Section markers */
		pSectionMarkers = new StringFieldEditor(
				IEditorPreferenceConstants.EDITOR_SECTION_MARKERS,
				Messages.getString("preferences.save.markers.preferred"),
				parent);
		addField(pSectionMarkers);

		pResetMarkersOnsave = new BooleanFieldEditor(
				IEditorPreferenceConstants.EDITOR_SAVE_RESET_MARKERS,
				Messages.getString("preferences.save.markers.reset"), parent);
		addField(pResetMarkersOnsave);

		/* Tabulations */
		pTabsLengthField = new IntegerFieldEditor(
				IEditorPreferenceConstants.EDITOR_TABS_LENGTH,
				Messages.getString("preferences.tab.len"), parent);
		pTabsLengthField.setValidRange(1, 16);
		addField(pTabsLengthField);

		pTabsToSpaceField = new BooleanFieldEditor(
				IEditorPreferenceConstants.EDITOR_TABS_TO_SPACES,
				Messages.getString("preferences.tab.tospace"), parent);
		addField(pTabsToSpaceField);

		/* Line wrap */
		pLineWrapMode = new ComboFieldEditor(
				IEditorPreferenceConstants.EDITOR_LINEWRAP_MODE,
				Messages.getString("preferences.wrap.mode"),
				prepareLineWrapEntries(), parent);
		addField(pLineWrapMode);

		pLineWrapLength = new IntegerFieldEditor(
				IEditorPreferenceConstants.EDITOR_LINEWRAP_LENGTH,
				Messages.getString("preferences.wrap.length"), parent);
		addField(pLineWrapLength);

		/* Separator */
		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);

		/* Spell engine */
		pSpellingServiceEnabledField = new BooleanFieldEditor(
				SpellingService.PREFERENCE_SPELLING_ENABLED,
				Messages.getString("preferences.spell.activate"), parent);
		addField(pSpellingServiceEnabledField);

		String[][] descriptorsNames = listSpellingEngines();
		pSpellingServiceIdField = new ComboFieldEditor(
				SpellingService.PREFERENCE_SPELLING_ENGINE,
				Messages.getString("preferences.spell.service"),
				descriptorsNames, parent);
		addField(pSpellingServiceIdField);

		/* Reset colors preferences button (not a field) */
		Button btn = new Button(parent, SWT.PUSH);
		btn.setText(Messages.getString("preferences.colors.reset"));

		btn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent aEvent) {
				// Do nothing
			}

			@Override
			public void widgetSelected(final SelectionEvent aEvent) {
				resetColors();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return RestPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {
		// Do nothing
	}

	/**
	 * Tests if the IDE is running with the ReST Editor debug mode parameter
	 * 
	 * @return True if debug options must be enabled
	 */
	public boolean isInDebugMode() {
		return System.getProperties().keySet()
				.contains(IEditorPreferenceConstants.DEBUG_MODE);
	}

	/**
	 * Lists all available spelling engines for a combo box output with the
	 * spell engine name as label and its ID as value.
	 * 
	 * @return A two dimension array : first dimension is the engine label,
	 *         second dimension is its ID.
	 */
	private String[][] listSpellingEngines() {

		SpellingEngineDescriptor[] descriptors = EditorsUI.getSpellingService()
				.getSpellingEngineDescriptors();
		String descriptorsNames[][] = new String[descriptors.length][2];

		int i = 0;
		for (SpellingEngineDescriptor descr : descriptors) {
			descriptorsNames[i][0] = descr.getLabel();
			descriptorsNames[i][1] = descr.getId();
			i++;
		}
		return descriptorsNames;
	}

	/**
	 * Creates a two-dimensional array to set the content of the line wrapping
	 * mode combo box.
	 * 
	 * @return The array to fill the combo box
	 */
	private String[][] prepareLineWrapEntries() {

		LineWrapUtil.LineWrapMode[] wrapModes = LineWrapUtil.LineWrapMode
				.values();
		String[][] comboEntries = new String[wrapModes.length][2];

		int i = 0;
		for (LineWrapUtil.LineWrapMode mode : wrapModes) {
			comboEntries[i][0] = Messages.getString("preferences.wrap.mode."
					+ mode.toString());
			comboEntries[i][1] = mode.toString();

			i++;
		}

		return comboEntries;
	}

	/**
	 * Resets the color definitions in the preference store, stored by Eclipse
	 * Color Theme
	 */
	public void resetColors() {

		IEclipsePreferences preferences = new InstanceScope()
				.getNode(RestPlugin.PLUGIN_ID);

		for (String themeKey : IThemeConstants.THEME_KEYS) {
			preferences.remove(themeKey);
		}

		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			RestPlugin.logError("Error saving color preferences", e);
		}
	}
}
