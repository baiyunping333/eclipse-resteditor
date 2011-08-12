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

package org.isandlatech.plugins.rest.editor.userassist;

/**
 * Common constants used by text hover process
 * 
 * @author Thomas Calmant
 */
public interface IAssistanceConstants {

	/** Insert sample message */
	String INSERT_SAMPLE_MESSAGE = "help.directive.sample.insert";

	/** Internal browser ID */
	String INTERNAL_BROWSER_ID = "org.isandlatech.plugins.rest.editor.userassist";

	/** Browser internal links prefix */
	String INTERNAL_PREFIX = "rest-internal://";

	/** Sample insertion action prefix */
	String SAMPLE_LINK_PREFIX = "insert-sample/";

	/** Spelling action prefix in internal links */
	String SPELL_LINK_PREFIX = "spell/";
}
