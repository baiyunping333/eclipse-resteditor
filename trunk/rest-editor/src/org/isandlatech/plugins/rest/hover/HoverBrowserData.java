/**
 * File:   HoverBrowserData.java
 * Author: Thomas Calmant
 * Date:   4 mai 2011
 */
package org.isandlatech.plugins.rest.hover;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * Structure used for hover handler and hover control communications
 * 
 * @author Thomas Calmant
 */
public class HoverBrowserData {

	/** Document associated to the hovering */
	private IDocument pDocument;

	/** Hover region */
	private IRegion pHoverRegion;

	/** Hover control information */
	private String pInformation;

	/** Data sender */
	private IHoverBrowserListener pListener;

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
	public HoverBrowserData(final IHoverBrowserListener aSource,
			final IDocument aDocument, final IRegion aHoverRegion,
			final boolean aRegionWithSuffix) {

		pListener = aSource;
		pDocument = aDocument;
		pHoverRegion = aHoverRegion;
		pRegionWithSuffix = aRegionWithSuffix;
	}

	/**
	 * @return the document
	 */
	public IDocument getDocument() {
		return pDocument;
	}

	/**
	 * @return the hoverRegion
	 */
	public IRegion getHoverRegion() {
		return pHoverRegion;
	}

	/**
	 * @return the information
	 */
	public String getInformation() {
		return pInformation;
	}

	/**
	 * @return the link listener
	 */
	public IHoverBrowserListener getListener() {
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
	 * Sets the information content
	 * 
	 * @param aInformation
	 *            Information associated to the tooltip
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
