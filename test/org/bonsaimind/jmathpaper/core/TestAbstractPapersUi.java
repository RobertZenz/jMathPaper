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
import org.bonsaimind.jmathpaper.core.resources.ResourceLoader;
import org.bonsaimind.jmathpaper.core.ui.AbstractPapersUi;
import org.bonsaimind.jmathpaper.core.ui.CommandExecutionException;
import org.bonsaimind.jmathpaper.core.ui.UiParameters;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestAbstractPapersUi extends AbstractPapersUi {
	private String clipboard = "";
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
	public void testCommandCopy() throws CommandExecutionException, InvalidExpressionException {
		process("1+1");
		process("2+2");
		process("3+3");
		process("4+4");
		process("5+5");
		
		// Lines by ID
		
		process("copy #1");
		assertClipboard("#1 1+1 = 2");
		
		process("copy #1, #2, #3");
		assertClipboard("#1 1+1 = 2\n#2 2+2 = 4\n#3 3+3 = 6");
		
		process("copy #1..#3");
		assertClipboard("#1 1+1 = 2\n#2 2+2 = 4\n#3 3+3 = 6");
		
		// Lines by index.
		
		process("copy 1");
		assertClipboard("#1 1+1 = 2");
		
		process("copy -1");
		assertClipboard("#5 5+5 = 10");
		
		process("copy 1, 2, 3");
		assertClipboard("#1 1+1 = 2\n#2 2+2 = 4\n#3 3+3 = 6");
		
		process("copy 1..3");
		assertClipboard("#1 1+1 = 2\n#2 2+2 = 4\n#3 3+3 = 6");
		
		process("copy 1..-3");
		assertClipboard("#1 1+1 = 2\n#2 2+2 = 4\n#3 3+3 = 6");
		
		// IDs
		process("copy ID 1..-3");
		assertClipboard("#1\n#2\n#3");
		
		// Expression
		process("copy exp 1..-3");
		assertClipboard("1+1\n2+2\n3+3");
		
		// Result
		process("copy res 1..-3");
		assertClipboard("2\n4\n6");
		
		// Copy everything
		process("copy exp");
		assertClipboard("1+1\n2+2\n3+3\n4+4\n5+5");
	}
	
	@Test
	public void testCommandNote() throws CommandExecutionException, InvalidExpressionException {
		process("note add 1");
		process("note add 2");
		process("note add 3");
		process("note add 4");
		assertNote("1\n2\n3\n4\n");
		
		process("note insert 1 First");
		assertNote("First\n1\n2\n3\n4\n");
		
		process("note delete 3");
		assertNote("First\n1\n3\n4\n");
		
		process("note clear");
		assertNote("");
	}
	
	@Test
	public void testCommandQuit() throws CommandExecutionException, InvalidExpressionException {
		process("quit");
		
		Assert.assertTrue(quitCalled);
	}
	
	@Test
	public void testCompoundUnitConversions() throws CommandExecutionException, InvalidExpressionException {
		// Load the defaults
		ResourceLoader.processResource("units/iec.prefixes", getPaper().getEvaluator().getUnitConverter()::loadPrefix);
		ResourceLoader.processResource("units/si.prefixes", getPaper().getEvaluator().getUnitConverter()::loadPrefix);
		ResourceLoader.processResource("units/default.units", getPaper().getEvaluator().getUnitConverter()::loadUnit);
		ResourceLoader.processResource("units/default.conversions", getPaper().getEvaluator().getUnitConverter()::loadConversion);
		
		process("1m/sec to km/h");
		assertLastResult("3.6");
		
		process("1l/sec/m^2 to usfloz/hour/in^2");
		assertLastResult("78.535637590755700991921464362409");
		
		process("1 km/h m/h");
		assertLastResult("1000");
		
		process("2 km/h m/h");
		assertLastResult("2000");
		
		process("1+1 * 8 / 2 + 10km/h m/h");
		assertLastResult("15000");
		
		process("km/h");
		assertLastResult("1000");
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
	
	@Test
	public void testUnitConversions() throws CommandExecutionException, InvalidExpressionException {
		// Load the defaults
		ResourceLoader.processResource("units/iec.prefixes", getPaper().getEvaluator().getUnitConverter()::loadPrefix);
		ResourceLoader.processResource("units/si.prefixes", getPaper().getEvaluator().getUnitConverter()::loadPrefix);
		ResourceLoader.processResource("units/default.units", getPaper().getEvaluator().getUnitConverter()::loadUnit);
		ResourceLoader.processResource("units/default.conversions", getPaper().getEvaluator().getUnitConverter()::loadConversion);
		
		process("1km to m");
		assertLastResult("1000");
		
		process("1km in m");
		assertLastResult("1000");
		
		process("1km as m");
		assertLastResult("1000");
		
		process("1km m");
		assertLastResult("1000");
		
		process("1km");
		assertLastResult("1000");
		
		process("km");
		assertLastResult("1000");
	}
	
	@Override
	protected void copyToClipboard(String value) {
		clipboard = value;
	}
	
	private final void assertClipboard(String expected) {
		Assert.assertEquals(expected, clipboard);
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
	
	private final void assertNote(String expected) {
		Assert.assertEquals(expected, paper.getNotes());
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
