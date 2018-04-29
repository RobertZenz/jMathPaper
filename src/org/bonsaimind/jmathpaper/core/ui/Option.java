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

import java.math.RoundingMode;

/**
 * An {@link Option} allows to change the way the {@link Ui} behaves.
 */
public enum Option {
	NUMBER_FORMAT("numberformat", "number-format", "format", "fmt"),
	
	/**
	 * Sets the precision to the given value.
	 * <p>
	 * The value must be an integer, with {@code 0} meaning "endless".
	 */
	PRECISION("precision", "prec", "decimals", "dec"),
	
	/**
	 * Sets the rounding mode to the given value.
	 * <p>
	 * The value must be something that can be converted to a member in
	 * {@link RoundingMode}.
	 */
	ROUNDING("rounding-mode", "rounding");
	
	private String[] aliases = null;
	
	private Option(String... aliases) {
		this.aliases = aliases;
	}
	
	public static Option getOption(String name) {
		if (name == null || name.length() == 0) {
			return null;
		}
		
		for (Option option : values()) {
			for (String alias : option.aliases) {
				if (name.equalsIgnoreCase(alias)) {
					return option;
				}
			}
		}
		
		return null;
	}
}
