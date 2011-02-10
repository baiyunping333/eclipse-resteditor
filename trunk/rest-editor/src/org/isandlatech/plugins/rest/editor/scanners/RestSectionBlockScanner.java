/**
 * File:   RestSectionBlockScanner.java
 * Author: Thomas Calmant
 * Date:   26 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

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
