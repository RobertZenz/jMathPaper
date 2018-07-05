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

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bonsaimind.jmathpaper.Arguments;
import org.bonsaimind.jmathpaper.Configuration;
import org.bonsaimind.jmathpaper.core.ConfigurationProcessor;
import org.bonsaimind.jmathpaper.core.Evaluator;
import org.bonsaimind.jmathpaper.core.InvalidExpressionException;
import org.bonsaimind.jmathpaper.core.Paper;
import org.bonsaimind.jmathpaper.core.resources.ResourceLoader;
import org.bonsaimind.jmathpaper.core.units.UnitConverter;

/**
 * {@link AbstractPapersUi} is an {@link Ui} implementation which implements the
 * basic management of multiple {@link Paper}s. It also implements all
 * functionality based on that.
 */
public abstract class AbstractPapersUi implements Ui {
	/** The {@link Arguments} with which this has been run. */
	protected Arguments arguments = null;
	
	/** The currently selected {@link Paper}. */
	protected Paper paper = null;
	
	/** The {@link List} of {@link Paper}s. */
	protected List<Paper> papers = new ArrayList<>();
	
	/** The {@link #papers} as read-only {@link List}. */
	private List<Paper> readonlyPapers = null;
	
	/**
	 * Creates a new instance of {@link AbstractPapersUi}.
	 */
	protected AbstractPapersUi() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		checkCurrentPaper();
		
