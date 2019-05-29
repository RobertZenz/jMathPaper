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

package org.bonsaimind.jmathpaper.uis.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

import org.bonsaimind.jmathpaper.core.InvalidExpressionException;
import org.bonsaimind.jmathpaper.core.Paper;
import org.bonsaimind.jmathpaper.core.ui.CommandExecutionException;
import org.bonsaimind.jmathpaper.core.ui.Ui;
import org.bonsaimind.jmathpaper.uis.gui.events.KeyPressedListener;
import org.bonsaimind.jmathpaper.uis.gui.events.NotifyingDocumentListener;
import org.bonsaimind.jmathpaper.uis.gui.models.PaperColumnModel;
import org.bonsaimind.jmathpaper.uis.gui.models.PaperModel;

import com.sibvisions.rad.ui.swing.ext.JVxSplitPane;

public class PaperComponent extends JComponent {
	private static final double DEFAULT_DIVIDER_LOCATION = 0.75d;
	private String bufferedInput = null;
	private ColumnStretchingTable expressionsTable = null;
	private boolean firstRepaint = true;
	private JTextField inputTextField = null;
	private JLabel messageLabel = null;
	private JScrollPane notesScrollContainer = null;
	private JTextArea notesTextArea = null;
	private int originalDividerLocation = -1;
	private int originalDividerSize = -1;
	private Paper paper = null;
	private PaperModel paperModel = null;
	private JVxSplitPane splitPane = null;
	private JScrollPane tableScrollContainer = null;
	private Ui ui = null;
	
