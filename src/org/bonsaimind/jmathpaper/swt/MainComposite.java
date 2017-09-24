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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bonsaimind.jmathpaper.Arguments;
import org.bonsaimind.jmathpaper.core.Paper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class MainComposite extends Composite {
	private MenuItem clearMenuItem = null;
	private MenuItem closeAllMenuItem = null;
	private MenuItem closeMenuItem = null;
	private CTabFolder cTabFolder = null;
	private MenuItem nextPaperMenuItem = null;
	private MenuItem openMenuItem = null;
	private int paperCounter = 0;
	private MenuItem previousPaperMenuItem = null;
	private MenuItem saveAsMenuItem = null;
	private MenuItem saveMenuItem = null;
	
	public MainComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Menu menu = new Menu(getShell(), SWT.BAR);
		getShell().setMenuBar(menu);
		
		MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		fileMenuItem.setText("&File");
		
		Menu fileMenu = new Menu(getShell(), SWT.DROP_DOWN);
		fileMenuItem.setMenu(fileMenu);
		
		MenuItem newMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		newMenuItem.setAccelerator(SWT.CTRL | 'N');
		newMenuItem.setText("&New paper\tCtrl+N");
		newMenuItem.setToolTipText("Adds a new paper.");
		newMenuItem.addListener(SWT.Selection, this::onNewPushed);
		
		openMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		openMenuItem.setAccelerator(SWT.CTRL | 'O');
		openMenuItem.setText("&Open\tCtrl+O");
		openMenuItem.setToolTipText("Opens a previously saved paper.");
		openMenuItem.addListener(SWT.Selection, this::onOpenPushed);
		
		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		closeMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		closeMenuItem.setAccelerator(SWT.CTRL | 'W');
		closeMenuItem.setText("&Close\tCtrl+W");
		closeMenuItem.setToolTipText("Closes the current paper.");
		closeMenuItem.addListener(SWT.Selection, this::onClosePushed);
		
		closeAllMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		closeAllMenuItem.setAccelerator(SWT.CTRL | SWT.SHIFT | 'W');
		closeAllMenuItem.setText("C&lose all\tShift+Ctrl+W");
		closeAllMenuItem.setToolTipText("Closes all papers.");
		closeAllMenuItem.addListener(SWT.Selection, this::onCloseAllPushed);
		
		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		saveMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		saveMenuItem.setAccelerator(SWT.CTRL | 'S');
		saveMenuItem.setText("&Save\tCtrl+S");
		saveMenuItem.setToolTipText("Saves the current paper.");
		saveMenuItem.addListener(SWT.Selection, this::onSavePushed);
		
		saveAsMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		saveAsMenuItem.setText("S&ave as");
		saveAsMenuItem.setToolTipText("Saves the current paper under a new name.");
		saveAsMenuItem.addListener(SWT.Selection, this::onSaveAsPushed);
		
		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		saveAsMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		saveAsMenuItem.setText("Clea&r");
		saveAsMenuItem.setToolTipText("Clears the current paper.");
		saveAsMenuItem.addListener(SWT.Selection, this::onClearPushed);
		
		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		MenuItem quitMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		quitMenuItem.setAccelerator(SWT.CTRL | 'Q');
		quitMenuItem.setText("&Quit\tCtrl+Q");
		quitMenuItem.setToolTipText("Quit jMathPaper.");
		quitMenuItem.addListener(SWT.Selection, this::onQuitPushed);
		
		MenuItem viewMenuItem = new MenuItem(menu, SWT.CASCADE);
		viewMenuItem.setText("&View");
		
		Menu viewMenu = new Menu(getShell(), SWT.DROP_DOWN);
		viewMenuItem.setMenu(viewMenu);
		
		nextPaperMenuItem = new MenuItem(viewMenu, SWT.PUSH);
		nextPaperMenuItem.setAccelerator(SWT.CTRL | SWT.TAB);
		nextPaperMenuItem.setText("N&ext paper\tCtrl+Tab");
		nextPaperMenuItem.setToolTipText("Navigates to the next paper.");
		nextPaperMenuItem.addListener(SWT.Selection, this::onNextPaperPushed);
		
		previousPaperMenuItem = new MenuItem(viewMenu, SWT.PUSH);
		previousPaperMenuItem.setAccelerator(SWT.CTRL | SWT.SHIFT | SWT.TAB);
		previousPaperMenuItem.setText("P&revious paper\tShift+Ctrl+Tab");
		previousPaperMenuItem.setToolTipText("Navigates to the previous paper.");
		previousPaperMenuItem.addListener(SWT.Selection, this::onPreviousPaperPushed);
		
		new MenuItem(viewMenu, SWT.SEPARATOR);
		
		MenuItem notesMenuItem = new MenuItem(viewMenu, SWT.CHECK);
		notesMenuItem.setAccelerator(SWT.F4);
		notesMenuItem.setSelection(true);
		notesMenuItem.setText("&Notes\tF4");
		notesMenuItem.setToolTipText("Toggles the visibility of the notes area.");
		notesMenuItem.addListener(SWT.Selection, this::onShowHideNotesSelected);
		
		cTabFolder = new CTabFolder(this, SWT.BORDER);
		cTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		getDisplay().addFilter(SWT.Traverse, this::onTraverse);
	}
	
	public void init(Arguments arguments) {
		if (arguments.hasFiles()) {
			for (Path file : arguments.getFiles()) {
				open(file);
			}
		}
		
		if (arguments.getContext() != null) {
			open(arguments.getContext());
		}
		
		if (cTabFolder.getItemCount() == 0) {
			onNewPushed(null);
		}
		
		if (cTabFolder.getSelection() != null) {
			cTabFolder.getSelection().getControl().setFocus();
		}
		
		if (arguments.getExpression() != null) {
			PaperComponent paperComponent = (PaperComponent)cTabFolder.getSelection().getControl();
			paperComponent.evaluate(arguments.getExpression());
		}
	}
	
	private void onClearPushed(Event event) {
		if (cTabFolder.getSelection() != null) {
			CTabItem cTabItem = cTabFolder.getSelection();
			PaperComponent paperComponent = (PaperComponent)cTabItem.getControl();
			
			paperComponent.clear();
		}
	}
	
	private void onCloseAllPushed(Event event) {
		while (cTabFolder.getItemCount() > 0) {
			cTabFolder.getItem(0).dispose();
		}
		
		onCTabFolderChanged(null);
	}
	
	private void onClosePushed(Event event) {
		if (cTabFolder.getSelection() != null) {
			cTabFolder.getSelection().dispose();
			
			onCTabFolderChanged(null);
		}
	}
	
	private void onCTabFolderChanged(Event event) {
		if (closeMenuItem.isDisposed()) {
			return;
		}
		
		boolean hasItems = cTabFolder.getItemCount() > 0;
		
		closeMenuItem.setEnabled(hasItems);
		closeAllMenuItem.setEnabled(hasItems);
		
		saveMenuItem.setEnabled(hasItems);
		saveAsMenuItem.setEnabled(hasItems);
		
		boolean hasManyItems = cTabFolder.getItemCount() > 1;
		
		nextPaperMenuItem.setEnabled(hasManyItems);
		previousPaperMenuItem.setEnabled(hasManyItems);
	}
	
	private void onNewPushed(Event event) {
		paperCounter = paperCounter + 1;
		
		CTabItem cTabItem = new CTabItem(cTabFolder, SWT.CLOSE);
		cTabItem.setText("Paper #" + Integer.toString(paperCounter));
		cTabItem.addListener(SWT.Dispose, this::onCTabFolderChanged);
		
		PaperComponent paperComponent = new PaperComponent(cTabFolder, cTabItem, SWT.NONE);
		paperComponent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		cTabFolder.setSelection(cTabItem);
		
		onCTabFolderChanged(null);
	}
	
	private void onNextPaperPushed(Event event) {
		if (cTabFolder.getItemCount() > 1
				&& cTabFolder.getSelectionIndex() < cTabFolder.getItemCount() - 1) {
			cTabFolder.setSelection(cTabFolder.getSelectionIndex() + 1);
		}
	}
	
	private void onOpenPushed(Event event) {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
		fileDialog.setOverwrite(true);
		
		open(fileDialog.open());
	}
	
	private void onPreviousPaperPushed(Event event) {
		if (cTabFolder.getItemCount() > 1
				&& cTabFolder.getSelectionIndex() > 0) {
			cTabFolder.setSelection(cTabFolder.getSelectionIndex() - 1);
		}
	}
	
	private void onQuitPushed(Event event) {
		getShell().setVisible(false);
		getShell().dispose();
	}
	
	private void onSaveAsPushed(Event event) {
		if (cTabFolder.getSelection() != null) {
			CTabItem cTabItem = cTabFolder.getSelection();
			PaperComponent paperComponent = (PaperComponent)cTabItem.getControl();
			Paper paper = paperComponent.getPaper();
			
			FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
			
			if (paper.getFile() != null) {
				fileDialog.setFileName(paper.getFile().getFileName().toString());
			} else {
				fileDialog.setFileName("new-paper.jmathpaper");
			}
			
			fileDialog.setOverwrite(true);
			
			String filePath = fileDialog.open();
			
			if (filePath != null) {
				paper.setFile(Paths.get(filePath));
				onSavePushed(null);
			}
		}
	}
	
	private void onSavePushed(Event event) {
		if (cTabFolder.getSelection() != null) {
			CTabItem cTabItem = cTabFolder.getSelection();
			PaperComponent paperComponent = (PaperComponent)cTabItem.getControl();
			Paper paper = paperComponent.getPaper();
			
			if (paper.getFile() != null) {
				try {
					paperComponent.save(paper.getFile());
					cTabItem.setText(paper.getFile().getFileName().toString());
					cTabItem.setToolTipText(paper.getFile().toAbsolutePath().toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				onSaveAsPushed(null);
			}
		}
	}
	
	private void onShowHideNotesSelected(Event event) {
		for (CTabItem cTabItem : cTabFolder.getItems()) {
			PaperComponent paperComponent = (PaperComponent)cTabItem.getControl();
			paperComponent.setNotesVisible(((MenuItem)event.widget).getSelection());
		}
	}
	
	private void onTraverse(Event event) {
		if ((event.stateMask | SWT.CTRL) == SWT.CTRL
				&& event.keyCode == SWT.TAB) {
			onNextPaperPushed(null);
			event.doit = false;
		} else if ((event.stateMask | (SWT.CTRL | SWT.SHIFT)) == (SWT.CTRL | SWT.SHIFT)
				&& event.keyCode == SWT.TAB) {
			onPreviousPaperPushed(null);
			event.doit = false;
		}
	}
	
	private void open(Path file) {
		if (cTabFolder.getItemCount() == 1
				&& ((PaperComponent)cTabFolder.getSelection().getControl()).isEmpty()) {
			onClosePushed(null);
		}
		
		for (CTabItem cTabItem : cTabFolder.getItems()) {
			PaperComponent paperComponent = ((PaperComponent)cTabItem.getControl());
			Paper paper = paperComponent.getPaper();
			
			if (file.equals(paper.getFile())) {
				cTabFolder.setSelection(cTabItem);
				return;
			}
		}
		
		onNewPushed(null);
		
		CTabItem cTabItem = cTabFolder.getSelection();
		cTabItem.setText(file.getFileName().toString());
		cTabItem.setToolTipText(file.toAbsolutePath().toString());
		
		try {
			((PaperComponent)cTabItem.getControl()).load(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void open(String filePath) {
		if (filePath != null) {
			Path file = Paths.get(filePath);
			
			if (Files.isRegularFile(file)) {
				open(file);
			}
		}
	}
}
