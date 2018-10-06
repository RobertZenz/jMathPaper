/*
 * Copyright 2018, Robert 'Bobby' Zenz
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

package org.bonsaimind.jmathpaper.core.units;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bonsaimind.jmathpaper.core.units.CompoundUnit.Token;
import org.bonsaimind.jmathpaper.core.units.CompoundUnit.TokenType;

import com.udojava.evalex.Expression;

public class UnitConverter {
	protected static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(512, RoundingMode.HALF_UP);
	protected Map<Unit, Map<Unit, BigDecimal>> conversionFactors = new HashMap<>();
	protected MathContext conversionMathContext = DEFAULT_MATH_CONTEXT;
	protected Map<Unit, Map<Unit, String>> conversions = new HashMap<>();
	protected Map<String, Prefix> prefixesByName = new HashMap<>();
	protected Map<String, Prefix> prefixesBySymbol = new HashMap<>();
	protected Map<String, Unit> unitsByName = new HashMap<>();
	protected Map<String, Unit> unitsBySymbol = new HashMap<>();
	private List<Prefix> readonlyPrefixes = null;
	private List<Unit> readonlyUnits = null;
	
	public UnitConverter() {
		super();
		
		registerUnit(Unit.ONE);
	}
	
	public BigDecimal convert(CompoundUnit from, CompoundUnit to, BigDecimal value, MathContext mathContext) {
		MathContext calculationMathContext = createCalculationMathContext(mathContext);
		
		return getConversionFactor(from, to, calculationMathContext)
				.multiply(value, calculationMathContext)
				.round(mathContext)
				.stripTrailingZeros();
	}
	
	public BigDecimal convert(PrefixedUnit from, PrefixedUnit to, BigDecimal value, MathContext mathContext) {
		MathContext calculationMathContext = createCalculationMathContext(mathContext);
		
		BigDecimal conversionFactor = getConversionFactor(from, to, calculationMathContext);
		
		if (conversionFactor != null) {
			return value
					.multiply(conversionFactor, calculationMathContext)
					.round(mathContext)
					.stripTrailingZeros();
		} else {
			// Let's try with a conversion expression instead.
			List<String> conversions = getConversions(from.getUnit(), to.getUnit());
			
			if (conversions == null) {
				throw new UnsupportedOperationException("Cannot convert from " + from.toString() + " to " + to.toString() + ".");
			} else if (conversions.isEmpty()) {
				return convertBetweenPrefixes(from, to, value, calculationMathContext)
						.round(mathContext)
						.stripTrailingZeros();
			} else {
				BigDecimal convertedValue = value;
				convertedValue = convertedValue.multiply(from.getPrefix().getFactor(), calculationMathContext);
				
				for (String conversion : conversions) {
					convertedValue = new Expression(conversion, calculationMathContext).with("x", convertedValue).eval();
				}
				
				return convertedValue
						.divide(to.getPrefix().getFactor(), calculationMathContext)
						.round(mathContext)
						.stripTrailingZeros();
			}
		}
	}
	
	public BigDecimal convert(String from, String to, BigDecimal value, MathContext mathContext) {
		if (isCompoundUnit(from) || isCompoundUnit(to)) {
			if (!isCompoundUnit(from) || !isCompoundUnit(to)) {
				throw new UnsupportedOperationException("Cannot convert between " + from + " and " + to + ".");
			}
			
			return convert(getCompoundUnit(from), getCompoundUnit(to), value, mathContext);
		}
		
		PrefixedUnit fromPrefixedUnit = getPrefixedUnit(from);
		
		if (fromPrefixedUnit == null) {
			throw new UnsupportedOperationException("No such unit known: " + from);
		}
		
		PrefixedUnit toPrefixedUnit = getPrefixedUnit(to);
		
		if (toPrefixedUnit == null) {
			throw new UnsupportedOperationException("No such unit known: " + to);
		}
		
		return convert(fromPrefixedUnit, toPrefixedUnit, value, mathContext);
	}
	
	public CompoundUnit getCompoundUnit(String compoundUnit) {
		if (compoundUnit == null) {
			return null;
		}
		
		StringBuilder currentToken = new StringBuilder();
		TokenType currentTokenType = null;
		
		List<Token> tokens = new ArrayList<>();
		
		for (char character : compoundUnit.toCharArray()) {
			if (isUnitPart(character)) {
				if (currentTokenType != TokenType.UNIT && currentToken.length() > 0) {
					tokens.add(new Token(
							currentToken.toString(),
							currentTokenType,
							null));
					
					currentToken.delete(0, currentToken.length());
				}
				
				currentToken.append(character);
				currentTokenType = TokenType.UNIT;
			} else if (isOperatorPart(character)) {
				if (currentTokenType != TokenType.OPERATOR && currentToken.length() > 0) {
					PrefixedUnit prefixedUnit = getPrefixedUnit(currentToken.toString());
					
					if (prefixedUnit == null) {
						return null;
					}
					
					tokens.add(new Token(
							currentToken.toString(),
							currentTokenType,
							prefixedUnit));
					
					currentToken.delete(0, currentToken.length());
				}
				
				currentToken.append(character);
				currentTokenType = TokenType.OPERATOR;
			}
		}
		
		if (currentToken.length() > 0) {
			if (currentTokenType == TokenType.OPERATOR) {
				tokens.add(new Token(
						currentToken.toString(),
						currentTokenType,
						null));
			} else {
				PrefixedUnit prefixedUnit = getPrefixedUnit(currentToken.toString());
				
				if (prefixedUnit == null) {
					return null;
				}
				
				tokens.add(new Token(
						currentToken.toString(),
						currentTokenType,
						prefixedUnit));
			}
		}
		
		if (tokens.isEmpty()) {
			return null;
		}
		
		return new CompoundUnit(tokens);
	}
	
	public Prefix getPrefix(String prefixNameOrSymbol) {
		if (prefixNameOrSymbol == null) {
			return null;
		}
		
		Prefix prefix = prefixesByName.get(prefixNameOrSymbol.toLowerCase());
		
		if (prefix == null) {
			prefix = prefixesBySymbol.get(prefixNameOrSymbol);
		}
		
		return prefix;
	}
	
	public PrefixedUnit getPrefixedUnit(String prefixedUnit) {
		if (prefixedUnit == null) {
			return null;
		}
		
		if (prefixedUnit.toLowerCase().startsWith("square")) {
			prefixedUnit = prefixedUnit.substring(6) + "^2";
		} else if (prefixedUnit.toLowerCase().startsWith("sq")) {
			prefixedUnit = prefixedUnit.substring(2) + "^2";
		} else if (prefixedUnit.toLowerCase().startsWith("cubic")) {
			prefixedUnit = prefixedUnit.substring(5) + "^3";
		} else if (prefixedUnit.toLowerCase().startsWith("cu")) {
			prefixedUnit = prefixedUnit.substring(2) + "^3";
		}
		
		Unit unit = getUnit(prefixedUnit);
		
		if (unit != null) {
			return new PrefixedUnit(Prefix.BASE, unit);
		}
		
		Prefix prefix = null;
		
		for (int index = 1; index < prefixedUnit.length(); index++) {
			prefix = getPrefix(prefixedUnit.substring(0, index));
			
			if (prefix != null) {
				unit = getUnit(prefixedUnit.substring(index));
				
				if (unit != null) {
					return new PrefixedUnit(prefix, unit);
				}
			}
		}
		
		// No prefixed unit found, let's try with just a prefix.
		prefix = getPrefix(prefixedUnit);
		
		if (prefix != null) {
			return new PrefixedUnit(prefix, Unit.ONE);
		}
		
		// No luck.
		return null;
	}
	
	public List<Prefix> getPrefixes() {
		if (readonlyPrefixes == null) {
			readonlyPrefixes = new ArrayList<>(prefixesByName.values());
		}
		
		return readonlyPrefixes;
	}
	
	public Unit getUnit(String unitNameOrAlias) {
		if (unitNameOrAlias == null) {
			return null;
		}
		
		int exponent = 1;
		int exponentIndex = unitNameOrAlias.indexOf("^");
		
		if (exponentIndex >= 0) {
			exponent = Integer.parseInt(unitNameOrAlias.substring(exponentIndex + 1));
			unitNameOrAlias = unitNameOrAlias.substring(0, exponentIndex);
		} else if (unitNameOrAlias.toLowerCase().startsWith("square")) {
			exponent = 2;
			unitNameOrAlias = unitNameOrAlias.substring(6);
		} else if (unitNameOrAlias.toLowerCase().startsWith("sq")) {
			exponent = 2;
			unitNameOrAlias = unitNameOrAlias.substring(2);
		} else if (unitNameOrAlias.toLowerCase().startsWith("cubic")) {
			exponent = 3;
			unitNameOrAlias = unitNameOrAlias.substring(5);
		} else if (unitNameOrAlias.toLowerCase().startsWith("cu")) {
			exponent = 3;
			unitNameOrAlias = unitNameOrAlias.substring(2);
		}
		
		Unit unit = unitsByName.get(unitNameOrAlias.toLowerCase());
		
		if (unit == null) {
			if (unitNameOrAlias.toLowerCase().endsWith("s")) {
				unit = unitsByName.get(unitNameOrAlias.substring(0, unitNameOrAlias.length() - 1).toLowerCase());
			}
			
			if (unit == null) {
				unit = unitsBySymbol.get(unitNameOrAlias);
			}
		}
		
		if (unit != null && exponent > 1 && exponent != unit.getExponent()) {
			unit = unit.withExponent(exponent);
		}
		
		return unit;
	}
	
	public List<Unit> getUnits() {
		if (readonlyUnits == null) {
			readonlyUnits = new ArrayList<>(unitsByName.values());
		}
		
		return readonlyUnits;
	}
	
	public void loadConversion(String conversionDefinition) {
		if (conversionDefinition == null || conversionDefinition.length() == 0) {
			return;
		}
		
		String[] splittedDefinition = null;
		
		if (conversionDefinition.contains("(") && conversionDefinition.contains(")")) {
			int openingParantheses = conversionDefinition.indexOf("(");
			int closingParantheses = conversionDefinition.lastIndexOf(")");
			
			splittedDefinition = new String[] {
					conversionDefinition.substring(0, openingParantheses).trim(),
					conversionDefinition.substring(openingParantheses, closingParantheses + 1).trim(),
					conversionDefinition.substring(closingParantheses + 1).trim()
			};
		} else {
			splittedDefinition = conversionDefinition.split("[^a-zA-Z0-9μ°.+\\-*/\\^()]+");
		}
		
		if (splittedDefinition.length == 1) {
			return;
		}
		
		PrefixedUnit from = getPrefixedUnit(splittedDefinition[0]);
		
		if (from == null) {
			throw new UnsupportedOperationException("No such prefixed unit: " + splittedDefinition[0]);
		}
		
		PrefixedUnit to = null;
		String conversionString = null;
		
		if (splittedDefinition.length == 2) {
			String toDefinition = splittedDefinition[1];
			
			int unitIndex = toDefinition.lastIndexOf(")");
			
			if (unitIndex < 0) {
				unitIndex = 0;
				
				while (Character.isDigit(toDefinition.charAt(unitIndex))
						|| toDefinition.charAt(unitIndex) == '+'
						|| toDefinition.charAt(unitIndex) == '-'
						|| toDefinition.charAt(unitIndex) == '*'
						|| toDefinition.charAt(unitIndex) == '/'
						|| toDefinition.charAt(unitIndex) == '.'
						|| toDefinition.charAt(unitIndex) == '^') {
					unitIndex++;
				}
			} else {
				unitIndex = unitIndex + 1;
			}
			
			to = getPrefixedUnit(toDefinition.substring(unitIndex));
			
			if (to == null) {
				throw new UnsupportedOperationException("No such prefixed unit: " + toDefinition.substring(unitIndex));
			}
			
			conversionString = toDefinition.substring(0, unitIndex);
		} else {
			to = getPrefixedUnit(splittedDefinition[2]);
			
			if (to == null) {
				throw new UnsupportedOperationException("No such prefixed unit: " + splittedDefinition[2]);
			}
			
			conversionString = splittedDefinition[1];
		}
		
		if (conversionString.contains("x") || conversionString.startsWith(")")) {
			registerConversion(from, to, conversionString);
		} else {
			registerConversion(from, to, new Expression(conversionString, conversionMathContext).eval());
		}
	}
	
	public void loadPrefix(String prefixDefinition) {
		if (prefixDefinition == null || prefixDefinition.length() == 0) {
			return;
		}
		
		String[] splittedDefinition = prefixDefinition.split("[^a-zA-Z0-9μ°\\-]+");
		
		if (splittedDefinition.length != 4) {
			throw new IllegalArgumentException("Not a well formed prefix definition: " + prefixDefinition);
		}
		
		Prefix prefix = new Prefix(
				splittedDefinition[0],
				splittedDefinition[1],
				Integer.parseInt(splittedDefinition[2]),
				Integer.parseInt(splittedDefinition[3]));
		
		registerPrefix(prefix);
	}
	
	public void loadUnit(String unitDefinition) {
		if (unitDefinition == null || unitDefinition.length() == 0) {
			return;
		}
		
		String[] splittedDefinition = unitDefinition.split("[^a-zA-Z0-9μ°]+");
		
		String name = splittedDefinition[0];
		int exponent = 1;
		String[] aliases = null;
		
		if (splittedDefinition.length > 1) {
			exponent = Integer.parseInt(splittedDefinition[1]);
		}
		
		if (splittedDefinition.length > 2) {
			aliases = new String[splittedDefinition.length - 2];
			
			System.arraycopy(splittedDefinition, 2, aliases, 0, aliases.length);
		}
		
		Unit unit = new Unit(name, exponent, aliases);
		
		registerUnit(unit);
	}
	
	public UnitConverter registerConversion(PrefixedUnit from, PrefixedUnit to, BigDecimal conversionFactor) {
		return registerConversion(
				from.getUnit(),
				to.getUnit(),
				convertBetweenPrefixes(to, from, conversionFactor, conversionMathContext));
	}
	
	public UnitConverter registerConversion(PrefixedUnit from, PrefixedUnit to, String conversion) {
		String expressionString = conversion;
		
		if (from.getPrefix() != Prefix.BASE) {
			expressionString = expressionString.replace("x", "(x*" + from.getPrefix().getFactor().pow(from.getUnit().getExponent(), conversionMathContext).toString() + ")");
		}
		
		if (from.getPrefix() != Prefix.BASE) {
			expressionString = "(" + expressionString + ")*" + to.getPrefix().getFactor().pow(to.getUnit().getExponent(), conversionMathContext).toString() + "";
		}
		
		registerConversionInternal(
				from.getUnit(),
				to.getUnit(),
				expressionString);
		
		return this;
	}
	
	public UnitConverter registerConversion(Unit from, Unit to, BigDecimal conversionFactor) {
		registerConversionInternal(from, to, conversionFactor);
		registerConversionInternal(to, from, BigDecimal.ONE.divide(conversionFactor, conversionMathContext));
		
		return this;
	}
	
	public UnitConverter registerConversion(Unit from, Unit to, String expression) {
		registerConversionInternal(from, to, expression);
		
		return this;
	}
	
	public UnitConverter registerPrefix(Prefix prefix) {
		prefixesByName.put(prefix.getName().toLowerCase(), prefix);
		prefixesBySymbol.put(prefix.getSymbol(), prefix);
		
		return this;
	}
	
	public UnitConverter registerUnit(Unit unit) {
		if (unit == null) {
			return this;
		}
		
		unitsByName.put(unit.getName().toLowerCase(), unit);
		
		for (String alias : unit.getAliases()) {
			unitsBySymbol.put(alias, unit);
		}
		
		return this;
	}
	
	protected BigDecimal convertBetweenPrefixes(PrefixedUnit from, PrefixedUnit to, BigDecimal value, MathContext mathContext) {
		BigDecimal fromFactor = null;
		BigDecimal toFactor = null;
		
		if (from.getUnit().isDerived()) {
			fromFactor = from.getPrefix().getFactor().pow(from.getUnit().getExponent(), mathContext);
		} else {
			fromFactor = from.getPrefix().getFactor();
		}
		
		if (to.getUnit().isDerived()) {
			toFactor = to.getPrefix().getFactor().pow(to.getUnit().getExponent(), mathContext);
		} else {
			toFactor = to.getPrefix().getFactor();
		}
		
		return value
				.multiply(fromFactor, mathContext)
				.divide(toFactor, mathContext);
	}
	
	protected MathContext createCalculationMathContext(MathContext resultMathContext) {
		return new MathContext(
				Math.max(resultMathContext.getPrecision() * 2, 4),
				resultMathContext.getRoundingMode());
	}
	
	protected <TARGET> boolean findConversions(
			Unit sourceUnit,
			Unit targetUnit,
			Map<Unit, Map<Unit, TARGET>> conversions,
			Set<Unit> checkedUnits,
			List<TARGET> foundConversionFactors) {
		Map<Unit, TARGET> sourceConversionFactors = conversions.get(sourceUnit);
		
		if (sourceConversionFactors != null) {
			for (Entry<Unit, TARGET> entry : sourceConversionFactors.entrySet()) {
				if (entry.getKey().equals(targetUnit)) {
					foundConversionFactors.add(entry.getValue());
					
					return true;
				} else if (!checkedUnits.contains(entry.getKey())) {
					checkedUnits.add(entry.getKey());
					
					if (findConversions(entry.getKey(), targetUnit, conversions, checkedUnits, foundConversionFactors)) {
						foundConversionFactors.add(0, entry.getValue());
						
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	protected BigDecimal getConversionFactor(CompoundUnit from, CompoundUnit to, MathContext mathContext) {
		StringBuilder conversionExpressionString = new StringBuilder();
		Map<String, BigDecimal> variables = new HashMap<>();
		
		if (from.getTokens().size() != to.getTokens().size()) {
			throw new UnsupportedOperationException("Cannot convert from " + from.toString() + " to " + to.toString() + ".");
		}
		
		for (int index = 0; index < from.getTokens().size(); index++) {
			Token fromToken = from.getTokens().get(index);
			Token toToken = to.getTokens().get(index);
			
			if (fromToken.getTokenType() != toToken.getTokenType()) {
				throw new UnsupportedOperationException("Cannot convert from " + from.toString() + " to " + to.toString() + ".");
			}
			
			if (fromToken.getTokenType() == TokenType.UNIT) {
				String variableName = "V" + Integer.toString(variables.size());
				BigDecimal convertedValue = convert(
						fromToken.getUnit(),
						toToken.getUnit(),
						BigDecimal.ONE,
						mathContext);
				
				conversionExpressionString.append(variableName);
				variables.put(variableName, convertedValue);
			} else {
				if (!fromToken.getValue().equals(toToken.getValue())) {
					throw new UnsupportedOperationException("Cannot convert from " + from.toString() + " to " + to.toString() + ".");
				}
				
				conversionExpressionString.append(fromToken.getValue());
			}
		}
		
		Expression conversionExpression = new Expression(
				conversionExpressionString.toString(),
				mathContext);
		
		for (Entry<String, BigDecimal> entry : variables.entrySet()) {
			conversionExpression.setVariable(entry.getKey(), entry.getValue());
		}
		
		return conversionExpression.eval();
	}
	
	protected BigDecimal getConversionFactor(PrefixedUnit from, PrefixedUnit to, MathContext mathContext) {
		BigDecimal conversionFactor = getConversionFactor(from.getUnit(), to.getUnit(), mathContext);
		
		if (conversionFactor != null) {
			conversionFactor = convertBetweenPrefixes(from, to, conversionFactor, mathContext);
		}
		
		return conversionFactor;
	}
	
	protected BigDecimal getConversionFactor(Unit from, Unit to, MathContext mathContext) {
		if (from.equals(to)) {
			return BigDecimal.ONE;
		}
		
		if (from.getExponent() != to.getExponent()) {
			throw new UnsupportedOperationException("Cannot convert between units with different dimensions, from " + from.toString() + " to " + to.toString());
		}
		
		BigDecimal conversionFactor = getConversionFactorInternal(from, to);
		
		if (conversionFactor == null) {
			// Try the other way round.
			conversionFactor = getConversionFactorInternal(to, from);
			
			if (conversionFactor != null) {
				conversionFactor = BigDecimal.ONE.divide(conversionFactor, mathContext);
			}
		}
		
		if (conversionFactor == null) {
			// Okay, let's see if we can get it with some hops.
			List<BigDecimal> foundConversionFactors = new ArrayList<>();
			
			if (findConversions(from, to, conversionFactors, new HashSet<Unit>(), foundConversionFactors)) {
				conversionFactor = BigDecimal.ONE;
				
				for (BigDecimal singleConversionFactor : foundConversionFactors) {
					conversionFactor = conversionFactor.multiply(singleConversionFactor, mathContext);
				}
			}
		}
		
		if (conversionFactor == null
				&& from.getExponent() > 1
				&& (from.isDerived() || to.isDerived())) {
			// Squared/Cubic units? Let's try the plain ones.
			
			Unit plainFrom = from;
			Unit plainTo = to;
			
			if (from.isDerived()) {
				plainFrom = from.withExponent(1);
			}
			
			if (to.isDerived()) {
				plainTo = to.withExponent(1);
			}
			
			conversionFactor = getConversionFactor(plainFrom, plainTo, mathContext);
			
			if (conversionFactor != null) {
				conversionFactor = conversionFactor.pow(from.getExponent(), mathContext);
			}
		}
		
		return conversionFactor;
	}
	
	protected BigDecimal getConversionFactorInternal(Unit from, Unit to) {
		Map<Unit, BigDecimal> toMap = conversionFactors.get(from);
		
		if (toMap != null) {
			return toMap.get(to);
		}
		
		return null;
	}
	
	protected String getConversionInternal(Unit from, Unit to) {
		Map<Unit, String> toMap = conversions.get(from);
		
		if (toMap != null) {
			return toMap.get(to);
		}
		
		return null;
	}
	
	protected List<String> getConversions(Unit from, Unit to) {
		if (from.equals(to)) {
			return Collections.emptyList();
		}
		
		if (from.getExponent() != to.getExponent()) {
			throw new UnsupportedOperationException("Cannot convert between units with different dimensions, from " + from.toString() + " to " + to.toString());
		}
		
		String conversion = getConversionInternal(from, to);
		
		if (conversion != null) {
			return Arrays.asList(conversion);
		} else if (conversion == null) {
			// Okay, let's see if we can get it with some hops.
			List<String> foundConversions = new ArrayList<>();
			
			if (findConversions(from, to, conversions, new HashSet<Unit>(), foundConversions)) {
				return foundConversions;
			}
		}
		
		return null;
	}
	
	protected boolean isCompoundUnit(String unit) {
		return unit.contains("+")
				|| unit.contains("-")
				|| unit.contains("*")
				|| unit.contains("/");
	}
	
	protected boolean isOperatorPart(char character) {
		return character == '+'
				|| character == '-'
				|| character == '*'
				|| character == '/';
	}
	
	protected boolean isUnitPart(char character) {
		return Character.isLetter(character)
				|| Character.isDigit(character)
				|| character == '^';
	}
	
	protected void registerConversionInternal(Unit from, Unit to, BigDecimal conversionFactor) {
		Map<Unit, BigDecimal> targetMap = conversionFactors.get(from);
		
		if (targetMap == null) {
			targetMap = new HashMap<>();
			conversionFactors.put(from, targetMap);
		}
		
		targetMap.put(to, conversionFactor);
	}
	
	protected void registerConversionInternal(Unit from, Unit to, String conversion) {
		Map<Unit, String> targetMap = conversions.get(from);
		
		if (targetMap == null) {
			targetMap = new HashMap<>();
			conversions.put(from, targetMap);
		}
		
		targetMap.put(to, conversion);
	}
}