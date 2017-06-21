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

import org.eclipse.swt.widgets.Table;

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
		
		int[] originalOrder = table.getColumnOrder();
		
		// Move the to be stretched column to the end of the table, so that it
		// is the last (which will be stretched automatically.
		int[] stretchOrder = new int[originalOrder.length];
		
		System.arraycopy(originalOrder, 0, stretchOrder, 0, originalOrder.length);
		
		for (int index = columnIndexToStretch; index < stretchOrder.length - 1; index++) {
			int swap = stretchOrder[index];
			
			stretchOrder[index] = stretchOrder[index + 1];
			stretchOrder[index + 1] = swap;
		}
		
		// Set the new order.
		table.setColumnOrder(stretchOrder);
		
		// Now measure the size of every column, except the last, which is the
		// one which will be stretched.
		int[] sizes = new int[table.getColumnCount()];
		int offset = 0;
		
		for (int index = 0; index < table.getColumnCount() - 1; index++) {
			if (index == columnIndexToStretch) {
				offset = 1;
			}
			
			table.getColumn(index).pack();
			sizes[index + offset] = table.getColumn(index).getWidth();
		}
		
		// Restore the original order.
		table.setColumnOrder(originalOrder);
		
		// Calculate the total width of all columns.
		// Don't worry, the to be stretched column size is 0, so no special case
		// needed here.
		int totalWidth = 0;
		
		for (int index = 0; index < sizes.length; index++) {
			totalWidth = totalWidth + sizes[index];
		}
		
		// Now calculate the size of the stretched column.
		sizes[columnIndexToStretch] = table.getSize().x - table.getBorderWidth() * 2 - totalWidth;
		
		// And finally apply the new sizes.
		for (int index = 0; index < table.getColumnCount(); index++) {
			table.getColumn(index).setWidth(sizes[index]);
		}
	}
}
