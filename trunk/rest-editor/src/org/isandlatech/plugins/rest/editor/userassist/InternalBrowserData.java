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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * Structure used for hover handler and hover control communications
 * 
 * @author Thomas Calmant
 */
public class InternalBrowserData {

	/** Document associated to the hovering */
	private IDocument pDocument;

	/** Hover region */
	private IRegion pHoverRegion;

	/** Hover control displayed information */
	private String pInformation;

	/** Data sender */
	private IInternalBrowserListener pListener;

	/** Does the region includes the directive suffix ('::') */
	private boolean pRegionWithSuffix;

	/**
	 * Sets up members
	 * 
	 * @param aSource
	 *            Hover handler / creator
	 * @param aDocument
	 *            Hovered document
	 * @param aHoverRegion
	 *            Hovered document region
	 * @param aRegionWithSuffix
	 *            Indicates if the aHoverRegion length includes the reST
	 *            directive suffix ('::')
	 */
	public InternalBrowserData(final IInternalBrowserListener aSource,
			final IDocument aDocument, final IRegion aHoverRegion,
			final boolean aRegionWithSuffix) {

		pListener = aSource;
		pDocument = aDocument;
		pHoverRegion = aHoverRegion;
		pRegionWithSuffix = aRegionWithSuffix;
	}

	/**
	 * @return the document associated to the hovering
	 */
	public IDocument getDocument() {
		return pDocument;
	}

	/**
	 * @return the hovered region
	 */
	public IRegion getHoverRegion() {
		return pHoverRegion;
	}

	/**
	 * @return the hover control displayed string
	 */
	public String getInformation() {
		return pInformation;
	}

	/**
	 * @return the link listener
	 */
	public IInternalBrowserListener getListener() {
		return pListener;
	}

	/**
	 * Indicates if the region length includes the reST directive suffix ('::')
	 * 
	 * @return true if the suffix is included
	 */
	public boolean isRegionWithSuffix() {
		return pRegionWithSuffix;
	}

	/**
	 * Notifies the listener, if any, that an internal link has been clicked
	 * 
	 * @param aInternalLink
	 *            The clicked link
	 * @return True if the event has been treated, else false
	 */
	public boolean notifyListener(final String aInternalLink) {
		if (pListener != null) {
			return pListener.hoverInternalLinkClicked(aInternalLink, this);
		}

		return false;
	}

	/**
	 * Sets the information content (displayed string)
	 * 
	 * @param aInformation
	 *            Information associated to the tool-tip
	 */
	public void setInformation(final String aInformation) {
		pInformation = aInformation;
	}

	/**
	 * Returns the information associated to this data
	 * 
	 * @see #getInformation()
	 */
	@Override
	public String toString() {
		return getInformation();
	}
}
