/**
 * File:   RestDocumentProvider.java
 * Author: Thomas Calmant
 * Date:   20 janv. 2011
 */
package org.isandlatech.plugins.rest.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.isandlatech.plugins.rest.editor.scanners.RestPartitionScanner;

/**
 * @author Thomas Calmant
 * 
 */
public class RestDocumentProvider extends FileDocumentProvider {

	/** Element => Document map */
	private Map<Object, IDocument> pDocumentsMap = new HashMap<Object, IDocument>();

	@Override
	protected IDocument createDocument(final Object aElement)
			throws CoreException {
		IDocument document = super.createDocument(aElement);

		// Set the document partitioner
		if (document != null) {

			IDocumentPartitioner partitioner = new FastPartitioner(
					new RestPartitionScanner(),
					RestPartitionScanner.PARTITION_TYPES);

			partitioner.connect(document);

			document.setDocumentPartitioner(partitioner);

			if (document instanceof IDocumentExtension3) {
				IDocumentExtension3 docExt3 = (IDocumentExtension3) document;
				docExt3.setDocumentPartitioner(
						RestPartitionScanner.PARTITIONNING, partitioner);
			}

			pDocumentsMap.put(aElement, document);
		}

		return document;
	}

	@Override
	public IDocument getDocument(final Object aElement) {
		IDocument document = pDocumentsMap.get(aElement);

		if (document == null) {
			document = super.getDocument(aElement);
		}

		return document;
	}
}
