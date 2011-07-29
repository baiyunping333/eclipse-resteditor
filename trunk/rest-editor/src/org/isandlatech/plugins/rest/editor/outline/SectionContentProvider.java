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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.isandlatech.plugins.rest.RestPlugin;
import org.isandlatech.plugins.rest.editor.rules.DecoratedLinesRule;
import org.isandlatech.plugins.rest.editor.scanners.RestPartitionScanner;
import org.isandlatech.plugins.rest.i18n.Messages;
import org.isandlatech.plugins.rest.parser.RestLanguage;

/**
 * Fills the outline with section titles
 * 
 * @author Thomas Calmant
 */
public class SectionContentProvider implements ITreePathContentProvider {

	/** Decoration level list */
	private final List<SectionDecoration> pDecoratorsLevels = new ArrayList<SectionDecoration>(
			RestLanguage.SECTION_DECORATIONS.length);

	/** Root path label */
	private final TreeData pDocumentRoot = new TreeData(
			Messages.getString("outline.document.root"));

	/** Parent outline page */
	private final RestContentOutlinePage pParentOutline;

	/**
	 * Configures the content provider
	 * 
	 * @param aParent
	 *            Parent outline page
	 */
	public SectionContentProvider(final RestContentOutlinePage aParent) {
		pParentOutline = aParent;
	}

