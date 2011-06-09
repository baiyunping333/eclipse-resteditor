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

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.isandlatech.plugins.rest.RestPlugin;
import org.isandlatech.plugins.rest.i18n.Messages;

/**
 * The hierarchy modification action, to be added to the outline page
 * 
 * @author Thomas Calmant
 */
public class HierarchyAction extends Action {

	/**
	 * Available directions for hierarchy movements
	 * 
	 * @author Thomas Calmant
	 */
	enum Direction {
		A_UP, B_DOWN, C_LEFT, D_RIGHT,
	}

	/**
	 * Creates an action array, with an action per direction
	 * 
	 * @param aParentPage
	 *            Parent outline page
	 * @return An array with all needed actions
	 */
	public static HierarchyAction[] createActions(
			final RestContentOutlinePage aParentPage) {

		HierarchyAction[] actions = new HierarchyAction[Direction.values().length];
		int i = 0;

		for (Direction direction : Direction.values()) {
			actions[i++] = new HierarchyAction(aParentPage, direction);
		}

		return actions;
	}

	/** Hierarchy modification direction */
	private Direction pDirection;

	/** The associated outline page */
	private RestContentOutlinePage pOutline;

	/**
	 * Sets up the hierarchy action
	 * 
	 * @param aParentPage
	 *            Parent outline page
	 * @param aDirection
	 *            Hierarchy modification direction
	 */
	public HierarchyAction(final RestContentOutlinePage aParentPage,
			final Direction aDirection) {
		super();

		pOutline = aParentPage;
		pDirection = aDirection;
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

	/**
	 * Returns an image associated to the direction, based on navigation icons.
	 * Rotates icons for A_UP and B_DOWN directions.
	 * 
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {

		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();

		switch (pDirection) {
		case C_LEFT:
			return sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK);

		case D_RIGHT:
			return sharedImages
					.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD);

		case A_UP:
			return RestPlugin.rotateImage(sharedImages
					.getImage(ISharedImages.IMG_TOOL_FORWARD));

		case B_DOWN:
			return RestPlugin.rotateImage(sharedImages
					.getImage(ISharedImages.IMG_TOOL_BACK));
		}

		// In case of new directions ...
		return super.getImageDescriptor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {

		switch (pDirection) {
		case A_UP:
			return Messages.getString("outline.hierarchy.up");

		case B_DOWN:
			return Messages.getString("outline.hierarchy.down");

		case C_LEFT:
			return Messages.getString("outline.hierarchy.left");

		case D_RIGHT:
			return Messages.getString("outline.hierarchy.right");
		}

		return super.getText();
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
		IRegion sourceSection = OutlineUtil.getCompleteSection(aSectionNode);
		int targetOffset = 0;

		switch (aDirection) {
		case A_UP:
			targetNode = aSectionNode.getPrevious();

			// No need to handle first replacement nor the target section length
			break;

		case B_DOWN:
			targetNode = aSectionNode.getNext();

			// Move *after* the complete section, not only its title
			targetOffset = OutlineUtil.getCompleteSectionLength(targetNode);

			// Handle the first replacement : text moves up
			targetOffset -= sourceSection.getLength();
			break;

		case C_LEFT:
			changeSectionLevel(aSectionNode, -1);
			return true;

		case D_RIGHT:
			changeSectionLevel(aSectionNode, +1);
			return true;
		}

		if (targetNode == null
				|| targetNode.getLevel() != aSectionNode.getLevel()) {
			return false;
		}

		// Target offset rebase
		targetOffset += OutlineUtil.getCompleteSectionOffset(targetNode);

		// Handle movement after last section : we may need to add some
		// blank lines
		if (aDirection == Direction.B_DOWN && targetNode.getNext() == null) {

			targetOffset += setEndOfDocument(document);
		}

		// Move !
		moveRegion(document, sourceSection, targetOffset);
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

		// Document EOL
		String endOfLine = TextUtilities.getDefaultLineDelimiter(aDocument);

		// Save text content (in case of error)
		String documentText = aDocument.get();

		// Movement by 2 replacements...
		String sectionText;
		try {
			sectionText = aDocument.get(aMovedRegion.getOffset(),
					aMovedRegion.getLength());

			// Just to be sure we have a correct section separation
			String endOfSection = endOfLine + endOfLine;
			if (!sectionText.endsWith(endOfSection)) {
				sectionText += endOfSection;
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
	protected void replaceDecorators(final TreeData aSectionNode,
			final int aIncrement) {

		// Get the decoration character
		int newLevel = aSectionNode.getLevel() + aIncrement;
		char newDecorator = pOutline.getContentProvider()
				.getDecorationForLevel(newLevel);

		OutlineUtil.replaceSectionMarker(aSectionNode, newDecorator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {

		// Do not handle selection changes
		pOutline.setNormalizeSelection(false);

		TreeSelection selectedNodes = (TreeSelection) pOutline.getSelection();

		Iterator<?> iterator = selectedNodes.iterator();
		while (iterator.hasNext()) {
			Object nodeData = iterator.next();

			// Just to be sure...
			if (nodeData instanceof TreeData) {
				handleHierarchyMovement((TreeData) nodeData, pDirection);

				// Update each time to have a valid tree
				pOutline.update();
			}
		}

		// Reset selection, with new data (positions...)
		OutlineUtil.postUpdateSelection(pOutline, selectedNodes);

		// Re-handle selection changes
		pOutline.setNormalizeSelection(true);
	}

	/**
	 * Adds an end of section sequence (2 ends of line sequence) at the end of
	 * the document, if needed.
	 * 
	 * @param aDocument
	 *            Document to be modified
	 * @return The number of characters appended to the document
	 */
	private int setEndOfDocument(final IDocument aDocument) {

		final String endOfLine = TextUtilities
				.getDefaultLineDelimiter(aDocument);
		final String endOfSection = endOfLine + endOfLine;

		try {

			final String endOfDocument = aDocument.get(aDocument.getLength()
					- endOfSection.length() - 1, endOfSection.length());

			if (!endOfDocument.equals(endOfSection)) {
				// Add blank lines, if needed
				String content = aDocument.get();
				content += endOfSection;
				aDocument.set(content);

				// Update target offset
				return endOfSection.length();
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return 0;
	}
}
