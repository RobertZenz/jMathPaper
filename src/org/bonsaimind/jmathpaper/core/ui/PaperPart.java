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
 * Defines the part of a paper.
 */
public enum PaperPart {
	/** The expression of a line/expression. */
	EXPRESSION("expression", "exp"),
	
	/** The ID of a line/expression. */
	ID("id"),
	
	/** The whole line-expression. */
	LINE("line"),
	
	/** The whole paper. */
	PAPER("paper"),
	
	/** The result of a line/expression. */
	RESULT("result", "res");
	
	private String[] aliases = null;
	
	private PaperPart(String... aliases) {
		this.aliases = aliases;
	}
	
	public static PaperPart getPaperPart(String name) {
		if (name == null || name.length() == 0) {
			return null;
		}
		
		for (PaperPart paperPart : values()) {
			for (String alias : paperPart.aliases) {
				if (name.equalsIgnoreCase(alias)) {
					return paperPart;
				}
			}
		}
		
		return null;
	}
}
