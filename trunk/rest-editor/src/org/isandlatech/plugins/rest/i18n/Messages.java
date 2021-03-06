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

package org.isandlatech.plugins.rest.i18n;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.isandlatech.plugins.rest.RestPlugin;

/**
 * Internationalization handler. Loads strings from a resource bundle
 * (.properties file).
 * 
 * @author Thomas Calmant
 */
public final class Messages {

	/** Base resource bundle */
	private static final String BUNDLE_NAME = "org.isandlatech.plugins.rest.i18n.messages";

	/** Resource bundle to use */
	public static final ResourceBundle RESOURCE_BUNDLE;

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
	 * Tests whether the resource bundle contains the given key or not
	 * 
	 * @param aKey
	 *            The key to be tested
	 * @return True if the key is present, else false
	 */
	public static boolean containsKey(final String aKey) {
		return RESOURCE_BUNDLE.containsKey(aKey);
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
			RestPlugin.logError("Ressource not found", ex);
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
