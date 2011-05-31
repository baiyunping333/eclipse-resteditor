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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.Position;

/**
 * Modifies the document content to avoid lines above a preferred length
 * 
 * @author Thomas Calmant
 */
public class HardLineWrapAutoEdit implements IAutoEditStrategy {

	/** Category of the positions that indicates hard wrapped lines */
	public static final String WRAP_POSITION_CATEGORY = "ReST_Editor_line_wrap_";

	/** Document position updater */
	private IPositionUpdater pPositionUpdater;

	/** Modified Texlipse line wrapper */
	private final org.isandlatech.plugins.rest.editor.linewrap.HardLineWrap pWrapper;

	/**
	 * Prepares members : line wrapper and position updater
	 */
	public HardLineWrapAutoEdit() {
		pWrapper = new org.isandlatech.plugins.rest.editor.linewrap.HardLineWrap();
		pPositionUpdater = new LinePositionUpdater(WRAP_POSITION_CATEGORY);
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
		} catch (BadPositionCategoryException e) {
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

		// aDocument.addPositionUpdater(pPositionUpdater);
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
	 * @throws BadPositionCategoryException
	 *             The wrap position couldn't be added
	 */
	private void wrapLine(final IDocument aDocument,
			final DocumentCommand aCommand) throws BadLocationException,
			BadPositionCategoryException {

		int modifiedBlockBegin = pWrapper.wrapRegion(aDocument, aCommand, 80);
		if (modifiedBlockBegin >= 0) {

			// On success, store modified block line
			int beginOffset = aDocument.getLineOffset(modifiedBlockBegin);
			aDocument.addPosition(WRAP_POSITION_CATEGORY, new Position(
					beginOffset));
		}
	}
}
