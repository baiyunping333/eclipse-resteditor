/**
 * File:   RestSourceBlockScanner.java
 * Author: Thomas Calmant
 * Date:   27 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.scanners;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;

/**
 * @author Thomas Calmant
 * 
 */
public class RestSourceBlockScanner extends RuleBasedPartitionScanner {

	public RestSourceBlockScanner(final TokenProvider aTokenProvider) {
		super();

		// Create the tokens
		IToken defaultToken = aTokenProvider
				.getTokenForElement(TokenProvider.SOURCE);

		// Default token
		setDefaultReturnToken(defaultToken);
	}
}
