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
import java.util.Collections;
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
import org.bonsaimind.jmathpaper.core.units.ConversionKeyword;
import org.bonsaimind.jmathpaper.core.units.UnitConversion;
import org.bonsaimind.jmathpaper.core.units.UnitConverter;

import com.udojava.evalex.Expression;

public class Evaluator {
	protected static final Pattern BINARY_NUMBER = ResourceLoader.compileRegex("binary-number");
	protected static final String COMMENT_INLINE_END = "*/";
	protected static final String COMMENT_INLINE_START = "/*";
	protected static final String COMMENT_START = "//";
	protected static final MathContext DEFAULT_CALCULATION_MATH_CONTEXT = new MathContext(64, RoundingMode.HALF_UP);
	protected static final MathContext DEFAULT_RESULT_MATH_CONTEXT = new MathContext(32, RoundingMode.HALF_UP);
	protected static final Pattern EXPRESSION_UNIT_SEPARATOR = ResourceLoader.compileRegex("expression-unit-separator");
	protected static final Pattern FUNCTION = ResourceLoader.compileRegex("function");
	protected static final Pattern HEX_NUMBER = ResourceLoader.compileRegex("hex-number");
	protected static final Pattern ID = ResourceLoader.compileRegex("id");
	protected static final Pattern LAST_REFERENCE = ResourceLoader.compileRegex("last-reference");
	protected static final Pattern OCTAL_NUMBER = ResourceLoader.compileRegex("octal-number");
	protected Map<String, String> aliases = new HashMap<>();
	protected MathContext calculationMathContext = DEFAULT_CALCULATION_MATH_CONTEXT;
	protected List<EvaluatedExpression> contextExpressions = new ArrayList<>();
	protected List<EvaluatedExpression> evaluatedExpressions = new ArrayList<>();
	protected MathContext resultMathContext = DEFAULT_RESULT_MATH_CONTEXT;
	protected UnitConverter unitConverter = new UnitConverter();
	private int expressionCounter = 0;
	private List<EvaluatedExpression> readonlyEvaluatedExpressions = null;
	
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
	
	public List<EvaluatedExpression> getEvaluatedExpressions() {
		if (readonlyEvaluatedExpressions == null) {
			readonlyEvaluatedExpressions = Collections.unmodifiableList(evaluatedExpressions);
		}
		
		return evaluatedExpressions;
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
		
		for (EvaluatedExpression previousEvaluatedExpression : evaluatedExpressions) {
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
		evaluatedExpressions.clear();
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
		if (expression == null || expression.trim().isEmpty()) {
			if (idSupplier != null) {
				return new NumberEvaluatedExpression(idSupplier.get(), "0", BigDecimal.ZERO, CompoundUnit.ONE);
			} else {
				return new NumberEvaluatedExpression(null, "0", BigDecimal.ZERO, CompoundUnit.ONE);
			}
		}
		
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
		
		CompoundUnit unitSource = null;
		CompoundUnit unitTarget = null;
		
		Matcher expressionUnitSeparatorMatcher = EXPRESSION_UNIT_SEPARATOR.matcher(processedExpression);
		
		if (findExpressionUnitSeparatorPosition(processedExpression, expressionUnitSeparatorMatcher)) {
			String expressionPart = processedExpression.substring(0, expressionUnitSeparatorMatcher.start() + 1);
			String unitsPart = processedExpression.substring(expressionUnitSeparatorMatcher.start() + 1);
			
			UnitConversion unitConversion = splitUnitConversion(unitsPart);
			
			if (unitConversion.getSourceString() != null && unitConversion.getSourceString().isEmpty()) {
				unitSource = unitConverter.getCompoundUnit(expressionPart.trim());
				
				if (unitSource != null && !isKnown(unitSource) && !unitSource.isOne()) {
					expressionPart = "1";
				} else if (unitConversion.getKeywordString() != null) {
					unitSource = unitConverter.getCompoundUnit(unitConversion.getKeywordString());
					
					if (unitSource == null) {
						throw new InvalidExpressionException("No such unit: " + unitConversion.getKeywordString());
					}
					
					unitConversion.setSourceString(unitConversion.getKeywordString());
					unitConversion.setKeywordString(null);
				} else {
					unitSource = unitConverter.getCompoundUnit(unitConversion.getTargetString());
					
					if (unitSource == null) {
						throw new InvalidExpressionException("No such unit: " + unitConversion.getTargetString());
					}
					
					unitConversion.setSourceString(unitConversion.getTargetString());
					unitConversion.setTargetString(null);
				}
			} else if (unitConversion.getTargetString() != null && unitConversion.getTargetString().isEmpty()) {
				unitSource = unitConverter.getCompoundUnit(expressionPart.trim());
				
				if (unitSource == null) {
					throw new InvalidExpressionException("No such unit: " + expressionPart.trim());
				}
				
				expressionPart = "1";
				unitConversion.setTargetString(unitConversion.getSourceString());
			} else {
				unitSource = unitConverter.getCompoundUnit(unitConversion.getSourceString());
				
				if (unitSource == null) {
					throw new InvalidExpressionException("No such unit: " + unitConversion.getSourceString());
				}
			}
			
			if (unitConversion.getTargetString() == null) {
				unitTarget = unitSource.atBase();
			} else {
				unitTarget = unitConverter.getCompoundUnit(unitConversion.getTargetString());
				
				if (unitTarget == null) {
					throw new InvalidExpressionException("No such unit: " + unitConversion.getTargetString());
				}
			}
			
			processedExpression = expressionPart;
		} else if (!isKnown(processedExpression)) {
			unitSource = unitConverter.getCompoundUnit(processedExpression);
			
			if (unitSource != null) {
				// Disallow automatic conversion from 1.
				if (!isKnown(unitSource) && !unitSource.isOne()) {
					unitTarget = unitSource.atBase();
					processedExpression = "1";
				} else {
					unitSource = null;
				}
			}
		}
		
		try {
			Expression mathExpression = prepareExpression(processedExpression);
			
			BigDecimal result = mathExpression.eval();
			
			if (unitSource != null && unitTarget != null) {
				result = unitConverter.convert(unitSource, unitTarget, result, calculationMathContext).stripTrailingZeros();
			}
			
			if (id == null && idSupplier != null) {
				id = idSupplier.get();
			}
			
			result = result.round(resultMathContext);
			
			if (mathExpression.isBoolean()) {
				return new BooleanEvaluatedExpression(id, preProcessedExpression, result);
			} else {
				if (unitTarget != null) {
					return new NumberEvaluatedExpression(id, preProcessedExpression, result, unitTarget);
				} else {
					return new NumberEvaluatedExpression(id, preProcessedExpression, result, CompoundUnit.ONE);
				}
			}
		} catch (Throwable th) {
			throw new InvalidExpressionException(th.getMessage(), th);
		}
	}
	
	/**
	 * Strips any comments from the given expression.
	 * 
	 * @param expression The expression to process.
	 * @return The expression with all comments stripped.
	 */
	protected String stripComments(String expression) {
		if (expression == null || expression.isEmpty()) {
			return expression;
		}
		
		String strippedExpression = expression;
		
		// Strip the inline/fenced comments first.
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
				return strippedExpression.substring(0, commentStartIndex);
			}
			
			commentStartIndex = strippedExpression.indexOf(COMMENT_INLINE_START);
		}
		
