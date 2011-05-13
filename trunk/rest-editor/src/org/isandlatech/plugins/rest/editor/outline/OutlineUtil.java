/**
 * File:   OutlineUtil.java
 * Author: Thomas Calmant
 * Date:   11 mai 2011
 */
package org.isandlatech.plugins.rest.editor.outline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.isandlatech.plugins.rest.RestPlugin;
import org.isandlatech.plugins.rest.parser.RestLanguage;
import org.isandlatech.plugins.rest.prefs.IEditorPreferenceConstants;

/**
 * Utility class for outline operations
 * 
 * @author Thomas Calmant
 */
public class OutlineUtil {

	/**
	 * Retrieves the region corresponding to the given section content with all
	 * its children
	 * 
	 * @param aSectionNode
	 *            Section to select
	 * @return The section content region
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
	 * Retrieves the region corresponding to the given section title, decorating
	 * lines included
	 * 
	 * @param aSectionNode
	 *            Section to select
	 * @return The section title block, null on error
	 */
	public static IRegion getSectionBlock(final TreeData aSectionNode) {

		IDocument document = aSectionNode.getDocument();
		if (document == null) {
			return null;
		}

		int blockOffset = getCompleteSectionOffset(aSectionNode);

		// Compute block length
		int blockLength = 0;
		int line = aSectionNode.getLine();

		// Upper line
		try {
			if (aSectionNode.isUpperlined()) {
				blockLength += document.getLineLength(line - 2);
			}

			// Title
			blockLength += document.getLineLength(line - 1);

			// Under line
			blockLength += document.getLineLength(line);

		} catch (BadLocationException e) {
			e.printStackTrace();
			return null;
		}

		return new Region(blockOffset, blockLength);
	}

	/**
	 * Rewrites section and subsections titles blocks to use the preferred
	 * marker for its level. Reads the preferred markers from the editor
	 * preferences.
	 * 
	 * It is recommended to have a least 6 preferred markers.
	 * 
	 * @param aSectionNode
	 *            Base node to modify (its children will be modified to)
	 */
	public static void normalizeSectionsMarker(final TreeData aSectionNode) {

		if (aSectionNode == null) {
			return;
		}

		// Get preferred markers
		IPreferenceStore preferenceStore = RestPlugin.getDefault()
				.getPreferenceStore();
		char[] preferredMarkersArray;

		// Try current preferences
		String preferredMarkers = preferenceStore
				.getString(IEditorPreferenceConstants.EDITOR_SECTION_MARKERS);

		if (preferredMarkers == null || preferredMarkers.isEmpty()) {
			// Else, try default preferences
			preferredMarkers = preferenceStore
					.getDefaultString(IEditorPreferenceConstants.EDITOR_SECTION_MARKERS);
		}

		if (preferredMarkers == null || preferredMarkers.isEmpty()) {
			// Else, use language definitions
			preferredMarkersArray = RestLanguage.SECTION_DECORATIONS;
		} else {

			preferredMarkersArray = preferredMarkers.toCharArray();
		}

		normalizeSectionsMarker(aSectionNode, preferredMarkersArray);
	}

	/**
	 * Rewrites section and subsections titles blocks to use the preferred
	 * marker for its level.
	 * 
	 * It is recommended to have a least 6 preferred markers.
	 * 
	 * @param aSectionNode
	 *            Base node to modify (its children will be modified to)
	 * @param aMarkers
	 *            Preferred markers array.
	 */
	public static void normalizeSectionsMarker(final TreeData aSectionNode,
			final char[] aMarkers) {

		int sectionLevel = aSectionNode.getLevel();

		// Ignore logical nodes (level <= 0)
		if (sectionLevel > 0) {

			// Rebase to 0
			sectionLevel--;

			// Don't use more than the given amount of markers
			if (sectionLevel >= aMarkers.length) {
				sectionLevel = aMarkers.length - 1;
			}

			replaceSectionMarker(aSectionNode, aMarkers[sectionLevel]);
		}

		// Treat children
		for (TreeData section : aSectionNode.getChildrenArray()) {
			normalizeSectionsMarker(section, aMarkers);
		}
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

	/**
	 * Replaces section title decoration lines
	 * 
	 * @param aSectionNode
	 *            The section to be modified
	 * @param aNewMarker
	 *            The new marker to use
	 */
	public static void replaceSectionMarker(final TreeData aSectionNode,
			final char aNewMarker) {

		final IDocument document = aSectionNode.getDocument();
		if (document == null) {
			return;
		}

		// Find old section block bounds
		IRegion sectionBlock = getSectionBlock(aSectionNode);
		if (sectionBlock == null) {
			return;
		}

		// Use document line delimiter
		final String endOfLine = TextUtilities
				.getDefaultLineDelimiter(document);

		final String sectionTitle = aSectionNode.getText();

		// Prepare the decoration line
		char[] decorationArray = new char[sectionTitle.length()];
		Arrays.fill(decorationArray, aNewMarker);

		StringBuilder newSectionBlock = new StringBuilder(3
				* sectionTitle.length() + 3 * endOfLine.length());

		// Add upperline, if needed
		if (aSectionNode.isUpperlined()) {
			newSectionBlock.append(decorationArray);
			newSectionBlock.append(endOfLine);
		}

		// Section title
		newSectionBlock.append(sectionTitle);
		newSectionBlock.append(endOfLine);

		// Underline
		newSectionBlock.append(decorationArray);
		newSectionBlock.append(endOfLine);

		// Replace section block in document
		try {
			document.replace(sectionBlock.getOffset(),
					sectionBlock.getLength(), newSectionBlock.toString());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
