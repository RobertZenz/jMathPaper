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

package org.bonsaimind.jmathpaper.cli;

import java.io.IOException;

import org.bonsaimind.jmathpaper.Arguments;
import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.ui.AbstractPapersUi;

public class Cli extends AbstractPapersUi {
	public Cli() {
		super();
	}
	
	@Override
	public void clear() {
		super.clear();
		
		try {
			save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void quit() {
		// Nothing to do here.
	}
	
	@Override
	public void run(Arguments arguments) throws Exception {
		super.run(arguments);
		
		if (arguments.getExpression() != null) {
			save();
			
			if (!arguments.isPrintResultOnly()) {
				System.out.print(paper.toString().trim());
			} else {
				EvaluatedExpression evaluatedExpression = paper.getEvaluatedExpressions().get(paper.getEvaluatedExpressions().size() - 1);
				System.out.print(evaluatedExpression.getFormattedResult(paper.getNumberFormat()));
			}
			
			if (!arguments.isNoNewline()) {
				System.out.println();
			}
		} else {
			if (!arguments.isPrintResultOnly()) {
				System.out.print(paper.toString().trim());
			}
			
			if (!arguments.isNoNewline()) {
				System.out.println();
			}
		}
	}
}
