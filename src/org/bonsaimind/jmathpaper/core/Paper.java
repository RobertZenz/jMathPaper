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

package org.bonsaimind.jmathpaper.core;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Paper {
	private static final int DEFAULT_WIDTH = 50;
	protected boolean changed = true;
	protected List<EvaluatedExpression> evaluatedExpressions = new ArrayList<>();
	protected Evaluator evaluator = new Evaluator();
	protected int expressionColumnSize = 0;
	protected Path file = null;
	protected int idColumnSize = 0;
	protected String notes = "";
	protected NumberFormat numberFormat = null;
	protected String originalNumberFormat = null;
	protected int resultColumnSize = 0;
	private List<EvaluatedExpression> readonlyEvaluatedExpression = null;
	
	public Paper() {
		super();
		
		setNumberFormat("0.?");
	}
	
	public void clear() {
		evaluatedExpressions.clear();
		evaluator.reset();
		
		changed = true;
	}
	
	public EvaluatedExpression evaluate(String expression) throws InvalidExpressionException {
		EvaluatedExpression evaluatedExpression = evaluator.evaluate(expression);
		
		evaluatedExpressions.add(evaluatedExpression);
		
		measureExpression(evaluatedExpression);
		
		changed = true;
		
		return evaluatedExpression;
	}
	
	public void evaluateFromText(String text) {
		evaluateLines(Arrays.asList(text.split("\\n")));
	}
	
	public void evaluateLines(List<String> lines) {
		StringBuilder notesBuilder = new StringBuilder();
		boolean notesReached = false;
		
		for (String line : lines) {
			if (!notesReached) {
				if (!line.trim().isEmpty()) {
					try {
						evaluate(extractExpression(line));
					} catch (InvalidExpressionException e) {
						e.printStackTrace();
					}
				} else {
					notesReached = true;
				}
			} else {
				notesBuilder.append(line);
				notesBuilder.append("\n");
			}
		}
		
		notes = notesBuilder.toString().trim();
		
		remeasureColumnSizes();
	}
	
	public List<EvaluatedExpression> getEvaluatedExpressions() {
		if (readonlyEvaluatedExpression == null) {
			readonlyEvaluatedExpression = Collections.unmodifiableList(evaluatedExpressions);
		}
		
		return readonlyEvaluatedExpression;
	}
	
	public Evaluator getEvaluator() {
		return evaluator;
	}
	
	public int getExpressionColumnSize() {
		return expressionColumnSize;
	}
	
	public Path getFile() {
		return file;
	}
	
	public int getIdColumnSize() {
		return idColumnSize;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public NumberFormat getNumberFormat() {
		return numberFormat;
	}
	
	public int getPrecision() {
		return evaluator.getMathContext().getPrecision();
	}
	
	public int getResultColumnSize() {
		return resultColumnSize;
	}
	
	public RoundingMode getRoundingMode() {
		return evaluator.getMathContext().getRoundingMode();
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	public void load() throws IOException {
		loadFrom(file);
	}
	
	public void loadFrom(Path file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null.");
		}
		
		if (!Files.exists(file)) {
			throw new FileNotFoundException(file.toAbsolutePath().toString());
		}
		
		clear();
		
		evaluateLines(Files.readAllLines(file, StandardCharsets.UTF_8));
		
		changed = false;
	}
	
	/**
	 * Reevaluates all statements.
	 * <p>
	 * If one of the statement fails to evaluate and
	 * {@link InvalidExpressionException} will be thrown for that statement.
	 * Only when all statements could be evaluated, the current state will be
	 * overridden with the new one. So if an exception occurs, the {@link Paper}
	 * is left unchanged.
	 * 
	 * @throws InvalidExpressionException If any of the statements failed to
	 *         reevaluate.
	 */
	public void reevaluate() throws InvalidExpressionException {
		if (evaluatedExpressions.isEmpty()) {
			return;
		}
		
		Evaluator newEvaluator = new Evaluator();
		newEvaluator.setMathContext(evaluator.getMathContext());
		
		List<EvaluatedExpression> newEvaluatedExpressions = new ArrayList<>();
		
		for (EvaluatedExpression evaluatedExpression : evaluatedExpressions) {
			newEvaluatedExpressions.add(newEvaluator.evaluate(evaluatedExpression.getExpression()));
		}
		
		evaluator = newEvaluator;
		
		evaluatedExpressions.clear();
		evaluatedExpressions.addAll(newEvaluatedExpressions);
		
		remeasureColumnSizes();
	}
	
	public void save() throws IOException {
		saveTo(file);
	}
	
	public void saveTo(Path file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null.");
		}
		
		try (BufferedWriter writer = Files.newBufferedWriter(
				file,
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			writer.write(toString());
		}
		
		changed = false;
	}
	
	public void setFile(Path file) {
		this.file = file;
	}
	
	public void setNotes(String notes) {
		changed = changed || Objects.equals(this.notes, notes);
		
		this.notes = notes;
	}
	
	public void setNumberFormat(String format) {
		originalNumberFormat = format;
		
		if (format.contains("?")) {
			StringBuilder builder = new StringBuilder();
			
			for (int counter = 0; counter < evaluator.getMathContext().getPrecision(); counter++) {
				builder.append("#");
			}
			
			format = format.replaceFirst("\\?", builder.toString());
		}
		
		numberFormat = new DecimalFormat(format);
		numberFormat.setRoundingMode(evaluator.getMathContext().getRoundingMode());
	}
	
	public void setPrecision(int precision) {
		evaluator.setMathContext(new MathContext(
				precision,
				evaluator.getMathContext().getRoundingMode()));
		
		setNumberFormat(originalNumberFormat);
	}
	
	public void setRoundingMode(RoundingMode roundingMode) {
		evaluator.setMathContext(new MathContext(
				evaluator.getMathContext().getPrecision(),
				roundingMode));
		
		numberFormat.setRoundingMode(evaluator.getMathContext().getRoundingMode());
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (EvaluatedExpression evaluatedExpression : evaluatedExpressions) {
			builder.append(evaluatedExpression.format(
					idColumnSize,
					expressionColumnSize,
					resultColumnSize,
					numberFormat));
			builder.append('\n');
		}
		
		if (notes != null && notes.trim().length() > 0) {
			builder.append('\n');
			builder.append(notes);
			builder.append('\n');
		}
		
		return builder.toString();
	}
	
	protected String extractExpression(String line) {
		if (line == null) {
			return null;
		}
		
		String trimmedString = line.trim();
		
		int firstSeparatorIndex = trimmedString.indexOf(" ");
		if (firstSeparatorIndex >= 0) {
			trimmedString = trimmedString.substring(firstSeparatorIndex + 1);
		}
		
		int lastSeparatorIndex = trimmedString.lastIndexOf("=");
		if (lastSeparatorIndex >= 0) {
			trimmedString = trimmedString.substring(0, lastSeparatorIndex);
		}
		
		trimmedString = trimmedString.trim();
		
		return trimmedString;
	}
	
	protected void measureExpression(EvaluatedExpression evaluatedExpression) {
		idColumnSize = Math.max(idColumnSize, evaluatedExpression.getId().length());
		expressionColumnSize = Math.max(expressionColumnSize, evaluatedExpression.getExpression().length());
		resultColumnSize = Math.max(resultColumnSize, evaluatedExpression.getFormattedResult(numberFormat).length());
		
		if ((idColumnSize + expressionColumnSize + resultColumnSize + 4) < DEFAULT_WIDTH) {
			expressionColumnSize = DEFAULT_WIDTH - 4 - idColumnSize - resultColumnSize;
		}
	}
	
	protected void remeasureColumnSizes() {
		idColumnSize = 0;
		expressionColumnSize = 0;
		resultColumnSize = 0;
		
		for (EvaluatedExpression evaluatedExpression : evaluatedExpressions) {
			measureExpression(evaluatedExpression);
		}
		
		if ((idColumnSize + expressionColumnSize + resultColumnSize + 4) < DEFAULT_WIDTH) {
			expressionColumnSize = DEFAULT_WIDTH - 4 - idColumnSize - resultColumnSize;
		}
	}
}
