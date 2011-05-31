/**
 * File:   DefaultBlockWrappingHandler.java
 * Author: Thomas Calmant
 * Date:   27 mai 2011
 */
package org.isandlatech.plugins.rest.editor.linewrap.v2;

/**
 * Default block wrapper : tries to conserve the indentation
 * 
 * @author Thomas Calmant
 */
public class DefaultBlockWrappingHandler extends AbstractBlockWrappingHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #getType()
	 */
	@Override
	public String getType() {
		return DEFAULT_HANDLER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #wrap()
	 */
	@Override
	public String wrap(final int aMaxLen) {

		// Transform the block in a line
		StringBuilder blockInLine = convertBlockInLine(getBlockContent());

		// Do the wrapping
		setBlockContent(wrapLine(blockInLine.toString(), aMaxLen).toString());

		// Convert internals EOL into document ones
		return replaceInternalLineMarkers();
	}
}