		// Now strip the ones the go to the end of the line.
		commentStartIndex = strippedExpression.indexOf(COMMENT_START);
		
		if (commentStartIndex >= 0) {
			strippedExpression = strippedExpression.substring(0, commentStartIndex);
		}
		
		return strippedExpression;
	}
	
	private EvaluatedExpression addEvaluatedExpression(EvaluatedExpression evaluatedExpression) {
		if (evaluatedExpression != null) {
			evaluatedExpressions.add(evaluatedExpression);
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
		for (EvaluatedExpression evaluatedExpression : evaluatedExpressions) {
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
		for (int index = evaluatedExpressions.size() - 1; index >= 0; index--) {
			EvaluatedExpression evaluatedExpression = evaluatedExpressions.get(index);
			
			if (evaluatedExpression instanceof NumberEvaluatedExpression) {
				return evaluatedExpression.getId();
			}
		}
		
		return "0";
	}
	
	private UnitConversion splitUnitConversion(String unitConversionString) {
		UnitConversion unitConversion = new UnitConversion();
		int splitIndex = -1;
		
		// Find a keyword.
		for (ConversionKeyword keyword : ConversionKeyword.ALL) {
			splitIndex = unitConversionString.indexOf(" " + keyword.toString() + " ");
			
			if (splitIndex >= 0) {
				unitConversion.setKeywordString(keyword.toString());
				unitConversion.setKeyword(keyword);
			} else {
				splitIndex = unitConversionString.indexOf(" " + keyword.toString().toLowerCase() + " ");
				
				if (splitIndex >= 0) {
					unitConversion.setKeywordString(keyword.toString().toLowerCase());
					unitConversion.setKeyword(keyword);
				}
			}
			
			if (unitConversion.getKeyword() != null) {
				break;
			}
		}
		
		if (splitIndex >= 0) {
			unitConversion.setSourceString(unitConversionString.substring(0, splitIndex).trim());
			unitConversion.setTargetString(unitConversionString.substring(splitIndex + unitConversion.getKeywordString().length() + 2).trim());
			
			return unitConversion;
		}
		
		splitIndex = unitConversionString.lastIndexOf(" ");
		
		if (splitIndex >= 0) {
			unitConversion.setSourceString(unitConversionString.substring(0, splitIndex).trim());
			unitConversion.setTargetString(unitConversionString.substring(splitIndex + 1).trim());
			
			return unitConversion;
		}
		
		unitConversion.setSourceString(unitConversionString);
		
		return unitConversion;
	}
}
