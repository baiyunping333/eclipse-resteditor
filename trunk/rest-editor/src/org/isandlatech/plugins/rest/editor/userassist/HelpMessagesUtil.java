/**
 * File:   HelpMessagesUtil.java
 * Author: Thomas Calmant
 * Date:   11 mai 2011
 */
package org.isandlatech.plugins.rest.editor.userassist;

import org.isandlatech.plugins.rest.i18n.Messages;

/**
 * @author Thomas Calmant
 * 
 */
public class HelpMessagesUtil {

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

}
