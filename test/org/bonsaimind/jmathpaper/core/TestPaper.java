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

public class TestPaper {
	@Test
	public void testToFromStringSanity() {
		Paper paper = new Paper();
		
		try {
			paper.evaluate("1+1");
			paper.evaluate("#1+5");
			paper.evaluate("#1+8");
		} catch (InvalidExpressionException e) {
			Assert.fail(e.getCause().getMessage());
		}
		
		paper.setNotes("Some test text.");
		
		Paper secondPaper = new Paper();
		secondPaper.fromString(paper.toString());
		
		Assert.assertEquals(paper.toString(), secondPaper.toString());
	}
}
