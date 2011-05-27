/**
 * File:   AbstractBlockWrappingHandler.java
 * Author: Thomas Calmant
 * Date:   27 mai 2011
 */
package org.isandlatech.plugins.rest.editor.linewrap.v2;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;

/**
 * @author Thomas Calmant
 * 
 */
public abstract class AbstractBlockWrappingHandler implements
		IBlockWrappingHandler {

	/** Internal end-of-line representation */
	public static final String INTERNAL_LINE_FEED = "\b";

	/** Working block content */
	protected String pBlockContent;

	/** Modified block */
	protected BlockInformation pDocBlock;

	/** Handled document */
	protected IDocument pDocument;

	/** Line delimiter for the current document */
	protected String pLineDelimiter;

	/** Line utility singleton */
	protected final LineUtil pLineUtil = LineUtil.get();

	/** Reference offset */
	protected int pReferenceOffset;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #applyCommand(org.eclipse.jface.text.DocumentCommand)
	 */
	@Override
	public String applyCommand(final DocumentCommand aCommand) {

		// Make offsets relative to the block
		final int blockRelativeCommandOffset = aCommand.offset
				- pDocBlock.getOffset();
		int blockRelativeReferenceOffset = pReferenceOffset
				- pDocBlock.getOffset();

		StringBuilder modifiedString = new StringBuilder(pBlockContent.length()
				+ aCommand.text.length());

		// Insert first part (not modified)
		modifiedString.append(pBlockContent.substring(0,
				blockRelativeCommandOffset));

		// Insert the new text
		if (TextUtilities.startsWith(TextUtilities.DELIMITERS, aCommand.text) != -1) {
			aCommand.text = INTERNAL_LINE_FEED;
		}

		modifiedString.append(aCommand.text);

		// Insert the rest, avoiding deleted parts
		int postDeletionOffset = blockRelativeCommandOffset + aCommand.length;
		if (postDeletionOffset < pBlockContent.length()) {
			modifiedString.append(pBlockContent.substring(postDeletionOffset));
		}

		// Update the reference offset as needed
		if (blockRelativeReferenceOffset >= blockRelativeCommandOffset) {

			// On pure deletion, the base offset is already at the good place
			pReferenceOffset += Math.max(aCommand.text.length()
					- aCommand.length, 0);
		}

		pBlockContent = modifiedString.toString();
		return pBlockContent;
	}

	/**
	 * Retrieves the current block information
	 * 
	 * @return the current block information
	 */
	public BlockInformation getBlockInformation() {
		return pDocBlock;
	}

	/**
	 * Retrieves the document currently modified by this handler
	 * 
	 * @return the document modified by this handler
	 */
	public IDocument getDocument() {
		return pDocument;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #getReferenceOffset()
	 */
	@Override
	public int getReferenceOffset() {
		return pReferenceOffset;
	}

	/**
	 * Debug print of the current block content with a pipe indicating the
	 * current reference offset position
	 */
	protected void printOffset() {

		System.out.println("Insert @"
				+ (pReferenceOffset - pDocBlock.getOffset()));

		try {
			StringBuilder modifiedString = new StringBuilder(pBlockContent);

			modifiedString
					.insert(pReferenceOffset - pDocBlock.getOffset(), '|');

			System.out.println("String : '" + modifiedString + "'");

			modifiedString.deleteCharAt(pReferenceOffset
					- pDocBlock.getOffset());

		} catch (Exception ex) {
			System.out.println("[Reference error - " + ex + "]");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #setReferenceOffset(int)
	 */
	@Override
	public void setReferenceOffset(final int aOffset) {
		pReferenceOffset = aOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #setUp(org.eclipse.jface.text.IDocument,
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.BlockInformation)
	 */
	@Override
	public boolean setUp(final IDocument aDocument,
			final BlockInformation aBlock) {

		pDocument = aDocument;
		pDocBlock = aBlock;
		pBlockContent = null;
		pLineDelimiter = TextUtilities.getDefaultLineDelimiter(aDocument);

		if (!pDocBlock.computeOffsets(aDocument)) {
			return false;
		}

		// Store the block content
		try {
			pBlockContent = aDocument.get(pDocBlock.getOffset(),
					pDocBlock.getLength());

		} catch (BadLocationException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
