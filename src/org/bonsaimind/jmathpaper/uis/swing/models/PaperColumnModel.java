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

package org.bonsaimind.jmathpaper.uis.swing.models;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class PaperColumnModel extends DefaultTableColumnModel {
	public PaperColumnModel() {
		super();
		
		DefaultTableCellRenderer rightAlignedTableCellRenderer = new DefaultTableCellRenderer();
		rightAlignedTableCellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		
		TableColumn idColumn = new TableColumn();
		idColumn.setCellRenderer(rightAlignedTableCellRenderer);
		idColumn.setHeaderValue("ID");
		idColumn.setModelIndex(0);
		
		TableColumn expressionColumn = new TableColumn();
		expressionColumn.setCellRenderer(rightAlignedTableCellRenderer);
		expressionColumn.setHeaderValue("Expression");
		expressionColumn.setModelIndex(1);
		
		TableColumn resultColumn = new TableColumn();
		resultColumn.setCellRenderer(rightAlignedTableCellRenderer);
		resultColumn.setHeaderValue("Result");
		resultColumn.setModelIndex(2);
		
		addColumn(idColumn);
		addColumn(expressionColumn);
		addColumn(resultColumn);
	}
}
