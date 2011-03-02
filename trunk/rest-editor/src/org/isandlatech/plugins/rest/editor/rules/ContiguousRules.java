/**
 * File:   ContiguousRules.java
 * Author: Thomas Calmant
 * Date:   26 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class ContiguousRules extends AbstractRule {

	/** First rule to test */
	private IRule pFirstRule;

	/** Second rule to test */
	private IRule pSecondRule;

	/**
	 * Configures the rule
	 * 
	 * @param aFirstRule
	 *            First rule to test
	 * @param aSecondRule
	 *            Second rule to test (right after the previous one)
	 * @param aSuccessToken
	 *            Token to be returned on success
	 */
	public ContiguousRules(final IRule aFirstRule, final IRule aSecondRule,
			final IToken aSuccessToken) {
		super(aSuccessToken);

		pFirstRule = aFirstRule;
		pSecondRule = aSecondRule;
	}

	@Override
	public IToken evaluate(final ICharacterScanner aScanner) {

		MarkedCharacterScanner markedScanner = new MarkedCharacterScanner(
				aScanner);

		// Evaluate first rule
		IToken result = pFirstRule.evaluate(markedScanner);
		if (!isValidToken(result)) {
			return Token.UNDEFINED;
		}

		// Evaluate the second rule
		result = pSecondRule.evaluate(markedScanner);
		if (!isValidToken(result)) {
			// Return to the previous position
			markedScanner.reset();

			return Token.UNDEFINED;
		}

		return getSuccessToken();
	}
}
