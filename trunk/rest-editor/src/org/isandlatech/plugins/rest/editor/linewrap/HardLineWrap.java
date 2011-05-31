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
		private String pDetectorType;

		/** First line of the detected block */
		private int pFirstBlockLine;

		/** Handler used for wrapping */
		private String pHandlerType;

		/**
		 * Prepares a read-only wrapping result
		 * 
		 * @param aDetector
		 *            detector used to detect the block
		 * @param aHandler
		 *            handler used for wrapping
		 * @param aLine
		 *            first line of the detected block
		 */
		public WrapResult(final String aDetector, final String aHandler,
				final int aLine) {

			pDetectorType = aDetector;
			pHandlerType = aHandler;
			pFirstBlockLine = aLine;
		}

		/**
		 * Retrieves the name of the detector that has been selected to find the
		 * wrapped block
		 * 
		 * @return the detector type
		 */
		public String getDetectorType() {
			return pDetectorType;
		}

		/**
		 * Retrieves the number of the first line of the block in the document
		 * 
		 * @return the number of the first line of the block
		 */
		public int getFirstBlockLine() {
			return pFirstBlockLine;
		}

		/**
		 * Retrieves the type of the handler used for block wrapping
		 * 
		 * @return the handler type
		 */
		public String getHandlerType() {
			return pHandlerType;
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
		System.out.println("\toffset = " + aCommand.offset);
		System.out.println("\tlength = " + aCommand.length);
		System.out.println("\ttext   = '" + aCommand.text + "'");
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
				RestPartitionScanner.PARTITIONNING));
		pDetectors.add(new ListBlockDetector());

		// Register handlers
		BlockWrappingHandlerStore.get().registerHandler(
				new ListBlockWrappingHandler());
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

		aCommand.offset = baseDocBlock.getOffset();
		aCommand.length = baseDocBlock.getLength();
		aCommand.caretOffset = blockHandler.getReferenceOffset();
		aCommand.text = result;

		// Return the line at the beginning of the block
		WrapResult wrapResult = new WrapResult(bestDetector.getType(),
				blockHandler.getType(), baseDocBlock.getFirstLine());
		return wrapResult;
	}
}
