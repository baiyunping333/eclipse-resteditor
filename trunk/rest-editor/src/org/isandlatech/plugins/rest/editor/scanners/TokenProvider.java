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

	/** Default element */
	public static final String DEFAULT = "default";

	/** In-line bold text element **/
	public static final String INLINE_BOLD_TEXT = "inline.text.bold";

	/** In-line italic text element **/
	public static final String INLINE_EMPHASIS_TEXT = "inline.text.emphasis";

	/** In-line literal element **/
	public static final String INLINE_LITERAL = "inline.literal";

	/** Link element */
	public static final String LINK = "link";

	/** Footnote */
	public static final String LINK_FOOTNOTE = "link.footnote";

	/** Reference */
	public static final String LINK_REFERENCE = "link.reference";

	/** List bullet */
	public static final String LIST_BULLET = "list.bullet";

	/** Literal block default element */
	public static final String LITERAL_DEFAULT = "literal.default";

	/** Literal block directive */
	public static final String LITERAL_DIRECTIVE = "literal.directive";

	/** Section block element */
	public static final String SECTION = "section";

	/** Substitution */
	public static final String SUBSTITUTION = "substitution";

	/** Source block */
	public static final String SOURCE = "source";

	/** Table block element */
	public static final String TABLE = "table";

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

		if (aElement.equals(INLINE_LITERAL)) {
			return getToken(new RGB(63, 127, 95), null, SWT.ITALIC);
		}

		if (aElement.equals(INLINE_EMPHASIS_TEXT)) {
			return getToken(new RGB(77, 77, 77), null, SWT.ITALIC);
		}

		if (aElement.equals(INLINE_BOLD_TEXT)) {
			return getToken(new RGB(77, 77, 77), null, SWT.BOLD);
		}

		if (aElement.equals(LINK)) {
			return getToken(new RGB(0, 0, 128), null, SWT.ITALIC);
		}

		if (aElement.equals(LINK_REFERENCE) || aElement.equals(LINK_FOOTNOTE)) {
			return getToken(new RGB(0, 0, 128), null, SWT.BOLD);
		}

		if (aElement.equals(LIST_BULLET)) {
			return getToken(new RGB(45, 170, 45), null, SWT.ITALIC | SWT.BOLD);
		}

		if (aElement.equals(LITERAL_DEFAULT)) {
			return getToken(new RGB(73, 116, 230));
		}

		if (aElement.equals(LITERAL_DIRECTIVE)) {
			return getToken(new RGB(0, 0, 255), null, SWT.BOLD);
		}

		if (aElement.equals(SECTION)) {
			return getToken(new RGB(255, 165, 0), null, SWT.BOLD);
		}

		if (aElement.equals(SOURCE)) {
			return getToken(new RGB(63, 127, 95), null, SWT.NORMAL);
		}

		if (aElement.equals(TABLE)) {
			return getToken(new RGB(165, 42, 42));
		}

		return getToken(new RGB(0, 0, 0));
	}
}
