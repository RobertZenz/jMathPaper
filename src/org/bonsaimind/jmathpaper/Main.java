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

import org.bonsaimind.jmathpaper.cli.Cli;
import org.bonsaimind.jmathpaper.core.ui.Ui;
import org.bonsaimind.jmathpaper.swt.Swt;
import org.bonsaimind.jmathpaper.tui.Tui;

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
		
		if ((arguments.getExpression() != null && !arguments.hasFiles())
				|| arguments.useCli()) {
			ui = new Cli();
		}
		
		if (arguments.useTui()) {
			ui = new Tui();
		}
		
		if (arguments.useSwt() || ui == null) {
			ui = new Swt();
		}
		
		try {
			ui.init();
			ui.run(arguments);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
