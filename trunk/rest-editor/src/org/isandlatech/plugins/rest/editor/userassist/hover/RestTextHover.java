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

package org.isandlatech.plugins.rest.editor.userassist.hover;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;
import org.isandlatech.plugins.rest.RestPlugin;
import org.isandlatech.plugins.rest.editor.scanners.RestPartitionScanner;
import org.isandlatech.plugins.rest.editor.ui.RestInformationPresenter;
import org.isandlatech.plugins.rest.editor.userassist.BasicInternalLinkHandler;
import org.isandlatech.plugins.rest.editor.userassist.HelpMessagesUtil;
import org.isandlatech.plugins.rest.editor.userassist.IInternalLinkListener;
import org.isandlatech.plugins.rest.editor.userassist.InternalHoverData;

/**
 * Text assistant : spell checker or directive helper
 * 
 * @author Thomas Calmant
 */
public class RestTextHover implements ITextHover, ITextHoverExtension,
		ITextHoverExtension2 {

	/** Hover link handler */
	private final IInternalLinkListener pInternalLinkListener;

	/** Last generated hover data */
	private InternalHoverData pLastHoverData;

	/** Spell checking flag */
	private boolean pSpellCheckingEnabled;

	/** Spelling context to be used (standard text) */
	private final SpellingContext pSpellingContext;

	/** Spelling engine to use */
	private final ISpellingEngine pSpellingEngine;

	/**
	 * Prepares the spell check hover
	 * 
	 * @param aSpellingEngine
	 *            Spelling engine to use
	 */
	public RestTextHover(final ISpellingEngine aSpellingEngine) {

		pSpellingEngine = aSpellingEngine;
		pSpellCheckingEnabled = pSpellingEngine != null;

		// Prepare the spelling context
		String contentTypeString = IContentTypeManager.CT_TEXT;
		IContentType contentType = Platform.getContentTypeManager()
				.getContentType(contentTypeString);

		pSpellingContext = new SpellingContext();
		pSpellingContext.setContentType(contentType);

		// Internal link handler
		pInternalLinkListener = new BasicInternalLinkHandler();
	}

	/**
	 * Enables or disables the spell checking while hovering.
	 * 
	 * @param aEnable
	 *            True to enable spell checking, else false.
	 */
	public void enableSpellChecking(final boolean aEnable) {
		pSpellCheckingEnabled = aEnable;
	}

	/**
	 * Generates the hovered directive help message, if any.
	 * 
	 * @param aDocument
	 *            Current document
	 * @param aHoverRegion
	 *            Hovered document region
	 * 
	 * @return The directive help string, null if it isn't a directive
	 */
	protected String generateDirectiveHelp(final IDocument aDocument,
			final IRegion aHoverRegion) {

		// Get the partitioner
		IDocumentPartitioner partitioner;
		if (aDocument instanceof IDocumentExtension3) {
			IDocumentExtension3 doc3 = (IDocumentExtension3) aDocument;
			partitioner = doc3
					.getDocumentPartitioner(RestPartitionScanner.PARTITIONING);
		} else {
			partitioner = aDocument.getDocumentPartitioner();
		}

		// No partitioner : no test
		if (partitioner == null) {
			return null;
		}

		// Test if we are in a literal block
		String contentType = partitioner.getContentType(aHoverRegion
				.getOffset());

		if (!RestPartitionScanner.LITERAL_BLOCK.equals(contentType)) {
			return null;
		}

		String directive;
		try {
			directive = aDocument.get(aHoverRegion.getOffset(),
					aHoverRegion.getLength() + 2);

		} catch (BadLocationException e) {
			// Invalid range (maybe because of "+2")
			return null;
		}

		// Only catch directives
		if (!directive.endsWith("::")) {
			return null;
		}

		// Remove trailing "::"
		directive = directive.substring(0, directive.length() - 2).trim();

		if (directive.isEmpty()) {
			return null;
		}

		return HelpMessagesUtil.getDirectiveHelp(directive);
	}

	/**
	 * Generates a HTML snippet with spelling corrections proposals. Returns
	 * null if no proposal is available
	 * 
	 * @param aDocument
	 *            The current document
	 * @param aHoverRegion
	 *            The hovered region
	 * @return An HTML snippet with correction links, or null
	 */
	protected String generateSpellingProposals(final IDocument aDocument,
			final IRegion aHoverRegion) {

		if (!pSpellCheckingEnabled || pSpellingEngine == null) {
			return null;
		}

		String correctionProposals = "";

		try {
			SpellingProblemCollector collector = new SpellingProblemCollector();

			pSpellingEngine.check(aDocument, new IRegion[] { aHoverRegion },
					pSpellingContext, collector, null);

			List<SpellingProblem> foundProblems = collector.getProblems();

			for (SpellingProblem problem : foundProblems) {

				if (problem == null) {
					continue;
				}

				correctionProposals += "<h1>" + problem.getMessage()
						+ " :</h1>\n";

				for (ICompletionProposal proposal : problem.getProposals()) {

					if (proposal == null) {
						continue;
					}

					final String displayedString = proposal.getDisplayString();
					correctionProposals += "<a href=\""
							+ BasicInternalLinkHandler.makeSpellLink(problem,
									proposal) + "\">" + displayedString
							+ "</a>" + "<br />\n";
				}
			}

		} catch (NullPointerException ex) {
			RestPlugin.logError("Error while using the spell checker", ex);
		}

		// No valid proposals
		if (correctionProposals.isEmpty()) {
			return null;
		}

		return correctionProposals;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 */
	@Override
	public IInformationControlCreator getHoverControlCreator() {

		// Store the last generated hover data (can be null)
		final InternalHoverData associatedData = pLastHoverData;

		return new IInformationControlCreator() {

			@Override
			public IInformationControl createInformationControl(
					final Shell aParent) {

				// Prepare the presenter
				final RestInformationPresenter restPresenter = new RestInformationPresenter(
						associatedData);

				// Prepare the information control
				final IInformationControl informationControl = new DefaultInformationControl(
						aParent, EditorsUI.getTooltipAffordanceString(),
						restPresenter);

				// Link the presenter and the information control
				restPresenter.setInformationControl(informationControl);

				return informationControl;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text
	 * .ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public String getHoverInfo(final ITextViewer aTextViewer,
			final IRegion aHoverRegion) {

		// Get the document
		IDocument document = aTextViewer.getDocument();

		// Directive help, if valid
		String hoverProposals = generateDirectiveHelp(document, aHoverRegion);
		if (hoverProposals != null) {
			return hoverProposals;
		}

		// Spelling service proposals (if any)
		return generateSpellingProposals(document, aHoverRegion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse
	 * .jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public synchronized Object getHoverInfo2(final ITextViewer aTextViewer,
			final IRegion aHoverRegion) {

		String info = getHoverInfo(aTextViewer, aHoverRegion);

		if (info == null) {
			pLastHoverData = null;
			return null;
		}

		/*
		 * As we searched for a valid word, getHoverRegion() omits the reST
		 * directive suffix ('::')
		 */
		pLastHoverData = new InternalHoverData(pInternalLinkListener,
				aTextViewer.getDocument(), aHoverRegion, false);
		pLastHoverData.setInformation(info);

		return pLastHoverData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text
	 * .ITextViewer, int)
	 */
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

		if (endWord <= 0 || endWord <= beginWord) {
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

			if (!isWordCharacter(wordArray[i])) {
				break;
			}

			beginRealWord--;
		}

		for (int i = offsetInWord; i < wordArray.length; i++) {
			if (!isWordCharacter(wordArray[i])) {
				break;
			}

			endRealWord++;
		}

		// Backward movement correction
		if (!Character.isLetter(word.charAt(beginRealWord))) {
			beginRealWord++;
		}

		// The "greater than" case appears if the mouse is over a non-letter
		// character
		if (beginRealWord >= endRealWord) {
			return new Region(aOffset, 0);
		}

		return new Region(lineOffset + beginWord + beginRealWord, endRealWord
				- beginRealWord);
	}

	/**
	 * Tests if the given character can be considered as a word character
	 * 
	 * @param aCharacter
	 *            Character to be tested
	 * 
	 * @return True if the character is valid in a word.
	 */
	public boolean isWordCharacter(final char aCharacter) {

		if (Character.isLetter(aCharacter)) {
			return true;
		}

		if (aCharacter == '-') {
			return true;
		}

		return false;
	}
}
