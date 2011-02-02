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
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WordRule;
import org.isandlatech.plugins.rest.editor.rules.MarkupRule;
import org.isandlatech.plugins.rest.parser.RestLanguage;
import org.isandlatech.plugins.rest.parser.SingleWordDetector;

/**
 * @author Thomas Calmant
 * 
 */
public class RestLiteralBlockScanner extends RuleBasedScanner implements
		RestLanguage {

	public RestLiteralBlockScanner(final TokenProvider aTokenProvider) {
		super();

		// Create the tokens
		IToken defaultToken = aTokenProvider
				.getTokenForElement(TokenProvider.LITERAL_DEFAULT);

		IToken directiveToken = aTokenProvider
				.getTokenForElement(TokenProvider.LITERAL_DIRECTIVE);

		IToken linkFootnoteDefToken = aTokenProvider
				.getTokenForElement(TokenProvider.LINK_FOOTNOTE);

		IToken linkReferenceDefToken = aTokenProvider
				.getTokenForElement(TokenProvider.LINK_REFERENCE);

		// Default token
		setDefaultReturnToken(defaultToken);

		// Create the rules to identify tokens
		List<IRule> rules = new ArrayList<IRule>();

		WordRule directiveRule = new WordRule(new SingleWordDetector());
		for (String directive : RestLanguage.DIRECTIVES) {
			directiveRule.addWord(directive + "::", directiveToken);
		}

		rules.add(directiveRule);

		rules.add(new MarkupRule(LINK_FOOTNOTE_DEF_BEGIN,
				LINK_FOOTNOTE_DEF_END, linkFootnoteDefToken));

		rules.add(new MarkupRule(LINK_REFERENCE_DEF_BEGIN,
				LINK_REFERENCE_DEF_END, linkReferenceDefToken));

		// Pass the rules to the partitioner
		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}
