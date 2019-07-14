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

import org.bonsaimind.jmathpaper.core.evaluatedexpressions.BooleanEvaluatedExpression;
import org.bonsaimind.jmathpaper.core.evaluatedexpressions.FunctionEvaluatedExpression;
import org.junit.Assert;

public abstract class AbstractExpressionTest {
	protected void assertEquals(BigDecimal expected, BigDecimal actual) {
		if (expected.compareTo(actual) != 0) {
			Assert.fail("expected: <"
					+ expected.toPlainString()
					+ "> but was: <"
					+ actual.toPlainString()
					+ ">");
		}
	}
	
	protected void assertException(Class<? extends Exception> expectedException, String expression, Evaluator evaluator) throws Exception {
		try {
			evaluator.evaluate(expression);
		} catch (Exception e) {
			if (!expectedException.isAssignableFrom(e.getClass())) {
				throw e;
			}
		}
	}
	
	protected void assertExpression(
			String expectedId,
			boolean expectedResult,
			String expression,
			EvaluatedExpression evaluatedExpression) throws InvalidExpressionException {
		Assert.assertNotNull(evaluatedExpression);
		Assert.assertEquals(expectedId, evaluatedExpression.getId());
		Assert.assertEquals(expression, evaluatedExpression.getExpression());
		
		Assert.assertTrue(
				"Expected a BooleanEvaluatedExpression, but got: <" + evaluatedExpression.getClass().getName() + ">",
				evaluatedExpression instanceof BooleanEvaluatedExpression);
		
		Assert.assertEquals(
				Boolean.valueOf(expectedResult),
				((BooleanEvaluatedExpression)evaluatedExpression).getBooleanResult());
	}
	
	protected void assertExpression(
			String expectedId,
			boolean expectedResult,
			String expression,
			Evaluator evaluator) throws InvalidExpressionException {
		assertExpression(expectedId, expectedResult, expression, evaluator.evaluate(expression));
	}
	
	protected void assertExpression(
			String expectedId,
			String expectedResult,
			String expression,
			EvaluatedExpression evaluatedExpression) throws InvalidExpressionException {
		Assert.assertNotNull(evaluatedExpression);
		Assert.assertEquals(expectedId, evaluatedExpression.getId());
		Assert.assertEquals(expression, evaluatedExpression.getExpression());
		assertEquals(new BigDecimal(expectedResult), evaluatedExpression.getResult());
	}
	
	protected void assertExpression(
			String expectedId,
			String expectedResult,
			String expression,
			Evaluator evaluator) throws InvalidExpressionException {
		assertExpression(expectedId, expectedResult, expression, evaluator.evaluate(expression));
	}
	
	protected void assertFail(String expression, Evaluator evaluator) throws InvalidExpressionException {
		try {
			evaluator.evaluate(expression);
			
			Assert.fail("Expression \" + expression + \" is supposed to fail, but did not.");
		} catch (InvalidExpressionException e) {
			// Okay, continue.
		}
	}
	
	protected void assertFunction(
			String expectedId,
			String expectedBody,
			EvaluatedExpression evaluatedExpression) throws InvalidExpressionException {
		Assert.assertTrue(
				"Expected a FunctionEvaluatedExpression, but got: <" + evaluatedExpression.getClass().getName() + ">",
				evaluatedExpression instanceof FunctionEvaluatedExpression);
		
		Assert.assertEquals(
				Boolean.TRUE,
				((FunctionEvaluatedExpression)evaluatedExpression).getBooleanResult());
		
		Assert.assertEquals(
				expectedBody,
				((FunctionEvaluatedExpression)evaluatedExpression).getBody());
	}
	
	protected void assertFunction(
			String expectedId,
			String expectedBody,
			String expression,
			Evaluator evaluator) throws InvalidExpressionException {
		assertFunction(expectedId, expectedBody, evaluator.evaluate(expression));
	}
	
	protected void assertResult(boolean expected, EvaluatedExpression evaluatedExpression) throws InvalidExpressionException {
		Assert.assertTrue(
				"Expected a BooleanEvaluatedExpression, but got: <" + evaluatedExpression.getClass().getName() + ">",
				evaluatedExpression instanceof BooleanEvaluatedExpression);
		
		Assert.assertEquals(
				Boolean.valueOf(expected),
				((BooleanEvaluatedExpression)evaluatedExpression).getBooleanResult());
	}
	
	protected void assertResult(boolean expected, String expression) throws InvalidExpressionException {
		assertResult(expected, expression, new Evaluator());
	}
	
	protected void assertResult(boolean expected, String expression, Evaluator evaluator) throws InvalidExpressionException {
		assertResult(expected, evaluator.evaluate(expression));
	}
	
	protected void assertResult(String expected, BigDecimal actualResult) throws InvalidExpressionException {
		assertEquals(new BigDecimal(expected), actualResult);
	}
	
	protected void assertResult(String expected, String expression) throws InvalidExpressionException {
		assertResult(expected, expression, new Evaluator());
	}
	
	protected void assertResult(String expected, String expression, Evaluator evaluator) throws InvalidExpressionException {
		assertResult(expected, evaluator.evaluate(expression).getResult());
	}
}
