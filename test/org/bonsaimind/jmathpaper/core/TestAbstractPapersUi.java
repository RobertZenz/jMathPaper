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

import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jmathpaper.core.ui.AbstractPapersUi;
import org.junit.Assert;
import org.junit.Test;

public class TestAbstractPapersUi extends AbstractPapersUi {
	
	public TestAbstractPapersUi() {
		super();
	}
	
	@Override
	public void quit() {
	}
	
	@Test
	public void testSplitStatements() {
		assertSplitStatements(new String[] {}, null);
		assertSplitStatements(new String[] {}, "");
		assertSplitStatements(new String[] {}, "     ");
		assertSplitStatements(new String[] {}, "   ;  ;\"  \" ;; ;  ");
		
		assertSplitStatements(new String[] { "1+1" }, "1+1");
		assertSplitStatements(new String[] { "1+1", "2+2" }, "1+1;2+2");
		assertSplitStatements(new String[] { "1+1;1+1" }, "1+1\\;1+1");
		assertSplitStatements(new String[] { "1+1;1+1" }, "\"1+1;1+1\"");
		assertSplitStatements(new String[] { "streq(\"a\", \"b\")" }, "streq(\"a\", \"b\")");
		
		assertSplitStatements(new String[] {
				"command value",
				"1+1",
				"command \"some ; value 1+1\" ; 2+2"
		}, "   command value    ; 1+1; command \"some ; value 1+1\" \\; 2+2");
	}
	
	private final void assertSplitStatements(String[] expected, String input) {
		List<String> actualList = new ArrayList<>();
		splitStatements(input).forEach(actualList::add);
		
		Assert.assertEquals(expected.length, actualList.size());
		
		for (int index = 0; index < expected.length; index++) {
			Assert.assertEquals(expected[index], actualList.get(index));
		}
	}
}
