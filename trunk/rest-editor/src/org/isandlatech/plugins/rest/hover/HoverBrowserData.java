/**
 * File:   HoverBrowserData.java
 * Author: Thomas Calmant
 * Date:   4 mai 2011
 */
package org.isandlatech.plugins.rest.hover;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * @author Thomas Calmant
 * 
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

	public HoverBrowserData(final IHoverBrowserListener aSource,
			final IDocument aDocument, final IRegion aHoverRegion) {

		pListener = aSource;
		pDocument = aDocument;
		pHoverRegion = aHoverRegion;
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
