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

import javax.swing.table.DefaultTableModel;

import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.Paper;

public class PaperModel extends DefaultTableModel {
	private Paper paper = null;
	
	public PaperModel() {
		super(0, 3);
	}
	
	public Paper getPaper() {
		return paper;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	public void refresh() {
		while (getRowCount() > 0) {
			removeRow(0);
		}
		
		if (paper != null) {
			for (EvaluatedExpression evaluatedExpression : paper.getEvaluatedExpressions()) {
				addRow(new Object[] {
						" " + evaluatedExpression.getId() + " ",
						" " + evaluatedExpression.getExpression() + " ",
						" " + evaluatedExpression.getFormattedResult(paper.getNumberFormat()) + " "
				});
			}
		}
	}
	
	public void setPaper(Paper paper) {
		this.paper = paper;
		
		refresh();
	}
}
