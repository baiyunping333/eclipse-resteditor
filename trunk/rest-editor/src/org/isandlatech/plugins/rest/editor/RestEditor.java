/**
 * File:   RestEditor.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.isandlatech.plugins.rest.editor.outline.RestContentOutlinePage;
import org.isandlatech.plugins.rest.i18n.Messages;

/**
 * reStructuredText editor main class
 * 
 * @author Thomas Calmant
 */
public class RestEditor extends TextEditor {

	/** Source viewer configuration */
	private RestViewerConfiguration pConfiguration;

	/** Outline page */
	private RestContentOutlinePage pOutlinePage;

	/**
	 * ReST editor entry point
	 */
	public RestEditor() {
		super();
	}

	@Override
	protected void createActions() {
		super.createActions();

		// Messages resource bundle
		ResourceBundle resBundle = Messages.getBundle();

		// Set up the content assistant (resource bundle needed)
		ContentAssistAction contentAssistAction = new ContentAssistAction(
				resBundle, "contentAssist.", this);

		contentAssistAction
				.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);

		setAction(ITextEditorActionConstants.CONTENT_ASSIST,
				contentAssistAction);
	}

	@Override
	public void dispose() {
		if (pOutlinePage != null) {
			pOutlinePage.dispose();
		}

		super.dispose();
	}

	@Override
	protected void editorSaved() {

		if (pOutlinePage != null) {
			pOutlinePage.update();
		}

		// Save actions...
		super.editorSaved();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class adapter) {

		// Request for a content outline page adapter
		if (IContentOutlinePage.class.equals(adapter)) {
			return getOutlinePage();
		}

		return super.getAdapter(adapter);
	}

	/**
	 * Retrieves the unique outline page instance for this editor
	 * 
	 * @return the unique outline page instance for this editor
	 */
	public RestContentOutlinePage getOutlinePage() {

		if (pOutlinePage == null) {
			pOutlinePage = new RestContentOutlinePage(getDocumentProvider(),
					this);
		}

		return pOutlinePage;
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();

		// Set the viewer configuration
		pConfiguration = new RestViewerConfiguration(this);
		setSourceViewerConfiguration(pConfiguration);
	}

	/**
	 * Do on-save operations
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#performSave(boolean,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void performSave(final boolean aOverwrite,
			final IProgressMonitor aProgressMonitor) {

		pConfiguration.onEditorPerformSave(getSourceViewer());
		super.performSave(aOverwrite, aProgressMonitor);
	}

	/**
	 * Do on-save operations
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#performSaveAs(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void performSaveAs(final IProgressMonitor aProgressMonitor) {

		// Perform treatments before saving the document...
		pConfiguration.onEditorPerformSave(getSourceViewer());

		super.performSaveAs(aProgressMonitor);
	}

	/**
	 * Updates the content dependent actions and the outline page
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#updateContentDependentActions()
	 */
	@Override
	protected void updateContentDependentActions() {
		super.updateContentDependentActions();

		// Update outline page on content change
		if (pOutlinePage != null) {
			pOutlinePage.update();
		}
	}
}
