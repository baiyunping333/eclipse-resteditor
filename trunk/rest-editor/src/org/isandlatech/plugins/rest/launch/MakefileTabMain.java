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

package org.isandlatech.plugins.rest.launch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.isandlatech.plugins.rest.RestPlugin;
import org.isandlatech.plugins.rest.i18n.Messages;

/**
 * Sphinx build launcher main options
 * 
 * @author Thomas Calmant
 */
public class MakefileTabMain extends AbstractLaunchConfigurationTab {

	/**
	 * Tab elements modification listener
	 * 
	 * @author Thomas Calmant
	 */
	private class ModificationListener implements ModifyListener,
			SelectionListener {

		@Override
		public void modifyText(final ModifyEvent aEvent) {
			setDirty(true);
			updateLaunchConfigurationDialog();
		}

		@Override
		public void widgetDefaultSelected(final SelectionEvent aEvent) {
			setDirty(true);
			updateLaunchConfigurationDialog();
		}

		@Override
		public void widgetSelected(final SelectionEvent aEvent) {
			setDirty(true);
			updateLaunchConfigurationDialog();

			Object source = aEvent.getSource();

			if (source.equals(pWorkspaceLocationButton)) {
				// Workspace folder selection
				handleWorkspaceWorkingDirectoryButtonSelected();

			} else if (source.equals(pFileSystemLocationButton)) {
				// File system selection
				handleFileWorkingDirectoryButtonSelected();

			} else if (source.equals(pVariablesLocationButton)) {
				// Variables insertion
				handleVariablesButtonSelected();

			} else if (source.equals(pCustomRulesEnabled)) {
				// Enable/disable custom rules
				pCustomRules.setEnabled(pCustomRulesEnabled.getSelection());
			}
		}
	}

	/** Custom make rules */
	private Text pCustomRules;

	/** Custom rules enables */
	private Button pCustomRulesEnabled;

	/** File selection button */
	private Button pFileSystemLocationButton;

	/** Make command to use */
	private Text pMakeCommand;

	/** Output check boxes */
	private Map<String, Button> pMakeRules = new HashMap<String, Button>();

	/** Common modification listener */
	private ModificationListener pModificationListener = new ModificationListener();

	/** Variables selection button */
	private Button pVariablesLocationButton;

	/** Project to compile */
	private Text pWorkingDirectory;

	/** Workspace selection button */
	private Button pWorkspaceLocationButton;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	public void createControl(final Composite aParent) {

		// Create the root control
		Composite composite = new Composite(aParent, SWT.NONE);
		setGridLayout(composite, 1);

		setControl(composite);

		// Fill the root control
		createLocationComponent(composite);
		createControlsOutput(composite);
	}

	/**
	 * Creates the make configuration button group
	 * 
	 * @param aParent
	 *            Parent container
	 */
	private void createControlsOutput(final Composite aParent) {

		// Output group
		Group rulesGroup = new Group(aParent, SWT.SHADOW_ETCHED_IN);
		rulesGroup.setText(Messages.getString("runner.main.output.title"));
		setGridLayout(rulesGroup, 2);

		// Available outputs
		pMakeRules.clear();

		for (String rule : IMakefileConstants.SPHINX_MAKE_RULES) {
			Button checkBox = createCheckButton(rulesGroup, rule);
			checkBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			checkBox.setSelection(false);
			checkBox.addSelectionListener(pModificationListener);

			pMakeRules.put(rule, checkBox);
		}

		// Custom make rule
		pCustomRulesEnabled = createCheckButton(rulesGroup,
				Messages.getString("runner.main.output.custom"));
		pCustomRulesEnabled.setSelection(false);
		pCustomRulesEnabled.addSelectionListener(pModificationListener);

		pCustomRules = createText(rulesGroup);
		pCustomRules.setEnabled(false);
		pCustomRules.addModifyListener(pModificationListener);

		// Button group
		Group group = new Group(aParent, SWT.SHADOW_ETCHED_IN);
		group.setText(Messages.getString("runner.main.options.title"));
		setGridLayout(group, 2);

		// Make command
		createLabel(group, Messages.getString("runner.main.options.makecmd"));

		pMakeCommand = createText(group);
		pMakeCommand.addModifyListener(pModificationListener);
	}

	/**
	 * Creates a label with the given name (without style nor layout flag)
	 * 
	 * @param aParent
	 *            Label parent
	 * @param aText
	 *            Text on the label
	 * @return The created label
	 */
	public Label createLabel(final Composite aParent, final String aText) {

		Label label = new Label(aParent, SWT.NONE);
		label.setText(aText);

		return label;
	}

