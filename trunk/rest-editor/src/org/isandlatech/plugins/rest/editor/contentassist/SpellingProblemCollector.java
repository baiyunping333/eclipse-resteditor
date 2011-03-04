package org.isandlatech.plugins.rest.editor.contentassist;

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
