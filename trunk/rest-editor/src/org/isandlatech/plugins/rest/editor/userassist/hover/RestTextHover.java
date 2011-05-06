/**
 * File:   RestTextHover.java
 * Author: Thomas Calmant
 * Date:   4 mars 2011
 */
package org.isandlatech.plugins.rest.editor.userassist.hover;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;
import org.isandlatech.plugins.rest.editor.scanners.RestPartitionScanner;
import org.isandlatech.plugins.rest.editor.userassist.BasicInternalLinkHandler;
import org.isandlatech.plugins.rest.editor.userassist.IAssistanceConstants;
import org.isandlatech.plugins.rest.editor.userassist.IInternalBrowserListener;
import org.isandlatech.plugins.rest.editor.userassist.InternalBrowserData;
import org.isandlatech.plugins.rest.editor.userassist.InternalBrowserInformationControl;
import org.isandlatech.plugins.rest.i18n.Messages;

/**
 * Text assistant : spell checker or directive helper
 * 
 * @author Thomas Calmant
 */
public class RestTextHover implements ITextHover, ITextHoverExtension,
		ITextHoverExtension2 {

	/** Spelling context to be used (standard text) */
	private final SpellingContext pSpellingContext;

	/** Spelling engine to use */
	private final ISpellingEngine pSpellingEngine;

	/** Hover link handler */
	private final IInternalBrowserListener pBrowserListener;

	/**
	 * Prepares the spell check hover
	 * 
	 * @param aSpellingEngine
	 *            Spelling engine to use
	 */
	public RestTextHover(final ISpellingEngine aSpellingEngine) {

		pSpellingEngine = aSpellingEngine;

		// Prepare the spelling context
		String contentTypeString = IContentTypeManager.CT_TEXT;
		IContentType contentType = Platform.getContentTypeManager()
				.getContentType(contentTypeString);

		pSpellingContext = new SpellingContext();
		pSpellingContext.setContentType(contentType);

		// Browser link handler
		pBrowserListener = new BasicInternalLinkHandler();
	}

	/**
	 * 
	 * @param aDocument
	 * @param aHoverRegion
	 * @return
	 */
	protected String generateDirectiveHelp(final IDocument aDocument,
			final IRegion aHoverRegion) {

		// Get the partitioner
		IDocumentPartitioner partitioner;
		if (aDocument instanceof IDocumentExtension3) {
			IDocumentExtension3 doc3 = (IDocumentExtension3) aDocument;
			partitioner = doc3
					.getDocumentPartitioner(RestPartitionScanner.PARTITIONNING);
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
		directive = directive.substring(0, directive.length() - 2);

		return Messages.getDirectiveHelp(directive);
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

		String correctionProposals = "";

		try {
			SpellingProblemCollector collector = new SpellingProblemCollector();

			pSpellingEngine.check(aDocument, new IRegion[] { aHoverRegion },
					pSpellingContext, collector, null);

			List<SpellingProblem> foundProblems = collector.getProblems();

			for (SpellingProblem problem : foundProblems) {
				correctionProposals += "<h1>" + problem.getMessage()
						+ " :</h1>\n";

				for (ICompletionProposal proposal : problem.getProposals()) {

					String displayedString = proposal.getDisplayString();

					correctionProposals += "<a href=\""
							+ BasicInternalLinkHandler.makeLink(
									IAssistanceConstants.SPELL_LINK_PREFIX,
									displayedString) + "\">" + displayedString
							+ "</a>" + "<br />\n";
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 */
	@Override
	public IInformationControlCreator getHoverControlCreator() {
		return InternalBrowserInformationControl.getCreator();
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
	public Object getHoverInfo2(final ITextViewer aTextViewer,
			final IRegion aHoverRegion) {

		String info = getHoverInfo(aTextViewer, aHoverRegion);

		if (info == null) {
			return null;
		}

		/*
		 * As we searched for a valid word, getHoverRegion() omits the reST
		 * directive suffix ('::')
		 */
		InternalBrowserData data = new InternalBrowserData(pBrowserListener,
				aTextViewer.getDocument(), aHoverRegion, false);
		data.setInformation(info);

		return data;
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

		// The "greater than" case appears if the mouse is over a non-letter
		// character
		if (beginRealWord >= endRealWord) {
			return new Region(aOffset, 0);
		}

		return new Region(lineOffset + beginWord + beginRealWord, endRealWord
				- beginRealWord);
	}
}
