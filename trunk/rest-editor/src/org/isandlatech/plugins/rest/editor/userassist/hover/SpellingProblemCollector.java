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

package org.isandlatech.plugins.rest.editor.userassist.hover;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

/**
 * Stores all encountered problems while testing words spelling
 * 
 * @author Thomas Calmant
 */
public class SpellingProblemCollector implements ISpellingProblemCollector {

	/** Problems list */
	private List<SpellingProblem> pProblemsList = new ArrayList<SpellingProblem>();

	@Override
	public void accept(final SpellingProblem aProblem) {
		pProblemsList.add(aProblem);
	}

	@Override
	public void beginCollecting() {
		// Do nothing
	}

	@Override
	public void endCollecting() {
		// Do nothing
	}

	/**
	 * Retrieves the list of all encountered spelling problems.
	 * 
	 * The result can't be null.
	 * 
	 * @return The list of all problems found
	 */
	public List<SpellingProblem> getProblems() {
		return pProblemsList;
	}
}
