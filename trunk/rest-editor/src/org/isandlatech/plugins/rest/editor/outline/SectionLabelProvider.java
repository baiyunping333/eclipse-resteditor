/**
 * File:   SectionLabelProvider.java
 * Author: Thomas Calmant
 * Date:   27 janv. 2011
 */
package org.isandlatech.plugins.rest.editor.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * Converts tree content into labels
 * 
 * @author Thomas Calmant
 */
public class SectionLabelProvider implements ILabelProvider {

	/** List of listeners */
	private List<ILabelProviderListener> pListeners = new ArrayList<ILabelProviderListener>();

	@Override
	public void addListener(final ILabelProviderListener aListener) {
		pListeners.add(aListener);
	}

	@Override
	public void dispose() {
		pListeners.clear();
	}

	@Override
	public Image getImage(final Object aElement) {
		return null;
	}

	@Override
	public String getText(final Object aElement) {
		return String.valueOf(aElement);
	}

	@Override
	public boolean isLabelProperty(final Object aElement, final String aProperty) {
		return false;
	}

	@Override
	public void removeListener(final ILabelProviderListener aListener) {
		pListeners.remove(aListener);
	}
}
