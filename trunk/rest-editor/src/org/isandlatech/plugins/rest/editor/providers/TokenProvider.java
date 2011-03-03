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
import org.eclipse.swt.graphics.RGB;
import org.isandlatech.plugins.rest.editor.scanners.ITokenConstants;

/**
 * Token objects provider. Based on a color provider.
 * 
 * @author Thomas Calmant
 */
public class TokenProvider {

	/** The tokens' colors provider */
	private ColorProvider pColorProvider;

	/** Existing tokens */
	private Map<String, IToken> pTokensRegistry;

	/** Default token */
	public final IToken pDefaultToken;

	/** Token provider initialization */
	public TokenProvider() {
		initializeProvider(null);
		pDefaultToken = getToken(new RGB(0, 0, 0));
	}

	/**
	 * Prepares a text token
	 * 
	 * @param aForeground
	 *            Foreground color
	 * @return The new token
	 */
	private Token getToken(final RGB aForeground) {
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
	private Token getToken(final RGB aForeground, final RGB aBackground,
			final int style) {

		Color foregroundColor = pColorProvider.getColor(aForeground);
		Color backgroundColor = pColorProvider.getColor(aBackground);

		return new Token(new TextAttribute(foregroundColor, backgroundColor,
				style));
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
	public void initializeProvider(final ColorProvider aColorProvider) {

		IToken commonToken;

		pColorProvider = aColorProvider;
		if (pColorProvider == null) {
			pColorProvider = new ColorProvider();
		}

		pTokensRegistry = new HashMap<String, IToken>();

		// In-line modifiers
		pTokensRegistry.put(ITokenConstants.INLINE_LITERAL,
				getToken(new RGB(63, 127, 95), null, SWT.ITALIC));

		pTokensRegistry.put(ITokenConstants.INLINE_EMPHASIS_TEXT,
				getToken(new RGB(77, 77, 77), null, SWT.ITALIC));

		pTokensRegistry.put(ITokenConstants.INLINE_BOLD_TEXT,
				getToken(new RGB(77, 77, 77), null, SWT.BOLD));

		// Links, fields, roles
		commonToken = getToken(new RGB(0, 0, 128), null, SWT.ITALIC);
		pTokensRegistry.put(ITokenConstants.LINK, commonToken);
		pTokensRegistry.put(ITokenConstants.FIELD, commonToken);
		pTokensRegistry.put(ITokenConstants.ROLE, commonToken);

		// Link references
		commonToken = getToken(new RGB(0, 0, 128), null, SWT.BOLD);
		pTokensRegistry.put(ITokenConstants.LINK_REFERENCE, commonToken);
		pTokensRegistry.put(ITokenConstants.LINK_FOOTNOTE, commonToken);
		pTokensRegistry.put(ITokenConstants.SUBSTITUTION, commonToken);

		// Lists
		pTokensRegistry.put(ITokenConstants.LIST_BULLET,
				getToken(new RGB(45, 170, 45), null, SWT.ITALIC | SWT.BOLD));

		// Literals
		pTokensRegistry.put(ITokenConstants.LITERAL_DEFAULT, getToken(new RGB(
				73, 116, 230)));

		pTokensRegistry.put(ITokenConstants.LITERAL_DIRECTIVE,
				getToken(new RGB(0, 0, 255), null, SWT.BOLD));

		// Section
		pTokensRegistry.put(ITokenConstants.SECTION,
				getToken(new RGB(255, 165, 0), null, SWT.BOLD));

		// Code block
		pTokensRegistry.put(ITokenConstants.SOURCE,
				getToken(new RGB(63, 127, 95), null, SWT.NORMAL));

		// Table (grid and simple)
		pTokensRegistry.put(ITokenConstants.TABLE,
				getToken(new RGB(165, 42, 42)));
	}
}
