package org.isandlatech.plugins.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.isandlatech.plugins.rest.editor.scanners.RestPartitionScanner;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RestPlugin extends AbstractUIPlugin {

	/** The shared instance */
	private static RestPlugin plugin;

	/** The plug-in ID */
	public static final String PLUGIN_ID = "ReSTEditor"; //$NON-NLS-1$

	/** The plug-in name */
	public static final String PLUGIN_NAME = "ReST Editor";

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static RestPlugin getDefault() {
		return plugin;
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
		plugin = this;
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
		plugin = null;
		super.stop(context);
	}
}
