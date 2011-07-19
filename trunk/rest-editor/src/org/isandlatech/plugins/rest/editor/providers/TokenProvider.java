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
import org.isandlatech.plugins.rest.RestPlugin;
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
	 * Initializes the token provider
	 */
	public TokenProvider() {
		initializeProvider();
	}

	/**
	 * Converts a string representation of a Color (from
	 * {@link Color#toString()} to a color instance.
	 * 
	 * Color format : "RED,GREEN,BLUE", values in base 10.
	 * 
	 * @param aKey
	 *            Preference key
	 * @return The color described by preferences, null on error
	 */
	public Color getColorFromPreferences(final String aKey) {

		String value = RestPlugin.getDefault().getPreferenceStore()
				.getString(aKey);

		if (value == null) {
			return null;
		}

		String values[] = value.split(",");
		if (values.length != 3) {
			return null;
		}

		int red = Integer.parseInt(values[0], 10);
		int green = Integer.parseInt(values[1], 10);
		int blue = Integer.parseInt(values[2], 10);

		return new Color(Display.getCurrent(), red, green, blue);
	}

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

		Color result;

		// Eclipse Color theme
		result = getColorFromPreferences(aKey);

		// Basic Eclipse theme
		if (result == null) {
			result = themeManager.getCurrentTheme().getColorRegistry()
					.get(aKey);
		}

		// Absolutely no color...
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
		Color defaultColor = getKeyThemeColor(IThemeConstants.DEFAULT);
		Color inlineEmphasisColor = getKeyThemeColor(IThemeConstants.INLINE_EMPHASIS);
		Color inlineLiteralColor = getKeyThemeColor(IThemeConstants.INLINE_LITERAL);

		Color linkColor = getKeyThemeColor(IThemeConstants.LINK);
		Color listBulletsColor = getKeyThemeColor(IThemeConstants.LIST_BULLET);
		Color directiveColor = getKeyThemeColor(IThemeConstants.DIRECTIVE);
		Color literalBlockColor = getKeyThemeColor(IThemeConstants.LITERAL);
		Color sectionBlockColor = getKeyThemeColor(IThemeConstants.SECTION);
		Color souceBlockColor = getKeyThemeColor(IThemeConstants.SOURCE);
		Color tableBlockColor = getKeyThemeColor(IThemeConstants.TABLE);

		// Default token
		pDefaultToken = getToken(defaultColor);

		if (pTokensRegistry == null) {
			pTokensRegistry = new HashMap<String, IToken>();
		} else {
			pTokensRegistry.clear();
		}

		// In-line modifiers
		pTokensRegistry.put(ITokenConstants.INLINE_LITERAL,
				getToken(inlineLiteralColor, null, SWT.ITALIC));

		pTokensRegistry.put(ITokenConstants.INLINE_EMPHASIS_TEXT,
				getToken(inlineEmphasisColor, null, SWT.ITALIC));

		pTokensRegistry.put(ITokenConstants.INLINE_BOLD_TEXT,
				getToken(inlineEmphasisColor, null, SWT.BOLD));

		// Links, fields, roles
		commonToken = getToken(linkColor, null, SWT.ITALIC);
		pTokensRegistry.put(ITokenConstants.LINK, commonToken);
		pTokensRegistry.put(ITokenConstants.FIELD, commonToken);
		pTokensRegistry.put(ITokenConstants.ROLE, commonToken);

		// Link references
		commonToken = getToken(linkColor, null, SWT.BOLD);
		pTokensRegistry.put(ITokenConstants.LINK_REFERENCE, commonToken);
		pTokensRegistry.put(ITokenConstants.LINK_FOOTNOTE, commonToken);
		pTokensRegistry.put(ITokenConstants.SUBSTITUTION, commonToken);

		// Lists
		pTokensRegistry.put(ITokenConstants.LIST_BULLET,
				getToken(listBulletsColor, null, SWT.ITALIC | SWT.BOLD));

		// Literals
		pTokensRegistry.put(ITokenConstants.LITERAL_DEFAULT,
				getToken(literalBlockColor));

		pTokensRegistry.put(ITokenConstants.LITERAL_DIRECTIVE,
				getToken(directiveColor, null, SWT.BOLD));

		// Section
		pTokensRegistry.put(ITokenConstants.SECTION,
				getToken(sectionBlockColor, null, SWT.BOLD));

		// Code block
		pTokensRegistry.put(ITokenConstants.SOURCE,
				getToken(souceBlockColor, null, SWT.NORMAL));

		// Table (grid and simple)
		pTokensRegistry.put(ITokenConstants.TABLE, getToken(tableBlockColor));
	}
}
