/**
 * File:   HardLineWrapAutoEdit.java
 * Author: Thomas Calmant
 * Date:   17 mai 2011
 */
package org.isandlatech.plugins.rest.editor.linewrap;

import net.sourceforge.texlipse.editor.HardLineWrap;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;

/**
 * Modifies the document content to avoid lines above a preferred length
 * 
 * @author Thomas Calmant
 */
public class HardLineWrapAutoEdit implements IAutoEditStrategy {

	/** Category of the positions that indicates hard wrapped lines */
	public static final String WRAP_POSITION_CATEGORY = "ReST_Editor_line_wrap_";

	// private final Set<Integer> pModifiedLines;

	/** Document position updater */
	private final IPositionUpdater pPositionUpdater;

	/** Modified Texlipse line wrapper */
	private final HardLineWrap pWrapper;

	/**
	 * Prepares members : line wrapper and position updater
	 */
	public HardLineWrapAutoEdit() {
		pWrapper = new HardLineWrap();
		pPositionUpdater = new DefaultPositionUpdater(WRAP_POSITION_CATEGORY);
		// pModifiedLines = new HashSet<Integer>();
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

		if (!aCommand.doit) {
			return;
		}

		try {
			wrapLine(aDocument, aCommand);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prepares the position category for the given document
	 * 
	 * @param aDocument
	 *            Document to set up
	 */
	public void setupPositionCategory(final IDocument aDocument) {

		// Adds the position category
		if (!aDocument.containsPositionCategory(WRAP_POSITION_CATEGORY)) {
			aDocument.addPositionCategory(WRAP_POSITION_CATEGORY);
		}

		// Set the position updater, if needed
		for (IPositionUpdater updater : aDocument.getPositionUpdaters()) {
			if (pPositionUpdater.equals(updater)) {
				return;
			}
		}

		aDocument.addPositionUpdater(pPositionUpdater);
	}

	/**
	 * Hards wrap the current line if its length goes over the preferred limit.
	 * 
	 * Inspired by Texlipse hard line wrap.
	 * 
	 * @param aDocument
	 *            Currently edited document
	 * @param aCommand
	 *            Document customization command
	 * @throws BadLocationException
	 *             Document command gives out of bound values
	 */
	private void wrapLine(final IDocument aDocument,
			final DocumentCommand aCommand) throws BadLocationException {

		pWrapper.wrapRegion(aDocument, aCommand, 80);
	}
}
