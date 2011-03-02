/**
 * File:   ColorProvider.java
 * Author: Thomas Calmant
 * Date:   2 mars 2011
 */
package org.isandlatech.plugins.rest.editor.providers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Color objects provider (to avoid useless memory consumption)
 * 
 * @author Thomas Calmant
 */
public class ColorProvider {

	/** RGB -> Color mapping */
	private Map<RGB, Color> pColorMap = new HashMap<RGB, Color>();

	/**
	 * Object clean up method
	 */
	public void dispose() {
		for (Color color : pColorMap.values()) {
			color.dispose();
		}

		pColorMap.clear();
	}

	/**
	 * Retrieves the color associated to the given RGB values.
	 * 
	 * @param aColor
	 *            RGB values representing the color
	 * @return The color
	 */
	public Color getColor(final RGB aColor) {

		if (aColor == null) {
			return null;
		}

		if (pColorMap.containsKey(aColor)) {
			return pColorMap.get(aColor);
		}

		Color color = new Color(Display.getCurrent(), aColor);
		pColorMap.put(aColor, color);
		return color;
	}
}
