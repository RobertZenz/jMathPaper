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
		super();
		
		this.id = id;
		this.expression = expression;
		this.result = result;
		this.valid = valid;
		this.errorMessage = errorMessage;
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
	
	public boolean isValid() {
		return valid;
	}
}
