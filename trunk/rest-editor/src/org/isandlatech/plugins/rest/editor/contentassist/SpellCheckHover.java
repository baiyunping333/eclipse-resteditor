/**
 * File:   SpellCheckHover.java
 * Author: Thomas Calmant
 * Date:   4 mars 2011
 */
package org.isandlatech.plugins.rest.editor.contentassist;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

/**
 * Misspelled text correction assistant
 * 
 * @author Thomas Calmant
 */
public class SpellCheckHover implements ITextHover {

	/** Spelling engine to use */
	private ISpellingEngine pSpellingEngine;

	/** Spelling context to be used (standard text) */
	private final SpellingContext pSpellingContext;

	/**
	 * Prepares the spell check hover
	 * 
	 * @param aSpellingEngine
	 *            Spelling engine to use
	 */
	public SpellCheckHover(final ISpellingEngine aSpellingEngine) {
		pSpellingEngine = aSpellingEngine;

		// Prepare the spelling context
		String contentTypeString = IContentTypeManager.CT_TEXT;
		IContentType contentType = Platform.getContentTypeManager()
				.getContentType(contentTypeString);

		pSpellingContext = new SpellingContext();
		pSpellingContext.setContentType(contentType);
	}

	@Override
	public String getHoverInfo(final ITextViewer aTextViewer,
			final IRegion aHoverRegion) {

		IDocument document = aTextViewer.getDocument();
		String correctionProposals = "";

		try {
			SpellingProblemCollector collector = new SpellingProblemCollector();

			pSpellingEngine.check(document, new IRegion[] { aHoverRegion },
					pSpellingContext, collector, null);

			List<SpellingProblem> foundProblems = collector.getProblems();

			for (SpellingProblem problem : foundProblems) {
				correctionProposals += problem.getMessage() + " :\n";

				for (ICompletionProposal proposal : problem.getProposals()) {
					correctionProposals += proposal.getDisplayString() + "\n";
				}
			}

		} catch (NullPointerException ex) {
			System.err.println("Error while using the spell checker");
			ex.printStackTrace();
		}

		// No valid proposals
		if (correctionProposals.isEmpty()) {
			return null;
		}

		return correctionProposals;
	}

	@Override
	public IRegion getHoverRegion(final ITextViewer aTextViewer,
			final int aOffset) {

		IDocument document = aTextViewer.getDocument();
		String hoverLine;
		int lineOffset;
		int offsetInLine;

		// Get hovered word's line information
		try {
			int line = document.getLineOfOffset(aOffset);
			lineOffset = document.getLineOffset(line);
			int lineLength = document.getLineLength(line);

			hoverLine = document.get(lineOffset, lineLength);
			offsetInLine = aOffset - lineOffset;

		} catch (BadLocationException e) {
			// Don't say anything
			return new Region(aOffset, 0);
		}

		// Extract the hovered word
		// The +1 suppresses the space from the substring, and uses
		// the beginning of the line if no space was found
		int beginWord = hoverLine.lastIndexOf(' ', offsetInLine) + 1;
		int endWord = hoverLine.indexOf(' ', offsetInLine);

		// The word ends with the line
		if (endWord == -1) {
			endWord = hoverLine.length() - 1;
		}

		if (endWord <= 0 || endWord < beginWord) {
			// Empty word ?
			return new Region(aOffset, 0);
		}

		// Work on the word
		String word = hoverLine.substring(beginWord, endWord);
		int offsetInWord = offsetInLine - beginWord;

		// Only select letters
		int beginRealWord = offsetInWord;
		int endRealWord = offsetInWord;
		char[] wordArray = word.toCharArray();

		for (int i = offsetInWord; i > 0; i--) {

			if (!Character.isLetter(wordArray[i])) {
				break;
			}

			beginRealWord--;
		}

		for (int i = offsetInWord; i < wordArray.length; i++) {
			if (!Character.isLetter(wordArray[i])) {
				break;
			}

			endRealWord++;
		}

		// Backward movement correction
		if (!Character.isLetter(word.charAt(beginRealWord))) {
			beginRealWord++;
		}

		if (beginRealWord == endRealWord) {
			return new Region(aOffset, 0);
		}

		return new Region(lineOffset + beginWord + beginRealWord, endRealWord
				- beginRealWord);
	}
}
