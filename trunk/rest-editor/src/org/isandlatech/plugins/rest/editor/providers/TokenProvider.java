/**
 * File:   TokenProvider.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.providers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.IThemeManager;
import org.isandlatech.plugins.rest.editor.scanners.ITokenConstants;

/**
 * Token objects provider. Based on a color provider.
 * 
 * @author Thomas Calmant
 */
public class TokenProvider {

	/** Default token */
	private IToken pDefaultToken;

	/** Existing tokens */
	private Map<String, IToken> pTokensRegistry;

	/**
	 * Retrieves the color from the current Eclipse theme corresponding to the
	 * given key
	 * 
	 * @param aKey
	 *            Theme color key
	 * @return The theme color, black (0,0,0) by default
	 */
	public Color getKeyThemeColor(final String aKey) {

		IThemeManager themeManager = PlatformUI.getWorkbench()
				.getThemeManager();
		Color result = themeManager.getCurrentTheme().getColorRegistry()
				.get(aKey);

		if (result == null) {
			result = new Color(Display.getCurrent(), 0, 0, 0);
		}

		return result;
	}

	/**
	 * Prepares a text token
	 * 
	 * @param aForeground
	 *            Foreground color
	 * @return The new token
	 */
	private Token getToken(final Color aForeground) {
		return getToken(aForeground, null, SWT.NORMAL);
	}

	/**
	 * Prepares a text token
	 * 
	 * @param aForeground
	 *            Foreground color
	 * @param aBackground
	 *            Background color
	 * @param style
	 *            SWT text style
	 * @return The new token
	 */
	private Token getToken(final Color aForeground, final Color aBackground,
			final int style) {

		return new Token(new TextAttribute(aForeground, aBackground, style));
	}

	/**
	 * Retrieves the token corresponding to the given element
	 * 
	 * @param aElement
	 *            Recognized element
	 * @return The token corresponding to the given element, or a default
	 *         non-null one
	 */
	public IToken getTokenForElement(final String aElement) {

		IToken token = pTokensRegistry.get(aElement);
		if (token != null) {
			return token;
		}

		return pDefaultToken;
	}

	/**
	 * Initializes the token provider
	 * 
	 * @param aColorProvider
	 *            Color provider (can be null)
	 */
	public void initializeProvider() {

		IToken commonToken;

		// Theme colors
		Color default_color = getKeyThemeColor(IThemeConstants.DEFAULT);
		Color inline_emphasis_color = getKeyThemeColor(IThemeConstants.INLINE_EMPHASIS);
		Color inline_literal_color = getKeyThemeColor(IThemeConstants.INLINE_LITERAL);

		Color link_color = getKeyThemeColor(IThemeConstants.LINK);
		Color list_bullets_color = getKeyThemeColor(IThemeConstants.LIST_BULLET);
		Color directive_color = getKeyThemeColor(IThemeConstants.DIRECTIVE);
		Color literal_block_color = getKeyThemeColor(IThemeConstants.LITERAL);
		Color section_block_color = getKeyThemeColor(IThemeConstants.SECTION);
		Color souce_block_color = getKeyThemeColor(IThemeConstants.SOURCE);
		Color table_block_color = getKeyThemeColor(IThemeConstants.TABLE);

		// Default token
		pDefaultToken = getToken(default_color);

		pTokensRegistry = new HashMap<String, IToken>();

		// In-line modifiers
		pTokensRegistry.put(ITokenConstants.INLINE_LITERAL,
				getToken(inline_literal_color, null, SWT.ITALIC));

		pTokensRegistry.put(ITokenConstants.INLINE_EMPHASIS_TEXT,
				getToken(inline_emphasis_color, null, SWT.ITALIC));

		pTokensRegistry.put(ITokenConstants.INLINE_BOLD_TEXT,
				getToken(inline_emphasis_color, null, SWT.BOLD));

		// Links, fields, roles
		commonToken = getToken(link_color, null, SWT.ITALIC);
		pTokensRegistry.put(ITokenConstants.LINK, commonToken);
		pTokensRegistry.put(ITokenConstants.FIELD, commonToken);
		pTokensRegistry.put(ITokenConstants.ROLE, commonToken);

		// Link references
		commonToken = getToken(link_color, null, SWT.BOLD);
		pTokensRegistry.put(ITokenConstants.LINK_REFERENCE, commonToken);
		pTokensRegistry.put(ITokenConstants.LINK_FOOTNOTE, commonToken);
		pTokensRegistry.put(ITokenConstants.SUBSTITUTION, commonToken);

		// Lists
		pTokensRegistry.put(ITokenConstants.LIST_BULLET,
				getToken(list_bullets_color, null, SWT.ITALIC | SWT.BOLD));

		// Literals
		pTokensRegistry.put(ITokenConstants.LITERAL_DEFAULT,
				getToken(literal_block_color));

		pTokensRegistry.put(ITokenConstants.LITERAL_DIRECTIVE,
				getToken(directive_color, null, SWT.BOLD));

		// Section
		pTokensRegistry.put(ITokenConstants.SECTION,
				getToken(section_block_color, null, SWT.BOLD));

		// Code block
		pTokensRegistry.put(ITokenConstants.SOURCE,
				getToken(souce_block_color, null, SWT.NORMAL));

		// Table (grid and simple)
		pTokensRegistry.put(ITokenConstants.TABLE, getToken(table_block_color));
	}
}
