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
import java.math.MathContext;
import java.util.List;

import com.udojava.evalex.Expression;

/**
 * {@link EvaluatorAwareExpression} is an {@link Expression} extension which
 * knows what {@link Evaluator} it comes from.
 */
class EvaluatorAwareExpression extends Expression {
	/** The {@link Evaluator}. */
	private Evaluator evaluator = null;
	
	/**
	 * Creates a new instance of {@link EvaluatorAwareExpression}.
	 *
	 * @param evaluator The {@link Evaluator}.
	 * @param expression The expression.
	 */
	public EvaluatorAwareExpression(Evaluator evaluator, String expression) {
		super(expression);
		
		this.evaluator = evaluator;
	}
	
	/**
	 * Creates a new instance of {@link EvaluatorAwareExpression}.
	 *
	 * @param evaluator The {@link Evaluator}.
	 * @param expression The expression.
	 * @param defaultMathContext The {@link MathContext} to use.
	 */
	public EvaluatorAwareExpression(Evaluator evaluator, String expression, MathContext defaultMathContext) {
		super(expression, defaultMathContext);
		
		this.evaluator = evaluator;
	}
	
	/**
	 * {@link EvaluatingFunction} is a
	 * {@link com.udojava.evalex.Expression.Function} implementation which
	 * accepts an operation as {@link String} and evaluates it with the current
	 * {@link #evaluator}.
	 */
	public class EvaluatingFunction extends Function {
		/** The body of the function. */
		private String body = null;
		
		/** The names of the parameters. */
		private List<String> parameterNames = null;
		
		/**
		 * Creates a new instance of {@link EvaluatingFunction}.
		 *
		 * @param name The name.
		 * @param parameterNames The parameter names.
		 * @param body The body.
		 * @param isBoolean If this function returns a boolean.
		 */
		public EvaluatingFunction(String name, List<String> parameterNames, String body, boolean isBoolean) {
			super(name, parameterNames.size(), isBoolean);
			
			this.parameterNames = parameterNames;
			this.body = body;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public BigDecimal eval(List<BigDecimal> parameters) {
			Expression mathExpression = evaluator.prepareExpression(body);
			
			for (int index = 0; index < parameters.size(); index++) {
				mathExpression.with(
						parameterNames.get(index),
						parameters.get(index));
			}
			
			return mathExpression.eval();
		}
	}
}
