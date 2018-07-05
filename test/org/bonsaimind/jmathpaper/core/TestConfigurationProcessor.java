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

package org.bonsaimind.jmathpaper.core;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

public class TestConfigurationProcessor {
	@Test
	public void testComments() {
		assertProcess("First line\nSecond line\n", "# This is a comment\n"
				+ "First line # with a comment\n"
				+ "# Another comment line\n"
				+ "Second line\n", "\n");
	}
	
	@Test
	public void testEmpty() {
		assertProcess("", "", null);
	}
	
	@Test
	public void testLineEnding() {
		assertProcess("Allinoneline.", "All\n in\n one\n line.", null);
		assertProcess("All-in-one-line.-", "All\n in\n one\n line.", "-");
	}
	
	@Test
	public void testNullArguments() {
		ConfigurationProcessor.process(null, null);
		ConfigurationProcessor.process(null, null, null);
	}
	
	@Test
	public void testWhitespaceTreatment() {
		assertProcess("None", "   \t \t  None    \t \t", null);
		assertProcess("None", "   \t \t  None    \t # Comment \t", null);
		assertProcess("", "   \t \t      \t # Comment \t", null);
	}
	
	private final void assertProcess(String expected, String configurationInput, String lineEnding) {
		StringBuilder readConfiguration = new StringBuilder();
		
		ConfigurationProcessor.process(
				new ByteArrayInputStream(configurationInput.getBytes(StandardCharsets.UTF_8)),
				readConfiguration::append,
				lineEnding);
		
		Assert.assertEquals(expected, readConfiguration.toString());
	}
}
