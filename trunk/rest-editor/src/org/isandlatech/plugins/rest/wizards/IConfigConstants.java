/**
 * File:   IConfigConstants.java
 * Author: Thomas Calmant
 * Date:   17 févr. 2011
 */
package org.isandlatech.plugins.rest.wizards;

import java.util.HashMap;
import java.util.Map;

/**
 * Sphinx conf.py file constants
 * 
 * @author Thomas Calmant
 */
public interface IConfigConstants {

	/** Language -> LaTex babel package language */
	Map<String, String> BABEL_LANGUAGE_MAP = new HashMap<String, String>() {
		/** Serial Version UID (to avoid a warning) */
		private static final long serialVersionUID = 1L;

		{
			put("en", "english");
			put("fr", "french");
			put("ca", "catalan");
			put("cs", "czech");
			put("da", "danish");
			put("de", "german");
			put("es", "spanish");
			put("fi", "finnish");
			put("hr", "croatian");
			put("it", "italian");
			put("nl", "dutch");
			put("pl", "polish");
			put("pt_BR", "brazilian");
			put("ru", "russian");
			put("sl", "slovene");
			put("tr", "turkish");
			put("uk_UA", "ukrainian");
		}
	};

	/** Sphinx configuration file name */
	String CONFIG_FILE_NAME = "conf.py";

	/** Common conf.py part */
	String CONFIG_PREFIX = "# -*- coding: utf-8 -*-\n\n"
			+ "import sys, os\n\n"
			+ "# Add any paths that contain templates here, relative to this directory.\n"
			+ "templates_path = ['_templates']\n"
			+ "# The suffix of source filenames.\n"
			+ "source_suffix = '.rst'\n"
			+ "# List of patterns, relative to source directory, that match files and\n"
			+ "# directories to ignore when looking for source files.\n"
			+ "exclude_patterns = []\n"
			+ "# The name of the Pygments (syntax highlighting) style to use.\n"
			+ "pygments_style = 'sphinx'\n"
			+ "# Add any paths that contain custom static files (such as style sheets) here,\n"
			+ "# relative to this directory. They are copied after the builtin static files,\n"
			+ "# so a file named \"default.css\" will overwrite the builtin \"default.css\".\n"
			+ "html_static_path = ['_static']\n\n"
			+ "# Beginning of generated configuration\n";

	/** Generated HTML documents style */
	String[] HTML_STYLES = { "", "default", "sphinxdoc", "scrolls", "agogo",
			"traditional", "nature", "haiku" };

	/** Generated documents language */
	String LANGUAGE = "language";

	/** Available languages (as of 1.0.7) */
	String[] LANGUAGES = { "", "en", "fr", "bn", "ca", "cs", "da", "de", "es",
			"fi", "hr", "it", "ja", "lt", "nl", "pl", "pt_BR", "ru", "sl",
			"tr", "uk_UA", "zh_CN", "zh_TW" };

	/** Latex document informations */
	String LATEX_DOCUMENT = "latex_documents";

	/** Latex snippet (Python dictionary). Used for babel package configuration */
	String LATEX_ELEMENTS = "latex_elements";

	/** Latex paper size */
	String LATEX_PAPER_SIZE = "latex_paper_size";

	/** Man pages informations */
	String MAN_DOCUMENT = "man_pages";

	/** Copyright */
	String PROJECT_COPYRIGHT = "copyright";

	/** Project name */
	String PROJECT_NAME = "project";

	/** Project release */
	String PROJECT_RELEASE = "release";

	/** Project version */
	String PROJECT_VERSION = "version";
}
