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

package org.bonsaimind.jmathpaper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public class Arguments {
	@Option(names = { "-h", "--help" }, description = "Displays this help.", usageHelp = true)
	private boolean helpRequested = false;
	
	private List<String> readonlyUnnamedParameters = null;
	
	@Parameters(description = "The files to open.", paramLabel = "FILES")
	private String[] unnamedParameters = null;
	
	public List<String> getUnnamedParameters() {
		if (readonlyUnnamedParameters == null) {
			if (unnamedParameters == null || unnamedParameters.length == 0) {
			} else {
				readonlyUnnamedParameters = Collections.unmodifiableList(Arrays.asList(unnamedParameters));
			}
		}
		
		return readonlyUnnamedParameters;
	}
	
	public boolean isHelpRequested() {
		return helpRequested;
	}
	
}
