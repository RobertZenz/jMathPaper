/*
 * Copyright 2017, Robert 'Bobby' Zenz
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

package org.bonsaimind.jmathpaper.core.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * {@link ResourceLoader} is a static utility for loading embedded resources.
 */
public final class ResourceLoader {
	/** The {@link String} with which a comment in a regex file starts. */
	private static final String REGEX_COMMENT_START = "#";
	
	/** The package which contains the regex files/resources. */
	private static final String REGEX_PACKAGE = "/" + ResourceLoader.class.getPackage().getName().replace(".", "/") + "/regex";
	
	/**
	 * No instance required.
	 */
	private ResourceLoader() {
		// No instance required.
	}
	
	/**
	 * Loads the content of the regex file with the given name and compiles it.
	 * 
	 * @param name The name of the regex file, without path or extension.
	 * @return The {@link Pattern} compiled from the regex.
	 */
	public static final Pattern compileRegex(String name) {
		return Pattern.compile(loadRegex(name));
	}
	
	/**
	 * Loads the regex content with the given name.
	 * 
	 * @param name The name of the regex file, without path or extension.
	 * @return The content of the regex file with the given name.
	 */
	public static final String loadRegex(String name) {
		String regexFile = REGEX_PACKAGE + "/" + name + ".regex";
		
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						ResourceLoader.class.getResourceAsStream(regexFile),
						StandardCharsets.UTF_8))) {
			
			StringBuilder content = new StringBuilder();
			
			String line = reader.readLine();
			
			while (line != null) {
				int commentIndex = line.indexOf(REGEX_COMMENT_START);
				
				if (commentIndex >= 0) {
					line = line.substring(0, commentIndex);
				}
				
				line = line.trim();
				
				content.append(line);
				
				line = reader.readLine();
			}
			
			return content.toString();
		} catch (IOException e) {
			e.printStackTrace();
			
			return null;
		}
	}
}
