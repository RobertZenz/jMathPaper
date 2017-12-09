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

public class TestOctalNumber extends AbstractValueTest {
	@Test
	public void test() {
		assertNoMatch("");
		assertNoMatch("abcd");
		assertNoMatch("1+1");
		assertNoMatch("sin(45) * 34 - 2");
		
		assertNoMatch("0o");
		assertNoMatch("10o01");
		assertNoMatch("o0o01");
		assertNoMatch("0o078");
		
		assertMatch("177", "0o177");
		assertMatch("177", "0o177*7");
		assertMatch("177", "1+0o177*7");
		assertMatch("177", "a+sin(0o177)*y");
	}
	
	@Override
	protected String getRegexName() {
		return "octal-number";
	}
}
