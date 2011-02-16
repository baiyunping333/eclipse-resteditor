/**
 * File:   RestViewerConfiguration.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor;

import java.util.Arrays;

import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TabsToSpacesConverter;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy;
import org.eclipse.ui.texteditor.spelling.SpellingService;
import org.isandlatech.plugins.rest.editor.contentassist.DeclarativeProposalProcessor;
import org.isandlatech.plugins.rest.editor.contentassist.ProposalDispatcher;
import org.isandlatech.plugins.rest.editor.formatters.GridTableFormattingStrategy;
import org.isandlatech.plugins.rest.editor.formatters.SectionFormattingStrategy;
import org.isandlatech.plugins.rest.editor.scanners.RestLiteralBlockScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestPartitionScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestSectionBlockScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestSourceBlockScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestTableBlockScanner;
import org.isandlatech.plugins.rest.editor.scanners.RuleProvider;
import org.isandlatech.plugins.rest.editor.scanners.TokenProvider;

/**
 * @author Thomas Calmant
 * 
 */
public class RestViewerConfiguration extends TextSourceViewerConfiguration {

	/** Content pAssistant */
	private ContentAssistant pAssistant = null;

	/** ReST document token scanner */
	private RestScanner pDocScanner = null;

	/** Document formatter */
	private ContentFormatter pDocFormatter = null;

	/** Token provider */
	private TokenProvider pTokenProvider = null;

	/** Scanner rule provider */
	private RuleProvider pRuleProvider = null;

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(
			final ISourceViewer aSourceViewer, final String aContentType) {

		// Automatic tabs to space conversion
		TabsToSpacesConverter tabs2spaces = new TabsToSpacesConverter();
		tabs2spaces.setNumberOfSpacesPerTab(getTabWidth(aSourceViewer));
		tabs2spaces.setLineTracker(new DefaultLineTracker());

		return new IAutoEditStrategy[] { tabs2spaces };
	}

	@Override
	public String[] getConfiguredContentTypes(final ISourceViewer sourceViewer) {
		return RestPartitionScanner.PARTITION_TYPES;
	}

	@Override
	public String getConfiguredDocumentPartitioning(
			final ISourceViewer sourceViewer) {
		return RestPartitionScanner.PARTITIONNING;
	}

	@Override
	public IContentAssistant getContentAssistant(
			final ISourceViewer aSourceViewer) {

		if (pAssistant == null) {
			pAssistant = new ContentAssistant();

			// Mandatory for contextual description
			pAssistant
					.setInformationControlCreator(getInformationControlCreator(aSourceViewer));

			// Document partitioning informations
			pAssistant
					.setDocumentPartitioning(getConfiguredDocumentPartitioning(aSourceViewer));

			// Set the content assistant processor for literal blocks
			ProposalDispatcher proposalDispatcher = new ProposalDispatcher();

			IContentAssistProcessor literalBlocksProcessor = new DeclarativeProposalProcessor();
			proposalDispatcher.set(RestPartitionScanner.LITERAL_BLOCK,
					literalBlocksProcessor);
			pAssistant.setContentAssistProcessor(literalBlocksProcessor,
					RestPartitionScanner.LITERAL_BLOCK);

			pAssistant.setContentAssistProcessor(proposalDispatcher,
					IDocument.DEFAULT_CONTENT_TYPE);
		}

		return pAssistant;
	}

	@Override
	public IContentFormatter getContentFormatter(
			final ISourceViewer aSourceViewer) {

		if (pDocFormatter == null) {

			pDocFormatter = new ContentFormatter();
			pDocFormatter.enablePartitionAwareFormatting(true);
			pDocFormatter
					.setDocumentPartitioning(RestPartitionScanner.PARTITIONNING);

			// Sections formatter
			pDocFormatter.setFormattingStrategy(
					new SectionFormattingStrategy(),
					RestPartitionScanner.SECTION_BLOCK);

			// Tables formatter
			pDocFormatter.setFormattingStrategy(
					new GridTableFormattingStrategy(),
					RestPartitionScanner.GRID_TABLE_BLOCK);

			// TODO RestPartitionScanner.SIMPLE_TABLE_BLOCK
			// TODO IDocument.DEFAULT_CONTENT_TYPE
		}

		return pDocFormatter;
	}

