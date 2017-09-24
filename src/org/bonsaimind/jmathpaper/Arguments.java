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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public class Arguments {
	
	@Option(names = { "-c", "--context" }, paramLabel = "FILE", arity = "1", description = "The paper to use as context for a given expression.")
	private String context = null;
	
	private Path contextPath = null;
	
	private String expression = null;
	
	@Parameters(paramLabel = "EXPRESSION", description = "The expression to evaluate. By default the expression will be evaluated on the command line, no UI will be started.")
	private List<String> expressionParts = null;
	
	@Option(names = { "-o", "--open" }, paramLabel = "FILE", description = "Opens the given paper. This starts the UI by default.")
	private List<String> files = null;
	
	private List<Path> filesPaths = null;
	
	@Option(names = { "-h", "--help" }, description = "Displays this help.", usageHelp = true)
	private boolean helpRequested = false;
	
	@Option(names = { "-n", "--no-newline" }, description = "Omit a trailing new line when printing things.")
	private boolean noNewline = false;
	
	@Option(names = { "-p", "--print-only", "--print-result-only" }, description = "Print only the result of the given expression.")
	private boolean printResultOnly = false;
	
	@Option(names = { "-u", "--ui" }, arity = "0..1", description = "Define what user interface (UI) to start.")
	private String ui = null;
	
	@Option(names = { "--version" }, description = "Prints the version information.")
	private boolean versionRequested = false;
	
	public Path getContext() {
		if (contextPath == null && context != null) {
			contextPath = Paths.get(context);
		}
		
		return contextPath;
	}
	
	public String getExpression() {
		if (expression == null
				&& expressionParts != null
				&& !expressionParts.isEmpty()) {
			StringBuilder expressionBuilder = new StringBuilder();
			
			for (String expressionPart : expressionParts) {
				expressionBuilder.append(expressionPart);
				expressionBuilder.append(" ");
			}
			
			expression = expressionBuilder.toString().trim();
		}
		
		return expression;
	}
	
	public List<Path> getFiles() {
		if (filesPaths == null && hasFiles()) {
			filesPaths = new ArrayList<>();
			
			for (String file : files) {
				filesPaths.add(Paths.get(file));
			}
		}
		
		return filesPaths;
	}
	
	public boolean hasFiles() {
		return files != null && !files.isEmpty();
	}
	
	public boolean isHelpRequested() {
		return helpRequested;
	}
	
	public boolean isNoNewline() {
		return noNewline;
	}
	
	public boolean isPrintResultOnly() {
		return printResultOnly;
	}
	
	public boolean isVersionRequested() {
		return versionRequested;
	}
	
	public boolean useCli() {
		return "c".equalsIgnoreCase(ui)
				|| "cli".equalsIgnoreCase(ui);
	}
	
	public boolean useSwt() {
		return "s".equalsIgnoreCase(ui)
				|| "swt".equalsIgnoreCase(ui);
	}
	
	public boolean useTui() {
		return "t".equalsIgnoreCase(ui)
				|| "tui".equalsIgnoreCase(ui);
	}
}
