/**
 * File:   AbstractProposalProcessor.java
 * Author: Thomas Calmant
 * Date:   2 févr. 2011
 */
package org.isandlatech.plugins.rest.editor.contentassist;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * @author Thomas Calmant
 * 
 */
public abstract class AbstractProposalProcessor implements
		IContentAssistProcessor {

	/**
	 * Builds an array of completion proposals.
	 * 
	 * @param aSuggestions
	 *            List of suggestions
	 * @param aReplacedWord
	 *            Replaced word
	 * @param aOffset
	 *            Offset of the beginning of the replaced word
	 * @return An array of completion proposals, null on error
	 */
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

			// Completion proposal
			proposals[i++] = new CompletionProposal(suggestion, aOffset,
					aReplacedWord.length(), suggestion.length(), null,
					suggestion, null, suggestion);
		}

		return proposals;
	}

	/**
	 * Builds a list containing all possible words starting with the given word
	 * 
	 * @param aWord
	 *            Word to complete (can be empty)
	 * @return All directives starting with the given word, or an empty list
	 */
	protected abstract List<String> buildSuggestions(final String aWord);

	/**
	 * Based on M. Baron's code
	 */
	@Override
	public ICompletionProposal[] computeCompletionProposals(
			final ITextViewer aViewer, final int aOffset) {

		IDocument document = aViewer.getDocument();
		int currentOffset = aOffset - 1;

		String currentWord = "";
		char currentChar;

		try {
			while (currentOffset > 0
					&& !Character.isWhitespace(currentChar = document
							.getChar(currentOffset))) {
				currentWord = currentChar + currentWord;
				currentOffset--;
			}

			List<String> suggestions = buildSuggestions(currentWord);
			return buildProposals(suggestions, currentWord, aOffset
					- currentWord.length());

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public IContextInformation[] computeContextInformation(
			final ITextViewer aViewer, final int aOffset) {
		// Do nothing
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		// Do nothing
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		// Do nothing
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		// Do nothing
		return null;
	}

	@Override
	public String getErrorMessage() {
		// Do nothing
		return null;
	}
}
