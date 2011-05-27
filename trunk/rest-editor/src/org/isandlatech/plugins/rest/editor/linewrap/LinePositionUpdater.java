/**
 * File:   LinePositionUpdater.java
 * Author: Thomas Calmant
 * Date:   27 mai 2011
 */
package org.isandlatech.plugins.rest.editor.linewrap;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.Position;

/**
 * Updates all document positions of the given category to store the offset of
 * there associated line
 * 
 * @author Thomas Calmant
 */
public class LinePositionUpdater implements IPositionUpdater {

	/** Position category to be used */
	private String pCategory;

	/**
	 * Stores the category of positions to update
	 * 
	 * @param aCategory
	 *            Category of positions to update
	 */
	public LinePositionUpdater(final String aCategory) {
		pCategory = aCategory;
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

		// Compute the difference between the old and the new offset
		final int delta = aEvent.getText().length() - aEvent.getLength();

		if (delta == 0) {
			return;
		}

		// Retrieve all needed positions
		Position[] positions;
		try {
			positions = document.getPositions(pCategory);
		} catch (BadPositionCategoryException e) {
			e.printStackTrace();
			// No need to go further...
			return;
		}

		// Update positions
		for (Position position : positions) {

			// Ignore positions before the modification
			if (position.offset < modificationOffset) {
				continue;
			}

			try {
				final int oldLine = document.getLineOfOffset(position
						.getOffset());

				System.out.println("Old offset : " + position.getOffset());

				final int newLine = document.getLineOfOffset(position
						.getOffset() + delta);

				position.setOffset(document.getLineOffset(newLine));
				position.setLength(0);

				System.out.println("New offset : " + position.getOffset());

				System.out
						.println("Pos update : " + oldLine + " -> " + newLine);

			} catch (BadLocationException ex) {
				ex.printStackTrace();

				// Bad position : remove it from document
				try {
					document.removePosition(pCategory, position);
				} catch (BadPositionCategoryException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("\n");
	}
}
