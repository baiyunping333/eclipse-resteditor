/**
 * File:   HierarchyAction.java
 * Author: Thomas Calmant
 * Date:   11 mai 2011
 */
package org.isandlatech.plugins.rest.editor.outline;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
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
			return rotateImage(sharedImages
					.getImage(ISharedImages.IMG_TOOL_FORWARD));

		case B_DOWN:
			return rotateImage(sharedImages
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
		IRegion sourceSection = getCompleteSection(aSectionNode);
		int targetOffset = 0;

		switch (aDirection) {
		case A_UP:
			targetNode = aSectionNode.getPrevious();

			// No need to handle first replacement nor the target section length
			break;

		case B_DOWN:
			targetNode = aSectionNode.getNext();

			// Move *after* the complete section, not only its title
			targetOffset = getCompleteSectionLength(targetNode);

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
		targetOffset += getCompleteSectionOffset(targetNode);
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
		char newDecorator = pOutline.getContentProvider()
				.getDecorationForLevel(newLevel);

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

	/**
	 * Makes a new image which is a 45° counter-clockwise rotation of the given
	 * one
	 * 
	 * @param aImage
	 *            Base image
	 * @return A rotated image
	 */
	private ImageDescriptor rotateImage(final Image aImage) {

		ImageData srcData = aImage.getImageData();
		if (srcData == null) {
			return null;
		}

		ImageData destData = new ImageData(srcData.height, srcData.width,
				srcData.depth, srcData.palette);

		/* rotate by rearranging the pixels */
		for (int i = 0; i < srcData.width; i++) {

			for (int j = 0; j < srcData.height; j++) {

				// Color
				int pixel = srcData.getPixel(i, j);
				destData.setPixel(j, srcData.width - 1 - i, pixel);

				// Transparency
				int alpha = srcData.getAlpha(i, j);
				destData.setAlpha(j, srcData.width - 1 - i, alpha);

			}
		}

		return ImageDescriptor.createFromImageData(destData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {

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
	}
}
