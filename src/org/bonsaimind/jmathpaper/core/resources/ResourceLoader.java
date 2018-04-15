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
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * {@link ResourceLoader} is a static utility for loading embedded resources.
 */
public final class ResourceLoader {
	/** The package which contains the resources. */
	private static final String BASE_PACKAGE = "/" + ResourceLoader.class.getPackage().getName().replace(".", "/");
	
	/** The {@link String} with which a comment in a file starts. */
	private static final String COMMENT_START = "#";
	
	/** The package which contains the regex files/resources. */
	private static final String REGEX_PACKAGE = "regex";
	
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
		return Pattern.compile(loadResource(REGEX_PACKAGE + "/" + name + ".regex", null));
	}
	
	/**
	 * Loads the content of the given resource with the specified line endings.
	 * <p>
	 * This function will strip empty lines and also comments (see the
	 * {@link #COMMENT_START} string.
	 * 
	 * @param relativePath The path to the resource relative to this class.
	 * @param lineEnding The string to use as line-ending.
	 * @return The content of the given resource file.
	 */
	public static final String loadResource(String relativePath, String lineEnding) {
		StringBuilder content = new StringBuilder();
		
		processResource(relativePath, content::append, lineEnding);
		
		return content.toString();
	}
	
	/**
	 * Iterates over each line of the given resource.
	 * <p>
	 * This function will strip empty lines and also comments (see the
	 * {@link #COMMENT_START} string.
	 * 
	 * @param relativePath The path to the resource relative to this class.
	 * @param lineProcessor The function to execute for every line.
	 */
	public static final void processResource(String relativePath, Consumer<String> lineProcessor) {
		processResource(relativePath, lineProcessor, null);
	}
	
	/**
	 * Iterates over each line of the given resource.
	 * <p>
	 * This function will strip empty lines and also comments (see the
	 * {@link #COMMENT_START} string.
	 * 
	 * @param relativePath The path to the resource relative to this class.
	 * @param lineProcessor The function to execute for every line.
	 * @param lineEnding The line ending to append to each line.
	 */
	public static final void processResource(String relativePath, Consumer<String> lineProcessor, String lineEnding) {
		String file = BASE_PACKAGE + "/" + relativePath;
		
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						ResourceLoader.class.getResourceAsStream(file),
						StandardCharsets.UTF_8))) {
			
			String line = reader.readLine();
			
			while (line != null) {
				int commentIndex = line.indexOf(COMMENT_START);
				
				if (commentIndex >= 0) {
					line = line.substring(0, commentIndex);
				}
				
				line = line.trim();
				
				if (line.length() > 0) {
					if (lineEnding != null) {
						line = line + lineEnding;
					}
					
					lineProcessor.accept(line);
				}
				
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			
			return;
		}
	}
}
