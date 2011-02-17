/**
 * File:   ProjectAdvancedPropertiesPage.java
 * Author: Thomas Calmant
 * Date:   17 févr. 2011
 */
package org.isandlatech.plugins.rest.wizards;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

/**
 * Sphinx project advanced (optionnal) properties
 * 
 * @author Thomas Calmant
 */
public class ProjectAdvancedPropertiesPage extends AbstractWizardPage {

	/** Use parts instead of chapters in LaTex */
	private Button pLatexParts;

	/** Master document name (without suffix) */
	private Text pMasterDocumentName;

	/** Source encoding */
	private Text pSourceEncoding;

	/** Static and Template folders prefix */
	private Text pStaticFoldersPrefix;

	/**
	 * Sets up the wizard page
	 * 
	 * @param aPageName
	 *            The page name
	 */
	protected ProjectAdvancedPropertiesPage(final String aPageName) {
		super(aPageName);
		setTitle("Advanced Sphinx project properties");
		setDescription("Set the advanced project properties");
	}

	@Override
	protected void createFields() {
		pStaticFoldersPrefix = addTextField("Template and Static folders prefix :");
		pStaticFoldersPrefix.setText("_");

		pMasterDocumentName = addTextField("Master document name (without suffix) :");
		pMasterDocumentName.setText("index");

		pSourceEncoding = addTextField("Source encoding :");
		pSourceEncoding.setText("utf-8-sig");

		pLatexParts = addCheckBox("Use parts instead of chapters in LaTex");
		pLatexParts.setSelection(false);
	}

	/**
	 * @return the latexParts
	 */
	public Button getLatexParts() {
		return pLatexParts;
	}

	/**
	 * @return the master document name
	 */
	public Text getMasterDocumentName() {
		return pMasterDocumentName;
	}

	@Override
	protected int getNbColumns() {
		return 2;
	}

	/**
	 * @return the source encoding
	 */
	public Text getSourceEncoding() {
		return pSourceEncoding;
	}

	/**
	 * @return the static folders prefix
	 */
	public Text getStaticFoldersPrefix() {
		return pStaticFoldersPrefix;
	}
}
