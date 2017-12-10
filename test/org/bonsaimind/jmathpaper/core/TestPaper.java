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

import org.junit.Assert;
import org.junit.Test;

public class TestPaper extends AbstractExpressionTest {
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
