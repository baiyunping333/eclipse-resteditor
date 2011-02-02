/**
 * File:   DeclarativeProposalProcessor.java
 * Author: Thomas Calmant
 * Date:   2 févr. 2011
 */
package org.isandlatech.plugins.rest.editor.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.isandlatech.plugins.rest.parser.RestLanguage;

/**
 * @author Thomas Calmant
 * 
 */
public class DeclarativeProposalProcessor extends AbstractProposalProcessor {

	/**
	 * Builds an array of completion proposals. Adds a suffix to each
	 * suggestion.
	 * 
	 * @param aSuggestions
	 *            List of suggestions
	 * @param aReplacedWord
	 *            Replaced word
	 * @param aOffset
	 *            Offset of the beginning of the replaced word
	 * @return An array of completion proposals, null on error
	 */
	@Override
	protected ICompletionProposal[] buildProposals(
			final List<String> aSuggestions, final String aReplacedWord,
			final int aOffset) {
		if (aSuggestions == null || aSuggestions.size() == 0) {
			return null;
		}

		ICompletionProposal[] proposals = new ICompletionProposal[aSuggestions
				.size()];

		int i = 0;
		for (String suggestion : aSuggestions) {

			// Suggestion with a suffix
			String replacementWord = suggestion + ":: ";

			// Proposal description
			IContextInformation contextInfo = new ContextInformation(
					"A ReST declarative keyword", suggestion);

			// Completion proposal
			proposals[i++] = new CompletionProposal(replacementWord, aOffset,
					aReplacedWord.length(), replacementWord.length(), null,
					suggestion, contextInfo, suggestion);
		}

		return proposals;
	}

	/**
	 * Builds a list containing all ReST directives starting with the given word
	 * 
	 * @param aWord
	 *            Directive prefix
	 * @return All directives starting with the given word, or an empty list
	 */
	@Override
	protected List<String> buildSuggestions(final String aWord) {
		List<String> result = new ArrayList<String>();

		for (String keyword : RestLanguage.DIRECTIVES) {
			if (keyword.startsWith(aWord)) {
				result.add(keyword);
			}
		}

		Collections.sort(result);
		return result;
	}
}
