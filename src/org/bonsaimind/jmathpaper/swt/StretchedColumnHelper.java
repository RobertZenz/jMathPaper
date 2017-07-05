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

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * The {@link StretchedColumnHelper} is a simple helper class which allows to
 * stretch a single column inside a {@link Table} which is not the last.
 */
public class StretchedColumnHelper {
	/** The calculated and cached column sizes. */
	int[] columnSizes = null;
	
	/** The index of the column to stretch. */
	private int columnIndexToStretch = 0;
	
	/** The {@link GC} which is used for measuring strings. */
	private GC gc = null;
	
	/** The last seen item index. */
	private int lastSeenItemIndex = -1;
	
	/** The parent {@link Table} that this is attached to. */
	private Table parentTable = null;
	
	/**
	 * Creates a new instance of {@link StretchedColumnHelper}.
	 * 
	 * @param parentTable The parent {@link Table}.
	 * @param columnIndexToStretch The index of the column to stretch.
	 */
	public StretchedColumnHelper(Table parentTable, int columnIndexToStretch) {
		super();
		
		if (parentTable == null) {
			throw new IllegalArgumentException("parentTable cannot be null.");
		}
		
		if (parentTable.getColumnCount() == 0) {
			throw new IllegalArgumentException("The parentTable must have added columns.");
		}
		
		if (columnIndexToStretch < 0 || columnIndexToStretch >= parentTable.getColumnCount()) {
			throw new IllegalArgumentException("columnIndexToStretch is outside of allowed range: "
					+ columnIndexToStretch
					+ ", must be between 0 and "
					+ (parentTable.getColumnCount() - 1)
					+ ".");
		}
		
		this.parentTable = parentTable;
		this.columnIndexToStretch = columnIndexToStretch;
		
		columnSizes = new int[parentTable.getColumnCount()];
		gc = new GC(parentTable);
		
		parentTable.addControlListener(new ControlListener() {
			@Override
			public void controlMoved(ControlEvent e) {
				// Nothing to do.
			}
			
			@Override
			public void controlResized(ControlEvent e) {
				pack();
			}
		});
	}
	
	/**
	 * Packs/resizes all columns.
	 */
	public void pack() {
		updateColumnSizes();
		
		int notStretchedWidth = 0;
		
		for (int index = 0; index < columnSizes.length; index++) {
			if (index != columnIndexToStretch) {
				notStretchedWidth = notStretchedWidth + columnSizes[index];
			}
		}
		
		int stretchedSize = parentTable.getSize().x - parentTable.getBorderWidth() * 2 - notStretchedWidth;
		
		columnSizes[columnIndexToStretch] = Math.max(0, stretchedSize);
		
		for (int columnIndex = 0; columnIndex < parentTable.getColumnCount(); columnIndex++) {
			parentTable.getColumn(columnIndex).setWidth(columnSizes[columnIndex]);
		}
	}
	
	/**
	 * Recalculates the size of all columns.
	 */
	public void recalculateSizes() {
		lastSeenItemIndex = -1;
		
		for (int index = 0; index < columnSizes.length; index++) {
			columnSizes[index] = 0;
		}
		
		updateColumnSizes();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(getClass().getName());
		builder.append("@");
		builder.append(Integer.toHexString(System.identityHashCode(this)));
		
		builder.append("[");
		builder.append(lastSeenItemIndex);
		builder.append("]");
		
		builder.append("[");
		for (int index = 0; index < columnSizes.length; index++) {
			if (index == columnIndexToStretch) {
				builder.append("*");
			}
			builder.append(columnSizes[index]);
			builder.append(",");
		}
		builder.delete(builder.length() - 1, builder.length());
		builder.append("]");
		
		return builder.toString();
	}
	
	/**
	 * Measures the given text, for that it adds two spaces at the end and at
	 * the start.
	 * 
	 * @param text The text to measure.
	 * @return The size of the text.
	 */
	protected int measureText(String text) {
		return gc.textExtent("  " + text + "  ").x;
	}
	
	/**
	 * Updates the column sizes (if nedded).
	 */
	protected void updateColumnSizes() {
		if (lastSeenItemIndex == -1) {
			updateWithCaptionSizes();
		}
		
		if (lastSeenItemIndex <= parentTable.getItemCount() - 1) {
			while (lastSeenItemIndex < parentTable.getItemCount() - 1) {
				lastSeenItemIndex++;
				
				TableItem item = parentTable.getItem(lastSeenItemIndex);
				
				for (int columnIndex = 0; columnIndex < parentTable.getColumnCount(); columnIndex++) {
					columnSizes[columnIndex] = Math.max(
							columnSizes[columnIndex],
							measureText(item.getText(columnIndex)));
				}
			}
		}
	}
	
	/**
	 * Updates the column sizes with the size of the captions.
	 */
	protected void updateWithCaptionSizes() {
		for (int columnIndex = 0; columnIndex < parentTable.getColumnCount(); columnIndex++) {
			if (columnIndex != columnIndexToStretch) {
				columnSizes[columnIndex] = Math.max(
						columnSizes[columnIndex],
						measureText(parentTable.getColumn(columnIndex).getText()));
			}
		}
	}
}
