/**
 * File:   MakefileTabMain.java
 * Author: Thomas Calmant
 * Date:   6 mai 2011
 */
package org.isandlatech.plugins.rest.launch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
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
		}
	}

	/** Make command to use */
	private Text pMakeCommand;

	/** Output check boxes */
	private Map<String, Button> pMakeRules = new HashMap<String, Button>();

	/** Common modification listener */
	private ModificationListener pModificationListener = new ModificationListener();

	/** Project to compile */
	private Text pTargetProject;

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
		createControlsProject(composite);
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
	 * Creates the project selection button group
	 * 
	 * @param aParent
	 *            Parent container
	 */
	private void createControlsProject(final Composite aParent) {

		// Button group
		Group group = new Group(aParent, SWT.SHADOW_ETCHED_IN);
		group.setText(Messages.getString("runner.main.project.title"));
		setGridLayout(group, 3);

		// Project selection
		createLabel(group,
				Messages.getString("runner.main.project.select.label"));

		pTargetProject = createText(group);
		pTargetProject.addModifyListener(pModificationListener);

		Button selectProject = createPushButton(group,
				Messages.getString("runner.main.project.select.button"), null);

		selectProject.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent aEvent) {
				// Never called (written in the doc)
			}

			@Override
			public void widgetSelected(final SelectionEvent aEvent) {
				selectProject();
			}
		});
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
				pTargetProject);

		// Make rules
		try {

			@SuppressWarnings("unchecked")
			Set<String> selectedRules = aConfiguration.getAttribute(
					IMakefileConstants.ATTR_MAKE_RULES, new HashSet<String>());

			for (String rule : selectedRules) {

				Button btn = pMakeRules.get(rule);
				if (btn != null) {
					btn.setSelection(true);
				}
			}

		} catch (CoreException e) {
			e.printStackTrace();
		}
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
				pTargetProject.getText().trim());

		Set<String> selectedRules = new HashSet<String>();
		for (Entry<String, Button> entry : pMakeRules.entrySet()) {

			if (entry.getValue().getSelection()) {
				selectedRules.add(entry.getKey());
			}
		}
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
			if (!newPathString.equals(pTargetProject.getText())) {
				pTargetProject.setText(newPathString);
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
	}
}
