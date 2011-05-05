/**
 * File:   BasicInternalLinkHandler.java
 * Author: Thomas Calmant
 * Date:   5 mai 2011
 */
package org.isandlatech.plugins.rest.editor.userassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.isandlatech.plugins.rest.i18n.Messages;

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
	public static String makeLink(final String aActionPrefix,
			final String aValue) {

		StringBuilder builder = new StringBuilder();
		builder.append(IAssistanceConstants.INTERNAL_PREFIX);
		builder.append(aActionPrefix);
		builder.append(aValue);

		return builder.toString();
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
			String replacementWord = aInternalLink
					.substring(IAssistanceConstants.SPELL_LINK_PREFIX.length());

			return spellAction(aAssociatedData, replacementWord);

		} else if (aInternalLink.startsWith(IAssistanceConstants.SAMPLE_LINK_PREFIX)) {

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

		String sample = Messages.getDirectiveSample(aDirective);
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
	 * @param aReplacementWord
	 *            Well-spelled word to use
	 * @return True on success, False on error
	 */
	protected boolean spellAction(final InternalBrowserData aAssociatedData,
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
