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

package org.bonsaimind.jmathpaper.core.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bonsaimind.jmathpaper.Arguments;
import org.bonsaimind.jmathpaper.Configuration;
import org.bonsaimind.jmathpaper.core.InvalidExpressionException;
import org.bonsaimind.jmathpaper.core.Paper;

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
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Command command, String... parameters) throws CommandExecutionException {
		try {
			switch (command) {
				case CLEAR:
					clear();
					break;
				
				case CLOSE:
					close();
					break;
				
				case NEXT:
					next();
					break;
				
				case NEW:
					new_();
					break;
				
				case OPEN:
					open(Paths.get(parameters[0]));
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
					if (parameters.length > 0) {
						save(Paths.get(parameters[0]));
					} else {
						save();
					}
					break;
				
				case SAVE_AND_QUIT:
					if (parameters.length > 0) {
						save(Paths.get(parameters[0]));
					} else {
						save();
					}
					quit();
					break;
				
			}
		} catch (Exception e) {
			throw new CommandExecutionException("Could not execute command " + command.name() + ".", e);
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
		Paper newPaper = new Paper();
		
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
	public void open(Path file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null.");
		}
		
		for (Paper paper : papers) {
			if (file.equals(paper.getFile())) {
				setPaper(paper);
				return;
			}
		}
		
		Paper loadedPaper = new Paper();
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
		if (!tryAsCommand(input)) {
			evaluate(input);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() throws IOException {
		checkCurrentPaper();
		
		paper.load();
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
			if (!tryAsCommand(arguments.getExpression())) {
				evaluate(arguments.getExpression());
			} else if (isOneshot()) {
				return;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save() throws IOException {
		checkCurrentPaper();
		
		paper.save();
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
	 * Checks if is oneshot.
	 *
	 * @return true, if is oneshot
	 */
	protected boolean isOneshot() {
		return false;
	}
	
	/**
	 * Opens the "default" {@link Paper}.
	 * <p>
	 * The default is used during startup when no {@link Paper}s have been
	 * opened because of arguments.
	 * 
	 * @throws IOException If accessing the file failed.
	 */
	protected void openDefaultPaper() throws IOException {
		open(Configuration.getGlobalPaperFile());
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
		
		String trimmedInput = input.trim();
		String name = trimmedInput;
		String parameters = "";
		
		int spaceIndex = input.indexOf(' ');
		if (spaceIndex >= 0) {
			name = trimmedInput.substring(0, spaceIndex);
			parameters = trimmedInput.substring(spaceIndex + 1);
		}
		
		Command command = Command.getCommand(name);
		
		if (command != null) {
			execute(command, parameters.split(" "));
			
			return true;
		}
		
		return false;
	}
}
