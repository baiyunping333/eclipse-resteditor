/**
 * File:   ConfigGenerator.java
 * Author: Thomas Calmant
 * Date:   17 févr. 2011
 */
package org.isandlatech.plugins.rest.wizards;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.isandlatech.plugins.rest.RestPlugin;

/**
 * Generates the Sphinx conf.py file from a template
 * 
 * @author Thomas Calmant
 */
public final class ConfigGenerator {

	/** Configuration content */
	private final Map<String, String> pConfiguration;

	/** Extension packages list */
	private final List<String> pExtensionPackages;

	/** Project name */
	private String pProjectName;

	/** Project name (without spaces) */
	private String pNoSpaceProjectName;

	/**
	 * Prepares the configuration map
	 */
	public ConfigGenerator() {
		pConfiguration = new HashMap<String, String>();
		pExtensionPackages = new ArrayList<String>();
	}

	/**
	 * Adds the given package to the extensions list (if needed)
	 * 
	 * @param aPackageName
	 *            Extension package name
	 */
	public void addExtensionPackage(final String aPackageName) {
		if (!pExtensionPackages.contains(aPackageName)) {
			pExtensionPackages.add(aPackageName);
		}
	}

	/**
	 * Generates the configuration file content from a template file in the
	 * bundle
	 * 
	 * @return the configuration file content
	 * @throws IOException
	 */
	public String generateConfigurationContent() throws IOException {
		// Open and read the template file
		String configTemplate = RestPlugin.getDefault().getBundleFileContent(
				IConfigConstants.RESOURCE_CONF_PY_TEMPLATE);

		// Transform it into a String builder
		StringBuilder config = new StringBuilder(configTemplate);

		// Generate the extension packages list
		generateExtensionList();

		// Set the configuration values
		if (pConfiguration != null) {
			for (Entry<String, String> entry : pConfiguration.entrySet()) {
				setConfigLine(config, entry.getKey(), entry.getValue());
			}
		}

		// Set the last fields
		replacePluginFields(config);
		return config.toString();
	}

	/**
	 * Generates the extension packages list and stores it in the configuration
	 * dictionary.
	 * 
	 * Automatically called by {@link #generateConfigurationContent()}.
	 */
	private void generateExtensionList() {

		StringBuilder extensionListStr = new StringBuilder();
		extensionListStr.append('[');

		int i = 0;
		int nbExtensions = pExtensionPackages.size();

		for (String extensionPackage : pExtensionPackages) {
			extensionListStr.append('\'').append(extensionPackage).append('\'');

			if (++i < nbExtensions) {
				extensionListStr.append(", ");
			}
		}

		extensionListStr.append(']');

		// Store the list
		pConfiguration.put(IConfigConstants.EXTENSION_LIST_NAME,
				extensionListStr.toString());
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
	 * Replaces all plug-in specific fields in the template
	 * 
	 * @param aConfig
	 *            Configuration template content
	 */
	private void replacePluginFields(final StringBuilder aConfig) {

		// Project name
		searchAndReplace(aConfig, IConfigConstants.EXTRA_PROJECT_NAME,
				pProjectName);

		searchAndReplace(aConfig, IConfigConstants.EXTRA_PROJECT_NAME_NOSPACE,
				pNoSpaceProjectName);

		// Plug-in name
		searchAndReplace(aConfig, IConfigConstants.EXTRA_PLUGIN_NAME,
				RestPlugin.PLUGIN_NAME);

		// Date of creation
		String dateOfCreation = DateFormat.getDateInstance().format(
				Calendar.getInstance().getTime());

		searchAndReplace(aConfig, IConfigConstants.EXTRA_DATE, dateOfCreation);
	}

	/**
	 * Search and replace all occurrences of the given pattern by the given
	 * string.
	 * 
	 * Returns true on success, false if the pattern wasn't found.
	 * 
	 * @param aText
	 *            Text to search in
	 * @param aPattern
	 *            Pattern to be replaced
	 * @param aReplacement
	 *            Replacement string
	 * @return True on success, False if no replacement have been done
	 */
	private boolean searchAndReplace(final StringBuilder aText,
			final String aPattern, final String aReplacement) {

		// Find the pattern
		int index = aText.indexOf(aPattern);
		boolean replaced = (index != -1);

		while (index != -1) {
			// Known configuration key : replace it
			aText.replace(index, index + aPattern.length(), aReplacement);
			index = aText.indexOf(aPattern);
		}

		return replaced;
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
		pProjectName = aProjectName;
		pNoSpaceProjectName = aProjectName.replace(" ", "");
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
		infos = "[('" + aBaseFile + "', '" + pNoSpaceProjectName + ".tex', u'"
				+ aProjectName + " Documentation', u'" + aAuthors
				+ "', 'manual')]";

		pConfiguration.put(IConfigConstants.LATEX_DOCUMENT, infos);

		/*
		 * Man informations # One entry per manual page. List of tuples #
		 * (source start file, name, description, authors, manual section).
		 */
		infos = "[('" + aBaseFile + "', '" + pNoSpaceProjectName + "', u'"
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
	 * Adds the given configuration entry to the configuration content.
	 * 
	 * If the key is found as a replaceable entry, then it replaces it, else it
	 * is appended.s
	 * 
	 * @param aConfig
	 *            Configuration content builder
	 * @param aKey
	 *            Configuration entry key
	 * @param aValue
	 *            Entry value
	 */
	private void setConfigLine(final StringBuilder aConfig, final String aKey,
			final String aValue) {

		// Key to be searched
		String configKey = "${" + aKey + "}";

		// Replacement line
		String configLine = aKey + " = " + aValue + '\n';

		if (!searchAndReplace(aConfig, configKey, configLine)) {
			// Key not found : append the line
			aConfig.append(configLine);
		}
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
