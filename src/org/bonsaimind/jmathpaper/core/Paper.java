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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Paper {
	private static final int DEFAULT_WIDTH = 50;
	protected List<EvaluatedExpression> evaluatedExpressions = new ArrayList<>();
	protected Evaluator evaluator = new Evaluator();
	protected int expressionColumnSize = 0;
	protected Path file = null;
	protected int idColumnSize = 0;
	protected String notes = "";
	protected int resultColumnSize = 0;
	private List<EvaluatedExpression> readonlyEvaluatedExpression = null;
	
	public Paper() {
		super();
	}
	
	public void clear() {
		evaluatedExpressions.clear();
		evaluator.reset();
	}
	
	public EvaluatedExpression evaluate(String expression) throws InvalidExpressionException {
		EvaluatedExpression evaluatedExpression = evaluator.evaluate(expression);
		
		evaluatedExpressions.add(evaluatedExpression);
		
		measureExpression(evaluatedExpression);
		
		return evaluatedExpression;
	}
	
	public void fromString(List<String> string) {
		clear();
		
		StringBuilder notesBuilder = new StringBuilder();
		boolean notesReached = false;
		
		for (String line : string) {
			if (!notesReached) {
				if (!line.isEmpty()) {
					EvaluatedExpression evaluatedExpression = EvaluatedExpressionCreator.create(line);
					
					if (evaluatedExpression != null) {
						evaluator.addEvaluatedExpression(evaluatedExpression);
						evaluatedExpressions.add(evaluatedExpression);
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
		
		evaluator.setExpressionCounter(evaluatedExpressions.size());
		
		remeasureColumnSizes();
	}
	
	public void fromString(String string) {
		fromString(Arrays.asList(string.split("\\n")));
	}
	
	public List<EvaluatedExpression> getEvaluatedExpressions() {
		if (readonlyEvaluatedExpression == null) {
			readonlyEvaluatedExpression = Collections.unmodifiableList(evaluatedExpressions);
		}
		
		return readonlyEvaluatedExpression;
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
	
	public int getResultColumnSize() {
		return resultColumnSize;
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
		
		fromString(Files.readAllLines(file, StandardCharsets.UTF_8));
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
	}
	
	public void setFile(Path file) {
		this.file = file;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (EvaluatedExpression evaluatedExpression : evaluatedExpressions) {
			builder.append(evaluatedExpression.format(
					idColumnSize,
					expressionColumnSize,
					resultColumnSize));
			builder.append('\n');
		}
		
		if (notes != null && notes.trim().length() > 0) {
			builder.append('\n');
			builder.append(notes);
			builder.append('\n');
		}
		
		return builder.toString();
	}
	
	protected void measureExpression(EvaluatedExpression evaluatedExpression) {
		idColumnSize = Math.max(idColumnSize, evaluatedExpression.getId().length());
		expressionColumnSize = Math.max(expressionColumnSize, evaluatedExpression.getExpression().length());
		resultColumnSize = Math.max(resultColumnSize, evaluatedExpression.getFormattedResult().length());
		
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
