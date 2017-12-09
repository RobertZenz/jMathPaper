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

public class TestBinaryNumber extends AbstractValueTest {
	@Test
	public void test() {
		assertNoMatch("");
		assertNoMatch("abcd");
		assertNoMatch("1+1");
		assertNoMatch("sin(45) * 34 - 2");
		
		assertNoMatch("0b");
		assertNoMatch("10b01");
		assertNoMatch("b0b01");
		assertNoMatch("0b012");
		
		assertMatch("101", "0b101");
		assertMatch("101", "0b101*7");
		assertMatch("101", "1+0b101*7");
		assertMatch("101", "a+sin(0b101)*y");
	}
	
	@Override
	protected String getRegexName() {
		return "binary-number";
	}
}
