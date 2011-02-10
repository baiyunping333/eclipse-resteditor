/**
 * File:   RestSourceBlockScanner.java
 * Author: Thomas Calmant
 * Date:   27 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.scanners;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;

/**
 * Source code block scanner
 * 
 * @author Thomas Calmant
 */
public class RestSourceBlockScanner extends RuleBasedPartitionScanner {

	/**
	 * Only sets the default token
	 * 
	 * @param aTokenProvider
	 *            Token provider
	 */
	public RestSourceBlockScanner(final TokenProvider aTokenProvider) {
		super();

		// Create the tokens
		IToken defaultToken = aTokenProvider
				.getTokenForElement(ITokenConstants.SOURCE);

		// Default token
		setDefaultReturnToken(defaultToken);
	}
}
