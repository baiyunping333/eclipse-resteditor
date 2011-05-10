/**
 * File:   InternalBrowserInformationControl.java
 * Author: Thomas Calmant
 * Date:   4 mai 2011
 */
package org.isandlatech.plugins.rest.editor.userassist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.isandlatech.plugins.rest.BrowserController;
import org.isandlatech.plugins.rest.RestPlugin;

/**
 * Creates a browser widget to display a tooltip
 * 
 * @author Thomas Calmant
 */
public class InternalBrowserInformationControl extends
		AbstractInformationControl implements IInformationControlExtension2,
		LocationListener {

	/** Browser ID for external links */
	public static final String BROWSER_ID = "rest-documentaton-browser";

	/** Browser content style */
	public static final String STYLE_SHEET_PATH = "/HoverStyle.css";

	/**
	 * Returns a CSS hexadecimal version of the given color
	 * 
	 * @param aColor
	 *            Color to be converted
	 * @return The CSS color value
	 */
	public static String colorToHex(final Color aColor) {

		return String.format("#%1$02x%2$02x%3$02x", aColor.getRed(),
				aColor.getGreen(), aColor.getBlue());
	}

	/**
	 * Creates a new instance of an anonymous information control creator that
	 * provides a new browser tooltip each time it is invoked.
	 * 
	 * @return A browser control creator
	 */
	public static IInformationControlCreator getCreator() {

		return new IInformationControlCreator() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.text.IInformationControlCreator#
			 * createInformationControl(org.eclipse.swt.widgets.Shell)
			 */
			@Override
			public IInformationControl createInformationControl(
					final Shell aParent) {

				try {
					return new InternalBrowserInformationControl(aParent, this);

				} catch (SWTError error) {
					// Browser support error ?
					return new DefaultInformationControl(aParent, true);
				}
			}
		};

	}

	/** The web browser widget */
	private Browser pBrowser;

	/** Indicates if setInformation() or setInput() has been called */
	private boolean hasData;

	/** The creator that instantiated this object */
	private final IInformationControlCreator pControlCreator;

	/** Data associated to this tooltip */
	private InternalBrowserData pData;

	/**
	 * Prepares the information control
	 * 
	 * @param aParentShell
	 *            Parent shell
	 * @param aInformationControlCreator
	 *            The creator that instantiated this object
	 */
	public InternalBrowserInformationControl(final Shell aParentShell,
			final IInformationControlCreator aInformationControlCreator) {

		super(aParentShell, EditorsUI.getTooltipAffordanceString());
		pControlCreator = aInformationControlCreator;

		create();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.browser.LocationListener#changed(org.eclipse.swt.browser
	 * .LocationEvent)
	 */
	@Override
	public void changed(final LocationEvent aEvent) {
		// Do nothing...
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.browser.LocationListener#changing(org.eclipse.swt.browser
	 * .LocationEvent)
	 */
	@Override
	public void changing(final LocationEvent aEvent) {

		if (aEvent.location.equals("about:blank")) {
			// called when setting text manually

			aEvent.doit = true;
			return;
		}

		// The hover browser just shows internal HTML
		aEvent.doit = false;

		if (aEvent.location.startsWith("about:")) {
			// Internal pages are forbidden
			return;
		}

		// Standard location
		if (!aEvent.location.startsWith(IAssistanceConstants.INTERNAL_PREFIX)) {

			BrowserController.getController().openUrl(BROWSER_ID,
					aEvent.location);

			dispose();
			return;
		}

		// Propagate the event
		if (pData != null) {

			String rawLocation = aEvent.location
					.substring(IAssistanceConstants.INTERNAL_PREFIX.length());

			String location;
			try {
				// TODO: get the doc/platform charset
				location = URLDecoder.decode(rawLocation, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				location = rawLocation;
			}

			if (pData.notifyListener(location)) {
				// Close the tooltip on success
				dispose();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.AbstractInformationControl#createContent(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected void createContent(final Composite aParent) {

		// Fall back on default browser
		pBrowser = new Browser(aParent, SWT.NONE);

		// Disable links (for safety)
		pBrowser.setJavascriptEnabled(false);

		// Handle browser links (internal & external ones)
		pBrowser.addLocationListener(this);

		// Remove the contextual menu
		pBrowser.setMenu(new Menu(getShell(), SWT.NONE));

		// Currently no data available
		hasData = false;
	}

	/**
	 * Prepares the HTML content head, with CSS styles.
	 * 
	 * @return The HTML content, after the body begin tag
	 */
	protected StringBuilder createHtmlHead() {

		// Retrieve colors to be used for display
		Display display = Display.getCurrent();
		Color foreground = display.getSystemColor(SWT.COLOR_INFO_FOREGROUND);
		Color background = display.getSystemColor(SWT.COLOR_INFO_BACKGROUND);

		// Head
		StringBuilder htmlContent = new StringBuilder();
		htmlContent.append("<html>\n<head>\n<style type=\"text/css\">\n");

		// Common styles (from JDT)
		try {
			htmlContent.append(RestPlugin.getDefault().getBundleFileContent(
					STYLE_SHEET_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Colors styles from display
		htmlContent.append("html { color: ");
		htmlContent.append(colorToHex(foreground));
		htmlContent.append("; background-color: ");
		htmlContent.append(colorToHex(background));
		htmlContent.append(" }\n");

		htmlContent.append("</style>\n</head>\n<body>\n");
		return htmlContent;
	}

	/**
	 * Adds the final body and html end tags
	 * 
	 * @param aHtmlContent
	 *            The current HTML content
	 */
	protected void finishHtmlContent(final StringBuilder aHtmlContent) {

		if (aHtmlContent == null) {
			return;
		}

		aHtmlContent.append("\n</body>\n</html>\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.AbstractInformationControl#
	 * getInformationPresenterControlCreator()
	 */
	@Override
	public IInformationControlCreator getInformationPresenterControlCreator() {
		return pControlCreator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.AbstractInformationControl#handleDispose()
	 */
	@Override
	protected void handleDispose() {

		// Release the browser
		if (pBrowser != null) {
			pBrowser.dispose();
			pBrowser = null;
		}

		super.handleDispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControlExtension#hasContents()
	 */
	@Override
	public boolean hasContents() {
		return hasData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.AbstractInformationControl#setInformation(java
	 * .lang.String)
	 */
	@Override
	public void setInformation(final String aInformation) {

		// Prepare HTML content
		StringBuilder htmlContent = createHtmlHead();
		htmlContent.append(aInformation);
		finishHtmlContent(htmlContent);

		pBrowser.setText(htmlContent.toString());
		hasData = true;

		// Data changed
		pData = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.IInformationControlExtension2#setInput(java.lang
	 * .Object)
	 */
	@Override
	public void setInput(final Object aInput) {

		setInformation(String.valueOf(aInput));

		if (aInput instanceof InternalBrowserData) {
			pData = (InternalBrowserData) aInput;
		} else {
			pData = null;
		}
	}

	/*
	 * @see org.eclipse.jface.text.AbstractInformationControl#setSize(int, int)
	 */
	@Override
	public void setSize(final int width, final int height) {

		pBrowser.setRedraw(false);

		try {
			super.setSize(width, height);
		} finally {
			pBrowser.setRedraw(true);
		}
	}
}
