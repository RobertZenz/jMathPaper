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

import org.bonsaimind.jmathpaper.Arguments;
import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.InvalidExpressionException;
import org.bonsaimind.jmathpaper.core.ui.AbstractPapersUi;
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
			new_();
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
	public void run(Arguments arguments) throws Exception {
		super.run(arguments);
		
		try (Terminal terminal = TerminalBuilder.terminal()) {
			writer = terminal.writer();
			
			printPaper();
			
			ClearingLineReader reader = new ClearingLineReader(terminal);
			reader.setHistory(new PaperBasedHistory(this));
			
			String previousValue = null;
			
			while (running) {
				try {
					String input = reader.readLine("> ", null, previousValue);
					previousValue = null;
					
					if (input.trim().length() > 0) {
						if (!tryAsCommand(input)) {
							try {
								evaluate(input);
								
								EvaluatedExpression evaluatedExpression = paper.getEvaluatedExpressions().get(paper.getEvaluatedExpressions().size() - 1);
								
								writer.write(evaluatedExpression.format(
										paper.getIdColumnSize(),
										paper.getExpressionColumnSize(),
										paper.getResultColumnSize()));
							} catch (InvalidExpressionException e) {
								writer.write(e.getCause().getMessage());
								
								previousValue = input;
							}
							
							writer.write("\n");
						}
					}
				} catch (UserInterruptException e) {
					running = false;
				} catch (Exception e) {
					writer.write(e.getMessage());
					writer.write("\n");
				}
				
				terminal.flush();
			}
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
				writer.write(evaluatedExpression.format(
						paper.getIdColumnSize(),
						paper.getExpressionColumnSize(),
						paper.getResultColumnSize()));
				writer.write("\n");
			}
		}
	}
	
	@Override
	protected void reevaluate() throws InvalidExpressionException {
		super.reevaluate();
		
		printPaper();
	}
}
