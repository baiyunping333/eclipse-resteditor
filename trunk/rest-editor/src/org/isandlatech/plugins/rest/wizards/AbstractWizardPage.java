/**
 * File:   AbstractWizardPage.java
 * Author: Thomas Calmant
 * Date:   17 févr. 2011
 */
package org.isandlatech.plugins.rest.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Thomas Calmant
 * 
 */
public abstract class AbstractWizardPage extends WizardPage {

	/** Parent of all fields */
	private Composite pPageRoot;

	/** Modification listener */
	private ModifyListener pModifyListener;

	/**
	 * Sets up the wizard page
	 * 
	 * @param aPageName
	 *            The page name
	 */
	protected AbstractWizardPage(final String aPageName) {
		super(aPageName);
		setTitle(aPageName);
		setDescription(aPageName);

		pModifyListener = new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent aEvent) {
				getWizard().getContainer().updateButtons();
			}
		};
	}

	protected Button addCheckBox(final Composite aParent, final String aLabel) {
		Button button = new Button(aParent, SWT.BORDER | SWT.CHECK);
		button.setText(aLabel);
		return button;
	}

	protected Button addCheckBox(final String aLabel) {
		return addCheckBox(pPageRoot, aLabel);
	}

	/**
	 * Adds a combo box to the given parent
	 * 
	 * @param aParent
	 *            Parent of the combo
	 * @param aLabel
	 *            Associated label
	 * @param aItems
	 *            Combo content
	 * @return The combo box
	 */
	protected Combo addComboBox(final Composite aParent, final String aLabel,
			final String[] aItems) {
		addLabel(aParent, aLabel);

		Combo combo = new Combo(aParent, SWT.BORDER);
		combo.setItems(aItems);
		combo.addModifyListener(pModifyListener);
		return combo;
	}

	/**
	 * Adds a combo box to the given parent
	 * 
	 * @param aLabel
	 *            Associated label
	 * @param aItems
	 *            Combo content
	 * @return The combo box
	 */
	protected Combo addComboBox(final String aLabel, final String[] aItems) {
		return addComboBox(pPageRoot, aLabel, aItems);
	}

	/**
	 * Adds a label to the given parent
	 * 
	 * @param aParent
	 *            Parent of the label
	 * @param aLabel
	 *            Text of the label
	 * @return The label
	 */
	private Label addLabel(final Composite aParent, final String aLabel) {
		Label label = new Label(aParent, SWT.NONE);
		label.setText(aLabel);

		return label;
	}

	/**
	 * Adds a text field with a label to the page
	 * 
	 * @param aParent
	 *            The field parent
	 * @param aLabel
	 *            The label corresponding to the field
	 * @return The text field
	 */
	protected Text addTextField(final Composite aParent, final String aLabel) {
		addLabel(aParent, aLabel);

		Text text = new Text(aParent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(pModifyListener);
		return text;
	}

	/**
	 * Adds a text field with a label to the page
	 * 
	 * @param aLabel
	 *            Text field label
	 * @return The text field
	 */
	protected Text addTextField(final String aLabel) {
		return addTextField(pPageRoot, aLabel);
	}

	/**
	 * Sets up the page content
	 */
	@Override
	public void createControl(final Composite aParent) {
		pPageRoot = new Composite(aParent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = getNbColumns();
		pPageRoot.setLayout(layout);

		createFields();

		setControl(pPageRoot);
	}

	/**
	 * Adds the fields to page
	 */
	protected abstract void createFields();

	/**
	 * Retrieves the number of columns in the grid layout
	 * 
	 * @return the number of columns in the grid layout
	 */
	protected abstract int getNbColumns();

	/**
	 * Retrieves the root field container
	 * 
	 * @return The root container
	 */
	protected Composite getPageRoot() {
		return pPageRoot;
	}
}
