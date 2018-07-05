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

package org.bonsaimind.jmathpaper.core;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bonsaimind.jmathpaper.core.evaluatedexpressions.BooleanEvaluatedExpression;
import org.bonsaimind.jmathpaper.core.evaluatedexpressions.FunctionEvaluatedExpression;
import org.bonsaimind.jmathpaper.core.evaluatedexpressions.NumberEvaluatedExpression;
import org.bonsaimind.jmathpaper.core.resources.ResourceLoader;
import org.bonsaimind.jmathpaper.core.units.PrefixedUnit;
import org.bonsaimind.jmathpaper.core.units.UnitConverter;

import com.udojava.evalex.Expression;

public class Evaluator {
	private static final Pattern BINARY_NUMBER = ResourceLoader.compileRegex("binary-number");
	private static final String COMMENT_INLINE_END = "*/";
	private static final String COMMENT_INLINE_START = "/*";
	private static final String COMMENT_START = "//";
	private static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(32, RoundingMode.HALF_UP);
	private static final Pattern FUNCTION = ResourceLoader.compileRegex("function");
	private static final Pattern HEX_NUMBER = ResourceLoader.compileRegex("hex-number");
	private static final Pattern ID = ResourceLoader.compileRegex("id");
	private static final Pattern LAST_REFERENCE = ResourceLoader.compileRegex("last-reference");
	private static final Pattern OCTAL_NUMBER = ResourceLoader.compileRegex("octal-number");
	private static final Pattern UNIT_CONVERSION = ResourceLoader.compileRegex("unit-conversion");
	private Map<String, String> aliases = new HashMap<>();
	private int expressionCounter = 0;
	private String lastVariableAdded = null;
	private MathContext mathContext = DEFAULT_MATH_CONTEXT;
	private List<EvaluatedExpression> previousEvaluatedExpressions = new ArrayList<>();
	private UnitConverter unitConverter = new UnitConverter();
	
	public Evaluator() {
		super();
		
		reset();
	}
	
