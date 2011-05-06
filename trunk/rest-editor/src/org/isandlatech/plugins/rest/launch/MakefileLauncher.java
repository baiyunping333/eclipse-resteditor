/**
 * File:   MakefileLauncher.java
 * Author: Thomas Calmant
 * Date:   6 mai 2011
 */
package org.isandlatech.plugins.rest.launch;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;

/**
 * Sphinx build launcher
 * 
 * @author Thomas Calmant
 */
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
	 * Retrieves the absolute path corresponding to the compilation base
	 * directory. Returns null on error.
	 * 
	 * @param aConfiguration
	 *            Launch configuration
	 * @return The absolute path of the working directory, null on error
	 */
	private File getWorkingDirectory(final ILaunchConfiguration aConfiguration) {

		// Search for the project directory
		String configWorkDir = getConfiguration(aConfiguration,
				IMakefileConstants.ATTR_WORKING_DIRECTORY,
				IMakefileConstants.ATTR_DEFAULT_WORKING_DIRECTORY);

		File workingDirectory = null;

		try {
			URI projectURI = new URI(configWorkDir);

			if (!projectURI.isAbsolute()) {
				// Dialog box selection or relative user entry : construct an
				// absolute path

				IPath completePath = ResourcesPlugin.getWorkspace().getRoot()
						.getLocation().append(configWorkDir);

				projectURI = URIUtil.toURI(completePath.makeAbsolute());
			}

			workingDirectory = new File(projectURI);

		} catch (URISyntaxException e) {
			e.printStackTrace();
			workingDirectory = null;
		}

		return workingDirectory;
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
		ArrayList<String> cmdLine = new ArrayList<String>(makeRules.size() + 1);
		cmdLine.add(makeCmd);
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
