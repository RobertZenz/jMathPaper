/*
 * Copyright 2019, Robert 'Bobby' Zenz
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

package org.bonsaimind.jmathpaper.core.units;

import java.util.EnumSet;

public enum ConversionKeyword {
	AS, IN, TO;
	
	public static final EnumSet<ConversionKeyword> ALL = EnumSet.allOf(ConversionKeyword.class);
	
	public static final EnumSet<ConversionKeyword> NONE = EnumSet.noneOf(ConversionKeyword.class);
	
	public static ConversionKeyword getKeyword(String value) {
		for (ConversionKeyword keyword : ALL) {
			if (keyword.name().equalsIgnoreCase(value)) {
				return keyword;
			}
		}
		
		return null;
	}
	
	public static boolean isKeyword(String value) {
		return getKeyword(value) != null;
	}
}
