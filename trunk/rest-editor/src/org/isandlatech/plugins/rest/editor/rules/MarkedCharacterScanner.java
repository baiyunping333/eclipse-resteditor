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

package org.isandlatech.plugins.rest.editor.rules;

import java.lang.reflect.Field;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.isandlatech.plugins.rest.RestPlugin;

/**
 * Eclipse character scanner extension, allowing to return to its original
 * position
 * 
 * @author Thomas Calmant
 */
public class MarkedCharacterScanner implements ICharacterScanner {

	/** {@link RuleBasedScanner}.fDocument field */
	private static Field sDocumentField;

	/** {@link RuleBasedScanner}.fOffset field */
	private static Field sOffsetField;

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

	/**
	 * Test if the given two character sequence is the end of line.
	 * 
	 * @param aCodePoint
	 *            The first character to be tested
	 * @param aCodePoint2
	 *            The second character to be tested
	 * @return True if the given two characters are the EOL character sequence
	 */
	public static boolean isTwoCharEOL(final int aCodePoint,
			final int aCodePoint2) {
		return (aCodePoint == '\r' && aCodePoint2 == '\n')
				|| (aCodePoint == '\n' && aCodePoint2 == '\r');
	}

	/** The scanner start offset */
	private int pBaseOffset;

	/** The scanned document */
	private IDocument pDocument;

	/** Character count */
	private int pReadCharacters;

	/** Real character scanner */
	private final ICharacterScanner pRealScanner;

	/**
	 * Prepares the marked scanner
	 * 
	 * @param aRealScanner
	 *            The real character scanner
	 */
	public MarkedCharacterScanner(final ICharacterScanner aRealScanner) {
		pRealScanner = aRealScanner;
		pReadCharacters = 0;

		readScannerInfo();
	}

	/**
	 * Retrieves the scanner base offset, -1 on error
	 * 
	 * @return
	 */
	public int getBaseOffset() {
		return pBaseOffset;
	}

	@Override
	public int getColumn() {

		if (pBaseOffset != -1 && pDocument != null
				&& pBaseOffset + pReadCharacters >= pDocument.getLength()) {
			return -1;
		}

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
	 * Reads the real scanner fields values and stores them
	 */
	private void readScannerInfo() {

		if (!(pRealScanner instanceof RuleBasedScanner)) {
			pBaseOffset = -1;
			return;
		}

		// Get the document field
		if (sDocumentField == null) {
			sDocumentField = retrieveHiddenField(RuleBasedScanner.class,
					"fDocument");
		}

		// Get the offset field
		if (sOffsetField == null) {
			sOffsetField = retrieveHiddenField(RuleBasedScanner.class,
					"fOffset");
		}

		// Store their value
		try {
			pDocument = (IDocument) sDocumentField.get(pRealScanner);

		} catch (Exception ex) {
			RestPlugin.logError("Error reading scanner private fields", ex);
			pDocument = null;
		}

		try {
			pBaseOffset = (Integer) sOffsetField.get(pRealScanner);

		} catch (Exception ex) {
			RestPlugin.logError("Error reading scanner private fields", ex);
			pBaseOffset = -1;
		}
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
	 * Retrieves the given class field by reflection and sets it accessible.
	 * Returns null on error.
	 * 
	 * @param aClass
	 *            Class containing the field
	 * @param aFieldName
	 *            The field name
	 * @return The field (accessible), null on error
	 */
	private Field retrieveHiddenField(final Class<?> aClass,
			final String aFieldName) {

		try {
			Field field = aClass.getDeclaredField(aFieldName);
			field.setAccessible(true);

			return field;

		} catch (SecurityException ex) {
			RestPlugin.logError("Error accessing scanner private fields", ex);
		} catch (NoSuchFieldException ex) {
			RestPlugin.logError("Error accessing scanner private fields", ex);
		}

		return null;
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

		if (isAnEOL(readChar)) {
			int readChar2 = read();
			if (!isTwoCharEOL(readChar, readChar2)) {
				unread();
			}
		}

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
