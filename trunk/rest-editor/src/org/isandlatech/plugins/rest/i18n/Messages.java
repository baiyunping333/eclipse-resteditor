package org.isandlatech.plugins.rest.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Internationalization handler. Loads strings from a resource bundle
 * (.properties file).
 * 
 * @author Thomas Calmant
 */
public class Messages {

	/** Base resource bundle */
	private static final String BUNDLE_NAME = "org.isandlatech.plugins.rest.i18n.messages"; //$NON-NLS-1$

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

		// Foreach variable...
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
		return getString("directive." + aDirective + ".help");
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
			message = RESOURCE_BUNDLE.getString(aKey);
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
