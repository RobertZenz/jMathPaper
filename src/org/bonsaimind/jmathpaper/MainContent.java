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

package org.bonsaimind.jmathpaper;

import org.bonsaimind.jmathpaper.swt.StretchedColumnHelper;
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

public class MainContent extends Composite {
	private String bufferedInput = null;
	private Label errorLabel = null;
	private Evaluator evaluator = new Evaluator();
	private Table expressionsTable = null;
	private Text inputText = null;
	private Text notesText = null;
	private StretchedColumnHelper stretchedColumnHelper = null;
	
	public MainContent(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		SashForm mainContainer = new SashForm(this, SWT.HORIZONTAL);
		mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite expressionsComposite = new Composite(mainContainer, SWT.NONE);
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
		
		Composite notesComposite = new Composite(mainContainer, SWT.NONE);
		notesComposite.setLayout(new GridLayout(1, false));
		
		notesText = new Text(notesComposite, SWT.BORDER | SWT.MULTI);
		notesText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		mainContainer.setWeights(new int[] { (int)(parent.getSize().x * 0.75), (int)(parent.getSize().x * 0.25) });
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (visible) {
			inputText.setFocus();
		}
	}
	
	private void convertEvaluatedExpressionToTableItem(EvaluatedExpression evaluatedExpression) {
		TableItem item = new TableItem(expressionsTable, SWT.NONE);
		item.setData(evaluatedExpression);
		item.setText(0, evaluatedExpression.getId());
		item.setText(1, evaluatedExpression.getExpression());
		
		if (evaluatedExpression.isValid()) {
			item.setText(2, evaluatedExpression.getResult().toPlainString());
		} else {
			item.setText(2, "Invalid");
		}
		
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
				EvaluatedExpression evaluatedExpression = evaluator.evaluate(inputText.getText());
				
				if (evaluatedExpression.isValid()) {
					convertEvaluatedExpressionToTableItem(evaluatedExpression);
					
					resetInput();
				} else {
					errorLabel.setText(evaluatedExpression.getErrorMessage());
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
