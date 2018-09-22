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

public class TestUnitConversionSimple extends AbstractRegexTest {
	@Test
	public void test() {
		assertNoMatch("");
		assertNoMatch("1+1");
		assertNoMatch("sin(45) * 34 - 2");
		
		assertNoMatch("=1");
		assertNoMatch("0=1");
		assertNoMatch("a-r=1");
		assertNoMatch("--er=2");
		
		assertNoMatch("a=5");
		
		assertMatch("", "abcd", "abcd");
		assertMatch("1", "km", "1km");
		assertMatch("1*8 + abcd", "km", "1*8 + abcd km");
		assertMatch("abcd", "km", "abcd km");
	}
	
	protected void assertMatch(String expectedExpression, String expectedFrom, String value) {
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
	}
	
	@Override
	protected String getRegexName() {
		return "unit-conversion-simple";
	}
}
