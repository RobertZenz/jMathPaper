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

import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.junit.Assert;
import org.junit.Test;

public class TestEvaluatedExpression {
	@Test
	public void testFromString() {
		Assert.assertNull(EvaluatedExpression.fromString(null));
		Assert.assertNull(EvaluatedExpression.fromString(""));
		Assert.assertNull(EvaluatedExpression.fromString("invalid"));
		
		Assert.assertEquals(
				new EvaluatedExpression("1", "1+1", new BigDecimal("2"), true, null),
				EvaluatedExpression.fromString("1\t1+1\t2"));
		
		Assert.assertEquals(
				new EvaluatedExpression("1", "1+1", BigDecimal.ZERO, false, "error"),
				EvaluatedExpression.fromString("1\t1+1\terror"));
		
		Assert.assertEquals(
				new EvaluatedExpression("1", "1+1", new BigDecimal("2"), true, null),
				EvaluatedExpression.fromString("1   \t  \t  1+1 \t    2"));
	}
	
	@Test
	public void testToFromStringSanity() {
		EvaluatedExpression validEvaluatedExpression = new EvaluatedExpression("1", "1+1", new BigDecimal("2"), true, null);
		Assert.assertEquals(validEvaluatedExpression, EvaluatedExpression.fromString(validEvaluatedExpression.toString()));
		
		EvaluatedExpression invalidEvaluatedExpression = new EvaluatedExpression("1", "1+1", BigDecimal.ZERO, false, "error");
		Assert.assertEquals(invalidEvaluatedExpression, EvaluatedExpression.fromString(invalidEvaluatedExpression.toString()));
	}
	
	@Test
	public void testToString() {
		Assert.assertEquals(
				"1\t1+1\t2",
				new EvaluatedExpression("1", "1+1", new BigDecimal("2"), true, null).toString());
		
		Assert.assertEquals(
				"1\t1+1\terror",
				new EvaluatedExpression("1", "1+1", BigDecimal.ZERO, false, "error").toString());
	}
}
