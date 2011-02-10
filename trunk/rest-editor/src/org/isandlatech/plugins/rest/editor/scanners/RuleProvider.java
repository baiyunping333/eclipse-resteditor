/**
 * File:   RuleProvider.java
 * Author: Thomas Calmant
 * Date:   10 févr. 2011
 */
package org.isandlatech.plugins.rest.editor.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WordRule;
import org.isandlatech.plugins.rest.editor.rules.ContiguousRules;
import org.isandlatech.plugins.rest.editor.rules.ExactStringRule;
import org.isandlatech.plugins.rest.editor.rules.MarkupRule;
import org.isandlatech.plugins.rest.parser.RestLanguage;
import org.isandlatech.plugins.rest.parser.SingleWordDetector;

/**
 * Provides rules for scanners
 * 
 * @author Thomas Calmant
 */
public class RuleProvider {

	/** Directives */
	private WordRule pDirectives;

	/** Field */
	private MarkupRule pField;

	/** In-line bold text */
	private MarkupRule pInlineBold;

	/** In-line emphasis text */
	private MarkupRule pInlineEmphasis;

	/** In-line literal text */
	private MarkupRule pInlineLiteral;

	/** Simple link */
	private MarkupRule pLink;

	/** Footnote link definition */
	private MarkupRule pLinkDefFootnote;

	/** Reference link definition */
	private MarkupRule pLinkDefReference;

	/** Footnote link */
	private MarkupRule pLinkFootnote;

	/** Reference link */
	private MarkupRule pLinkReference;

	/** Lists */
	private List<ExactStringRule> pLists;

	/** Role usage / definition */
	private MarkupRule pRoleRule;

	/** Role with content */
	private ContiguousRules pRoleWithContentRule;

	/** Substitution */
	private MarkupRule pSubstitution;

	/** Token provider */
	private TokenProvider pTokenProvider;

	/**
	 * Prepares the rule provider
	 * 
	 * @param aTokenProvider
	 *            Token provider
	 */
	public RuleProvider(final TokenProvider aTokenProvider) {
		pTokenProvider = aTokenProvider;
	}

	/**
	 * Retrieves the literal directive (ReST and Sphinx) rule
	 * 
	 * @return the directive rule
	 */
	public WordRule getDirectives() {
		if (pDirectives == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.LITERAL_DIRECTIVE);

			pDirectives = new WordRule(new SingleWordDetector());
			for (String directive : RestLanguage.DIRECTIVES) {
				pDirectives.addWord(directive + "::", token);
			}

			for (String directive : RestLanguage.SPHINX_DIRECTIVES) {
				pDirectives.addWord(directive + "::", token);
			}
		}

