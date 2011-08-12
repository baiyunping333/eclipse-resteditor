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

package org.isandlatech.plugins.rest.editor.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jface.text.DefaultInformationControl.IInformationPresenter;
import org.eclipse.jface.text.DefaultInformationControl.IInformationPresenterExtension;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.widgets.Display;
import org.isandlatech.plugins.rest.RestPlugin;
import org.isandlatech.plugins.rest.editor.userassist.InternalHoverData;

/**
 * Tool tip presenter. Converts pseudo HTML text into a styled text
 * presentation.
 * 
 * @author Thomas Calmant
 */
public class RestInformationPresenter implements IInformationPresenter,
		IInformationPresenterExtension {

	/** Marker of attribute assignment */
	protected final static char ATTRIBUTE_ASSIGNMENT_MARKER = '=';

	/** Marker of a long attribute value (with spaces) */
	protected final static char LONG_ATTRIBUTE_VALUE_MARKER = '"';

	/** Beginning of an HTML tag */
	protected final static char TAG_BEGIN = '<';

	/** End of an HTML tag */
	protected final static char TAG_END = '>';

	/** Marker of the end of an HTML tag zone */
	protected final static char TAG_END_MARKER = '/';

	/** The presented information control */
	private IInformationControl pInformationControl;

	/** The associated hover data */
	private final InternalHoverData pInternalHoverData;

	/**
	 * Sets up the presenter
	 * 
	 * @param aHoverData
	 *            The internal hover data
	 */
	public RestInformationPresenter(final InternalHoverData aHoverData) {
		pInternalHoverData = aHoverData;
	}

	/**
	 * Extract attributes (format: a=b c="d e")
	 * 
	 * @param aTagContent
	 *            Content of the tag opening area (after the tag name)
	 * @return The tag attributes as a map
	 */
	protected Map<String, String> extractTagAttributes(final String aTagContent) {

		// Result map
		final Map<String, String> attributes = new HashMap<String, String>();

		// Attribute value offsets
		int valueStartIndex;
		int valueEndIndex;

		int valuationIndex = aTagContent.indexOf(ATTRIBUTE_ASSIGNMENT_MARKER);
		while (valuationIndex != -1) {

			// Find the attribute name start
			int attributeNameStart = aTagContent.lastIndexOf(' ',
					valuationIndex);
			if (attributeNameStart == -1) {
				// Use tag begin if needed
				attributeNameStart = 0;

			} else {
				// Avoid the found space
				attributeNameStart++;
			}

			if (valuationIndex + 1 >= aTagContent.length()) {
				// If the attribute can't have a value, stop there
				break;
			}

			// Extract the attribute name
			final String attributeName = aTagContent.substring(
					attributeNameStart, valuationIndex).trim();

			// Find attribute value
			if (aTagContent.charAt(valuationIndex + 1) == LONG_ATTRIBUTE_VALUE_MARKER) {
				// Attribute between quotes
				valueStartIndex = valuationIndex + 2;

				if (valueStartIndex >= aTagContent.length()) {
					// If the attribute can't have a value, stop there
					break;
				}

				valueEndIndex = aTagContent.indexOf(
						LONG_ATTRIBUTE_VALUE_MARKER, valueStartIndex);

			} else {
				// Simple attribute (space separation)
				valueStartIndex = valuationIndex + 1;
				valueEndIndex = aTagContent.indexOf(' ', valueStartIndex);
			}

			// Extract attribute value
			final String attributeValue = aTagContent.substring(
					valueStartIndex, valueEndIndex);

			// Put it to the attribute map
			attributes.put(attributeName, attributeValue);

			// Next loop
			valuationIndex = aTagContent.indexOf(ATTRIBUTE_ASSIGNMENT_MARKER,
					valuationIndex + 1);
		}

		return attributes;
	}

	/**
	 * Treat single HTML tags (<br />
	 * , ...)
	 * 
	 * @param aBuilder
	 *            Result text builder (without HTML tags)
	 * @param aTag
	 *            Read tag name
	 * @return A style range to apply (null if none)
	 */
	protected StyleRange handleSingleTag(final StringBuilder aBuilder,
			final String aTag) {

		final String tagName = aTag.toLowerCase();

		if (tagName.equals("br")) {
			// <br /> are new lines
			aBuilder.append("\n");
		}

		// No style to apply
		return null;
	}

	/**
	 * Handle a HTML tag
	 * 
	 * @param aBuilder
	 *            Result text builder
	 * @param aTag
	 *            Read tag information
	 * @return
	 */
	protected StyleRange handleTag(final StringBuilder aBuilder,
			final HtmlTag aTag) {

		final String tagName = aTag.getTagName();

		// Default style attribute
		StyleRange style = new StyleRange();
		style.fontStyle = SWT.BOLD;
		style.start = aTag.getStartOffset();
		style.length = aBuilder.length() - aTag.getStartOffset();

		if (tagName.equals("a")) {
			// Link
			style.data = aTag.getAttributes().get("href");
			style.underline = true;
			style.underlineStyle = SWT.UNDERLINE_LINK;

			return style;

		} else if (tagName.equals("pre")) {
			// Pre-formatted text

			aBuilder.insert(aTag.getStartOffset(), '\n');
			aBuilder.append('\n');

			style.start++;
			style.fontStyle = SWT.ITALIC;
			return style;

		} else if (tagName.equals("h1")) {
			// HTML Title

			// Add an end of line (after title)
			aBuilder.append("\n");

			// Set up the style range
			style.fontStyle = SWT.BOLD;
			return style;

		} else if (tagName.equals("b")) {
			// Set up the style range
			style.fontStyle = SWT.BOLD;
			return style;

		} else if (tagName.equals("i")) {
			// Set up the style range
			style.fontStyle = SWT.ITALIC;
			return style;

		} else if (tagName.equals("u")) {
			// Set up the style range
			style.fontStyle = SWT.UNDERLINE_SINGLE;
			return style;

		}

		return null;
	}

	/**
	 * Converts the given HTML pseudo-code to style range
	 * 
	 * @param aHtml
	 *            A HTML pseudo-code
	 * @param aPresentation
	 *            The styled text presentation (to be updated)
	 * @return The styled text content (without HTML tags)
	 */
	protected String html2TextPresentation(final String aHtml,
			final TextPresentation aPresentation) {

		// Result string builder
		final StringBuilder builder = new StringBuilder(aHtml.length());

		// Styles list
		final List<StyleRange> stylesList = new ArrayList<StyleRange>();

		// Stack of HTML tags
		final Stack<HtmlTag> tagStack = new Stack<HtmlTag>();

		// Parsing area
		int tagStart = aHtml.indexOf(TAG_BEGIN);
		int tagEnd = 0;
		int oldTagEnd = 0;

		if (tagStart != -1) {
			// First tag begins after some text...
			builder.append(aHtml.substring(0, tagStart));
		}

		while (tagStart != -1) {

			// Search the end of the current tag
			tagEnd = aHtml.indexOf(TAG_END, tagStart);

			// End of tag not found, append the "tag" content
			if (tagEnd == -1) {
				tagEnd = tagStart;
				break;
			}

			// Extract the tag
			String tag = aHtml.substring(tagStart + 1, tagEnd).trim();

			if (tag.charAt(tag.length() - 1) == TAG_END_MARKER) {
				// Test single tags (<br />, ...)
				tag = tag.substring(0, tag.length() - 1).trim();

				// Apply its handler
				StyleRange style = handleSingleTag(builder, tag);
				if (style != null) {
					stylesList.add(style);
				}

			} else if (tag.charAt(0) == TAG_END_MARKER) {
				// End of a tag area
				tag = tag.substring(1).trim();

				final HtmlTag poppedTag = tagStack.pop();
				if (!tag.equalsIgnoreCase(poppedTag.getTagName())) {

					RestPlugin
							.logWarning("Error parsing an HTML styled text - "
									+ tag + " found instead of "
									+ poppedTag.getTagName() + " at "
									+ tagStart);
					// Stop on first error
					return null;
				}

				// Append text between the current text index and the current
				// one
				builder.append(aHtml.substring(oldTagEnd, tagStart));

				StyleRange style = handleTag(builder, poppedTag);
				if (style != null) {
					stylesList.add(style);
				}

			} else {
				// Beginning of a tag area
				HtmlTag foundTag;

				// Extract tag name
				int tagNameEnd = tag.indexOf(' ');

				if (tagNameEnd == -1) {
					// Simple tag
					foundTag = new HtmlTag(tag, builder.length());

				} else {
					// Tag with attributes (and without a space after the '<'
					foundTag = new HtmlTag(tag.substring(0, tagNameEnd),
							builder.length());

					// Read attributes in a map
					foundTag.getAttributes().putAll(
							extractTagAttributes(tag.substring(tagNameEnd + 1)
									.trim()));
				}

				tagStack.push(foundTag);
			}

			// Continue to parse...
			tagStart = aHtml.indexOf(TAG_BEGIN, tagEnd);
			oldTagEnd = tagEnd + 1;
		}

		// Append the end of the string
		builder.append(aHtml.substring(oldTagEnd));

		// Apply computed styles
		for (StyleRange style : stylesList) {
			aPresentation.addStyleRange(style);
		}

		return builder.toString();
	}

	/**
	 * Sets the presented information control. Allows the link listener to
	 * dispose it.
	 * 
	 * @param aInformationControl
	 *            The presented information control
	 */
	public void setInformationControl(
			final IInformationControl aInformationControl) {

		pInformationControl = aInformationControl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.DefaultInformationControl.IInformationPresenter
	 * #updatePresentation(org.eclipse.swt.widgets.Display, java.lang.String,
	 * org.eclipse.jface.text.TextPresentation, int, int)
	 */
	@Override
	public String updatePresentation(final Display display,
			final String hoverInfo, final TextPresentation presentation,
			final int maxWidth, final int maxHeight) {

		return updatePresentation((Drawable) display, hoverInfo, presentation,
				maxWidth, maxHeight);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.DefaultInformationControl.
	 * IInformationPresenterExtension
	 * #updatePresentation(org.eclipse.swt.graphics.Drawable, java.lang.String,
	 * org.eclipse.jface.text.TextPresentation, int, int)
	 */
	@Override
	public String updatePresentation(final Drawable aDrawable,
			final String aHoverInfo, final TextPresentation aPresentation,
			final int aMaxWidth, final int aMaxHeight) {

		if (aDrawable instanceof StyledText) {

			// Set up the link listener
			final StyledTextLinkListener listener = new StyledTextLinkListener(
					pInformationControl, pInternalHoverData);
			listener.registerTo((StyledText) aDrawable);

			// Convert pseudo-HTML to TextPresentation styles
			return html2TextPresentation(aHoverInfo, aPresentation);
		}

		return null;
	}
}
