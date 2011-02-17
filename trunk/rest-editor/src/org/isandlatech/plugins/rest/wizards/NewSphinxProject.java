/**
 * File:   NewSphinxProject.java
 * Author: Thomas Calmant
 * Date:   17 févr. 2011
 */
package org.isandlatech.plugins.rest.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * Sphinx project creation wizard
 * 
 * @author Thomas Calmant
 */
public class NewSphinxProject extends BasicNewProjectResourceWizard {

	/** Project main properties */
	private ProjectPropertiesPage pPropertiesPage;

	/** Advanced project properties */
	private ProjectAdvancedPropertiesPage pAdvancedPropertiesPage;

	/**
	 * Sets the window title
	 */
	public NewSphinxProject() {
		super();
		setWindowTitle("Sphinx Documentation Project");
	}

	@Override
	public void addPages() {
		super.addPages();

		IWizardPage startingPage = getStartingPage();
		startingPage.setTitle("Sphinx project");
		startingPage
				.setDescription("Creates a Sphinx documentation project from scratch");

		pPropertiesPage = new ProjectPropertiesPage("Project properties");
		pAdvancedPropertiesPage = new ProjectAdvancedPropertiesPage(
				"Advanced project properties");

		addPage(pPropertiesPage);
		addPage(pAdvancedPropertiesPage);
	}

	@Override
	public boolean canFinish() {
		return pPropertiesPage.canFlipToNextPage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {

		ConfigGenerator generator = new ConfigGenerator();

		// TODO retrieve it correctly
		String projectName = "test";

		generator.setBaseProjectInformations(projectName,
				pPropertiesPage.getAuthors(), pPropertiesPage.getVersion(),
				pPropertiesPage.getRelease(),
				pAdvancedPropertiesPage.getMasterDocumentName());

		generator.setLanguage(pPropertiesPage.getLanguage());

		System.out.println(generator.generateConfigurationContent());

		return super.performFinish();
	}
}
