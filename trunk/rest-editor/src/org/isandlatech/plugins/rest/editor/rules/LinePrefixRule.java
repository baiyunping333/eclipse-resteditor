/**
 * File:   LinePrefixRule.java
 * Author: Thomas Calmant
 * Date:   26 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * @author Thomas Calmant
 * 
 */
public class LinePrefixRule extends AbstractRule {

	/** Does the block begin right after the block prefix or the next line ? */
	private boolean pBeginLineAfterBlockPrefix;

	/** Prefix needed before the prefixed block */
	private String pBlockPrefix;

	/** Maximum column of the block prefix. -1 for "don't care" */
	private int pMaxBlockPrefixColumn;

	/** Maximum prefix length */
	private int pMaxPrefixLength;

	/** Allowed line prefixes */
	private char[][] pPrefixes;

	/** Rule fails if EOF is on the prefix line */
	private boolean pFailOnEOF;

	/**
	 * Configures the rule
	 * 
	 * @param aBlockPrefix
	 *            Prefix needed before the block begins (e.g. "::\n")
	 * @param aBeginLineAfterBlockPrefix
	 *            Does the block begin right after the block prefix (not the
	 *            next line) ?
	 * @param aMaxBlockPrefixColum
	 *            Maximum column of the block prefix. -1 for "don't care"
	 * @param aPrefixes
	 *            Possible lines prefixes
	 * @param aFailOnEOF
	 *            Fails if the prefix block line is ended by EOF
	 * @param aSuccessToken
	 *            Token to be returned on success
	 */
	public LinePrefixRule(final String aBlockPrefix,
			final boolean aBeginLineAfterBlockPrefix,
			final int aMaxBlockPrefixColum, final String[] aPrefixes,
			final boolean aFailOnEOF, final IToken aSuccessToken) {
		super(aSuccessToken);

		pBlockPrefix = aBlockPrefix;
		pBeginLineAfterBlockPrefix = aBeginLineAfterBlockPrefix;
		pMaxBlockPrefixColumn = aMaxBlockPrefixColum;
		pMaxPrefixLength = 0;
		pFailOnEOF = aFailOnEOF;

		pPrefixes = new char[aPrefixes.length][];
		int i = 0;
		for (String prefix : aPrefixes) {
			pPrefixes[i++] = prefix.toCharArray();

			if (prefix.length() > pMaxPrefixLength) {
				pMaxPrefixLength = prefix.length();
			}
		}
	}

	/**
	 * Configures the rule. No block prefix needed. Doesn't fail on EOF.
	 * 
	 * @param aPrefixes
	 *            Possible lines prefixes
	 * @param aSuccessToken
	 *            Token to be returned on success
	 */
	public LinePrefixRule(final String[] aPrefixes, final IToken aSuccessToken) {
		this(null, false, -1, aPrefixes, false, aSuccessToken);
	}

	@Override
	public IToken evaluate(final MarkedCharacterScanner aScanner) {
		int readChar = 0;

		// Test block begin
		if (pBlockPrefix != null) {

			if (pMaxBlockPrefixColumn >= 0
					&& aScanner.getColumn() != pMaxBlockPrefixColumn) {
				return Token.UNDEFINED;
			}

			for (int i = 0; i < pBlockPrefix.length(); i++) {
				readChar = aScanner.read();

				if (readChar == ICharacterScanner.EOF
						|| readChar != pBlockPrefix.charAt(i)) {
					return Token.UNDEFINED;
				}
			}

			// Go to next line
			while (aScanner.getColumn() != 0) {
				readChar = aScanner.read();

				// Handle EOF
				if (readChar == ICharacterScanner.EOF) {
					if (pFailOnEOF) {
						return Token.UNDEFINED;
					}

					return getSuccessToken();
				}

				// Error on none whitespace character, if the block begins the
				// next line
				if (pBeginLineAfterBlockPrefix && aScanner.getColumn() != 0
						&& !Character.isWhitespace(readChar)) {
					return Token.UNDEFINED;
				}
			}
		} else if (aScanner.getColumn() != 0) {
			// We can't return valid values if we're not at the beginning of a
			// line
			return Token.UNDEFINED;
		}

		// Test lines
		boolean validPrefixFound = false;
		boolean impureLine = false;
		int caughtLines = 0;

		List<char[]> selectedPrefixes = new ArrayList<char[]>(pPrefixes.length);
		for (char[] prefix : pPrefixes) {
			selectedPrefixes.add(prefix);
		}

		String readData = "";

		do {
			readChar = aScanner.read();
			readData += (char) readChar;

			// Stop on EOF
			if (readChar == ICharacterScanner.EOF) {
				if (!pFailOnEOF) {
					caughtLines++;
				}

				break;
			}

			int column = aScanner.getColumn();
			if (column == 0) {

				if (!validPrefixFound && impureLine) {
					// No prefix found for previous non-blank line
					// We don't have to unread the line, we're already at column
					// 0
					break;
				}

				readData = "";

				// Reset the flags on new line
				validPrefixFound = false;
				impureLine = false;
				caughtLines++;

				selectedPrefixes.clear();
				for (char[] prefix : pPrefixes) {
					selectedPrefixes.add(prefix);
				}
			} else {

				if (validPrefixFound) {
					// Don't test a validated line
					continue;
				}

				// A line is not pure anymore if there is something else than
				// white spaces
				if (!impureLine && !Character.isWhitespace(readChar)) {
					impureLine = true;
				}

				// Column is after the read character
				column--;

				// Test for prefixes
				if (column < pMaxPrefixLength) {
					for (int i = 0; i < selectedPrefixes.size(); i++) {
						char[] prefix = selectedPrefixes.get(i);

						if (column < prefix.length
								&& prefix[column] != readChar) {

							// Remove invalid prefixes
							selectedPrefixes.remove(prefix);
							i--;

						} else if (column == prefix.length - 1) {
							// We have a winner
							validPrefixFound = true;
							break;
						}
					}
				}

				if (impureLine && selectedPrefixes.size() == 0) {
					aScanner.unreadLine();
					break;
				}
			}

		} while (true);

		if (caughtLines > 0) {
			return getSuccessToken();
		} else {
			return Token.UNDEFINED;
		}
	}
}
