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

package org.isandlatech.plugins.rest.editor.linewrap.v2;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Thomas Calmant
 * 
 */
public class BlockWrappingHandlerStore {

	/** Factory singleton */
	private static BlockWrappingHandlerStore sSingleton = new BlockWrappingHandlerStore();

	/**
	 * Retrieves the factory singleton
	 * 
	 * @return the factory singleton
	 */
	public static BlockWrappingHandlerStore get() {
		return sSingleton;
	}

	/** List of all registered handlers */
	private Set<IBlockWrappingHandler> pHandlers;

	/** Default handler */
	private IBlockWrappingHandler pDefaultHandler;

	/** Singleton constructor. Registers the default block handler */
	private BlockWrappingHandlerStore() {

		pDefaultHandler = new DefaultBlockWrappingHandler();

		pHandlers = new HashSet<IBlockWrappingHandler>();
		pHandlers.add(pDefaultHandler);
	}

	/**
	 * Retrieves an instance of the default block wrapping handler
	 * 
	 * @return an instance of the default block wrapping handler
	 */
	public IBlockWrappingHandler getDefault() {
		return pDefaultHandler;
	}

	/**
	 * Retrieves the handler associated to the given handler type
	 * 
	 * @param type
	 *            The handler type
	 * @return The handler associated to the given type, null if none found
	 */
	public IBlockWrappingHandler getHandler(final String type) {

		if (type == null) {
			return pDefaultHandler;
		}

		for (IBlockWrappingHandler handler : pHandlers) {

			if (type.equals(handler.getType())) {
				return handler;
			}
		}

		return null;
	}

	/**
	 * Registers a handler instance to the store
	 * 
	 * @param handler
	 *            Handler to be registered
	 * @return True if the handler wasn't already registered, else false
	 */
	public boolean registerHandler(final IBlockWrappingHandler handler) {
		return pHandlers.add(handler);
	}

	/**
	 * Unregisters a handler instance from the store
	 * 
	 * @param handler
	 *            Handler to be unregistered
	 * @return True if the handler was in the set, else false
	 */
	public boolean unregisterHandler(final IBlockWrappingHandler handler) {
		return pHandlers.remove(handler);
	}
}
