/**
 * File:   RestContentOutlinePage.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.outline;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.isandlatech.plugins.rest.editor.RestEditor;

/**
 * @author Thomas Calmant
 * 
 */
public class RestContentOutlinePage extends ContentOutlinePage {

	/** Parent document provider */
	private IDocumentProvider pDocumentProvider;

	/** Parent ReST source editor */
	private RestEditor pParentEditor;

	/**
	 * Configures the outline page
	 * 
	 * @param aDocumentProvider
	 *            The document provider used by the parent editor
	 * @param aParentEditor
	 *            The parent ReST editor
	 */
	public RestContentOutlinePage(final IDocumentProvider aDocumentProvider,
			final RestEditor aParentEditor) {
		super();

		pDocumentProvider = aDocumentProvider;
		pParentEditor = aParentEditor;
	}

	@Override
	public void createControl(final Composite parent) {
		// Creates the outline view control
		super.createControl(parent);

		TreeViewer tree = getTreeViewer();
		tree.setContentProvider(new SectionContentProvider(this));
		tree.setLabelProvider(new SectionLabelProvider());

		tree.setInput(pParentEditor.getEditorInput());
		tree.expandAll();
	}

	/**
	 * Retrieves the document provider associated to the outline page
	 * 
	 * @return The document provider associated to the outline page
	 */
	public IDocumentProvider getDocumentProvider() {
		return pDocumentProvider;
	}

	@Override
	public TreeViewer getTreeViewer() {
		return super.getTreeViewer();
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent aEvent) {
		super.selectionChanged(aEvent);

		ISelection selection = aEvent.getSelection();
		if (selection.isEmpty()) {
			pParentEditor.resetHighlightRange();

		} else if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			TreeData selectedElement = (TreeData) structuredSelection
					.getFirstElement();

			int sectionTitleOffset = selectedElement.getLineOffset();
			if (sectionTitleOffset < 0) {
				sectionTitleOffset = 0;
			}

			pParentEditor.setHighlightRange(sectionTitleOffset, 0, true);

		}
	}

	/**
	 * Updates the outline page view
	 */
	public void update() {

		TreeViewer tree = getTreeViewer();
		if (tree != null) {

			Control control = tree.getControl();
			if (control != null) {
				control.setRedraw(false);
				tree.setInput(pParentEditor.getEditorInput());
				tree.expandAll();
				control.setRedraw(true);
			}
		}
	}
}
