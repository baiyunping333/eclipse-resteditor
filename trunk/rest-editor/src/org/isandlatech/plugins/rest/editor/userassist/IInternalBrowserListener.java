/**
 * File:   IInternalBrowserListener.java
 * Author: Thomas Calmant
 * Date:   4 mai 2011
 */
package org.isandlatech.plugins.rest.editor.userassist;


/**
 * Interface to be implemented by hover handlers to receive internal links
 * events from the browser tooltip
 * 
 * @author Thomas Calmant
 */
public interface IInternalBrowserListener {

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
			InternalBrowserData aAssociatedData);
}
