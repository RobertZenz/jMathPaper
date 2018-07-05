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

package org.bonsaimind.jmathpaper.core.resources.regex;

import java.util.regex.Matcher;

import org.junit.Assert;
import org.junit.Test;

public class TestUnitConversion extends AbstractRegexTest {
	@Test
	public void test() {
		assertNoMatch("");
		assertNoMatch("abcd");
		assertNoMatch("1+1");
		assertNoMatch("sin(45) * 34 - 2");
		
		assertNoMatch("=1");
		assertNoMatch("0=1");
		assertNoMatch("a-r=1");
		assertNoMatch("--er=2");
		
		assertNoMatch("a=5");
		
		assertMatch("1", "1", "1", "1 1 to 1");
		assertMatch("1", "1", "1", "1 1 in 1");
		assertMatch("1", "1", "1", "1 1 1");
		assertMatch("1", "k1", "1", "1 k1 to 1");
		assertMatch("1", "k1", "1", "1 k1 in 1");
		assertMatch("1", "k1", "1", "1 k1 1");
		assertMatch("1", "1", "k1", "1 1 to k1");
		assertMatch("1", "1", "k1", "1 1 in k1");
		assertMatch("1", "1", "k1", "1 1 k1");
		
		assertMatch("sin(45) * sqrt(50) + 49 - 10", "1", "1", "sin(45) * sqrt(50) + 49 - 10 1 to 1");
		assertMatch("sin(45) * sqrt(50) + 49 - 10", "1", "1", "sin(45) * sqrt(50) + 49 - 10 1 in 1");
		assertMatch("sin(45) * sqrt(50) + 49 - 10", "1", "1", "sin(45) * sqrt(50) + 49 - 10 1 1");
		
		assertMatch("1", "km", "m", "1 km to m");
		assertMatch("1+1", "km", "m", "1+1 km to m");
		assertMatch("(a*5) + b - test", "km", "m", "(a*5) + b - test km to m");
		
		assertMatch("1", "km", "m", "1 km in m");
		assertMatch("1+1", "km", "m", "1+1 km in m");
		assertMatch("(a*5) + b - test", "km", "m", "(a*5) + b - test km in m");
		
		assertMatch("5", "1^2", "1^2", "5 1^2 to 1^2");
		assertMatch("5", "km^2", "mi^2", "5 km^2 to mi^2");
		assertMatch("5", "m^3", "cm^3", "5 m^3 to cm^3");
	}
	
	protected void assertMatch(String expectedExpression, String expectedFrom, String expectedTo, String value) {
		Matcher matcher = pattern.matcher(value);
		
		Assert.assertTrue(
				"Match expected for: <" + value + "> but did not match.",
				matcher.find());
		
		Assert.assertEquals(
				"Expected EXPRESSION not found.",
				expectedExpression,
				matcher.group("EXPRESSION"));
		Assert.assertEquals(
				"Expected FROM not found.",
				expectedFrom,
				matcher.group("FROM"));
		Assert.assertEquals(
				"Expected TO not found.",
				expectedTo,
				matcher.group("TO"));
	}
	
	@Override
	protected String getRegexName() {
		return "unit-conversion";
	}
}
