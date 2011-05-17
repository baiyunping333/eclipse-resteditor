/**
 * File:   RestContentOutlinePage.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.outline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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

	private boolean pNormalize;

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
		pNormalize = true;
	}

	/**
	 * Collects all nodes with an ID within aFirst and a aLast (included)
	 * 
	 * @param aNode
	 *            Base node to search in
	 * @param aFirst
	 *            First ID to collect
	 * @param aLast
	 *            Last ID to collect
	 * @param aCurrentList
	 *            Growing list containing searched nodes
	 * @return True on full success (all nodes were found), else false
	 */
	private boolean collectNodes(final TreeData aNode, final int aFirst,
			final int aLast, final List<TreeData> aCurrentList) {

		int nodeId = aNode.getId();

		// Add the current if it is in the selection range
		if (nodeId >= aFirst && nodeId <= aLast) {
			aCurrentList.add(aNode);
		}

		// Stop the recursion if we found the last one
		if (nodeId == aLast) {
			return true;
		}

		// Continue to children...
		for (TreeData child : aNode.getChildrenArray()) {

			// Stop on the first child who stopped
			if (collectNodes(child, aFirst, aLast, aCurrentList)) {
				return true;
			}
		}

		return false;
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

		// Get the outline page toolbar
		IToolBarManager toolbarManager = getSite().getActionBars()
				.getToolBarManager();

		// Insert the refresh button
		toolbarManager.add(new RefreshOutlineAction(this));

		// Insert the normalize button
		toolbarManager.add(new NormalizeSectionsAction(this));

		// Insert hierarchy actions to the outline toolbar
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

	@SuppressWarnings("unchecked")
	private void normalizeSelection(
			final IStructuredSelection aStructuredSelection) {

		List<TreeData> oldSelection = aStructuredSelection.toList();
		String message = "";
		for (TreeData data : oldSelection) {
			message += data + "\n";
		}

		DebugPlugin.logMessage(message, null);

		// Be sure we have the good order...
		Collections.sort(oldSelection);

		TreeData firstNode = oldSelection.get(0);
		TreeData lastNode = oldSelection.get(oldSelection.size() - 1);

		int distance = lastNode.getId() - firstNode.getId();

		// Selection complete, no work to do
		if (distance == oldSelection.size() - 1) {
			return;
		}

		// List all nodes between those two ones
		List<TreeData> newSelectionContent = new ArrayList<TreeData>(distance);

		if (!collectNodes(pContentProvider.getRoot(), firstNode.getId(),
				lastNode.getId(), newSelectionContent)) {
			// Not all nodes were found
			return;
		}

		if (newSelectionContent.size() > oldSelection.size()) {

			StructuredSelection newSelection = new StructuredSelection(
					newSelectionContent);
			setSelection(newSelection);
		}
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

		ISelection selection = getSelection();
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

			// Make the selection contiguous
			if (pNormalize) {
				normalizeSelection(structuredSelection);
			}
		}
	}

	public void setNormalizeSelection(final boolean aNormalize) {
		pNormalize = aNormalize;
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
