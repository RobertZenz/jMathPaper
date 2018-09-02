/*
 * Copyright 2017, Robert 'Bobby' Zenz
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

package org.bonsaimind.jmathpaper.uis.swt;

import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.InvalidExpressionException;
import org.bonsaimind.jmathpaper.core.Paper;
import org.bonsaimind.jmathpaper.core.ui.CommandExecutionException;
import org.bonsaimind.jmathpaper.core.ui.Ui;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class PaperComponent extends SashForm {
	private String bufferedInput = null;
	private Label errorLabel = null;
	private Composite expressionsComposite = null;
	private Table expressionsTable = null;
	private Text inputText = null;
	private Composite notesComposite = null;
	private Text notesText = null;
	private Paper paper = null;
	private StretchedColumnHelper stretchedColumnHelper = null;
	private Ui ui = null;
	
	public PaperComponent(Composite parent, Ui ui, Paper paper, int style) {
		super(parent, style);
		
		this.ui = ui;
		this.paper = paper;
		
		expressionsComposite = new Composite(this, SWT.NONE);
		expressionsComposite.setLayout(new GridLayout(1, false));
		
		expressionsTable = new Table(expressionsComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
		expressionsTable.addListener(SWT.Selection, this::onExpressionsTableSelectionChanged);
		expressionsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		expressionsTable.setHeaderVisible(true);
		expressionsTable.setLinesVisible(true);
		
		TableColumn idColumn = new TableColumn(expressionsTable, SWT.RIGHT);
		idColumn.setText("ID");
		
		TableColumn expressionColumn = new TableColumn(expressionsTable, SWT.RIGHT);
		expressionColumn.setText("Expression");
		
		TableColumn resultColumn = new TableColumn(expressionsTable, SWT.RIGHT);
		resultColumn.setText("Result");
		
		stretchedColumnHelper = new StretchedColumnHelper(expressionsTable, 1);
		stretchedColumnHelper.pack();
		
		errorLabel = new Label(expressionsComposite, SWT.RIGHT);
		errorLabel.setText("");
		errorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		inputText = new Text(expressionsComposite, SWT.BORDER | SWT.RIGHT);
		inputText.addListener(SWT.KeyDown, this::onInputTextDownKey);
		inputText.addListener(SWT.KeyDown, this::onInputTextEscapeKey);
		inputText.addListener(SWT.KeyDown, this::onInputTextUpKey);
		inputText.addListener(SWT.Traverse, this::onInputTextReturnKey);
		inputText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		notesComposite = new Composite(this, SWT.NONE);
		notesComposite.setLayout(new GridLayout(1, false));
		
		notesText = new Text(notesComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		notesText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		notesText.addListener(SWT.CHANGED, this::onNotesTextChanged);
	}
	
	public void clearExpressions() {
		expressionsTable.removeAll();
		
		resetInput();
	}
	
	public String getNotes() {
		return notesText.getText();
	}
	
	public Paper getPaper() {
		return paper;
	}
	
	@Override
	public boolean setFocus() {
		return inputText.setFocus();
	}
	
	public void setNotesVisible(boolean visible) {
		if (visible) {
			setMaximizedControl(null);
		} else {
			setMaximizedControl(expressionsComposite);
		}
		
		inputText.setFocus();
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (visible) {
			setWeights(new int[] { (int)(getSize().x * 0.75), (int)(getSize().x * 0.25) });
			
			inputText.setFocus();
		}
	}
	
	public void updateExpressions() {
		for (int index = expressionsTable.getItemCount(); index < paper.getEvaluatedExpressions().size(); index++) {
			EvaluatedExpression evaluatedExpression = paper.getEvaluatedExpressions().get(index);
			
			convertEvaluatedExpressionToTableItem(evaluatedExpression);
		}
		
		stretchedColumnHelper.recalculateSizes();
		stretchedColumnHelper.pack();
	}
	
	public void updateNotes() {
		notesText.setText(paper.getNotes());
	}
	
	private void convertEvaluatedExpressionToTableItem(EvaluatedExpression evaluatedExpression) {
		TableItem item = new TableItem(expressionsTable, SWT.NONE);
		item.setData(evaluatedExpression);
		item.setText(0, evaluatedExpression.getId());
		item.setText(1, evaluatedExpression.getExpression());
		item.setText(2, evaluatedExpression.getFormattedResult(paper.getNumberFormat()));
		
		stretchedColumnHelper.pack();
	}
	
	private void onExpressionsTableSelectionChanged(Event event) {
		updateCurrentExpression();
	}
	
	private void onInputTextDownKey(Event event) {
		if (event.keyCode == SWT.ARROW_DOWN) {
			if (expressionsTable.getItemCount() > 0) {
				if (expressionsTable.getSelectionIndex() >= 0
						&& expressionsTable.getSelectionIndex() < expressionsTable.getItemCount() - 1) {
					expressionsTable.setSelection(expressionsTable.getSelectionIndex() + 1);
					updateCurrentExpression();
				} else if (expressionsTable.getSelectionIndex() == expressionsTable.getItemCount() - 1) {
					expressionsTable.setSelection(-1);
					updateCurrentExpression();
				}
			}
			
			event.doit = false;
		}
	}
	
	private void onInputTextEscapeKey(Event event) {
		if (event.keyCode == SWT.ESC) {
			if (bufferedInput != null) {
				expressionsTable.setSelection(-1);
				updateCurrentExpression();
			} else {
				resetInput();
			}
		}
	}
	
	private void onInputTextReturnKey(Event event) {
		if (event.detail == SWT.TRAVERSE_RETURN) {
			if (inputText.getText().length() > 0) {
				try {
					ui.process(inputText.getText());
					
					// The input might have been a command which closed the UI.
					if (!isDisposed()) {
						resetInput();
					}
				} catch (CommandExecutionException | InvalidExpressionException e) {
					if (e.getMessage() != null) {
						errorLabel.setText(e.getMessage());
					} else {
						errorLabel.setText("No details available: " + e.getClass().getSimpleName());
					}
				}
			} else {
				resetInput();
			}
		}
	}
	
	private void onInputTextUpKey(Event event) {
		if (event.keyCode == SWT.ARROW_UP) {
			if (expressionsTable.getItemCount() > 0) {
				if (expressionsTable.getSelectionIndex() == -1) {
					expressionsTable.setSelection(expressionsTable.getItemCount() - 1);
					updateCurrentExpression();
				} else if (expressionsTable.getSelectionIndex() > 0) {
					expressionsTable.setSelection(expressionsTable.getSelectionIndex() - 1);
					updateCurrentExpression();
				}
			}
			
			event.doit = false;
		}
	}
	
	private void onNotesTextChanged(Event event) {
		paper.setNotes(notesText.getText());
	}
	
	private void resetInput() {
		bufferedInput = null;
		errorLabel.setText("");
		inputText.setText("");
		
		expressionsTable.setSelection(-1);
		// -2 seems to be required because the top index can not be the last
		// item.
		expressionsTable.setTopIndex(expressionsTable.getItemCount() - 2);
		
		stretchedColumnHelper.pack();
	}
	
	private void updateCurrentExpression() {
		if (expressionsTable.getSelectionCount() > 0) {
			if (bufferedInput == null) {
				bufferedInput = inputText.getText();
			}
			
			TableItem selectedItem = expressionsTable.getItem(expressionsTable.getSelectionIndex());
			EvaluatedExpression selectedExpression = (EvaluatedExpression)selectedItem.getData();
			
			inputText.setText(selectedExpression.getExpression());
		} else {
			expressionsTable.setTopIndex(expressionsTable.getItemCount() - 1);
			
			if (bufferedInput != null) {
				inputText.setText(bufferedInput);
				bufferedInput = null;
			}
		}
		
		inputText.setFocus();
		inputText.setSelection(inputText.getText().length());
	}
}