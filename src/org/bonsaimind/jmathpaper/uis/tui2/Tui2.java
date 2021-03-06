/*
 * Copyright 2018, Robert 'Bobby' Zenz
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

package org.bonsaimind.jmathpaper.uis.tui2;

import java.io.IOException;
import java.util.Arrays;

import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.Paper;
import org.bonsaimind.jmathpaper.core.ui.AbstractPapersUi;
import org.bonsaimind.jmathpaper.core.ui.UiParameters;
import org.bonsaimind.jmathpaper.uis.tui2.components.EventExtendedTextBox;
import org.bonsaimind.jmathpaper.uis.tui2.components.PseudoTabBar;

import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.BorderLayout.Location;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Separator;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.TextBox.Style;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.Window.Hint;
import com.googlecode.lanterna.gui2.table.DefaultTableRenderer;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class Tui2 extends AbstractPapersUi {
	private String bufferedInput = null;
	private Label errorLabel = null;
	private Table<String> expressionsTable = null;
	private EventExtendedTextBox inputTextBox = null;
	private TextBox notesTextBox = null;
	private Screen screen = null;
	private PseudoTabBar<Paper> tabBar = null;
	private Window window = null;
	
	public Tui2() {
		super();
	}
	
	@Override
	public void init(UiParameters uiParameters) throws Exception {
		super.init(uiParameters);
		
		tabBar = new PseudoTabBar<Paper>()
				.addSelectedTabChangedListener(this::onSelectedTabChanged);
		
		Panel tabPanel = new Panel();
		tabPanel.setLayoutManager(new BorderLayout());
		tabPanel.addComponent(tabBar, Location.TOP);
		tabPanel.addComponent(new Separator(Direction.HORIZONTAL), Location.BOTTOM);
		
		notesTextBox = new EventExtendedTextBox(Style.MULTI_LINE)
				.addTextChangedHandler(this::onNotesTextBoxChanged);
		
		Panel notesPanel = new Panel();
		notesPanel.setLayoutManager(new BorderLayout());
		notesPanel.addComponent(new Separator(Direction.VERTICAL), Location.CENTER);
		notesPanel.addComponent(notesTextBox, Location.RIGHT);
		
		errorLabel = new Label("");
		
		inputTextBox = new EventExtendedTextBox(Style.SINGLE_LINE)
				.addTextChangedHandler(this::onInputTextChanged)
				.addHandler(new KeyStroke(KeyType.ArrowDown), this::onInputTextBoxDownKey)
				.addHandler(new KeyStroke(KeyType.Enter), this::onInputTextBoxEnterKey)
				.addHandler(new KeyStroke(KeyType.ArrowUp), this::onInputTextBoxUpKey);
		inputTextBox.setHorizontalFocusSwitching(false);
		inputTextBox.setVerticalFocusSwitching(false);
		
		Panel southPanel = new Panel();
		southPanel.setLayoutManager(new BorderLayout());
		southPanel.addComponent(new Separator(Direction.HORIZONTAL), Location.TOP);
		southPanel.addComponent(errorLabel, Location.CENTER);
		southPanel.addComponent(inputTextBox, Location.BOTTOM);
		
		expressionsTable = new Table<String>("ID", "Expression", "Result")
				.setCellSelection(false)
				.setEscapeByArrowKey(false);
		((DefaultTableRenderer<?>)expressionsTable.getRenderer()).setExpandableColumns(Arrays.asList(Integer.valueOf(1)));
		
		Panel paperPanel = new Panel();
		paperPanel.setLayoutManager(new BorderLayout());
		paperPanel.addComponent(expressionsTable, Location.CENTER);
		paperPanel.addComponent(southPanel, Location.BOTTOM);
		
		Panel mainPanel = new Panel();
		mainPanel.setLayoutManager(new BorderLayout());
		mainPanel.addComponent(tabPanel, Location.TOP);
		mainPanel.addComponent(paperPanel, Location.CENTER);
		mainPanel.addComponent(notesPanel, Location.RIGHT);
		
		window = new BasicWindow("Test");
		window.setHints(Arrays.asList(Hint.FULL_SCREEN, Hint.NO_DECORATIONS));
		window.setComponent(mainPanel);
		
		inputTextBox.takeFocus();
	}
	
	@Override
	public void quit() {
		try {
			window.close();
			screen.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public void run() throws Exception {
		Terminal terminal = new DefaultTerminalFactory().createTerminal();
		
		screen = new TerminalScreen(terminal);
		screen.startScreen();
		
		MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
		gui.setTheme(SimpleTheme.makeTheme(
				false,
				ANSI.WHITE,
				ANSI.BLACK,
				ANSI.WHITE,
				ANSI.BLACK,
				ANSI.BLACK,
				ANSI.WHITE,
				ANSI.BLACK));
		
		gui.addWindowAndWait(window);
	}
	
	@Override
	protected void currentPaperHasBeenAdded() {
		addTab(paper);
	}
	
	@Override
	protected void currentPaperHasBeenModified() {
		clearExpressionsTable();
		updateExpressionTable();
	}
	
	@Override
	protected void currentPaperHasBeenRemoved() {
		tabBar.removeTab(tabBar.getSelectedTabIndex());
	}
	
	@Override
	protected void currentPaperHasBeenReset() {
		clearExpressionsTable();
		updateExpressionTable();
	}
	
	@Override
	protected void currentSelectedPaperHasChanged() {
		updateExpressionTable();
	}
	
	protected void setMessage(String message) {
		if (message == null) {
			errorLabel.setText(" ");
		} else {
			errorLabel.setText(message + " ");
		}
	}
	
	@Override
	protected void setPaper(Paper paper) throws IllegalStateException {
		super.setPaper(paper);
		
		if (paper != null) {
			tabBar.setSelectedTab(papers.indexOf(paper));
		}
	}
	
	private void addTab(Paper paper) {
		tabBar.addTab(papers.indexOf(paper), getShortPaperTitle(paper), paper);
	}
	
	private void clearExpressionsTable() {
		while (expressionsTable.getTableModel().getRowCount() > 0) {
			expressionsTable.getTableModel().removeRow(0);
		}
	}
	
	private void onInputTextBoxDownKey(EventExtendedTextBox textBox) {
		selectNextRow(1);
	}
	
	private void onInputTextBoxEnterKey(EventExtendedTextBox textBox) {
		try {
			process(inputTextBox.getText());
			resetInput();
		} catch (Exception e) {
			if (e.getMessage() != null) {
				setMessage(e.getMessage());
			} else {
				setMessage("No details available: " + e.getClass().getSimpleName());
			}
		}
	}
	
	private void onInputTextBoxUpKey(EventExtendedTextBox textBox) {
		selectNextRow(-1);
	}
	
	private void onInputTextChanged(EventExtendedTextBox textBox, String oldText, String newText) {
		setMessage(paper.previewResult(newText));
	}
	
	private void onNotesTextBoxChanged(EventExtendedTextBox textBox, String oldText, String newText) {
		paper.setNotes(newText);
	}
	
	private void onSelectedTabChanged(PseudoTabBar<?> pseudoBar, int oldSelectedTabIndex, int newSelectedTabIndex) {
		clearExpressionsTable();
		
		if (newSelectedTabIndex >= 0) {
			setPaper(papers.get(newSelectedTabIndex));
			updateExpressionTable();
		}
	}
	
	private void resetInput() {
		inputTextBox.setText("");
		setMessage("");
		
		expressionsTable.setSelectedRow(-1);
		bufferedInput = null;
	}
	
	private void selectNextRow(int direction) {
		if (expressionsTable.getTableModel().getRowCount() == 0) {
			return;
		}
		
		int rowCount = expressionsTable.getTableModel().getRowCount();
		int selectedRow = expressionsTable.getSelectedRow();
		
		if (selectedRow >= 0) {
			selectedRow = selectedRow + direction;
			
			if (selectedRow < 0) {
				selectedRow = 0;
			} else if (selectedRow >= rowCount) {
				selectedRow = -1;
			}
		} else if (direction < 0) {
			selectedRow = rowCount - 1;
		}
		
		expressionsTable.setSelectedRow(selectedRow);
		expressionsTable.invalidate();
		
		if (selectedRow < 0) {
			if (bufferedInput != null) {
				inputTextBox.setText(bufferedInput);
				bufferedInput = null;
			}
		} else {
			if (bufferedInput == null) {
				bufferedInput = inputTextBox.getText();
			}
			
			inputTextBox.setText(expressionsTable.getTableModel().getRow(selectedRow).get(1));
		}
	}
	
	private void updateExpressionTable() {
		if (paper != null) {
			for (int index = expressionsTable.getTableModel().getRowCount(); index < paper.getEvaluatedExpressions().size(); index++) {
				EvaluatedExpression evaluatedExpression = paper.getEvaluatedExpressions().get(index);
				
				expressionsTable.getTableModel().addRow(
						evaluatedExpression.getId(),
						evaluatedExpression.getExpression(),
						evaluatedExpression.getFormattedResult(paper.getNumberFormat()));
				expressionsTable.setSelectedRow(-1);
			}
		}
	}
}
