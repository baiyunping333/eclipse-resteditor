/**
 * File:   HardLineWrap.java
 * Author: Thomas Calmant
 * Date:   17 mai 2011
 */
package org.isandlatech.plugins.rest.editor.linewrap.v2;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;

/**
 * This class handles the line wrapping. Inspired from Texclipse hard line wrap
 * class (by Antti Pirinen, Oskar Ojala, Boris von Loesch).
 * 
 * @author Thomas Calmant
 */
public class HardLineWrap {

	private void printCommand(final DocumentCommand aCommand) {
		System.out.println("--- Command ---");
		System.out.println("\toffset = " + aCommand.offset);
		System.out.println("\tlength = " + aCommand.length);
		System.out.println("\ttext   = '" + aCommand.text + "'");
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
	public int wrapRegion(final IDocument aDocument,
			final DocumentCommand aCommand, final int aMaxLen)
			throws BadLocationException {

		if (!aCommand.doit) {
			return -1;
		}

		printCommand(aCommand);

		// Store some information
		final int initialOffset = aCommand.offset;

		final int baseDocLineNr = aDocument.getLineOfOffset(initialOffset);
		final int endDocLineNr = aDocument.getLineOfOffset(initialOffset
				+ aCommand.length);

		IBlockWrappingHandler blockHandler = null;
		IBlockDetector[] detectors = new IBlockDetector[] { new DefaultBlockDetector() };
		int bestDetectorPriority = Integer.MAX_VALUE;
		IBlockDetector bestDetector = null;
		BlockInformation baseDocBlock = null;

		// Look for the "best" detector available
		for (IBlockDetector detector : detectors) {

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
			return -1;
		}

		blockHandler = BlockWrappingHandlerFactory.getHandler(bestDetector
				.getHandlerType());

		blockHandler.setUp(aDocument, baseDocBlock);
		blockHandler.setReferenceOffset(aCommand.offset);
		blockHandler.applyCommand(aCommand);
		String result = blockHandler.wrap(aMaxLen);

		aCommand.offset = baseDocBlock.getOffset();
		aCommand.length = baseDocBlock.getLength();
		aCommand.caretOffset = blockHandler.getReferenceOffset();
		aCommand.text = result;

		// Return the line at the beginning of the block
		return baseDocLineNr;
	}
}
