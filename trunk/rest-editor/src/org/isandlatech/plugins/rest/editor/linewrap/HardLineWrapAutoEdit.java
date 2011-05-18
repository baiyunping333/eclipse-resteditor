/**
 * File:   HardLineWrapAutoEdit.java
 * Author: Thomas Calmant
 * Date:   17 mai 2011
 */
package org.isandlatech.plugins.rest.editor.linewrap;

import net.sourceforge.texlipse.editor.HardLineWrap;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;

/**
 * @author Thomas Calmant
 */
public class HardLineWrapAutoEdit implements IAutoEditStrategy {

	private HardLineWrap pWrapper = new HardLineWrap();

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

		pWrapper.doWrapB(aDocument, aCommand);
	}
}
