/**
 * File:   MarkedCharacterScanner.java
 * Author: Thomas Calmant
 * Date:   2 mars 2011
 */
package org.isandlatech.plugins.rest.editor.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;

/**
 * @author Thomas Calmant
 * 
 */
public class MarkedCharacterScanner implements ICharacterScanner {

	/**
	 * Test if the given character can considered as an end of line character
	 * 
	 * @param codePoint
	 *            The character to be tested
	 * @return True if the given character is an EOL character
	 */
	public static boolean isAnEOL(final int codePoint) {
		return codePoint == '\n' || codePoint == '\r';
	}

	/** Character count */
	private long pReadCharacters;

	/** Real character scanner */
	private ICharacterScanner pRealScanner;

	/**
	 * Prepares the marked scanner
	 * 
	 * @param aRealScanner
	 *            The real character scanner
	 */
	public MarkedCharacterScanner(final ICharacterScanner aRealScanner) {
		pRealScanner = aRealScanner;
		pReadCharacters = 0;
	}

	@Override
	public int getColumn() {
		return pRealScanner.getColumn();
	}

	@Override
	public char[][] getLegalLineDelimiters() {
		return pRealScanner.getLegalLineDelimiters();
	}

	@Override
	public int read() {
		int readChar = pRealScanner.read();

		if (readChar != EOF) {
			pReadCharacters++;
		}

		return readChar;
	}

	/**
	 * Reset the character scanner to its original position
	 */
	public void reset() {

		if (pReadCharacters > 0) {
			// Some characters have been read
			while (pReadCharacters-- > 0) {
				pRealScanner.unread();
			}

		} else {
			// The scanner points before its original position

			while (pReadCharacters++ < 0) {
				pRealScanner.read();
			}
		}
	}

	/**
	 * Skips the current line
	 * 
	 * @return true if the line was empty, else false
	 */
	public boolean skipLine() {
		int readChar;
		boolean emptyLine = true;

		do {
			readChar = read();

			if (readChar != EOF && !Character.isWhitespace(readChar)) {
				emptyLine = false;
			}

		} while (!isAnEOL(readChar) && readChar != EOF);

		return emptyLine;
	}

	@Override
	public void unread() {
		pReadCharacters--;
		pRealScanner.unread();
	}

	/**
	 * Unread all characters until the current column is 0
	 */
	public void unreadLine() {
		while (getColumn() != 0) {
			unread();
		}
	}
}
