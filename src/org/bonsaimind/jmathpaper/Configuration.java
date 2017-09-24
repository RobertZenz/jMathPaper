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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Configuration {
	private Configuration() {
		// No instancing required.
	}
	
	public static final Path getConfigDirectory() {
		Path configDirectory = null;
		
		String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
		
		if (xdgConfigHome == null || xdgConfigHome.trim().length() == 0) {
			String userHome = System.getenv("HOME");
			
			if (userHome == null || userHome.trim().length() == 0) {
				// Wait, what?
				return Paths.get(".");
			}
			
			configDirectory = Paths.get(userHome, ".local");
		}
		
		configDirectory = Paths.get(configDirectory.toString(), "jmathpaper");
		
		return configDirectory;
	}
	
	public static final Path getGlobalPaperFile() {
		Path globalPaperFile = Paths.get(getConfigDirectory().toString(), "global.jmathpaper");
		
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
}
