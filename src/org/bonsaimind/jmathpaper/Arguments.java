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

package org.bonsaimind.jmathpaper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public class Arguments {
	@Option(names = { "--aliases" }, paramLabel = "ALIASESFILE", description = "Load aliases from this file.")
	private List<String> aliasesFiles = null;
	
	private List<Path> aliasesFilesPaths = null;
	
	@Option(names = { "-c", "--context" }, paramLabel = "FILE", arity = "1", description = "The paper to use as context for a given expression.")
	private String context = null;
	
	private Path contextPath = null;
	
	@Option(names = { "--conversions" }, paramLabel = "CONVERSIONSFILE", description = "Load unit conversions from this file.")
	private List<String> conversionsFiles = null;
	
	private List<Path> conversionsFilesPaths = null;
	
	private String expression = null;
	
	@Parameters(paramLabel = "EXPRESSION", description = "The expression to evaluate. By default the expression will be evaluated on the command line, no UI will be started.")
	private List<String> expressionParts = null;
	
	@Option(names = { "-o", "--open" }, paramLabel = "FILE", description = "Opens the given paper. This starts the UI by default.")
	private List<String> files = null;
	
	private List<Path> filesPaths = null;
	
	@Option(names = { "-h", "--help" }, description = "Displays this help.", usageHelp = true)
	private boolean helpRequested = false;
	
	@Option(names = { "-n", "--no-newline" }, description = "Omit a trailing new line when printing things. Only applicable for the CLI or Service TUI.")
	private boolean noNewline = false;
	
	@Option(names = { "--prefixes" }, paramLabel = "PREFIXESFILE", description = "Load prefixes from this file.")
	private List<String> prefixesFiles = null;
	
	private List<Path> prefixesFilesPaths = null;
	
	@Option(names = { "-p", "--print-only", "--print-result-only" }, description = "Print only the result of the given expression. Only applicable for the CLI or Service TUI.")
	private boolean printResultOnly = false;
	
	@Option(names = { "-u", "--ui" }, arity = "1", description = "Define what user interface (UI) to start.")
	private String ui = null;
	
	@Option(names = { "--units" }, paramLabel = "UNITSFILE", description = "Load units from this file.")
	private List<String> unitsFiles = null;
	
	private List<Path> unitsFilesPaths = null;
	
	@Option(names = { "--version" }, description = "Prints the version information.")
	private boolean versionRequested = false;
	
	public List<Path> getAliasesFiles() {
		if (aliasesFilesPaths == null) {
			aliasesFilesPaths = convertStringsToPaths(aliasesFiles);
		}
		
		return aliasesFilesPaths;
	}
	
	public Path getContext() {
		if (contextPath == null && context != null) {
			contextPath = Paths.get(context);
		}
		
		return contextPath;
	}
	
	public List<Path> getConversionsFiles() {
		if (conversionsFilesPaths == null) {
			conversionsFilesPaths = convertStringsToPaths(conversionsFiles);
		}
		
		return conversionsFilesPaths;
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
			filesPaths = convertStringsToPaths(files);
		}
		
		return filesPaths;
	}
	
	public List<Path> getPrefixesFiles() {
		if (prefixesFilesPaths == null) {
			prefixesFilesPaths = convertStringsToPaths(prefixesFiles);
		}
		
		return prefixesFilesPaths;
	}
	
	public String getUi() {
		return ui;
	}
	
	public List<Path> getUnitsFiles() {
		if (unitsFilesPaths == null) {
			unitsFilesPaths = convertStringsToPaths(unitsFiles);
		}
		
		return unitsFilesPaths;
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
	
	protected List<Path> convertStringsToPaths(List<String> strings) {
		if (strings == null || strings.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<Path> paths = new ArrayList<>();
		
		for (String item : strings) {
			paths.add(Paths.get(item));
		}
		
		return Collections.unmodifiableList(paths);
	}
}
