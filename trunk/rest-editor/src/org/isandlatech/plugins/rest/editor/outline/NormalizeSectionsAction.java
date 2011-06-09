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

package org.isandlatech.plugins.rest.editor.outline;

import org.eclipse.jface.action.Action;
import org.isandlatech.plugins.rest.i18n.Messages;

/**
 * Outline page action : normalize sections markers
 * 
 * @author Thomas Calmant
 */
public class NormalizeSectionsAction extends Action {

	/** Parent outline */
	private RestContentOutlinePage pOutline;

	/**
	 * Prepares the normalization action
	 * 
	 * @param aOutlinePage
	 *            The parent outline page
	 */
	public NormalizeSectionsAction(final RestContentOutlinePage aOutlinePage) {
		pOutline = aOutlinePage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		return Messages.getString("outline.normalize");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {

		// First refresh : be sure we have a correct content representation
		pOutline.update();

		// Do the work
		TreeData rootElement = pOutline.getContentProvider().getRoot();
		OutlineUtil.normalizeSectionsMarker(rootElement);

		// Refresh outline
		pOutline.update();
	}
}
