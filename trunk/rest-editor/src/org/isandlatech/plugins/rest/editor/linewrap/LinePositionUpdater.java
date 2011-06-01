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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;

/**
 * Updates all document positions of the given category to store the offset of
 * there associated line
 * 
 * @author Thomas Calmant
 */
public class LinePositionUpdater implements IDocumentListener {

	/** Lines to update */
	private Set<Integer> pWatchedLines;

	/** Update needed flag */
	private boolean pAlreadyUpdated;

	/** Number of lines in the document before the update */
	private int pPreviousLinesCount;

	/**
	 * Stores the category of positions to update
	 */
	public LinePositionUpdater() {
		pWatchedLines = new HashSet<Integer>();
		pAlreadyUpdated = false;
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
			e.printStackTrace();
			return;
		}

		updateLine(modificationLine, currentLinesCount - pPreviousLinesCount);
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

		for (int line : pWatchedLines) {
			builder.append(line);
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
	 * @param aOldLastLine
	 *            Last line of the block, before its modification
	 * @param aNewLastLine
	 *            Last line of the block, after its modification
	 */
	public void updateBlockSize(final int aLine, final int aOldLastLine,
			final int aNewLastLine) {

		// Add the line to the updated ones
		pWatchedLines.add(aLine);

		// Update lines
		final int delta = aNewLastLine - aOldLastLine;

		Integer[] currentLines = pWatchedLines.toArray(new Integer[0]);
		Set<Integer> newLinesSet = new HashSet<Integer>();

		for (int line : currentLines) {

			if (line <= aLine) {
				// Simple conservation
				newLinesSet.add(line);

			} else {
				if (line > aOldLastLine) {
					newLinesSet.add(line + delta);
				}
			}
		}

		pWatchedLines = newLinesSet;
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
		Integer[] currentLines = pWatchedLines.toArray(new Integer[0]);
		Set<Integer> newLinesSet = new HashSet<Integer>();

		for (int line : currentLines) {

			if (line <= aLine) {

				// Simple conservation
				newLinesSet.add(line);

			} else if (line + aAddedLines > aLine) {
				// Forget line that are moving upper than the base line (deleted
				// ones)

				newLinesSet.add(line + aAddedLines);
			}
		}

		pWatchedLines = newLinesSet;
	}
}
