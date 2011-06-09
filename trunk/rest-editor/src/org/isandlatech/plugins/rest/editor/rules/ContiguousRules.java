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

package org.isandlatech.plugins.rest.editor.rules;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * A rule composed of two successive rules, without separation. Validated only
 * if both rules are valid.
 * 
 * @author Thomas Calmant
 */
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
	public IToken evaluate(final MarkedCharacterScanner aScanner) {

		// Evaluate first rule
		IToken result = pFirstRule.evaluate(aScanner);
		if (!isValidToken(result)) {
			return Token.UNDEFINED;
		}

		// Evaluate the second rule
		result = pSecondRule.evaluate(aScanner);
		if (!isValidToken(result)) {
			return Token.UNDEFINED;
		}

		return getSuccessToken();
	}
}
