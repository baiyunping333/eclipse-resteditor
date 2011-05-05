package org.isandlatech.plugins.rest.i18n;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.isandlatech.plugins.rest.hover.IHoverConstants;
import org.isandlatech.plugins.rest.hover.RestTextHover;

/**
 * Internationalization handler. Loads strings from a resource bundle
 * (.properties file).
 * 
 * @author Thomas Calmant
 */
public class Messages {

	/** Base resource bundle */
	private static final String BUNDLE_NAME = "org.isandlatech.plugins.rest.i18n.messages";

	/** Resource bundle to use */
	private static final ResourceBundle RESOURCE_BUNDLE;

	/** Prefix of variables inside message to reference other keys values */
	private static final String VARIABLE_PREFIX = "${";

	/** Suffix of variables inside message to reference other keys values */
	private static final String VARIABLE_SUFFIX = "}";

	static {
		/* Initialization of bundles */
		RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	}

	/**
	 * Replaces variables inside the given message by their value
	 * 
	 * @param aMessage
	 *            Message to be completed
	 * @return The transformed message
	 */
	private static String completeMessage(final String aMessage) {

		int posVarStart = 0;
		int posVarNameStart = 0;
		int posVarEnd = 0;

		StringBuilder builder = new StringBuilder(aMessage);

		// For each variable...
		while ((posVarStart = builder.indexOf(VARIABLE_PREFIX, posVarStart)) != -1) {

			posVarNameStart = posVarStart + VARIABLE_PREFIX.length();

			// Search for the end of the variable
			posVarEnd = builder.indexOf(VARIABLE_SUFFIX, posVarNameStart);
			if (posVarEnd == -1) {
				break;
			}

			// Extract variable name and get its value
			String variableName = builder.substring(posVarNameStart, posVarEnd);
			String variableKey = getString(variableName);

			builder.replace(posVarStart, posVarEnd + 1, variableKey);
		}

		return builder.toString();
	}

	/**
	 * Retrieves the resource bundle
	 * 
	 * @return The resource bundle
	 */
	public static ResourceBundle getBundle() {
		return RESOURCE_BUNDLE;
	}

	/**
	 * Retrieves the help message associated to the given key
	 * 
	 * @param aDirective
	 *            Directive to use
	 * @return The associated help message
	 */
	public static String getDirectiveHelp(final String aDirective) {

		StringBuilder help = new StringBuilder();
		help.append(getString("directive." + aDirective + ".help"));

		// Insert the sample link, if any
		if (RESOURCE_BUNDLE.keySet().contains(
				"directive." + aDirective + ".sample")) {

			help.append("<p><a href=\"");

			// Example: rest-internal://insert-sample/directive.note.sample
			help.append(RestTextHover.makeLink(
					IHoverConstants.SAMPLE_LINK_PREFIX, aDirective));

			help.append("\">");
			help.append(getString(IHoverConstants.INSERT_SAMPLE_MESSAGE));
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

		if (!RESOURCE_BUNDLE.keySet().contains(sampleKey)) {
			return null;
		}

		return getString(sampleKey);
	}

	/**
	 * Retrieves the localized string corresponding to this key
	 * 
	 * @param aKey
	 *            String key
	 * @return The localized string
	 */
	public static String getString(final String aKey) {

		String message;

		try {
			// Raw message
			message = RESOURCE_BUNDLE.getString(aKey);

			// Trim right all lines
			String line;
			StringBuilder trimmedMessage = new StringBuilder(message.length());
			BufferedReader reader = new BufferedReader(
					new StringReader(message));

			try {

				while ((line = reader.readLine()) != null) {
					// Only way to do an rtrim in Java...
					trimmedMessage.append(line.replaceAll("\\s+$", ""));

					// The EOL marker has been deleted
					trimmedMessage.append("\n");
				}

				// Delete the last \n : it is an artificial one
				trimmedMessage.deleteCharAt(trimmedMessage.length() - 1);

				message = trimmedMessage.toString();

			} catch (IOException e) {
				// Do nothing...
			}

		} catch (MissingResourceException ex) {
			message = "<undefined>" + aKey + "</undefined>";
			ex.printStackTrace();
		}

		return completeMessage(message);
	}

	/**
	 * No constructor allowed
	 */
	private Messages() {
		// No constructor
	}
}
