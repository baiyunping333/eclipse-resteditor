/**
 * File:   RestViewerConfiguration.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
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
import org.isandlatech.plugins.rest.editor.formatters.SectionFormattingStrategy;
import org.isandlatech.plugins.rest.editor.formatters.TableFormattingStrategy;
import org.isandlatech.plugins.rest.editor.scanners.RestLiteralBlockScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestPartitionScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestSectionBlockScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestSourceBlockScanner;
import org.isandlatech.plugins.rest.editor.scanners.RestTableBlockScanner;
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
			pDocFormatter.setFormattingStrategy(new TableFormattingStrategy(),
					RestPartitionScanner.TABLE_BLOCK);

			// formatter.setFormattingStrategy(strategy,
			// IDocument.DEFAULT_CONTENT_TYPE);
		}

		return pDocFormatter;
	}

	/**
	 * Retrieves the instance unique token pDocScanner
	 * 
	 * @return The instance token pDocScanner
	 */
	protected ITokenScanner getDocumentScanner() {
		if (pDocScanner == null) {
			TokenProvider provider = new TokenProvider();
			pDocScanner = new RestScanner(provider);
		}

		return pDocScanner;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(
			final ISourceViewer aSourceViewer) {

		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler
				.setDocumentPartitioning(getConfiguredDocumentPartitioning(aSourceViewer));

		// Text data
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(
				getDocumentScanner());

		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		// Literal blocks
		dr = new DefaultDamagerRepairer(new RestLiteralBlockScanner(
				new TokenProvider()));
		reconciler.setDamager(dr, RestPartitionScanner.LITERAL_BLOCK);
		reconciler.setRepairer(dr, RestPartitionScanner.LITERAL_BLOCK);

		// Section blocks
		dr = new DefaultDamagerRepairer(new RestSectionBlockScanner(
				new TokenProvider()));
		reconciler.setDamager(dr, RestPartitionScanner.SECTION_BLOCK);
		reconciler.setRepairer(dr, RestPartitionScanner.SECTION_BLOCK);

		// Source blocks
		dr = new DefaultDamagerRepairer(new RestSourceBlockScanner(
				new TokenProvider()));
		reconciler.setDamager(dr, RestPartitionScanner.SOURCE_BLOCK);
		reconciler.setRepairer(dr, RestPartitionScanner.SOURCE_BLOCK);

		// Table blocks
		dr = new DefaultDamagerRepairer(new RestTableBlockScanner(
				new TokenProvider()));
		reconciler.setDamager(dr, RestPartitionScanner.TABLE_BLOCK);
		reconciler.setRepairer(dr, RestPartitionScanner.TABLE_BLOCK);

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

	public void onEditorPerformSave(final ISourceViewer aSourceViewer) {

		// Doc informations
		IDocument document = aSourceViewer.getDocument();
		IRegion docRegion = new Region(0, document.getLength());

		// Format the document
		pDocFormatter.format(document, docRegion);
	}
}
