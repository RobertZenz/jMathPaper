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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import org.bonsaimind.jmathpaper.core.resources.ResourceLoader;

public final class Configuration {
	/** The {@link String} with which a comment in a file starts. */
	public static final String COMMENT_START = "#";
	
	private static Path cachedConfigDirectory = null;
	private static final String GLOBAL_PAPER_NAME = "global.jmathpaper";
	private static final String USER_ALIASES_NAME = "user.aliases";
	private static final String USER_CONVERSIONS_NAME = "user.conversions";
	private static final String USER_PREFIXES_NAME = "user.prefixes";
	private static final String USER_UNITS_NAME = "user.units";
	
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
			
			try {
				Files.createDirectories(cachedConfigDirectory);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	public static final Path getUserAliasesFile() {
		return getConfigDirectory().resolve(USER_ALIASES_NAME);
	}
	
	public static final Path getUserConversionsFile() {
		return getConfigDirectory().resolve(USER_CONVERSIONS_NAME);
	}
	
	public static final Path getUserPrefixesFile() {
		return getConfigDirectory().resolve(USER_PREFIXES_NAME);
	}
	
	public static final Path getUserUnitsFile() {
		return getConfigDirectory().resolve(USER_UNITS_NAME);
	}
	
	public static final void init() {
		migrateGlobalPaper();
		copyDefaultConfigFilesIfNeeded();
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
	 * Iterates over each line of the given {@link InputStream}.
	 * <p>
	 * This function will strip empty lines and also comments (see the
	 * {@link #COMMENT_START} {@link String}.
	 * 
	 * @param file The {@link Path file} from which to read.
	 * @param lineProcessor The function to execute for every line.
	 */
	public static final void processConfiguration(Path file, Consumer<String> lineProcessor) {
		try {
			processConfiguration(new FileInputStream(file.toFile()), lineProcessor, null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Copies the given default configuration file, if it does not already
	 * exist.
	 * 
	 * @param configFileName The name of the configuration file.
	 */
	private static final void copyDefaultConfigFileIfNeeded(String configFileName) {
		Path configFile = getConfigDirectory().resolve(configFileName);
		
		if (!Files.exists(configFile)) {
			try (OutputStream outputStream = new FileOutputStream(configFile.toFile())) {
				try (InputStream inputStream = ResourceLoader.class.getResourceAsStream("defaults/" + configFileName)) {
					byte[] buffer = new byte[4096];
					int read = 0;
					
					while ((read = inputStream.read(buffer)) > 0) {
						outputStream.write(buffer, 0, read);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Copies the default configuration files, if they do not exist.
	 */
	private static final void copyDefaultConfigFilesIfNeeded() {
		copyDefaultConfigFileIfNeeded(USER_ALIASES_NAME);
		copyDefaultConfigFileIfNeeded(USER_CONVERSIONS_NAME);
		copyDefaultConfigFileIfNeeded(USER_PREFIXES_NAME);
		copyDefaultConfigFileIfNeeded(USER_UNITS_NAME);
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
