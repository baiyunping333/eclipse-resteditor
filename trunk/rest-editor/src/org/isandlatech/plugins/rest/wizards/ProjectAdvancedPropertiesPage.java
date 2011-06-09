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

package org.isandlatech.plugins.rest.wizards;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.isandlatech.plugins.rest.i18n.Messages;

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
		setTitle(Messages.getString("wizard.newproject.pages.advanced.title"));
		setDescription(Messages
				.getString("wizard.newproject.pages.advanced.description"));
	}

	@Override
	protected void createFields() {
		pStaticFoldersPrefix = addTextField(Messages
				.getString("wizard.newproject.pages.advanced.staticfolders"));
		pStaticFoldersPrefix.setText("_");

		pMasterDocumentName = addTextField(Messages
				.getString("wizard.newproject.pages.advanced.master"));
		pMasterDocumentName.setText("index");

		pSourceEncoding = addTextField(Messages
				.getString("wizard.newproject.pages.advanced.encoding"));
		pSourceEncoding.setText("utf-8-sig");

		pLatexPaperSize = addTextField(Messages
				.getString("wizard.newproject.pages.advanced.papersize"));
		pLatexPaperSize.setText("a4");

		pLatexPageFontSize = addTextField(Messages
				.getString("wizard.newproject.pages.advanced.fontsize"));
		pLatexPageFontSize.setText("10pt");

		pLatexParts = addCheckBox(Messages
				.getString("wizard.newproject.pages.advanced.latexparts"));
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