	/**
	 * Creates the controls needed to edit the location attribute of an external
	 * tool.
	 * 
	 * Code from external tools plugin internals.
	 * 
	 * @param parent
	 *            the composite to create the controls in
	 */
	protected void createLocationComponent(final Composite parent) {

		// "Location" group
		Group group = new Group(parent, SWT.NONE);
		String locationLabel = Messages.getString("runner.main.dir.title");
		group.setText(locationLabel);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayout(layout);
		group.setLayoutData(gridData);

		// Working directory text field
		pWorkingDirectory = new Text(group, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = IDialogConstants.ENTRY_FIELD_WIDTH;
		pWorkingDirectory.setLayoutData(gridData);
		pWorkingDirectory.addModifyListener(pModificationListener);

		// 3-buttons group
		Composite buttonComposite = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 3;
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(gridData);
		buttonComposite.setFont(parent.getFont());

		// Workspace folder selection
		pWorkspaceLocationButton = createPushButton(buttonComposite,
				Messages.getString("runner.main.dir.workspace"), null);
		pWorkspaceLocationButton.addSelectionListener(pModificationListener);

		// File system folder selection
		pFileSystemLocationButton = createPushButton(buttonComposite,
				Messages.getString("runner.main.dir.filesystem"), null);
		pFileSystemLocationButton.addSelectionListener(pModificationListener);

		// Variables injection
		pVariablesLocationButton = createPushButton(buttonComposite,
				Messages.getString("runner.main.dir.variables"), null);
		pVariablesLocationButton.addSelectionListener(pModificationListener);
	}

	/**
	 * Creates a bordered text field, with the {@link GridData#FILL_HORIZONTAL}
	 * layout flag
	 * 
	 * @param aParent
	 *            Text field parent
	 * @return The new text field
	 */
	public Text createText(final Composite aParent) {

		Text text = new Text(aParent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	@Override
	public String getName() {
		return Messages.getString("runner.main.title");
	}

	/**
	 * Prompts the user to choose a working directory from the file system.
	 */
	protected void handleFileWorkingDirectoryButtonSelected() {

		DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SAVE);
		dialog.setMessage(Messages.getString("runner.main.dir.dialog.label"));
		dialog.setFilterPath(pWorkingDirectory.getText());

		String text = dialog.open();
		if (text != null) {
			pWorkingDirectory.setText(text);
		}
	}

	/**
	 * A variable entry button has been pressed for the given text field. Prompt
	 * the user for a variable and enter the result in the project field.
	 */
	private void handleVariablesButtonSelected() {

		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(
				getShell());
		dialog.open();

		String variable = dialog.getVariableExpression();
		if (variable != null) {
			pWorkingDirectory.insert(variable);
		}
	}

	/**
	 * Prompts the user for a working directory location within the workspace
	 * and sets the working directory as a String containing the workspace_loc
	 * variable or <code>null</code> if no location was obtained from the user.
	 */
	protected void handleWorkspaceWorkingDirectoryButtonSelected() {

		ContainerSelectionDialog containerDialog;
		containerDialog = new ContainerSelectionDialog(getShell(),
				ResourcesPlugin.getWorkspace().getRoot(), false,
				Messages.getString("runner.main.dir.dialog.label"));
		containerDialog.open();

		Object[] resource = containerDialog.getResult();
		String text = null;
		if (resource != null && resource.length > 0) {
			text = newVariableExpression(
					"workspace_loc", ((IPath) resource[0]).toString()); //$NON-NLS-1$
		}

		if (text != null) {
			pWorkingDirectory.setText(text);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse
	 * .debug.core.ILaunchConfiguration)
	 */
	@Override
	public void initializeFrom(final ILaunchConfiguration aConfiguration) {

		// Reset check boxes
		unselectAllRules();

		// Make command
		setConfiguredText(aConfiguration, IMakefileConstants.ATTR_MAKE_CMD,
				IMakefileConstants.ATTR_DEFAULT_MAKE_CMD, pMakeCommand);

		// Project name
		setConfiguredText(aConfiguration,
				IMakefileConstants.ATTR_WORKING_DIRECTORY,
				IMakefileConstants.ATTR_DEFAULT_WORKING_DIRECTORY,
				pWorkingDirectory);

		// Make rules
		try {

			@SuppressWarnings("unchecked")
			Set<String> selectedRules = aConfiguration.getAttribute(
					IMakefileConstants.ATTR_MAKE_RULES, new HashSet<String>());

			for (String rule : selectedRules) {

				Button btn = pMakeRules.get(rule);
				if (btn != null) {
					btn.setSelection(true);

				} else {
					// Add rule to custom ones if unknown
					if (!pCustomRules.getText().isEmpty()) {
						pCustomRules.append(" ");
					}

					pCustomRules.append(rule);
				}
			}

			if (aConfiguration.getAttribute(
					IMakefileConstants.ATTR_CUSTOM_RULES_ENABLED, false)) {
				pCustomRulesEnabled.setSelection(true);
				pCustomRules.setEnabled(true);

			} else {
				pCustomRulesEnabled.setSelection(false);
				pCustomRules.setEnabled(false);
			}

		} catch (CoreException e) {
			RestPlugin.logError("Error preparing make rules selection", e);
		}
	}

	/**
	 * Returns a new variable expression with the given variable and the given
	 * argument.
	 * 
	 * @see IStringVariableManager#generateVariableExpression(String, String)
	 */
	protected String newVariableExpression(final String varName,
			final String arg) {
		return VariablesPlugin.getDefault().getStringVariableManager()
				.generateVariableExpression(varName, arg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse
	 * .debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void performApply(
			final ILaunchConfigurationWorkingCopy aConfiguration) {

		aConfiguration.setAttribute(IMakefileConstants.ATTR_MAKE_CMD,
				pMakeCommand.getText().trim());

		aConfiguration.setAttribute(IMakefileConstants.ATTR_WORKING_DIRECTORY,
				pWorkingDirectory.getText().trim());

		Set<String> selectedRules = new HashSet<String>();
		for (Entry<String, Button> entry : pMakeRules.entrySet()) {

			if (entry.getValue().getSelection()) {
				selectedRules.add(entry.getKey());
			}
		}

		if (pCustomRulesEnabled.getSelection()) {
			selectedRules.add(pCustomRules.getText());
		}

		aConfiguration.setAttribute(
				IMakefileConstants.ATTR_CUSTOM_RULES_ENABLED,
				pCustomRulesEnabled.getSelection());

		aConfiguration.setAttribute(IMakefileConstants.ATTR_MAKE_RULES,
				selectedRules);
	}

	/**
	 * Opens the project selection window and updates the corresponding text
	 * field if needed
	 */
	protected void selectProject() {

		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), null, false,
				Messages.getString("runner.main.project.select.message"));

		dialog.setBlockOnOpen(true);
		if (dialog.open() == StringVariableSelectionDialog.OK) {

			IPath newPath = (IPath) dialog.getResult()[0];
			if (newPath == null) {
				return;
			}

			String newPathString = newPath.makeRelative().toOSString();
			if (!newPathString.equals(pWorkingDirectory.getText())) {
				pWorkingDirectory.setText(newPathString);
				setDirty(true);
			}
		}
	}

	/**
	 * Sets the text field content using the given configuration or the default
	 * value. If the default value is null, {@link IMakefileConstants#UNDEFINED}
	 * is used.
	 * 
	 * @param aConfiguration
	 *            Launch configuration to be used
	 * @param aConfigurationKey
	 *            Text field key in the launch configuration
	 * @param aDefaultValue
	 *            Value to be used if the text field key is not present
	 * @param aTextField
	 *            Target text field
	 */
	private void setConfiguredText(final ILaunchConfiguration aConfiguration,
			final String aConfigurationKey, final String aDefaultValue,
			final Text aTextField) {

		String value;
		try {
			value = aConfiguration.getAttribute(aConfigurationKey,
					String.valueOf(aDefaultValue));

		} catch (CoreException e) {
			value = IMakefileConstants.UNDEFINED;
		}

		aTextField.setText(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.
	 * debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults(final ILaunchConfigurationWorkingCopy aConfiguration) {

		aConfiguration.setAttribute(IMakefileConstants.ATTR_MAKE_CMD,
				IMakefileConstants.ATTR_DEFAULT_MAKE_CMD);
		aConfiguration.setAttribute(IMakefileConstants.ATTR_WORKING_DIRECTORY,
				IMakefileConstants.ATTR_DEFAULT_WORKING_DIRECTORY);

		aConfiguration.setAttribute(IMakefileConstants.ATTR_MAKE_RULES,
				new HashSet<String>());

		aConfiguration.setAttribute(
				IMakefileConstants.ATTR_CUSTOM_RULES_ENABLED, false);
	}

	/**
	 * Applies a new grid layout to the given control
	 * 
	 * @param aControl
	 *            Control to use
	 * @param aNumColumns
	 *            Number of columns in the grid layouts
	 */
	protected void setGridLayout(final Composite aControl, final int aNumColumns) {

		GridLayout layout = new GridLayout();
		layout.numColumns = aNumColumns;

		aControl.setLayout(layout);
		aControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	/**
	 * Un-selects all check boxes
	 */
	private void unselectAllRules() {

		for (Button btn : pMakeRules.values()) {
			btn.setSelection(false);
		}

		pCustomRulesEnabled.setSelection(false);
		pCustomRules.setEnabled(false);
	}
}
