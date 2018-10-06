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

import org.bonsaimind.jmathpaper.core.resources.ResourceLoader;
import org.junit.Assert;
import org.junit.Test;

public class TestUnitConverter {
	private static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(34, RoundingMode.HALF_UP);
	
	@Test
	public void testBultinExponentPrefixes() {
		UnitConverter unitConverter = new UnitConverter();
		
		unitConverter.loadUnit("meter 1");
		
		Assert.assertEquals(2, unitConverter.getUnit("squaremeter").getExponent());
		Assert.assertEquals(2, unitConverter.getUnit("sqmeter").getExponent());
		Assert.assertEquals(3, unitConverter.getUnit("cubicmeter").getExponent());
		Assert.assertEquals(3, unitConverter.getUnit("cumeter").getExponent());
		
		Assert.assertEquals(2, unitConverter.getPrefixedUnit("squaremeter").getUnit().getExponent());
		Assert.assertEquals(2, unitConverter.getPrefixedUnit("sqmeter").getUnit().getExponent());
		Assert.assertEquals(3, unitConverter.getPrefixedUnit("cubicmeter").getUnit().getExponent());
		Assert.assertEquals(3, unitConverter.getPrefixedUnit("cumeter").getUnit().getExponent());
	}
	
	@Test
	public void testCompoundUnits() {
		UnitConverter unitConverter = new UnitConverter();
		ResourceLoader.processResource("units/iec.prefixes", unitConverter::loadPrefix);
		ResourceLoader.processResource("units/si.prefixes", unitConverter::loadPrefix);
		ResourceLoader.processResource("units/default.units", unitConverter::loadUnit);
		ResourceLoader.processResource("units/default.conversions", unitConverter::loadConversion);
		
		Assert.assertNull(unitConverter.getCompoundUnit(null));
		Assert.assertNull(unitConverter.getCompoundUnit(""));
		Assert.assertNull(unitConverter.getCompoundUnit("   "));
		Assert.assertNull(unitConverter.getCompoundUnit("12"));
		Assert.assertNull(unitConverter.getCompoundUnit("12 / 12"));
		Assert.assertNull(unitConverter.getCompoundUnit("km/2/h"));
		
		assertEquals(new BigDecimal("0.0001726031089548149915603983845453662"), unitConverter.convert("km/h", "ml/sec", new BigDecimal("1"), DEFAULT_MATH_CONTEXT));
		assertEquals(new BigDecimal("60"), unitConverter.convert("l/min", "l/h", new BigDecimal("1"), DEFAULT_MATH_CONTEXT));
		assertEquals(new BigDecimal("127137.6"), unitConverter.convert("m/s^2", "km/h^2", new BigDecimal("9.81"), DEFAULT_MATH_CONTEXT));
		assertEquals(new BigDecimal("0.04257460806979184310033457322666291"), unitConverter.convert("l/min/m^2", "gal/h/in^2", new BigDecimal("5"), DEFAULT_MATH_CONTEXT));
	}
	
	@Test
	public void testConversion() {
		Unit unitA = new Unit("a", 1);
		Unit unitB = new Unit("b", 1);
		Unit unitC = new Unit("c", 1);
		
		UnitConverter unitConverter = new UnitConverter();
		unitConverter.registerConversion(unitA, unitB, new BigDecimal("1"));
		unitConverter.registerConversion(unitB, unitC, new BigDecimal("2"));
		unitConverter.registerConversion(unitC, unitA, new BigDecimal("3"));
		
		assertEquals(new BigDecimal("1.0"), unitConverter.getConversionFactor(unitA, unitB, MathContext.DECIMAL128));
		assertEquals(new BigDecimal("2.0"), unitConverter.getConversionFactor(unitB, unitC, MathContext.DECIMAL128));
		assertEquals(new BigDecimal("3.0"), unitConverter.getConversionFactor(unitC, unitA, MathContext.DECIMAL128));
	}
	
