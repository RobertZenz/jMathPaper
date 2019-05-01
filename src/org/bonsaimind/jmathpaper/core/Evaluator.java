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
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bonsaimind.jmathpaper.core.evaluatedexpressions.BooleanEvaluatedExpression;
import org.bonsaimind.jmathpaper.core.evaluatedexpressions.FunctionEvaluatedExpression;
import org.bonsaimind.jmathpaper.core.evaluatedexpressions.NumberEvaluatedExpression;
import org.bonsaimind.jmathpaper.core.resources.ResourceLoader;
import org.bonsaimind.jmathpaper.core.units.CompoundUnit;
import org.bonsaimind.jmathpaper.core.units.CompoundUnit.Token;
import org.bonsaimind.jmathpaper.core.units.CompoundUnit.TokenType;
import org.bonsaimind.jmathpaper.core.units.UnitConverter;

import com.udojava.evalex.Expression;

public class Evaluator {
	private static final Pattern BINARY_NUMBER = ResourceLoader.compileRegex("binary-number");
	private static final String COMMENT_INLINE_END = "*/";
	private static final String COMMENT_INLINE_START = "/*";
	private static final String COMMENT_START = "//";
	private static final MathContext DEFAULT_CALCULATION_MATH_CONTEXT = new MathContext(64, RoundingMode.HALF_UP);
	private static final MathContext DEFAULT_RESULT_MATH_CONTEXT = new MathContext(32, RoundingMode.HALF_UP);
	private static final Pattern EXPRESSION_UNIT_SEPARATOR = ResourceLoader.compileRegex("expression-unit-separator");
	private static final Pattern FUNCTION = ResourceLoader.compileRegex("function");
	private static final Pattern HEX_NUMBER = ResourceLoader.compileRegex("hex-number");
	private static final Pattern ID = ResourceLoader.compileRegex("id");
	private static final Pattern LAST_REFERENCE = ResourceLoader.compileRegex("last-reference");
	private static final Pattern OCTAL_NUMBER = ResourceLoader.compileRegex("octal-number");
	private Map<String, String> aliases = new HashMap<>();
	private MathContext calculationMathContext = DEFAULT_CALCULATION_MATH_CONTEXT;
	private List<EvaluatedExpression> contextExpressions = new ArrayList<>();
	private int expressionCounter = 0;
	private List<EvaluatedExpression> previousEvaluatedExpressions = new ArrayList<>();
	private MathContext resultMathContext = DEFAULT_RESULT_MATH_CONTEXT;
	private UnitConverter unitConverter = new UnitConverter();
	
	public Evaluator() {
		super();
		
		reset();
	}
	
	public Evaluator(Evaluator evaluator) {
		this();
		
		aliases = evaluator.aliases;
		calculationMathContext = evaluator.calculationMathContext;
		resultMathContext = evaluator.resultMathContext;
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
		return addEvaluatedExpression(evaluateInternal(expression, this::getNextId));
	}
	
	public MathContext getCalculationMathContext() {
		return calculationMathContext;
	}
	
