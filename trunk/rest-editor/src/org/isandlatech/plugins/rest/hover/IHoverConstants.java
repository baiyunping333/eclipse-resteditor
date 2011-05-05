/**
 * File:   IHoverConstants.java
 * Author: Thomas Calmant
 * Date:   5 mai 2011
 */
package org.isandlatech.plugins.rest.hover;

/**
 * Common constants used by text hover process
 * 
 * @author Thomas Calmant
 */
public interface IHoverConstants {

	/** Browser internal links prefix */
	String INTERNAL_PREFIX = "rest-internal://";

	/** Sample insertion action prefix */
	String SAMPLE_LINK_PREFIX = "insert-sample/";

	/** Spelling action prefix in internal links */
	String SPELL_LINK_PREFIX = "spell/";

	/** Insert sample message */
	String INSERT_SAMPLE_MESSAGE = "help.directive.sample.insert";
}
