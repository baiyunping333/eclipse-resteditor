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

package org.isandlatech.plugins.rest.editor.userassist.contentassist;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * Utility class for content assistant proposal generation.
 * 
 * @author Thomas Calmant
 */
public abstract class AbstractProposalProcessor implements
		IContentAssistProcessor {

	/**
	 * Builds an array of completion proposals.
	 * 
	 * @param aDocument
	 * 
	 * @param aSuggestions
	 *            A suggestion -> description mapping
	 * @param aReplacedWord
	 *            Replaced word
	 * @param aOffset
	 *            Offset of the beginning of the replaced word
	 * @return An array of completion proposals, null on error
	 */
	protected ICompletionProposal[] buildProposals(final IDocument aDocument,
			final Map<String, String> aSuggestions, final String aReplacedWord,
			final int aOffset) {

		if (aSuggestions == null || aSuggestions.size() == 0) {
			return null;
		}

		ICompletionProposal[] proposals = new ICompletionProposal[aSuggestions
				.size()];

		int i = 0;
		for (Entry<String, String> suggestionEntry : aSuggestions.entrySet()) {
			String suggestion = suggestionEntry.getKey();
			String description = suggestionEntry.getValue();

			if (description == null) {
				description = suggestion;
			}

			// Completion proposal
			proposals[i++] = new CompletionProposal(suggestion, aOffset,
					aReplacedWord.length(), suggestion.length(), null,
					suggestion, null, description);
		}

		return proposals;
	}

	/**
	 * Builds a list containing all possible words starting with the given word
	 * 
	 * @param aWord
	 *            Word to complete (can be empty)
	 * @return All directives starting with the given word, or an empty map
	 */
	protected abstract Map<String, String> buildSuggestions(final String aWord);

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

			Map<String, String> suggestions = buildSuggestions(currentWord);
			return buildProposals(document, suggestions, currentWord, aOffset
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
