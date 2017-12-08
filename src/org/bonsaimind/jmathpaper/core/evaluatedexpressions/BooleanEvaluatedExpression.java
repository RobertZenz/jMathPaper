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

package org.bonsaimind.jmathpaper.core.evaluatedexpressions;

import java.math.BigDecimal;

/**
 * {@link BooleanEvaluatedExpression} is an extension of
 * {@link NumberEvaluatedExpression} which handles the result as boolean.
 */
public class BooleanEvaluatedExpression extends NumberEvaluatedExpression {
	/** The {@link BigDecimal} value that is used for {@code false}. */
	public static final BigDecimal FALSE = BigDecimal.ZERO;
	
	/** The {@link BigDecimal} value that is used for {@code true}. */
	public static final BigDecimal TRUE = BigDecimal.ONE;
	
	/**
	 * Creates a new instance of {@link BooleanEvaluatedExpression}.
	 *
	 * @param id The ID.
	 * @param expression The expression.
	 * @param result The result.
	 */
	public BooleanEvaluatedExpression(String id, String expression, BigDecimal result) {
		super(id, expression, result);
	}
	
	/**
	 * Creates a new instance of {@link BooleanEvaluatedExpression}.
	 *
	 * @param id The ID.
	 * @param expression The expression.
	 * @param result The result.
	 */
	public BooleanEvaluatedExpression(String id, String expression, boolean result) {
		super(id, expression, asBigDecimal(result));
	}
	
	/**
	 * Creates a new instance of {@link BooleanEvaluatedExpression}.
	 *
	 * @param id The ID.
	 * @param expression The expression.
	 * @param result The result.
	 */
	public BooleanEvaluatedExpression(String id, String expression, Boolean result) {
		super(id, expression, asBigDecimal(result.booleanValue()));
	}
	
	/**
	 * Returns the appropriate value for the given value.
	 * 
	 * @param value The value.
	 * @return Either {@link #TRUE} or {@link #FALSE}.
	 */
	protected static final BigDecimal asBigDecimal(boolean value) {
		if (value) {
			return TRUE;
		} else {
			return FALSE;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String formatResult() {
		if (FALSE.compareTo(result) == 0) {
			return Boolean.FALSE.toString();
		} else {
			return Boolean.TRUE.toString();
		}
	}
}