		return pDirectives;
	}

	/**
	 * Retrieves the field usage rule
	 * 
	 * @return the field usage rule
	 */
	public MarkupRule getField() {
		if (pField == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.FIELD);

			pField = new MarkupRule(RestLanguage.FIELD_MARKER, token);
		}

		return pField;
	}

	/**
	 * Retrieves the in-line bold text rule
	 * 
	 * @return The in-line bold text rule
	 */
	public MarkupRule getInlineBold() {
		if (pInlineBold == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.INLINE_BOLD_TEXT);

			pInlineBold = new MarkupRule(RestLanguage.BOLD_MARKER, token);
		}

		return pInlineBold;
	}

	/**
	 * Retrieves the in-line emphasis text rule
	 * 
	 * @return the in-line emphasis text rule
	 */
	public MarkupRule getInlineEmphasis() {
		if (pInlineEmphasis == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.INLINE_EMPHASIS_TEXT);

			pInlineEmphasis = new MarkupRule(RestLanguage.EMPHASIS_MARKER,
					token);
		}

		return pInlineEmphasis;
	}

	/**
	 * Retrieves the in-line literal rule
	 * 
	 * @return the in-line literal rule
	 */
	public MarkupRule getInlineLiteral() {
		if (pInlineLiteral == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.INLINE_LITERAL);

			pInlineLiteral = new MarkupRule(RestLanguage.INLINE_LITERAL_MARKER,
					token);
		}

		return pInlineLiteral;
	}

	/**
	 * Retrieves the link rule
	 * 
	 * @return the link rule
	 */
	public MarkupRule getLink() {
		if (pLink == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.LINK);

			pLink = new MarkupRule(RestLanguage.LINK_BEGIN,
					RestLanguage.LINK_END, token);
		}

		return pLink;
	}

	/**
	 * Retrieves the footnote link definition rule
	 * 
	 * @return the footnote link definition rule
	 */
	public MarkupRule getLinkDefFootnote() {
		if (pLinkDefFootnote == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.LINK_FOOTNOTE);

			pLinkDefFootnote = new MarkupRule(
					RestLanguage.LINK_FOOTNOTE_DEF_BEGIN,
					RestLanguage.LINK_FOOTNOTE_DEF_END, token);
		}

		return pLinkDefFootnote;
	}

	/**
	 * Retrieves the reference link definition rule
	 * 
	 * @return the reference link definition rule
	 */
	public MarkupRule getLinkDefReference() {
		if (pLinkDefReference == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.LINK_REFERENCE);

			pLinkDefReference = new MarkupRule(
					RestLanguage.LINK_REFERENCE_DEF_BEGIN,
					RestLanguage.LINK_REFERENCE_DEF_END, token);
		}

		return pLinkDefReference;
	}

	/**
	 * Retrieves the footnote link rule
	 * 
	 * @return the footnote link rule
	 */
	public MarkupRule getLinkFootnote() {
		if (pLinkFootnote == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.LINK_FOOTNOTE);

			pLinkFootnote = new MarkupRule(RestLanguage.LINK_FOOTNOTE_BEGIN,
					RestLanguage.LINK_FOOTNOTE_END, token);
		}

		return pLinkFootnote;
	}

	/**
	 * Retrieves the reference link rule
	 * 
	 * @return the reference link rule
	 */
	public MarkupRule getLinkReference() {
		if (pLinkReference == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.LINK_REFERENCE);

			pLinkReference = new MarkupRule(RestLanguage.LINK_REFERENCE_BEGIN,
					RestLanguage.LINK_REFERENCE_END, token);
		}

		return pLinkReference;
	}

	/**
	 * Retrieves all rules detecting a list / enumeration
	 * 
	 * @return list of all rules detecting a list / enumeration
	 */
	public List<ExactStringRule> getListRules() {
		if (pLists == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.LIST_BULLET);

			pLists = new ArrayList<ExactStringRule>();

			for (String marker : RestLanguage.LIST_MARKERS) {
				pLists.add(new ExactStringRule(marker, 0, true, token));
			}
		}

		return pLists;
	}

	/**
	 * Retrieves the rule detecting a role without content
	 * 
	 * @return the rule detecting a role
	 */
	public MarkupRule getRoleRule() {
		if (pRoleRule == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.ROLE);

			pRoleRule = new MarkupRule(RestLanguage.ROLE_MARKER, token);
		}

		return pRoleRule;
	}

	/**
	 * Retrieves the rule detecting a role with some content
	 * 
	 * @return the rule detecting a role with some content
	 */
	public IRule getRoleWithContentRule() {
		if (pRoleWithContentRule == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.ROLE);

			IRule roleRule = getRoleRule();
			MarkupRule contentRule = new MarkupRule(
					RestLanguage.ROLE_CONTENT_MARKER,
					ITokenConstants.DUMMY_TOKEN);

			pRoleWithContentRule = new ContiguousRules(roleRule, contentRule,
					token);
		}

		return pRoleWithContentRule;
	}

	/**
	 * Retrieves the substitution text rule
	 * 
	 * @return the substitution text rule
	 */
	public MarkupRule getSubstitution() {
		if (pSubstitution == null) {
			IToken token = pTokenProvider
					.getTokenForElement(ITokenConstants.SUBSTITUTION);

			pSubstitution = new MarkupRule(RestLanguage.SUBSTITUTION_MARKER,
					token);
		}

		return pSubstitution;
	}

	/**
	 * Retrieves the associated token provider
	 * 
	 * @return the associated token provider
	 */
	public TokenProvider getTokenProvider() {
		return pTokenProvider;
	}
}
