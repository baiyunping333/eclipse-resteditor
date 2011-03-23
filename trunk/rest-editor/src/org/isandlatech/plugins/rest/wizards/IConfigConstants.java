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

	/** Build folder name */
	String BUILD_FOLDER_NAME = "build";

	/** Sphinx configuration file name */
	String CONFIG_FILE_NAME = "conf.py";

	/** Common conf.py part */
	String CONFIG_PREFIX = "# -*- coding: utf-8 -*-\n"
			+ "import sys, os\n\n"
			+ "# The suffix of source filenames.\n"
			+ "source_suffix = '.rst'\n"
			+ "# List of patterns, relative to source directory, that match files and\n"
			+ "# directories to ignore when looking for source files.\n"
			+ "exclude_patterns = []\n"
			+ "# The name of the Pygments (syntax highlighting) style to use.\n"
			+ "pygments_style = 'sphinx'\n\n"
			+ "# Beginning of generated configuration\n";

	/** Master document default name */
	String DEFAULT_MASTER_DOCUMENT_NAME = "index";

	/** todo:: extension activation */
	String EXT_TODO_ACTIVATION = "todo_include_todos";

	/** todo:: extension package */
	String EXT_TODO_PACKAGE = "sphinx.ext.todo";

	/** Extensions list name */
	String EXTENSION_LIST_NAME = "extensions";

	/** Date of creation */
	String EXTRA_DATE = "${date}";

	/** Plugin name */
	String EXTRA_PLUGIN_NAME = "${plugin}";

	/** Project name */
	String EXTRA_PROJECT_NAME = "${project.name}";

	/** Project name without spaces */
	String EXTRA_PROJECT_NAME_NOSPACE = "${project.name.nospace}";

	/** HTML theme property */
	String HTML_THEME = "html_theme";

	/** Generated HTML documents style */
	String[] HTML_THEMES = { "", "default", "sphinxdoc", "scrolls", "agogo",
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

	/** Latex font size */
	String LATEX_FONT_SIZE = "latex_font_size";

	/** Latex paper size */
	String LATEX_PAPER_SIZE = "latex_paper_size";

	/** Use parts or chapters as root element in LaTex */
	String LATEX_USE_PARTS = "latex_use_parts";

	/** Man pages informations */
	String MAN_DOCUMENT = "man_pages";

	/** Master document name */
	String MASTER_DOCUMENT_NAME = "master_doc";

	/** Copyright */
	String PROJECT_COPYRIGHT = "copyright";

	/** Project name */
	String PROJECT_NAME = "project";

	/** Project release */
	String PROJECT_RELEASE = "release";

	/** Project version */
	String PROJECT_VERSION = "version";

	/** Conf.py template, relative to the plug-in JAR file root */
	String RESOURCE_CONF_PY_TEMPLATE = "/conf.py.template";

	/** Make.bat template, relative to the plug-in JAR file root */
	String RESOURCE_MAKEBAT_TEMPLATE = "/make.bat.template";

	/** Makefile template, relative to the plug-in JAR file root */
	String RESOURCE_MAKEFILE_TEMPLATE = "/Makefile.template";

	/** Templates source folder variable */
	String RESOURCE_TEMPLATE_SOURCE_VAR = "${OUTPUT_SOURCE_FOLDER}";

	/** ReST file extension (with the dot) */
	String REST_FILE_EXTENSION = ".rst";

	/** Input source encoding */
	String SOURCE_ENCODING = "source_encoding";

	/** Source folder name */
	String SOURCE_FOLDER_NAME = "source";

	/** HTML static path */
	String STATIC_PATH = "html_static_path";

	/** Template path */
	String TEMPLATE_PATH = "templates_path";

	/** Template project name without space variable */
	String RESOURCE_TEMPLATE_PROJECTNAME_VAR = "${PROJECT_NAME_NOSPACE}";
}
