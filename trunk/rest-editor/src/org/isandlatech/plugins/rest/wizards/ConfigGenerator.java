/**
 * File:   ConfigGenerator.java
 * Author: Thomas Calmant
 * Date:   17 févr. 2011
 */
package org.isandlatech.plugins.rest.wizards;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Generates the Sphinx conf.py file
 * 
 * @author Thomas Calmant
 */
public class ConfigGenerator {

	/** Configuration content */
	private final Map<String, String> pConfiguration;

	/**
	 * Prepares the configuration map
	 */
	public ConfigGenerator() {
		pConfiguration = new HashMap<String, String>();
	}

	/**
	 * Generates the configuration file content
	 * 
	 * @return the configuration file content
	 */
	public String generateConfigurationContent() {
		StringBuilder config = new StringBuilder(IConfigConstants.CONFIG_PREFIX);

		if (pConfiguration != null) {
			for (Entry<String, String> entry : pConfiguration.entrySet()) {

				config.append(entry.getKey()).append(" = ")
						.append(entry.getValue()).append('\n');
			}
		}

		return config.toString();
	}

	/**
	 * Retrieves the value associated to the given key
	 * 
	 * @param aProperty
	 *            Property key
	 * @return The property value (can be null)
	 */
	public String getProperty(final String aProperty) {
		String result = pConfiguration.get(aProperty);

		if (result == null) {
			return null;
		}

		// FIXME ugly trick to handle array values
		// If the value is an array, return its first value
		if (result.startsWith("[")) {
			int endOfFirstValue = result.indexOf(',');

			// Only one value, find the end of the array
			if (endOfFirstValue == -1) {
				endOfFirstValue = result.indexOf(']');
			}

			// If found, extract the value
			if (endOfFirstValue != -1) {
				result = result.substring(1, endOfFirstValue).trim();
			}

			// Continue the string treatment
		}

		// Python unicode prefix found, remove it
		if (result.startsWith("u'")) {
			return result.substring(2, result.length() - 1).trim();
		}

		return result;
	}

	/**
	 * Sets the base project informations
	 * 
	 * @param aProjectName
	 *            Project name
	 * @param aAuthors
	 *            Project authors
	 * @param aVersion
	 *            Project version (1.0.0, ...)
	 * @param aRelease
	 *            Project version release (alpha, ...)
	 * @param aBaseFile
	 *            Base file name, without suffix (index)
	 */
	public void setBaseProjectInformations(final String aProjectName,
			final String aAuthors, final String aVersion,
			final String aRelease, final String aBaseFile) {

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);

		String infos;
		String noSpaceProjectName = aProjectName.replace(" ", "");
		String copyright = currentYear + ", " + aAuthors;

		setStringProperty(IConfigConstants.PROJECT_NAME, aProjectName);
		setStringProperty(IConfigConstants.PROJECT_COPYRIGHT, copyright);
		setStringProperty(IConfigConstants.PROJECT_VERSION, aVersion);
		setStringProperty(IConfigConstants.PROJECT_RELEASE, aRelease);

		/*
		 * Latex informations # Grouping the document tree into LaTeX files.
		 * List of tuples # (source start file, target name, title, author,
		 * documentclass [howto/manual]).
		 */
		infos = "[('" + aBaseFile + "', '" + noSpaceProjectName + ".tex', u'"
				+ aProjectName + " Documentation', u'" + aAuthors
				+ "', 'manual')]";

		pConfiguration.put(IConfigConstants.LATEX_DOCUMENT, infos);

		/*
		 * Man informations # One entry per manual page. List of tuples #
		 * (source start file, name, description, authors, manual section).
		 */
		infos = "[('" + aBaseFile + "', '" + noSpaceProjectName + "', u'"
				+ aProjectName + " Documentation', [u'" + aAuthors + "'], 1)]";

		pConfiguration.put(IConfigConstants.MAN_DOCUMENT, infos);
	}

	/**
	 * Sets a configuration boolean property
	 * 
	 * @param aProperty
	 *            Property entry
	 * @param aValue
	 *            Associated value
	 */
	public void setBooleanProperty(final String aProperty, final boolean aValue) {

		if (aProperty == null || aProperty.length() == 0) {
			return;
		}

		pConfiguration.put(aProperty, aValue ? "True" : "False");
	}

	/**
	 * Sets the generated document language
	 * 
	 * @param aLanguage
	 *            Document language
	 */
	public void setLanguage(final String aLanguage) {
		// Set the standard output language
		setStringProperty(IConfigConstants.LANGUAGE, aLanguage);

		// Set the Latex babel language
		String babelLanguage = IConfigConstants.BABEL_LANGUAGE_MAP
				.get(aLanguage);
		if (babelLanguage != null) {
			pConfiguration.put(IConfigConstants.LATEX_ELEMENTS,
					"{ 'babel': '\\\\usepackage[" + babelLanguage
							+ "]{babel}' }");
		}
	}

	/**
	 * Sets the static and template folders names
	 * 
	 * @param aFolderPrefix
	 *            The prefix to be used
	 */
	public void setStaticFoldersPrefix(final String aFolderPrefix) {
		pConfiguration.put(IConfigConstants.TEMPLATE_PATH, "[u'"
				+ aFolderPrefix + "templates']");

		pConfiguration.put(IConfigConstants.STATIC_PATH, "[u'" + aFolderPrefix
				+ "static']");
	}

	/**
	 * Sets a configuration string property
	 * 
	 * @param aProperty
	 *            Property entry
	 * @param aValue
	 *            Associated value
	 */
	public void setStringProperty(final String aProperty, String aValue) {

		if (aProperty == null || aProperty.length() == 0) {
			return;
		}

		if (aValue == null) {
			aValue = "None";
		} else {
			aValue = "u'" + aValue + "'";
		}

		pConfiguration.put(aProperty, aValue);
	}

	/**
	 * Sets a configuration string property
	 * 
	 * @param aProperty
	 *            Property entry
	 * @param aValue
	 *            Property value
	 * @param aDefaultValue
	 *            Value to be used if aValue is null or empty
	 */
	public void setStringProperty(final String aProperty, final String aValue,
			final String aDefaultValue) {

		if (aValue == null || aValue.isEmpty()) {
			setStringProperty(aProperty, aDefaultValue);
		} else {
			setStringProperty(aProperty, aValue);
		}
	}
}
