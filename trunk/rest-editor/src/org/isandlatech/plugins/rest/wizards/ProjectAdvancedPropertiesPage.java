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

	/** LaTex font size (10pt, ...) */
	private Text pLatexPageFontSize;

	/** LaTex output page format (a4, ...) */
	private Text pLatexPaperSize;

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

		pLatexPaperSize = addTextField("LaTex page format :");
		pLatexPaperSize.setText("a4");

		pLatexPageFontSize = addTextField("LaTex font size :");
		pLatexPageFontSize.setText("10pt");

		pLatexParts = addCheckBox("Use parts instead of chapters in LaTex");
		pLatexParts.setSelection(false);
	}

	/**
	 * @return the latex font size
	 */
	public String getLatexPageFontSize() {
		return pLatexPageFontSize.getText();
	}

	/**
	 * @return the latex paper size
	 */
	public String getLatexPaperSize() {
		return pLatexPaperSize.getText();
	}

	/**
	 * @return the latexParts
	 */
	public boolean getLatexParts() {
		return pLatexParts.getSelection();
	}

	/**
	 * @return the master document name
	 */
	public String getMasterDocumentName() {
		return pMasterDocumentName.getText();
	}

	@Override
	protected int getNbColumns() {
		return 2;
	}

	/**
	 * @return the source encoding
	 */
	public String getSourceEncoding() {
		return pSourceEncoding.getText();
	}

	/**
	 * @return the static folders prefix
	 */
	public String getStaticFoldersPrefix() {
		return pStaticFoldersPrefix.getText();
	}
}
