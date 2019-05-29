/*
 * Copyright 2009 SIB Visions GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *
 * History
 * 
 * 14.11.2008 - [JR] - creation
 * 04.11.2009 - [JR] - moved image scaling to ImageUtil
 * 28.02.2011 - [JR] - setImageMapping: clear image cache
 * 13.05.2011 - [JR] - setGlobalCursor: check if change is necessary 
 * 25.08.2011 - [JR] - #465: clipboard actions via JNLP services
 * 31.08.2012 - [JR] - fixed NPEs in static {} when LaF does not support used colors
 * 24.09.2013 - [JR] - getIcon(String, byte[]) -> fallback to getIcon(String)
 */

/*
 * This class has been stripped of all not needed functionality.
 * 
 * For the original please see the JVx distribution packages which you can
 * obtain either through Maven or from https://sourceforge.net/projects/jvx/.
 */

package com.sibvisions.rad.ui.swing.ext;

import java.awt.Component;
import java.awt.Dimension;

/**
 * The <code>JVxUtil</code> is a utility class with often used functionality
 * encapsulated in useful methods.
 * 
 * @author Rene Jahn
 */
public final class JVxUtil {
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Initialization
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * Invisible constructor, because the <code>JVxUtil</code> class is a
	 * utility class.
	 */
	protected JVxUtil() {
	}
	
	/**
	 * Gets the preferred size of a component. The size is between the minimum
	 * and maximum size.
	 * 
	 * @param pComponent the component
	 * @return the preferred size dependent of the minimum and maximum size
	 */
	public static Dimension getPreferredSize(Component pComponent) {
		Dimension dimMin = pComponent.getMinimumSize();
		Dimension dimPref = pComponent.getPreferredSize();
		Dimension dimMax = pComponent.getMaximumSize();
		
		int iWidth = dimPref.width;
		int iHeight = dimPref.height;
		
		if (pComponent.isMinimumSizeSet()) {
			if (dimMin.width > iWidth) {
				iWidth = dimMin.width;
			}
			
			if (dimMin.height > iHeight) {
				iHeight = dimMin.height;
			}
		}
		
		if (pComponent.isMaximumSizeSet()) {
			if (dimMax.width < iWidth) {
				iWidth = dimMax.width;
			}
			
			if (dimMax.height < iHeight) {
				iHeight = dimMax.height;
			}
		}
		
		return new Dimension(iWidth, iHeight);
	}
	
} // JVxUtil
