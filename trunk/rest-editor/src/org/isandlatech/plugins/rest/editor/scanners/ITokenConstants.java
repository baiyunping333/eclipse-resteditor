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
import org.eclipse.jface.text.rules.Token;

/**
 * Declaration of internal token constants
 * 
 * @author Thomas Calmant
 */
public interface ITokenConstants {

	/** Default element */
	String DEFAULT = "default";

	/** Dummy token (for contiguous rules) */
	IToken DUMMY_TOKEN = new Token("__abstract_dummy_token");

	/** Field element */
	String FIELD = "field";

	/** In-line bold text element **/
	String INLINE_BOLD_TEXT = "inline.text.bold";

	/** In-line italic text element **/
	String INLINE_EMPHASIS_TEXT = "inline.text.emphasis";

	/** In-line literal element **/
	String INLINE_LITERAL = "inline.literal";

	/** Link element */
	String LINK = "link";

	/** Footnote */
	String LINK_FOOTNOTE = "link.footnote";

	/** Reference */
	String LINK_REFERENCE = "link.reference";

	/** List bullet */
	String LIST_BULLET = "list.bullet";

	/** Literal block default element */
	String LITERAL_DEFAULT = "literal.default";

	/** Literal block directive */
	String LITERAL_DIRECTIVE = "literal.directive";

	/** Role (with or without content) */
	String ROLE = "role";

	/** Section block element */
	String SECTION = "section";

	/** Source block */
	String SOURCE = "source";

	/** Substitution */
	String SUBSTITUTION = "substitution";

	/** Table block element */
	String TABLE = "table";
}
