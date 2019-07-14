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

package org.bonsaimind.jmathpaper.core.configuration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bonsaimind.jmathpaper.core.resources.ResourceLoader;

/**
 * The {@link Configuration} is a static utility class which allows to access
 * the configuration which is stored on the filesystem.
 */
public final class Configuration {
	private static Path cachedConfigDirectory = null;
	private static final String DIRECTORY_NAME = "jmathpaper";
	private static final String GLOBAL_PAPER_NAME = "global.jmathpaper";
	private static final String USER_ALIASES_NAME = "user.aliases";
	private static final String USER_CONTEXT_EXPRESSIONS_NAME = "user.context";
	private static final String USER_CONVERSIONS_NAME = "user.conversions";
	private static final String USER_PAPER_TEMPLATE_NAME = "template.jmathpaper";
	private static final String USER_PREFIXES_NAME = "user.prefixes";
	private static final String USER_UNITS_NAME = "user.units";
	
	private Configuration() {
		// No instancing required.
	}
	
	/**
	 * Gets the {@link Path configuration directory} in which all the
	 * configuration resides.
	 * 
	 * @return The {@link Path configuration directory}.
	 */
	public static final Path getConfigDirectory() {
		if (cachedConfigDirectory == null) {
			String xdgDataHome = System.getenv("XDG_DATA_HOME");
			
			if (xdgDataHome != null && !xdgDataHome.trim().isEmpty()) {
				cachedConfigDirectory = Paths.get(xdgDataHome);
			} else {
				String userHome = System.getenv("HOME");
				
				if (userHome != null && !userHome.trim().isEmpty()) {
					cachedConfigDirectory = Paths.get(userHome, ".local", "share");
				} else {
					// Wait, what?
					cachedConfigDirectory = Paths.get("");
				}
			}
			
			cachedConfigDirectory = cachedConfigDirectory.resolve(DIRECTORY_NAME);
			cachedConfigDirectory = cachedConfigDirectory.normalize().toAbsolutePath();
		}
		
		return cachedConfigDirectory;
	}
	
	/**
	 * Gets the {@link Path} for the global paper.
	 * 
	 * @return The {@link Path} for the global paper.
	 */
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
	
	public static final Path getUserContextExpressionsFile() {
		return getConfigDirectory().resolve(USER_CONTEXT_EXPRESSIONS_NAME);
	}
	
	public static final Path getUserConversionsFile() {
		return getConfigDirectory().resolve(USER_CONVERSIONS_NAME);
	}
	
	public static final Path getUserPaperTemplateFile() {
		return getConfigDirectory().resolve(USER_PAPER_TEMPLATE_NAME);
	}
	
	public static final Path getUserPrefixesFile() {
		return getConfigDirectory().resolve(USER_PREFIXES_NAME);
	}
	
	public static final Path getUserUnitsFile() {
		return getConfigDirectory().resolve(USER_UNITS_NAME);
	}
	
	/**
	 * Initializes the configuration.
	 */
	public static final void init() {
		migrateGlobalPaper();
		migrateFromConfigToData();
		createConfigDirectoryIfNeeded();
		copyDefaultConfigFilesIfNeeded();
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
		copyDefaultConfigFileIfNeeded(USER_CONTEXT_EXPRESSIONS_NAME);
		copyDefaultConfigFileIfNeeded(USER_CONVERSIONS_NAME);
		copyDefaultConfigFileIfNeeded(USER_PAPER_TEMPLATE_NAME);
		copyDefaultConfigFileIfNeeded(USER_PREFIXES_NAME);
		copyDefaultConfigFileIfNeeded(USER_UNITS_NAME);
	}
	
	/**
	 * Creates the configuration directory if it does not exist.
	 */
	private static final void createConfigDirectoryIfNeeded() {
		try {
			if (!Files.exists(getConfigDirectory())) {
				Files.createDirectories(getConfigDirectory());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Migrates the configuration from XDG_CONFIG_HOME (1.2.* and below) to the
	 * correct location.
	 */
	private static final void migrateFromConfigToData() {
		String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
		
		if (xdgConfigHome != null && xdgConfigHome.trim().length() > 0) {
			Path oldConfigDirectory = Paths.get(xdgConfigHome, DIRECTORY_NAME);
			
			try {
				if (Files.isDirectory(oldConfigDirectory)) {
					Path configDirectory = getConfigDirectory();
					
					if (!Files.exists(configDirectory)) {
						Files.move(oldConfigDirectory, configDirectory);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
