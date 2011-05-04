/**
 * File:   TreeData.java
 * Author: Thomas Calmant
 * Date:   27 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.outline;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Calmant
 * 
 */
public class TreeData {

	/**
	 * Creates an array of TreeData objects from a list of labels
	 * 
	 * @param aParent
	 *            Parent of the elements (can be null for roots)
	 * @param aLabels
	 *            Labels of the given elements
	 * @return An array of TreeData objects. Never null.
	 */
	public static TreeData[] createElements(final TreeData aParent,
			final String... aLabels) {
		TreeData[] resultArray = new TreeData[aLabels.length];

		if (aLabels != null) {
			int i = 0;
			for (String label : aLabels) {
				resultArray[i++] = new TreeData(aParent, label);
			}
		}

		return resultArray;
	}

	/** Element label */
	private String pText;

	/** Element level */
	private int pLevel;

	/** Element line */
	private int pLine;

	/** Element line offset */
	private int pLineOffset;

	/** Element parent */
	private TreeData pParent;

	/** Element children */
	private List<TreeData> pChildren;

	/**
	 * Configures the tree element
	 * 
	 * @param aText
	 *            Element label
	 */
	public TreeData(final String aText) {
		this(null, aText);
	}

	/**
	 * Configures the tree element
	 * 
	 * @param aText
	 *            Element label
	 * @param aLine
	 *            Element extra data : line in the source document
	 * @param aLineOffset
	 *            Element extra data : line offset in the source document
	 */
	public TreeData(final String aText, final int aLine, final int aLineOffset) {
		this(null, aText, aLine, aLineOffset);
	}

	/**
	 * Configures the tree element
	 * 
	 * @param aParent
	 *            Element parent
	 * @param aText
	 *            Element label
	 */
	public TreeData(final TreeData aParent, final String aText) {
		this(aParent, aText, -1, 0);
	}

	/**
	 * Configures the tree element
	 * 
	 * @param aParent
	 *            Element parent
	 * @param aText
	 *            Element label
	 * @param aLine
	 *            Element extra data : line in the source document
	 * @param aLineOffset
	 *            Element extra data : line offset in the source document
	 */
	public TreeData(final TreeData aParent, final String aText,
			final int aLine, final int aLineOffset) {
		pParent = aParent;
		pChildren = new ArrayList<TreeData>();
		pText = String.valueOf(aText);

		if (aParent != null) {
			aParent.addChild(this);
		} else {
			pLevel = 0;
		}

		pLine = aLine;
		pLineOffset = aLineOffset;
	}

	/**
	 * Adds a child to the element
	 * 
	 * @param aChild
	 *            A new child
	 */
	public TreeData addChild(final TreeData aChild) {

		if (aChild == null) {
			return null;
		}

		pChildren.add(aChild);
		aChild.pParent = this;
		aChild.pLevel = pLevel + 1;

		return aChild;
	}

	/**
	 * Suppress all children, recursively
	 */
	public void clearChildren() {

		for (TreeData child : pChildren) {
			child.clearChildren();
			child.pParent = null;
		}

		pChildren.clear();
	}

	@Override
	public boolean equals(final Object aObj) {

		if (!(aObj instanceof TreeData)) {
			return false;
		}

		TreeData other = (TreeData) aObj;

		if (!pText.equals(other.pText)) {
			return false;
		}

		if (pParent == null) {
			return other.pParent == null;
		}

		// else
		if (!pParent.equals(other.pParent)) {
			return false;
		}

		return true;
	}

	/**
	 * Retrieves the element children in an array
	 * 
	 * @return An array containing all children
	 */
	public TreeData[] getChildrenArray() {
		TreeData[] childrenArray = new TreeData[pChildren.size()];
		pChildren.toArray(childrenArray);
		return childrenArray;
	}

	/**
	 * Retrieves the element level
	 * 
	 * @return The element level
	 */
	public int getLevel() {
		return pLevel;
	}

	/**
	 * Retrieves the line associated to the element
	 * 
	 * @return The line associated to the element
	 */
	public int getLine() {
		return pLine;
	}

	/**
	 * Retrieves the line offset associated to the element
	 * 
	 * @return The line offset associated to the element
	 */
	public int getLineOffset() {
		return pLineOffset;
	}

	/**
	 * Retrieves the element parent.
	 * 
	 * @return The element parent. Null if the element is a root
	 */
	public TreeData getParent() {
		return pParent;
	}

	/**
	 * Retrieves the element label
	 * 
	 * @return The element label
	 */
	public String getText() {
		return pText;
	}

	/**
	 * Tests if the current element has children
	 * 
	 * @return True if the element has children
	 */
	public boolean hasChildren() {
		return !pChildren.isEmpty();
	}

	/**
	 * Removes a child from the element
	 * 
	 * @param aChild
	 *            The child to be removed
	 */
	public void removeChild(final TreeData aChild) {

		if (aChild == null) {
			return;
		}

		pChildren.remove(aChild);
		aChild.pParent = null;
		aChild.pLevel = 0;
	}

	/**
	 * Sets the element parent.
	 * 
	 * @param parent
	 *            The element parent. Null to make a root
	 */
	public void setParent(final TreeData parent) {
		pParent = parent;
	}

	/** Sets the element label */
	public void setText(final String text) {
		pText = String.valueOf(text);
	}

	@Override
	public String toString() {
		if (pLine != -1) {
			return pText + " (" + pLine + ")";
		}

		return pText;
	}
}
