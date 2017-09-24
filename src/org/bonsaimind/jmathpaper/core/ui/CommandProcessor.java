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

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import org.bonsaimind.jmathpaper.core.CommandException;

public final class CommandProcessor {
	private CommandProcessor() {
		// No instancing needed.
	}
	
	public static final boolean applyCommand(Ui ui, String input) throws CommandException {
		if (input == null || input.length() == 0) {
			return false;
		}
		
		String trimmedInput = input.trim();
		String name = trimmedInput;
		String parameter = null;
		
		int spaceIndex = input.indexOf(' ');
		if (spaceIndex >= 0) {
			name = trimmedInput.substring(0, spaceIndex);
			parameter = trimmedInput.substring(spaceIndex + 1);
		}
		
		Command command = Command.getCommand(name);
		
		if (command != null) {
			try {
				switch (command) {
					case CLEAR:
						ui.clear();
						break;
					
					case CLOSE:
						ui.close();
						break;
					
					case LOAD:
						ui.load(Paths.get(parameter));
						break;
					
					case NEW:
						ui.new_();
						break;
					
					case OPEN:
						ui.open(Paths.get(parameter));
						break;
					
					case QUIT:
						ui.quit();
						break;
					
					case RELOAD:
						ui.reload();
						break;
					
					case SAVE:
						ui.save();
						break;
					
					case SAVE_AND_QUIT:
						ui.save();
						ui.quit();
						break;
					
				}
			} catch (InvalidPathException | IOException e) {
				throw new CommandException("Could not execute command " + command.name() + ".", e);
			}
			
			return true;
		}
		
		return false;
	}
}
