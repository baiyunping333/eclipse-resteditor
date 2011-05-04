/**
 * File:   SingleWordDetector.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.parser;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Consider that a word is a token of letters, digits or hyphens
 * 
 * @author Thomas Calmant
 */
public class SingleWordDetector implements IWordDetector {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	@Override
	public boolean isWordPart(final char c) {
		return Character.isLetterOrDigit(c) || c == '-' || c == ':';
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	@Override
	public boolean isWordStart(final char c) {
		return Character.isLetterOrDigit(c) || c == '-';
	}
}
