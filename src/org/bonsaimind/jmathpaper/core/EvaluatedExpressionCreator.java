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

package org.bonsaimind.jmathpaper.core;

import java.math.BigDecimal;

import org.bonsaimind.jmathpaper.core.evaluatedexpressions.BooleanEvaluatedExpression;
import org.bonsaimind.jmathpaper.core.evaluatedexpressions.NumberEvaluatedExpression;

/**
 * {@link EvaluatedExpressionCreator} is a static helper utility to create an
 * {@link EvaluatedExpression} from a {@link String line}.
 */
public final class EvaluatedExpressionCreator {
	/**
	 * No instancing.
	 */
	private EvaluatedExpressionCreator() {
		// No instance required.
	}
	
	/**
	 * Creates the appropriate {@link EvaluatedExpression} for the given
	 * {@link String line} or returns {@code null}.
	 * 
	 * @param line The {@link String line}.
	 * @return The appropriate {@link EvaluatedExpression} for the given
	 *         {@link String line} or {@code null}.
	 */
	public static final EvaluatedExpression create(String line) {
		if (line == null) {
			return null;
		}
		
		String trimmedString = line.trim();
		
		int firstSeparatorIndex = trimmedString.indexOf(" ");
		if (firstSeparatorIndex < 0) {
			return null;
		}
		
		int lastSeparatorIndex = trimmedString.lastIndexOf("=");
		if (lastSeparatorIndex < 0) {
			return null;
		}
		
		String id = trimmedString.substring(0, firstSeparatorIndex).trim();
		String expression = trimmedString.substring(firstSeparatorIndex + 1, lastSeparatorIndex).trim();
		String result = trimmedString.substring(lastSeparatorIndex + 1).trim();
		
		if (result.equals("true") || result.equals("false")) {
			return new BooleanEvaluatedExpression(
					id,
					expression,
					Boolean.parseBoolean(result));
		} else {
			try {
				return new NumberEvaluatedExpression(
						id,
						expression,
						new BigDecimal(result).stripTrailingZeros());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				
				return null;
			}
		}
	}
}
