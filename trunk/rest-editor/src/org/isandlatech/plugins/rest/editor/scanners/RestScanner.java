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
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.isandlatech.plugins.rest.editor.rules.AbstractRule;
import org.isandlatech.plugins.rest.editor.rules.ContiguousRules;
import org.isandlatech.plugins.rest.editor.rules.ExactStringRule;
import org.isandlatech.plugins.rest.editor.rules.MarkupRule;
import org.isandlatech.plugins.rest.parser.RestLanguage;

/**
 * @author Thomas Calmant
 * 
 */
public class RestScanner extends RuleBasedScanner implements RestLanguage {

	/**
	 * Sets up the scanner rules
	 */
	public RestScanner(final TokenProvider aTokenProvider) {
		super();

		// Create the tokens
		IToken defaultToken = aTokenProvider
				.getTokenForElement(TokenProvider.DEFAULT);

		IToken fieldToken = aTokenProvider
				.getTokenForElement(TokenProvider.LINK);

		IToken boldToken = aTokenProvider
				.getTokenForElement(TokenProvider.INLINE_BOLD_TEXT);

		IToken emphasisToken = aTokenProvider
				.getTokenForElement(TokenProvider.INLINE_EMPHASIS_TEXT);

		IToken inlineLiteralToken = aTokenProvider
				.getTokenForElement(TokenProvider.INLINE_LITERAL);

		IToken linkToken = aTokenProvider
				.getTokenForElement(TokenProvider.LINK);

		IToken linkFootnoteToken = aTokenProvider
				.getTokenForElement(TokenProvider.LINK_FOOTNOTE);

		IToken linkReferenceToken = aTokenProvider
				.getTokenForElement(TokenProvider.LINK_REFERENCE);

		IToken listBulletToken = aTokenProvider
				.getTokenForElement(TokenProvider.LIST_BULLET);

		// Default token
		setDefaultReturnToken(defaultToken);

		// Create the rules to identify tokens
		List<IRule> rules = new ArrayList<IRule>();

		// Field marker (same as role marker)
		rules.add(new MarkupRule(FIELD_MARKER, fieldToken));

		// Role rule (used 2 times)
		MarkupRule roleRule = new MarkupRule(ROLE_MARKER, boldToken);

		/** Warning : sort rules in descending marker length order */
		{
			// Role + content rule
			MarkupRule contentRule = new MarkupRule("`",
					AbstractRule.DUMMY_TOKEN);

			rules.add(new ContiguousRules(roleRule, contentRule, linkToken));
		}

		// Simple role marker
		rules.add(roleRule);

		rules.add(new MarkupRule(BOLD_MARKER, boldToken));

		rules.add(new MarkupRule(EMPHASIS_MARKER, emphasisToken));

		rules.add(new MarkupRule(INLINE_LITERAL_MARKER, inlineLiteralToken));

		for (String marker : LIST_MARKERS) {
			rules.add(new ExactStringRule(marker, 0, true, listBulletToken));
		}

		rules.add(new MarkupRule(LINK_BEGIN, LINK_END, linkToken));

		rules.add(new MarkupRule(LINK_FOOTNOTE_BEGIN, LINK_FOOTNOTE_END,
				linkFootnoteToken));

		rules.add(new MarkupRule(LINK_REFERENCE_BEGIN, LINK_REFERENCE_END,
				linkReferenceToken));

		// Pass the rules to the partitioner
		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}
