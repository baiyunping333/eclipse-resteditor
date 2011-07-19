/*******************************************************************************
 * Copyright (c) 2011 isandlaTech, Olivier Gattaz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olivier Gattaz (isandlaTech) - initial API and implementation
 *******************************************************************************/

package org.isandlatech.plugins.rest.editor;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy;
import org.eclipse.ui.texteditor.spelling.SpellingService;
import org.isandlatech.plugins.rest.RestPlugin;

/**
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 09/06/2011 (dd/mm/yy)
 */
public class RestSpellingReconcileStrategy extends SpellingReconcileStrategy {

	/**
	 * @param viewer
	 * @param spellingService
	 */
	public RestSpellingReconcileStrategy(final ISourceViewer viewer,
			final SpellingService spellingService) {
		super(viewer, spellingService);
		// TODO: retrieve and keep the document to be able to get the list of
		// partition of the region in the "reconcile" methods.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy#getContentType
	 * ()
	 */
	@Override
	protected IContentType getContentType() {
		return RestPlugin.REST_CONTENT_TYPE;
	}

	/**
	 * @return
	 */
	private boolean isRestSpellingEnabled() {
		return RestPlugin.getDefault().getPreferenceStore()
				.getBoolean(SpellingService.PREFERENCE_SPELLING_ENABLED);
	}

	/*
	 * @see
	 * org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.
	 * eclipse.jface.text.reconciler.DirtyRegion,org.eclipse.jface.text.IRegion)
	 */
	@Override
	public void reconcile(final DirtyRegion dirtyRegion, final IRegion subRegion) {
		if (isRestSpellingEnabled()) {
			// TODO: implement a filter to check only a subset of the partitions
			// of the document. @see TextUtilities.computePartitioning()
			super.reconcile(dirtyRegion, subRegion);
		}
	}

	/*
	 * @see
	 * org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.
	 * eclipse.jface.text.IRegion)
	 */
	@Override
	public void reconcile(final IRegion region) {
		if (isRestSpellingEnabled()) {
			// TODO: implement a filter to check only a subset of the partitions
			// of the document. @see TextUtilities.computePartitioning()
			super.reconcile(region);
		}
	}

}
