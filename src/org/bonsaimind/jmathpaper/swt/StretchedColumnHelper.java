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

package org.bonsaimind.jmathpaper.swt;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * The {@link StretchedColumnHelper} is a simple helper class which allows to
 * stretch a single column inside a {@link Table} which is not the last.
 */
public final class StretchedColumnHelper {
	
	/**
	 * No instancing, static utility.
	 */
	private StretchedColumnHelper() {
		super();
	}
	
	/**
	 * Stretches the column at the given index.
	 * 
	 * @param table The {@link Table} that contains the column.
	 * @param columnIndexToStretch The index of the column to stretch.
	 * @throws IllegalArgumentException If the given {@link Table} is
	 *         {@code null}, the given index is negative or greater or equal the
	 *         total column count in the given {@link Table}.
	 */
	public static final void stretchColumn(Table table, int columnIndexToStretch) {
		if (table == null) {
			throw new IllegalArgumentException("table must not be null.");
		}
		
		if (columnIndexToStretch < 0 || columnIndexToStretch >= table.getColumnCount()) {
			throw new IllegalArgumentException("columnIndexToStretch must be greater than 0 and less than the total column count, was: " + columnIndexToStretch);
		}
		
		GC gc = new GC(table);
		
		int[] columnSizes = new int[table.getColumnCount()];
		
		for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
			if (columnIndex != columnIndexToStretch) {
				String text = "  " + table.getColumn(columnIndex).getText() + "  ";
				int textSize = gc.textExtent(text).x;
				
				columnSizes[columnIndex] = Math.max(columnSizes[columnIndex], textSize);
			}
		}
		
		for (int itemIndex = 0; itemIndex < table.getItemCount(); itemIndex++) {
			TableItem item = table.getItem(itemIndex);
			
			for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
				if (columnIndex != columnIndexToStretch) {
					String text = "  " + item.getText(columnIndex) + "  ";
					int textSize = gc.textExtent(text).x;
					
					columnSizes[columnIndex] = Math.max(columnSizes[columnIndex], textSize);
				}
			}
		}
		
		int notStretchedWidth = 0;
		
		for (int index = 0; index < columnSizes.length; index++) {
			if (index != columnIndexToStretch) {
				notStretchedWidth = notStretchedWidth + columnSizes[index];
			}
		}
		
		int stretchedSize = table.getSize().x - table.getBorderWidth() * 2 - notStretchedWidth;
		
		columnSizes[columnIndexToStretch] = Math.max(columnSizes[columnIndexToStretch], stretchedSize);
		
		for (int index = 0; index < table.getColumnCount(); index++) {
			table.getColumn(index).setWidth(columnSizes[index]);
		}
	}
}
