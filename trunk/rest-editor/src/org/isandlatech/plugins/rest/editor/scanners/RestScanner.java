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
 * Scanner for undefined partitions (default text)
 * 
 * @author Thomas Calmant
 */
public class RestScanner extends AbstractRuleBasedScanner {

	/**
	 * Sets the rule provider.
	 * 
	 * @param aRuleProvider
	 *            The rule provider
	 */
	public RestScanner(final RuleProvider aRuleProvider) {
		super(aRuleProvider);
	}

	/**
	 * Sets up the plain-text scanner rules. Looks for :
	 * 
	 * <ul> <li>fields</li> <li>substitutions</li> <li>in-line bold</li>
	 * <li>in-line emphasis</li> <li>in-line literal</li> <li>links</li>
	 * <li>footnote links</li> <li>reference links</li> <li>lists /
	 * enumerations</li> </ul>
	 */
	@Override
	protected void generateRules() {
		RuleProvider ruleProvider = getRuleProvider();

		// Create the token
		IToken defaultToken = ruleProvider.getTokenProvider()
				.getTokenForElement(ITokenConstants.DEFAULT);

		// Default token
		setDefaultReturnToken(defaultToken);

		// Create the rules to identify tokens
		List<IRule> rules = new ArrayList<IRule>();

		// Roles
		rules.add(ruleProvider.getRoleWithContentRule());
		rules.add(ruleProvider.getRoleRule());

		// Field
		rules.add(ruleProvider.getField());

		// In-line modifiers
		rules.add(ruleProvider.getInlineBold());
		rules.add(ruleProvider.getInlineEmphasis());
		rules.add(ruleProvider.getInlineLiteral());

		// Lists
		rules.addAll(ruleProvider.getListRules());

		// Links
		rules.add(ruleProvider.getLink());
		rules.add(ruleProvider.getLinkFootnote());
		rules.add(ruleProvider.getLinkReference());

		// Substitutions
		rules.add(ruleProvider.getSubstitution());

		setRules(rules);
	}
}
