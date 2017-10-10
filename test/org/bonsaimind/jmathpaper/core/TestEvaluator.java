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

import org.junit.Assert;
import org.junit.Test;

public class TestEvaluator {
	private static final void assertResult(boolean expected, Evaluator evaluator, String expression) throws InvalidExpressionException {
		EvaluatedExpression evaluatedExpression = evaluator.evaluate(expression);
		
		Assert.assertTrue("Expected a boolean result, but evaluated expression is not of type boolean.", evaluatedExpression.isBoolean());
		
		if (expected) {
			Assert.assertEquals(BigDecimal.ONE, evaluatedExpression.getResult());
		} else {
			Assert.assertEquals(BigDecimal.ZERO, evaluatedExpression.getResult());
		}
	}
	
	private static final void assertResult(String expected, Evaluator evaluator, String expression) throws InvalidExpressionException {
		Assert.assertEquals(new BigDecimal(expected), evaluator.evaluate(expression).getResult().setScale(0));
	}
	
	@Test
	public void testBasicExpression() throws InvalidExpressionException {
		assertResult("2", new Evaluator(), "1+1");
		assertResult("680", new Evaluator(), "5*8*(8+9)");
	}
	
	@Test
	public void testBooleans() throws InvalidExpressionException {
		assertResult(true, new Evaluator(), "1==1");
		assertResult(false, new Evaluator(), "1==2");
		assertResult(false, new Evaluator(), "true and false");
	}
	
	@Test
	public void testComments() throws InvalidExpressionException {
		assertResult("0", new Evaluator(), "// Completely empty statement");
		assertResult("0", new Evaluator(), "/* Another empty. */");
		assertResult("2", new Evaluator(), "1 /* Inlined */ + 1");
		assertResult("2", new Evaluator(), "1 + /* Nested // */ 1");
	}
	
	@Test
	public void testlastResultReference() throws InvalidExpressionException {
		Evaluator evaluator = new Evaluator();
		
		assertResult("25", evaluator, "00 + 5*5");
		assertResult("30", evaluator, "00+5");
		assertResult("150", evaluator, "5*00");
	}
	
	@Test
	public void testNumberBases() throws InvalidExpressionException {
		assertResult("32", new Evaluator(), "32");
		assertResult("12", new Evaluator(), "0b1100");
		assertResult("63", new Evaluator(), "0o77");
		assertResult("255", new Evaluator(), "0xff");
	}
	
	@Test
	public void testVariableDefinition() throws InvalidExpressionException {
		Evaluator evaluator = new Evaluator();
		
		assertResult("2", evaluator, "a=2");
		assertResult("12", evaluator, "b=a+10");
		assertResult("22", evaluator, "abc5=b+10");
		assertResult("220", evaluator, "_10=abc5*10");
		
		assertResult("10", evaluator, "test = 10");
		assertResult("10", evaluator, "test");
		assertResult("15", evaluator, "test2 = test + 5");
	}
}
