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

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.isandlatech.plugins.rest.RestPlugin;

/**
 * Utility class for Sphinx build shortcuts.
 * 
 * @author Thomas Calmant
 */
public abstract class AbstractShinxShortcut implements ILaunchShortcut {

	/** Launch configuration Run mode */
	public static final String RUN_MODE = "run";

	/**
	 * Retrieves the resource associated to the given editor
	 * 
	 * @param aEditorPart
	 *            The editor displaying the resource
	 * @return The displayed resource if it's a file
	 */
	protected IResource extractResource(final IEditorPart aEditorPart) {

		IEditorInput input = aEditorPart.getEditorInput();

		// Not a file
		if (!(input instanceof IFileEditorInput)) {
			return null;
		}

		return ((IFileEditorInput) input).getFile();
	}

	/**
	 * Retrieves the first resource associated to the given selection
	 * 
	 * @param aSelection
	 *            The active selection
	 * @return The first selected resource if any
	 */
	protected IResource extractResource(final ISelection aSelection) {

		if (!(aSelection instanceof IStructuredSelection)) {
			return null;
		}

		Object element = ((IStructuredSelection) aSelection).getFirstElement();

		// Return immediately if we have a resource
		if (element instanceof IResource) {
			return (IResource) element;
		}

		// Try to adapt the selection to a resource if possible
		if (!(element instanceof IAdaptable)) {
			return null;
		}

		return (IResource) ((IAdaptable) element).getAdapter(IResource.class);
	}

	/**
	 * Retrieves the shortcut launch configuration name
	 * 
	 * @param aProject
	 *            The current project
	 * @return the launch configuration name
	 */
	protected abstract String getConfigName(IProject aProject);

	/**
	 * Retrieves the makefile target rules. Aborts the launch if returns null.
	 * 
	 * @return The makefile target rules
	 */
	protected abstract Set<String> getMakeRules();

	/**
	 * Tests if the given project has a Makefile on Unix or a make.bat file on
	 * Windows in its root directory.
	 * 
	 * @param aProject
	 *            Project to be tested
	 * @return True if a Make file is present
	 */
	protected boolean hasMakefile(final IProject aProject) {

		String makeFile = "/Makefile";

		final String osName = System.getProperty("os.name").toLowerCase();

		// Do not use make on windows
		if (osName.startsWith("win")) {
			makeFile = "/make.bat";
		}

		if (aProject.getFile(makeFile).exists()) {
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.ui.IEditorPart,
	 * java.lang.String)
	 */
	@Override
	public void launch(final IEditorPart aEditor, final String aMode) {

		IResource selectedResource = extractResource(aEditor);
		if (selectedResource != null) {
			try {
				launchSphinx(selectedResource.getProject());
			} catch (CoreException e) {
				RestPlugin.logError("Error preparing Sphinx launch", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.jface.viewers
	 * .ISelection, java.lang.String)
	 */
	@Override
	public void launch(final ISelection aSelection, final String aMode) {

		IResource selectedResource = extractResource(aSelection);
		if (selectedResource != null) {
			try {
				launchSphinx(selectedResource.getProject());
			} catch (CoreException e) {
				RestPlugin.logError("Error preparing Sphinx launch", e);
			}
		}
	}

	/**
	 * Launch a Sphinx build on the given project.
	 * 
	 * @param aProject
	 *            Current project
	 */
	protected void launchSphinx(final IProject aProject) throws CoreException {

		if (!hasMakefile(aProject)) {
			RestPlugin.logError("Project " + aProject.getName()
					+ " has no Makefile", null);
			return;
		}

		// Find the Sphinx Makefile launch configuration type
		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		ILaunchConfigurationType configType = launchManager
				.getLaunchConfigurationType(MakefileLauncher.LAUNCHER_ID);

		// Prepare the launch configuration
		final String configurationName = getConfigName(aProject);

		ILaunchConfigurationWorkingCopy launchConfiguration = configType
				.newInstance(aProject, configurationName);

		// Let the sub-class edit it
		if (!prepareConfiguration(aProject, launchConfiguration)) {
			RestPlugin.logError("Error while preparing the run configuration",
					null);
			return;
		}

		// Prepare the launch...
		ILaunch launch = new Launch(launchConfiguration, RUN_MODE, null);

		// Run !
		MakefileLauncher launcher = new MakefileLauncher();
		launchConfiguration.doSave();
		launcher.launch(launchConfiguration, RUN_MODE, launch, null);
		launchConfiguration.delete();
	}

	/**
	 * Prepares the attributes of the launch configuration
	 * 
	 * @param aProject
	 *            The current project
	 * @param aLaunchConfiguration
	 *            Launch configuration that will be used
	 * @return True to continue, False to abort the launch
	 */
	protected boolean prepareConfiguration(final IProject aProject,
			final ILaunchConfigurationWorkingCopy aLaunchConfiguration) {

		final Set<String> makeRule = getMakeRules();
		if (makeRule == null || makeRule.isEmpty()) {
			return false;
		}

		// Set the make rule
		aLaunchConfiguration.setAttribute(IMakefileConstants.ATTR_MAKE_RULES,
				makeRule);

		// Set the working directory
		aLaunchConfiguration.setAttribute(
				IMakefileConstants.ATTR_WORKING_DIRECTORY, aProject
						.getLocation().toOSString());

		return true;
	}
}
