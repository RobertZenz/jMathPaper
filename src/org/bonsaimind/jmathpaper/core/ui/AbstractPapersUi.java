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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.InvalidExpressionException;
import org.bonsaimind.jmathpaper.core.Paper;
import org.bonsaimind.jmathpaper.core.configuration.Definitions;

/**
 * {@link AbstractPapersUi} is an {@link Ui} implementation which implements the
 * basic management of multiple {@link Paper}s. It also implements all
 * functionality based on that.
 */
public abstract class AbstractPapersUi implements Ui {
	/** The {@link Definitions} to use by default. */
	protected Definitions defaultDefinitions = null;
	
	/** The currently selected {@link Paper}. */
	protected Paper paper = null;
	
	/** The {@link List} of {@link Paper}s. */
	protected List<Paper> papers = new ArrayList<>();
	
	/** The {@link UiParameters} with which this has been run. */
	protected UiParameters uiParameters = null;
	
	/** The counter for papers which are not saved. */
	private int paperCounter = 0;
	
	/** The cache for the titles of the papers. */
	private Map<Paper, String> paperTitleCache = new WeakHashMap<>();
	
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
			
			currentPaperHasBeenRemoved();
			
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
		while (!papers.isEmpty()) {
			close();
		}
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
				case ADD:
					if (parameters != null && parameters.length > 0) {
						if (parameters.length > 1) {
							List<String> remainingParameters = new ArrayList<>(Arrays.asList(parameters));
							remainingParameters.remove(0);
							
							String arguments = String.join(" ", remainingParameters);
							
							switch (parameters[0]) {
								case "conversion":
									addConversion(arguments);
									break;
								
								case "prefix":
									addPrefix(arguments);
									break;
								
								case "unit":
									addUnit(arguments);
									break;
								
								default:
									throw new CommandExecutionException("No arguments provided: add unit/prefix/conversion");
									
							}
						} else {
							throw new CommandExecutionException("No arguments provided, valid definition expected.");
						}
					} else {
						throw new CommandExecutionException("No arguments provided: add unit/prefix/conversion");
					}
					break;
				
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
				
				case CLOSEALL:
					closeAll();
					break;
				
				case COPY:
					if (parameters != null && parameters.length > 0) {
						PaperPart paperPart = PaperPart.getPaperPart(parameters[0]);
						
						if (paperPart == null) {
							copy(PaperPart.LINE, String.join(" ", parameters));
						} else if (parameters.length > 1) {
							List<String> remainingParameters = new ArrayList<>(Arrays.asList(parameters));
							remainingParameters.remove(0);
							
							copy(paperPart, String.join(" ", remainingParameters));
						} else {
							copy(paperPart, null);
						}
					} else {
						copy(PaperPart.PAPER, null);
					}
					break;
				
				case NEXT:
					next();
					break;
				
				case NEW:
					new_();
					break;
				
				case NOTE:
					if (parameters != null && parameters.length > 0) {
						NoteAction noteAction = NoteAction.getNoteAction(parameters[0]);
						
						if (noteAction == null) {
							noteAction = NoteAction.ADD;
						}
						
						editNote(noteAction, Arrays.copyOfRange(parameters, 1, parameters.length));
					} else {
						throw new CommandExecutionException("No parameters given: add/clear/delete/insert parameters");
					}
					break;
				
				case OPEN:
					for (String parameter : parameters) {
						open(Paths.get(parameter));
					}
					break;
				
