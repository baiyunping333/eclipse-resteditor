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

package org.isandlatech.plugins.rest.editor.linewrap.v2;

/**
 * Default block wrapper : tries to conserve the indentation
 * 
 * @author Thomas Calmant
 */
public class DefaultBlockWrappingHandler extends AbstractBlockWrappingHandler {

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
