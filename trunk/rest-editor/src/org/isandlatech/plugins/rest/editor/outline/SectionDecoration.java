/**
 * 
 */
package org.isandlatech.plugins.rest.editor.outline;

/**
 * @author Thomas Calmant
 */
public class SectionDecoration {

	/** Character used for decoration */
	private final char pMarker;

	/** An upper line was used for the decoration */
	private final boolean pUpperlined;

	public SectionDecoration(final char aMarker, final boolean aUpperlined) {
		pMarker = aMarker;
		pUpperlined = aUpperlined;
	}

	@Override
	public boolean equals(final Object obj) {

		// Test with a simple character
		if (obj instanceof Character) {
			return ((Character) obj).equals(pMarker);
		}

		// Strict equality
		if (obj instanceof SectionDecoration) {

			SectionDecoration other = (SectionDecoration) obj;
			return pMarker == other.pMarker && pUpperlined == other.pUpperlined;
		}

		return false;
	}

	/**
	 * Retrieves the decoration marker
	 * 
	 * @return the decoration marker
	 */
	public char getMarker() {
		return pMarker;
	}

	/**
	 * Tests if the section has an upper line
	 * 
	 * @return True if the section has an upper line
	 */
	public boolean isUpperlined() {
		return pUpperlined;
	}
}
