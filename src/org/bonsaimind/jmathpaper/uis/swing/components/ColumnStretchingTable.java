/*
 * Copyright 2019, Robert 'Bobby' Zenz
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

package org.bonsaimind.jmathpaper.uis.swing.components;

import java.awt.Component;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ColumnStretchingTable extends JTable {
	protected int stretchedColumnIndex = -1;
	
	public ColumnStretchingTable(int stretchedColumnIndex) {
		super();
		
		this.stretchedColumnIndex = stretchedColumnIndex;
		
		setAutoResizeMode(AUTO_RESIZE_OFF);
		addHierarchyBoundsListener(new ColumnResizingHierarchyBoundsListener());
	}
	
	public int getStretchedColumnIndex() {
		return stretchedColumnIndex;
	}
	
	public void resizeColumns() {
		int totalColumnWidth = 0;
		
		for (int columnIndex = 0; columnIndex < getColumnModel().getColumnCount(); columnIndex++) {
			if (columnIndex != stretchedColumnIndex) {
				TableColumn column = getColumnModel().getColumn(columnIndex);
				int columnWidth = 0;
				
				columnWidth = Math.max(columnWidth, calculateHeaderSize(column, columnIndex));
				columnWidth = Math.max(columnWidth, calculateDataSize(column, columnIndex));
				
				column.setPreferredWidth(columnWidth);
				
				totalColumnWidth = totalColumnWidth + columnWidth;
			}
		}
		
		if (stretchedColumnIndex >= 0) {
			TableColumn column = getColumnModel().getColumn(stretchedColumnIndex);
			
			column.setPreferredWidth(getParent().getWidth() - totalColumnWidth);
		}
	}
	
	public void setStretchedColumnIndex(int stretchedColumnIndex) {
		this.stretchedColumnIndex = stretchedColumnIndex;
	}
	
	protected int calculateDataSize(TableColumn column, int columnIndex) {
		int columnWidth = 0;
		
		for (int rowIndex = 0; rowIndex < getModel().getRowCount(); rowIndex++) {
			TableCellRenderer renderer = getCellRenderer(rowIndex, columnIndex);
			
			Component component = renderer.getTableCellRendererComponent(
					this,
					" " + getValueAt(rowIndex, columnIndex).toString() + " ",
					false,
					false,
					rowIndex,
					columnIndex);
			
			int cellWidth = (int)component.getPreferredSize().getWidth();
			
			if (cellWidth >= columnWidth) {
				columnWidth = cellWidth;
			}
		}
		
		if (columnWidth < column.getMinWidth()) {
			columnWidth = column.getMinWidth();
		}
		
		return columnWidth;
	}
	
	protected int calculateHeaderSize(TableColumn column, int columnIndex) {
		TableCellRenderer headerRenderer = column.getHeaderRenderer();
		
		if (headerRenderer == null) {
			headerRenderer = getTableHeader().getDefaultRenderer();
		}
		
		Component headerComponent = headerRenderer.getTableCellRendererComponent(
				this,
				"  " + column.getHeaderValue().toString() + "  ",
				false,
				false,
				-1,
				columnIndex);
		
		return headerComponent.getPreferredSize().width;
	}
	
	private final class ColumnResizingHierarchyBoundsListener implements HierarchyBoundsListener {
		public ColumnResizingHierarchyBoundsListener() {
			super();
		}
		
		@Override
		public void ancestorMoved(HierarchyEvent e) {
			// Nothing to do.
		}
		
		@Override
		public void ancestorResized(HierarchyEvent e) {
			resizeColumns();
		}
	}
}
