/**
 * File:   RestContentOutlinePage.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.outline;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
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

	/**
	 * Available directions for hierarchy movements
	 * 
	 * @author Thomas Calmant
	 */
	enum Direction {
		UP, DOWN, LEFT, RIGHT,
	}

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

		// TODO insert hierarchy actions to the outline toolbar

		TreeViewer tree = getTreeViewer();
		tree.setContentProvider(new SectionContentProvider(this));
		tree.setLabelProvider(new SectionLabelProvider());

		tree.setInput(pParentEditor.getEditorInput());
		tree.expandAll();
	}

	/**
	 * Retrieves the region corresponding to the given section and all its
	 * children
	 * 
	 * @param aSectionNode
	 *            Section to select
	 * @return The section region
	 */
	protected IRegion getCompleteSection(final TreeData aSectionNode) {

		// Compute the section start
		int offset = getCompleteSectionOffset(aSectionNode);

		// Compute the length
		int length = getCompleteSectionLength(aSectionNode);

		return new Region(offset, length);
	}

	/**
	 * Computes the length of the complete section content, including title
	 * upper line.
	 * 
	 * @param aSectionNode
	 *            The section to handle
	 * @return The section length, 0 on error
	 */
	protected int getCompleteSectionLength(final TreeData aSectionNode) {

		if (aSectionNode == null) {
			return 0;
		}

		IDocument document = aSectionNode.getDocument();
		TreeData nextNode = aSectionNode.getNext();

		int offset = getCompleteSectionOffset(aSectionNode);
		int length = 0;

		if (nextNode == null) {
			// Select everything until the end of document
			length = document.getLength() - offset;

		} else {
			// Select everything until the beginning of the next section
			length = getCompleteSectionOffset(nextNode) - offset;
		}

		return Math.max(length, 0);
	}

	/**
	 * Returns the real offset of the given section : the first character offset
	 * of the title or the decorative upper line
	 * 
	 * @param aSectionNode
	 *            Section to handle
	 * @return The given section real offset, upper line included, 0 on error
	 */
	protected int getCompleteSectionOffset(final TreeData aSectionNode) {

		IDocument document = aSectionNode.getDocument();
		int offset = aSectionNode.getLineOffset();

		if (document != null && aSectionNode.isUpperlined()) {
			// Line - 1, 1 based
			int line = aSectionNode.getLine() - 2;

			try {
				offset = document.getLineOffset(line);
			} catch (BadLocationException e) {
				offset = aSectionNode.getLineOffset();
			}
		}

		return Math.max(offset, 0);
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

	protected boolean moveSection(final TreeData aSectionNode,
			final Direction aDirection) {

		TreeData targetNode = null;
		IDocument document = aSectionNode.getDocument();
		IRegion sourceSection = getCompleteSection(aSectionNode);
		int targetOffset = 0;

		switch (aDirection) {
		case UP:
			// TODO : move up !
			break;

		case DOWN:
			targetNode = aSectionNode.getNext();
			// Handle the first replacement : text moves up
			targetOffset -= sourceSection.getLength();
			// Move *after* the complete section, not only its title
			targetOffset += getCompleteSectionLength(targetNode);
			break;

		case LEFT:
			// TODO: only replace section title decoration
			break;

		case RIGHT:
			// TODO: only replace section title decoration
			break;
		}

		if (targetNode == null
				|| targetNode.getLevel() != aSectionNode.getLevel()) {
			return false;
		}

		targetOffset += getCompleteSectionOffset(targetNode);

		// Movement by 2 replacements...
		String sectionText;
		try {
			sectionText = document.get(sourceSection.getOffset(),
					sourceSection.getLength());

			document.replace(sourceSection.getOffset(),
					sourceSection.getLength(), "");

			document.replace(targetOffset, 0, sectionText);

		} catch (BadLocationException e) {
			e.printStackTrace();
			return false;
		}

		update();
		return true;
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

			int sectionTitleOffset = selectedElement.getLineOffset();
			if (sectionTitleOffset < 0) {
				sectionTitleOffset = 0;
			}

			IRegion region = getCompleteSection(selectedElement);

			pParentEditor.setHighlightRange(region.getOffset(),
					region.getLength(), true);

			/*
			 * pParentEditor.selectAndReveal(region.getOffset(),
			 * region.getLength());
			 * 
			 * moveSection(selectedElement, Direction.DOWN);
			 */
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
