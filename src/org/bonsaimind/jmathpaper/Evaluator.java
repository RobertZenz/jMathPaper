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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Evaluator {
	private static final Pattern ID_FINDER = Pattern.compile("^(?<ID>[a-zA-Z_]+)=(?<EXPRESSION>.*)$");
	private List<EvaluatedExpression> evaluatedExpressions = new ArrayList<>();
	private Map<String, Double> variables = new HashMap<>();
	
	public Evaluator() {
		super();
		
		addVariable("pi", Math.PI);
		addVariable("PI", Math.PI);
		
		addVariable("e", Math.E);
		addVariable("E", Math.E);
	}
	
	public void addVariable(String name, double value) {
		variables.put(name, Double.valueOf(value));
	}
	
	public EvaluatedExpression evaluate(String expression) {
		EvaluatedExpression evaluatedExpression = null;
		String id = null;
		String processedExpression = expression;
		
		Matcher idFinderMatcher = ID_FINDER.matcher(processedExpression);
		
		if (idFinderMatcher.matches()) {
			id = idFinderMatcher.group("ID");
			processedExpression = idFinderMatcher.group("EXPRESSION");
		} else {
			id = getNextId();
		}
		
		try {
			evaluatedExpression = new EvaluatedExpression(
					id,
					expression,
					evaluateInternal(processedExpression),
					true,
					null);
			
			evaluatedExpressions.add(evaluatedExpression);
		} catch (Throwable th) {
			evaluatedExpression = new EvaluatedExpression(
					id,
					expression,
					0.0d,
					false,
					th.getMessage());
		}
		
		return evaluatedExpression;
		
	}
	
	public void removeVariable(String name) {
		variables.remove(name);
	}
	
	private double evaluateInternal(String expression) {
		String processedExpression = expression.replace('#', 'R');
		
		ExpressionBuilder expressionBuilder = new ExpressionBuilder(processedExpression);
		for (Entry<String, Double> variable : variables.entrySet()) {
			expressionBuilder.variable(variable.getKey());
		}
		for (EvaluatedExpression evaluatedExpression : evaluatedExpressions) {
			expressionBuilder.variable(evaluatedExpression.getId().replace('#', 'R'));
		}
		
		Expression mathExpression = expressionBuilder.build();
		for (Entry<String, Double> variable : variables.entrySet()) {
			mathExpression.setVariable(
					variable.getKey(),
					variable.getValue().doubleValue());
		}
		for (EvaluatedExpression evaluatedExpression : evaluatedExpressions) {
			mathExpression.setVariable(
					evaluatedExpression.getId().replace('#', 'R'),
					evaluatedExpression.getResult());
		}
		
		return mathExpression.evaluate();
	}
	
	private String getNextId() {
		return "#" + Integer.toString(evaluatedExpressions.size() + 1);
	}
}
