/**
 * File:   RestLiteralBlockScanner.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

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
