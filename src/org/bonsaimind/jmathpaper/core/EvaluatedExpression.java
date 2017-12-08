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
 * {@link EvaluatedExpression} is the interface for defining a container for an
 * evaluated expression, including the expression, the id and the result.
 */
public interface EvaluatedExpression {
	/**
	 * Creates a well defined string representation from this
	 * {@link EvaluatedExpression}.
	 * 
	 * @param idColumnWidth The width of the column for the ID.
	 * @param expressionColumnWidth The width of the column for the expression.
	 * @param resultColumnWidth The width of the column for the result.
	 * @return A well defined string representation.
	 */
	public String format(int idColumnWidth, int expressionColumnWidth, int resultColumnWidth);
	
	/**
	 * Gets the expression.
	 * 
	 * @return The expression.
	 */
	public String getExpression();
	
	/**
	 * Gets the formatted result.
	 * 
	 * @return The formatted result.
	 */
	public String getFormattedResult();
	
	/**
	 * Gets the ID.
	 * 
	 * @return The ID.
	 */
	public String getId();
	
	/**
	 * Gets the result.
	 * 
	 * @return The result.
	 */
	public BigDecimal getResult();
}
