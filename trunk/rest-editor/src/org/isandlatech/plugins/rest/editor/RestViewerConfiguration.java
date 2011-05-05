/**
 * File:   RestViewerConfiguration.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TabsToSpacesConverter;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.SpellingService;
import org.isandlatech.plugins.rest.RestPlugin;
import org.isandlatech.plugins.rest.editor.contentassist.DeclarativeProposalProcessor;
import org.isandlatech.plugins.rest.editor.contentassist.ProposalDispatcher;
import org.isandlatech.plugins.rest.editor.formatters.GridTableFormattingStrategy;
import org.isandlatech.plugins.rest.editor.formatters.SectionFormattingStrategy;
import org.isandlatech.plugins.rest.editor.providers.RuleProvider;
import org.isandlatech.plugins.rest.editor.providers.TokenProvider;
import org.isandlatech.plugins.rest.editor.scanners.RestLiteralBlockScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestPartitionScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestSectionBlockScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestSourceBlockScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestTableBlockScanner;
import org.isandlatech.plugins.rest.hover.HoverBrowserInformationControl;
import org.isandlatech.plugins.rest.hover.RestTextHover;
import org.isandlatech.plugins.rest.prefs.IEditorPreferenceConstants;

/**
 * RestEditor configuration handler
 * 
 * @author Thomas Calmant
 */
public class RestViewerConfiguration extends TextSourceViewerConfiguration {

	/** Content pAssistant */
	private ContentAssistant pAssistant = null;

	/** Document formatter */
	private ContentFormatter pDocFormatter = null;

	/** ReST document token scanner */
	private RestScanner pDocScanner = null;

	/** Preference store */
	private IPreferenceStore pPreferenceStore = null;

	/** Scanner rule provider */
	private RuleProvider pRuleProvider = null;

	/** Spell check text hover */
	private RestTextHover pSpellCheckHover = null;

	/** Token provider */
	private TokenProvider pTokenProvider = null;

	/**
	 * Prepares the configuration. Get a preference store reference.
	 */
	public RestViewerConfiguration() {
		super();
		pPreferenceStore = RestPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(
			final ISourceViewer aSourceViewer, final String aContentType) {

		List<IAutoEditStrategy> strategies = new ArrayList<IAutoEditStrategy>(2);
		strategies.add(new DefaultIndentLineAutoEditStrategy());

		if (pPreferenceStore
				.getBoolean(IEditorPreferenceConstants.EDITOR_TABS_TO_SPACES)) {

			// Automatic tabs to space conversion
			TabsToSpacesConverter tabs2spaces = new TabsToSpacesConverter();
			tabs2spaces.setNumberOfSpacesPerTab(getTabWidth(aSourceViewer));
			tabs2spaces.setLineTracker(new DefaultLineTracker());

			strategies.add(tabs2spaces);
		}

		return strategies.toArray(new IAutoEditStrategy[0]);
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
					.setInformationControlCreator(HoverBrowserInformationControl
							.getCreator());

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

	@Override
	public String[] getIndentPrefixes(final ISourceViewer aSourceViewer,
			final String aContentType) {
		return new String[] { "   ", "\t", "" };
	}

	@Override
	protected String[] getIndentPrefixesForTab(final int aTabWidth) {

		boolean useSpaces = pPreferenceStore
				.getBoolean(IEditorPreferenceConstants.EDITOR_TABS_TO_SPACES);

		if (useSpaces) {
			char[] array = new char[aTabWidth];
			Arrays.fill(array, ' ');

			return new String[] { new String(array) };
		}

		return new String[] { "\t" };
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

	// @Override
	// public IReconciler getReconciler(final ISourceViewer aSourceViewer) {
	//
	// // Uses the preferences to select the spell engine
	// SpellingService selectedService = new SpellingService(pPreferenceStore);
	//
	// IReconcilingStrategy strategy = new SpellingReconcileStrategy(
	// aSourceViewer, selectedService);
	//
	// MonoReconciler reconciler = new MonoReconciler(strategy, false);
	// return reconciler;
	// }

	@Override
	public int getTabWidth(final ISourceViewer aSourceViewer) {
		return pPreferenceStore
				.getInt(IEditorPreferenceConstants.EDITOR_TABS_LENGTH);
	}

	@Override
	public ITextHover getTextHover(final ISourceViewer aSourceViewer,
			final String aContentType) {

		if (pSpellCheckHover == null) {
			SpellingService selectedService = new SpellingService(
					pPreferenceStore);

			ISpellingEngine engine;
			try {
				engine = selectedService.getActiveSpellingEngineDescriptor(
						pPreferenceStore).createEngine();

				pSpellCheckHover = new RestTextHover(engine);

			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return pSpellCheckHover;
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

		if (pPreferenceStore
				.getBoolean(IEditorPreferenceConstants.EDITOR_SAVE_FORMAT)) {

			// Doc informations
			IDocument document = aSourceViewer.getDocument();
			IRegion docRegion = new Region(0, document.getLength());

			// Format the document
			pDocFormatter.format(document, docRegion);
		}
	}
}
