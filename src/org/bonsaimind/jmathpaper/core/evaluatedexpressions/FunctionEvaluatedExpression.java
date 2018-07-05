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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * {@link FunctionEvaluatedExpression} is an extension of
 * {@link BooleanEvaluatedExpression} and holds the information about a
 * function.
 */
public class FunctionEvaluatedExpression extends BooleanEvaluatedExpression {
	/** The body of the function. */
	protected String body = null;
	
	/** If this function returns a boolean. */
	protected boolean isBoolean = false;
	
	/** The {@link List} of parameters names. */
	protected List<String> parameters = Collections.emptyList();
	
	/**
	 * Creates a new instance of {@link FunctionEvaluatedExpression}.
	 *
	 * @param id The ID.
	 * @param expression The expression from which this function is created.
	 * @param parameters The names of the parameters.
	 * @param body The body of this function.
	 * @param isBoolean If this function is a boolean function.
	 */
	public FunctionEvaluatedExpression(String id, String expression, List<String> parameters, String body, boolean isBoolean) {
		super(id, expression, Boolean.TRUE);
		
		if (parameters != null) {
			this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters));
		}
		this.body = body;
		this.isBoolean = isBoolean;
	}
	
	/**
	 * Gets the body of this function.
	 * <p>
	 * The body is the same as the expression but without the function
	 * declaration.
	 * 
	 * @return The body of the function.
	 */
	public String getBody() {
		return body;
	}
	
	/**
	 * Gets the names of the parameters, may be empty if there are none.
	 * 
	 * @return The names of the parameters.
	 */
	public List<String> getParameters() {
		return parameters;
	}
	
	/**
	 * If this function returns a boolean.
	 * 
	 * @return {@code true} if this function returns a boolean.
	 */
	public boolean isBoolean() {
		return isBoolean;
	}
}
