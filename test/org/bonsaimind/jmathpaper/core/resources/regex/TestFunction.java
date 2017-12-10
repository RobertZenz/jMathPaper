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

package org.bonsaimind.jmathpaper.core.resources.regex;

import java.util.regex.Matcher;

import org.junit.Assert;
import org.junit.Test;

public class TestFunction extends AbstractRegexTest {
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
		
		assertMatch("func", null, "1+1", "func()=1+1");
		assertMatch("func", "a", "a+1+1", "func(a)=a+1+1");
		assertMatch("func", "a, b, c", "a+b+c+1+1", "func(a, b, c)=a+b+c+1+1");
		assertMatch("func", "  \ta   , \t b\t, c   ", "a+b+c+1+1", "func   (  \ta   , \t b\t, c   )  \t =a+b+c+1+1");
		
		assertMatch("func", "_aBc_ds", "_aBc_ds+1+1", "func(_aBc_ds)=_aBc_ds+1+1");
	}
	
	protected void assertMatch(String expectedId, String expectedParameters, String expectedExpression, String value) {
		Matcher matcher = pattern.matcher(value);
		
		Assert.assertTrue(
				"Match expected for: <" + value + "> but did not match.",
				matcher.find());
		
		Assert.assertEquals(
				"Expected ID not found.",
				expectedId,
				matcher.group("ID"));
		Assert.assertEquals(
				"Expected parameters not found.",
				expectedParameters,
				matcher.group("PARAMETERS"));
		Assert.assertEquals(
				"Expected expression not found.",
				expectedExpression,
				matcher.group("EXPRESSION"));
	}
	
	@Override
	protected String getRegexName() {
		return "function";
	}
}
