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
 * @author Thomas Calmant
 * 
 */
public class SectionLabelProvider implements ILabelProvider {

	/** List of listeners */
	private List<ILabelProviderListener> pListeners = new ArrayList<ILabelProviderListener>();

	@Override
	public void addListener(final ILabelProviderListener listener) {
		pListeners.add(listener);
	}

	@Override
	public void dispose() {
		pListeners.clear();
	}

	@Override
	public Image getImage(final Object element) {
		return null;
	}

	@Override
	public String getText(final Object element) {

		if (element != null) {
			return element.toString();
		}

		return "<null>";
	}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {
		pListeners.remove(listener);
	}
}
