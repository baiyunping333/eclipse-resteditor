/**
 * File:   ColorProvider.java
 * Author: Thomas Calmant
 * Date:   7 mai 2011
 */
package org.isandlatech.plugins.rest.editor.providers;


/**
 * Theme colors keys, as used in the extension declaration (plugin.xml)
 * 
 * @author Thomas Calmant
 */
public interface IThemeConstants {

	/** Default / Text color */
	String DEFAULT = "org.isandlatech.plugins.rest.theme.default";

	/** Literal directives */
	String DIRECTIVE = "org.isandlatech.plugins.rest.theme.directive";

	/** Inline emphasis */
	String INLINE_EMPHASIS = "org.isandlatech.plugins.rest.theme.inline.emphasis";

	/** Inline literal */
	String INLINE_LITERAL = "org.isandlatech.plugins.rest.theme.inline.literal";

	/** Links */
	String LINK = "org.isandlatech.plugins.rest.theme.link";

	/** List bullets */
	String LIST_BULLET = "org.isandlatech.plugins.rest.theme.bullet";

	/** Literal blocks */
	String LITERAL = "org.isandlatech.plugins.rest.theme.literal";

	/** Sections */
	String SECTION = "org.isandlatech.plugins.rest.theme.section";

	/** Source blocks */
	String SOURCE = "org.isandlatech.plugins.rest.theme.source";

	/** Tables */
	String TABLE = "org.isandlatech.plugins.rest.theme.table";

	/** Color theme keys */
	String[] THEME_KEYS = { DEFAULT, DIRECTIVE, INLINE_EMPHASIS,
			INLINE_LITERAL, LINK, LIST_BULLET, LITERAL, SECTION, SOURCE, TABLE };
}
