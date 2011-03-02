/**
 * File:   TokenProvider.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.scanners;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author Thomas Calmant
 * 
 */
public class TokenProvider {

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

		Display display = Display.getCurrent();
		Color foregroundColor = null;
		Color backgroundColor = null;

		if (aForeground != null) {
			foregroundColor = new Color(display, aForeground);
		}

		if (aBackground != null) {
			backgroundColor = new Color(display, aBackground);
		}

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

		// In-line modifiers
		if (aElement.equals(ITokenConstants.INLINE_LITERAL)) {
			return getToken(new RGB(63, 127, 95), null, SWT.ITALIC);
		}

		if (aElement.equals(ITokenConstants.INLINE_EMPHASIS_TEXT)) {
			return getToken(new RGB(77, 77, 77), null, SWT.ITALIC);
		}

		if (aElement.equals(ITokenConstants.INLINE_BOLD_TEXT)) {
			return getToken(new RGB(77, 77, 77), null, SWT.BOLD);
		}

		// Links
		if (aElement.equals(ITokenConstants.LINK)
				|| aElement.equals(ITokenConstants.FIELD)) {
			return getToken(new RGB(0, 0, 128), null, SWT.ITALIC);
		}

		if (aElement.equals(ITokenConstants.LINK_REFERENCE)
				|| aElement.equals(ITokenConstants.LINK_FOOTNOTE)
				|| aElement.equals(ITokenConstants.SUBSTITUTION)) {
			return getToken(new RGB(0, 0, 128), null, SWT.BOLD);
		}

		// Lists
		if (aElement.equals(ITokenConstants.LIST_BULLET)) {
			return getToken(new RGB(45, 170, 45), null, SWT.ITALIC | SWT.BOLD);
		}

		// Literals
		if (aElement.equals(ITokenConstants.LITERAL_DEFAULT)) {
			return getToken(new RGB(73, 116, 230));
		}

		if (aElement.equals(ITokenConstants.LITERAL_DIRECTIVE)) {
			return getToken(new RGB(0, 0, 255), null, SWT.BOLD);
		}

		// Section
		if (aElement.equals(ITokenConstants.SECTION)) {
			return getToken(new RGB(255, 165, 0), null, SWT.BOLD);
		}

		// Code block
		if (aElement.equals(ITokenConstants.SOURCE)) {
			return getToken(new RGB(63, 127, 95), null, SWT.NORMAL);
		}

		// Table (grid and simple)
		if (aElement.equals(ITokenConstants.TABLE)) {
			return getToken(new RGB(165, 42, 42));
		}

		// Role
		if (aElement.equals(ITokenConstants.ROLE)) {
			return getToken(new RGB(0, 0, 128), null, SWT.NORMAL);
		}

		// Default
		return getToken(new RGB(0, 0, 0));
	}
}
