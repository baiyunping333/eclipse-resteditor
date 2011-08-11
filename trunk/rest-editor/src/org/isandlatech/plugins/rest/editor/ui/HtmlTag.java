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

package org.isandlatech.plugins.rest.editor.ui;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an HTML tag information
 * 
 * @author Thomas Calmant
 */
public class HtmlTag {

	/** Tag attributes */
	private final Map<String, String> pAttributes = new HashMap<String, String>();

	/** Offset in output text */
	private final int pStartOffset;

	/** HTML Tag */
	private final String pTagName;

	/**
	 * Sets up the HTML tag information
	 * 
	 * @param aTagName
	 *            HTML tag name
	 * @param aContentStartOffset
	 *            Tag content offset
	 */
	public HtmlTag(final String aTagName, final int aContentStartOffset) {

		pTagName = aTagName;
		pStartOffset = aContentStartOffset;
	}

	/**
	 * Retrieves the attributes map (never null)
	 * 
	 * @return The attributes map
	 */
	public Map<String, String> getAttributes() {
		return pAttributes;
	}

	/**
	 * Retrieves the content start offset
	 * 
	 * @return The content start offset
	 */
	public int getStartOffset() {
		return pStartOffset;
	}

	/**
	 * Retrieves the tag name
	 * 
	 * @return The tag name
	 */
	public String getTagName() {
		return pTagName;
	}
}