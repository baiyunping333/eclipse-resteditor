/**
 * File:   ListBlockWrappingHandler.java
 * Author: Thomas Calmant
 * Date:   27 mai 2011
 */
package org.isandlatech.plugins.rest.editor.linewrap.v2;

/**
 * @author Thomas Calmant
 * 
 */
public class ListBlockWrappingHandler extends AbstractBlockWrappingHandler {

	/** Handler type */
	public static String HANDLER_TYPE = "__list_block_handler__";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #getType()
	 */
	@Override
	public String getType() {
		return HANDLER_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.isandlatech.plugins.rest.editor.linewrap.v2.IBlockWrappingHandler
	 * #wrap(int)
	 */
	@Override
	public String wrap(final int aMaxLen) {
		// TODO Auto-generated method stub

		// First line = base indentation (same treatment as normal block)
		// Next lines = base indentation + marker length (but same treatment as
		// normal block)

		replaceInternalLineMarkers();
		return null;
	}
}
