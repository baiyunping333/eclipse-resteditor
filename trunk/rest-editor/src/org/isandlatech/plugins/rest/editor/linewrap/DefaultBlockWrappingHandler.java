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

package org.isandlatech.plugins.rest.editor.linewrap;

import java.util.Set;

import org.isandlatech.plugins.rest.parser.RestLanguage;

/**
 * Default block wrapper : tries to conserve the indentation
 * 
 * @author Thomas Calmant
 */
public class DefaultBlockWrappingHandler extends AbstractBlockWrappingHandler {

	/**
	 * Sets up the marker set to take ReST markers into account
	 */
	public DefaultBlockWrappingHandler() {

		// Take care of ReST markers
		Set<String> markersSet = getMarkersSet();
		markersSet.add(RestLanguage.BOLD_MARKER);
		markersSet.add(RestLanguage.EMPHASIS_MARKER);
		markersSet.add(RestLanguage.INLINE_LITERAL_MARKER);
		markersSet.add(RestLanguage.LINK_BEGIN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #getType()
	 */
	@Override
	public String getType() {
		return DEFAULT_HANDLER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #wrap()
	 */
	@Override
	public String wrap(final int aMaxLen) {

		// Transform the block in a line
		StringBuilder blockInLine = convertBlockInLine(getBlockContent());

		// Do the wrapping
		setBlockContent(wrapLine(blockInLine.toString(), aMaxLen).toString());

		// Convert internals EOL into document ones
		return replaceInternalLineMarkers();
	}
}
