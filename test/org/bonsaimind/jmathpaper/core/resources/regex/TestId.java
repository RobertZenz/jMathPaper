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
import java.util.regex.Pattern;

import org.bonsaimind.jmathpaper.core.resources.ResourceLoader;
import org.junit.Assert;
import org.junit.Test;

public class TestId {
	private static final Pattern ID_PATTERN = ResourceLoader.compileRegex("id");
	
	private static final void assertMatch(String expectedId, String expectedExpression, String value) {
		Matcher matcher = ID_PATTERN.matcher(value);
		
		Assert.assertTrue(
				"Match expected for: <" + value + "> but did not match.",
				matcher.find());
		
		Assert.assertEquals(
				"Expected ID not found.",
				expectedId,
				matcher.group("ID"));
		Assert.assertEquals(
				"Expected expression not found.",
				expectedExpression,
				matcher.group("EXPRESSION"));
	}
	
	private static final void assertNoMatch(String value) {
		Assert.assertFalse(
				"No match expected for: <" + value + "> but did match.",
				ID_PATTERN.matcher(value).find());
	}
	
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
		
		assertMatch("a", "1+1", "a=1+1");
		assertMatch("a", " \t 1 + 1", "a \t\t  \t  = \t 1 + 1");
		assertMatch("abCC76", "1+1", "abCC76=1+1");
		
		assertMatch("_", "1", "_=1");
		assertMatch("_0012", "1", "_0012=1");
	}
}
