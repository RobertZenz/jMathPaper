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

package org.bonsaimind.jmathpaper.tui;

import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.ui.AbstractPapersUi;
import org.bonsaimind.jmathpaper.core.ui.CommandProcessor;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Tui extends AbstractPapersUi {
	private boolean running = true;
	
	public Tui() {
		super();
	}
	
	@Override
	public void quit() {
		running = false;
	}
	
	@Override
	protected void internalStart() throws Exception {
		try (Terminal terminal = TerminalBuilder.terminal()) {
			if (paper.getNotes() != null && paper.getNotes().trim().length() > 0) {
				terminal.writer().write(paper.getNotes());
				terminal.writer().write("\n");
				terminal.writer().write("\n");
			}
			
			for (EvaluatedExpression evaluatedExpression : paper.getEvaluatedExpressions()) {
				terminal.writer().write(evaluatedExpression.toString(
						paper.getIdColumnSize(),
						paper.getExpressionColumnSize(),
						paper.getResultColumnSize()));
				terminal.writer().write("\n");
			}
			
			LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();
			
			while (running) {
				String input = reader.readLine("> ");
				
				if (input.trim().length() > 0) {
					if (!CommandProcessor.applyCommand(this, input)) {
						EvaluatedExpression evaluatedExpression = paper.evaluate(input);
						
						terminal.writer().write(evaluatedExpression.toString(
								paper.getIdColumnSize(),
								paper.getExpressionColumnSize(),
								paper.getResultColumnSize()));
						terminal.writer().write("\n");
						terminal.flush();
					}
				}
			}
		} catch (UserInterruptException e) {
			// Everything okay, just exit.
			return;
		}
	}
}
