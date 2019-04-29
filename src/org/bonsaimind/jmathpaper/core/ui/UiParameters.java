/*
 * Copyright 2018, Robert 'Bobby' Zenz
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

import java.util.HashMap;
import java.util.Map;

/**
 * {@link UiParameters} is a simple container class for parameters which are
 * being used by an UI implementation.
 */
public class UiParameters {
	protected Map<String, String> parameters = null;
	
	/**
	 * Creates a new instance of {@link UiParameters}.
	 *
	 * @param parameters The {@link Map} of parameters to use, can not be
	 *        {@code null}.
	 */
	public UiParameters(Map<String, String> parameters) {
		super();
		
		this.parameters = new HashMap<>(parameters);
	}
	
	/**
	 * Gets the parameter value with the given name.
	 * 
	 * @param parameterName The name of the parameter to get, case-sensitive.
	 * @return The value of the parameter with the given name, {@code false} if
	 *         there is no such parameter.
	 */
	public boolean getBoolean(String parameterName) {
		return getBoolean(parameterName, false);
	}
	
	/**
	 * Gets the parameter value with the given name.
	 * 
	 * @param parameterName The name of the parameter to get, case-sensitive.
	 * @param defaultValue The default value to return if the parameter does not
	 *        exist.
	 * @return The value of the parameter with the given name, the default value
	 *         if there is no such parameter.
	 */
	public boolean getBoolean(String parameterName, boolean defaultValue) {
		if (parameters.containsKey(parameterName)) {
			return parseBoolean(parameters.get(parameterName), defaultValue);
		} else {
			return defaultValue;
		}
	}
	
	/**
	 * Gets the parameter value with the given name.
	 * 
	 * @param parameterName The name of the parameter to get, case-sensitive.
	 * @return The value of the parameter with the given name, {@code 0} if
	 *         there is no such parameter.
	 */
	public int getInt(String parameterName) {
		return getInt(parameterName, 0);
	}
	
	/**
	 * Gets the parameter value with the given name.
	 * 
	 * @param parameterName The name of the parameter to get, case-sensitive.
	 * @param defaultValue The default value to return if the parameter does not
	 *        exist.
	 * @return The value of the parameter with the given name, the default value
	 *         if there is no such parameter.
	 */
	public int getInt(String parameterName, int defaultValue) {
		if (parameters.containsKey(parameterName)) {
			try {
				return Integer.parseInt(parameters.get(parameterName));
			} catch (NumberFormatException e) {
				// Ignore the exception, return the default value.
				return defaultValue;
			}
		} else {
			return defaultValue;
		}
	}
	
	/**
	 * Gets the parameter value with the given name.
	 * 
	 * @param parameterName The name of the parameter to get, case-sensitive.
	 * @return The value of the parameter with the given name, {@code null} if
	 *         there is no such parameter.
	 */
	public String getString(String parameterName) {
		return getString(parameterName, null);
	}
	
	/**
	 * Gets the parameter value with the given name.
	 * 
	 * @param parameterName The name of the parameter to get, case-sensitive.
	 * @param defaultValue The default value to return if the parameter does not
	 *        exist.
	 * @return The value of the parameter with the given name, the default value
	 *         if there is no such parameter.
	 */
	public String getString(String parameterName, String defaultValue) {
		if (parameters.containsKey(parameterName)) {
			return parameters.get(parameterName);
		} else {
			return defaultValue;
		}
	}
	
	/**
	 * Gets whether the parameter with the given name does exist.
	 * 
	 * @param parameterName The name of the parameter to check.
	 * @return {@code true} if the parameter with the given name exists.
	 */
	public boolean has(String parameterName) {
		return parameters.containsKey(parameterName);
	}
	
	/**
	 * Parses the given value as boolean. Additionally to the default
	 * {@code true} it also recognizes {@code t}, {@code y} and {@code yes}.
	 * 
	 * @param value The value to parse as boolean.
	 * @param defaultValue The default value to return if the value is
	 *        {@code null}.
	 * @return The given value parsed as boolean, the default value if it is
	 *         {@code null}.
	 */
	protected boolean parseBoolean(String value, boolean defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		
		return value.equalsIgnoreCase("t")
				|| value.equalsIgnoreCase("true")
				|| value.equalsIgnoreCase("y")
				|| value.equalsIgnoreCase("yes");
	}
}
