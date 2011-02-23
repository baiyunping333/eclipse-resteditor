package org.isandlatech.plugins.rest.prefs;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.spelling.SpellingEngineDescriptor;
import org.eclipse.ui.texteditor.spelling.SpellingService;
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

	/** Spelling service activation */
	private BooleanFieldEditor pSpellingServiceEnabledField;

	/** Spelling service ID */
	private ComboFieldEditor pSpellingServiceIdField;

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

		/* Save actions */
		pFormatOnSave = new BooleanFieldEditor(
				IEditorPreferenceConstants.EDITOR_SAVE_FORMAT,
				"&Format on save", parent);

		/* Python */
		pPythonPathField = new DirectoryFieldEditor(
				IEditorPreferenceConstants.PYTHON_PATH, "&Python interpreter",
				parent);
		pPythonPathField.setEmptyStringAllowed(true);

		pTabsLengthField = new IntegerFieldEditor(
				IEditorPreferenceConstants.EDITOR_TABS_LENGTH, "Tab &length",
				parent);
		pTabsLengthField.setValidRange(1, 16);

		pTabsToSpaceField = new BooleanFieldEditor(
				IEditorPreferenceConstants.EDITOR_TABS_TO_SPACES,
				"Insert &spaces instead of tabs", parent);

		/* Spell engine */
		pSpellingServiceEnabledField = new BooleanFieldEditor(
				SpellingService.PREFERENCE_SPELLING_ENABLED,
				"&Activate spell checking", parent);

		String[][] descriptorsNames = listSpellingEngines();
		pSpellingServiceIdField = new ComboFieldEditor(
				SpellingService.PREFERENCE_SPELLING_ENGINE,
				"&Spelling service :", descriptorsNames, parent);

		addField(pPythonPathField);
		addField(pTabsLengthField);
		addField(pTabsToSpaceField);
		addField(pFormatOnSave);

		addField(pSpellingServiceEnabledField);
		addField(pSpellingServiceIdField);
	}

	@Override
	public void init(final IWorkbench workbench) {
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
			descriptorsNames[i][1] = descr.getLabel();
			i++;
		}
		return descriptorsNames;
	}
}
