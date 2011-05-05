/**
 * File:   RestTextHover.java
 * Author: Thomas Calmant
 * Date:   4 mars 2011
 */
package org.isandlatech.plugins.rest.hover;

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
import org.isandlatech.plugins.rest.i18n.Messages;

/**
 * Text assistant : spell checker or directive helper
 * 
 * @author Thomas Calmant
 */
public class RestTextHover implements ITextHover, ITextHoverExtension,
		ITextHoverExtension2, IHoverBrowserListener {

	/**
	 * Simple way to make an internal link
	 * 
	 * @param aActionPrefix
	 *            The action prefix to use (see {@link IHoverConstants}
	 * @param aValue
	 *            Action parameter
	 * @return The forged internal link
	 */
	public static String makeLink(final String aActionPrefix,
			final String aValue) {

		StringBuilder builder = new StringBuilder();
		builder.append(IHoverConstants.INTERNAL_PREFIX);
		builder.append(aActionPrefix);
		builder.append(aValue);

		return builder.toString();
	}

	/** Spelling context to be used (standard text) */
	private final SpellingContext pSpellingContext;

	/** Spelling engine to use */
	private ISpellingEngine pSpellingEngine;

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
							+ makeLink(IHoverConstants.SPELL_LINK_PREFIX,
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
		return HoverBrowserInformationControl.getCreator();
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

		HoverBrowserData data = new HoverBrowserData(this,
				aTextViewer.getDocument(), aHoverRegion);
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

		if (beginRealWord == endRealWord) {
			return new Region(aOffset, 0);
		}

		return new Region(lineOffset + beginWord + beginRealWord, endRealWord
				- beginRealWord);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.isandlatech.plugins.rest.hover.IHoverBrowserListener#
	 * hoverInternalLinkClicked(java.lang.String,
	 * org.isandlatech.plugins.rest.hover.HoverBrowserData)
	 */
	@Override
	public boolean hoverInternalLinkClicked(final String aInternalLink,
			final HoverBrowserData aAssociatedData) {

		if (aInternalLink.startsWith(IHoverConstants.SPELL_LINK_PREFIX)) {

			// Spell checker link
			String replacementWord = aInternalLink
					.substring(IHoverConstants.SPELL_LINK_PREFIX.length());

			return spellAction(aAssociatedData, replacementWord);

		} else if (aInternalLink.startsWith(IHoverConstants.SAMPLE_LINK_PREFIX)) {

			// Insert sample link
			String directive = aInternalLink
					.substring(IHoverConstants.SAMPLE_LINK_PREFIX.length());

			String sample = Messages.getDirectiveSample(directive);
			System.out.println(sample);
			// TODO insert it
			return sampleInsertionAction(aAssociatedData, directive);
		}

		// Link not treated
		return false;
	}

	private boolean sampleInsertionAction(
			final HoverBrowserData aAssociatedData, final String aDirective) {

		IDocument document = aAssociatedData.getDocument();
		IRegion region = aAssociatedData.getHoverRegion();

		String sample = Messages.getDirectiveSample(aDirective);
		if (sample == null) {
			return false;
		}

		try {
			// Replace the whole line
			int line = document.getLineOfOffset(region.getOffset());
			int lineStart = document.getLineOffset(line);

			// +2 : don't forget the last '::'
			int replacementLength = region.getLength() + region.getOffset()
					- lineStart + 2;

			document.replace(lineStart, replacementLength, sample);

		} catch (BadLocationException e) {
			return false;
		}

		return true;
	}

	/**
	 * Does the spell correction, by replacing badly spelled word with the given
	 * one
	 * 
	 * @param aAssociatedData
	 *            Data associated to the hover browser event
	 * @param aReplacementWord
	 *            Well-spelled word to use
	 * @return True on success, False on error
	 */
	private boolean spellAction(final HoverBrowserData aAssociatedData,
			final String aReplacementWord) {

		IDocument document = aAssociatedData.getDocument();
		IRegion region = aAssociatedData.getHoverRegion();

		try {
			document.replace(region.getOffset(), region.getLength(),
					aReplacementWord);

		} catch (BadLocationException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
