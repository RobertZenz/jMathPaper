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
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jmathpaper.Arguments;
import org.bonsaimind.jmathpaper.Configuration;
import org.bonsaimind.jmathpaper.core.Paper;

public abstract class AbstractPapersUi implements Ui {
	protected Arguments arguments = null;
	protected Paper paper = null;
	protected List<Paper> papers = new ArrayList<>();
	
	protected AbstractPapersUi() {
		super();
	}
	
	@Override
	public void clear() {
		if (paper != null) {
			paper.clear();
		}
	}
	
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
	
	@Override
	public void load(Path file) throws IOException {
		if (paper != null) {
			paper.loadFrom(file);
		}
	}
	
	@Override
	public void new_() {
		Paper newPaper = new Paper();
		
		papers.add(newPaper);
		setPaper(newPaper);
	}
	
	@Override
	public void open(Path file) throws IOException {
		Paper loadedPaper = new Paper();
		loadedPaper.setFile(file);
		loadedPaper.loadFrom(file);
		
		papers.add(loadedPaper);
		setPaper(loadedPaper);
	}
	
	@Override
	public void reload() throws IOException {
		if (paper != null) {
			paper.load();
		}
	}
	
	@Override
	public void save() throws IOException {
		if (paper != null) {
			paper.save();
		}
	}
	
	@Override
	public void save(Path file) throws IOException {
		if (paper != null) {
			paper.saveTo(file);
		}
	}
	
	@Override
	public void start(Arguments arguments) throws Exception {
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
			initDefaultPaper();
		}
		
		if (arguments.getExpression() != null) {
			if (!CommandProcessor.applyCommand(this, arguments.getExpression())) {
				paper.evaluate(arguments.getExpression());
			} else if (exitWhenExpressionIsCommand()) {
				return;
			}
		}
		
		internalStart();
	}
	
	protected boolean exitWhenExpressionIsCommand() {
		return false;
	}
	
	protected void initDefaultPaper() throws IOException {
		open(Configuration.getGlobalPaperFile());
	}
	
	protected abstract void internalStart() throws Exception;
	
	protected void setPaper(Paper paper) {
		this.paper = paper;
	}
}
