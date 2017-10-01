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
	private static final String COMMENT_INLINE_END = "*/";
	private static final String COMMENT_INLINE_START = "/*";
	private static final String COMMENT_START = "//";
	private static final Pattern HEX_NUMBER = Pattern.compile("(^|[^0-9])0x(?<VALUE>[0-9a-fA-F]+)($|[^.,])");
	private static final Pattern ID_FINDER = Pattern.compile("^(?<ID>[a-zA-Z_]+)=(?<EXPRESSION>[^=]+.*)$");
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
		variables.put(evaluatedExpression.getId(), evaluatedExpression.getResult());
	}
	
	public EvaluatedExpression evaluate(String expression) throws InvalidExpressionException {
		String id = null;
		String processedExpression = stripComments(expression);
		
		Matcher idFinderMatcher = ID_FINDER.matcher(processedExpression);
		
		if (idFinderMatcher.matches()) {
			id = idFinderMatcher.group("ID");
			processedExpression = idFinderMatcher.group("EXPRESSION");
		}
		
		try {
			Expression mathExpression = prepareExpression(processedExpression);
			
			BigDecimal result = mathExpression.eval();
			
			if (id == null) {
				id = getNextId();
			}
			
			variables.put(id, result);
			
			return new EvaluatedExpression(id, expression, result, mathExpression.isBoolean());
		} catch (Throwable th) {
			throw new InvalidExpressionException(th.getMessage(), th);
		}
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
	
	private String getNextId() {
		expressionCounter = expressionCounter + 1;
		
		return "#" + Integer.toString(expressionCounter);
	}
	
	private Expression prepareExpression(String expression) {
		if (expression == null || expression.length() == 0) {
			return null;
		}
		
		expression = expression.replaceAll("==", "=");
		
		expression = applyPattern(expression, BINARY_NUMBER, Evaluator::convertFromBinary);
		expression = applyPattern(expression, OCTAL_NUMBER, Evaluator::convertFromOctal);
		expression = applyPattern(expression, HEX_NUMBER, Evaluator::convertFromHex);
		
		String processedExpression = expression.replace('#', 'R');
		
		Expression mathExpression = new Expression(processedExpression);
		
		for (Entry<String, BigDecimal> variable : variables.entrySet()) {
			mathExpression.with(variable.getKey().replace('#', 'R'), variable.getValue());
		}
		
		return mathExpression;
	}
	
	private String stripComments(String expression) {
		String strippedExpression = expression;
		
		int commentStartIndex = strippedExpression.indexOf(COMMENT_INLINE_START);
		
		// I'm aware that this is not the most efficient way to do it,
		// but this is the easiest option and we are dealing with strings with
		// an estimated length of less then 12 characters...so it's okay.
		while (commentStartIndex >= 0) {
			int commentEndIndex = strippedExpression.indexOf(
					COMMENT_INLINE_END,
					commentStartIndex + COMMENT_INLINE_START.length());
			
			if (commentEndIndex >= 0) {
				String start = strippedExpression.substring(0, commentStartIndex);
				String end = strippedExpression.substring(commentEndIndex + COMMENT_INLINE_END.length());
				
				strippedExpression = start + end;
			} else {
				// Seems like the expression is malformed, better chicken out.
				return strippedExpression;
			}
			
			commentStartIndex = strippedExpression.indexOf(commentStartIndex);
		}
		
		commentStartIndex = strippedExpression.indexOf(COMMENT_START);
		
		if (commentStartIndex >= 0) {
			strippedExpression = strippedExpression.substring(0, commentStartIndex);
		}
		
		return strippedExpression;
	}
}
