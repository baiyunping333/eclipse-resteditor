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
import java.util.TreeMap;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.isandlatech.plugins.rest.RestPlugin;

/**
 * Updates all document positions of the given category to store the offset of
 * there associated line
 * 
 * @author Thomas Calmant
 */
public class LinePositionUpdater implements IDocumentListener {

	/** Lines to update */
	private Map<Integer, IBlockDetector> pWatchedLines;

	/** Update needed flag */
	private boolean pAlreadyUpdated;

	/** Number of lines in the document before the update */
	private int pPreviousLinesCount;

	/**
	 * Stores the category of positions to update
	 */
	public LinePositionUpdater() {
		pWatchedLines = new TreeMap<Integer, IBlockDetector>();
		pAlreadyUpdated = false;
	}

	/**
	 * Resets the updater state
	 */
	public void clear() {

		pAlreadyUpdated = false;
		pPreviousLinesCount = 0;
		pWatchedLines.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org
	 * .eclipse.jface.text.DocumentEvent)
	 */
	@Override
	public void documentAboutToBeChanged(final DocumentEvent aEvent) {

		if (pAlreadyUpdated) {
			return;
		}

		pPreviousLinesCount = aEvent.fDocument.getNumberOfLines();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.
	 * jface.text.DocumentEvent)
	 */
	@Override
	public void documentChanged(final DocumentEvent aEvent) {

		if (pAlreadyUpdated) {
			pAlreadyUpdated = false;
			return;
		}

		IDocument document = aEvent.getDocument();
		int currentLinesCount = document.getNumberOfLines();
		int modificationLine;

		try {
			modificationLine = document.getLineOfOffset(aEvent.getOffset());
		} catch (BadLocationException e) {
			RestPlugin.logError("Error retrieving modified line offset", e);
			return;
		}

		updateLine(modificationLine, currentLinesCount - pPreviousLinesCount);
	}

	/**
	 * Retrieves the current state of watched lines
	 * 
	 * @return the current state of watched lines
	 */
	public Map<Integer, IBlockDetector> getWatchedLines() {
		return pWatchedLines;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	/**
	 * Removes a line from the watched ones
	 * 
	 * @param aLine
	 *            Line to be removed
	 */
	public void removeLine(final int aLine) {
		pWatchedLines.remove(aLine);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("LinePosition[");

		for (Entry<Integer, IBlockDetector> entry : pWatchedLines.entrySet()) {
			builder.append('(');
			builder.append(entry.getKey());
			builder.append(',');
			builder.append(entry.getValue());
			builder.append(')');
			builder.append(',');
		}

		builder.append(']');
		return builder.toString();
	}

	/**
	 * Updates the watched lines value according to the given block size
	 * modification.
	 * 
	 * @param aLine
	 *            First line of the block
	 * @param aDetector
	 *            The detector used for detecting this block
	 * @param aOldLastLine
	 *            Last line of the block, before its modification
	 * @param aNewLastLine
	 *            Last line of the block, after its modification
	 */
	public void updateBlockSize(final int aLine,
			final IBlockDetector aDetector, final int aOldLastLine,
			final int aNewLastLine) {

		// Add the line to the updated ones
		pWatchedLines.put(aLine, aDetector);

		// Update lines
		final int delta = aNewLastLine - aOldLastLine;
		Map<Integer, IBlockDetector> newMap = new TreeMap<Integer, IBlockDetector>();

		for (Entry<Integer, IBlockDetector> entry : pWatchedLines.entrySet()) {

			int line = entry.getKey();
			if (line <= aLine) {
				// Simple conservation
				newMap.put(line, entry.getValue());

			} else if (line > aOldLastLine) {
				newMap.put(line + delta, entry.getValue());
			}
		}

		pWatchedLines = newMap;
		pAlreadyUpdated = true;
	}

	/**
	 * Updates lines according to a simple document modification
	 * 
	 * @param aLine
	 *            Modification first line
	 * @param aAddedLines
	 *            Number of lines added by the modification (negative on
	 *            removal)
	 */
	public void updateLine(final int aLine, final int aAddedLines) {

		// Update lines
		Map<Integer, IBlockDetector> newMap = new TreeMap<Integer, IBlockDetector>();

		for (Entry<Integer, IBlockDetector> entry : pWatchedLines.entrySet()) {

			int line = entry.getKey();
			if (line <= aLine) {
				// Simple conservation
				newMap.put(line, entry.getValue());

			} else if (line + aAddedLines > aLine) {
				// Forget line that are moving upper than the base line (deleted
				// ones)

				newMap.put(line + aAddedLines, entry.getValue());
			}
		}

		pWatchedLines = newMap;
	}
}
