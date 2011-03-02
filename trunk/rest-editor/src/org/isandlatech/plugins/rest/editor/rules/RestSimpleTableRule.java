/**
 * File:   RestSimpleTableRule.java
 * Author: Thomas Calmant
 * Date:   26 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.isandlatech.plugins.rest.parser.RestLanguage;

/**
 * @author Thomas Calmant
 * 
 */
public class RestSimpleTableRule extends AbstractRule implements RestLanguage {

	/**
	 * Type of the analyzed line
	 * 
	 * @author Thomas Calmant
	 */
	public enum ELineType {
		EOF, EMPTY, MARKER, TEXT,
	}

	/**
	 * Configures the rule
	 * 
	 * @param aSuccessToken
	 *            Token to be returned on success
	 */
	public RestSimpleTableRule(final IToken aSuccessToken) {
		super(aSuccessToken);
	}

	/**
	 * Analyzes the next line.
	 * 
	 * @param aMarkedScanner
	 *            Scanner controller
	 * @return {@link ELineType#EMPTY} if the line is empty,
	 *         {@link ELineType#MARKER} if the line is a simple table marker row
	 *         or {@link ELineType#TEXT} if the line contains any other kind of
	 *         data.
	 */
	protected ELineType analyzeNextLine(
			final MarkedCharacterScanner aMarkedScanner) {
		int readChar;
		boolean emptyLine = true;
		boolean markerLine = true;

		while ((readChar = aMarkedScanner.read()) != ICharacterScanner.EOF
				&& readChar != '\n') {

			if (emptyLine && !Character.isWhitespace(readChar)) {
				emptyLine = false;
			}

			if (markerLine
					&& !((readChar == SIMPLE_TABLE_MARKER || (Character
							.isWhitespace(readChar) && aMarkedScanner
							.getColumn() > 1)))) {
				markerLine = false;
			}
		}

		if (emptyLine) {
			return ELineType.EMPTY;
		}

		if (markerLine) {
			return ELineType.MARKER;
		}

		return ELineType.TEXT;
	}

	@Override
	public IToken evaluate(final MarkedCharacterScanner aScanner) {

		// Useless rule if not at the beginning of a line
		if (aScanner.getColumn() != 0) {
			return Token.UNDEFINED;
		}

		// First line : stop immediately on bad grid marker, or if the line
		// begins with a white space
		if (analyzeNextLine(aScanner) != ELineType.MARKER) {
			return Token.UNDEFINED;
		}

		// Second line : must not be empty
		ELineType lineType = analyzeNextLine(aScanner);
		if (lineType == ELineType.EMPTY) {
			return Token.UNDEFINED;
		}

		// We're in the table until the next blank line following a marker row
		// or the end of the file
		ELineType oldLineType;
		do {
			oldLineType = lineType;
			lineType = analyzeNextLine(aScanner);
		} while (!(lineType == ELineType.EMPTY && oldLineType == ELineType.MARKER)
				&& lineType != ELineType.EOF);

		return getSuccessToken();
	}
}
