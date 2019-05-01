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

package org.bonsaimind.jmathpaper.uis.swing;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

import org.bonsaimind.jmathpaper.Version;
import org.bonsaimind.jmathpaper.core.ui.AbstractPapersUi;
import org.bonsaimind.jmathpaper.core.ui.UiParameters;
import org.bonsaimind.jmathpaper.uis.swing.components.PaperComponent;
import org.bonsaimind.jmathpaper.uis.swing.events.ActionForwardingListener;
import org.bonsaimind.jmathpaper.uis.swing.events.UiQuittingWindowListener;

public class Swing extends AbstractPapersUi {
	protected JFileChooser fileChooser = null;
	protected JFrame frame = null;
	private JMenuItem clearPaperMenuItem;
	private JMenuItem closeAllPapersMenuItem;
	private JMenuItem closePaperMenuItem;
	private JMenuItem nextPaperMenuItem;
	private boolean notesVisible = true;
	private JMenuItem previousPaperMenuItem;
	private JMenuItem savePaperAsMenuItem;
	private JMenuItem savePaperMenuItem;
	private JLabel statusLabel = null;
	private JTabbedPane tabbedPane = null;
	
	public Swing() {
		super();
	}
	
	@Override
	public void init(UiParameters uiParameters) throws Exception {
		super.init(uiParameters);
		
		setupLookAndFeel();
		setupAlternateRowColor();
		
		fileChooser = new JFileChooser();
		
		// We need to catch the Ctrl+Tab/Ctrl+Shift+Tab keys directly in
		// the event loop. See method for further information.
		Toolkit.getDefaultToolkit().addAWTEventListener(this::onAwtEvent, AWTEvent.KEY_EVENT_MASK);
		
		JMenuItem newPaperMenuItem = new JMenuItem();
		newPaperMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		newPaperMenuItem.setMnemonic('N');
		newPaperMenuItem.setText("New paper");
		newPaperMenuItem.addActionListener(new ActionForwardingListener(this::new_));
		
		JMenuItem openPaperMenuItem = new JMenuItem();
		openPaperMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		openPaperMenuItem.setMnemonic('O');
		openPaperMenuItem.setText("Open");
		openPaperMenuItem.addActionListener(new ActionForwardingListener(this::onOpenMenuItemClicked));
		
		closePaperMenuItem = new JMenuItem();
		closePaperMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
		closePaperMenuItem.setMnemonic('C');
		closePaperMenuItem.setText("Close");
		closePaperMenuItem.addActionListener(new ActionForwardingListener(this::close));
		
		closeAllPapersMenuItem = new JMenuItem();
		closeAllPapersMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		closeAllPapersMenuItem.setMnemonic('l');
		closeAllPapersMenuItem.setText("Close all");
		closeAllPapersMenuItem.addActionListener(new ActionForwardingListener(this::closeAll));
		
		savePaperMenuItem = new JMenuItem();
		savePaperMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		savePaperMenuItem.setMnemonic('s');
		savePaperMenuItem.setText("Save");
		savePaperMenuItem.addActionListener(new ActionForwardingListener(this::onSaveMenuItemClicked));
		
		savePaperAsMenuItem = new JMenuItem();
		savePaperAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		savePaperAsMenuItem.setMnemonic('a');
		savePaperAsMenuItem.setText("Save as");
		savePaperAsMenuItem.addActionListener(new ActionForwardingListener(this::onSaveAsMenuItemClicked));
		
		clearPaperMenuItem = new JMenuItem();
		clearPaperMenuItem.setMnemonic('r');
		clearPaperMenuItem.setText("Clear");
		clearPaperMenuItem.addActionListener(new ActionForwardingListener(this::clear));
		
		JMenuItem quitMenuItem = new JMenuItem();
		quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
		quitMenuItem.setMnemonic('Q');
		quitMenuItem.setText("Quit");
		quitMenuItem.addActionListener(new ActionForwardingListener(this::quit));
		
		JMenu fileMenu = new JMenu();
		fileMenu.setMnemonic('F');
		fileMenu.setText("File");
		fileMenu.add(newPaperMenuItem);
		fileMenu.add(openPaperMenuItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(closePaperMenuItem);
		fileMenu.add(closeAllPapersMenuItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(savePaperMenuItem);
		fileMenu.add(savePaperAsMenuItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(clearPaperMenuItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(quitMenuItem);
		
		nextPaperMenuItem = new JMenuItem();
		nextPaperMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_MASK));
		nextPaperMenuItem.setMnemonic('e');
		nextPaperMenuItem.setText("Next paper");
		nextPaperMenuItem.addActionListener(new ActionForwardingListener(this::next));
		
		previousPaperMenuItem = new JMenuItem();
		previousPaperMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
		previousPaperMenuItem.setMnemonic('r');
		previousPaperMenuItem.setText("Previous paper");
		previousPaperMenuItem.addActionListener(new ActionForwardingListener(this::previous));
		
		JCheckBoxMenuItem notesMenuItem = new JCheckBoxMenuItem();
		notesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		notesMenuItem.setSelected(notesVisible);
		notesMenuItem.setText("Notes");
		notesMenuItem.addActionListener(this::onShowHideNotesClicked);
		
		JMenu viewMenu = new JMenu();
		viewMenu.setMnemonic('V');
		viewMenu.setText("View");
		viewMenu.add(nextPaperMenuItem);
		viewMenu.add(previousPaperMenuItem);
		viewMenu.add(new JSeparator());
		viewMenu.add(notesMenuItem);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(this::onSelectedTabChanged);
		
		statusLabel = new JLabel(" ");
		statusLabel.setHorizontalAlignment(JLabel.RIGHT);
		
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.setSize(720, 480);
		frame.setTitle("jMathPaper " + Version.CURRENT);
		frame.add(tabbedPane, BorderLayout.CENTER);
		frame.add(statusLabel, BorderLayout.SOUTH);
		frame.addWindowListener(new UiQuittingWindowListener(this));
	}
	
	@Override
	public void quit() {
		frame.setVisible(false);
		frame.dispose();
	}
	
	@Override
	public void run() throws Exception {
		frame.setVisible(true);
		
		// Make sure that the focus is correctly set when opening.
		if (tabbedPane.getSelectedComponent() != null) {
			SwingUtilities.invokeLater(tabbedPane.getSelectedComponent()::requestFocus);
		}
	}
	
	@Override
	public void save() throws IOException {
		checkCurrentPaper();
		
		if (paper.getFile() == null) {
			onSaveAsMenuItemClicked();
		} else {
			super.save();
		}
	}
	
	@Override
	protected void currentPaperHasBeenAdded() {
		PaperComponent paperComponent = new PaperComponent(this, paper);
		paperComponent.setNotesVisible(notesVisible);
		
		tabbedPane.addTab(
				getShortPaperTitle(paper),
				null,
				paperComponent,
				getLongPaperTitle(paper));
		
		tabbedPane.setSelectedComponent(paperComponent);
		
		SwingUtilities.invokeLater(tabbedPane.getSelectedComponent()::requestFocus);
	}
	
	@Override
	protected void currentPaperHasBeenModified() {
		statusLabel.setText(" ");
		
		((PaperComponent)tabbedPane.getSelectedComponent()).refresh();
		
		tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), getShortPaperTitle(paper));
		tabbedPane.setToolTipTextAt(tabbedPane.getSelectedIndex(), getShortPaperTitle(paper));
	}
	
