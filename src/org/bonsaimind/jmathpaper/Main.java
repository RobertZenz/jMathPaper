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

import org.bonsaimind.jmathpaper.core.DynamicLoader;
import org.bonsaimind.jmathpaper.core.ui.Ui;

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
				ui = DynamicLoader.getUi(arguments.getUi());
			} catch (Exception e) {
				System.out.println("Given UI \"" + arguments.getUi() + "\" could not be loaded, cause:");
				System.out.println(e.toString());
				System.exit(1);
			}
		} else {
			if ((arguments.getExpression() == null || arguments.hasFiles())) {
				try {
					ui = DynamicLoader.getUi("swt");
				} catch (Exception e) {
					// Ignore the exception, as the SWT UI might not be included
					// in the jar that we run.
					
					try {
						ui = DynamicLoader.getUi("tui");
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
				ui = DynamicLoader.getUi("cli");
			} catch (Exception e) {
				System.out.println("Failed to load any UI, please specify one with the --ui=UI parameter.");
				System.exit(1);
			}
		}
		
		try {
			ui.init();
			ui.run(arguments);
		} catch (Exception e) {
			System.out.println("Failed to run UI.");
			System.out.println(e.toString());
			System.exit(1);
		}
	}
}
