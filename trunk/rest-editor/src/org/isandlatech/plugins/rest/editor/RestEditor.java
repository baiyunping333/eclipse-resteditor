/**
 * File:   RestEditor.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
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

	/** Inactivity time to wait before updating the outline */
	public static final int ACTION_UPDATE_TIMEOUT = 500;

	/** Object called by the action update timer */
	private Runnable pActionUpdater;

	/** Source viewer configuration */
	private RestViewerConfiguration pConfiguration;

	/** Dummy display to access timers */
	private Display pDummyDisplay;

	/** Outline page */
	private RestContentOutlinePage pOutlinePage;

	/**
	 * ReST editor entry point
	 */
	public RestEditor() {
		super();
		pDummyDisplay = Display.getDefault();

		// Prepare the updater
		pActionUpdater = new Runnable() {

			@Override
			public void run() {
				// Call the object one
				runnableUpdateContentDependentActions();
			}
		};
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
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);
		updateConfigurationDocument();
	}

	@Override
	public void dispose() {
		if (pOutlinePage != null) {
			pOutlinePage.dispose();
		}

		super.dispose();
	}

	@Override
	protected void doSetInput(final IEditorInput input) throws CoreException {
		super.doSetInput(input);
		updateConfigurationDocument();
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

	private void runnableUpdateContentDependentActions() {

		// Update outline page on content change
		if (pOutlinePage != null) {
			pOutlinePage.update();
		}
	}

	/**
	 * Updates the document instance in the configuration, if needed.
	 */
	private void updateConfigurationDocument() {

		ISourceViewer viewer = getSourceViewer();
		if (viewer != null) {
			IDocument document = viewer.getDocument();
			if (document != null) {
				pConfiguration.setDocument(document);
			}
		}
	}

	/**
	 * Updates the content dependent actions and the outline page
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#updateContentDependentActions()
	 */
	@Override
	protected void updateContentDependentActions() {
		super.updateContentDependentActions();

		// Cancel the current timer
		pDummyDisplay.timerExec(-1, pActionUpdater);
		// Set the "new" one
		pDummyDisplay.timerExec(ACTION_UPDATE_TIMEOUT, pActionUpdater);
	}
}
