/**
 * File:   MakefileLauncher.java
 * Author: Thomas Calmant
 * Date:   6 mai 2011
 */
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
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.jface.util.Util;

public class MakefileLauncher implements ILaunchConfigurationDelegate {

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
	private String getConfiguration(final ILaunchConfiguration aConfiguration,
			final String aConfigKey, final String aDefaultValue) {

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
	private Set<String> getConfigurationSet(
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

		// Run !
		Process execProcess = DebugPlugin.exec(cmdLine, workingDirectory,
				envArray);

		IProcess streamProcess = DebugPlugin.newProcess(aLaunch, execProcess,
				cmdLine[0]);

		// Handle output
		streamProcess.getStreamsProxy().getErrorStreamMonitor()
				.addListener(new IStreamListener() {

					@Override
					public void streamAppended(final String aText,
							final IStreamMonitor aMonitor) {
						// TODO treat error output
						System.out.print("[ERROR] : " + aText);
					}
				});
	}

	/**
	 * Prepares the command line to execute.
	 * 
	 * @param aConfiguration
	 *            Launch configuration
	 * @return The command line in an array
	 */
	private String[] makeCommandLine(final ILaunchConfiguration aConfiguration) {

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
			cmdLine = new ArrayList<String>(makeRules.size() + 3);

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
	private String[] makeEnvironment(final ILaunchConfiguration aConfiguration) {

		String[] env = null;

		try {
			env = DebugPlugin.getDefault().getLaunchManager()
					.getEnvironment(aConfiguration);

		} catch (CoreException e) {
			e.printStackTrace();
		}

		return env;
	}
}
