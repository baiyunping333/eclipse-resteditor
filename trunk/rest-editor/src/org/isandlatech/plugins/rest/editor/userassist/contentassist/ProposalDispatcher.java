/**
 * File:   ProposalDispatcher.java
 * Author: Thomas Calmant
 * Date:   2 févr. 2011
 */
package org.isandlatech.plugins.rest.editor.userassist.contentassist;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.isandlatech.plugins.rest.editor.scanners.RestPartitionScanner;

/**
 * Dispatches the content assist processor to be used switch the current
 * partition in the document
 * 
 * @author Thomas Calmant
 */
public class ProposalDispatcher extends AbstractProposalProcessor {

	/** Partition - Content assist processor mapping */
	private Map<String, IContentAssistProcessor> pPartitionsProcessors;

	/**
	 * Prepares members
	 */
	public ProposalDispatcher() {
		pPartitionsProcessors = new HashMap<String, IContentAssistProcessor>();
	}

	@Override
	protected Map<String, String> buildSuggestions(final String aWord) {
		return null;
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(
			final ITextViewer aViewer, final int aOffset) {

		IDocument document = aViewer.getDocument();
		IDocumentPartitioner partitioner;

		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 doc3 = (IDocumentExtension3) document;
			partitioner = doc3
					.getDocumentPartitioner(RestPartitionScanner.PARTITIONNING);
		} else {
			partitioner = document.getDocumentPartitioner();
		}

		// No partitioner : no test
		if (partitioner == null) {
			return null;
		}

		String partititon = partitioner.getContentType(aOffset - 1);

		IContentAssistProcessor processor = pPartitionsProcessors
				.get(partititon);
		if (processor != null) {
			return processor.computeCompletionProposals(aViewer, aOffset);
		}

		return null;
	}

	/**
	 * Sets the processor associated to the partition
	 * 
	 * @param aPartition
	 *            Document partition
	 * @param aProcessor
	 *            Content assist processor
	 */
	public void set(final String aPartition,
			final IContentAssistProcessor aProcessor) {

		if (aPartition == null || aProcessor == null) {
			return;
		}

		pPartitionsProcessors.put(aPartition, aProcessor);
	}

	/**
	 * Remove the informations relative to the given partition
	 * 
	 * @param aPartition
	 *            Document partition
	 */
	public void unset(final String aPartition) {
		pPartitionsProcessors.remove(aPartition);
	}
}
