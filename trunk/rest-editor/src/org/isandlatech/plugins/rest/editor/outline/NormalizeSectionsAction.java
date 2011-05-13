/**
 * File:   NormalizeSectionsAction.java
 * Author: Thomas Calmant
 * Date:   13 mai 2011
 */
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

		// Do the work
		TreeData rootElement = pOutline.getContentProvider().getRoot();
		OutlineUtil.normalizeSectionsMarker(rootElement);

		// Refresh outline
		pOutline.update();
	}
}
