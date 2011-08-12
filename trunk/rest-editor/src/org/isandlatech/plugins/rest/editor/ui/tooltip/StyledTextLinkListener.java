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

package org.isandlatech.plugins.rest.editor.ui.tooltip;

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.isandlatech.plugins.rest.editor.userassist.InternalHoverData;

/**
 * Handles links on a StyledText widget : notifies clicks and changes the cursor
 * on mouse hover.
 * 
 * @author Thomas Calmant
 */
public class StyledTextLinkListener implements Listener {

	/** Default cursor */
	private final Cursor pDefaultCursor;

	/** Hand cursor */
	private final Cursor pHandCursor;

	/** The associated information control */
	private final IInformationControl pInformationControl;

	/** Internal hover data */
	private final InternalHoverData pInternalHoverData;

	/**
	 * Prepares the styled text link handler
	 * 
	 * @param aInformationControl
	 *            Parent information control (to be disposed when a link is
	 *            clicked)
	 * 
	 * @param aInternalHoverData
	 *            Associated internal hover data (handles link notification)
	 */
	public StyledTextLinkListener(
			final IInformationControl aInformationControl,
			final InternalHoverData aInternalHoverData) {

		pInformationControl = aInformationControl;
		pInternalHoverData = aInternalHoverData;

		pDefaultCursor = new Cursor(Display.getDefault(), SWT.CURSOR_ARROW);
		pHandCursor = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.
	 * Event)
	 */
	@Override
	public void handleEvent(final Event aEvent) {

		if (!(aEvent.widget instanceof StyledText)) {
			// Unknown widget
			return;
		}

		final StyledText widget = (StyledText) aEvent.widget;

		if (aEvent.type == SWT.MouseExit) {
			// On mouse exit : always get back to the default cursor
			widget.setCursor(pDefaultCursor);
			return;
		}

		// Compute the clicked text offset
		final int clickOffset;

		try {
			clickOffset = widget.getOffsetAtLocation(new Point(aEvent.x,
					aEvent.y));

		} catch (IllegalArgumentException ex) {
			// No text at the given point, be quiet, reset cursor
			widget.setCursor(pDefaultCursor);
			return;
		}

		// Style at the given offset
		final StyleRange style = widget.getStyleRangeAtOffset(clickOffset);

		if (style != null && style.underlineStyle == SWT.UNDERLINE_LINK) {
			// The user clicked on a link

			switch (aEvent.type) {

			case SWT.MouseUp:
				// Notify the internal hover listener
				if (pInternalHoverData != null
						&& style.data instanceof CharSequence) {

					final boolean controlMustBeClosed = pInternalHoverData
							.notifyListener(style.data.toString());

					if (controlMustBeClosed && pInformationControl != null) {

						// If the notification returns true, the tool tip must
						// be closed
						pInformationControl.dispose();
					}
				}

				break;

			case SWT.MouseMove:
				// Mouse move on the link : set the hand cursor
				widget.setCursor(pHandCursor);
				break;
			}

		} else if (aEvent.type == SWT.MouseMove) {
			// Mouse moved out of a link : set the default cursor
			widget.setCursor(pDefaultCursor);
		}

		aEvent.doit = false;
	}

	/**
	 * Register the current listener to the given widget
	 * 
	 * @param aWidget
	 *            The widget to register to
	 */
	public void registerTo(final StyledText aWidget) {

		if (aWidget == null) {
			return;
		}

		aWidget.addListener(SWT.MouseUp, this);
		aWidget.addListener(SWT.MouseMove, this);
		aWidget.addListener(SWT.MouseExit, this);
	}
}
