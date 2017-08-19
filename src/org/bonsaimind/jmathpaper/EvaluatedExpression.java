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

package org.bonsaimind.jmathpaper;

import java.math.BigDecimal;

public class EvaluatedExpression {
	private String errorMessage = null;
	private String expression = null;
	private String id = null;
	private BigDecimal result = BigDecimal.ZERO;
	private boolean valid = true;
	
	public EvaluatedExpression(
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
	
	private EvaluatedExpression() {
		super();
	}
	
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
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public String getExpression() {
		return expression;
	}
	
	public String getId() {
		return id;
	}
	
	public BigDecimal getResult() {
		return result;
	}
	
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
	
	public boolean isValid() {
		return valid;
	}
	
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
