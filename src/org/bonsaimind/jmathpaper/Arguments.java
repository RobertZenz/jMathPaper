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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonsaimind.jmathpaper.core.ui.UiParameters;

import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public class Arguments {
	@Option(names = { "--aliases" }, paramLabel = "ALIASESFILE", description = "Load aliases from this file.")
	private List<String> aliasesFiles = null;
	
	private List<Path> aliasesFilesPaths = null;
	
	@Option(names = { "-c", "--config", "--configuration", "--config-dir",
			"--configuration-directory" }, paramLabel = "CONFIGDIR", description = "Specify the directory for the configuration files.")
	private String configurationDirectory = null;
	
	private Path configurationDirectoryPath = null;
	
	@Option(names = { "--context" }, paramLabel = "CONTEXTFILE", description = "Load context expressions from this file.")
	private List<String> contextExpressionsFiles = null;
	
	private List<Path> contextExpressionsFilesPaths = null;
	
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
	
	@Option(names = { "-t", "--template" }, paramLabel = "", arity = "1", description = "The template to use for new papers.")
	private String paperTemplateFile = null;
	
	private Path paperTemplateFilePath = null;
	
	@Option(names = { "--prefixes" }, paramLabel = "PREFIXESFILE", description = "Load prefixes from this file.")
	private List<String> prefixesFiles = null;
	
	private List<Path> prefixesFilesPaths = null;
	
	@Option(names = { "-u", "--ui" }, arity = "1", description = ""
			+ "Define what user interface (UI) to start."
			+ " The given parameter can either be a fully qualified classname, or a class- and packagename relative to the \"org.bonsaimind.jmathpaper.uis\" package."
			+ " By default you can use \"cli\", \"service\", \"tui\", \"tui2\" and \"gui\" to start the different UIs. For further descriptions please see the README.")
	private String ui = null;
	
	private UiParameters uiParameters = null;
	
	@Option(names = { "-p", "--uiparam", "--uiparameter" }, description = "Define a parameter which will be passed to the UI.")
	private List<String> uiParametersStrings = null;
	
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
	
	public Path getConfigurationDirectory() {
		if (configurationDirectoryPath == null && configurationDirectory != null && !configurationDirectory.trim().isEmpty()) {
			configurationDirectoryPath = Paths.get(configurationDirectory);
		}
		
		return configurationDirectoryPath;
	}
	
	public List<Path> getContextExpressionsFiles() {
		if (contextExpressionsFilesPaths == null) {
			contextExpressionsFilesPaths = convertStringsToPaths(contextExpressionsFiles);
		}
		
		return contextExpressionsFilesPaths;
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
	
	public Path getPaperTemplateFile() {
		if (paperTemplateFilePath == null) {
			if (paperTemplateFile != null) {
				paperTemplateFilePath = Paths.get(paperTemplateFile);
			}
		}
		
		return paperTemplateFilePath;
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
	
	public UiParameters getUiParameters() {
		if (uiParameters == null) {
			Map<String, String> parameters = new HashMap<>();
			
			if (uiParametersStrings != null && !uiParametersStrings.isEmpty()) {
				for (String uiParameterString : uiParametersStrings) {
					if (uiParameterString != null && !uiParameterString.trim().isEmpty()) {
						int firstColonIndex = uiParameterString.indexOf(":");
						
						if (firstColonIndex > 0) {
							parameters.put(
									uiParameterString.substring(0, firstColonIndex).trim(),
									uiParameterString.substring(firstColonIndex + 1));
						} else {
							parameters.put(uiParameterString, Boolean.TRUE.toString());
						}
					}
				}
			}
			
			uiParameters = new UiParameters(parameters);
		}
		
		return uiParameters;
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
