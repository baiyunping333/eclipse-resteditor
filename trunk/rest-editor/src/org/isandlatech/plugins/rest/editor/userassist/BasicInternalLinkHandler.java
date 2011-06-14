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

package org.isandlatech.plugins.rest.editor.userassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

/**
 * Basic hover browser internal links handler. Provides complete spell checking
 * and sample insertion support.
 * 
 * @author Thomas Calmant
 */
public class BasicInternalLinkHandler implements IInternalBrowserListener {

	/**
	 * Simple way to make an internal link
	 * 
	 * @param aActionPrefix
	 *            The action prefix to use (see {@link IAssistanceConstants}
	 * @param aValue
	 *            Action parameter
	 * @return The forged internal link
	 */
	public static String makeLink(final CharSequence aActionPrefix,
			final CharSequence aValue) {

		StringBuilder builder = new StringBuilder();
		builder.append(IAssistanceConstants.INTERNAL_PREFIX);
		builder.append(aActionPrefix);
		builder.append(aValue);

		return builder.toString();
	}

	/**
	 * Prepare a spell replacement link in the browser.
	 * 
	 * The problem identifies the region, the proposal sets the replacement
	 * string.
	 * 
	 * @param aProblem
	 *            Spelling problem
	 * @param aProposal
	 *            A proposal of the spelling problem
	 * @return An internal spell URI
	 */
	public static String makeSpellLink(final SpellingProblem aProblem,
			final ICompletionProposal aProposal) {

		final StringBuilder internalURI = new StringBuilder();
		internalURI.append(aProblem.getOffset());
		internalURI.append('/');
		internalURI.append(aProblem.getLength());
		internalURI.append('/');
		internalURI.append(aProposal.getDisplayString());

		return makeLink(IAssistanceConstants.SPELL_LINK_PREFIX, internalURI);
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
			final InternalBrowserData aAssociatedData) {

		if (aInternalLink.startsWith(IAssistanceConstants.SPELL_LINK_PREFIX)) {

			// Spell checker link
			String spellInfo = aInternalLink
					.substring(IAssistanceConstants.SPELL_LINK_PREFIX.length());

			return spellAction(aAssociatedData, spellInfo);

		} else if (aInternalLink
				.startsWith(IAssistanceConstants.SAMPLE_LINK_PREFIX)) {

			// Insert sample link
			String directive = aInternalLink
					.substring(IAssistanceConstants.SAMPLE_LINK_PREFIX.length());

			return sampleInsertionAction(aAssociatedData, directive);
		}

		// Link not treated
		return false;
	}

	/**
	 * Does the sample insertion, replacing the current line
	 * 
	 * @param aAssociatedData
	 *            Data associated to the hover browser event
	 * @param aDirective
	 *            Selected directive
	 * @return True on success, False on error
	 */
	protected boolean sampleInsertionAction(
			final InternalBrowserData aAssociatedData, final String aDirective) {

		IDocument document = aAssociatedData.getDocument();
		IRegion region = aAssociatedData.getHoverRegion();

		String sample = HelpMessagesUtil.getDirectiveSample(aDirective);
		if (sample == null) {
			return false;
		}

		try {
			// Replace the whole line
			int line = document.getLineOfOffset(region.getOffset());
			int lineStart = document.getLineOffset(line);

			// Recalculate the replacement length
			int replacementLength = region.getLength() + region.getOffset()
					- lineStart;

			// Don't forget the directive suffix '::'
			if (!aAssociatedData.isRegionWithSuffix()) {
				replacementLength += 2;
			}

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
	 * @param aSpellURI
	 *            The spell URI content, without its prefix. Format
	 *            offset/len/word.
	 * @return True on success, False on error
	 */
	protected boolean spellAction(final InternalBrowserData aAssociatedData,
			final String aSpellURI) {

		final IDocument document = aAssociatedData.getDocument();

		final int offset, length;
		final String word;

		String[] spellInfo = aSpellURI.split("/");
		if (spellInfo.length == 1) {
			// No region info in the URI
			IRegion region = aAssociatedData.getHoverRegion();
			offset = region.getOffset();
			length = region.getLength();
			word = aSpellURI;

		} else if (spellInfo.length == 3) {
			// Extract data
			offset = Integer.valueOf(spellInfo[0]);
			length = Integer.valueOf(spellInfo[1]);
			word = spellInfo[2];

		} else {
			// Invalid URI
			return false;
		}

		try {
			document.replace(offset, length, word);

		} catch (BadLocationException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
