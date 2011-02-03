/**
 * File:   RestPartitionScanner.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.isandlatech.plugins.rest.editor.rules.DecoratedLinesRule;
import org.isandlatech.plugins.rest.editor.rules.LinePrefixRule;
import org.isandlatech.plugins.rest.editor.rules.RestGridTableRule;
import org.isandlatech.plugins.rest.editor.rules.RestSimpleTableRule;
import org.isandlatech.plugins.rest.parser.RestLanguage;

/**
 * @author Thomas Calmant
 * 
 */
public class RestPartitionScanner extends RuleBasedPartitionScanner {

	/** ReST grid tables */
	public static final String GRID_TABLE_BLOCK = "__section_grid_table";

	/** ReST literal block (indented) */
	public static final String LITERAL_BLOCK = "__literal_block";

	/** Scanner identifier */
	public static final String PARTITIONNING = "reStructuredText";

	/** ReST section */
	public static final String SECTION_BLOCK = "__section_block";

	/** ReST simple tables */
	public static final String SIMPLE_TABLE_BLOCK = "__section_simple_table";

	/** ReST source block */
	public static final String SOURCE_BLOCK = "__source_block";

	/** Possible partitions types */
	public static final String[] PARTITION_TYPES = {
			IDocument.DEFAULT_CONTENT_TYPE, LITERAL_BLOCK, SECTION_BLOCK,
			SOURCE_BLOCK, GRID_TABLE_BLOCK, SIMPLE_TABLE_BLOCK };

	/**
	 * Sets up the scanner rules
	 */
	public RestPartitionScanner() {
		super();

		// Create the partitions tokens
		IToken literalToken = new Token(LITERAL_BLOCK);
		IToken sectionToken = new Token(SECTION_BLOCK);
		IToken sourceToken = new Token(SOURCE_BLOCK);

		IToken gridTableToken = new Token(GRID_TABLE_BLOCK);
		IToken simpleTableToken = new Token(SIMPLE_TABLE_BLOCK);

		// Create the rules to identify tokens
		List<IRule> rules = new ArrayList<IRule>();

		// Sections (can conflict with RestSimpleTableRule)
		rules.add(new DecoratedLinesRule(sectionToken));

		// Tables
		rules.add(new RestGridTableRule(gridTableToken));
		rules.add(new RestSimpleTableRule(simpleTableToken));

		// Comments / literal blocks
		rules.add(new LinePrefixRule(".. ", false, 0,
				RestLanguage.LITERAL_BLOCK_PREFIXES, false, literalToken));

		rules.add(new LinePrefixRule("..", true, 0,
				RestLanguage.LITERAL_BLOCK_PREFIXES, false, literalToken));

		// Source blocks
		rules.add(new LinePrefixRule("::", true, -1,
				RestLanguage.LITERAL_BLOCK_PREFIXES, false, sourceToken));

		// Pass the rules to the partitioner
		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);

		setPredicateRules(result);
	}

}
