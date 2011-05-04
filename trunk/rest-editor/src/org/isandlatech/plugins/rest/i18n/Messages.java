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

	static {
		/* Initialization of bundles */
		RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
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

		return message;
	}

	/**
	 * No constructor allowed
	 */
	private Messages() {
		// No constructor
	}
}
