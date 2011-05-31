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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;

/**
 * Updates all document positions of the given category to store the offset of
 * there associated line
 * 
 * @author Thomas Calmant
 */
public class LinePositionUpdater implements IPositionUpdater {

	/** Lines to update */
	private Set<Integer> pWatchedLines;

	/**
	 * Stores the category of positions to update
	 */
	public LinePositionUpdater() {
		pWatchedLines = new HashSet<Integer>();
	}

	/**
	 * Adds a line to the watched ones
	 * 
	 * @param aLine
	 *            Line to be added
	 */
	public void addLine(final int aLine) {
		pWatchedLines.add(aLine);
	}

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
	 * @see
	 * org.eclipse.jface.text.IPositionUpdater#update(org.eclipse.jface.text
	 * .DocumentEvent)
	 */
	@Override
	public void update(final DocumentEvent aEvent) {

		// Get event constants
		final IDocument document = aEvent.getDocument();
		final int modificationOffset = aEvent.getOffset();

		int modificationBegin;
		int modificationNewEnd;
		int modificationOldEnd;

		try {
			modificationBegin = document.getLineOfOffset(modificationOffset);

			modificationNewEnd = document.getLineOfOffset(modificationOffset
					+ aEvent.getText().length());

			modificationOldEnd = document.getLineOfOffset(modificationOffset
					+ aEvent.getLength());

		} catch (BadLocationException e1) {
			e1.printStackTrace();
			return;
		}

		System.out.println("-----\nModifs : " + modificationBegin + " -> "
				+ modificationOldEnd + " <- " + modificationNewEnd);

		int delta = modificationNewEnd - modificationOldEnd;
		Integer[] currentLines = pWatchedLines.toArray(new Integer[0]);
		Set<Integer> newLinesSet = new HashSet<Integer>();

		for (int i = 0; i < currentLines.length; i++) {
			int line = currentLines[i];

			if (line >= modificationOldEnd) {
				// Updates lines after the modification
				newLinesSet.add(line + delta);
				System.out.println("Update " + line + " -> " + (line + delta));

			} else if (line >= modificationBegin && line < modificationNewEnd) {
				// Aggregate block lines
				newLinesSet.add(modificationBegin);
				System.out.println("Aggregating " + line + " btw "
						+ modificationBegin + " - " + modificationNewEnd);

			} else if (line > modificationNewEnd && line < modificationOldEnd) {
				// To be deleted
				System.out.println("Remove " + line + " btw "
						+ modificationNewEnd + " and " + modificationOldEnd);

			} else {
				// Non-updated lines
				newLinesSet.add(line);
			}
		}

		pWatchedLines = newLinesSet;

		System.out.println(Arrays.toString(pWatchedLines.toArray()));
		System.out.println("\n");
	}
}
