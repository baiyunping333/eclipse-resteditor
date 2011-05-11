/**
 * File:   RestContentOutlinePage.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.outline;

import java.util.Arrays;

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
		DOWN, LEFT, RIGHT, UP,
	}

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

	/**
	 * Change section and sub-sections decoration to correspond to its new level
	 * 
	 * @param aSectionNode
	 *            Section to be modified
	 * @param aIncrement
	 *            Direction of level modification (+1, -1)
	 */
	private void changeSectionLevel(final TreeData aSectionNode, int aIncrement) {

		if (aIncrement == 0) {
			return;
		}

		// Normalize level modification
		if (aIncrement < 0) {
			aIncrement = -1;
		}

		if (aIncrement > 0) {
			aIncrement = 1;
		}

		if (aSectionNode.getLevel() + aIncrement < 1) {
			return;
		}

		// Let's go
		replaceDecorators(aSectionNode, aIncrement);

		for (TreeData node : aSectionNode.getChildrenArray()) {
			changeSectionLevel(node, aIncrement);
		}
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

		// TODO insert hierarchy actions to the outline toolbar

		TreeViewer tree = getTreeViewer();
		tree.setContentProvider(pContentProvider);
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

		if (aSectionNode == null) {
			return 0;
		}

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

	/**
	 * Handles hierarchy modification buttons
	 * 
	 * @param aSectionNode
	 *            Modified section
	 * @param aDirection
	 *            Direction of hierarchy modification
	 * @return True on success, else false
	 */
	protected boolean handleHierarchyMovement(final TreeData aSectionNode,
			final Direction aDirection) {

		TreeData targetNode = null;
		IDocument document = aSectionNode.getDocument();
		IRegion sourceSection = getCompleteSection(aSectionNode);
		int targetOffset = 0;

		switch (aDirection) {
		case UP:
			targetNode = aSectionNode.getPrevious();

			// No need to handle first replacement nor the target section length
			break;

		case DOWN:
			targetNode = aSectionNode.getNext();

			// Move *after* the complete section, not only its title
			targetOffset = getCompleteSectionLength(targetNode);

			// Handle the first replacement : text moves up
			targetOffset -= sourceSection.getLength();
			break;

		case LEFT:
			changeSectionLevel(aSectionNode, -1);
			update();
			return true;

		case RIGHT:
			changeSectionLevel(aSectionNode, +1);
			update();
			return true;
		}

		if (targetNode == null
				|| targetNode.getLevel() != aSectionNode.getLevel()) {
			return false;
		}

		// Target offset rebase
		targetOffset += getCompleteSectionOffset(targetNode);
		moveRegion(document, sourceSection, targetOffset);

		update();
		return true;
	}

	/**
	 * Moves the given region content to the given target offset. Uses
	 * {@link IDocument#replace(int, int, String)} method.
	 * 
	 * Reverts document content on error.
	 * 
	 * @param aDocument
	 *            Document to modify
	 * @param aMovedRegion
	 *            Region to be moved
	 * @param aTargetOffset
	 *            Target offset for region movement
	 * @return True on success, False on error
	 */
	private boolean moveRegion(final IDocument aDocument,
			final IRegion aMovedRegion, final int aTargetOffset) {

		// Save text content (in case of error)
		String documentText = aDocument.get();

		// Movement by 2 replacements...
		String sectionText;
		try {
			sectionText = aDocument.get(aMovedRegion.getOffset(),
					aMovedRegion.getLength());

			// Just to be sure we have a correct section separation
			if (!sectionText.endsWith("\n")) {
				sectionText += "\n\n";
			}

			// Remove current section text
			aDocument.replace(aMovedRegion.getOffset(),
					aMovedRegion.getLength(), "");

			// Insert text at its new position
			aDocument.replace(aTargetOffset, 0, sectionText);

		} catch (BadLocationException e) {
			// Revert text
			aDocument.set(documentText);

			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Replaces section title decoration lines
	 * 
	 * @param aSectionNode
	 *            The section to be modified
	 * @param aIncrement
	 *            The level modification indicator (+1, -1)
	 */
	private void replaceDecorators(final TreeData aSectionNode,
			final int aIncrement) {

		IDocument document = aSectionNode.getDocument();
		if (document == null) {
			return;
		}

		String endOfLine;
		try {
			endOfLine = document.getLineDelimiter(aSectionNode.getLine() - 1);

		} catch (BadLocationException e1) {
			endOfLine = "\n";
		}

		// Get the decoration character
		int newLevel = aSectionNode.getLevel() + aIncrement;
		char newDecorator = pContentProvider.getDecorationForLevel(newLevel);

		// Prepare the decoration line
		char[] decorationArray = new char[aSectionNode.getText().length()];
		Arrays.fill(decorationArray, newDecorator);
		String decorationLine = new String(decorationArray) + endOfLine;

		// Replace the upper line, if needed
		if (aSectionNode.isUpperlined()) {

			try {
				int upperline = aSectionNode.getLine() - 2;
				int upperlineOffset = document.getLineOffset(upperline);
				int upperlineLength = document.getLineLength(upperline);

				document.replace(upperlineOffset, upperlineLength,
						decorationLine);

			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		// Replace the under line
		try {
			int underline = aSectionNode.getLine();
			int underlineOffset = document.getLineOffset(underline);
			int underlineLength = document.getLineLength(underline);

			document.replace(underlineOffset, underlineLength, decorationLine);

		} catch (BadLocationException e) {
			e.printStackTrace();
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

			// pParentEditor.selectAndReveal(region.getOffset(),
			// region.getLength());
			//
			// handleHierarchyMovement(selectedElement, Direction.RIGHT);
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
