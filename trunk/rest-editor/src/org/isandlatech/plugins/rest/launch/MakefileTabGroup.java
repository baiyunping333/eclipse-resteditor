/**
 * File:   MakefileTabGroup.java
 * Author: Thomas Calmant
 * Date:   6 mai 2011
 */
package org.isandlatech.plugins.rest.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * Configures the Sphinx make file build configuration tool
 * 
 * @author Thomas Calmant
 */
public class MakefileTabGroup extends AbstractLaunchConfigurationTabGroup {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse
	 * .debug.ui.ILaunchConfigurationDialog, java.lang.String)
	 */
	@Override
	public void createTabs(final ILaunchConfigurationDialog aDialog,
			final String aMode) {

		// Tabs in the tab group
		ILaunchConfigurationTab tabs[] = new ILaunchConfigurationTab[] {
				new MakefileTabMain(), new CommonTab(), new EnvironmentTab() };

		setTabs(tabs);
	}
}
