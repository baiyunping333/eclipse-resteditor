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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.isandlatech.plugins.rest.parser.RestLanguage;

/**
 * Searches for surrounded strings (tags, ...)
 * 
 * @author Thomas Calmant
 */
public class MarkupRule extends AbstractRule {

	/** Marker end string */
	private final String pEnd;

	/** False if first character mustn't be a white space */
	private final boolean pNoSpace;

	/** If true, the value between markers must be on a single line */
	private final boolean pSingleLine;

	/** Marker start string */
	private final String pStart;

	/**
	 * Configures the rule
	 * 
	 * @param aMarkupDelimiter
	 *            Start and end marker
	 * @param aToken
	 *            Token returned on success
	 */
	public MarkupRule(final String aMarkupDelimiter, final IToken aToken) {
		this(aMarkupDelimiter, aMarkupDelimiter, aToken, true);
	}

	/**
	 * Configures the rule
	 * 
	 * @param aMarkupStart
	 *            Start marker
	 * @param aMarkupEnd
	 *            End marker
	 * @param aToken
	 *            Token returned on success
	 */
	public MarkupRule(final String aMarkupStart, final String aMarkupEnd,
			final IToken aToken) {
		this(aMarkupStart, aMarkupEnd, aToken, true);
	}

	/**
	 * Configures the rule
	 * 
	 * @param aMarkupStart
	 *            Start marker
	 * @param aMarkupEnd
	 *            End marker
	 * @param aToken
	 *            Token returned on success
	 * @param aNoBoundSpace
	 *            If true, the marker must be follow by anything but a
	 *            whitespace
	 */
	public MarkupRule(final String aMarkupStart, final String aMarkupEnd,
			final IToken aToken, final boolean aNoBoundSpace) {
		this(aMarkupStart, aMarkupEnd, aToken, aNoBoundSpace, true);
	}

	/**
	 * Configures the rule
	 * 
	 * @param aMarkupStart
	 *            Start marker
	 * @param aMarkupEnd
	 *            End marker
	 * @param aToken
	 *            Token returned on success
	 * @param aNoBoundSpace
	 *            If true, the marker must be follow by anything but a
	 *            whitespace
	 * @param aSingleLine
	 *            If true, the text between markers must be on a single line
	 */
	public MarkupRule(final String aMarkupStart, final String aMarkupEnd,
			final IToken aToken, final boolean aNoBoundSpace,
			final boolean aSingleLine) {
		super(aToken);

		pStart = aMarkupStart;
		pEnd = aMarkupEnd;
		pNoSpace = aNoBoundSpace;
		pSingleLine = aSingleLine;
	}

	@Override
	public IToken evaluate(final MarkedCharacterScanner aScanner) {

		int readChar;

		// If the previous character is an escape one, ignore the starting
		// marker
		if (aScanner.getColumn() > 0) {
			aScanner.unread();
			readChar = aScanner.read();

			if (readChar == RestLanguage.ESCAPE_CHARACTER) {
				return Token.UNDEFINED;
			}
		}

		// Test markup beginning
		for (int i = 0; i < pStart.length(); i++) {
			readChar = aScanner.read();

			if (readChar == ICharacterScanner.EOF
					|| pStart.charAt(i) != readChar) {
				return Token.UNDEFINED;
			}
		}

		// Test first character
		if (pNoSpace) {
			readChar = aScanner.read();

			if (readChar == ICharacterScanner.EOF
					|| Character.isWhitespace(readChar)) {
				return Token.UNDEFINED;
			}
		}

		// Test markup ending
		int currentMarkupPos = 0;
		String currentEnd = "";

		int lastReadChar = 0;
		while ((readChar = aScanner.read()) != ICharacterScanner.EOF) {

			// EOL in single line mode -> stop
			if (pSingleLine && MarkedCharacterScanner.isAnEOL(readChar)) {
				int readChar2 = aScanner.read();
				if (!MarkedCharacterScanner.isTwoCharEOL(readChar, readChar2)) {
					aScanner.unread();
				}
				return Token.UNDEFINED;
			}

			// Escaped character -> restart pEnd search
			if (lastReadChar == RestLanguage.ESCAPE_CHARACTER) {
				currentMarkupPos = 0;
				currentEnd = "";
				continue;
			}

			if (pEnd.charAt(currentMarkupPos) == readChar
					&& !(pNoSpace && Character.isWhitespace(lastReadChar))) {

				currentEnd += (char) readChar;
				currentMarkupPos++;

				if (currentEnd.endsWith(pEnd)) {
					do {
						readChar = aScanner.read();
						if (readChar != ICharacterScanner.EOF) {
							currentEnd += (char) readChar;
						} else {
							break;
						}
					} while (currentEnd.endsWith(pEnd));

					if (readChar != ICharacterScanner.EOF) {
						aScanner.unread();
					}

					return getSuccessToken();
				}

			} else {
				currentMarkupPos = 0;
				currentEnd = "";
			}

			lastReadChar = readChar;
		}

		return Token.UNDEFINED;
	}
}
