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

import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;

/**
 * Describes a block line-wrapping handler.
 * 
 * @author Thomas Calmant
 */
public interface IBlockWrappingHandler {

	/** Default wrapping handler name */
	String DEFAULT_HANDLER = "__default__handler__";

	/**
	 * Applies the given command.
	 * 
	 * @param aCommand
	 *            A document replace command
	 * @return The new block content
	 */
	String applyCommand(DocumentCommand aCommand);

	/**
	 * Retrieves the updated value of the reference offset
	 * 
	 * @see #setReferenceOffset(int)
	 * 
	 * @return the updated value of the reference offset
	 */
	int getReferenceOffset();

	/**
	 * Retrieves the type of this block handler
	 * 
	 * @return the type of this block handler
	 */
	String getType();

	/**
	 * Set an offset reference, that will be updated during block modification
	 * handling. The updated value is available through
	 * {@link #getReferenceOffset()}.
	 * 
	 * This method must be called before any content modification by this
	 * handler, i.e. before {@link #applyCommand(DocumentCommand)} and
	 * {@link #wrap(int)}.
	 * 
	 * @param aOffset
	 *            The reference offset.
	 */
	void setReferenceOffset(int aOffset);

	/**
	 * Sets up this object for the next calls
	 * 
	 * @param aDocument
	 *            The document containing the block
	 * @param aBlock
	 *            The block that will be treated
	 * @return True on success, False on error
	 */
	boolean setUp(IDocument aDocument, BlockInformation aBlock);

	/**
	 * Wraps the text block according to internal rules. Computes the new
	 * reference offset in the wrapped text block. Returns null on error
	 * 
	 * @param aMaxLen
	 *            Maximum line length to aim
	 * 
	 * @return The new block content, null on error
	 */
	String wrap(int aMaxLen);
}