	/**
	 * Appends the first available section decoration character to the current
	 * levels decoration list
	 */
	private void appendDecorator() {

		for (char marker : RestLanguage.SECTION_DECORATIONS) {

			SectionDecoration decoration = new SectionDecoration(marker, false);

			if (!pDecoratorsLevels.contains(decoration)) {
				pDecoratorsLevels.add(decoration);
				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		pDecoratorsLevels.clear();
		pDocumentRoot.clearChildren();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreePathContentProvider#getChildren(org.eclipse
	 * .jface.viewers.TreePath)
	 */
	@Override
	public Object[] getChildren(final TreePath aParentPath) {

		if (aParentPath.getLastSegment() instanceof TreeData) {
			TreeData lastSegment = (TreeData) aParentPath.getLastSegment();
			return lastSegment.getChildrenArray();
		}

		return null;
	}

	/**
	 * Retrieves the decoration associated to the given level
	 * 
	 * @param aLevel
	 *            Section level
	 * @return The section decoration
	 */
	protected SectionDecoration getDecorationForLevel(final int aLevel) {

		// Rebase level (root = 1 in data)
		int level = aLevel - 1;

		if (level >= RestLanguage.SECTION_DECORATIONS.length) {
			return pDecoratorsLevels.get(pDecoratorsLevels.size() - 1);
		}

		while (level >= pDecoratorsLevels.size()) {
			appendDecorator();
		}

		return pDecoratorsLevels.get(level);
	}

	/**
	 * Retrieves the level of the given decorator
	 * 
	 * @param aChar
	 *            The decorator to find
	 * @param The
	 *            decorated line also have an upper-line
	 * @return The level of the decorator
	 */
	protected int getDecorationLevel(final char aChar, final boolean aUpperlined) {

		if (!DecoratedLinesRule.isDecorationCharacter(aChar)) {
			return -1;
		}

		SectionDecoration decoration = new SectionDecoration(aChar, aUpperlined);

		if (!pDecoratorsLevels.contains(decoration)) {
			pDecoratorsLevels.add(decoration);
		}

		// "+ 1" : the title is on level 1, the level 0 corresponds to the
		// pDocumentRoot member (tree root)
		return pDecoratorsLevels.indexOf(decoration) + 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreePathContentProvider#getElements(java.lang
	 * .Object)
	 */
	@Override
	public Object[] getElements(final Object aInputElement) {
		// Called by TreeViewer.setInput. Returns the root element(s).
		return new Object[] { pDocumentRoot };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreePathContentProvider#getParents(java.lang
	 * .Object)
	 */
	@Override
	public TreePath[] getParents(final Object aElement) {

		if (aElement instanceof TreeData) {

			Stack<TreeData> elementGenealogy = new Stack<TreeData>();

			TreeData parent = (TreeData) aElement;
			while ((parent = parent.getParent()) != null) {
				elementGenealogy.push(parent);
			}

			TreeData[] parentsArray = new TreeData[elementGenealogy.size()];
			return new TreePath[] { new TreePath(
					elementGenealogy.toArray(parentsArray)) };
		}

		return null;
	}

	/**
	 * Retrieves the document root
	 * 
	 * @return The document root
	 */
	public TreeData getRoot() {
		return pDocumentRoot;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreePathContentProvider#hasChildren(org.eclipse
	 * .jface.viewers.TreePath)
	 */
	@Override
	public boolean hasChildren(final TreePath aPath) {

		if (aPath.getLastSegment() instanceof TreeData) {
			return ((TreeData) aPath.getLastSegment()).hasChildren();
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(final Viewer aViewer, final Object aOldInput,
			final Object aNewInput) {

		// Get the doc
		IDocument document = pParentOutline.getDocumentProvider().getDocument(
				aNewInput);
		if (document == null) {
			// We come here when exiting Eclipse
			return;
		}

		// Prepare variables
		ITypedRegion[] allPartitions = null;
		TreeData currentElement = pDocumentRoot;
		pDocumentRoot.setDocument(document);
		pDocumentRoot.clearChildren();
		TreeData.resetIdIndex();

		pDecoratorsLevels.clear();

		// Retrieve all partitions
		if (document instanceof IDocumentExtension3) {
			try {
				allPartitions = ((IDocumentExtension3) document)
						.computePartitioning(RestPartitionScanner.PARTITIONING,
								0, document.getLength(), false);

			} catch (BadLocationException e) {
				RestPlugin.logError("Error computing block partitions", e);
				return;
			} catch (BadPartitioningException e) {
				RestPlugin.logError("Invalid partition", e);
				return;
			}
		}

		for (ITypedRegion partition : allPartitions) {

			// Only treat section blocks
			if (partition.getType().equals(RestPartitionScanner.SECTION_BLOCK)) {

				try {
					int partitionOffset = partition.getOffset();
					int sectionBeginLineNumber = document
							.getLineOfOffset(partitionOffset);

					String content = document.get(partitionOffset,
							partition.getLength());

					currentElement = storeSection(document, currentElement,
							content, sectionBeginLineNumber);

				} catch (BadLocationException e) {
					// Should never happen...
					RestPlugin.logError("Error reading section", e);
				}
			}
		}

		pParentOutline.refreshTreeViewer();
	}

	/**
	 * Analyzes the section block and stores its title in the outline
	 * 
	 * @param aDocument
	 *            Document currently read
	 * @param aCurrentElement
	 *            Current element in the outline hierarchy
	 * @param aContent
	 *            Content of the section to add to the outline
	 * @param aSectionLineNumber
	 *            Line where the section begins
	 * @return The "new" current element, added to the outline
	 * 
	 * @throws BadLocationException
	 *             An error occurred while retrieving the section title location
	 */
	private TreeData storeSection(final IDocument aDocument,
			final TreeData aCurrentElement, final String aContent,
			final int aSectionLineNumber) throws BadLocationException {

		TreeData newElement = aCurrentElement;
		char decorationChar = 0;
		boolean upperlined = false;
		boolean underlined = false;
		String sectionTitle = null;
		int sectionLineNumber = aSectionLineNumber;

		BufferedReader strReader = new BufferedReader(
				new StringReader(aContent));

		String sectionBlockLine = null;
		try {
			while ((sectionBlockLine = strReader.readLine()) != null) {

				if (sectionTitle == null) {
					sectionLineNumber++;

					if (!DecoratedLinesRule.isDecorativeLine(sectionBlockLine)) {
						sectionTitle = sectionBlockLine;
					} else {
						upperlined = true;
					}

				} else if (sectionTitle != null
						&& DecoratedLinesRule
								.isDecorativeLine(sectionBlockLine)) {
					// Under line found (section is OK)
					decorationChar = sectionBlockLine.charAt(0);
					underlined = true;
					break;
				}
			}
		} catch (IOException e) {
			// May never happen...
			RestPlugin.logError("Error section block lines", e);
			return newElement;
		}

		// We found nothing about a section block there...
		if (!underlined) {
			return newElement;
		}

		// We found something interesting
		sectionTitle = sectionTitle.trim();
		int decorationLevel = getDecorationLevel(decorationChar, upperlined);

		// Not a valid decoration marker...
		if (decorationLevel < 0) {
			return newElement;
		}

		TreeData root = null;

		if (decorationLevel > aCurrentElement.getLevel()) {
			// Child
			root = aCurrentElement;

		} else if (decorationChar == aCurrentElement.getLevel()) {
			// Brother
			root = aCurrentElement.getParent();

		} else {

			// Uncle
			while (decorationLevel <= newElement.getLevel()) {
				newElement = newElement.getParent();
				if (newElement == null) {
					break;
				}
			}

			root = newElement;
		}

		if (root == null) {
			root = pDocumentRoot;
		}

		// Why "-1" ? getLine() begins at 1, getLineOffset() at 0.
		int lineOffset = aDocument.getLineOffset(sectionLineNumber - 1);

		// Store the document in node data
		TreeData newNodeData = new TreeData(sectionTitle, sectionLineNumber,
				lineOffset, upperlined);
		newNodeData.setDocument(aDocument);

		return root.addChild(newNodeData);
	}
}
