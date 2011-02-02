/**
 * File:   RestSectionBlockScanner.java
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
public class RestSectionBlockScanner extends RuleBasedPartitionScanner {

	public RestSectionBlockScanner(final TokenProvider aTokenProvider) {
		super();

		// Create the tokens
		IToken defaultToken = aTokenProvider
				.getTokenForElement(TokenProvider.SECTION);

		// Default token
		setDefaultReturnToken(defaultToken);
	}
}
