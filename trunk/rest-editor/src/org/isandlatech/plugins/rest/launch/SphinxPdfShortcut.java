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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;

/**
 * Shortcut for a Sphinx PDF build
 * 
 * @author Thomas Calmant
 */
public class SphinxPdfShortcut extends AbstractShinxShortcut {

	/** The Makefile rule used by this shortcut */
	public static final String MAKE_RULE = "latexpdf";

	/** The rule(s) set */
	private Set<String> pRulesSet;

	/**
	 * Prepares members
	 */
	public SphinxPdfShortcut() {

		pRulesSet = new HashSet<String>();
		pRulesSet.add(MAKE_RULE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.launch.AbstractShinxShortcut#getConfigName()
	 */
	@Override
	protected String getConfigName(final IProject aProject) {
		return "Auto Sphinx PDF for " + aProject.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.launch.AbstractShinxShortcut#getMakeRules()
	 */
	@Override
	protected Set<String> getMakeRules() {
		return pRulesSet;
	}
}
