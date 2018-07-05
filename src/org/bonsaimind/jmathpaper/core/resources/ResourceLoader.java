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

package org.bonsaimind.jmathpaper.core.resources;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.bonsaimind.jmathpaper.core.ConfigurationProcessor;

/**
 * {@link ResourceLoader} is a static utility for loading embedded resources.
 */
public final class ResourceLoader {
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
		StringBuilder pattern = new StringBuilder();
		
		processResource(REGEX_PACKAGE + "/" + name + ".regex", pattern::append, null);
		
		return Pattern.compile(pattern.toString());
	}
	
	/**
	 * Iterates over each line of the given resource.
	 * <p>
	 * This function will strip empty lines and also comments (see
	 * {@link ConfigurationProcessor#process(java.io.InputStream, Consumer, String)}
	 * .
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
	 * This function will strip empty lines and also comments (see
	 * {@link ConfigurationProcessor#process(java.io.InputStream, Consumer, String)}
	 * .
	 * 
	 * @param relativePath The path to the resource relative to this class.
	 * @param lineProcessor The function to execute for every line.
	 * @param lineEnding The line ending to append to each line.
	 */
	private static final void processResource(String relativePath, Consumer<String> lineProcessor, String lineEnding) {
		ConfigurationProcessor.process(
				ResourceLoader.class.getResourceAsStream(relativePath),
				lineProcessor,
				lineEnding);
	}
}
