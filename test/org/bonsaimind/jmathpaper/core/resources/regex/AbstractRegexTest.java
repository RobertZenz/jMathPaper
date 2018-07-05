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

package org.bonsaimind.jmathpaper.core.resources.regex;

import java.util.regex.Pattern;

import org.bonsaimind.jmathpaper.core.resources.ResourceLoader;
import org.junit.Assert;
import org.junit.Before;

public abstract class AbstractRegexTest {
	protected Pattern pattern = null;
	
	@Before
	public void setUp() {
		pattern = ResourceLoader.compileRegex(getRegexName());
	}
	
	protected void assertNoMatch(String value) {
		Assert.assertFalse(
				"No match expected for: <" + value + "> but did match.",
				pattern.matcher(value).find());
	}
	
	protected abstract String getRegexName();
}
