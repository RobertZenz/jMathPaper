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

package org.bonsaimind.jmathpaper.cli;

import java.io.IOException;

import org.bonsaimind.jmathpaper.Arguments;
import org.bonsaimind.jmathpaper.Configuration;
import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.Paper;

public final class Cli {
	private Cli() {
		// No instancing required.
	}
	
	public final static void run(Arguments arguments) {
		Paper paper = new Paper();
		
		try {
			if (arguments.getContext() != null) {
				paper.loadFrom(arguments.getContext());
			} else if (arguments.hasFiles()) {
				paper.loadFrom(arguments.getFiles().get(arguments.getFiles().size() - 1));
			} else {
				paper.loadFrom(Configuration.getGlobalPaperFile());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		EvaluatedExpression evaluatedExpression = paper.evaluate(arguments.getExpression());
		
		try {
			paper.store();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (evaluatedExpression == null) {
			// Nothing to do here, everything is oh-kay.
		} else if (evaluatedExpression.getErrorMessage() == null) {
			if (!arguments.isPrintResultOnly()) {
				System.out.print(paper.toString().trim());
			} else {
				System.out.print(evaluatedExpression.getResult().toPlainString());
			}
			
			if (!arguments.isNoNewline()) {
				System.out.println();
			}
		} else {
			System.err.println(evaluatedExpression.getErrorMessage());
		}
	}
}