	@Override
	protected void currentPaperHasBeenRemoved() {
		tabbedPane.remove(tabbedPane.getSelectedComponent());
	}
	
	@Override
	protected void currentPaperHasBeenReset() {
		statusLabel.setText(" ");
		
		((PaperComponent)tabbedPane.getSelectedComponent()).refresh();
	}
	
	@Override
	protected void currentSelectedPaperHasChanged() {
		savePaperAsMenuItem.setEnabled(paper != null);
		savePaperMenuItem.setEnabled(paper != null);
		closePaperMenuItem.setEnabled(paper != null);
		closeAllPapersMenuItem.setEnabled(paper != null);
		clearPaperMenuItem.setEnabled(paper != null);
		
		nextPaperMenuItem.setEnabled(paper != null && papers.indexOf(paper) < papers.size() - 1);
		previousPaperMenuItem.setEnabled(paper != null && papers.indexOf(paper) > 0);
		
		for (Component component : tabbedPane.getComponents()) {
			if (((PaperComponent)component).getPaper() == paper) {
				tabbedPane.setSelectedComponent(component);
				component.requestFocus();
				break;
			}
		}
	}
	
	protected void setupAlternateRowColor() {
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		
		try {
			if (uiParameters.has("alternate-row-color")) {
				defaults.put("Table.alternateRowColor", Color.decode(uiParameters.getString("alternate-row-color")));
			} else if (defaults.get("Table.alternateRowColor") == null) {
				defaults.put("Table.alternateRowColor", new Color(240, 240, 240));
			}
		} catch (Exception e) {
			// Nothing we can do, as this called before anything is being
			// initialized or created.
			e.printStackTrace();
		}
	}
	
