/**
 * File:   RestScanner.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

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
	 * <ul> <li>fields</li> <li>substitutions</li> <li>inline bold</li>
	 * <li>inline emphasis</li> <li>inline literal</li> <li>links</li>
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

		// Field
		rules.add(ruleProvider.getField());

		// Roles
		rules.add(ruleProvider.getRoleWithContentRule());
		rules.add(ruleProvider.getRoleRule());

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
