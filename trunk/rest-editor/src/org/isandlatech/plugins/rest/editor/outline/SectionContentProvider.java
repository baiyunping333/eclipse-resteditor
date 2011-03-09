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
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.isandlatech.plugins.rest.editor.rules.DecoratedLinesRule;
import org.isandlatech.plugins.rest.editor.scanners.RestPartitionScanner;
import org.isandlatech.plugins.rest.parser.RestLanguage;

/**
 * @author Thomas Calmant
 * 
 */
public class SectionContentProvider implements ITreePathContentProvider {

	/** Root path label */
	private final TreeData pDocumentRoot = new TreeData("Document");

	/** Decoration level list */
	private final List<Character> pDecoratorsLevels = new ArrayList<Character>(
			RestLanguage.SECTION_DECORATIONS.length);

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

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getChildren(final TreePath aParentPath) {

		if (aParentPath.getLastSegment() instanceof TreeData) {
			TreeData lastSegment = (TreeData) aParentPath.getLastSegment();
			return lastSegment.getChildrenArray();
		}

		return null;
	}

	/**
	 * Retrieves the level of the given decorator
	 * 
	 * @param aChar
	 *            The decorator to find
	 * @return The level of the decorator
	 */
	private int getDecorationLevel(final char aChar) {

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

	@Override
	public boolean hasChildren(final TreePath aPath) {

		if (aPath.getLastSegment() instanceof TreeData) {
			return ((TreeData) aPath.getLastSegment()).hasChildren();
		}

		return false;
	}

	@Override
	public void inputChanged(final Viewer aViewer, final Object aOldInput,
			final Object aNewInput) {

		IDocument document = pParentOutline.getDocumentProvider().getDocument(
				aNewInput);
		if (document == null) {
			return;
		}

		IDocumentPartitioner partitioner = document.getDocumentPartitioner();
		if (partitioner == null) {
			return;
		}

		List<String> sectionBlockContent = new ArrayList<String>(3);
		TreeData currentElement = pDocumentRoot;
		pDocumentRoot.clearChildren();
		pDecoratorsLevels.clear();

		try {
			for (int line = 0; line < document.getNumberOfLines(); line++) {
				int lineOffset = document.getLineOffset(line);
				String contentType = partitioner.getContentType(lineOffset);

				// Found a section line : get all contiguous section lines
				int sectionBeginLineNumber = line;

				sectionBlockContent.clear();
				while (contentType.equals(RestPartitionScanner.SECTION_BLOCK)
						&& line < document.getNumberOfLines()) {

					String lineContent = getLine(document, line);

					// Stop on first empty line
					if (lineContent.trim().isEmpty()) {
						break;
					}

					sectionBlockContent.add(lineContent);

					lineOffset = document.getLineOffset(line++);
					contentType = document.getDocumentPartitioner()
							.getContentType(lineOffset);
				}

				// We found something
				if (sectionBlockContent.size() != 0) {
					char decorationChar = 0;
					boolean underlined = false;
					int sectionLineNumber = sectionBeginLineNumber;
					String sectionTitle = null;

					for (String sectionBlockLine : sectionBlockContent) {

						if (sectionTitle == null) {
							sectionLineNumber++;

							if (!DecoratedLinesRule
									.isDecorativeLine(sectionBlockLine)) {
								sectionTitle = sectionBlockLine;
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

					// We found something interesting
					if (underlined) {
						sectionTitle = sectionTitle.trim();
						int decorationLevel = getDecorationLevel(decorationChar);

						// Valid decoration level found
						if (decorationLevel != -1) {
							TreeData root = null;

							if (decorationLevel > currentElement.getLevel()) {
								// Child
								root = currentElement;

							} else if (decorationChar == currentElement
									.getLevel()) {
								// Brother
								root = currentElement.getParent();

							} else {
								// Uncle

								while (decorationLevel <= currentElement
										.getLevel()) {
									currentElement = currentElement.getParent();
									if (currentElement == null) {
										break;
									}
								}

								root = currentElement;
							}

							if (root == null) {
								root = pDocumentRoot;
							}

							// Why "-1" ? I don't know :/
							currentElement = root
									.addChild(new TreeData(
											sectionTitle,
											sectionLineNumber,
											document.getLineOffset(sectionLineNumber - 1)));
						}
					}
				}
			}

			pParentOutline.getTreeViewer().refresh();

		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}
}
