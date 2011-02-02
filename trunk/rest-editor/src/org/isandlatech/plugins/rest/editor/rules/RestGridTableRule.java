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

import eclihx.ui.internal.ui.editors.ScannerController;

/**
 * @author Thomas Calmant
 * 
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
	public IToken evaluate(final ICharacterScanner aScanner) {

		// Useless rule if not at the beginning of a line
		if (aScanner.getColumn() != 0) {
			return Token.UNDEFINED;
		}

		ScannerController controller = new ScannerController(aScanner);

		// Bad grid marker, stop immediately
		if (controller.read() != GRID_TABLE_MARKER) {
			return undefinedToken(controller);
		}

		// First line validation
		if (!validateBorderLine(controller)) {
			return undefinedToken(controller);
		}

		// Internal lines validation
		int readChar;

		while ((readChar = controller.read()) != ICharacterScanner.EOF) {

			// Just take care of the first character of the line
			if (controller.getColumn() > 1) {
				continue;
			}

			// OK if it is a grid marker
			if (readChar == GRID_TABLE_ROW_MARKER
					|| readChar == GRID_TABLE_MARKER) {
				// Grid text row => skip line
				controller.skipLine();

			} else {
				// End of table
				controller.unread();
				break;
			}
		}

		return getSuccessToken();
	}

	/**
	 * Reads the line. Stops at the first invalid character. Returns true if the
	 * line was read correctly
	 * 
	 * @param aController
	 *            Scanner controller
	 * @return True if the last line was valid. Else false and the scanner is
	 *         stopped at the faulty character.
	 */
	private boolean validateBorderLine(final ScannerController aController) {
		int readChar;
		boolean onePass = false;

		while ((readChar = aController.read()) != ICharacterScanner.EOF
				&& aController.getColumn() != 0) {

			boolean validChar = false;
			for (char allowedChar : GRID_TABLE_BORDERS_CHARACTERS) {
				if (allowedChar == readChar) {
					validChar = true;
					break;
				}
			}

			if (!validChar) {
				aController.unreadLine();
				return false;
			}

			onePass = true;
		}

		return onePass;
	}
}
