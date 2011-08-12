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

/**
 * Interface to be implemented by hover handlers to receive internal links
 * events from the browser tool tip
 * 
 * @author Thomas Calmant
 */
public interface IInternalLinkListener {

	/**
	 * Handles an internal link selection in the hover presenter
	 * 
	 * @param aInternalLink
	 *            Selected link
	 * @param aAssociatedData
	 *            Data associated to the hover presenter sending this event
	 * @return True if the presenter must be closed, else False
	 */
	boolean hoverInternalLinkClicked(String aInternalLink,
			InternalHoverData aAssociatedData);
}
