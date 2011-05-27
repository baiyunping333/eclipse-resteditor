/**
 * File:   BlockWrappingHandlerFactory.java
 * Author: Thomas Calmant
 * Date:   27 mai 2011
 */
package org.isandlatech.plugins.rest.editor.linewrap.v2;


/**
 * @author Thomas Calmant
 * 
 */
public class BlockWrappingHandlerFactory {

	/**
	 * Retrieves an instance of the default block wrapping handler
	 * 
	 * @return an instance of the default block wrapping handler
	 */
	public static IBlockWrappingHandler getDefault() {
		return new DefaultBlockWrappingHandler();
	}

	/**
	 * Retrieves the handler associated to the given handler type
	 * 
	 * @param type
	 *            The handler type
	 * @return The handler associated to the given type, null if none found
	 */
	public static IBlockWrappingHandler getHandler(final String type) {

		if (type == null || IBlockWrappingHandler.DEFAULT_HANDLER.equals(type)) {
			return getDefault();
		}

		if (type.equals(ListBlockWrappingHandler.HANDLER_TYPE)) {
			return new ListBlockWrappingHandler();
		}

		return null;
	}
}
