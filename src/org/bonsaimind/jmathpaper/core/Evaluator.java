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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bonsaimind.jmathpaper.core.evaluatedexpressions.BooleanEvaluatedExpression;
import org.bonsaimind.jmathpaper.core.evaluatedexpressions.NumberEvaluatedExpression;

import com.udojava.evalex.Expression;

public class Evaluator {
	private static final Pattern BINARY_NUMBER = Pattern.compile("(^|[^0-9])0b(?<VALUE>[01]+)($|[^.,])");
	private static final String COMMENT_INLINE_END = "*/";
	private static final String COMMENT_INLINE_START = "/*";
	private static final String COMMENT_START = "//";
	private static final Pattern HEX_NUMBER = Pattern.compile("(^|[^0-9])0x(?<VALUE>[0-9a-fA-F]+)($|[^.,])");
	private static final Pattern ID_FINDER = Pattern.compile("^(?<ID>[a-zA-Z_][a-zA-Z_0-9]*)(\\s)*=(?<EXPRESSION>[^=]+.*)$");
	private static final Pattern LAST_REFERENCE = Pattern.compile("(^|[^0-9,.])(?<VALUE>00)($|[^0-9.,])");
	private static final Pattern OCTAL_NUMBER = Pattern.compile("(^|[^0-9])0o(?<VALUE>[0-7]+)($|[^.,])");
	private int expressionCounter = 0;
	private String lastVariableAdded = null;
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
		lastVariableAdded = evaluatedExpression.getId();
	}
	
	public EvaluatedExpression evaluate(String expression) throws InvalidExpressionException {
		String preProcessedExpression = preProcess(expression);
		
		String id = null;
		String processedExpression = stripComments(preProcessedExpression);
		
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
			lastVariableAdded = id;
			
			if (mathExpression.isBoolean()) {
				return new BooleanEvaluatedExpression(id, preProcessedExpression, result);
			} else {
				return new NumberEvaluatedExpression(id, preProcessedExpression, result);
			}
		} catch (Throwable th) {
			throw new InvalidExpressionException(th.getMessage(), th);
		}
	}
	
	public void reset() {
		expressionCounter = 0;
		variables.clear();
	}
	
	public void setExpressionCounter(int counter) {
		expressionCounter = counter;
	}
	
	private String applyPattern(String expression, Pattern pattern, Function<String, String> replacer) {
		StringBuffer buffer = new StringBuffer(expression.length());
		Matcher matcher = pattern.matcher(expression);
		
		while (matcher.find()) {
			matcher.appendReplacement(buffer, "$1" + replacer.apply(matcher.group("VALUE")) + "$3");
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
			return new Expression("0");
		}
		
		expression = expression.replace(" and ", " && ");
		expression = expression.replace(" or ", " || ");
		expression = expression.replace(" equal ", " == ");
		expression = expression.replace(" equals ", " == ");
		expression = expression.replace(" notequal ", " != ");
		expression = expression.replace(" notequals ", " != ");
		expression = expression.replace(" greater ", " > ");
		expression = expression.replace(" greaterequal ", " >= ");
		expression = expression.replace(" greaterequals ", " >= ");
		expression = expression.replace(" less ", " < ");
		expression = expression.replace(" lessequal ", " <= ");
		expression = expression.replace(" lessequals ", " <= ");
		
		expression = applyPattern(expression, BINARY_NUMBER, Evaluator::convertFromBinary);
		expression = applyPattern(expression, OCTAL_NUMBER, Evaluator::convertFromOctal);
		expression = applyPattern(expression, HEX_NUMBER, Evaluator::convertFromHex);
		
		String processedExpression = expression.replace('#', 'R');
		
		Expression mathExpression = new Expression(
				processedExpression,
				MathContext.UNLIMITED);
		
		for (Entry<String, BigDecimal> variable : variables.entrySet()) {
			mathExpression.with(variable.getKey().replace('#', 'R'), variable.getValue());
		}
		
		return mathExpression;
	}
	
	private String preProcess(String expression) {
		return applyPattern(expression.trim(), LAST_REFERENCE, this::replaceLastReference);
	}
	
	private String replaceLastReference(String value) {
		if (lastVariableAdded == null) {
			return "0";
		} else {
			return lastVariableAdded;
		}
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
