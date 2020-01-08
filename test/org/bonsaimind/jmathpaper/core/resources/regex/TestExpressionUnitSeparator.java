/*
 * Copyright 2018, Robert 'Bobby' Zenz
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

public class TestExpressionUnitSeparator extends AbstractRegexTest {
	@Test
	public void testNoMatch() {
		assertNoMatch("");
		assertNoMatch("1");
		assertNoMatch("12346433");
		assertNoMatch("something");
		assertNoMatch("321414+something");
		assertNoMatch("(5*6)+9^power");
		
		// abc1 is a valid variable name.
		assertNoMatch("abc1");
		
		// log10() should not be recognized as unit.
		assertNoMatch("log10(5)");
	}
	
	@Test
	public void testOne() {
		assertMatch(2, "1+1 1");
		assertMatch(2, "abc 1");
		assertMatch(12, "sqrt(5*abc+1)1");
		assertMatch(12, "sqrt(5*abc+1) 1");
		assertMatch(12, "sqrt(5*abc+1) 1/km");
	}
	
	@Test
	public void testUnits() {
		assertMatch(2, "1+1 km");
		assertMatch(2, "1+1km");
		assertMatch(2, "abc km");
		assertMatch(12, "sqrt(5*abc+1)km");
		assertMatch(12, "sqrt(5*abc+1) km");
		assertMatch(12, "sqrt(5*abc+1) km/km");
		assertMatch(0, "1inch to centimeter");
	}
	
	protected void assertMatch(int expectedIndex, String value) {
		Matcher matcher = pattern.matcher(value);
		
		Assert.assertTrue(
				"Match expected for: <" + value + "> but did not match.",
				matcher.find());
		
		Assert.assertEquals(
				expectedIndex,
				matcher.start());
	}
	
	@Override
	protected String getRegexName() {
		return "expression-unit-separator";
	}
}
