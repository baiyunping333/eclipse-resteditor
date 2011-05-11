/**
 * File:   SectionContentProvider.java
 * Author: Thomas Calmant
 * Date:   27 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.outline;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
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
	private final List<Character> pDecoratorsLevels = new ArrayList<Character>(
			RestLanguage.SECTION_DECORATIONS.length);

	/** Root path label */
	private final TreeData pDocumentRoot = new TreeData(
			Messages.getString("document.root"));

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

		for (char decoration : RestLanguage.SECTION_DECORATIONS) {

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
	protected char getDecorationForLevel(int aLevel) {

		// Rebase level (root = 1 in data)
		aLevel = aLevel - 1;

		if (aLevel >= RestLanguage.SECTION_DECORATIONS.length) {
			return pDecoratorsLevels.get(pDecoratorsLevels.size() - 1);
		}

		while (aLevel >= pDecoratorsLevels.size()) {
			appendDecorator();
		}

		return pDecoratorsLevels.get(aLevel);
	}

	/**
	 * Retrieves the level of the given decorator
	 * 
	 * @param aChar
	 *            The decorator to find
	 * @return The level of the decorator
	 */
	protected int getDecorationLevel(final char aChar) {

		if (!DecoratedLinesRule.isDecorationCharacter(aChar)) {
			return -1;
		}

		Character aCharObject = Character.valueOf(aChar);

		if (!pDecoratorsLevels.contains(aCharObject)) {
			pDecoratorsLevels.add(aCharObject);
		}

		// "+ 1" : the title is on level 1, the level 0 corresponds to the
		// pDocumentRoot member (tree root)
		return pDecoratorsLevels.indexOf(aCharObject) + 1;
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

	/**
	 * Retrieves the given line content
	 * 
	 * @param aDocument
	 *            Document to be read
	 * @param aLine
	 *            Line number in the document
	 * @return The line content, null on error
	 */
	private String getLine(final IDocument aDocument, final int aLine) {
		try {
			int lineOffset = aDocument.getLineOffset(aLine);
			return aDocument.get(lineOffset, aDocument.getLineLength(aLine));
		} catch (BadLocationException e) {
			return null;
		}
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

		pDocumentRoot.setDocument(document);

		// Get the partitioner
		IDocumentPartitioner partitioner;
		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 doc3 = (IDocumentExtension3) document;
			partitioner = doc3
					.getDocumentPartitioner(RestPartitionScanner.PARTITIONNING);
		} else {
			partitioner = document.getDocumentPartitioner();
		}

		if (partitioner == null) {
			System.err.println("No partitioner to fill outline");
			return;
		}

		// Prepare variables
		List<String> sectionBlockContent = new ArrayList<String>(3);
		TreeData currentElement = pDocumentRoot;
		pDocumentRoot.clearChildren();
		pDecoratorsLevels.clear();

		// Loop while there are some sections
		try {
			for (int line = 0; line < document.getNumberOfLines(); line++) {
				int lineOffset = document.getLineOffset(line);
				String contentType = partitioner.getContentType(lineOffset);

				// Found a section line : get all contiguous section lines
				int sectionBeginLineNumber = line;

				sectionBlockContent.clear();
				while (RestPartitionScanner.SECTION_BLOCK.equals(contentType)
						&& line < document.getNumberOfLines()) {

					String lineContent = getLine(document, line);

					// Stop on first empty line
					if (lineContent.trim().isEmpty()) {
						break;
					}

					sectionBlockContent.add(lineContent);

					lineOffset = document.getLineOffset(line++);
					contentType = partitioner.getContentType(lineOffset);
				}

				// We found something
				if (sectionBlockContent.size() != 0) {
					currentElement = storeSection(document, currentElement,
							sectionBlockContent, sectionBeginLineNumber);
				}
			}

			pParentOutline.getTreeViewer().refresh();

		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Analyzes the section block and stores its title in the outline
	 * 
	 * @param aDocument
	 *            Document currently read
	 * @param aCurrentElement
	 *            Current element in the outline hierarchy
	 * @param aSectionBlockContent
	 *            Content of the section to add to the outline
	 * @param aSectionLineNumber
	 *            Line where the section begins
	 * @return The "new" current element, added to the outline
	 * 
	 * @throws BadLocationException
	 *             An error occurred while retrieving the section title location
	 */
	private TreeData storeSection(final IDocument aDocument,
			final TreeData aCurrentElement,
			final List<String> aSectionBlockContent, int aSectionLineNumber)
			throws BadLocationException {

		TreeData newElement = aCurrentElement;
		char decorationChar = 0;
		boolean upperlined = false;
		boolean underlined = false;
		String sectionTitle = null;

		for (String sectionBlockLine : aSectionBlockContent) {

			if (sectionTitle == null) {
				aSectionLineNumber++;

				if (!DecoratedLinesRule.isDecorativeLine(sectionBlockLine)) {
					sectionTitle = sectionBlockLine;
				} else {
					upperlined = true;
				}

			} else if (sectionTitle != null
					&& DecoratedLinesRule.isDecorativeLine(sectionBlockLine)) {
				// Under line found (section is OK)
				decorationChar = sectionBlockLine.charAt(0);
				underlined = true;
				break;
			}
		}

		// We found something interesting
		if (underlined) {
			sectionTitle = sectionTitle.trim();
			int decorationLevel = getDecorationLevel(decorationChar);

			// Valid decoration level found
			if (decorationLevel != -1) {
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
				int lineOffset = aDocument
						.getLineOffset(aSectionLineNumber - 1);

				// Store the document in node data
				TreeData newNodeData = new TreeData(sectionTitle,
						aSectionLineNumber, lineOffset, upperlined);
				newNodeData.setDocument(aDocument);

				newElement = root.addChild(newNodeData);
			}
		}

		return newElement;
	}
}