				case OPTION:
					tryAsOption(parameters);
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
	public void init(UiParameters uiParameters) throws Exception {
		this.uiParameters = uiParameters;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void new_() {
		Paper newPaper = createNewPaper();
		
		papers.add(newPaper);
		setPaper(newPaper);
		
		currentPaperHasBeenAdded();
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
		
		// Paper could be successfully loaded.
		if (papers.size() == 1
				&& paper != null
				&& paper.getEvaluatedExpressions().isEmpty()
				&& paper.getFile() == null) {
			// Seems like a new and empty paper, let's close it.
			close();
		}
		
		papers.add(loadedPaper);
		setPaper(loadedPaper);
		
		currentPaperHasBeenAdded();
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
	public void setDefaultDefinitions(Definitions definitions) {
		defaultDefinitions = definitions;
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
	
	/**
	 * Adds the given alias to the current {@link #paper}.
	 * 
	 * @param alias The alias to add.
	 */
	protected void addAlias(String alias) {
		checkCurrentPaper();
		
		paper.getEvaluator().loadAlias(alias);
	}
	
	/**
	 * Adds the given conversion to the current {@link #paper}.
	 * 
	 * @param alias The conversion to add.
	 */
	protected void addConversion(String conversion) {
		checkCurrentPaper();
		
		paper.getEvaluator().getUnitConverter().loadConversion(conversion);
	}
	
	/**
	 * Adds the given prefix to the current {@link #paper}.
	 * 
	 * @param alias The prefix to add.
	 */
	protected void addPrefix(String prefix) {
		checkCurrentPaper();
		
		paper.getEvaluator().getUnitConverter().loadPrefix(prefix);
	}
	
	/**
	 * Adds the given unit to the current {@link #paper}.
	 * 
	 * @param alias The unit to add.
	 */
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
	 * Checks that the current {@link #paper} is not empty.
	 *
	 * @throws IllegalStateException If the current {@link #paper} is empty.
	 */
	protected void checkCurrentPaperNotEmpty() throws IllegalStateException {
		if (paper == null || paper.getEvaluatedExpressions().isEmpty()) {
			throw new IllegalStateException("This operation can only be performed with a paper with lines.");
		}
	}
	
	/**
	 * Copies the current {@link #paper} to the clipboard of the system.
	 * 
	 * @throws IllegalStateException If there is no current {@link #paper}.
	 */
	protected void copy(PaperPart paperPart, String identification) throws IllegalStateException {
		checkCurrentPaper();
		
		switch (paperPart) {
			case EXPRESSION:
			case ID:
			case LINE:
			case RESULT:
				checkCurrentPaperNotEmpty();
				
				List<EvaluatedExpression> evaluatedExpressions = null;
				
				if (identification != null && !identification.isEmpty()) {
					evaluatedExpressions = getEvaluatedExpressions(identification);
				} else {
					evaluatedExpressions = paper.getEvaluatedExpressions();
				}
				
				if (evaluatedExpressions.isEmpty()) {
					throw new IllegalArgumentException("No matching lines found for: " + identification);
				}
				
				StringBuilder content = new StringBuilder();
				
				for (EvaluatedExpression evaluatedExpression : evaluatedExpressions) {
					if (content.length() > 0) {
						content.append("\n");
					}
					
					switch (paperPart) {
						case EXPRESSION:
							content.append(evaluatedExpression.getExpression());
							break;
						
						case ID:
							content.append(evaluatedExpression.getId());
							break;
						
						case LINE:
							content.append(evaluatedExpression.toString());
							break;
						
						case RESULT:
							content.append(evaluatedExpression.getFormattedResult(paper.getNumberFormat()));
							break;
					}
				}
				
				copyToClipboard(content.toString());
				break;
			
			case PAPER:
				copyToClipboard(paper.toString());
				break;
		}
	}
	
	protected void copyToClipboard(String value) {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				new StringSelection(value),
				null);
	}
	
	/**
	 * Creates a new {@link Paper} instance.
	 * 
	 * @return A new {@link Paper} instance.
	 */
	protected Paper createNewPaper() {
		Paper paper = new Paper();
		
		if (defaultDefinitions != null) {
			defaultDefinitions.apply(paper);
		}
		
		return paper;
	}
	
	/**
	 * Invoked whenever the current {@link Paper} has been added, meaning that
	 * is just has been added to this UI.
	 * <p>
	 * Overriding classes can safely assume that there is a current
	 * {@link Paper}.
	 */
	protected void currentPaperHasBeenAdded() {
		// For extending classes.
	}
	
	/**
	 * Invoked whenever the current {@link Paper} has been modified, meaning
	 * that one or more additional expressions has been added.
	 * <p>
	 * Overriding classes can safely assume that there is a current
	 * {@link Paper}.
	 */
	protected void currentPaperHasBeenModified() {
		// For extending classes.
	}
	
	/**
	 * Invoked whenever the current {@link Paper} has been removed.
	 * <p>
	 * Overriding classes can safely assume that there is a current
	 * {@link Paper}. However, that {@link Paper} has already been removed from
	 * the {@link #papers list}. Any changes to the {@link #setPaper(Paper)
	 * current paper} during this function will be undone.
	 */
	protected void currentPaperHasBeenRemoved() {
		// For extending classes.
	}
	
	/**
	 * Invoked whenever the current {@link Paper} has been reset, meaning the
	 * complete content has to be considered changed.
	 * <p>
	 * Overriding classes can safely assume that there is a current
	 * {@link Paper}.
	 */
	protected void currentPaperHasBeenReset() {
		// For extending classes.
	}
	
	/**
	 * Invoked whenever the currently selected {@link Paper} changes.
	 * <p>
	 * Overriding classes must check whether a {@link Paper} is currently
	 * selected.
	 */
	protected void currentSelectedPaperHasChanged() {
		// For extending classes.
	}
	
	/**
	 * Edits the note of the current {@link Paper}.
	 * 
	 * @param noteAction The {@link NoteAction} to perform on the current note.
	 * @param parameters The parameters to use.
	 */
	protected void editNote(NoteAction noteAction, String[] parameters) {
		checkCurrentPaper();
		
		switch (noteAction) {
			case ADD:
				paper.setNotes(paper.getNotes() + String.join(" ", parameters) + "\n");
				break;
			
			case CLEAR:
				paper.setNotes("");
				break;
			
			case DELETE:
			case INSERT:
				if (parameters.length <= 0) {
					throw new IllegalArgumentException("Expected an index, got nothing.");
				}
				
				int index = -1;
				
				try {
					index = Integer.parseInt(parameters[0]) - 1;
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Expected an index, got: " + parameters[0], e);
				}
				
				List<String> noteLines = new ArrayList<>(Arrays.asList(paper.getNotes().split("\\n")));
				
				if (index < 0 || index >= noteLines.size()) {
					throw new IllegalArgumentException("Given index is out of range.");
				}
				
				switch (noteAction) {
					case DELETE:
						noteLines.remove(index);
						break;
					
					case INSERT:
						noteLines.add(index, String.join(" ", Arrays.copyOfRange(parameters, 1, parameters.length)));
						break;
				}
				
				paper.setNotes(String.join("\n", noteLines) + "\n");
				break;
		}
		
		currentPaperHasBeenModified();
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
	 * Gets the {@link EvaluatedExpression} from the current {@link Paper} which
	 * matches the given identifier.
	 * <p>
	 * The give identifier can either be the ID (case-sensitive), or the 1-based
	 * index of the expression.
	 * 
	 * @param identifier The ID of the {@link EvaluatedExpression} to get or the
	 *        1-based index.
	 * @return The {@link EvaluatedExpression} which matches the given
	 *         identifier.
	 */
	protected EvaluatedExpression getEvaluatedExpression(String identifier) {
		if (identifier == null || identifier.isEmpty()) {
			return null;
		}
		
		String trimmedIdentifier = identifier.trim();
		
		try {
			int number = Integer.parseInt(trimmedIdentifier);
			
			if (number >= 0) {
				return paper.getEvaluatedExpressions().get(number - 1);
			} else {
				return paper.getEvaluatedExpressions().get(paper.getEvaluatedExpressions().size() + number);
			}
		} catch (NumberFormatException e) {
			// Ignore and continue.
		}
		
		for (EvaluatedExpression evaluatedExpression : paper.getEvaluatedExpressions()) {
			if (evaluatedExpression.getId().equals(trimmedIdentifier)) {
				return evaluatedExpression;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the {@link EvaluatedExpression}s which are matching the given
	 * identification}.
	 * <p>
	 * The given identification can either be a single ID or 1-based index, a
	 * comma-separated list of IDs or 1-based indexes or a range which is
	 * separated by double-dots.
	 * 
	 * @param identification The identification for the
	 *        {@link EvaluatedExpression}s.
	 * @return The {@link List} of matching {@link EvaluatedExpression}s.
	 */
	protected List<EvaluatedExpression> getEvaluatedExpressions(String identification) {
		if (identification == null || identification.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<EvaluatedExpression> evaluatedExpressions = new ArrayList<>();
		
		if (identification.contains("..")) {
			String[] identifiers = identification.split("\\.\\.");
			
			EvaluatedExpression startEvaluatedExpression = getEvaluatedExpression(identifiers[0]);
			EvaluatedExpression endEvaluatedExpression = getEvaluatedExpression(identifiers[1]);
			
			if (startEvaluatedExpression != null || endEvaluatedExpression != null) {
				boolean addEvaluatedExpressions = false;
				
				for (EvaluatedExpression evaluatedExpression : paper.getEvaluatedExpressions()) {
					if (evaluatedExpression == startEvaluatedExpression) {
						addEvaluatedExpressions = true;
					}
					
					if (addEvaluatedExpressions) {
						evaluatedExpressions.add(evaluatedExpression);
					}
					
					if (evaluatedExpression == endEvaluatedExpression) {
						addEvaluatedExpressions = false;
					}
				}
			}
		} else if (identification.contains(",")) {
			for (String identifier : identification.split(",")) {
				EvaluatedExpression evaluatedExpression = getEvaluatedExpression(identifier);
				
				if (evaluatedExpression != null) {
					evaluatedExpressions.add(evaluatedExpression);
				}
			}
		} else {
			evaluatedExpressions.add(getEvaluatedExpression(identification));
		}
		
		return evaluatedExpressions;
	}
	
	/**
	 * Gets the long title for the given {@link Paper}.
	 * <p>
	 * The long title contains the full path (if any) and a short notice if the
	 * {@link Paper} has unsaved modifications.
	 * 
	 * @param paper The {@link Paper} to get the long title for, can be
	 *        {@code null} in which case an empty {@link String} is returned.
	 * @return The long title for the given paper.
	 */
	protected String getLongPaperTitle(Paper paper) {
		if (paper == null) {
			return "";
		}
		
		if (paper.getFile() != null) {
			String title = paper.getFile().toAbsolutePath().toString();
			
			if (paper.isChanged()) {
				title = "(not saved) " + title;
			}
			
			return title;
		} else {
			return "not saved";
		}
	}
	
	/**
	 * Gets the short title for the given {@link Paper}.
	 * <p>
	 * The short title contains the filename (if or a placeholder name) and a
	 * marker if the {@link Paper} has unsaved modifications.
	 * 
	 * @param paper The {@link Paper} to get the short title for, can be
	 *        {@code null} in which case an empty {@link String} is returned.
	 * @return The short title for the given paper.
	 */
	protected String getShortPaperTitle(Paper paper) {
		if (paper == null) {
			return "";
		}
		
		if (paper.getFile() != null) {
			String title = paper.getFile().getFileName().toString();
			
			if (paper.isChanged()) {
				title = "*" + title;
			}
			
			return title;
		} else {
			String title = paperTitleCache.get(paper);
			
			if (title == null) {
				paperCounter = paperCounter + 1;
				title = "*Paper #" + Integer.toString(paperCounter);
				paperTitleCache.put(paper, title);
			}
			
			return title;
		}
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
		
		currentPaperHasBeenReset();
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
		
		if (paper != this.paper) {
			this.paper = paper;
			
			currentSelectedPaperHasChanged();
		}
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
