/*
 * Copyright 2018, Robert 'Bobby' Zenz
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bonsaimind.jmathpaper.core;

import org.bonsaimind.jmathpaper.core.ui.Ui;

/**
 * The {@link DynamicLoader} is a helper class to dynamically load {@link Class}
 * es.
 */
public final class DynamicLoader {
	private DynamicLoader() {
	}
	
	/**
	 * Loads the {@link Ui} class with the given name.
	 * <p>
	 * The name can either be a full qualified class name or a "simple" name
	 * which will be automatically converted to a fully qualified class name.
	 * 
	 * @param uiClassName The name of the UI class.
	 * @return The {@link Ui} instance.
	 * @throws ClassNotFoundException If there is no such class.
	 * @throws InstantiationException If the instance could not be created.
	 * @throws IllegalAccessException If the class could not be accessed.
	 * @throws IllegalStateException If the found class is not an instance of
	 *         {@link Ui}.
	 */
	public static final Ui getUi(String uiClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalStateException {
		Class<Ui> uiClass = getUiClass(uiClassName);
		
		if (uiClass != null) {
			return uiClass.newInstance();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected static final Class<Ui> getUiClass(String uiClassName) throws ClassNotFoundException, IllegalStateException {
		Class<?> clazz = null;
		
		if (uiClassName.contains(".")) {
			clazz = Class.forName(uiClassName.trim());
		} else {
			String packageName = uiClassName.trim().toLowerCase();
			String className = packageName.substring(0, 1).toUpperCase() + packageName.substring(1);
			
			clazz = Class.forName("org.bonsaimind.jmathpaper." + packageName + "." + className);
		}
		
		if (Ui.class.isAssignableFrom(clazz)) {
			return (Class<Ui>)clazz;
		} else if (clazz != null) {
			throw new IllegalStateException(clazz.getName() + " does not implement " + Ui.class.getName());
		}
		
		return null;
	}
}
