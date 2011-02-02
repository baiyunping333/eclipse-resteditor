/**
 * File:   RestTableBlockScanner.java
 * Author: Thomas Calmant
 * Date:   26 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.scanners;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;

/**
 * @author Thomas Calmant
 * 
 */
public class RestTableBlockScanner extends RuleBasedPartitionScanner {

	public RestTableBlockScanner(final TokenProvider aTokenProvider) {
		super();

		// Create the tokens
		IToken defaultToken = aTokenProvider
				.getTokenForElement(TokenProvider.TABLE);

		// Default token
		setDefaultReturnToken(defaultToken);
	}
}
