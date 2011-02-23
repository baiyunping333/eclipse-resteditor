/**
 * File:   AbstractRule.java
 * Author: Thomas Calmant
 * Date:   26 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import eclihx.ui.internal.ui.editors.ScannerController;

public abstract class AbstractRule implements IPredicateRule {

	/**
	 * Test if the given character can considered as an end of line character
	 * 
	 * @param codePoint
	 *            The character to be tested
	 * @return True if the given character is an EOL character
	 */
	public static boolean isAnEOL(final int codePoint) {
		return codePoint == '\n' || codePoint == '\r';
	}

	/**
	 * Tests if the given token is not null, not undefined and not a whitespace
	 * nor an EOF.
	 * 
	 * @param aToken
	 *            Token to be tested
	 * @return True if the token is valid
	 */
	public static boolean isValidToken(final IToken aToken) {
		return aToken != null && aToken.isOther();
	}

	/** THe success token */
	private IToken pSuccessToken;

	/**
	 * Basic rule configuration
	 * 
	 * @param aSuccessToken
	 *            Token to be returned on success
	 */
	public AbstractRule(final IToken aSuccessToken) {
		pSuccessToken = aSuccessToken;
	}

	@Override
	public abstract IToken evaluate(final ICharacterScanner aScanner);

	@Override
	public IToken evaluate(final ICharacterScanner aScanner,
			final boolean aResume) {

		if (!aResume) {
			// Standard mode
			return evaluate(aScanner);
		}

		// Resume mode
		// FIXME rewind the scanner to the start sequence
		return Token.UNDEFINED;
	}

	@Override
	public IToken getSuccessToken() {
		return pSuccessToken;
	}

	/**
	 * Sets the success token
	 * 
	 * @param aSuccessToken
	 *            The success token
	 */
	public void setSuccessToken(final IToken aSuccessToken) {
		pSuccessToken = aSuccessToken;
	}

	/**
	 * Resets the scanner and returns the undefined token
	 * 
	 * @param aScannerController
	 *            ICharacterScanner controller
	 * @return The token UNDEFINED
	 */
	protected IToken undefinedToken(final ScannerController aScannerController) {
		if (aScannerController != null) {
			aScannerController.unreadAll();
		}
		return Token.UNDEFINED;
	}
}
