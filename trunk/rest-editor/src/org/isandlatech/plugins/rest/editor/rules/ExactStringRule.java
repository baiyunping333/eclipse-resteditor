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

/**
 * Searches for the given string, with or without trimming.
 * 
 * @author Thomas Calmant
 */
public class ExactStringRule extends AbstractRule {

	/** Maximum column of the string (-1 to ignore) */
	private int pMaxColumn;

	/** String to find */
	private char[] pPatternCharArray;

	/** Ignore all white spaces before the string */
	private boolean pTrimLeft;

	/**
	 * Configures the rule : no column test.
	 * 
	 * @param aPattern
	 *            String to search for
	 * @param aTrimLeft
	 *            Ignore all white spaces before the string
	 * @param aSuccessToken
	 *            Token returned on success
	 */
	public ExactStringRule(final String aPattern, final boolean aTrimLeft,
			final IToken aSuccessToken) {
		this(aPattern, -1, aTrimLeft, aSuccessToken);
	}

	/**
	 * Configures the rule
	 * 
	 * @param aPattern
	 *            String to search for
	 * @param aMaxColumn
	 *            Maximum column of the string (-1 to ignore)
	 * @param aSuccessToken
	 *            Token returned on success
	 */
	public ExactStringRule(final String aPattern, final int aMaxColumn,
			final boolean aTrimLeft, final IToken aSuccessToken) {
		super(aSuccessToken);

		if (aPattern != null) {
			pPatternCharArray = aPattern.toCharArray();
		} else {
			pPatternCharArray = null;
		}

		pMaxColumn = aMaxColumn;
		pTrimLeft = aTrimLeft;
	}

	/**
	 * Configures the rule : no column test, no trim left.
	 * 
	 * @param aPattern
	 *            String to search for
	 * @param aSuccessToken
	 *            Token returned on success
	 */
	public ExactStringRule(final String aPattern, final IToken aSuccessToken) {
		this(aPattern, -1, false, aSuccessToken);
	}

	@Override
	public IToken evaluate(final MarkedCharacterScanner aScanner) {

		if (pPatternCharArray == null) {
			return Token.UNDEFINED;
		}

		// Skip white spaces
		int trimmed = 0;
		if (pTrimLeft) {
			int readChar;
			do {
				readChar = aScanner.read();
				trimmed++;
			} while (readChar != ICharacterScanner.EOF
					&& Character.isWhitespace(readChar));

			// Unread the last character
			trimmed--;
			aScanner.unread();
		}

		if (pMaxColumn >= 0 && aScanner.getColumn() - trimmed > pMaxColumn) {
			return Token.UNDEFINED;
		}

		for (char character : pPatternCharArray) {
			if (character != aScanner.read()) {
				return Token.UNDEFINED;
			}
		}

		return getSuccessToken();
	}
}
