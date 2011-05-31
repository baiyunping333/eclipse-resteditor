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

package org.isandlatech.plugins.rest.editor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.isandlatech.plugins.rest.RestPlugin;
import org.isandlatech.plugins.rest.editor.scanners.RestPartitionScanner;

/**
 * Sets up the document partitionner. Only works with IDocumentExtension3
 * documents.
 * 
 * @author Thomas Calmant
 */
public class RestDocumentSetupParticipant implements IDocumentSetupParticipant {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.filebuffers.IDocumentSetupParticipant#setup(org.eclipse
	 * .jface.text.IDocument)
	 */
	@Override
	public void setup(final IDocument aDocument) {

		if (aDocument instanceof IDocumentExtension3) {

			IDocumentPartitioner partitioner = new FastPartitioner(RestPlugin
					.getDefault().getPartitionScanner(),
					RestPartitionScanner.PARTITION_TYPES);

			IDocumentExtension3 docExt3 = (IDocumentExtension3) aDocument;
			docExt3.setDocumentPartitioner(RestPartitionScanner.PARTITIONNING,
					partitioner);

			partitioner.connect(aDocument);
		}
	}
}
