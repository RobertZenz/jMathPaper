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

import java.io.IOException;

import org.bonsaimind.jmathpaper.Arguments;
import org.bonsaimind.jmathpaper.Configuration;
import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.Paper;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public final class Tui {
	private Tui() {
		// No instance required.
	}
	
	public static final void run(Arguments arguments) {
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
		
		try (Terminal terminal = TerminalBuilder.terminal()) {
			for (EvaluatedExpression evaluatedExpression : paper.getEvaluatedExpressions()) {
				terminal.writer().write(evaluatedExpression.toString());
				terminal.writer().write("\n");
			}
			terminal.flush();
			
			boolean running = true;
			
			LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();
			
			while (running) {
				String line = reader.readLine("> ");
				
				EvaluatedExpression evaluatedExpression = paper.evaluate(line);
				
				terminal.writer().write(evaluatedExpression.toString());
				terminal.writer().write("\n");
				terminal.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserInterruptException e) {
			// Everything okay, just exit.
			return;
		}
	}
}
