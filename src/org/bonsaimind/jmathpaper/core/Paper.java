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
	protected List<EvaluatedExpression> evaluatedExpressions = new ArrayList<>();
	protected Evaluator evaluator = new Evaluator();
	protected Path file = null;
	protected String notes = "";
	private List<EvaluatedExpression> readonlyEvaluatedExpression = null;
	
	public Paper() {
		super();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Paper other = (Paper)obj;
		if (evaluatedExpressions == null) {
			if (other.evaluatedExpressions != null) {
				return false;
			}
		} else if (other.evaluatedExpressions == null) {
			return false;
		} else if (evaluatedExpressions.size() != other.evaluatedExpressions.size()) {
			return false;
		} else {
			for (int index = 0; index < evaluatedExpressions.size(); index++) {
				if (!evaluatedExpressions.get(index).equals(other.evaluatedExpressions.get(index))) {
					return false;
				}
			}
		}
		if (notes == null) {
			if (other.notes != null) {
				return false;
			}
		} else if (!notes.equals(other.notes)) {
			return false;
		}
		return true;
	}
	
	public EvaluatedExpression evaluate(String expression) {
		EvaluatedExpression evaluatedExpression = evaluator.evaluate(expression);
		
		if (evaluatedExpression.isValid()) {
			evaluator.addEvaluatedExpression(evaluatedExpression);
			evaluatedExpressions.add(evaluatedExpression);
		}
		
		return evaluatedExpression;
	}
	
	public void fromString(List<String> string) {
		evaluatedExpressions.clear();
		evaluator.reset();
		
		StringBuilder notesBuilder = new StringBuilder();
		boolean notesReached = false;
		
		for (String line : string) {
			if (!notesReached) {
				if (!line.isEmpty()) {
					EvaluatedExpression evaluatedExpression = EvaluatedExpression.fromString(line);
					
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
	
	public Path getFile() {
		return file;
	}
	
	public String getNotes() {
		return notes;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (EvaluatedExpression evaluatedExpression : evaluatedExpressions) {
			result = prime * result + evaluatedExpression.hashCode();
		}
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
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
		
		this.file = file;
		
		fromString(Files.readAllLines(file, StandardCharsets.UTF_8));
	}
	
	public void setFile(Path file) {
		this.file = file;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public void store() throws IOException {
		storeTo(file);
	}
	
	public void storeTo(Path file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null.");
		}
		
		this.file = file;
		
		try (BufferedWriter writer = Files.newBufferedWriter(
				file,
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			writer.write(toString());
		}
	}
	
	@Override
	public String toString() {
		int idLength = 0;
		int expressionLength = 0;
		int resultLength = 0;
		
		for (EvaluatedExpression evaluatedExpression : evaluatedExpressions) {
			idLength = Math.max(idLength, evaluatedExpression.getId().length());
			expressionLength = Math.max(expressionLength, evaluatedExpression.getExpression().length());
			resultLength = Math.max(resultLength, evaluatedExpression.getResult().toPlainString().length());
		}
		
		if ((idLength + expressionLength + resultLength + 4) < 60) {
			expressionLength = 60 - 4 - idLength - resultLength;
		}
		
		StringBuilder builder = new StringBuilder();
		
		for (EvaluatedExpression evaluatedExpression : evaluatedExpressions) {
			builder.append(evaluatedExpression.toString(idLength, expressionLength, resultLength));
			builder.append('\n');
		}
		
		builder.append('\n');
		
		builder.append(notes);
		
		builder.append('\n');
		
		return builder.toString();
	}
}