	@Test
	public void testConversionByExpression() {
		UnitConverter unitConverter = new UnitConverter();
		
		unitConverter.loadUnit("a 1");
		unitConverter.loadUnit("b 1");
		
		unitConverter.loadConversion("a (x*5/2-3)b");
		
		assertEquals(new BigDecimal("2"), unitConverter.convert("a", "b", new BigDecimal("2"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("72"), unitConverter.convert("a", "b", new BigDecimal("30"), MathContext.DECIMAL128));
	}
	
	@Test
	public void testConversionHigherDimensions() {
		UnitConverter unitConverter = new UnitConverter();
		
		ResourceLoader.processResource("units/si.prefixes", unitConverter::loadPrefix);
		
		assertEquals(new BigDecimal("1"), unitConverter.convert("1", "1", new BigDecimal("1"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("0.001"), unitConverter.convert("1", "k1", new BigDecimal("1"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("1000"), unitConverter.convert("k1", "1", new BigDecimal("1"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("0.000001"), unitConverter.convert("1^2", "k1^2", new BigDecimal("1"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("1000000"), unitConverter.convert("k1^2", "1^2", new BigDecimal("1"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("0.000000001"), unitConverter.convert("1^3", "k1^3", new BigDecimal("1"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("1000000000"), unitConverter.convert("k1^3", "1^3", new BigDecimal("1"), MathContext.DECIMAL128));
	}
	
	@Test
	public void testConversionOverMultipleSteps() {
		Unit unitA = new Unit("a", 1);
		Unit unitB = new Unit("b", 1);
		Unit unitC = new Unit("c", 1);
		Unit unitD = new Unit("d", 1);
		
		UnitConverter unitConverter = new UnitConverter();
		unitConverter.registerConversion(unitA, unitB, new BigDecimal("2"));
		unitConverter.registerConversion(unitB, unitC, new BigDecimal("3"));
		unitConverter.registerConversion(unitC, unitD, new BigDecimal("4"));
		
		assertEquals(new BigDecimal("24"), unitConverter.getConversionFactor(unitA, unitD, MathContext.DECIMAL128));
	}
	
	@Test
	public void testConversionOverMultipleStepsWithExpressions() {
		Unit unitA = new Unit("a", 1);
		Unit unitB = new Unit("b", 1);
		Unit unitC = new Unit("c", 1);
		Unit unitD = new Unit("d", 1);
		
		UnitConverter unitConverter = new UnitConverter();
		unitConverter.registerUnit(unitA);
		unitConverter.registerUnit(unitB);
		unitConverter.registerUnit(unitC);
		unitConverter.registerUnit(unitD);
		unitConverter.registerConversion(unitA, unitB, "x*2");
		unitConverter.registerConversion(unitB, unitC, "x*3");
		unitConverter.registerConversion(unitC, unitD, "x*4");
		
		assertEquals(new BigDecimal("24"), unitConverter.convert("a", "d", new BigDecimal("1"), MathContext.DECIMAL128));
	}
	
	@Test
	public void testConversionSameUnit() {
		UnitConverter unitConverter = new UnitConverter();
		
		unitConverter.loadUnit("a 1");
		unitConverter.loadPrefix("b b 10 3");
		
		assertEquals(new BigDecimal("1"), unitConverter.convert("a", "a", new BigDecimal("1"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("0.001"), unitConverter.convert("a", "ba", new BigDecimal("1"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("1000"), unitConverter.convert("ba", "a", new BigDecimal("1"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("1"), unitConverter.convert("ba", "ba", new BigDecimal("1"), MathContext.DECIMAL128));
	}
	
	@Test
	public void testConversionWithDerivedExponents() {
		UnitConverter unitConverter = new UnitConverter();
		
		unitConverter.loadUnit("a 1");
		unitConverter.loadUnit("b 1");
		
		unitConverter.loadConversion("a 2b");
		
		assertEquals(new BigDecimal("2"), unitConverter.convert("a", "b", new BigDecimal("1"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("4"), unitConverter.convert("a^2", "b^2", new BigDecimal("1"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("8"), unitConverter.convert("a^3", "b^3", new BigDecimal("1"), MathContext.DECIMAL128));
	}
	
	@Test
	public void testConversionWithPrefixedUnits() {
		Unit unitA = new Unit("a", 1);
		Unit unitB = new Unit("b", 1);
		
		Prefix kiloPrefix = new Prefix("kilo", "K", 10, 3);
		Prefix milliPrefix = new Prefix("milli", "m", 10, -3);
		
		UnitConverter unitConverter = new UnitConverter();
		unitConverter.registerConversion(unitA, unitB, new BigDecimal("3.5"));
		
		assertEquals(new BigDecimal("3.5"), unitConverter.getConversionFactor(
				new PrefixedUnit(Prefix.BASE, unitA),
				new PrefixedUnit(Prefix.BASE, unitB),
				MathContext.DECIMAL128));
		assertEquals(new BigDecimal("3.5"), unitConverter.getConversionFactor(
				new PrefixedUnit(kiloPrefix, unitA),
				new PrefixedUnit(kiloPrefix, unitB),
				MathContext.DECIMAL128));
		assertEquals(new BigDecimal("3500"), unitConverter.getConversionFactor(
				new PrefixedUnit(Prefix.BASE, unitA),
				new PrefixedUnit(milliPrefix, unitB),
				MathContext.DECIMAL128));
		assertEquals(new BigDecimal("3500"), unitConverter.getConversionFactor(
				new PrefixedUnit(kiloPrefix, unitA),
				new PrefixedUnit(Prefix.BASE, unitB),
				MathContext.DECIMAL128));
		assertEquals(new BigDecimal("3500000"), unitConverter.getConversionFactor(
				new PrefixedUnit(kiloPrefix, unitA),
				new PrefixedUnit(milliPrefix, unitB),
				MathContext.DECIMAL128));
		assertEquals(new BigDecimal("0.0035"), unitConverter.getConversionFactor(
				new PrefixedUnit(milliPrefix, unitA),
				new PrefixedUnit(Prefix.BASE, unitB),
				MathContext.DECIMAL128));
		assertEquals(new BigDecimal("0.0035"), unitConverter.getConversionFactor(
				new PrefixedUnit(Prefix.BASE, unitA),
				new PrefixedUnit(kiloPrefix, unitB),
				MathContext.DECIMAL128));
		assertEquals(new BigDecimal("0.0000035"), unitConverter.getConversionFactor(
				new PrefixedUnit(milliPrefix, unitA),
				new PrefixedUnit(kiloPrefix, unitB),
				MathContext.DECIMAL128));
	}
	
	@Test
	public void testConversionWitRegisteredPrefixUnits() {
		Unit unitA = new Unit("a", 1);
		Unit unitB = new Unit("b", 1);
		
		Prefix kiloPrefix = new Prefix("kilo", "K", 10, 3);
		Prefix milliPrefix = new Prefix("milli", "m", 10, -3);
		
		UnitConverter unitConverter = new UnitConverter();
		unitConverter.registerConversion(
				new PrefixedUnit(kiloPrefix, unitA),
				new PrefixedUnit(milliPrefix, unitB),
				new BigDecimal("3.5"));
		
		assertEquals(new BigDecimal("0.0000035"), unitConverter.getConversionFactor(unitA, unitB, MathContext.DECIMAL128));
	}
	
	@Test
	public void testGetPlural() {
		UnitConverter unitConverter = new UnitConverter()
				.registerUnit(new Unit("meter", 1, "m"));
		
		assertEquals(new Unit("meter", 1, "m"), unitConverter.getUnit("meter"));
		assertEquals(new Unit("meter", 1, "m"), unitConverter.getUnit("meters"));
		assertEquals(new Unit("meter", 1, "m"), unitConverter.getUnit("m"));
		Assert.assertNull(unitConverter.getUnit("ms"));
	}
	
	@Test
	public void testGetUnit() {
		UnitConverter unitConverter = new UnitConverter()
				.registerUnit(new Unit("meter", 1, "m"));
		
		Assert.assertEquals(new Unit("meter", 1), unitConverter.getUnit("Meter"));
		Assert.assertEquals(new Unit("meter", 1), unitConverter.getUnit("METER"));
		Assert.assertEquals(new Unit("meter", 1), unitConverter.getUnit("m"));
	}
	
	@Test
	public void testLoadingDefaults() {
		UnitConverter unitConverter = new UnitConverter();
		ResourceLoader.processResource("units/iec.prefixes", unitConverter::loadPrefix);
		ResourceLoader.processResource("units/si.prefixes", unitConverter::loadPrefix);
		ResourceLoader.processResource("units/default.units", unitConverter::loadUnit);
		ResourceLoader.processResource("units/default.conversions", unitConverter::loadConversion);
		
		assertEquals(new BigDecimal("25.4"), unitConverter.convert("in", "mm", new BigDecimal("1"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("2.54"), unitConverter.convert("in", "cm", new BigDecimal("1"), MathContext.DECIMAL128));
	}
	
	@Test
	public void testPrefixConversionForHigherDimensions() {
		UnitConverter unitConverter = new UnitConverter();
		unitConverter.registerUnit(new Unit("a", 3));
		
		ResourceLoader.processResource("units/si.prefixes", unitConverter::loadPrefix);
		
		assertEquals(new BigDecimal("1000"), unitConverter.convert("a", "ma", new BigDecimal("1"), MathContext.DECIMAL128));
		assertEquals(new BigDecimal("0.001"), unitConverter.convert("a", "ka", new BigDecimal("1"), MathContext.DECIMAL128));
	}
	
	@Test
	public void testSimpleConversion() {
		Unit unitA = new Unit("a", 1);
		Unit unitB = new Unit("b", 1);
		
		UnitConverter unitConverter = new UnitConverter();
		unitConverter.registerConversion(unitA, unitB, new BigDecimal("0.5"));
		
		assertEquals(new BigDecimal("0.5"), unitConverter.getConversionFactor(unitA, unitB, MathContext.DECIMAL128));
		assertEquals(new BigDecimal("2.0"), unitConverter.getConversionFactor(unitB, unitA, MathContext.DECIMAL128));
	}
	
	@Test
	public void testUnitLoading() {
		UnitConverter unitConverter = new UnitConverter();
		
		unitConverter.loadUnit(null);
		unitConverter.loadUnit("");
		
		unitConverter.loadUnit("name 1 alias");
		unitConverter.loadUnit("name2 3 alias1, alias2, alias3");
		
		assertEquals(new Unit("name", 1, "alias"), unitConverter.getUnit("name"));
		assertEquals(new Unit("name", 1, "alias"), unitConverter.getUnit("alias"));
		assertEquals(new Unit("name2", 3, "alias1", "alias2", "alias3"), unitConverter.getUnit("alias2"));
	}
	
	protected void assertEquals(BigDecimal expected, BigDecimal actual) {
		Assert.assertNotNull("expected value, but was null", actual);
		
		if (expected.compareTo(actual) != 0) {
			Assert.fail("expected: <"
					+ expected.toPlainString()
					+ "> but was: <"
					+ actual.toPlainString()
					+ ">");
		}
	}
	
	protected void assertEquals(Unit expected, Unit actual) {
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getExponent(), actual.getExponent());
		Assert.assertEquals(expected.getAliases(), actual.getAliases());
	}
}
