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

package org.bonsaimind.jmathpaper.core.evaluatedexpressions;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * {@link BooleanEvaluatedExpression} is an extension of
 * {@link NumberEvaluatedExpression} which handles the result as boolean.
 */
public class BooleanEvaluatedExpression extends NumberEvaluatedExpression {
	/** The {@link BigDecimal} value that is used for {@code false}. */
	public static final BigDecimal FALSE = BigDecimal.ZERO;
	
	/** The {@link BigDecimal} value that is used for {@code true}. */
	public static final BigDecimal TRUE = BigDecimal.ONE;
	
	/** The result as {@link Boolean}. */
	protected Boolean booleanResult = Boolean.FALSE;
	
	/**
	 * Creates a new instance of {@link BooleanEvaluatedExpression}.
	 *
	 * @param id The ID.
	 * @param expression The expression.
	 * @param result The result.
	 */
	public BooleanEvaluatedExpression(String id, String expression, BigDecimal result) {
		super(id, expression, result);
		
		booleanResult = asBoolean(result);
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
		
		booleanResult = Boolean.valueOf(result);
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
		
		booleanResult = result;
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
	 * Returns the appropriate value for the given value.
	 * 
	 * @param value The value.
	 * @return Either {@link Boolean#TRUE} or {@link Boolean#FALSE}.
	 */
	protected static final Boolean asBoolean(BigDecimal value) {
		if (FALSE.compareTo(value) == 0) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}
	
	/**
	 * Gets the result as {@link Boolean}.
	 * 
	 * @return The result as {@link Boolean}.
	 */
	public Boolean getBooleanResult() {
		return booleanResult;
	}
	
	@Override
	public String getFormattedResult(NumberFormat numberFormat) {
		return asBoolean(result).toString();
	}
}