	@Override
	public String[] getDefaultPrefixes(final ISourceViewer aSourceViewer,
			final String aContentType) {
		return getIndentPrefixes(aSourceViewer, aContentType);
	}

	/**
	 * Retrieves the instance unique token pDocScanner
	 * 
	 * @return The instance token pDocScanner
	 */
	protected ITokenScanner getDocumentScanner() {
		if (pDocScanner == null) {
			initProviders();
			pDocScanner = new RestScanner(pRuleProvider);
		}

		return pDocScanner;
	}

	// FIXME doesn't work as expected
	@Override
	public String[] getIndentPrefixes(final ISourceViewer aSourceViewer,
			final String aContentType) {
		// TODO use preferences
		return new String[] { "   ", "\t", "", };
	}

	// FIXME doesn't work as expected
	@Override
	protected String[] getIndentPrefixesForTab(final int aTabWidth) {
		// TODO use preferences
		char[] array = new char[3];
		Arrays.fill(array, ' ');

		return new String[] { new String(array) };
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(
			final ISourceViewer aSourceViewer) {

		initProviders();

		// Scanners
		RestLiteralBlockScanner literalBlockScanner = new RestLiteralBlockScanner(
				pRuleProvider);

		RestSectionBlockScanner sectionBlockScanner = new RestSectionBlockScanner(
				pRuleProvider);

		RestSourceBlockScanner sourceBlockScanner = new RestSourceBlockScanner(
				pTokenProvider);

		RestTableBlockScanner tableBlockScanner = new RestTableBlockScanner(
				pRuleProvider);

		// Reconciler
		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler
				.setDocumentPartitioning(getConfiguredDocumentPartitioning(aSourceViewer));

		// Text data
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(
				getDocumentScanner());

		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		// Literal blocks
		dr = new DefaultDamagerRepairer(literalBlockScanner);
		reconciler.setDamager(dr, RestPartitionScanner.LITERAL_BLOCK);
		reconciler.setRepairer(dr, RestPartitionScanner.LITERAL_BLOCK);

		// Section blocks
		dr = new DefaultDamagerRepairer(sectionBlockScanner);
		reconciler.setDamager(dr, RestPartitionScanner.SECTION_BLOCK);
		reconciler.setRepairer(dr, RestPartitionScanner.SECTION_BLOCK);

		// Source blocks
		dr = new DefaultDamagerRepairer(sourceBlockScanner);
		reconciler.setDamager(dr, RestPartitionScanner.SOURCE_BLOCK);
		reconciler.setRepairer(dr, RestPartitionScanner.SOURCE_BLOCK);

		// Table blocks
		dr = new DefaultDamagerRepairer(tableBlockScanner);
		reconciler.setDamager(dr, RestPartitionScanner.GRID_TABLE_BLOCK);
		reconciler.setRepairer(dr, RestPartitionScanner.GRID_TABLE_BLOCK);

		return reconciler;
	}

	@Override
	public IReconciler getReconciler(final ISourceViewer aSourceViewer) {

		SpellingService spellingService = EditorsUI.getSpellingService();
		IReconcilingStrategy strategy = new SpellingReconcileStrategy(
				aSourceViewer, spellingService);

		MonoReconciler reconciler = new MonoReconciler(strategy, false);
		return reconciler;
	}

	@Override
	public int getTabWidth(final ISourceViewer aSourceViewer) {
		// TODO use preferences
		return 3;
	}

	/**
	 * Initializes the token and rule providers if necessary
	 */
	private void initProviders() {
		if (pTokenProvider == null) {
			pTokenProvider = new TokenProvider();
		}

		if (pRuleProvider == null
				|| pRuleProvider.getTokenProvider() != pTokenProvider) {
			pRuleProvider = new RuleProvider(pTokenProvider);
		}
	}

	/**
	 * Auto formating when the editor saves the file
	 * 
	 * @param aSourceViewer
	 *            The editor source viewer
	 */
	public void onEditorPerformSave(final ISourceViewer aSourceViewer) {

		// Doc informations
		IDocument document = aSourceViewer.getDocument();
		IRegion docRegion = new Region(0, document.getLength());

		// Format the document
		pDocFormatter.format(document, docRegion);
	}
}
