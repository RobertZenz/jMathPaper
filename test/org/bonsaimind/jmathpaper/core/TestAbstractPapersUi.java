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

package org.bonsaimind.jmathpaper.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bonsaimind.jmathpaper.core.evaluatedexpressions.BooleanEvaluatedExpression;
import org.bonsaimind.jmathpaper.core.ui.AbstractPapersUi;
import org.bonsaimind.jmathpaper.core.ui.CommandExecutionException;
import org.bonsaimind.jmathpaper.core.ui.UiParameters;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestAbstractPapersUi extends AbstractPapersUi {
	private volatile boolean quitCalled = false;
	
	public TestAbstractPapersUi() {
		super();
	}
	
	@Override
	public void quit() {
		quitCalled = true;
	}
	
	@Override
	public void run() throws Exception {
		// Nothing to do.
	}
	
	@Before
	public void setup() throws Exception {
		init(new UiParameters(Collections.emptyMap()));
		run();
		
		new_();
	}
	
	@Test
	public void testCommandAdd() throws CommandExecutionException, InvalidExpressionException {
		process("add unit meter 1 m");
		process("add unit unittest 1");
		process("add prefix much m 3 3");
		process("add conversion unittest 5 meter");
		
		process("5muchunittest m");
		
		assertLastResult("675");
	}
	
	@Test
	public void testCommandAlias() throws CommandExecutionException, InvalidExpressionException {
		process("alias noway !=");
		
		process("true noway false");
		assertLastResult(true);
		
		process("true noway true");
		assertLastResult(false);
	}
	
	@Test
	public void testCommandQuit() throws CommandExecutionException, InvalidExpressionException {
		process("quit");
		
		Assert.assertTrue(quitCalled);
	}
	
	@Test
	public void testSplitParameters() {
		assertSplitParameters(new String[] {}, null);
		assertSplitParameters(new String[] {}, "");
		assertSplitParameters(new String[] {}, "     ");
		
		assertSplitParameters(new String[] { "param" }, "param");
		assertSplitParameters(new String[] { "param" }, "    param   ");
		assertSplitParameters(new String[] { "param" }, "  \"param\"  ");
		assertSplitParameters(new String[] {
				"param1",
				"param2",
				"param3"
		}, "param1  param2  param3");
		assertSplitParameters(new String[] {
				"par  am1",
				"pa r a m 2",
				"par\"am3"
		}, "   par\\ \\ am1   \"pa r a m 2\"  \"par\\\"am3\"");
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
	
	private final void assertLastResult(boolean expected) {
		EvaluatedExpression lastEvaluatedExpression = paper.evaluatedExpressions.get(paper.evaluatedExpressions.size() - 1);
		
		if (lastEvaluatedExpression instanceof BooleanEvaluatedExpression) {
			Assert.assertEquals(
					Boolean.valueOf(expected),
					((BooleanEvaluatedExpression)lastEvaluatedExpression).getBooleanResult());
		}
	}
	
	private final void assertLastResult(String expected) {
		EvaluatedExpression lastEvaluatedExpression = paper.evaluatedExpressions.get(paper.evaluatedExpressions.size() - 1);
		
		Assert.assertEquals(
				new BigDecimal(expected).stripTrailingZeros(),
				lastEvaluatedExpression.getResult().stripTrailingZeros());
	}
	
	private final void assertLists(String[] expected, List<String> actual) {
		Assert.assertEquals(expected.length, actual.size());
		
		for (int index = 0; index < expected.length; index++) {
			Assert.assertEquals(expected[index], actual.get(index));
		}
	}
	
	private final void assertSplitParameters(String[] expected, String input) {
		assertLists(expected, splitParameters(input));
	}
	
	private final void assertSplitStatements(String[] expected, String input) {
		List<String> actualList = new ArrayList<>();
		splitStatements(input).forEach(actualList::add);
		
		assertLists(expected, actualList);
	}
}
