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

package org.bonsaimind.jmathpaper.core.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.bonsaimind.jmathpaper.Arguments;
import org.bonsaimind.jmathpaper.core.InvalidExpressionException;
import org.bonsaimind.jmathpaper.core.Paper;

/**
 * {@link Ui} is the interface for defining the functionality of a jMathPaper
 * UI.
 * <p>
 * It is assumed that here is an "active" {@link Paper} which the operations can
 * be performed on.
 * <p>
 * Implementors can use the {@link AbstractPapersUi} as base.
 */
public interface Ui {
	/**
	 * Clears the current {@link Paper}, if any.
	 * 
	 * @throws IllegalStateException If there is no current {@link Paper}.
	 */
	public void clear() throws IllegalStateException;
	
	/**
	 * Closes/removes the current {@link Paper} from the UI.
	 * 
	 * @throws IllegalStateException If there is no current {@link Paper}.
	 */
	public void close() throws IllegalStateException;
	
	/**
	 * Closes all open {@link Paper}s.
	 */
	public void closeAll();
	
	/**
	 * Evaluates the given {@link String expression} as a mathematical
	 * expression.
	 * 
	 * @param expression The mathematical {@link String expression} to evaluate.
	 * @throws IllegalStateException If there is no current {@link Paper} to
	 *         evaluate the expression on.
	 * @throws InvalidExpressionException If the given {@link String expression}
	 *         was invalid by any means.
	 */
	public void evaluate(String expression) throws IllegalStateException, InvalidExpressionException;
	
	/**
	 * Executes the given {@link Command}.
	 * 
	 * @param command The {@link Command} to execute.
	 * @param parameters The parameters of/for the command.
	 * @throws CommandExecutionException If the given {@link Command} could not
	 *         be executed.
	 */
	public void execute(Command command, String... parameters) throws CommandExecutionException;
	
	/**
	 * Gets the current {@link Paper}, can return {@code null} if there is none.
	 * 
	 * @return The current {@link Paper}, can return {@code null} if there is
	 *         none.
	 */
	public Paper getPaper();
	
	/**
	 * Gets a read-only {@link List} of {@link Paper}s which are loaded.
	 * 
	 * @return A read-only {@link List} of {@link Paper}s which are loaded.
	 */
	public List<Paper> getPapers();
	
	/**
	 * Initializes the UI for later use.
	 * <p>
	 * Implementations should use this method to create any objects they will
	 * later on require to function. After this function has been called, it is
	 * assumed that the UI is in a "usable" state and can perform any operation,
	 * accept any {@link Command} and is able to {@link #evaluate(String)
	 * evaluate} expression.
	 * 
	 * @throws Exception If there was an {@link Exception} thrown.
	 */
	public void init() throws Exception;
	
	/**
	 * Creates a new {@link Paper}.
	 * <p>
	 * The created {@link Paper} as is set as current.
	 */
	public void new_();
	
	/**
	 * Selects the next {@link Paper} in the list.
	 * <p>
	 * If there is none, the current selection stays unchanged. This function
	 * does nothing if there are no {@link Paper}s.
	 * 
	 * @see #previous()
	 */
	public void next();
	
	/**
	 * Opens a {@link Paper} from the given {@link Path}. If there is already a
	 * {@link Paper} open from the given {@link Path}, that paper is selected
	 * instead.
	 * <p>
	 * The opened {@link Paper} is set as current (of the already existing one).
	 * 
	 * @param file The {@link Path} to the file which to open.
	 * @throws IllegalArgumentException If the given {@link Path} is
	 *         {@code null}.
	 * @throws InvalidExpressionException If any of the expressions could not be
	 *         {@link #evaluate(String) evaluated}.
	 * @throws IOException If accessing the {@link Path} failed.
	 */
	public void open(Path file) throws IllegalArgumentException, InvalidExpressionException, IOException;
	
	/**
	 * Selects the previous {@link Paper} in the list.
	 * <p>
	 * If there is none, the current selection stays unchanged. This function
	 * does nothing if there are no {@link Paper}s.
	 * 
	 * @see #next()
	 */
	public void previous();
	
	/**
	 * Processes the given {@link String input}, either executes it as
	 * {@link Command} or evaluates it as expression.
	 * 
	 * @param input The {@link String input} to process.
	 * @throws CommandExecutionException If the {@link String input} was
	 *         {@link #execute(Command, String...) executed} as {@link Command}
	 *         but that failed.
	 * @throws InvalidExpressionException If the {@link String input} was
	 *         {@link #evaluate(String) evaluated} as expression but that
	 *         failed.
	 * @see #evaluate(String)
	 * @see #execute(Command, String...)
	 */
	public void process(String input) throws CommandExecutionException, InvalidExpressionException;
	
	/**
	 * Quits/closes/terminates the UI.
	 */
	public void quit();
	
	/**
	 * Reloads the current {@link Paper} from its previous location, if there is
	 * any.
	 * <p>
	 * If there is no current {@link Paper} or it was not loaded from a file,
	 * this function does nothing.
	 * 
	 * @throws IllegalStateException If there is no current {@link Paper}.
	 * @throws InvalidExpressionException If any of the expressions could not be
	 *         {@link #evaluate(String) evaluated}.
	 * @throws IOException If accessing the file failed.
	 */
	public void reload() throws IllegalStateException, InvalidExpressionException, IOException;
	
	/**
	 * Runs the UI.
	 * <p>
	 * With this call the UI should start working, start its main loop if there
	 * is any.
	 * 
	 * @param arguments The {@link Arguments} to use.
	 * @throws Exception If running the UI failed.
	 */
	public void run(Arguments arguments) throws Exception;
	
	/**
	 * Saves the current {@link Paper}.
	 * 
	 * @throws IllegalStateException If there is no current {@link Paper}.
	 * @throws IOException If accessing the file failed.
	 */
	public void save() throws IllegalStateException, IOException;
	
	/**
	 * Saves the current {@link Paper} to the given {@link Path}.
	 * 
	 * @param file The {@link Path} to save to.
	 * @throws IllegalArgumentException If the given {@link Path} is
	 *         {@code null}.
	 * @throws IllegalStateException If there is no current {@link Paper}.
	 * @throws IOException If accessing the file failed.
	 */
	public void save(Path file) throws IllegalArgumentException, IllegalStateException, IOException;
	
	public void setOption(Option option, String value) throws CommandExecutionException;
}
