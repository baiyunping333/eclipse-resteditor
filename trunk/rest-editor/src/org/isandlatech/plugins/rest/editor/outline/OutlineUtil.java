/**
 * File:   OutlineUtil.java
 * Author: Thomas Calmant
 * Date:   11 mai 2011
 */
package org.isandlatech.plugins.rest.editor.outline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;

/**
 * Utility class for outline operations
 * 
 * @author Thomas Calmant
 */
public class OutlineUtil {

	/**
	 * Retrieves the region corresponding to the given section and all its
	 * children
	 * 
	 * @param aSectionNode
	 *            Section to select
	 * @return The section region
	 */
	public static IRegion getCompleteSection(final TreeData aSectionNode) {

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
	public static int getCompleteSectionLength(final TreeData aSectionNode) {

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
	public static int getCompleteSectionOffset(final TreeData aSectionNode) {

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
	 * Selects the outline nodes corresponding to the given previous selection
	 * 
	 * @param aOutline
	 *            Outline page
	 * @param aPreviousSelection
	 *            Previous selection state
	 */
	public static void postUpdateSelection(
			final RestContentOutlinePage aOutline,
			final TreeSelection aPreviousSelection) {

		List<TreePath> newSelectedPaths = new ArrayList<TreePath>(
				aPreviousSelection.size());

		TreeData treeRoot = aOutline.getContentProvider().getRoot();

		Iterator<?> iterator = aPreviousSelection.iterator();
		while (iterator.hasNext()) {
			TreeData nodeData = (TreeData) iterator.next();

			// Find the new node corresponding to the old one
			TreeData newNodedata = treeRoot.find(nodeData);
			if (newNodedata != null) {
				newSelectedPaths.add(newNodedata.getTreePath());
			}
		}

		TreeSelection newSelection = new TreeSelection(
				newSelectedPaths.toArray(new TreePath[0]));

		aOutline.setSelection(newSelection);
	}
}
