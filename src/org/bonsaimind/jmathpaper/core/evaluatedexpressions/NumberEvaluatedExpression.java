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
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.bonsaimind.jmathpaper.core.EvaluatedExpression;

/**
 * {@link NumberEvaluatedExpression} is a simple {@link EvaluatedExpression}
 * implementation.
 * <p>
 * It implements the complete interface in an easily extensible way.
 */
public class NumberEvaluatedExpression implements EvaluatedExpression {
	/** The expression. */
	protected String expression = null;
	
	/** The ID. */
	protected String id = null;
	
	/** The result. */
	protected BigDecimal result = null;
	
	/**
	 * Creates a new instance of {@link NumberEvaluatedExpression}.
	 *
	 * @param id The ID.
	 * @param expression The expression.
	 * @param result The result.
	 */
	public NumberEvaluatedExpression(String id, String expression, BigDecimal result) {
		super();
		
		this.id = id;
		this.expression = expression;
		this.result = result;
	}
	
	/**
	 * A small helper function to add a padded {@link String} to a
	 * {@link StringBuilder} .
	 * 
	 * @param builder The {@link StringBuilder} to append to.
	 * @param value The {@link String} to append.
	 * @param padLeft The amount of padding on the left.
	 * @param padRight The amount of padding on the right.
	 */
	protected static final void appendPadded(StringBuilder builder, String value, int padLeft, int padRight) {
		for (int counter = 0; counter < padLeft - value.length(); counter++) {
			builder.append(" ");
		}
		
		builder.append(value);
		
		for (int counter = 0; counter < padRight - value.length(); counter++) {
			builder.append(" ");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		NumberEvaluatedExpression other = (NumberEvaluatedExpression)obj;
		if (expression == null) {
			if (other.expression != null) {
				return false;
			}
		} else if (!expression.equals(other.expression)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (result == null) {
			if (other.result != null) {
				return false;
			}
		} else if (!result.equals(other.result)) {
			return false;
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String format(int idColumnWidth, int expressionColumnWidth, int resultColumnWidth, NumberFormat numberFormat) {
		StringBuilder builder = new StringBuilder();
		
		appendPadded(builder, id, 0, idColumnWidth);
		builder.append(" ");
		appendPadded(builder, expression, expressionColumnWidth, 0);
		builder.append(" = ");
		
		appendPadded(builder, getFormattedResult(numberFormat), resultColumnWidth, 0);
		
		return builder.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getExpression() {
		return expression;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFormattedResult(NumberFormat numberFormat) {
		return numberFormat.format(result);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigDecimal getResult() {
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		return result;
	}
	
	/**
	 * Creates a well defined string representation from this
	 * {@link EvaluatedExpression}.
	 * 
	 * @return A well defined string representation.
	 */
	@Override
	public String toString() {
		return format(0, 0, 0, DecimalFormat.getNumberInstance());
	}
}
