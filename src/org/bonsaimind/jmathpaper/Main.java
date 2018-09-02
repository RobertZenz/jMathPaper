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

import org.bonsaimind.jmathpaper.core.ConfigurationProcessor;
import org.bonsaimind.jmathpaper.core.configuration.Configuration;
import org.bonsaimind.jmathpaper.core.configuration.Definitions;
import org.bonsaimind.jmathpaper.core.resources.ResourceLoader;
import org.bonsaimind.jmathpaper.core.ui.Ui;
import org.bonsaimind.jmathpaper.core.ui.UiLoader;

import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

public final class Main {
	private Main() {
		// No instancing required.
	}
	
	public static final void main(String[] args) {
		Arguments arguments = null;
		
		try {
			arguments = CommandLine.populateCommand(new Arguments(), args);
		} catch (ParameterException e) {
			System.out.println(e.getMessage());
			System.out.println();
			CommandLine.usage(new Arguments(), System.out);
			
			System.exit(2);
		}
		
		if (arguments.isHelpRequested()) {
			CommandLine.usage(arguments, System.out);
			return;
		}
		
		if (arguments.isVersionRequested()) {
			System.out.println("jMathPaper " + Version.CURRENT);
			return;
		}
		
		Configuration.init();
		
		Ui ui = null;
		
		if (arguments.getUi() != null) {
			try {
				ui = UiLoader.getUi(arguments.getUi());
			} catch (Exception e) {
				System.out.println("Given UI \"" + arguments.getUi() + "\" could not be loaded, cause:");
				System.out.println(e.toString());
				System.exit(1);
			}
		} else {
			if ((arguments.getExpression() == null || arguments.hasFiles())) {
				try {
					ui = UiLoader.getUi("swt");
				} catch (Exception e) {
					// Ignore the exception, as the SWT UI might not be included
					// in the jar that we run.
					
					try {
						ui = UiLoader.getUi("tui");
					} catch (Exception e2) {
						// Ignore the exception, as the SWT UI might not be
						// included in the jar that we run.
					}
				}
				
				if (ui == null) {
				}
			}
		}
		
		if (ui == null) {
			try {
				ui = UiLoader.getUi("cli");
			} catch (Exception e) {
				System.out.println("Failed to load any UI, please specify one with the --ui=UI parameter.");
				System.exit(1);
			}
		}
		
		try {
			ui.init();
			
			ui.setDefaultDefinitions(createDefaultDefinitions(arguments));
			
			if (arguments.hasFiles()) {
				for (Path file : arguments.getFiles()) {
					ui.open(file);
				}
			}
			
			if (ui.getPaper() == null) {
				if (arguments.getExpression() != null) {
					ui.open(Configuration.getGlobalPaperFile());
				} else {
					ui.new_();
				}
			}
			
			if (arguments.getExpression() != null) {
				ui.process(arguments.getExpression());
			}
			
			ui.run(arguments);
		} catch (Exception e) {
			System.out.println("Failed to run UI.");
			System.out.println(e.toString());
			System.exit(1);
		}
	}
	
	/**
	 * Creates the default {@link Definitions} file from {@link Configuration}
	 * and {@link Arguments}.
	 * 
	 * @param arguments The {@link Arguments} to use.
	 * @return The {@link Definitions} file created from the
	 *         {@link Configuration} and {@link Arguments}.
	 */
	private static final Definitions createDefaultDefinitions(Arguments arguments) {
		Definitions definitions = new Definitions();
		
		// Aliases
		ResourceLoader.processResource("other/default.aliases", definitions::addAliasDefinition);
		ConfigurationProcessor.process(Configuration.getUserAliasesFile(), definitions::addAliasDefinition);
		for (Path aliasesFile : arguments.getAliasesFiles()) {
			ConfigurationProcessor.process(aliasesFile, definitions::addAliasDefinition);
		}
		
		// Prefixes
		ResourceLoader.processResource("units/si.prefixes", definitions::addPrefixDefinition);
		ResourceLoader.processResource("units/iec.prefixes", definitions::addPrefixDefinition);
		ConfigurationProcessor.process(Configuration.getUserPrefixesFile(), definitions::addPrefixDefinition);
		for (Path prefixesFile : arguments.getPrefixesFiles()) {
			ConfigurationProcessor.process(prefixesFile, definitions::addPrefixDefinition);
		}
		
		// Units
		ResourceLoader.processResource("units/default.units", definitions::addUnitDefinition);
		ConfigurationProcessor.process(Configuration.getUserUnitsFile(), definitions::addUnitDefinition);
		for (Path unitsFile : arguments.getUnitsFiles()) {
			ConfigurationProcessor.process(unitsFile, definitions::addUnitDefinition);
		}
		
		// Conversions
		ResourceLoader.processResource("units/default.conversions", definitions::addConversionDefinition);
		ConfigurationProcessor.process(Configuration.getUserConversionsFile(), definitions::addConversionDefinition);
		for (Path conversionsFile : arguments.getConversionsFiles()) {
			ConfigurationProcessor.process(conversionsFile, definitions::addConversionDefinition);
		}
		
		return definitions;
	}
}
