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
import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.Paper;

public final class Cli {
	private Cli() {
		super();
	}
	
	public final static void run(Arguments arguments) {
		Paper paper = new Paper();
		
		if (arguments.getContext() != null) {
			try {
				paper.loadFrom(arguments.getContext());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		} else {
			// TODO Load the global paper.
		}
		
		EvaluatedExpression evaluatedExpression = paper.evaluate(arguments.getExpression());
		
		if (evaluatedExpression.getErrorMessage() == null) {
			if (!arguments.isPrintResultOnly()) {
				System.out.print(evaluatedExpression.getId());
				System.out.print("\t");
				System.out.print(evaluatedExpression.getExpression());
				System.out.print("\t");
				System.out.print("= ");
			}
			
			System.out.print(evaluatedExpression.getResult().toPlainString());
			
			if (!arguments.isNoNewline()) {
				System.out.println();
			}
		} else {
			System.err.println(evaluatedExpression.getErrorMessage());
		}
	}
}
