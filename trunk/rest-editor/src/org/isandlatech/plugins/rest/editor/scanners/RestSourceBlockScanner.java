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

package org.isandlatech.plugins.rest.editor.scanners;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.isandlatech.plugins.rest.editor.providers.TokenProvider;

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
