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

import org.eclipse.debug.core.DebugPlugin;

/**
 * Constants for the makefile launcher and affiliates
 * 
 * @author Thomas Calmant
 */
public interface IMakefileConstants {

	/** Enable custom rules */
	String ATTR_CUSTOM_RULES_ENABLED = DebugPlugin.getUniqueIdentifier()
			+ ".custom.rules";

	/** Make command configuration attribute default value */
	String ATTR_DEFAULT_MAKE_CMD = "/usr/bin/make";

	/** Default project name */
	String ATTR_DEFAULT_WORKING_DIRECTORY = "";

	/** Make command configuration attribute */
	String ATTR_MAKE_CMD = DebugPlugin.getUniqueIdentifier() + ".make.cmd";

	/** Make rules attribute */
	String ATTR_MAKE_RULES = DebugPlugin.getUniqueIdentifier() + ".make.rules";

	/** Project name attribute */
	String ATTR_WORKING_DIRECTORY = DebugPlugin.getUniqueIdentifier()
			+ ".project.name";

	/**
	 * Known Sphinx rules, for Sphinx v1.0.7. Maybe we could ask Sphinx directly
	 * ?
	 */
	String[] SPHINX_MAKE_RULES = { "html", "dirhtml", "singlehtml", "pickle",
			"json", "htmlhelp", "qthelp", "devhelp", "epub", "latex",
			"latexpdf", "text", "man", "changes", "linkcheck", "doctest" };

	/** Undefined configuration value */
	String UNDEFINED = "<UNDEFINED>";
}
