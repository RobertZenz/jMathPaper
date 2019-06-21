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

import org.junit.Assert;
import org.junit.Test;

public class TestEvaluatorInternals extends Evaluator {
	@Test
	public void testStripComments() {
		Assert.assertNull(stripComments(null));
		Assert.assertEquals("", stripComments(""));
		Assert.assertEquals("   ", stripComments("   "));
		Assert.assertEquals("abc", stripComments("abc"));
		Assert.assertEquals("abc ", stripComments("abc // something"));
		Assert.assertEquals("a  b  c  d ", stripComments("a /* test */ b /* // test */ c /* /* /* test */ d // Test"));
		Assert.assertEquals("abc ", stripComments("abc /* Malformed but should not throw error."));
	}
}
