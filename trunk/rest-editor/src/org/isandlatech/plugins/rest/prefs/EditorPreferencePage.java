package org.isandlatech.plugins.rest.prefs;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.isandlatech.plugins.rest.RestPlugin;

/**
 * ReST Editor preferences page
 * 
 * @author Thomas Calmant
 */
public class EditorPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/** Formatting on file save activation */
	private BooleanFieldEditor pFormatOnSave;

	/** Python path selection field */
	private DirectoryFieldEditor pPythonPathField;

	/** Tabs length field */
	private IntegerFieldEditor pTabsLengthField;

	/** Tabs to space activation field */
	private BooleanFieldEditor pTabsToSpaceField;

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

		pFormatOnSave = new BooleanFieldEditor(
				IEditorPreferenceConstants.EDITOR_SAVE_FORMAT, "&Format on save",
				parent);

		pPythonPathField = new DirectoryFieldEditor(
				IEditorPreferenceConstants.PYTHON_PATH, "&Python interpreter", parent);
		pPythonPathField.setEmptyStringAllowed(true);

		pTabsLengthField = new IntegerFieldEditor(
				IEditorPreferenceConstants.EDITOR_TABS_LENGTH, "Tab &length", parent);
		pTabsLengthField.setValidRange(1, 16);

		pTabsToSpaceField = new BooleanFieldEditor(
				IEditorPreferenceConstants.EDITOR_TABS_TO_SPACES,
				"Insert &spaces instead of tabs", parent);

		addField(pPythonPathField);
		addField(pTabsLengthField);
		addField(pTabsToSpaceField);
		addField(pFormatOnSave);
	}

	@Override
	public void init(final IWorkbench workbench) {
	}
}
