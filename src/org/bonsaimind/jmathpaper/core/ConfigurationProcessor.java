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

package org.bonsaimind.jmathpaper.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * The {@link ConfigurationProcessor} is an helper utility to read configuration
 * files.
 * <p>
 * The configuration files are parsed line by line, with {@link #COMMENT_START}
 * being the character which is being used to indicate comments. Comments can
 * appear anywhere in the line and will end with the end of the line.
 */
public final class ConfigurationProcessor {
	/** The {@link String} with which a comment in a file starts. */
	public static final String COMMENT_START = "#";
	
	/**
	 * No instance required.
	 */
	public ConfigurationProcessor() {
		// No instance required.
	}
	
	/**
	 * Iterates over each line of the given {@link InputStream}.
	 * <p>
	 * This function will strip empty lines and also comments (see the
	 * {@link #COMMENT_START} {@link String}.
	 * 
	 * @param inputStream The {@link InputStream} from which to read. Will be
	 *        {@link InputStream#close() closed} when done. Can be {@code null},
	 *        in which case this function does nothing.
	 * @param lineProcessor The function to execute for every line. Can be
	 *        {@code null}, in which case this function does nothing.
	 * @param lineEnding The line ending to append to each line, can be
	 *        {@code null} for none.
	 */
	public static final void process(InputStream inputStream, Consumer<String> lineProcessor, String lineEnding) {
		if (inputStream == null || lineProcessor == null) {
			return;
		}
		
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			
			String line = reader.readLine();
			
			while (line != null) {
				int commentIndex = line.indexOf(COMMENT_START);
				
				if (commentIndex >= 0) {
					// Test if the comment char might have been escaped.
					if (commentIndex == 0 || line.charAt(commentIndex - 1) != '\\') {
						line = line.substring(0, commentIndex);
					} else {
						line = line.substring(0, commentIndex - 1) + line.substring(commentIndex);
					}
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
	
	/**
	 * Iterates over each line of the given {@link InputStream}.
	 * <p>
	 * This function will strip empty lines and also comments (see the
	 * {@link #COMMENT_START} {@link String}.
	 * 
	 * @param file The {@link Path file} from which to read. Can be {@code null}
	 *        , in which case this function does nothing.
	 * @param lineProcessor The function to execute for every line. Can be
	 *        {@code null}, in which case this function does nothing.
	 */
	public static final void process(Path file, Consumer<String> lineProcessor) {
		if (file == null || lineProcessor == null) {
			return;
		}
		
		try {
			process(new FileInputStream(file.toFile()), lineProcessor, null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
