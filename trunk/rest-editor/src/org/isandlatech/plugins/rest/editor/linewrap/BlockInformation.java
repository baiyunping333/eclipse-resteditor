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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * Stores basic information about a text block
 * 
 * @author Thomas Calmant
 */
public class BlockInformation implements IRegion {

	/** Block length */
	private int pBlockLength;

	/** Block offset */
	private int pBlockOffset;

	/** First line of the block */
	private int pFirstLine;

	/** Last line of the block */
	private int pLastLine;

	/**
	 * Sets up the block information
	 * 
	 * @param aFirstLine
	 *            First line of the block
	 * @param aLastLine
	 *            Last line of the block
	 */
	public BlockInformation(final int aFirstLine, final int aLastLine) {
		pFirstLine = aFirstLine;
		pLastLine = aLastLine;
	}

	/**
	 * Computes block offset and length values for the given document
	 * 
	 * @param aDocument
	 *            Document to use for offset computation
	 * @return True on success, False on error
	 */
	public boolean computeOffsets(final IDocument aDocument) {

		try {
			pBlockOffset = aDocument.getLineOffset(pFirstLine);

			IRegion lastLineInfo = aDocument.getLineInformation(pLastLine);
			pBlockLength = lastLineInfo.getOffset() + lastLineInfo.getLength()
					- pBlockOffset;

			return true;

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Tests if the given offset is in this block.
	 * 
	 * Needs offsets to be computed ({@link #computeOffsets(IDocument)})
	 * 
	 * @param aOffset
	 *            Offset to be tested
	 * @return True if the offset is in this block
	 */
	public boolean contains(final int aOffset) {
		return aOffset >= pBlockOffset && aOffset < pBlockOffset + pBlockLength;
	}

	/**
	 * Retrieves the first line of the block
	 * 
	 * @return the first line of the block
	 */
	int getFirstLine() {
		return pFirstLine;
	}

	/**
	 * Retrieves the last line of the block
	 * 
	 * @return the last line of the block
	 */
	int getLastLine() {
		return pLastLine;
	}

	/**
	 * Retrieves the block length
	 * 
	 * @return the block length
	 */
	@Override
	public int getLength() {
		return pBlockLength;
	}

	/**
	 * Retrieves the block offset
	 * 
	 * @return the block offset
	 */
	@Override
	public int getOffset() {
		return pBlockOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("BlockInformation[first=");
		builder.append(pFirstLine);
		builder.append(", last=");
		builder.append(pLastLine);
		builder.append(", offset=");
		builder.append(pBlockOffset);
		builder.append(", length=");
		builder.append(pBlockLength);
		builder.append("]");

		return builder.toString();
	}
}
