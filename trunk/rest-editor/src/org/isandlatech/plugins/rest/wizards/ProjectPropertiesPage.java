/**
 * File:   ProjectPropertiesPage.java
 * Author: Thomas Calmant
 * Date:   17 févr. 2011
 */
package org.isandlatech.plugins.rest.wizards;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

/**
 * Sphinx project properties
 * 
 * @author Thomas Calmant
 */
public class ProjectPropertiesPage extends AbstractWizardPage {

	/** Project authors */
	private Text pAuthors;

	/** Activate "todo" extension */
	private Button pExtensionTodo;

	/** Generated documents language */
	private Combo pLanguage;

	/** Project sub-version */
	private Text pRelease;

	/** Separate source and build */
	private Button pSeparateSourceBuild;

	/** Generated HTML documents theme */
	private Combo pTheme;

	/** Project version */
	private Text pVersion;

	/**
	 * Sets up the wizard page
	 * 
	 * @param aPageName
	 *            Page name
	 */
	protected ProjectPropertiesPage(final String aPageName) {
		super(aPageName);
		setTitle("Sphinx project properties");
		setDescription("Mandatory project properties");
	}

	@Override
	public boolean canFlipToNextPage() {
		return !getAuthors().isEmpty() && !getVersion().isEmpty();
	}

	@Override
	protected void createFields() {
		pAuthors = addTextField("Authors :");
		pVersion = addTextField("Version (X.Y) :");
		pRelease = addTextField("Release (X.Y.z ...) :");

		// TODO use preferences to select the default one
		pLanguage = addComboBox("Language", IConfigConstants.LANGUAGES);
		pLanguage.select(1);

		// TODO use preferences to select the default one
		pTheme = addComboBox("HTML Theme :", IConfigConstants.HTML_THEMES);
		pTheme.select(1);

		pSeparateSourceBuild = addCheckBox("Separate source and build folders");
		pSeparateSourceBuild.setSelection(true);

		pExtensionTodo = addCheckBox("Activate the todo:: directive extension");
		pExtensionTodo.setSelection(true);
	}

	/**
	 * @return the authors
	 */
	public String getAuthors() {
		return pAuthors.getText();
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return pLanguage.getText();
	}

	@Override
	protected int getNbColumns() {
		return 2;
	}

	/**
	 * @return the version release
	 */
	public String getRelease() {
		String release = pRelease.getText().trim();

		if (release.isEmpty()) {
			release = getVersion();
		}

		return release;
	}

	/**
	 * @return the HTML theme
	 */
	public String getTheme() {
		return pTheme.getText();
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return pVersion.getText().trim();
	}

	/**
	 * @return True if the "todo::" directive extension is activated
	 */
	public boolean isExtensionTodoActivated() {
		return pExtensionTodo.getSelection();
	}

	/**
	 * @return True if source and build folders must be separated
	 */
	public boolean isSourceBuildSeparated() {
		return pSeparateSourceBuild.getSelection();
	}
}
