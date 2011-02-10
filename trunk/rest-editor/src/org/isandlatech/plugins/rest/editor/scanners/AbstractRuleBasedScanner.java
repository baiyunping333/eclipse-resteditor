/**
 * File:   AbstractRuleBasedScanner.java
 * Author: Thomas Calmant
 * Date:   10 févr. 2011
 */
package org.isandlatech.plugins.rest.editor.scanners;

import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;

/**
 * Utility class to ease scanner definition.
 * 
 * @author Thomas Calmant
 */
public abstract class AbstractRuleBasedScanner extends RuleBasedScanner {

	/** The rule provider */
	private RuleProvider pRuleProvider;

	/**
	 * Sets the rule provider
	 * 
	 * @param aRuleProvider
	 *            the rule provider
	 */
	public AbstractRuleBasedScanner(final RuleProvider aRuleProvider) {
		super();
		setRuleProvider(aRuleProvider);
		generateRules();
	}

	/**
	 * Generates the rules corresponding to the scanner. Don't forget to use
	 * {@link #setRules(List)} or {@link #setRules(IRule[])} at the end.
	 * 
	 * <b>Warning :</b> sort rules in descending marker length order
	 */
	protected abstract void generateRules();

	/**
	 * Retrieves the associated rule provider
	 * 
	 * @return the associated rule provider
	 */
	protected RuleProvider getRuleProvider() {
		return pRuleProvider;
	}

	/**
	 * Sets the rule provider
	 * 
	 * @param aRuleProvider
	 *            The rule provider
	 */
	public void setRuleProvider(final RuleProvider aRuleProvider) {
		pRuleProvider = aRuleProvider;
	}

	/**
	 * Configures the scanner with the given sequence of rules.
	 * 
	 * @param aRules
	 *            the sequence of rules controlling this scanner
	 * @see RuleBasedScanner#setRules(IRule[])
	 */
	public void setRules(final List<IRule> aRules) {
		IRule[] result = new IRule[aRules.size()];
		aRules.toArray(result);
		setRules(result);
	}
}