		paper.clear();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		if (paper != null) {
			int removedIndex = papers.indexOf(paper);
			
			papers.remove(paper);
			
			if (!papers.isEmpty()) {
				if (removedIndex < papers.size() - 1) {
					setPaper(papers.get(removedIndex));
				} else {
					setPaper(papers.get(papers.size() - 1));
				}
			} else {
				setPaper(null);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeAll() {
		papers.clear();
		setPaper(null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void evaluate(String expression) throws IllegalStateException, InvalidExpressionException {
		if (expression == null) {
			return;
		}
		
		checkCurrentPaper();
		
		paper.evaluate(expression);
		
		currentPaperHasBeenModified();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Command command, String... parameters) throws CommandExecutionException {
		try {
			switch (command) {
				case ALIAS:
					if (parameters != null && parameters.length > 0) {
						addAlias(String.join(" ", parameters));
					} else {
						throw new CommandExecutionException("No arguments provided: alias ALIAS REPLACEMENT");
					}
					break;
				
				case CLEAR:
					clear();
					break;
				
				case CLOSE:
					close();
					break;
				
				case CONVERSION:
					if (parameters != null && parameters.length > 0) {
						addConversion(String.join(" ", parameters));
					} else {
						throw new CommandExecutionException("No arguments provided: conversion TARGETUNIT VALUE SOURCEUNIT");
					}
					break;
				
				case COPY:
					copy();
					break;
				
				case NEXT:
					next();
					break;
				
				case NEW:
					new_();
					break;
				
				case OPEN:
					for (String parameter : parameters) {
						open(Paths.get(parameter));
					}
					break;
				
				case OPTION:
					tryAsOption(parameters);
					break;
				
				case PREFIX:
					if (parameters != null && parameters.length > 0) {
						addPrefix(String.join(" ", parameters));
					} else {
						throw new CommandExecutionException("No arguments provided: prefix PREFIXNAME PREFIX BASE EXPONENT");
					}
					break;
				
				case PREVIOUS:
					previous();
					break;
				
				case QUIT:
					quit();
					break;
				
				case RELOAD:
					reload();
					break;
				
				case SAVE:
					if (parameters != null && parameters.length > 0) {
						for (String parameter : parameters) {
							save(Paths.get(parameter));
						}
					} else {
						save();
					}
					break;
				
				case SAVE_AND_QUIT:
					if (parameters != null && parameters.length > 0) {
						for (String parameter : parameters) {
							save(Paths.get(parameter));
						}
					} else {
						save();
					}
					quit();
					break;
				
				case UNIT:
					if (parameters != null && parameters.length > 0) {
						addUnit(String.join(" ", parameters));
					} else {
						throw new CommandExecutionException("No arguments provided: unit UNITNAME EXPONENT [ALIAS,ALIAS,...]");
					}
					break;
				
			}
		} catch (CommandExecutionException e) {
			throw e;
		} catch (Exception e) {
			throw new CommandExecutionException("Could not execute command " + command.name() + ": " + e.getMessage(), e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Paper getPaper() {
		return paper;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Paper> getPapers() {
		if (readonlyPapers == null) {
			readonlyPapers = Collections.unmodifiableList(papers);
		}
		
		return readonlyPapers;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws Exception {
		// Implemented to simplify extending classes.
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void new_() {
		Paper newPaper = createNewPaper();
		
		papers.add(newPaper);
		setPaper(newPaper);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void next() {
		if (!papers.isEmpty()) {
			if (paper == null) {
				setPaper(papers.get(0));
			} else {
				int currentIndex = papers.indexOf(paper);
				
				if (currentIndex < papers.size() - 1) {
					setPaper(papers.get(currentIndex + 1));
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void open(Path file) throws InvalidExpressionException, IOException {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null.");
		}
		
		for (Paper paper : papers) {
			if (file.equals(paper.getFile())) {
				setPaper(paper);
				return;
			}
		}
		
		Paper loadedPaper = createNewPaper();
		loadedPaper.setFile(file);
		loadedPaper.loadFrom(file);
		
		papers.add(loadedPaper);
		setPaper(loadedPaper);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void previous() {
		if (!papers.isEmpty()) {
			if (paper == null) {
				setPaper(papers.get(papers.size() - 1));
			} else {
				int currentIndex = papers.indexOf(paper);
				
				if (currentIndex > 0) {
					setPaper(papers.get(currentIndex - 1));
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(String input) throws CommandExecutionException, InvalidExpressionException {
		for (String part : splitStatements(input)) {
			if (!tryAsCommand(part)) {
				evaluate(part);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() throws InvalidExpressionException, IOException {
		checkCurrentPaper();
		
		paper.load();
		
		currentPaperHasBeenReset();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(Arguments arguments) throws Exception {
		this.arguments = arguments;
		
		if (arguments.hasFiles()) {
			for (Path file : arguments.getFiles()) {
				open(file);
			}
		}
		
		if (arguments.getContext() != null) {
			boolean contextFound = false;
			
			for (Paper paper : papers) {
				if (arguments.getContext().equals(paper.getFile())) {
					setPaper(paper);
					contextFound = true;
				}
			}
			
			if (!contextFound) {
				open(arguments.getContext());
			}
		}
		
		if (paper == null) {
			openDefaultPaper();
		}
		
		if (arguments.getExpression() != null) {
			process(arguments.getExpression());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save() throws IOException {
		checkCurrentPaper();
		
		paper.save();
		
		currentPaperHasBeenReset();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save(Path file) throws IOException {
		checkCurrentPaper();
		
		paper.saveTo(file);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOption(Option option, String value) throws CommandExecutionException {
		checkCurrentPaper();
		
		try {
			switch (option) {
				case NUMBER_FORMAT:
					getPaper().setNumberFormat(value);
					reevaluate();
					break;
				
				case PRECISION:
					getPaper().setPrecision(Integer.parseInt(value));
					reevaluate();
					break;
				
				case ROUNDING:
					getPaper().setRoundingMode(getEnum(RoundingMode.class, value));
					reevaluate();
					break;
				
			}
		} catch (InvalidExpressionException e) {
			throw new CommandExecutionException(e.getMessage(), e);
		} catch (Exception e) {
			throw new CommandExecutionException("Failed to set option \"" + option.name() + "\" to \"" + value + "\": " + e.getMessage(), e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder value = new StringBuilder();
		value.append(getClass().getSimpleName());
		value.append("[\n");
		
		if (paper != null) {
			value.append("Selected: ");
			value.append(String.format("%08x", Integer.valueOf(System.identityHashCode(paper))));
			value.append(" ");
			value.append(paper.getFile());
			value.append("\n");
		}
		
		for (int index = 0; index < papers.size(); index++) {
			value.append("    ");
			value.append(index);
			value.append(": ");
			value.append(String.format("%08x", Integer.valueOf(System.identityHashCode(papers.get(index)))));
			value.append(" ");
			value.append(papers.get(index).getFile());
			value.append("\n");
		}
		
		value.append("]");
		
		return value.toString();
	}
	
	protected void addAlias(String alias) {
		checkCurrentPaper();
		
		paper.getEvaluator().loadAlias(alias);
	}
	
	protected void addConversion(String conversion) {
		checkCurrentPaper();
		
		paper.getEvaluator().getUnitConverter().loadConversion(conversion);
	}
	
	protected void addPrefix(String prefix) {
		checkCurrentPaper();
		
		paper.getEvaluator().getUnitConverter().loadPrefix(prefix);
	}
	
	protected void addUnit(String unit) {
		checkCurrentPaper();
		
		paper.getEvaluator().getUnitConverter().loadUnit(unit);
	}
	
	/**
	 * Checks that there is a {@link #paper}.
	 *
	 * @throws IllegalStateException If there is no current {@link #paper}.
	 */
	protected void checkCurrentPaper() throws IllegalStateException {
		if (paper == null) {
			throw new IllegalStateException("This operation can only be performed with a paper open.");
		}
	}
	
	/**
	 * Copies the current {@link #paper} to the clipboard of the system.
	 * 
	 * @throws IllegalStateException If there is no current {@link #paper}.
	 */
	protected void copy() throws IllegalStateException {
		checkCurrentPaper();
		
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				new StringSelection(paper.toString()),
				null);
	}
	
	/**
	 * Creates a new {@link Paper} instance.
	 * 
	 * @return A new {@link Paper} instance.
	 */
	protected Paper createNewPaper() {
		Paper paper = new Paper();
		
		Evaluator evaluator = paper.getEvaluator();
		
		ResourceLoader.processResource("other/default.aliases", evaluator::loadAlias);
		
		ConfigurationProcessor.process(Configuration.getUserAliasesFile(), evaluator::loadAlias);
		
		for (Path aliasesFile : arguments.getAliasesFiles()) {
			ConfigurationProcessor.process(aliasesFile, evaluator::loadAlias);
		}
		
		UnitConverter unitConverter = evaluator.getUnitConverter();
		
		ResourceLoader.processResource("units/iec.prefixes", unitConverter::loadPrefix);
		ResourceLoader.processResource("units/si.prefixes", unitConverter::loadPrefix);
		ResourceLoader.processResource("units/default.units", unitConverter::loadUnit);
		ResourceLoader.processResource("units/default.conversions", unitConverter::loadConversion);
		
		ConfigurationProcessor.process(Configuration.getUserUnitsFile(), unitConverter::loadUnit);
		ConfigurationProcessor.process(Configuration.getUserPrefixesFile(), unitConverter::loadPrefix);
		ConfigurationProcessor.process(Configuration.getUserConversionsFile(), unitConverter::loadConversion);
		
		for (Path unitsFile : arguments.getUnitsFiles()) {
			ConfigurationProcessor.process(unitsFile, unitConverter::loadUnit);
		}
		
		for (Path prefixesFile : arguments.getPrefixesFiles()) {
			ConfigurationProcessor.process(prefixesFile, unitConverter::loadPrefix);
		}
		
		for (Path conversionsFile : arguments.getConversionsFiles()) {
			ConfigurationProcessor.process(conversionsFile, unitConverter::loadConversion);
		}
		
		return paper;
	}
	
	/**
	 * Invoked whenever the current {@link Paper} has been modified.
	 * <p>
	 * Overriding classes can safely assume that there is a current
	 * {@link Paper}.
	 */
	protected void currentPaperHasBeenModified() {
		// For overriding classes.
	}
	
	/**
	 * Invoked whenever the current {@link Paper} has been reset.
	 * <p>
	 * Overriding classes can safely assume that there is a current
	 * {@link Paper}.
	 */
	protected void currentPaperHasBeenReset() {
		// For overriding classes.
	}
	
	/**
	 * A helper method which gets the appropriate Enum value from the given
	 * class and name.
	 * 
	 * @param enumClass The Enum class.
	 * @param name The name of the value.
	 * @return The value of the Enum.
	 * @throws IllegalArgumentException If {@code enumClass} is null,
	 *         {@code name} is null or empty or if there is no value with that
	 *         name.
	 */
	protected <ENUM extends Enum<?>> ENUM getEnum(Class<ENUM> enumClass, String name) {
		if (enumClass == null) {
			throw new IllegalArgumentException("enumClass cannot be null.");
		}
		
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("name cannot be null or empty.");
		}
		
		for (ENUM value : enumClass.getEnumConstants()) {
			String valueName = value.name();
			
			if (valueName.equalsIgnoreCase(name)
					|| valueName.replace("_", "-").equalsIgnoreCase(name)
					|| valueName.replace("_", "").equalsIgnoreCase(name)) {
				return value;
			}
		}
		
		throw new IllegalArgumentException("\"" + name + "\" is not a value of " + enumClass.getSimpleName() + ".");
	}
	
	/**
	 * Opens the "default" {@link Paper}.
	 * <p>
	 * The default is used during startup when no {@link Paper}s have been
	 * opened because of arguments.
	 * 
	 * @throws IOException If accessing the file failed.
	 */
	protected void openDefaultPaper() throws InvalidExpressionException, IOException {
		open(Configuration.getGlobalPaperFile());
	}
	
	/**
	 * Reevaluates the current {@link Paper}.
	 * <p>
	 * Extending classes should override this function and update accordingly
	 * after calling super.
	 * 
	 * @throws InvalidExpressionException If the reevaluation of one statement
	 *         fails.
	 */
	protected void reevaluate() throws InvalidExpressionException {
		checkCurrentPaper();
		
		paper.reevaluate();
	}
	
	/**
	 * Sets the current {@link Paper}.
	 *
	 * @param paper The new current {@link Paper}, can be {@code null} to select
	 *        none.
	 * @throws IllegalStateException If the given {@link Paper} is not part of
	 *         this UI.
	 */
	protected void setPaper(Paper paper) throws IllegalStateException {
		if (paper != null && !papers.contains(paper)) {
			throw new IllegalStateException("The current paper must be part of this UI.");
		}
		
		this.paper = paper;
	}
	
	/**
	 * Splits the given {@String input} into single parameters.
	 * 
	 * @param input The {@link String input} to split.
	 * @return The result of the split.
	 */
	protected List<String> splitParameters(String input) {
		if (input == null || input.length() == 0) {
			return Collections.emptyList();
		}
		
		List<String> parameters = new ArrayList<>();
		
		StringBuilder currentParameter = new StringBuilder();
		boolean insideQuotes = false;
		boolean nextIsEscaped = false;
		
		for (int index = 0; index < input.length(); index++) {
			char currentChar = input.charAt(index);
			
			if (!insideQuotes
					&& !nextIsEscaped
					&& Character.isWhitespace(currentChar)) {
				if (currentParameter.length() > 0) {
					// Add the currently build parameter to the list.
					parameters.add(currentParameter.toString());
					
					currentParameter.delete(0, currentParameter.length());
				}
			} else if (!nextIsEscaped && currentChar == '\\') {
				nextIsEscaped = true;
			} else if (!nextIsEscaped && currentChar == '"') {
				insideQuotes = !insideQuotes;
			} else {
				currentParameter.append(currentChar);
				
				nextIsEscaped = false;
			}
		}
		
		if (currentParameter.length() > 0) {
			parameters.add(currentParameter.toString());
		}
		
		return parameters;
	}
	
	/**
	 * Splits the given {@link String input} into single, processable
	 * statements.
	 * 
	 * @param input The {@link String input} to split.
	 * @return The result of the split.
	 */
	protected Iterable<String> splitStatements(String input) {
		if (input == null || input.length() == 0) {
			return Collections.emptyList();
		}
		
		List<String> statements = new ArrayList<>();
		
		StringBuilder currentStatement = new StringBuilder();
		// We have to use a separate whitespace buffer to trim whitespace at
		// the end of the statement. We can simply skip over whitespace at
		// the beginning, but if we encounter whitespace inside the statement
		// we do not know whether it is trailing or not. So we add it to
		// this whitespace buffer and append it to the statement if we encounter
		// something different than whitespace.
		StringBuilder currentWhitespace = new StringBuilder();
		boolean insideQuotes = false;
		boolean nextIsEscaped = false;
		
		for (int index = 0; index < input.length(); index++) {
			char currentChar = input.charAt(index);
			
			if (Character.isWhitespace(currentChar)) {
				if (currentStatement.length() > 0) {
					// If we've already found something other than whitespace,
					// then append the character to the whitespace buffer.
					currentWhitespace.append(currentChar);
				}
				
				nextIsEscaped = false;
			} else if (currentChar == '\\') {
				nextIsEscaped = true;
			} else if (!nextIsEscaped
					&& !insideQuotes
					&& currentChar == ';') {
				if (currentStatement.length() > 0) {
					// Add the current statement to the list.
					statements.add(currentStatement.toString());
				}
				
				currentStatement.delete(0, currentStatement.length());
				currentWhitespace.delete(0, currentWhitespace.length());
			} else if (!nextIsEscaped
					&& currentChar == '"'
					&& (currentWhitespace.length() > 0
							|| currentStatement.length() == 0
							|| insideQuotes)) {
				insideQuotes = !insideQuotes;
				
				if (currentStatement.length() > 0) {
					currentWhitespace.append(currentChar);
				}
			} else {
				// If there is something in the whitespace buffer,
				// append it.
				if (currentWhitespace.length() > 0) {
					currentStatement.append(currentWhitespace);
				}
				
				currentWhitespace.delete(0, currentWhitespace.length());
				
				currentStatement.append(currentChar);
				
				nextIsEscaped = false;
			}
		}
		
		if (currentStatement.length() > 0) {
			statements.add(currentStatement.toString());
		}
		
		return statements;
	}
	
	/**
	 * Tries the given {@link String input} as {@link Command}, returns
	 * {@code true} if that succeeded, otherwise {@code false}.
	 *
	 * @param input The {@link String input} to try as {@link Command}.
	 * @return {@code true} if the {@link String input} was a {@link Command}.
	 * @throws CommandExecutionException If the execution of the {@link Command}
	 *         failed.
	 */
	protected boolean tryAsCommand(String input) throws CommandExecutionException {
		if (input == null || input.length() == 0) {
			return false;
		}
		
		List<String> parameters = splitParameters(input);
		
		if (parameters.isEmpty()) {
			return false;
		}
		
		Command command = Command.getCommand(parameters.get(0));
		
		if (command != null) {
			parameters.remove(0);
			
			execute(
					command,
					parameters.toArray(new String[parameters.size()]));
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Tries the given {@link String input} as {@link Option}, returns
	 * {@code true} if that succeeded, otherwise {@code false}.
	 *
	 * @param input The {@link String input} to try as {@link Option}.
	 * @return {@code true} if the {@link String input} was an {@link Option}.
	 * @throws CommandExecutionException If the execution of the {@link Option}
	 *         failed.
	 */
	protected boolean tryAsOption(String... input) throws CommandExecutionException {
		if (input == null || input.length <= 1) {
			return false;
		}
		
		Option option = Option.getOption(input[0]);
		
		if (option != null) {
			for (int index = 1; index < input.length; index++) {
				setOption(option, input[index]);
			}
			
			return true;
		}
		
		return false;
	}
}
