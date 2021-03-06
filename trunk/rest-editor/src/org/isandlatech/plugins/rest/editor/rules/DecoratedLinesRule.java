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
 * Tests if the current scanner position is in a decorated area
 * (reStructuredText sections)
 * 
 * @author Thomas Calmant
 */
public class DecoratedLinesRule extends AbstractRule {

	/**
	 * Tests is the given character is a valid ReST section decoration
	 * 
	 * @param aChar
	 *            The character to be tested
	 * @return True if the given character is not a whitespace nor a letter nor
	 *         a digit
	 */
	public static boolean isDecorationCharacter(final int aChar) {

		for (char allowedDecorator : RestLanguage.SECTION_DECORATIONS) {
			if (allowedDecorator == aChar) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Tests if the first line of the given string is a valid decorative line
	 * 
	 * @param aLine
	 *            Line to be tested
	 * @return True if the line is a valid decorative one
	 */
	public static boolean isDecorativeLine(final String aLine) {

		if (aLine == null) {
			return false;
		}

		boolean decorativeLine = true;

		for (char character : aLine.toCharArray()) {
			if (MarkedCharacterScanner.isAnEOL(character)) {
				// No need to test for two character EOL here.
				break;
			}

			if (!isDecorationCharacter(character)) {
				decorativeLine = false;
				break;
			}
		}

		return decorativeLine;
	}

	/**
	 * Configures the rule
	 * 
	 * @param aSuccessToken
	 *            Token to be returned on success
	 */
	public DecoratedLinesRule(final IToken aSuccessToken) {
		super(aSuccessToken);
	}

	@Override
	public IToken evaluate(final MarkedCharacterScanner aScanner) {

		// Useless rule if not at the beginning of a line
		if (aScanner.getColumn() != 0) {
			return Token.UNDEFINED;
		}

		// First try : the upper line
		int readChar;
		boolean upperline = true;
		int decorator = aScanner.read();

		if (aScanner.getColumn() <= 0) {
			// Empty line or EOF
			return Token.UNDEFINED;
		}

		if (isDecorationCharacter(decorator)) {
			// Decoration found, test the complete line
			readChar = decorator;
			do {
				if (readChar != decorator) {
					upperline = false;
					aScanner.skipLine();
					break;
				}

				readChar = aScanner.read();

			} while (readChar != ICharacterScanner.EOF
					&& !MarkedCharacterScanner.isAnEOL(readChar));

			// Consume the missing EOL marker, if needed
			if (MarkedCharacterScanner.isAnEOL(readChar)) {
				int readChar2 = aScanner.read();
				if (!MarkedCharacterScanner.isTwoCharEOL(readChar, readChar2)) {
					aScanner.unread();
				}
			}

		} else {
			// No decoration found, assume we are reading the content line
			upperline = false;
			if (aScanner.skipLine()) {
				return Token.UNDEFINED;
			}
		}

		if (upperline) {
			// If the first line was an upper line, the second must contain the
			// section title
			if (aScanner.skipLine()) {
				return Token.UNDEFINED;
			}

		} else {
			// We are on the under line, so the decoration is the first
			// character we read
			decorator = aScanner.read();

			// If the character is invalid, then we are not in a section
			// block (no under line)
			if (!isDecorationCharacter(decorator) || aScanner.getColumn() == 0) {
				return Token.UNDEFINED;
			}

			aScanner.unread();
		}

		// Underline
		int nbUnderlineDecorators = 0;

		while ((readChar = aScanner.read()) != ICharacterScanner.EOF) {

			if (MarkedCharacterScanner.isAnEOL(readChar)) {
				int readChar2 = aScanner.read();
				if (!MarkedCharacterScanner.isTwoCharEOL(readChar, readChar2)) {
					aScanner.unread();
				}
				break;
			}
			// Error on first different character
			else if (readChar != decorator) {
				return Token.UNDEFINED;
			}

			nbUnderlineDecorators++;
		}

		if (nbUnderlineDecorators < 2) {
			return Token.UNDEFINED;
		}

		return getSuccessToken();
	}
}
