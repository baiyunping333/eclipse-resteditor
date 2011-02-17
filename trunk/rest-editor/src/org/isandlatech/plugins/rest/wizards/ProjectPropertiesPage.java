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

	public static final String[] HTML_STYLES = { "", "default", "sphinxdoc",
			"scrolls", "agogo", "traditional", "nature", "haiku" };

	/** Possible languages */
	public static final String[] LANGUAGES = { "", "en", "fr", "bn", "ca",
			"cs", "da", "de", "es", "fi", "hr", "it", "ja", "lt", "nl", "pl",
			"pt_BR", "ru", "sl", "tr", "uk_UA", "zh_CN", "zh_TW" };

	/** Project authors */
	private Text pAuthors;

	/** Generated documents language */
	private Combo pLanguage;

	/** Project sub-version */
	private Text pRelease;

	/** Generated documents style */
	private Combo pStyle;

	/** Project version */
	private Text pVersion;

	/** Separate source and build */
	private Button pSeparateSourceBuild;

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
		pVersion = addTextField("Version :");
		pRelease = addTextField("Release :");

		pLanguage = addComboBox("Language", LANGUAGES);
		pLanguage.select(1);

		pStyle = addComboBox("HTML Theme :", HTML_STYLES);
		pStyle.select(1);

		pSeparateSourceBuild = addCheckBox("Separate source and build folders");
		pSeparateSourceBuild.setSelection(true);
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
		return pRelease.getText();
	}

	/**
	 * @return the HTML style
	 */
	public String getStyle() {
		return pStyle.getText();
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return pVersion.getText();
	}

	/**
	 * @return True if source and build folders must be separated
	 */
	public boolean isSourceBuildSeparated() {
		return pSeparateSourceBuild.getSelection();
	}
}
