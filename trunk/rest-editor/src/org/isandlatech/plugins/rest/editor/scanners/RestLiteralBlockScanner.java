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
 * Literal block scanner.
 * 
 * @author Thomas Calmant
 */
public class RestLiteralBlockScanner extends AbstractRuleBasedScanner {

	/**
	 * Sets the rule provider
	 * 
	 * @param aRuleProvider
	 *            Rule provider
	 */
	public RestLiteralBlockScanner(final RuleProvider aRuleProvider) {
		super(aRuleProvider);
	}

	/**
	 * Prepares the literal block (comments, directives, ...) scanner, looking
	 * for :
	 * 
	 * <ul> <li>directives</li> <li>substitutions</li> <li>footnotes</li>
	 * <li>references</li> </ul>
	 */
	@Override
	protected void generateRules() {
		RuleProvider ruleProvider = getRuleProvider();

		// Create the token
		IToken defaultToken = ruleProvider.getTokenProvider()
				.getTokenForElement(ITokenConstants.LITERAL_DEFAULT);

		// Default token
		setDefaultReturnToken(defaultToken);

		// Create the rules to identify tokens
		List<IRule> rules = new ArrayList<IRule>();

		// Directives (ReST and Sphinx)
		rules.add(ruleProvider.getDirectives());

		// Substitutions
		rules.add(ruleProvider.getSubstitution());

		// Links definitions
		rules.add(ruleProvider.getLinkDefFootnote());
		rules.add(ruleProvider.getLinkDefReference());

		setRules(rules);
	}
}
