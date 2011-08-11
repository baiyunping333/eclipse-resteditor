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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.isandlatech.plugins.rest.editor.userassist.BasicInternalLinkHandler;
import org.isandlatech.plugins.rest.editor.userassist.IInternalBrowserListener;
import org.isandlatech.plugins.rest.editor.userassist.InternalBrowserData;

/**
 * Completion proposal with an internal link handler registration
 * 
 * @author Thomas Calmant
 */
public class HoverCompletionProposal implements ICompletionProposal,
		ICompletionProposalExtension5 {

	/** Description printed when the proposal is selected */
	private final String pAdditionalInformation;

	/** Hover link handler */
	private final IInternalBrowserListener pBrowserListener;

	/** String display in the choice list */
	private String pDisplayString;

	/** Current document */
	private final IDocument pDocument;

	/** Replacement length */
	private final int pReplacementLength;

	/** Replacement offset */
	private final int pReplacementOffset;

	/** Replacement content */
	private final String pReplacementString;

	/**
	 * Stores a completion proposal
	 * 
	 * @param aDocument
	 *            Working document
	 * @param aReplacementString
	 *            String to use for replacement
	 * @param aReplacementOffset
	 *            Replacement offset in the document
	 * @param aReplacementLength
	 *            Replaced word length
	 * @param aDisplayString
	 *            String displayed in content assistant list
	 * @param aAdditionalProposalInfo
	 *            Description associated to this proposal
	 */
	public HoverCompletionProposal(final IDocument aDocument,
			final String aReplacementString, final int aReplacementOffset,
			final int aReplacementLength, final String aDisplayString,
			final String aAdditionalProposalInfo) {

		pDocument = aDocument;
		pReplacementString = aReplacementString;
		pReplacementOffset = aReplacementOffset;
		pReplacementLength = aReplacementLength;

		pDisplayString = aDisplayString;
		if (pDisplayString == null) {
			pDisplayString = aReplacementString;
		}

		pAdditionalInformation = aAdditionalProposalInfo;

		// Browser link handler
		pBrowserListener = new BasicInternalLinkHandler();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse
	 * .jface.text.IDocument)
	 */
	@Override
	public void apply(final IDocument aDocument) {

		try {
			aDocument.replace(pReplacementOffset, pReplacementLength,
					pReplacementString);

		} catch (BadLocationException x) {
			// ignore
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#
	 * getAdditionalProposalInfo()
	 */
	@Override
	public String getAdditionalProposalInfo() {
		return pAdditionalInformation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension5#
	 * getAdditionalProposalInfo(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Object getAdditionalProposalInfo(final IProgressMonitor aMonitor) {

		Region region = new Region(pReplacementOffset, pReplacementLength);

		/*
		 * As we are near to add a directive, its suffix ('::') is included in
		 * the replacement length value.
		 */
		InternalBrowserData data = new InternalBrowserData(pBrowserListener,
				pDocument, region, true);

		data.setInformation(getAdditionalProposalInfo());

		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#
	 * getContextInformation()
	 */
	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString
	 * ()
	 */
	@Override
	public String getDisplayString() {
		return pDisplayString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	@Override
	public Image getImage() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection
	 * (org.eclipse.jface.text.IDocument)
	 */
	@Override
	public Point getSelection(final IDocument aDocument) {
		// Point the end of the insertion
		return new Point(pReplacementOffset + pReplacementString.length(), 0);
	}
}