	protected void setupLookAndFeel() {
		try {
			if (uiParameters.has("laf")) {
				UIManager.setLookAndFeel(uiParameters.getString("laf"));
			} else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (Exception e) {
			// Nothing we can do, as this called before anything is being
			// initialized or created.
			e.printStackTrace();
		}
	}
	
	private void onAwtEvent(AWTEvent event) {
		// The Ctrl+Tab-Ctrlhiftab keys are being used by
		// the KeyboardFocusManager of Swing to give focus (or take it away)
		// from the JTabbedPane. But we want to use these keys to switch
		// between tabs, so we need to catch and process them directly in
		// the event loop of Swing.
		
		if (event.getID() == KeyEvent.KEY_PRESSED) {
			KeyEvent keyEvent = (KeyEvent)event;
			
			if (keyEvent.getKeyCode() == KeyEvent.VK_TAB) {
				if ((keyEvent.getModifiers() & KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK) {
					if ((keyEvent.getModifiers() & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK) {
						previous();
					} else {
						next();
					}
					
					keyEvent.consume();
				}
			}
		}
	}
	
	private void onOpenMenuItemClicked() {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			try {
				open(fileChooser.getSelectedFile().toPath());
			} catch (Exception e) {
				statusLabel.setText(e.getMessage() + " ");
			}
		}
	}
	
	private void onSaveAsMenuItemClicked() {
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			try {
				paper.setFile(fileChooser.getSelectedFile().toPath());
				save();
			} catch (Exception e) {
				statusLabel.setText(e.getMessage() + " ");
			}
		}
	}
	
	private void onSaveMenuItemClicked() {
		try {
			save();
		} catch (Exception e) {
			statusLabel.setText(e.getMessage() + " ");
		}
	}
	
	private void onSelectedTabChanged(ChangeEvent event) {
		if (tabbedPane.getSelectedComponent() != null) {
			setPaper(((PaperComponent)tabbedPane.getSelectedComponent()).getPaper());
			
			SwingUtilities.invokeLater(tabbedPane.getSelectedComponent()::requestFocus);
		} else {
			setPaper(null);
		}
	}
	
	private void onShowHideNotesClicked(ActionEvent event) {
		notesVisible = !notesVisible;
		
		((JCheckBoxMenuItem)event.getSource()).setSelected(notesVisible);
		
		for (Component component : tabbedPane.getComponents()) {
			((PaperComponent)component).setNotesVisible(notesVisible);
		}
	}
}