	public Evaluator(Evaluator evaluator) {
		this();
		
		aliases = evaluator.aliases;
		mathContext = evaluator.mathContext;
		unitConverter = evaluator.unitConverter;
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
	
	public EvaluatedExpression evaluate(String expression) throws InvalidExpressionException {
		String preProcessedExpression = preProcess(expression);
		String processedExpression = stripComments(preProcessedExpression);
		processedExpression = replaceAliases(processedExpression);
		processedExpression = convertNumbers(processedExpression);
		
		Matcher functionMatcher = FUNCTION.matcher(processedExpression);
		
		if (functionMatcher.matches()) {
			String parametersList = functionMatcher.group("PARAMETERS");
			List<String> parameters = null;
			
			if (parametersList != null && !parametersList.trim().isEmpty()) {
				parameters = Arrays.asList(parametersList.split("\\s*,\\s*"));
			}
			
			return addEvaluatedExpression(new FunctionEvaluatedExpression(
					functionMatcher.group("ID"),
					preProcessedExpression,
					parameters,
					functionMatcher.group("EXPRESSION"),
					prepareExpression(functionMatcher.group("EXPRESSION")).isBoolean()));
		}
		
		String id = null;
		
		Matcher idMatcher = ID.matcher(processedExpression);
		
		if (idMatcher.matches()) {
			id = idMatcher.group("ID");
			processedExpression = idMatcher.group("EXPRESSION");
		}
		
		PrefixedUnit unitFrom = null;
		PrefixedUnit unitTo = null;
		
		Matcher unitConversionMatcher = UNIT_CONVERSION.matcher(processedExpression);
		
		if (unitConversionMatcher.matches()) {
			processedExpression = unitConversionMatcher.group("EXPRESSION");
			
			unitFrom = unitConverter.getPrefixedUnit(unitConversionMatcher.group("FROM"));
			
			if (unitFrom == null) {
				throw new InvalidExpressionException("No such unit: " + unitConversionMatcher.group("FROM"));
			}
			
			unitTo = unitConverter.getPrefixedUnit(unitConversionMatcher.group("TO"));
			
			if (unitTo == null) {
				throw new InvalidExpressionException("No such unit: " + unitConversionMatcher.group("TO"));
			}
		}
		
		try {
			Expression mathExpression = prepareExpression(processedExpression);
			
			BigDecimal result = mathExpression.eval();
			
			if (unitFrom != null && unitTo != null) {
				result = unitConverter.convert(unitFrom, unitTo, result, mathContext).stripTrailingZeros();
			}
			
			if (id == null) {
				id = getNextId();
			}
			
			lastVariableAdded = id;
			
			if (mathExpression.isBoolean()) {
				return addEvaluatedExpression(new BooleanEvaluatedExpression(id, preProcessedExpression, result));
			} else {
				return addEvaluatedExpression(new NumberEvaluatedExpression(id, preProcessedExpression, result));
			}
		} catch (Throwable th) {
			throw new InvalidExpressionException(th.getMessage(), th);
		}
	}
	
	public MathContext getMathContext() {
		return mathContext;
	}
	
	public UnitConverter getUnitConverter() {
		return unitConverter;
	}
	
	public void loadAlias(String aliasDefinition) {
		if (aliasDefinition == null || aliasDefinition.isEmpty()) {
			return;
		}
		
		String[] splittedDefinition = aliasDefinition.split("[ \t]+", 2);
		
		if (splittedDefinition.length != 2) {
			return;
		}
		
		registerAlias(splittedDefinition[0], splittedDefinition[1]);
	}
	
	public Expression prepareExpression(String expression) {
		if (expression == null || expression.length() == 0) {
			return new Expression("0");
		}
		
		String processedExpression = expression.replace('#', 'R');
		
		EvaluatorAwareExpression mathExpression = new EvaluatorAwareExpression(
				this,
				processedExpression,
				mathContext);
		
		for (EvaluatedExpression previousEvaluatedExpression : previousEvaluatedExpressions) {
			if (previousEvaluatedExpression instanceof FunctionEvaluatedExpression) {
				FunctionEvaluatedExpression functionEvaluatedExpression = (FunctionEvaluatedExpression)previousEvaluatedExpression;
				
				mathExpression.addFunction(mathExpression.new EvaluatingFunction(
						functionEvaluatedExpression.getId(),
						functionEvaluatedExpression.getParameters(),
						functionEvaluatedExpression.getBody(),
						functionEvaluatedExpression.isBoolean()));
			} else {
				mathExpression.with(
						previousEvaluatedExpression.getId().replace('#', 'R'),
						previousEvaluatedExpression.getResult());
			}
		}
		
		return mathExpression;
	}
	
	public void registerAlias(String alias, String replacement) {
		aliases.put("(^| )" + alias + "( |$)", replacement);
	}
	
	public void reset() {
		expressionCounter = 0;
		previousEvaluatedExpressions.clear();
	}
	
	public void setMathContext(MathContext mathContext) {
		this.mathContext = mathContext;
	}
	
	private EvaluatedExpression addEvaluatedExpression(EvaluatedExpression evaluatedExpression) {
		if (evaluatedExpression != null) {
			previousEvaluatedExpressions.add(evaluatedExpression);
		}
		
		return evaluatedExpression;
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
	
	private String convertNumbers(String expression) {
		expression = applyPattern(expression, BINARY_NUMBER, Evaluator::convertFromBinary);
		expression = applyPattern(expression, OCTAL_NUMBER, Evaluator::convertFromOctal);
		expression = applyPattern(expression, HEX_NUMBER, Evaluator::convertFromHex);
		
		return expression;
	}
	
	private String getNextId() {
		expressionCounter = expressionCounter + 1;
		
		return "#" + Integer.toString(expressionCounter);
	}
	
	private String preProcess(String expression) {
		return applyPattern(expression.trim(), LAST_REFERENCE, this::replaceLastReference);
	}
	
	private String replaceAliases(String expression) {
		for (Entry<String, String> alias : aliases.entrySet()) {
			expression = expression.replaceAll(alias.getKey(), alias.getValue());
		}
		
		return expression;
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
