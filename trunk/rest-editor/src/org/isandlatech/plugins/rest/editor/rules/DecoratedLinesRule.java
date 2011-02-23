/**
 * File:   DecoratedLinesRule.java
 * Author: Thomas Calmant
 * Date:   26 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.isandlatech.plugins.rest.parser.RestLanguage;

import eclihx.ui.internal.ui.editors.ScannerController;

/**
 * @author Thomas Calmant
 * 
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
			if (isAnEOL(character)) {
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
	public IToken evaluate(final ICharacterScanner aScanner) {

		// Useless rule if not at the beginning of a line
		if (aScanner.getColumn() != 0) {
			return Token.UNDEFINED;
		}

		ScannerController controller = new ScannerController(aScanner);
		int readChar;

		// First try : the upper line
		boolean upperline = true;
		int decorator = controller.read();

		if (controller.getColumn() <= 0) {
			// Empty line
			return undefinedToken(controller);
		}

		if (isDecorationCharacter(decorator)) {
			while ((readChar = controller.read()) != ICharacterScanner.EOF
					&& controller.getColumn() != 0) {
				if (readChar != decorator && !isAnEOL(readChar)) {
					upperline = false;
				}
			}
		} else {
			upperline = false;
			if (controller.skipLine()) {
				return undefinedToken(controller);
			}
		}

		if (upperline) {
			// If the first line was an upper line, the second must contain the
			// section title
			if (controller.skipLine()) {
				return undefinedToken(controller);
			}

		} else {
			// We are on the under line, so the decoration is the first
			// character we read
			decorator = controller.read();

			// If the character is invalid, then we are not in a section
			// block (no under line)
			if (!isDecorationCharacter(decorator)
					|| controller.getColumn() == 0) {
				return undefinedToken(controller);
			}

			controller.unread();
		}

		// Underline
		while ((readChar = controller.read()) != ICharacterScanner.EOF) {

			if (isAnEOL(readChar)) {
				break;
			}
			// Error on first different character
			else if (readChar != decorator) {
				return undefinedToken(controller);
			}
		}

		return getSuccessToken();
	}
}
