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

package org.isandlatech.plugins.rest.editor.formatters;

/**
 * Default text formatter : deletes trailing white spaces
 * 
 * @author Thomas Calmant
 */
public class DefaultTextFormattingStrategy extends AbstractFormattingStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.formatters.AbstractFormattingStrategy
	 * #format(java.lang.String, boolean, java.lang.String, int[])
	 */
	@Override
	public String format(final String aContent, final boolean aIsLineStart,
			final String aIndentation, final int[] aPositions) {

		String normalizedContent = normalizeEndOfLines(aContent);
		StringBuilder newContent = new StringBuilder(aContent.length());

		// Standard line breaks
		String[] lines = getLines(normalizedContent);
		for (String line : lines) {

			// Trim right
			newContent.append(line.replaceAll("[\\s]+$", ""));

			// Reset line break
			if (line.isEmpty()) {
				newContent.append(NORMALIZED_LINE_BREAK);
			}
		}

		return resetEndOfLines(newContent.toString());
	}
}
