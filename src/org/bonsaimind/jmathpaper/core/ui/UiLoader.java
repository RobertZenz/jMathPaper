/*
 * Copyright 2017, Robert 'Bobby' Zenz
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.bonsaimind.jmathpaper.core.ui;

/**
 * The {@link UiLoader} is a helper class to dynamically load {@link Class} es.
 */
public final class UiLoader {
	private UiLoader() {
	}
	
	/**
	 * Loads the first available {@link Ui} class with the given name.
	 * <p>
	 * This function will try the given class names and return the first one
	 * that can be instantiated.
	 * <p>
	 * The name can either be a full qualified class name or a "simple" name
	 * which will be automatically converted to a fully qualified class name.
	 * 
	 * @param uiClassNames The names of the UI classes to try.
	 * @return The {@link Ui} instance, {@code null} if none could be created.
	 */
	public static final Ui getAvailableUi(String... uiClassNames) {
		if (uiClassNames == null || uiClassNames.length == 0) {
			return null;
		}
		
		for (String uiClassName : uiClassNames) {
			try {
				Ui ui = getUi(uiClassName);
				
				if (ui != null) {
					return ui;
				}
			} catch (Exception e) {
				// Ignore the exception.
			}
		}
		
		return null;
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
			
			clazz = Class.forName("org.bonsaimind.jmathpaper.uis." + packageName + "." + className);
		}
		
		if (Ui.class.isAssignableFrom(clazz)) {
			return (Class<Ui>)clazz;
		} else if (clazz != null) {
			throw new IllegalStateException(clazz.getName() + " does not implement " + Ui.class.getName());
		}
		
		return null;
	}
}
