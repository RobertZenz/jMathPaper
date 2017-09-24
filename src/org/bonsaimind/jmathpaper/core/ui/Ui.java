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

import org.bonsaimind.jmathpaper.Arguments;

public interface Ui {
	public void clear();
	
	public void close();
	
	public void load(Path file) throws IOException;
	
	public void new_();
	
	public void open(Path file) throws IOException;
	
	public void quit();
	
	public void reload() throws IOException;
	
	public void save() throws IOException;
	
	public void save(Path file) throws IOException;
	
	public void start(Arguments arguments) throws Exception;
}
