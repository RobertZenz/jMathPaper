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
import java.util.List;
import java.util.Objects;

import org.bonsaimind.jmathpaper.core.evaluatedexpressions.NumberEvaluatedExpression;

public class Paper {
	private static final int DEFAULT_WIDTH = 50;
	protected boolean changed = true;
	protected Evaluator evaluator = new Evaluator();
	protected int expressionColumnSize = 0;
	protected Path file = null;
	protected int idColumnSize = 0;
	protected String notes = "";
	protected NumberFormat numberFormat = null;
	protected String originalNumberFormat = null;
	protected int resultColumnSize = 0;
	
	public Paper() {
		super();
		
		setNumberFormat(",##0.?");
	}
	
	public void clear() {
		evaluator.reset();
		
		changed = true;
	}
	
	public EvaluatedExpression evaluate(String expression) throws InvalidExpressionException {
		EvaluatedExpression evaluatedExpression = evaluator.evaluate(expression);
		
		measureExpression(evaluatedExpression);
		
		changed = true;
		
		return evaluatedExpression;
	}
	
	public void evaluateFromText(String text) throws InvalidExpressionException {
		evaluateLines(Arrays.asList(text.split("\\r?\\n")));
	}
	
	public void evaluateLines(List<String> lines) throws InvalidExpressionException {
		StringBuilder notesBuilder = new StringBuilder();
		boolean notesReached = false;
		
		for (String line : lines) {
			if (!notesReached) {
				if (!line.trim().isEmpty()) {
					evaluate(extractExpression(line));
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
	
	public String format(EvaluatedExpression evaluatedExpression) {
		return evaluatedExpression.format(
				idColumnSize,
				expressionColumnSize,
				resultColumnSize,
				numberFormat);
	}
	
	public List<EvaluatedExpression> getEvaluatedExpressions() {
		return evaluator.getEvaluatedExpressions();
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
		return evaluator.getResultMathContext().getPrecision();
	}
	
	public int getResultColumnSize() {
		return resultColumnSize;
	}
	
	public RoundingMode getRoundingMode() {
		return evaluator.getResultMathContext().getRoundingMode();
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	public void load() throws InvalidExpressionException, IOException {
		loadFrom(file);
	}
	
	public void loadFrom(Path file) throws InvalidExpressionException, IOException {
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
	
	public EvaluatedExpression preview(String expression) throws InvalidExpressionException {
		return evaluator.preview(expression);
	}
	
	public String previewResult(String expression) {
		try {
			if (expression != null && !expression.trim().isEmpty()) {
				EvaluatedExpression previewExpression = preview(expression);
				String preview = previewExpression.getFormattedResult(numberFormat);
				
				if (previewExpression instanceof NumberEvaluatedExpression) {
					NumberEvaluatedExpression previewNumberExpression = (NumberEvaluatedExpression)previewExpression;
					
					if (!previewNumberExpression.getUnit().isOne()) {
						preview = preview + " " + previewNumberExpression.getUnit().toString();
					}
				}
				
				return preview;
			}
		} catch (InvalidExpressionException e) {
			// Ignore the exception, nothing to do.
		}
		
		return "";
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
		if (evaluator.getEvaluatedExpressions().isEmpty()) {
			return;
		}
		
		Evaluator newEvaluator = new Evaluator(evaluator);
		newEvaluator.setCalculationMathContext(evaluator.getCalculationMathContext());
		newEvaluator.setResultMathContext(evaluator.getResultMathContext());
		
		List<EvaluatedExpression> newEvaluatedExpressions = new ArrayList<>();
		
		for (EvaluatedExpression evaluatedExpression : evaluator.getEvaluatedExpressions()) {
			newEvaluatedExpressions.add(newEvaluator.evaluate(evaluatedExpression.getExpression()));
		}
		
		evaluator = newEvaluator;
		
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
		changed = changed || !Objects.equals(this.notes, notes);
		
		this.notes = notes;
	}
	
	public void setNumberFormat(String format) {
		originalNumberFormat = format;
		
		if (format.contains("?")) {
			StringBuilder builder = new StringBuilder();
			
			for (int counter = 0; counter < evaluator.getResultMathContext().getPrecision(); counter++) {
				builder.append("#");
			}
			
			format = format.replaceFirst("\\?", builder.toString());
		}
		
		numberFormat = new DecimalFormat(format);
		numberFormat.setRoundingMode(evaluator.getResultMathContext().getRoundingMode());
	}
	
	public void setPrecision(int precision) {
		if (precision <= 0) {
			evaluator.setCalculationMathContext(new MathContext(
					precision,
					evaluator.getCalculationMathContext().getRoundingMode()));
		} else {
			evaluator.setCalculationMathContext(new MathContext(
					Math.max(precision * 2, 4),
					evaluator.getCalculationMathContext().getRoundingMode()));
		}
		evaluator.setResultMathContext(new MathContext(
				precision,
				evaluator.getResultMathContext().getRoundingMode()));
		
		setNumberFormat(originalNumberFormat);
	}
	
	public void setRoundingMode(RoundingMode roundingMode) {
		evaluator.setCalculationMathContext(new MathContext(
				evaluator.getCalculationMathContext().getPrecision(),
				roundingMode));
		evaluator.setResultMathContext(new MathContext(
				evaluator.getResultMathContext().getPrecision(),
				roundingMode));
		
		numberFormat.setRoundingMode(evaluator.getResultMathContext().getRoundingMode());
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (EvaluatedExpression evaluatedExpression : evaluator.getEvaluatedExpressions()) {
			builder.append(format(evaluatedExpression));
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
		
		for (EvaluatedExpression evaluatedExpression : evaluator.getEvaluatedExpressions()) {
			measureExpression(evaluatedExpression);
		}
		
		if ((idColumnSize + expressionColumnSize + resultColumnSize + 4) < DEFAULT_WIDTH) {
			expressionColumnSize = DEFAULT_WIDTH - 4 - idColumnSize - resultColumnSize;
		}
	}
}
