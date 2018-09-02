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

import org.bonsaimind.jmathpaper.core.resources.ResourceLoader;
import org.junit.Test;

public class TestEvaluator extends AbstractExpressionTest {
	@Test
	public void testAliases() throws InvalidExpressionException {
		Evaluator evaluator = new Evaluator();
		
		// Test error behavior (should do nothing)
		evaluator.loadAlias(null);
		evaluator.loadAlias("");
		evaluator.loadAlias("   \t\t  ");
		evaluator.loadAlias("something");
		
		// Actual
		evaluator.loadAlias("alias *450");
		evaluator.loadAlias("and &&");
		evaluator.loadAlias("value 8 + 8 + 5");
		
		assertResult("900", "2 alias", evaluator);
		assertResult(true, "true and true", evaluator);
		assertResult("93", "10 * value", evaluator);
	}
	
	@Test
	public void testBasicExpression() throws InvalidExpressionException {
		assertResult("2", "1+1", new Evaluator());
		assertResult("680", "5*8*(8+9)", new Evaluator());
	}
	
	@Test
	public void testBooleans() throws InvalidExpressionException {
		assertResult(true, "1==1", new Evaluator());
		assertResult(false, "1==2", new Evaluator());
		assertResult(false, "true && false", new Evaluator());
	}
	
	@Test
	public void testComments() throws InvalidExpressionException {
		assertResult("0", "// Completely empty statement", new Evaluator());
		assertResult("0", "/* Another empty. */", new Evaluator());
		assertResult("2", "1 /* Inlined */ + 1", new Evaluator());
		assertResult("2", "1 + /* Nested // */ 1", new Evaluator());
	}
	
	@Test
	public void testFunctions() throws InvalidExpressionException {
		Evaluator evaluator = new Evaluator();
		
		assertFunction("func", "a+5", "func(a)=a+5", evaluator);
		assertResult("25", "func(20)", evaluator);
		
		// Redefine
		assertFunction("func", "a+10", "func(a)=a+10", evaluator);
		assertResult("30", "func(20)", evaluator);
		
		// Variable usage inside functions
		assertExpression("var", "1", "var=1", evaluator);
		assertFunction("func", "a+var", "func(a)=a+var", evaluator);
		assertResult("2", "func(1)", evaluator);
		
		// Parameters override variables
		assertExpression("var", "1", "var=1", evaluator);
		assertFunction("func", "var + 100", "func(var)=var + 100", evaluator);
		assertResult("200", "func(100)", evaluator);
		
		// Function usage inside function
		assertFunction("funcA", "5", "funcA()=5", evaluator);
		assertFunction("funcB", "funcA() + 1", "funcB()=funcA() + 1", evaluator);
		assertResult("6", "funcB()", evaluator);
		
		// Boolean support
		assertFunction("bool", "a && b", "bool(a, b)=a && b", evaluator);
		assertResult(false, "bool(true, false)", evaluator);
		assertResult(true, "bool(true, true)", evaluator);
	}
	
	@Test
	public void testLastResultReference() throws InvalidExpressionException {
		Evaluator evaluator = new Evaluator();
		
		assertResult("25", "00 + 5*5", evaluator);
		assertResult("30", "00+5", evaluator);
		assertResult("150", "5*00", evaluator);
		
		// Inside functions.
		evaluator.evaluate("add(a, b)=a+b");
		evaluator.evaluate("25");
		
		assertResult("50", "add(00, 25)", evaluator);
		assertResult("75", "add(25, 00)", evaluator);
	}
	
	@Test
	public void testNumberBases() throws InvalidExpressionException {
		assertResult("32", "32", new Evaluator());
		assertResult("12", "0b1100", new Evaluator());
		assertResult("63", "0o77", new Evaluator());
		assertResult("255", "0xff", new Evaluator());
	}
	
	@Test
	public void testPrecision() throws InvalidExpressionException {
		assertResult("123456790", "123456789+1", new Evaluator());
		assertResult("123456789123456790", "123456789123456789+1", new Evaluator());
		assertResult("1.000000001", "1.000000+0.000000001", new Evaluator());
	}
	
	@Test
	public void testUnitConversions() throws InvalidExpressionException {
		Evaluator evaluator = new Evaluator();
		
		// Load the defaults
		ResourceLoader.processResource("units/iec.prefixes", evaluator.getUnitConverter()::loadPrefix);
		ResourceLoader.processResource("units/si.prefixes", evaluator.getUnitConverter()::loadPrefix);
		ResourceLoader.processResource("units/default.units", evaluator.getUnitConverter()::loadUnit);
		ResourceLoader.processResource("units/default.conversions", evaluator.getUnitConverter()::loadConversion);
		
		// Basic support
		assertResult("2.54", "1inch to centimeter", evaluator);
		assertResult("2.54", "1in to cm", evaluator);
		assertResult("2.54", "1in in cm", evaluator);
		assertResult("2.54", "1in cm", evaluator);
		
		assertResult("1", "1 m to m", evaluator);
		assertResult("1", "1 km to km", evaluator);
		assertResult("1", "1 meter to meter", evaluator);
		assertResult("1", "1 kilometer to kilometer", evaluator);
		
		assertResult("1000", "1 km to m", evaluator);
		assertResult("1000", "1 kilometer to meter", evaluator);
		assertResult("1000", "1 km to meter", evaluator);
		assertResult("1", "1000 m to kilometer", evaluator);
		
		// No unit
		assertResult("1", "1 1 to 1", evaluator);
		assertResult("1000", "1 k1 to 1", evaluator);
		assertResult("0.001", "1 1 to k1", evaluator);
		
		// Expression support
		assertResult("0.0175", "5*5*70 centimeter to kilometer", evaluator);
		assertResult("2.7432", "30/10 * sqrt(9) ft to meter", evaluator);
		
		// Plural
		assertResult("1", "1000meters to kilometers", evaluator);
		
		// Prefix only
		assertResult("1000", "1kilo to 1", evaluator);
		assertResult("1000", "1k to 1", evaluator);
		assertResult("1000000", "1mega to 1", evaluator);
		assertResult("1000000", "1MEGA to 1", evaluator);
		assertResult("0.001", "1milli to 1", evaluator);
		assertResult("0.000001", "1milli to k", evaluator);
		assertResult("0.000001", "1milli to kilo", evaluator);
	}
	
	@Test
	public void testVariables() throws InvalidExpressionException {
		Evaluator evaluator = new Evaluator();
		
		assertExpression("#1", "2", "1+1", evaluator);
		assertExpression("#2", "4", "2+2", evaluator);
		assertExpression("#3", "6", "3+3", evaluator);
		
		assertExpression("a", "2", "a=2", evaluator);
		assertExpression("b", "12", "b=a+10", evaluator);
		assertExpression("abc5", "22", "abc5=b+10", evaluator);
		assertExpression("_10", "220", "_10=abc5*10", evaluator);
		
		assertExpression("test", "10", "test = 10", evaluator);
		assertExpression("#4", "10", "test", evaluator);
		assertExpression("test2", "15", "test2 = test + 5", evaluator);
	}
}