	public MathContext getResultMathContext() {
		return resultMathContext;
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
	
	public void loadContextExpression(String expression) {
		try {
			contextExpressions.add(evaluateInternal(expression, null));
		} catch (InvalidExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Expression prepareExpression(String expression) {
		if (expression == null || expression.length() == 0) {
			return new Expression("0");
		}
		
		String processedExpression = expression.replace('#', 'R');
		
		EvaluatorAwareExpression mathExpression = new EvaluatorAwareExpression(
				this,
				processedExpression,
				calculationMathContext);
		
		for (EvaluatedExpression contextExpression : contextExpressions) {
			applyEvaluatedExpression(mathExpression, contextExpression);
		}
		
		for (EvaluatedExpression previousEvaluatedExpression : previousEvaluatedExpressions) {
			applyEvaluatedExpression(mathExpression, previousEvaluatedExpression);
		}
		
		return mathExpression;
	}
	
	public EvaluatedExpression preview(String expression) throws InvalidExpressionException {
		return evaluateInternal(expression, null);
	}
	
	public void registerAlias(String alias, String replacement) {
		aliases.put("(^| )" + alias + "( |$)", replacement);
	}
	
	public void reset() {
		expressionCounter = 0;
		previousEvaluatedExpressions.clear();
	}
	
	public void setCalculationMathContext(MathContext calculationMathContext) {
		this.calculationMathContext = calculationMathContext;
	}
	
	public void setResultMathContext(MathContext resultMathContext) {
		this.resultMathContext = resultMathContext;
	}
	
	protected void applyEvaluatedExpression(EvaluatorAwareExpression mathExpression, EvaluatedExpression evaluatedExpression) {
		if (evaluatedExpression instanceof FunctionEvaluatedExpression) {
			FunctionEvaluatedExpression functionEvaluatedExpression = (FunctionEvaluatedExpression)evaluatedExpression;
			
			mathExpression.addFunction(mathExpression.new EvaluatingFunction(
					functionEvaluatedExpression.getId(),
					functionEvaluatedExpression.getParameters(),
					functionEvaluatedExpression.getBody(),
					functionEvaluatedExpression.isBoolean()));
		} else {
			mathExpression.with(
					evaluatedExpression.getId().replace('#', 'R'),
					evaluatedExpression.getResult());
		}
	}
	
	protected EvaluatedExpression evaluateInternal(String expression, Supplier<String> idSupplier) throws InvalidExpressionException {
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
			
			try {
				return new FunctionEvaluatedExpression(
						functionMatcher.group("ID"),
						preProcessedExpression,
						parameters,
						functionMatcher.group("EXPRESSION"),
						prepareExpression(functionMatcher.group("EXPRESSION")).isBoolean());
			} catch (Expression.ExpressionException e) {
				throw new InvalidExpressionException(e.getMessage(), e);
			}
		}
		
		String id = null;
		
		Matcher idMatcher = ID.matcher(processedExpression);
		
		if (idMatcher.matches()) {
			id = idMatcher.group("ID");
			processedExpression = idMatcher.group("EXPRESSION");
		}
		
		CompoundUnit unitFrom = null;
		CompoundUnit unitTo = null;
		
		Matcher expressionUnitSeparatorMatcher = EXPRESSION_UNIT_SEPARATOR.matcher(processedExpression);
		
		if (findExpressionUnitSeparatorPosition(processedExpression, expressionUnitSeparatorMatcher)) {
			String expressionPart = processedExpression.substring(0, expressionUnitSeparatorMatcher.start() + 1);
			String unitsPart = processedExpression.substring(expressionUnitSeparatorMatcher.start() + 1);
			
			String[] unitParts = splitUnitConversion(unitsPart);
			
			if (unitParts[0] != null && unitParts[0].isEmpty()) {
				unitFrom = unitConverter.getCompoundUnit(expressionPart.trim());
				
				if (unitFrom != null && !isKnown(unitFrom)) {
					expressionPart = "1";
				} else {
					unitFrom = unitConverter.getCompoundUnit(unitParts[1]);
					
					if (unitFrom == null) {
						throw new InvalidExpressionException("No such unit: " + unitParts[1]);
					}
					
					unitParts[0] = unitParts[1];
					unitParts[1] = null;
				}
			} else if (unitParts[1] != null && unitParts[1].isEmpty()) {
				unitFrom = unitConverter.getCompoundUnit(expressionPart.trim());
				
				if (unitFrom == null) {
					throw new InvalidExpressionException("No such unit: " + expressionPart.trim());
				}
				
				expressionPart = "1";
				unitParts[1] = unitParts[0];
			} else {
				unitFrom = unitConverter.getCompoundUnit(unitParts[0]);
				
				if (unitFrom == null) {
					throw new InvalidExpressionException("No such unit: " + unitParts[0]);
				}
			}
			
			if (unitParts[1] == null) {
				unitTo = unitFrom.atBase();
			} else {
				unitTo = unitConverter.getCompoundUnit(unitParts[1]);
				
				if (unitTo == null) {
					throw new InvalidExpressionException("No such unit: " + unitParts[1]);
				}
			}
			
			processedExpression = expressionPart;
		} else if (!isKnown(processedExpression)) {
			unitFrom = unitConverter.getCompoundUnit(processedExpression);
			
			if (unitFrom != null) {
				if (!isKnown(unitFrom)) {
					unitTo = unitFrom.atBase();
					processedExpression = "1";
				} else {
					unitFrom = null;
				}
			}
		}
		
		try {
			Expression mathExpression = prepareExpression(processedExpression);
			
			BigDecimal result = mathExpression.eval();
			
			if (unitFrom != null && unitTo != null) {
				result = unitConverter.convert(unitFrom, unitTo, result, calculationMathContext).stripTrailingZeros();
			}
			
			if (id == null && idSupplier != null) {
				id = idSupplier.get();
			}
			
			result = result.round(resultMathContext);
			
			if (mathExpression.isBoolean()) {
				return new BooleanEvaluatedExpression(id, preProcessedExpression, result);
			} else {
				return new NumberEvaluatedExpression(id, preProcessedExpression, result);
			}
		} catch (Throwable th) {
			throw new InvalidExpressionException(th.getMessage(), th);
		}
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
	
	private boolean findExpressionUnitSeparatorPosition(String expression, Matcher expressionUnitSeparatorMatcher) {
		int searchIndex = 0;
		
		while (expressionUnitSeparatorMatcher.find(searchIndex)) {
			if (expression.substring(
					expressionUnitSeparatorMatcher.start() + 1,
					expressionUnitSeparatorMatcher.end() - 1).equals(" ")) {
				return true;
			}
			
			String foundExpression = "";
			int index = 0;
			
			index = expressionUnitSeparatorMatcher.start();
			
			while (index >= 0 && Character.isLetterOrDigit(expression.charAt(index))) {
				index--;
			}
			
			foundExpression = expression.substring(
					index + 1,
					Math.min(expression.length(), expressionUnitSeparatorMatcher.end()));
			
			if (!foundExpression.isEmpty() && Character.isDigit(foundExpression.charAt(0))) {
				return true;
			}
			
			if (expressionUnitSeparatorMatcher.end() < expression.length()) {
				index = expressionUnitSeparatorMatcher.end();
				
				while (index < expression.length() && Character.isLetterOrDigit(expression.charAt(index))) {
					index++;
				}
				
				foundExpression = expression.substring(expressionUnitSeparatorMatcher.end(), index)
						+ foundExpression;
			}
			
			if (!isKnown(foundExpression)) {
				return true;
			}
			
			searchIndex = expressionUnitSeparatorMatcher.start() + 1;
		}
		
		return false;
	}
	
	private String getNextId() {
		expressionCounter = expressionCounter + 1;
		
		return "#" + Integer.toString(expressionCounter);
	}
	
	private boolean isKnown(CompoundUnit compoundUnit) {
		for (Token token : compoundUnit.getTokens()) {
			if (token.getTokenType() == TokenType.UNIT) {
				if (!isKnown(token.getValue())) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private boolean isKnown(String name) {
		for (EvaluatedExpression evaluatedExpression : previousEvaluatedExpressions) {
			if (evaluatedExpression.getId().equals(name)) {
				return true;
			}
		}
		
		return false;
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
		for (int index = previousEvaluatedExpressions.size() - 1; index >= 0; index--) {
			EvaluatedExpression evaluatedExpression = previousEvaluatedExpressions.get(index);
			
			if (evaluatedExpression instanceof NumberEvaluatedExpression) {
				return evaluatedExpression.getId();
			}
		}
		
		return "0";
	}
	
	private String[] splitUnitConversion(String unitConversionString) {
		int splitIndex = unitConversionString.indexOf(" as ");
		
		if (splitIndex < 0) {
			splitIndex = unitConversionString.indexOf(" in ");
		}
		
		if (splitIndex < 0) {
			splitIndex = unitConversionString.indexOf(" to ");
		}
		
		if (splitIndex >= 0) {
			return new String[] {
					unitConversionString.substring(0, splitIndex).trim(),
					unitConversionString.substring(splitIndex + 4).trim()
			};
		}
		
		splitIndex = unitConversionString.lastIndexOf(" ");
		
		if (splitIndex >= 0) {
			return new String[] {
					unitConversionString.substring(0, splitIndex).trim(),
					unitConversionString.substring(splitIndex + 1).trim()
			};
		}
		
		return new String[] {
				unitConversionString.trim(),
				null
		};
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
