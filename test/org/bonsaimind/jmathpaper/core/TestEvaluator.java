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

import org.bonsaimind.jmathpaper.core.evaluatedexpressions.BooleanEvaluatedExpression;
import org.junit.Assert;
import org.junit.Test;

public class TestEvaluator {
	private static final void assertEquals(BigDecimal expected, BigDecimal actual) {
		if (expected.compareTo(actual) != 0) {
			Assert.fail("expected: <"
					+ expected.toPlainString()
					+ "> but was: <"
					+ actual.toPlainString()
					+ ">");
		}
	}
	
	private static final void assertExpression(
			String expectedId,
			boolean expectedResult,
			String expression,
			Evaluator evaluator) throws InvalidExpressionException {
		EvaluatedExpression evaluatedExpression = evaluator.evaluate(expression);
		
		Assert.assertEquals(expectedId, evaluatedExpression.getId());
		Assert.assertEquals(expression, evaluatedExpression.getExpression());
		
		Assert.assertTrue(
				"Expected a BooleanEvaluatedExpression, but got: <" + evaluatedExpression.getClass().getName() + ">",
				evaluatedExpression instanceof BooleanEvaluatedExpression);
		
		Assert.assertEquals(
				Boolean.valueOf(expectedResult),
				((BooleanEvaluatedExpression)evaluatedExpression).getBooleanResult());
	}
	
	private static final void assertExpression(
			String expectedId,
			String expectedResult,
			String expression,
			Evaluator evaluator) throws InvalidExpressionException {
		EvaluatedExpression evaluatedExpression = evaluator.evaluate(expression);
		
		Assert.assertEquals(expectedId, evaluatedExpression.getId());
		Assert.assertEquals(expression, evaluatedExpression.getExpression());
		assertEquals(new BigDecimal(expectedResult), evaluatedExpression.getResult());
	}
	
	private static final void assertResult(boolean expected, String expression, Evaluator evaluator) throws InvalidExpressionException {
		EvaluatedExpression evaluatedExpression = evaluator.evaluate(expression);
		
		Assert.assertTrue(
				"Expected a BooleanEvaluatedExpression, but got: <" + evaluatedExpression.getClass().getName() + ">",
				evaluatedExpression instanceof BooleanEvaluatedExpression);
		
		Assert.assertEquals(
				Boolean.valueOf(expected),
				((BooleanEvaluatedExpression)evaluatedExpression).getBooleanResult());
	}
	
	private static final void assertResult(String expected, String expression, Evaluator evaluator) throws InvalidExpressionException {
		BigDecimal expectedBigDecimal = new BigDecimal(expected);
		BigDecimal actualBigDecimal = evaluator.evaluate(expression).getResult();
		
		assertEquals(expectedBigDecimal, actualBigDecimal);
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
		assertResult(false, "true and false", new Evaluator());
	}
	
	@Test
	public void testComments() throws InvalidExpressionException {
		assertResult("0", "// Completely empty statement", new Evaluator());
		assertResult("0", "/* Another empty. */", new Evaluator());
		assertResult("2", "1 /* Inlined */ + 1", new Evaluator());
		assertResult("2", "1 + /* Nested // */ 1", new Evaluator());
	}
	
	@Test
	public void testlastResultReference() throws InvalidExpressionException {
		Evaluator evaluator = new Evaluator();
		
		assertResult("25", "00 + 5*5", evaluator);
		assertResult("30", "00+5", evaluator);
		assertResult("150", "5*00", evaluator);
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
	public void testVariableDefinition() throws InvalidExpressionException {
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
