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
import org.isandlatech.plugins.rest.editor.linewrap.HardLineWrap.WrapResult;

/**
 * Modifies the document content to avoid lines above a preferred length
 * 
 * @author Thomas Calmant
 */
public class HardLineWrapAutoEdit implements IAutoEditStrategy {

	/** Document position updater */
	private LinePositionUpdater pLineUpdater;

	/** Line wrapper */
	private final HardLineWrap pWrapper;

	/**
	 * Prepares members : line wrapper and line position updater
	 */
	public HardLineWrapAutoEdit() {
		pWrapper = new org.isandlatech.plugins.rest.editor.linewrap.HardLineWrap();
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

		// Set the line updater, if needed
		if (pLineUpdater == null) {
			pLineUpdater = new LinePositionUpdater();
			aDocument.addDocumentListener(pLineUpdater);
		}
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

		WrapResult result = pWrapper.wrapRegion(aDocument, aCommand, 80);

		int firstLine = result.getFirstLine();
		int newLastLine = result.getNewLastLine();

		if (firstLine >= 0 && pLineUpdater != null) {

			if (newLastLine - firstLine <= 0) {
				// Remove line if the block is only 1 line long (or on error...)
				pLineUpdater.removeLine(firstLine);

			} else {
				// Watch the block if its length is more than 1 line
				pLineUpdater.updateBlockSize(firstLine,
						result.getOldLastLine(), newLastLine);
			}
		}
	}
}
