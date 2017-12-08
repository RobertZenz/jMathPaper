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
import org.bonsaimind.jmathpaper.core.evaluatedexpressions.NumberEvaluatedExpression;
import org.junit.Assert;
import org.junit.Test;

public class TestEvaluatedExpression {
	@Test
	public void testToString() {
		Assert.assertEquals(
				"1 1+1 = 2",
				new NumberEvaluatedExpression("1", "1+1", new BigDecimal("2")).toString());
		
		Assert.assertEquals(
				"1 1>0 = true",
				new BooleanEvaluatedExpression("1", "1>0", true).toString());
	}
}
