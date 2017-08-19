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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class MainContent extends Composite {
	private String bufferedInput = null;
	private Label errorLabel = null;
	private Evaluator evaluator = new Evaluator();
	private Composite expressionsComposite = null;
	private Table expressionsTable = null;
	private Text inputText = null;
	private SashForm mainSashForm = null;
	private Composite notesComposite = null;
	private Text notesText = null;
	private StretchedColumnHelper stretchedColumnHelper = null;
	
	public MainContent(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Menu menu = new Menu(getShell(), SWT.BAR);
		getShell().setMenuBar(menu);
		
		MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		fileMenuItem.setText("&File");
		
		Menu fileMenu = new Menu(getShell(), SWT.DROP_DOWN);
		fileMenuItem.setMenu(fileMenu);
		
		MenuItem quitMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		quitMenuItem.setAccelerator(SWT.CTRL | 'Q');
		quitMenuItem.setText("&Quit\tCtrl+Q");
		quitMenuItem.setToolTipText("Quit jMathPaper.");
		quitMenuItem.addListener(SWT.Selection, this::onQuitPushed);
		
		MenuItem viewMenuItem = new MenuItem(menu, SWT.CASCADE);
		viewMenuItem.setText("&View");
		
		Menu viewMenu = new Menu(getShell(), SWT.DROP_DOWN);
		viewMenuItem.setMenu(viewMenu);
		
		MenuItem notesMenuItem = new MenuItem(viewMenu, SWT.CHECK);
		notesMenuItem.setAccelerator(SWT.F4);
		notesMenuItem.setSelection(true);
		notesMenuItem.setText("&Notes\tF4");
		notesMenuItem.setToolTipText("Toggles the visibility of the notes area.");
		notesMenuItem.addListener(SWT.Selection, this::onShowHideNotesSelected);
		
		mainSashForm = new SashForm(this, SWT.HORIZONTAL);
		mainSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		expressionsComposite = new Composite(mainSashForm, SWT.NONE);
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
		
		notesComposite = new Composite(mainSashForm, SWT.NONE);
		notesComposite.setLayout(new GridLayout(1, false));
		
		notesText = new Text(notesComposite, SWT.BORDER | SWT.MULTI);
		notesText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		mainSashForm.setWeights(new int[] { (int)(parent.getSize().x * 0.75), (int)(parent.getSize().x * 0.25) });
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
	
	private void onQuitPushed(Event event) {
		getShell().setVisible(false);
		getShell().dispose();
	}
	
	private void onShowHideNotesSelected(Event event) {
		if (((MenuItem)event.widget).getSelection()) {
			mainSashForm.setMaximizedControl(null);
		} else {
			mainSashForm.setMaximizedControl(expressionsComposite);
		}
		
		inputText.setFocus();
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
