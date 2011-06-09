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

package org.isandlatech.plugins.rest.editor.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.isandlatech.plugins.rest.editor.providers.RuleProvider;

/**
 * Section block scanner.
 * 
 * @author Thomas Calmant
 */
public class RestSectionBlockScanner extends AbstractRuleBasedScanner {

	/**
	 * Sets the rule provider
	 * 
	 * @param aRuleProvider
	 *            the rule provider
	 */
	public RestSectionBlockScanner(final RuleProvider aRuleProvider) {
		super(aRuleProvider);
	}

	/**
	 * Sets the scanner rules. Looks for :
	 * 
	 * <ul> <li>substitutions</li> </ul>
	 */
	@Override
	protected void generateRules() {
		RuleProvider ruleProvider = getRuleProvider();

		// Create the tokens
		IToken defaultToken = ruleProvider.getTokenProvider()
				.getTokenForElement(ITokenConstants.SECTION);

		// Default token
		setDefaultReturnToken(defaultToken);

		// Create the rules to identify tokens
		List<IRule> rules = new ArrayList<IRule>();
		rules.add(ruleProvider.getSubstitution());
		setRules(rules);
	}
}
