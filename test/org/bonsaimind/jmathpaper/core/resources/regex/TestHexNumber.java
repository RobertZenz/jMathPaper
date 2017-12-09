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

import org.junit.Test;

public class TestHexNumber extends AbstractValueTest {
	@Test
	public void test() {
		assertNoMatch("");
		assertNoMatch("abcd");
		assertNoMatch("1+1");
		assertNoMatch("sin(45) * 34 - 2");
		
		assertNoMatch("0x");
		assertNoMatch("1xo01");
		assertNoMatch("oxo01");
		assertNoMatch("0x0a7g");
		
		assertMatch("05acf", "0x05acf");
		assertMatch("05acf", "0x05acf*7");
		assertMatch("05acf", "1+0x05acf*7");
		assertMatch("05acf", "a+sin(0x05acf)*y");
	}
	
	@Override
	protected String getRegexName() {
		return "hex-number";
	}
}
