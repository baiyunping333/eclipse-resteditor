/**
 * File:   RestColorRules.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.prefs;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * TODO: read values from the preferences
 * 
 * @author Thomas Calmant
 */
public class RestColorRules {

	/** Default color : black */
	public static final RGB DEFAULT_COLOR = new RGB(0, 0, 0);

	/** Comments default color : gray */
	public static final RGB DEFAULT_COMMENTS_COLOR = new RGB(128, 128, 128);

	/** Default directive color : blue */
	public static final RGB DEFAULT_DIRECTIVES_COLOR = new RGB(0, 0, 255);

	/** Active comments color */
	private Color pCommentsColor;

	/** Active default color */
	private Color pDefaultColor;

	/** Active directives color */
	private Color pDirectivesColor;

	/**
	 * Sets the default color values
	 */
	public RestColorRules() {
		final Display currentDisplay = Display.getCurrent();

		pCommentsColor = new Color(currentDisplay, DEFAULT_COMMENTS_COLOR);
		pDefaultColor = new Color(currentDisplay, DEFAULT_COLOR);
		pDirectivesColor = new Color(currentDisplay, DEFAULT_DIRECTIVES_COLOR);
	}

	/**
	 * Retrieves the comments color
	 * @return The comments color, never null
	 */
	public Color getCommentsColor() {
		return pCommentsColor;
	}

	/**
	 * Retrieves the comments color
	 * @return The comments color, never null
	 */
	public Color getDefaultColor() {
		return pDefaultColor;
	}

	/**
	 * Retrieves the comments color
	 * @return The comments color, never null
	 */
	public Color getDirectivesColor() {
		return pDirectivesColor;
	}
}
