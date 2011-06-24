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

package org.isandlatech.plugins.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.isandlatech.plugins.rest.editor.scanners.RestPartitionScanner;
import org.isandlatech.plugins.rest.i18n.Messages;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Thomas Calmant
 */
public class RestPlugin extends AbstractUIPlugin {

	/** The shared instance */
	private static RestPlugin sPlugin;

	/** The plug-in ID */
	public static final String PLUGIN_ID = "ReSTEditor";

	/** The plug-in name */
	public static final String PLUGIN_NAME = Messages.getString("plugin.name");

	/**
	 * The specific rest content-type id (cf. full qualified id : PLUGIN_ID +
	 * content-type id as defined in plug-in xml file)
	 **/
	public static final String REST_CONTENT_ID = PLUGIN_ID + ".restSource";

	/**
	 * @see the definition of this content type in the extension point
	 *      "org.eclipse.core.contenttype.contentTypes" in the manifest file of
	 *      the plug-in.
	 */
	public static final IContentType REST_CONTENT_TYPE = Platform
			.getContentTypeManager().getContentType(REST_CONTENT_ID);

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static RestPlugin getDefault() {
		return sPlugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Logs the given status
	 * 
	 * @param aStatus
	 *            Status to be logged
	 */
	public static void log(final IStatus aStatus) {
		sPlugin.getLog().log(aStatus);
	}

	/**
	 * Logs the given error
	 * 
	 * @param aMessage
	 *            Error message
	 * @param aThrowable
	 *            Associated exception or error
	 */
	public static void logError(final String aMessage,
			final Throwable aThrowable) {

		IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, aMessage,
				aThrowable);
		sPlugin.getLog().log(status);
	}

	/**
	 * Logs the given information
	 * 
	 * @param aMessage
	 *            Message to be logged
	 */
	public static void logInfo(final String aMessage) {

		IStatus status = new Status(IStatus.INFO, PLUGIN_ID, aMessage);
		sPlugin.getLog().log(status);
	}

	/**
	 * Makes a new image which is a 45° counter-clockwise rotation of the given
	 * one
	 * 
	 * @param aImage
	 *            Base image
	 * @return A rotated image
	 */
	public static ImageDescriptor rotateImage(final Image aImage) {

		ImageData srcData = aImage.getImageData();
		if (srcData == null) {
			return null;
		}

		ImageData destData = new ImageData(srcData.height, srcData.width,
				srcData.depth, srcData.palette);

		/* rotate by rearranging the pixels */
		for (int i = 0; i < srcData.width; i++) {

			for (int j = 0; j < srcData.height; j++) {

				// Color
				int pixel = srcData.getPixel(i, j);
				destData.setPixel(j, srcData.width - 1 - i, pixel);

				// Transparency
				int alpha = srcData.getAlpha(i, j);
				destData.setAlpha(j, srcData.width - 1 - i, alpha);

			}
		}

		return ImageDescriptor.createFromImageData(destData);
	}

	/** The partition scanner */
	private RestPartitionScanner pPartitionScanner;

	/**
	 * The constructor
	 */
	public RestPlugin() {
	}

	/**
	 * Reads the given file in the bundle
	 * 
	 * @param aFilePath
	 *            File path, '/' is the root of the bundle JAR file
	 * @return The file content
	 * @throws IOException
	 *             An error occurred while opening the file stream
	 */
	public String getBundleFileContent(final String aFilePath)
			throws IOException {

		// Open the file
		BufferedReader reader = getBundleFileReader(aFilePath);

		// Read it
		StringBuilder content = new StringBuilder();
		int charsRead = 0;
		char[] buffer = new char[2048];

		while ((charsRead = reader.read(buffer)) > 0) {
			content.append(buffer, 0, charsRead);
		}

		// Close it
		reader.close();
		return content.toString();
	}

	/**
	 * Opens a buffered reader for the given file in the bundle
	 * 
	 * @param aFilePath
	 *            File path, '/' is the root of the bundle JAR file
	 * @return A buffered reader for the requested file
	 * @throws IOException
	 *             An error occurred while opening the file stream
	 */
	public BufferedReader getBundleFileReader(final String aFilePath)
			throws IOException {

		// Read the template file
		Path templatePath = new Path(aFilePath);

		InputStream templateStream = FileLocator.openStream(getBundle(),
				templatePath, false);

		return new BufferedReader(new InputStreamReader(templateStream));
	}

	/**
	 * Retrieves the plugin-instance unique partition scanner
	 * 
	 * @return The partition scanner
	 */
	public RestPartitionScanner getPartitionScanner() {

		if (pPartitionScanner == null) {
			pPartitionScanner = new RestPartitionScanner();
		}

		return pPartitionScanner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		sPlugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		sPlugin = null;
		super.stop(context);
	}
}
