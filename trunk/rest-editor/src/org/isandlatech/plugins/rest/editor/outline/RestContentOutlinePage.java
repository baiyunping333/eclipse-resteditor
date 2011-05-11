/**
 * File:   RestContentOutlinePage.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.outline;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.text.IRegion;
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
 * Outline page for ReST documents
 * 
 * @author Thomas Calmant
 */
public class RestContentOutlinePage extends ContentOutlinePage {

	/** Outline page content provider */
	private SectionContentProvider pContentProvider;

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
		pContentProvider = new SectionContentProvider(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(
	 * org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(final Composite parent) {
		// Creates the outline view control
		super.createControl(parent);

		// Prepare the tree view
		TreeViewer tree = getTreeViewer();
		tree.setContentProvider(pContentProvider);
		tree.setLabelProvider(new SectionLabelProvider());

		tree.setInput(pParentEditor.getEditorInput());
		tree.expandAll();

		// Insert hierarchy actions to the outline toolbar
		IToolBarManager toolbarManager = getSite().getActionBars()
				.getToolBarManager();

		toolbarManager.add(new RefreshOutlineAction(this));

		HierarchyAction[] actions = HierarchyAction.createActions(this);
		for (HierarchyAction action : actions) {
			toolbarManager.add(action);
		}
	}

	/**
	 * @return the contentProvider
	 */
	public SectionContentProvider getContentProvider() {
		return pContentProvider;
	}

	/**
	 * Retrieves the document provider associated to the outline page
	 * 
	 * @return The document provider associated to the outline page
	 */
	public IDocumentProvider getDocumentProvider() {
		return pDocumentProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.contentoutline.ContentOutlinePage#getTreeViewer()
	 */
	@Override
	public TreeViewer getTreeViewer() {
		return super.getTreeViewer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.contentoutline.ContentOutlinePage#selectionChanged
	 * (org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
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

			IRegion sectionRegion = OutlineUtil
					.getCompleteSection(selectedElement);

			pParentEditor.setHighlightRange(sectionRegion.getOffset(),
					sectionRegion.getLength(), true);

			pParentEditor.selectAndReveal(sectionRegion.getOffset(), 0);
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
