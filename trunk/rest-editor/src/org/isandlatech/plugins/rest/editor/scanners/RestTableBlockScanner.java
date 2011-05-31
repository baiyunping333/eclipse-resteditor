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
 * Grid and simple tables scanner
 * 
 * @author Thomas Calmant
 */
public class RestTableBlockScanner extends AbstractRuleBasedScanner {

	/**
	 * Simple constructor
	 * 
	 * @param aRuleProvider
	 *            The rule provider
	 */
	public RestTableBlockScanner(final RuleProvider aRuleProvider) {
		super(aRuleProvider);
	}

	/**
	 * Sets the scanner rules. Looks for :
	 * 
	 * <ul> <li>inline formating</li> <li>substitutions</li> </ul>
	 */
	@Override
	protected void generateRules() {
		RuleProvider ruleProvider = getRuleProvider();

		// Create the tokens
		IToken defaultToken = ruleProvider.getTokenProvider()
				.getTokenForElement(ITokenConstants.TABLE);

		// Default token
		setDefaultReturnToken(defaultToken);

		// Create the rules to identify tokens
		List<IRule> rules = new ArrayList<IRule>();

		// In line modifiers
		rules.add(ruleProvider.getInlineBold());
		rules.add(ruleProvider.getInlineEmphasis());
		rules.add(ruleProvider.getInlineLiteral());

		// Substitution marker
		rules.add(ruleProvider.getSubstitution());

		// Pass the rules to the partitioner
		setRules(rules);
	}
}
