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

package org.bonsaimind.jmathpaper.core.ui;

/**
 * Defines an action being taken on a note.
 */
public enum NoteAction {
	/** Adds a line to the end of the note. */
	ADD("add", "append"),
	
	/** Clears the whole note. */
	CLEAR("clear", "clr", "cls", "reset"),
	
	/** Deletes the line with the given index. */
	DELETE("delete", "del", "remove", "rem"),
	
	/** Inserts a line at the given index. */
	INSERT("insert", "ins");
	
	private String[] aliases = null;
	
	private NoteAction(String... aliases) {
		this.aliases = aliases;
	}
	
	public static NoteAction getNoteAction(String name) {
		if (name == null || name.length() == 0) {
			return null;
		}
		
		for (NoteAction paperPart : values()) {
			for (String alias : paperPart.aliases) {
				if (name.equalsIgnoreCase(alias)) {
					return paperPart;
				}
			}
		}
		
		return null;
	}
}
