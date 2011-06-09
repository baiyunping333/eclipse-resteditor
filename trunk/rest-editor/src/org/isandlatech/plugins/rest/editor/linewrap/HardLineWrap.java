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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.isandlatech.plugins.rest.editor.scanners.RestPartitionScanner;

/**
 * This class handles the line wrapping. Inspired from Texclipse hard line wrap
 * class (by Antti Pirinen, Oskar Ojala, Boris von Loesch).
 * 
 * @author Thomas Calmant
 */
public class HardLineWrap {

	/**
	 * Stores wrapping result
	 * 
	 * @author Thomas Calmant
	 */
	public class WrapResult {

		/** The detector used to detect the block */
		private IBlockDetector pDetector;

		/** First line of the detected block */
		private int pFirstLine;

		/** Handler used for wrapping */
		private String pHandlerType;

		/** The new block last line */
		private int pNewLastLine;

		/** Last line of the detected block */
		private int pOldLastLine;

		/**
		 * Prepares a read-only wrapping result
		 * 
		 * @param aDetector
		 *            detector used to detect the block
		 * @param aHandler
		 *            handler used for wrapping
		 * @param aFirstLine
		 *            first line of the detected block
		 * @param aOldLastLine
		 *            last line of the detected block
		 * @param aNewLastLine
		 *            new block last line
		 */
		public WrapResult(final IBlockDetector aDetector,
				final String aHandler, final int aFirstLine,
				final int aOldLastLine, final int aNewLastLine) {

			pDetector = aDetector;
			pHandlerType = aHandler;
			pFirstLine = aFirstLine;
			pOldLastLine = aOldLastLine;
			pNewLastLine = aNewLastLine;
		}

		/**
		 * Retrieves the instance of the detector that has been selected to find
		 * the wrapped block
		 * 
		 * @return the detector
		 */
		public IBlockDetector getDetector() {
			return pDetector;
		}

		/**
		 * Retrieves the number of the first line of the block in the document
		 * 
		 * @return the number of the first line of the block
		 */
		public int getFirstLine() {
			return pFirstLine;
		}

		/**
		 * Retrieves the type of the handler used for block wrapping
		 * 
		 * @return the handler type
		 */
		public String getHandlerType() {
			return pHandlerType;
		}

		/**
		 * @return the new block last line
		 */
		public int getNewLastLine() {
			return pNewLastLine;
		}

		/**
		 * Retrieves the number of the last line of the block in the document
		 * 
		 * @return the number of the last line of the block
		 */
		public int getOldLastLine() {
			return pOldLastLine;
		}
	}

	/**
	 * Debug function : prints out the given document command attributes
	 * 
	 * @param aCommand
	 *            Command to be displayed
	 */
	public static void printCommand(final DocumentCommand aCommand) {
		System.out.println("--- Command ---");
		System.out.println("\toffset   = " + aCommand.offset);
		System.out.println("\tlength   = " + aCommand.length);
		System.out.println("\ttext len = " + aCommand.text.length());
		System.out.println("\ttext     = '" + aCommand.text + "'");
	}

	/** Block detectors list */
	private List<IBlockDetector> pDetectors;

	/**
	 * Registers detectors and handlers
	 */
	public HardLineWrap() {

		// Register detectors
		pDetectors = new ArrayList<IBlockDetector>();
		pDetectors.add(new DefaultBlockDetector(
				RestPartitionScanner.PARTITIONING));
		pDetectors.add(new ListBlockDetector());

		// Register handlers
		BlockWrappingHandlerStore.get().registerHandler(
				new ListBlockWrappingHandler());
	}

	/**
	 * Counts the number of occurrences of the given string
	 * 
	 * @param aText
	 *            String to read
	 * @param aSubstring
	 *            String to count
	 * @return The number of occurrences of aSubstring in aText
	 */
	public int countOccurrences(final String aText, final String aSubstring) {

		int count = 0;
		int index = 0;
		int iter = aSubstring.length();

		try {
			while ((index = aText.indexOf(aSubstring, index)) != -1) {
				count++;
				index += iter;
			}

		} catch (ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
		}

		return count;
	}

	/**
	 * Wraps a paragraph in the document
	 * 
	 * @param aDocument
	 *            Modified document
	 * @param aCommand
	 *            Document replace command
	 * @param aMaxLen
	 *            Maximum line length
	 * 
	 * @return The first line of the modified block, -1 on error
	 * 
	 * @throws BadLocationException
	 *             The replace command contains weird data
	 */
	public WrapResult wrapRegion(final IDocument aDocument,
			final DocumentCommand aCommand, final int aMaxLen)
			throws BadLocationException {

		if (!aCommand.doit) {
			return null;
		}

		// Store some information
		final int initialOffset = aCommand.offset;

		final int baseDocLineNr = aDocument.getLineOfOffset(initialOffset);
		final int endDocLineNr = aDocument.getLineOfOffset(initialOffset
				+ aCommand.length);

		final String lineDelimiter = TextUtilities
				.getDefaultLineDelimiter(aDocument);

		IBlockWrappingHandler blockHandler = null;
		int bestDetectorPriority = Integer.MAX_VALUE;
		IBlockDetector bestDetector = null;
		BlockInformation baseDocBlock = null;

		// Look for the "best" detector available
		for (IBlockDetector detector : pDetectors) {

			BlockInformation blockInfo = detector.getBlock(aDocument,
					baseDocLineNr, endDocLineNr);

			// Invalid values
			if (blockInfo == null) {
				continue;
			}

			if (detector.getPriority() < bestDetectorPriority) {
				bestDetector = detector;
				baseDocBlock = blockInfo;
				bestDetectorPriority = detector.getPriority();
			}
		}

		if (baseDocBlock == null || bestDetector == null) {
			System.err.println("No block detected...");
			return null;
		}

		blockHandler = BlockWrappingHandlerStore.get().getHandler(
				bestDetector.getHandlerType());

		// Handler not found
		if (blockHandler == null) {
			System.err.println("No handler available for : "
					+ bestDetector.getHandlerType());
			return null;
		}

		String result = null;

		// Compute the new offset
		int newOffset = aCommand.offset;
		if (aCommand.length > 0) {
			// Special case : paste / replace
			newOffset += aCommand.text.length();
		}

		synchronized (blockHandler) {
			// Just in case Eclipse gets a multi-threaded UI...

			blockHandler.setUp(aDocument, baseDocBlock);
			blockHandler.setReferenceOffset(newOffset);
			blockHandler.applyCommand(aCommand);
			result = blockHandler.wrap(aMaxLen);
		}

		if (result == null) {
			aCommand.doit = false;
			return null;
		}

		/*
		 * Do not delete this line ! It removes the bug of the bad caret
		 * behavior when inserting a character at length - 1, avoiding caret
		 * movement when the DocumentCommand is executed (forces
		 * DocumentCommand.updateCaret() to return false)
		 */
		aCommand.shiftsCaret = false;

		aCommand.offset = baseDocBlock.getOffset();
		aCommand.length = baseDocBlock.getLength();
		aCommand.caretOffset = blockHandler.getReferenceOffset();
		aCommand.text = result;

		// Return the line at the beginning of the block
		int baseBlockFirstLine = baseDocBlock.getFirstLine();
		int baseBlockLastLine = baseDocBlock.getLastLine();
		int newBlockLines = countOccurrences(result, lineDelimiter);

		WrapResult wrapResult = new WrapResult(bestDetector,
				blockHandler.getType(), baseBlockFirstLine, baseBlockLastLine,
				baseBlockFirstLine + newBlockLines);

		return wrapResult;
	}
}
