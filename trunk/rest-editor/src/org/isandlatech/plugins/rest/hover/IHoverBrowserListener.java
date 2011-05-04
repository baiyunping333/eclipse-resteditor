/**
 * File:   IHoverBrowserListener.java
 * Author: Thomas Calmant
 * Date:   4 mai 2011
 */
package org.isandlatech.plugins.rest.hover;

/**
 * @author Thomas Calmant
 * 
 */
public interface IHoverBrowserListener {

	/**
	 * Handles an internal link selection in the hover browser
	 * 
	 * @param aInternalLink
	 *            Selected link
	 * @param aAssociatedData
	 *            Data associated to the hover browser sending this event
	 * @return True if the browser must be closed, else False
	 */
	public boolean hoverInternalLinkClicked(final String aInternalLink,
			HoverBrowserData aAssociatedData);
}
