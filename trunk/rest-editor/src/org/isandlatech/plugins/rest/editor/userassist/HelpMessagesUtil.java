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

import org.isandlatech.plugins.rest.i18n.Messages;

/**
 * Help messages utility class
 * 
 * @author Thomas Calmant
 */
public final class HelpMessagesUtil {

	/**
	 * Retrieves the help message associated to the given key
	 * 
	 * @param aDirective
	 *            Directive to use
	 * @return The associated help message
	 */
	public static String getDirectiveHelp(final String aDirective) {

		StringBuilder help = new StringBuilder();
		help.append(Messages.getString("directive." + aDirective + ".help"));

		// Insert the sample link, if any
		if (Messages.containsKey("directive." + aDirective + ".sample")) {

			help.append("<p><a href=\"");

			// Example: rest-internal://insert-sample/directive.note.sample
			help.append(BasicInternalLinkHandler.makeLink(
					IAssistanceConstants.SAMPLE_LINK_PREFIX, aDirective));

			help.append("\">");
			help.append(Messages
					.getString(IAssistanceConstants.INSERT_SAMPLE_MESSAGE));
			help.append("</a></p>");
		}

		return help.toString();
	}

	/**
	 * Retrieves the sample associated to the help message of the given
	 * directive, or null if unavailable
	 * 
	 * @param aDirective
	 *            Directive to use
	 * @return The associated sample, or null
	 */
	public static String getDirectiveSample(final String aDirective) {

		String sampleKey = "directive." + aDirective + ".sample";

		if (!Messages.containsKey(sampleKey)) {
			return null;
		}

		return Messages.getString(sampleKey);
	}

	/**
	 * Hidden constructor
	 */
	private HelpMessagesUtil() {
		// Hidden constructor
	}

}
