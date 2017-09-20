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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.udojava.evalex.Expression;

public class Evaluator {
	private static final Pattern BINARY_NUMBER = Pattern.compile("(^|[^0-9])0b(?<VALUE>[01]+)($|[^.,])");
	private static final Pattern HEX_NUMBER = Pattern.compile("(^|[^0-9])0x(?<VALUE>[0-9a-fA-F]+)($|[^.,])");
	private static final Pattern ID_FINDER = Pattern.compile("^(?<ID>[a-zA-Z_]+)=(?<EXPRESSION>.*)$");
	private static final Pattern OCTAL_NUMBER = Pattern.compile("(^|[^0-9])0o(?<VALUE>[0-7]+)($|[^.,])");
	private int expressionCounter = 0;
	private Map<String, BigDecimal> variables = new HashMap<>();
	
	public Evaluator() {
		super();
		
		reset();
	}
	
	private static final String convertFromBinary(String value) {
		return Long.toString(Long.parseLong(value, 2));
	}
	
	private static final String convertFromHex(String value) {
		return Long.toString(Long.parseLong(value, 16));
	}
	
	private static final String convertFromOctal(String value) {
		return Long.toString(Long.parseLong(value, 8));
	}
	
	public void addEvaluatedExpression(EvaluatedExpression evaluatedExpression) {
		if (evaluatedExpression.isValid()) {
			variables.put(evaluatedExpression.getId(), evaluatedExpression.getResult());
		}
	}
	
	public EvaluatedExpression evaluate(String expression) {
		EvaluatedExpression evaluatedExpression = null;
		String id = null;
		String processedExpression = expression;
		
		Matcher idFinderMatcher = ID_FINDER.matcher(processedExpression);
		
		if (idFinderMatcher.matches()) {
			id = idFinderMatcher.group("ID");
			processedExpression = idFinderMatcher.group("EXPRESSION");
		}
		
		try {
			if (id == null) {
				id = getNextId();
			}
			
			evaluatedExpression = new EvaluatedExpression(
					id,
					expression,
					evaluateInternal(processedExpression));
			
			variables.put(id, evaluatedExpression.getResult());
		} catch (Throwable th) {
			evaluatedExpression = new EvaluatedExpression(
					"",
					expression,
					th.getMessage());
		}
		
		return evaluatedExpression;
		
	}
	
	public void reset() {
		expressionCounter = 0;
		variables.clear();
		
		variables.put("pi", new BigDecimal(Math.PI));
		variables.put("PI", new BigDecimal(Math.PI));
		
		variables.put("e", new BigDecimal(Math.E));
		variables.put("E", new BigDecimal(Math.E));
	}
	
	public void setExpressionCounter(int counter) {
		expressionCounter = counter;
	}
	
	private String applyPattern(String expression, Pattern pattern, Function<String, String> replacer) {
		StringBuffer buffer = new StringBuffer(expression.length());
		Matcher matcher = pattern.matcher(expression);
		
		while (matcher.find()) {
			matcher.appendReplacement(buffer, replacer.apply(matcher.group("VALUE")));
		}
		
		matcher.appendTail(buffer);
		
		return buffer.toString();
	}
	
	private BigDecimal evaluateInternal(String expression) {
		expression = applyPattern(expression, BINARY_NUMBER, Evaluator::convertFromBinary);
		expression = applyPattern(expression, OCTAL_NUMBER, Evaluator::convertFromOctal);
		expression = applyPattern(expression, HEX_NUMBER, Evaluator::convertFromHex);
		
		String processedExpression = expression.replace('#', 'R');
		
		Expression mathExpression = new Expression(processedExpression);
		
		for (Entry<String, BigDecimal> variable : variables.entrySet()) {
			mathExpression.with(variable.getKey().replace('#', 'R'), variable.getValue());
		}
		
		return mathExpression.eval();
	}
	
	private String getNextId() {
		expressionCounter = expressionCounter + 1;
		
		return "#" + Integer.toString(expressionCounter);
	}
}
