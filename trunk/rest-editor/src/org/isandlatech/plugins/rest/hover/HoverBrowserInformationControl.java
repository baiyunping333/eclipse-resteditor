/**
 * File:   HoverBrowserInformationControl.java
 * Author: Thomas Calmant
 * Date:   4 mai 2011
 */
package org.isandlatech.plugins.rest.hover;

import java.io.IOException;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.isandlatech.plugins.rest.RestPlugin;

/**
 * @author Thomas Calmant
 * 
 */
public class HoverBrowserInformationControl extends AbstractInformationControl
		implements IInformationControlExtension2 {

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

				return new HoverBrowserInformationControl(aParent, this);
			}
		};

	}

	/** The web browser widget */
	private Browser pBrowser;

	/** The creator that instanciated this object */
	private IInformationControlCreator pControlCreator;

	/**
	 * Prepares the information control
	 * 
	 * @param aParentShell
	 *            Parent shell
	 * @param aInformationControlCreator
	 *            The creator that instanciated this object
	 */
	public HoverBrowserInformationControl(final Shell aParentShell,
			final IInformationControlCreator aInformationControlCreator) {

		super(aParentShell, new ToolBarManager(SWT.FLAT));
		pControlCreator = aInformationControlCreator;

		create();
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

		pBrowser = new Browser(aParent, SWT.NONE);
		pBrowser.setJavascriptEnabled(false);
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

		// Colors styles
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
	 * @see org.eclipse.jface.text.IInformationControlExtension#hasContents()
	 */
	@Override
	public boolean hasContents() {
		return !pBrowser.getText().isEmpty();
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

		StringBuilder htmlContent = createHtmlHead();
		htmlContent.append(aInformation);
		finishHtmlContent(htmlContent);

		pBrowser.setText(htmlContent.toString());
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
	}
}
