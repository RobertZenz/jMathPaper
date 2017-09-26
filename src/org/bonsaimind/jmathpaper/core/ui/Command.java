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

package org.bonsaimind.jmathpaper.core.ui;

public enum Command {
	/** The paper should be cleared. */
	CLEAR("clean", "clear"),
	
	/**
	 * The current paper will be closed. Depending on the UI, that might mean
	 * that the application exits.
	 */
	CLOSE("close", ":bd", ":bdelete"),
	
	/** Loads the content of the given paper into the current one. */
	LOAD("load"),
	
	/** Start a new paper. */
	NEW("new"),
	
	/** Opens the given paper. */
	OPEN("open", ":e"),
	
	/** Quit the application. */
	QUIT("exit", "quit", ":q", ":q!"),
	
	/** Reloads the paper. */
	RELOAD("reload", "reset"),
	
	/** Save the current paper, if a name is given, at that location. */
	SAVE("save", "store", ":w"),
	
	/**
	 * Save the current paper, if a name is given, at that location and then
	 * exit.
	 */
	SAVE_AND_QUIT(":x");
	
	private String[] aliases = null;
	
	private Command(String... aliases) {
		this.aliases = aliases;
	}
	
	public static Command getCommand(String name) {
		if (name == null || name.length() == 0) {
			return null;
		}
		
		for (Command command : values()) {
			for (String alias : command.aliases) {
				if (name.equalsIgnoreCase(alias)) {
					return command;
				}
			}
		}
		
		return null;
	}
}
