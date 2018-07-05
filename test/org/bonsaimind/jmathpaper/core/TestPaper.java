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

import java.math.RoundingMode;

import org.junit.Assert;
import org.junit.Test;

public class TestPaper extends AbstractExpressionTest {
	@Test
	public void restReevaluate() throws InvalidExpressionException {
		Paper paper = new Paper();
		
		paper.reevaluate();
		
		paper.evaluate("a=1+1");
		paper.evaluate("b=2+2");
		paper.evaluate("c=3+3");
		paper.evaluate("a+b+c");
		
		paper.reevaluate();
	}
	
	@Test
	public void testClear() throws InvalidExpressionException {
		Paper paper = new Paper();
		assertExpression("#1", "2", "1+1", paper.evaluate("1+1"));
		assertExpression("#2", "4", "2+2", paper.evaluate("2+2"));
		assertExpression("#3", "6", "3+3", paper.evaluate("3+3"));
		
		paper.clear();
		
		assertExpression("#1", "2", "1+1", paper.evaluate("1+1"));
		assertExpression("#2", "4", "2+2", paper.evaluate("2+2"));
		assertExpression("#3", "6", "3+3", paper.evaluate("3+3"));
	}
	
	@Test
	public void testCorrectNumbering() throws InvalidExpressionException {
		Paper paper = new Paper();
		assertExpression("#1", "2", "1+1", paper.evaluate("1+1"));
		assertExpression("b", "4", "b=2+2", paper.evaluate("b=2+2"));
		assertExpression("c", "6", "c=3+3", paper.evaluate("c=3+3"));
		assertExpression("#2", "8", "4+4", paper.evaluate("4+4"));
		assertExpression("#3", "10", "5+5", paper.evaluate("5+5"));
	}
	
	@Test
	public void testFunctionLoading() throws InvalidExpressionException {
		Paper paper = new Paper();
		paper.evaluate("a(x)=x+1");
		paper.evaluate("b(x)=x+2");
		paper.evaluate("a(x)=x+3");
		
		Paper loadedPaper = new Paper();
		loadedPaper.evaluateFromText(paper.toString());
		
		assertExpression("#1", "4", "a(1)", paper.evaluate("a(1)"));
		assertExpression("#2", "3", "b(1)", paper.evaluate("b(1)"));
	}
	
	@Test
	public void testSetPrecision() throws InvalidExpressionException {
		Paper paper = new Paper();
		paper.evaluate("1/3");
		
		assertExpression("#1", "0.33333333333333333333333333333333", "1/3", paper.getEvaluatedExpressions().get(0));
		
		paper.setPrecision(5);
		Assert.assertEquals(5, paper.getPrecision());
		paper.reevaluate();
		
		assertExpression("#1", "0.33333", "1/3", paper.getEvaluatedExpressions().get(0));
		
		paper.setPrecision(0);
		
		try {
			paper.reevaluate();
			Assert.fail("1/3 should not evaluate with infinite precision.");
		} catch (InvalidExpressionException e) {
			// Supposed to fail, because 1/3 cannot be expressed if infinite
			// precision.
		}
	}
	
	@Test
	public void testSetRoundingMode() throws InvalidExpressionException {
		Paper paper = new Paper();
		paper.evaluate("9 / 4");
		
		assertExpression("#1", "2.25", "9 / 4", paper.getEvaluatedExpressions().get(0));
		
		paper.setPrecision(2);
		Assert.assertEquals(2, paper.getPrecision());
		paper.setRoundingMode(RoundingMode.CEILING);
		Assert.assertEquals(RoundingMode.CEILING, paper.getRoundingMode());
		paper.reevaluate();
		
		assertExpression("#1", "2.3", "9 / 4", paper.getEvaluatedExpressions().get(0));
		
		paper.setRoundingMode(RoundingMode.FLOOR);
		Assert.assertEquals(RoundingMode.FLOOR, paper.getRoundingMode());
		paper.reevaluate();
		
		assertExpression("#1", "2.2", "9 / 4", paper.getEvaluatedExpressions().get(0));
	}
	
	@Test
	public void testToFromStringSanity() throws InvalidExpressionException {
		Paper paper = new Paper();
		paper.evaluate("1+1");
		paper.evaluate("#1+5");
		paper.evaluate("#1+8");
		paper.setNotes("Some test text.");
		
		Paper secondPaper = new Paper();
		secondPaper.evaluateFromText(paper.toString());
		
		Assert.assertEquals(paper.toString(), secondPaper.toString());
	}
	
	@Test
	public void testVariabeLoading() throws InvalidExpressionException {
		Paper paper = new Paper();
		paper.evaluate("a=1");
		paper.evaluate("b=2");
		paper.evaluate("c=3");
		paper.evaluate("b=4");
		
		Paper loadedPaper = new Paper();
		loadedPaper.evaluateFromText(paper.toString());
		
		assertExpression("#1", "1", "a", paper.evaluate("a"));
		assertExpression("#2", "4", "b", paper.evaluate("b"));
		assertExpression("#3", "3", "c", paper.evaluate("c"));
	}
}
