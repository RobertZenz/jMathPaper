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

/**
 * The {@link EvaluatedExpression} is a simple immutable container for an
 * expression which has been evaluated.
 */
public class EvaluatedExpression {
	protected String errorMessage = null;
	protected String expression = null;
	protected String id = null;
	protected BigDecimal result = BigDecimal.ZERO;
	protected boolean valid = true;
	
	/**
	 * Creates a new, valid, instance of {@link EvaluatedExpression}.
	 *
	 * @param id The ID.
	 * @param expression The expression.
	 * @param result The result
	 */
	public EvaluatedExpression(String id, String expression, BigDecimal result) {
		this(id, expression, result, true, null);
	}
	
	/**
	 * Creates a new, invalid, instance of {@link EvaluatedExpression}.
	 *
	 * @param id The ID.
	 * @param expression The expression.
	 * @param errorMessage The error message.
	 */
	public EvaluatedExpression(String id, String expression, String errorMessage) {
		this(id, expression, BigDecimal.ZERO, false, errorMessage);
	}
	
	/**
	 * Creates a new instance of {@link EvaluatedExpression}.
	 * <p>
	 * Required for the {@link #fromString(String)} method.
	 */
	protected EvaluatedExpression() {
		super();
	}
	
	/**
	 * Creates a new instance of {@link EvaluatedExpression}.
	 *
	 * @param id The ID.
	 * @param expression The expression.
	 * @param result The result. Use {@link BigDecimal#ZERO} if the expression
	 *        could not be validated.
	 * @param valid If this expression is valid/could be evaluated.
	 * @param errorMessage The error message, can be {@code null} if there is
	 *        none.
	 */
	protected EvaluatedExpression(
			String id,
			String expression,
			BigDecimal result,
			boolean valid,
			String errorMessage) {
		this();
		
		this.id = id;
		this.expression = expression;
		this.result = result;
		this.valid = valid;
		this.errorMessage = errorMessage;
	}
	
	/**
	 * Creates a new {@link EvaluatedExpression} from the given string
	 * representation.
	 * <p>
	 * If the given string representation can not be processed {@code null} is
	 * returned.
	 * 
	 * @param string The string representation.
	 * @return The created {@link EvaluatedExpression} created from the given
	 *         string representation. {@code null} if the given string
	 *         representation could not be processed.
	 */
	public static EvaluatedExpression fromString(String string) {
		if (string == null) {
			return null;
		}
		
		String[] splitted = string.split("[\t ]+");
		
		if (splitted.length < 3) {
			return null;
		}
		
		EvaluatedExpression evaluatedExpression = new EvaluatedExpression();
		evaluatedExpression.id = splitted[0];
		evaluatedExpression.expression = splitted[1];
		
		try {
			evaluatedExpression.result = new BigDecimal(splitted[2]);
			evaluatedExpression.valid = true;
		} catch (NumberFormatException e) {
			evaluatedExpression.errorMessage = splitted[2];
			evaluatedExpression.valid = false;
		}
		
		return evaluatedExpression;
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
		EvaluatedExpression other = (EvaluatedExpression)obj;
		if (errorMessage == null) {
			if (other.errorMessage != null) {
				return false;
			}
		} else if (!errorMessage.equals(other.errorMessage)) {
			return false;
		}
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
		if (valid != other.valid) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the error message, {@code null} if there is none.
	 * 
	 * @return The error message, {@code null} if there is none.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	
	/**
	 * Gets the expression.
	 * 
	 * @return The expression.
	 */
	public String getExpression() {
		return expression;
	}
	
	/**
	 * The ID.
	 * 
	 * @return The ID.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * The {@link BigDecimal result}.
	 * 
	 * @return The {@link BigDecimal result}.
	 */
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
		result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + (valid ? 1231 : 1237);
		return result;
	}
	
	/**
	 * Gets if this {@link EvaluatedExpression} is valid.
	 * <p>
	 * A valid expression could be evaluated and has a result.
	 * 
	 * @return {@code true} if this {@link EvaluatedExpression} is valid.
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Creates a well defined string representation from this
	 * {@link EvaluatedExpression}.
	 * 
	 * @return A well defined string representation.
	 */
	@Override
	public String toString() {
		if (valid) {
			return id + "\t"
					+ expression + "\t"
					+ result.toPlainString();
		} else {
			return id + "\t"
					+ expression + "\t"
					+ errorMessage;
		}
	}
}
