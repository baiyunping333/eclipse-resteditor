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

/**
 * Utility class to simplify new rules declaration
 * 
 * @author Thomas Calmant
 */
public abstract class AbstractRule implements IPredicateRule {

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
	public IToken evaluate(final ICharacterScanner aScanner) {

		MarkedCharacterScanner markedScanner = new MarkedCharacterScanner(
				aScanner);
		IToken result = evaluate(markedScanner);

		if (result.isUndefined()) {
			markedScanner.reset();
		}

		return result;
	}

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

	/**
	 * Evaluates the token at the given scanner position. If the result token is
	 * undefined, the scanner will be automatically reset.
	 * 
	 * @param aScanner
	 *            A character scanner
	 * @return The evaluated token, or an undefined one
	 */
	public abstract IToken evaluate(final MarkedCharacterScanner aScanner);

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
}