	public PaperComponent(Ui ui, Paper paper) {
		super();
		
		this.ui = ui;
		this.paper = paper;
		
		paperModel = new PaperModel();
		paperModel.setPaper(paper);
		
		expressionsTable = new ColumnStretchingTable(1);
		expressionsTable.setFocusable(false);
		expressionsTable.setModel(paperModel);
		expressionsTable.setRowSorter(null);
		expressionsTable.setShowHorizontalLines(false);
		// Set this after the model, otherwise the model would override it.
		expressionsTable.setColumnModel(new PaperColumnModel());
		expressionsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		expressionsTable.getSelectionModel().addListSelectionListener(this::onExpressionsTableSelectionChanged);
		
		tableScrollContainer = new JScrollPane();
		tableScrollContainer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tableScrollContainer.setViewportView(expressionsTable);
		tableScrollContainer.getViewport().setBackground(expressionsTable.getBackground());
		
		messageLabel = new JLabel();
		messageLabel.setHorizontalAlignment(JLabel.RIGHT);
		messageLabel.setMinimumSize(new Dimension(0, 0));
		messageLabel.setText(" ");
		
		inputTextField = new JTextField();
		inputTextField.setHorizontalAlignment(JTextField.RIGHT);
		inputTextField.getDocument().addDocumentListener(new NotifyingDocumentListener(this::onInputTextFieldChanged));
		inputTextField.addKeyListener(new KeyPressedListener(KeyEvent.VK_DOWN, this::onInputTextFieldDownKey));
		inputTextField.addKeyListener(new KeyPressedListener(KeyEvent.VK_ESCAPE, this::onInputTextFieldEscapeKey));
		inputTextField.addKeyListener(new KeyPressedListener(KeyEvent.VK_ENTER, this::onInputTextFieldReturnKey));
		inputTextField.addKeyListener(new KeyPressedListener(KeyEvent.VK_UP, this::onInputTextFieldUpKey));
		
		BorderLayout inputPanelLayout = new BorderLayout();
		inputPanelLayout.setVgap(3);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(inputPanelLayout);
		inputPanel.add(messageLabel, BorderLayout.NORTH);
		inputPanel.add(inputTextField, BorderLayout.SOUTH);
		
		BorderLayout mainPanelLayout = new BorderLayout();
		mainPanelLayout.setVgap(3);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(mainPanelLayout);
		mainPanel.add(tableScrollContainer, BorderLayout.CENTER);
		mainPanel.add(inputPanel, BorderLayout.SOUTH);
		
		notesTextArea = new JTextArea();
		notesTextArea.setBorder(new JTextField().getBorder());
		notesTextArea.setLineWrap(true);
		notesTextArea.setWrapStyleWord(true);
		notesTextArea.getDocument().addDocumentListener(new NotifyingDocumentListener(this::onNotesTextAreaChanged));
		
		notesScrollContainer = new JScrollPane();
		notesScrollContainer.setViewportView(notesTextArea);
		
		splitPane = new JVxSplitPane(JVxSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerAlignment(JVxSplitPane.DIVIDER_BOTTOM_RIGHT);
		splitPane.setDividerLocation(DEFAULT_DIVIDER_LOCATION);
		splitPane.add(mainPanel, 0);
		splitPane.add(notesScrollContainer, 1);
		
		originalDividerLocation = splitPane.getDividerLocation();
		originalDividerSize = splitPane.getDividerSize();
		
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
		
		refresh();
	}
	
	public Paper getPaper() {
		return paper;
	}
	
	public void refresh() {
		paperModel.refresh();
		expressionsTable.resizeColumns();
		tableScrollContainer.getVerticalScrollBar().setValue(tableScrollContainer.getVerticalScrollBar().getMaximum());
		
		notesTextArea.setText(paper.getNotes());
		
		resetInput();
	}
	
	@Override
	public void repaint(long tm, int x, int y, int width, int height) {
		super.repaint(tm, x, y, width, height);
		
		// Setting it in this function is the best workaround I found which
		// still worked. Everything else would simply not yield the desired
		// effect of having a 3/1 partition.
		if (firstRepaint) {
			if (notesScrollContainer.isVisible()) {
				SwingUtilities.invokeLater(() -> {
					splitPane.setDividerLocation(DEFAULT_DIVIDER_LOCATION);
				});
			}
			firstRepaint = false;
		}
	}
	
	@Override
	public void requestFocus() {
		inputTextField.requestFocus();
	}
	
	public void setNotesVisible(boolean notesVisible) {
		notesScrollContainer.setVisible(notesVisible);
		
		if (notesVisible) {
			if (originalDividerLocation >= 0) {
				splitPane.setDividerSize(originalDividerSize);
				splitPane.setDividerLocation(originalDividerLocation);
			} else {
				splitPane.setDividerLocation(DEFAULT_DIVIDER_LOCATION);
			}
		} else {
			originalDividerLocation = splitPane.getDividerLocation();
			originalDividerSize = splitPane.getDividerSize();
			splitPane.setDividerSize(0);
			splitPane.setDividerLocation(1.0d);
		}
	}
	
	protected void resetInput() {
		inputTextField.setText("");
		setMessage(null);
	}
	
	protected void setMessage(String message) {
		if (message == null) {
			messageLabel.setText(" ");
			messageLabel.setToolTipText("");
		} else {
			messageLabel.setText(message + " ");
			messageLabel.setToolTipText(message);
		}
	}
	
	private void onExpressionsTableSelectionChanged(ListSelectionEvent event) {
		if (expressionsTable.getSelectedRow() >= 0) {
			if (bufferedInput == null) {
				bufferedInput = inputTextField.getText();
			}
			
			inputTextField.setText(paper.getEvaluatedExpressions().get(expressionsTable.getSelectedRow()).getExpression());
		} else {
			if (bufferedInput != null) {
				inputTextField.setText(bufferedInput);
				bufferedInput = null;
			}
		}
		inputTextField.requestFocus();
	}
	
	private void onInputTextFieldChanged() {
		setMessage(paper.previewResult(inputTextField.getText()));
	}
	
	private void onInputTextFieldDownKey() {
		if (expressionsTable.getSelectedRow() >= 0) {
			if (expressionsTable.getSelectedRow() < expressionsTable.getRowCount() - 1) {
				expressionsTable.getSelectionModel().setSelectionInterval(
						expressionsTable.getSelectedRow() + 1,
						expressionsTable.getSelectedRow() + 1);
			} else if (expressionsTable.getSelectedRow() == expressionsTable.getRowCount() - 1) {
				expressionsTable.getSelectionModel().clearSelection();
			}
		}
	}
	
	private void onInputTextFieldEscapeKey() {
		if (expressionsTable.getSelectedRow() >= 0) {
			expressionsTable.getSelectionModel().clearSelection();
		} else {
			resetInput();
		}
	}
	
	private void onInputTextFieldReturnKey() {
		if (inputTextField.getText().length() > 0) {
			try {
				ui.process(inputTextField.getText());
				resetInput();
			} catch (CommandExecutionException | InvalidExpressionException e) {
				if (e.getMessage() != null) {
					setMessage(e.getMessage());
				} else {
					setMessage("No details available: " + e.getClass().getSimpleName());
				}
			}
		} else {
			resetInput();
		}
	}
	
	private void onInputTextFieldUpKey() {
		if (expressionsTable.getSelectedRow() >= 0) {
			expressionsTable.getSelectionModel().setSelectionInterval(
					expressionsTable.getSelectedRow() - 1,
					expressionsTable.getSelectedRow() - 1);
		} else if (expressionsTable.getRowCount() > 0) {
			expressionsTable.getSelectionModel().setSelectionInterval(
					expressionsTable.getRowCount() - 1,
					expressionsTable.getRowCount() - 1);
		}
	}
	
	private void onNotesTextAreaChanged() {
		paper.setNotes(notesTextArea.getText());
	}
}
