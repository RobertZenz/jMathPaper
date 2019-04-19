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

package org.bonsaimind.jmathpaper.uis.tui;

import java.io.PrintWriter;

import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.InvalidExpressionException;
import org.bonsaimind.jmathpaper.core.Paper;
import org.bonsaimind.jmathpaper.core.ui.AbstractPapersUi;
import org.jline.reader.LineReader;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Tui extends AbstractPapersUi {
	protected boolean paperHasChanged = false;
	protected boolean running = true;
	protected PrintWriter writer = null;
	
	public Tui() {
		super();
	}
	
	@Override
	public void quit() {
		running = false;
	}
	
	@Override
	public void run() throws Exception {
		try (Terminal terminal = TerminalBuilder.terminal()) {
			writer = terminal.writer();
			
			printPaper();
			terminal.flush();
			
			LineReader reader = LineReaderBuilder.builder()
					.history(new PaperBasedHistory(this))
					.option(Option.ERASE_LINE_ON_FINISH, true)
					.option(Option.DELAY_LINE_WRAP, true)
					.build();
			
			String previousValue = null;
			
			while (running) {
				try {
					String prompt = "> ";
					
					if (paper != null && paper.isChanged()) {
						prompt = "*" + prompt;
					}
					
					String input = reader.readLine(prompt, null, previousValue);
					previousValue = null;
					
					if (input.trim().length() > 0) {
						if (!tryAsCommand(input)) {
							try {
								evaluate(input);
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
	
	@Override
	protected void currentPaperHasBeenModified() {
		EvaluatedExpression evaluatedExpression = paper.getEvaluatedExpressions().get(paper.getEvaluatedExpressions().size() - 1);
		
		writer.write(evaluatedExpression.format(
				paper.getIdColumnSize(),
				paper.getExpressionColumnSize(),
				paper.getResultColumnSize(),
				paper.getNumberFormat()));
	}
	
	@Override
	protected void currentPaperHasBeenReset() {
		printPaper();
	}
	
	@Override
	protected void currentSelectedPaperHasChanged() {
		printPaper();
	}
	
	protected void printPaper() {
		if (writer != null) {
			writer.write("------------------------------------------------------------\n");
			
			for (Paper paper : papers) {
				if (paper == this.paper) {
					writer.write("> ");
				}
				
				writer.write(getLongPaperTitle(paper));
				
				writer.write("\n");
			}
			
			writer.write("\n");
			
			if (paper != null) {
				if (paper.getNotes() != null && paper.getNotes().trim().length() > 0) {
					writer.write(paper.getNotes());
					writer.write("\n");
					writer.write("\n");
				}
				
				for (EvaluatedExpression evaluatedExpression : paper.getEvaluatedExpressions()) {
					writer.write(evaluatedExpression.format(
							paper.getIdColumnSize(),
							paper.getExpressionColumnSize(),
							paper.getResultColumnSize(),
							paper.getNumberFormat()));
					writer.write("\n");
				}
			}
		}
	}
}
