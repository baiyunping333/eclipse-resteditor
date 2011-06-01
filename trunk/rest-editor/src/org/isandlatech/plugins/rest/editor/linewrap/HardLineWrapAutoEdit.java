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

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.isandlatech.plugins.rest.editor.linewrap.HardLineWrap.WrapResult;

/**
 * Modifies the document content to avoid lines above a preferred length
 * 
 * @author Thomas Calmant
 */
public class HardLineWrapAutoEdit implements IAutoEditStrategy {

	protected class InternalDocumentCommand extends DocumentCommand {
		// Just provides DocumentCommand access
	}

	/** Associated document */
	private IDocument pDocument;

	/** Maximum line length */
	private int pMaxLineLength;

	/** Document position updater */
	private LinePositionUpdater pLineUpdater;

	/** Indicates if the line updater is registered to a document */
	private boolean pRegistered;

	/** Line wrapper */
	private final HardLineWrap pWrapper;

	/**
	 * Prepares members : line wrapper and line position updater
	 * 
	 * @param aMaxLineLength
	 *            Maximum line length
	 */
	public HardLineWrapAutoEdit(final int aMaxLineLength) {

		pWrapper = new HardLineWrap();
		pLineUpdater = new LinePositionUpdater();
		pRegistered = false;
		pMaxLineLength = aMaxLineLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.IAutoEditStrategy#customizeDocumentCommand(org
	 * .eclipse.jface.text.IDocument, org.eclipse.jface.text.DocumentCommand)
	 */
	@Override
	public void customizeDocumentCommand(final IDocument aDocument,
			final DocumentCommand aCommand) {

		if (pDocument == null) {
			pDocument = aDocument;
		} else if (pDocument != aDocument) {
			// Bad document ?
			return;
		}

		if (!aCommand.doit) {
			return;
		}

		try {
			wrapLine(aCommand);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the current state of added lines. The keys are line numbers,
	 * values are block detectors, used to detect the key.
	 * 
	 * @return the current state of added lines
	 */
	public Map<Integer, IBlockDetector> getVirtualLines() {
		return pLineUpdater.getWatchedLines();
	}

	/**
	 * Prepares the line updater for the given document
	 * 
	 * @param aDocument
	 *            Document to set up
	 */
	public void registerListener(final IDocument aDocument) {

		if (pDocument == null) {
			pDocument = aDocument;
		} else if (pDocument != aDocument) {
			return;
		}

		// Set the line updater, if needed
		if (!pRegistered) {
			pLineUpdater.clear();
			aDocument.addDocumentListener(pLineUpdater);
			pRegistered = true;
		}
	}

	/**
	 * Removes lines delimiters inserted by block wrapping handlers. The line
	 * updater <b>must</b> be disabled (via {@link #unregisterListener()})
	 * before calling this method.
	 * 
	 * Resets the document content on error
	 * 
	 * @return True on success, False on error.
	 */
	public boolean removeWrapping() {

		// Store current content, to be able to roll back
		final String docSave = pDocument.get();

		Map<Integer, IBlockDetector> virtualLinesInfo = pLineUpdater
				.getWatchedLines();

		@SuppressWarnings("unchecked")
		Entry<Integer, IBlockDetector>[] entries = virtualLinesInfo.entrySet()
				.toArray(new Entry[0]);

		// Modify document from bottom to top, to avoid offset modifications
		for (int i = entries.length - 1; i >= 0; i--) {

			Entry<Integer, IBlockDetector> entry = entries[i];

			int baseLine = entry.getKey();
			IBlockDetector detector = entry.getValue();

			// Find the block
			BlockInformation blockInfo = detector.getBlock(pDocument, baseLine,
					baseLine);

			// Re-use the handler, but to make a single line this time
			IBlockWrappingHandler handler = BlockWrappingHandlerStore.get()
					.getHandler(detector.getHandlerType());

			String blockContent;
			try {
				blockContent = pDocument.get(blockInfo.getOffset(),
						blockInfo.getLength());

			} catch (BadLocationException e) {
				e.printStackTrace();
				pDocument.set(docSave);
				return false;
			}

			// Replace the block
			StringBuilder newLine = handler.convertBlockInLine(blockContent);
			try {
				pDocument.replace(blockInfo.getOffset(), blockInfo.getLength(),
						newLine.toString());

			} catch (BadLocationException e) {
				e.printStackTrace();
				pDocument.set(docSave);
				return false;
			}
		}

		return true;
	}

	/**
	 * Sets the maximum line length
	 * 
	 * @param aMaxLineLength
	 *            the maximum line length
	 */
	public void setMaxLineLength(final int aMaxLineLength) {
		pMaxLineLength = aMaxLineLength;
	}

	/**
	 * Unregisters the line updater for the given document
	 * 
	 * @param aDocument
	 *            Document to set up
	 */
	public void unregisterListener() {

		// Set the line updater, if needed
		if (pRegistered) {
			pDocument.removeDocumentListener(pLineUpdater);
			pRegistered = false;
		}
	}

	/**
	 * Hards wrap the current line if its length goes over the preferred limit.
	 * 
	 * @param aDocument
	 *            Currently edited document
	 * @param aCommand
	 *            Document customization command
	 * @throws BadLocationException
	 *             Document command gives out of bound values
	 * @throws BadPositionCategoryException
	 *             The wrap position couldn't be added
	 */
	protected WrapResult wrapLine(final DocumentCommand aCommand)
			throws BadLocationException {

		WrapResult result = pWrapper.wrapRegion(pDocument, aCommand,
				pMaxLineLength);

		if (result == null) {
			return null;
		}

		int firstLine = result.getFirstLine();
		int newLastLine = result.getNewLastLine();

		if (firstLine >= 0 && pLineUpdater != null) {

			if (newLastLine - firstLine <= 0) {
				// Remove line if the block is only 1 line long (or on error...)
				pLineUpdater.removeLine(firstLine);

			} else {
				// Watch the block if its length is more than 1 line
				pLineUpdater.updateBlockSize(firstLine, result.getDetector(),
						result.getOldLastLine(), newLastLine);
			}
		}

		return result;
	}

	/**
	 * Line wrap the whole associated document. The line updater should be
	 * activated before calling this method (see
	 * {@link #registerListener(IDocument)})
	 * 
	 * @return True on success, False on error.
	 */
	public boolean wrapWholeDocument() {

		// No doc, no wrap...
		if (pDocument == null) {
			return false;
		}

		// Save the doc content
		final String docSave = pDocument.get();

		// Start the infernal loop
		final int nbLines = pDocument.getNumberOfLines();
		int line = 0;
		final DocumentCommand command = new InternalDocumentCommand();

		try {
			while (line < nbLines) {

				command.offset = pDocument.getLineOffset(line);
				command.length = 0;
				command.text = "";
				command.caretOffset = -1;
				command.doit = true;

				final String lineContentType = pDocument
						.getContentType(command.offset);

				if (IDocument.DEFAULT_CONTENT_TYPE.equals(lineContentType)) {
					// Only work in default content type
					WrapResult result = wrapLine(command);

					if (result != null && command.doit) {
						line = result.getNewLastLine() + 1;

					} else {
						line++;
					}

				} else {
					line++;
				}
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
			pDocument.set(docSave);
			return false;
		}

		return true;
	}
}
