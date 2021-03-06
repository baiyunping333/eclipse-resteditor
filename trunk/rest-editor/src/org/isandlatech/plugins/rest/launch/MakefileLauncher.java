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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.util.Util;
import org.isandlatech.plugins.rest.RestPlugin;

public class MakefileLauncher implements ILaunchConfigurationDelegate {

	/** Console name */
	public static final String CONSOLE_NAME = "Makefile output";

	/** Sphinx Makefile launcher ID */
	public static final String LAUNCHER_ID = "org.isandlatech.plugins.rest.launch.makefile";

	/** The output console */
	private OutputConsole pOutputConsole;

	/**
	 * Prepares the output console
	 */
	public MakefileLauncher() {
		pOutputConsole = new OutputConsole(CONSOLE_NAME);
	}

	/**
	 * Retrieves the expected configuration attribute. Returns
	 * {@link IMakefileConstants#UNDEFINED} on error.
	 * 
	 * @param aConfiguration
	 *            Configuration to use
	 * @param aConfigKey
	 *            Attribute name
	 * @param aDefaultValue
	 *            Value to be used if the attribute doesn't exist
	 * @return The attribute value or {@link IMakefileConstants#UNDEFINED}
	 */
	protected String getConfiguration(
			final ILaunchConfiguration aConfiguration, final String aConfigKey,
			final String aDefaultValue) {

		String value;
		try {
			value = aConfiguration.getAttribute(aConfigKey, aDefaultValue);
		} catch (CoreException e) {
			value = IMakefileConstants.UNDEFINED;
		}

		return value;
	}

	/**
	 * Retrieves the expected configuration attribute. Returns an empty on
	 * error.
	 * 
	 * @param aConfiguration
	 *            Configuration to use
	 * @param aConfigKey
	 *            Attribute name
	 * @return The attribute value or an empty set
	 */
	@SuppressWarnings("unchecked")
	protected Set<String> getConfigurationSet(
			final ILaunchConfiguration aConfiguration, final String aConfigKey) {

		Set<String> value;
		try {
			value = aConfiguration.getAttribute(aConfigKey,
					new HashSet<String>());
		} catch (CoreException e) {
			value = new HashSet<String>();
		}

		return value;
	}

	/**
	 * Expands and returns the working directory attribute of the given launch
	 * configuration. Returns <code>null</code> if a working directory is not
	 * specified. If specified, the working is verified to point to an existing
	 * directory in the local file system.
	 * 
	 * Copied from External tools plug-in internals.
	 * 
	 * @param aConfiguration
	 *            the launch configuration
	 * @return an absolute path to a directory in the local file system, or
	 *         <code>null</code> if unspecified
	 * @throws CoreException
	 *             if unable to retrieve the associated launch configuration
	 *             attribute, if unable to resolve any variables, or if the
	 *             resolved location does not point to an existing directory in
	 *             the local file system
	 */
	public File getWorkingDirectory(final ILaunchConfiguration aConfiguration)
			throws CoreException {

		String location = getConfiguration(aConfiguration,
				IMakefileConstants.ATTR_WORKING_DIRECTORY,
				IMakefileConstants.ATTR_DEFAULT_WORKING_DIRECTORY);

		if (location != null) {
			IStringVariableManager varMan = VariablesPlugin.getDefault()
					.getStringVariableManager();

			String expandedLocation = varMan
					.performStringSubstitution(location);

			if (expandedLocation.length() > 0) {
				File path = new File(expandedLocation);
				if (path.isDirectory()) {
					return path;
				}
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.
	 * eclipse.debug.core.ILaunchConfiguration, java.lang.String,
	 * org.eclipse.debug.core.ILaunch,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void launch(final ILaunchConfiguration aConfiguration,
			final String aMode, final ILaunch aLaunch,
			final IProgressMonitor aMonitor) throws CoreException {

		// Get working directory
		File workingDirectory = getWorkingDirectory(aConfiguration);
		if (workingDirectory == null) {
			return;
		}

		// Set the command line
		String[] cmdLine = makeCommandLine(aConfiguration);

		// Make the environment
		String[] envArray = makeEnvironment(aConfiguration);

		// Prepare a console
		pOutputConsole.activate();

		// Run !
		Process execProcess = DebugPlugin.exec(cmdLine, workingDirectory,
				envArray);

		IProcess streamProcess = DebugPlugin.newProcess(aLaunch, execProcess,
				cmdLine[0]);

		// Handle outputs
		pOutputConsole.listenStreams(streamProcess.getStreamsProxy());
	}

	/**
	 * Prepares the command line to execute.
	 * 
	 * @param aConfiguration
	 *            Launch configuration
	 * @return The command line in an array
	 */
	protected String[] makeCommandLine(final ILaunchConfiguration aConfiguration) {

		// Prepare the command line
		String makeCmd = getConfiguration(aConfiguration,
				IMakefileConstants.ATTR_MAKE_CMD,
				IMakefileConstants.ATTR_DEFAULT_MAKE_CMD);

		Set<String> makeRules = getConfigurationSet(aConfiguration,
				IMakefileConstants.ATTR_MAKE_RULES);

		// Make the command line
		ArrayList<String> cmdLine;

		if (Util.isWindows()) {
			// Handle Windows specific launch
			cmdLine = new ArrayList<String>(makeRules.size());

			// Executable : command line emulation
			cmdLine.add("cmd");

			// Execute a command
			cmdLine.add("/c");

			// The "makefile"
			cmdLine.add("make.bat");

		} else {
			// Unix like systems
			cmdLine = new ArrayList<String>(makeRules.size() + 1);
			cmdLine.add(makeCmd);
		}

		// The parameters
		cmdLine.addAll(makeRules);
		return cmdLine.toArray(new String[0]);
	}

	/**
	 * Retrieves the complete environment of the process to create.
	 * 
	 * @param aConfiguration
	 *            Launch configuration
	 * @return The complete process environment
	 */
	protected String[] makeEnvironment(final ILaunchConfiguration aConfiguration) {

		String[] env = null;

		try {
			env = DebugPlugin.getDefault().getLaunchManager()
					.getEnvironment(aConfiguration);

		} catch (CoreException e) {
			RestPlugin.logError("Error retrieving launch environment", e);
		}

		return env;
	}
}
