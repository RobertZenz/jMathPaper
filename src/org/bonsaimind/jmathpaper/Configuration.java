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

package org.bonsaimind.jmathpaper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public final class Configuration {
	/** The {@link String} with which a comment in a file starts. */
	public static final String COMMENT_START = "#";
	
	private static Path cachedConfigDirectory = null;
	private static final String GLOBAL_PAPER_NAME = "global.jmathpaper";
	
	private Configuration() {
		// No instancing required.
	}
	
	public static final Path getConfigDirectory() {
		if (cachedConfigDirectory == null) {
			String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
			
			if (xdgConfigHome == null || xdgConfigHome.trim().length() == 0) {
				String userHome = System.getenv("HOME");
				
				if (userHome == null || userHome.trim().length() == 0) {
					// Wait, what?
					return Paths.get(".");
				}
				
				cachedConfigDirectory = Paths.get(userHome, ".local", "share");
			}
			
			cachedConfigDirectory = cachedConfigDirectory.resolve("jmathpaper");
			cachedConfigDirectory = cachedConfigDirectory.normalize().toAbsolutePath();
		}
		
		return cachedConfigDirectory;
	}
	
	public static final Path getGlobalPaperFile() {
		Path globalPaperFile = getConfigDirectory().resolve(GLOBAL_PAPER_NAME);
		
		if (!Files.exists(globalPaperFile)) {
			try {
				Files.createDirectories(getConfigDirectory());
				Files.createFile(globalPaperFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return globalPaperFile;
	}
	
	public static final void init() {
		migrateGlobalPaper();
	}
	
	/**
	 * Iterates over each line of the given {@link InputStream}.
	 * <p>
	 * This function will strip empty lines and also comments (see the
	 * {@link #COMMENT_START} {@link String}.
	 * 
	 * @param inputStream The {@link InputStream} from which to read. Will be
	 *        {@link InputStream#close() closed} when done.
	 * @param lineProcessor The function to execute for every line.
	 * @param lineEnding The line ending to append to each line, can be
	 *        {@code null} for nothing.
	 */
	public static final void processConfiguration(InputStream inputStream, Consumer<String> lineProcessor, String lineEnding) {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			
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
	
	/**
	 * Migrates the global paper from its old location (1.1) in
	 * {@code $HOME/.local/jmathpaper} to its new home (1.2+) in
	 * {@code $HOME/.local/share/jmathpaper}.
	 */
	private static final void migrateGlobalPaper() {
		Path configDirectory = getConfigDirectory();
		
		if (configDirectory.getNameCount() >= 3
				&& configDirectory.getName(configDirectory.getNameCount() - 2).toString().equals("share")
				&& configDirectory.getName(configDirectory.getNameCount() - 3).toString().equals(".local")) {
			Path oldLocation = configDirectory.getParent().getParent();
			oldLocation = oldLocation.resolve("jmathpaper").resolve(GLOBAL_PAPER_NAME);
			
			if (Files.isRegularFile(oldLocation)) {
				Path newLocation = configDirectory.resolve(GLOBAL_PAPER_NAME);
				
				try {
					if (!Files.exists(newLocation)) {
						Files.createDirectories(newLocation.getParent());
						Files.copy(oldLocation, newLocation);
					}
					
					Files.deleteIfExists(oldLocation);
					Files.deleteIfExists(oldLocation.getParent());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
