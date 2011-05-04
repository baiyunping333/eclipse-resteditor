/**
 * File:   RestGridTableRule.java
 * Author: Thomas Calmant
 * Date:   26 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.isandlatech.plugins.rest.parser.RestLanguage;

/**
 * Detects reStructuredText grid tables
 * 
 * @author Thomas Calmant
 */
public class RestGridTableRule extends AbstractRule implements RestLanguage {

	/**
	 * Configures the rule
	 * 
	 * @param aSuccessToken
	 *            Token to be returned on success
	 */
	public RestGridTableRule(final IToken aSuccessToken) {
		super(aSuccessToken);
	}

	@Override
	public IToken evaluate(final MarkedCharacterScanner aScanner) {

		// Useless rule if not at the beginning of a line
		if (aScanner.getColumn() != 0) {
			return Token.UNDEFINED;
		}

		// Bad grid marker, stop immediately
		if (aScanner.read() != GRID_TABLE_MARKER) {
			return Token.UNDEFINED;
		}

		// First line validation
		if (!validateBorderLine(aScanner)) {
			return Token.UNDEFINED;
		}

		// Internal lines validation
		int readChar;

		while ((readChar = aScanner.read()) != ICharacterScanner.EOF) {

			// Just take care of the first character of the line
			if (aScanner.getColumn() > 1) {
				continue;
			}

			// OK if it is a grid marker
			if (readChar == GRID_TABLE_ROW_MARKER
					|| readChar == GRID_TABLE_MARKER) {
				// Grid text row => skip line
				aScanner.skipLine();

			} else {
				// End of table
				aScanner.unread();
				break;
			}
		}

		return getSuccessToken();
	}

	/**
	 * Reads the line. Stops at the first invalid character. Returns true if the
	 * line was read correctly
	 * 
	 * @param aScanner
	 *            Scanner controller
	 * @return True if the last line was valid. Else false and the scanner is
	 *         stopped at the faulty character.
	 */
	private boolean validateBorderLine(final MarkedCharacterScanner aScanner) {
		int readChar;
		boolean onePass = false;

		while ((readChar = aScanner.read()) != ICharacterScanner.EOF
				&& readChar != '\n') {

			boolean validChar = false;
			for (char allowedChar : GRID_TABLE_BORDERS_CHARACTERS) {
				if (allowedChar == readChar) {
					validChar = true;
					break;
				}
			}

			if (!validChar) {
				aScanner.unreadLine();
				return false;
			}

			onePass = true;
		}

		return onePass;
	}
}
