/**
 * Source: http://eclihx.googlecode.com
 */
package eclihx.ui.internal.ui.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Simple class which extend number of operations available for
 * ICharacterScanner. Note that you <b>can't</b> unread more character you have
 * read before.
 * 
 * Class is not finished yet.
 */
public final class ScannerController {

	/**
	 * Check if the read result isn't end of file flag.
	 * 
	 * @param readResult
	 *            the result of the read operation.
	 * @return <code>true</code> if parameter <b>isn't</b> end of file.
	 */
	private static boolean notEOF(final int readResult) {
		return readResult != ICharacterScanner.EOF;
	}

	/**
	 * Number of characters we had read.
	 */
	private int readLength;

	/**
	 * Original scanner.
	 */
	private final ICharacterScanner scanner;

	/**
	 * Constructor which wrap the original scanner.
	 * 
	 * @param scanner
	 *            base scanner.
	 */
	public ScannerController(final ICharacterScanner scanner) {
		this.scanner = scanner;
	}

	/**
	 * Simple proxy call to the scanner
	 * 
	 * @return The current column
	 * @see ICharacterScanner#getColumn()
	 */
	public int getColumn() {
		return scanner.getColumn();
	}

	/**
	 * Read one character.
	 * 
	 * @return result of the ICharacterScanner.read() operation.
	 */
	public int read() {
		int result = scanner.read();
		readLength += 1;

		return result;
	}

	/**
	 * Reading defined number of characters
	 * 
	 * @param length
	 *            number of characters to read.
	 * @return the string we had read.
	 */
	public String readString(final int length) {

		StringBuilder builder = new StringBuilder();

		for (int readResult = read(), counter = 0; counter < length; ++counter, readResult = read()) {

			if (notEOF(readResult)) { // We can't read anymore
				break;
			}

			// Good! We have read a character. Append it to the string
			builder.append((char) readResult);
		}

		return builder.toString();
	}

	/**
	 * Read the string defined by the word detector
	 * 
	 * @param wordDetectror
	 *            the word detector.
	 * @return String we had read or <code>null</code> if scanner was at the end
	 *         or start character is invalid.
	 */
	public String readString(final IWordDetector wordDetectror) {
		int readResult = read();

		if (notEOF(readResult) && wordDetectror.isWordStart((char) readResult)) {
			StringBuilder builder = new StringBuilder();

			do {
				builder.append((char) readResult);
				readResult = read();
			} while (notEOF(readResult)
					&& wordDetectror.isWordPart((char) readResult));

			// We should unread last character because it doesn't belong to the
			// string
			unread();

			return builder.toString();

		} else {
			return null;
		}
	}

	/**
	 * Skips the current line
	 * 
	 * @return true if the line was empty, else false
	 */
	public boolean skipLine() {
		boolean emptyLine = true;

		do {
			if (read() == ICharacterScanner.EOF) {
				break;
			}

			if (emptyLine && scanner.getColumn() > 0) {
				emptyLine = false;
			}

		} while (scanner.getColumn() != 0);

		return emptyLine;
	}

	/**
	 * Unread one character. Note that you can't unread more character you have
	 * read before.
	 * 
	 * @return <code>true</code> if we unread something
	 */
	public boolean unread() {
		if (readLength == 0) {
			return false;
		}

		scanner.unread();
		--readLength;

		return true;
	}

	/**
	 * Unread all characters scanner had read before.
	 * 
	 * @return <code>true</code> if we unread something.
	 */
	public boolean unreadAll() {
		if (readLength == 0) {
			return false;
		}

		for (; readLength > 0; --readLength) {
			scanner.unread();
		}

		return true;
	}

	/**
	 * Unread all characters of the current line
	 * 
	 * @return <code>true</code> if we unread something.
	 */
	public boolean unreadLine() {
		if (readLength == 0) {
			return false;
		}

		while (scanner.getColumn() != 0 && readLength > 0) {
			scanner.unread();
			--readLength;
		}

		return true;
	}
}
