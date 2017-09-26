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
import java.io.PrintWriter;
import java.nio.file.Path;

import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.ui.AbstractPapersUi;
import org.bonsaimind.jmathpaper.core.ui.CommandProcessor;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Tui extends AbstractPapersUi {
	protected boolean running = true;
	protected PrintWriter writer = null;
	
	public Tui() {
		super();
	}
	
	@Override
	public void close() {
		super.close();
		
		if (paper == null) {
			try {
				initDefaultPaper();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			printPaper();
		}
	}
	
	@Override
	public void open(Path file) throws IOException {
		super.open(file);
		
		printPaper();
	}
	
	@Override
	public void quit() {
		running = false;
	}
	
	@Override
	public void reload() throws IOException {
		super.reload();
		
		printPaper();
	}
	
	@Override
	protected void internalStart() throws Exception {
		try (Terminal terminal = TerminalBuilder.terminal()) {
			writer = terminal.writer();
			
			printPaper();
			
			LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();
			
			while (running) {
				String input = reader.readLine("> ");
				
				if (input.trim().length() > 0) {
					if (!CommandProcessor.applyCommand(this, input)) {
						EvaluatedExpression evaluatedExpression = paper.evaluate(input);
						
						writer.write(evaluatedExpression.toString(
								paper.getIdColumnSize(),
								paper.getExpressionColumnSize(),
								paper.getResultColumnSize()));
						writer.write("\n");
						terminal.flush();
					}
				}
			}
		} catch (UserInterruptException e) {
			// Everything okay, just exit.
			return;
		}
	}
	
	protected void printPaper() {
		if (writer != null && paper != null) {
			int paperWidth = paper.getIdColumnSize() + paper.getExpressionColumnSize() + paper.getResultColumnSize() + 4;
			
			for (int counter = 0; counter < paperWidth; counter++) {
				writer.write("-");
			}
			writer.write("\n");
			
			if (paper.getFile() != null) {
				writer.write(paper.getFile().toAbsolutePath().toString());
				writer.write("\n");
				writer.write("\n");
			}
			
			if (paper.getNotes() != null && paper.getNotes().trim().length() > 0) {
				writer.write(paper.getNotes());
				writer.write("\n");
				writer.write("\n");
			}
			
			for (EvaluatedExpression evaluatedExpression : paper.getEvaluatedExpressions()) {
				writer.write(evaluatedExpression.toString(
						paper.getIdColumnSize(),
						paper.getExpressionColumnSize(),
						paper.getResultColumnSize()));
				writer.write("\n");
			}
		}
	}
}
