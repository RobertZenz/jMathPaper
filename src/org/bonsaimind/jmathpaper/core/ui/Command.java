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

package org.bonsaimind.jmathpaper.core.ui;

/**
 * A {@link Command} can be executed on an {@link Ui}.
 */
public enum Command {
	/** Adds the given alias to the current paper. */
	ALIAS("alias"),
	
	/** The paper should be cleared. */
	CLEAR("clean", "clear"),
	
	/**
	 * The current paper will be closed. Depending on the UI, that might mean
	 * that the application exits.
	 */
	CLOSE("close", ":bdelete", ":bd"),
	
	/** Adds the given conversion to the current paper. */
	CONVERSION("conversion"),
	
	/**
	 * The current paper will be copied to the clipboard in its text
	 * representation.
	 */
	COPY("copy", "cp", "y"),
	
	/** Start a new paper. */
	NEW("new", ":new"),
	
	/** Switches to the next paper, if there is any. */
	NEXT("next", "right", ":bnext", ":bn"),
	
	/** Opens the given paper. */
	OPEN("open", ":e"),
	
	/** Sets the given option to the given value. */
	OPTION("option", "opt", "set", "setoption", "setopt", ":so", ":setopt"),
	
	/** Adds the given prefix to the current paper. */
	PREFIX("prefix"),
	
	/** Switches to the previous paper, if there is any. */
	PREVIOUS("previous", "left", ":bprevious", ":bp"),
	
	/** Quit the application. */
	QUIT("quit", "exit", ":q", ":q!"),
	
	/** Reloads the paper. */
	RELOAD("reload", "reset"),
	
	/** Save the current paper, if a name is given, at that location. */
	SAVE("save", "store", ":w"),
	
	/**
	 * Save the current paper, if a name is given, at that location and then
	 * exit.
	 */
	SAVE_AND_QUIT(":x"),
	
	/** Adds the given unit to the current paper. */
	UNIT("unit");
	
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
