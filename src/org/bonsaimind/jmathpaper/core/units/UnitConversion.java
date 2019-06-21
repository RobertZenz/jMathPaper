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

public class UnitConversion {
	protected ConversionKeyword keyword = null;
	protected String keywordString = null;
	protected String sourceString = null;
	protected CompoundUnit sourceUnit = null;
	protected String targetString = null;
	protected CompoundUnit targetUnit = null;
	
	public UnitConversion() {
		super();
	}
	
	public ConversionKeyword getKeyword() {
		return keyword;
	}
	
	public String getKeywordString() {
		return keywordString;
	}
	
	public String getSourceString() {
		return sourceString;
	}
	
	public CompoundUnit getSourceUnit() {
		return sourceUnit;
	}
	
	public String getTargetString() {
		return targetString;
	}
	
	public CompoundUnit getTargetUnit() {
		return targetUnit;
	}
	
	public boolean isValid() {
		return sourceUnit != null && targetUnit != null;
	}
	
	public UnitConversion setKeyword(ConversionKeyword keyword) {
		this.keyword = keyword;
		
		return this;
	}
	
	public UnitConversion setKeywordString(String keywordString) {
		this.keywordString = keywordString;
		
		return this;
	}
	
	public UnitConversion setSourceString(String sourceString) {
		this.sourceString = sourceString;
		
		return this;
	}
	
	public UnitConversion setSourceUnit(CompoundUnit sourceUnit) {
		this.sourceUnit = sourceUnit;
		
		return this;
	}
	
	public UnitConversion setTargetString(String targetString) {
		this.targetString = targetString;
		
		return this;
	}
	
	public UnitConversion setTargetUnit(CompoundUnit targetUnit) {
		this.targetUnit = targetUnit;
		
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		if (sourceUnit != null) {
			stringBuilder.append(sourceUnit);
		} else {
			stringBuilder.append(sourceString);
		}
		
		stringBuilder.append(" ");
		
		if (keyword != null) {
			stringBuilder.append(keyword.toString().toLowerCase());
		} else {
			stringBuilder.append(keywordString);
		}
		
		stringBuilder.append(" ");
		
		if (targetUnit != null) {
			stringBuilder.append(targetUnit);
		} else {
			stringBuilder.append(targetString);
		}
		
		return stringBuilder.toString();
	}
	
}
