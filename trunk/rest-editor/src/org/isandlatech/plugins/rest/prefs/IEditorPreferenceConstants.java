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

package org.isandlatech.plugins.rest.prefs;

/**
 * Constant definitions for plug-in preferences
 * 
 * @author Thomas Calmant
 */
public interface IEditorPreferenceConstants {

	/** Maximum line length when a line wrapper is turned on */
	String EDITOR_LINEWRAP_LENGTH = "editor.linewrap.length";

	/** Line wrap mode */
	String EDITOR_LINEWRAP_MODE = "editor.linewrap.mode";

	/** Auto-format on save */
	String EDITOR_SAVE_FORMAT = "editor.save.format";

	/** Reset section markers on save */
	String EDITOR_SAVE_RESET_MARKERS = "editor.save.markers";

	/** Trim lines on save */
	String EDITOR_SAVE_TRIM = "editor.save.trim";

	/** Preferred section marker order */
	String EDITOR_SECTION_MARKERS = "editor.section.markers";

	/** Tabs length */
	String EDITOR_TABS_LENGTH = "editor.tabs.length";

	/** Automatically convert tabs to spaces */
	String EDITOR_TABS_TO_SPACES = "editor.tabs.toSpaces";
}
