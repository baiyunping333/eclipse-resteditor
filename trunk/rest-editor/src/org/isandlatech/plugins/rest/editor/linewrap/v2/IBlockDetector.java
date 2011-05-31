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

import org.eclipse.jface.text.IDocument;

/**
 * Describes a text block detector.
 * 
 * @author Thomas Calmant
 */
public interface IBlockDetector {

	/**
	 * Retrieves the information about the block containing the given line, null
	 * on error.
	 * 
	 * @param aDocument
	 *            Document containing the block
	 * @param aBaseFirstLine
	 *            Reference line for the beginning of the block
	 * @param aBaseLastLine
	 *            Reference line for the end of the block
	 * 
	 * @return The detected block informations, null on error
	 */
	BlockInformation getBlock(IDocument aDocument, int aBaseFirstLine,
			int aBaseLastLine);

	/**
	 * Retrieves the handler to use for this block
	 * 
	 * @return The handler type to be used
	 */
	String getHandlerType();

	/**
	 * Retrieves the detector priority. The less the result is, the more this
	 * detector has chances to be override an already found region.
	 * 
	 * Could be seen as Linux priority values (<0 : really important, 0 :
	 * normal, >0 not important).
	 * 
	 * @return The detector priority (the less value, the more priority).
	 */
	int getPriority();
}
