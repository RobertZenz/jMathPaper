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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bonsaimind.jmathpaper.Arguments;
import org.bonsaimind.jmathpaper.Configuration;
import org.bonsaimind.jmathpaper.Version;
import org.bonsaimind.jmathpaper.core.InvalidExpressionException;
import org.bonsaimind.jmathpaper.core.Paper;
import org.bonsaimind.jmathpaper.core.ui.AbstractPapersUi;
import org.bonsaimind.jmathpaper.uis.swt.events.EventForwarder;
import org.bonsaimind.jmathpaper.uis.swt.events.ForwardingSelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class Swt extends AbstractPapersUi {
	protected Display display = null;
	protected Shell shell = null;
	private MenuItem clearMenuItem = null;
	private MenuItem closeAllMenuItem = null;
	private MenuItem closeMenuItem = null;
	private CTabFolder cTabFolder = null;
	private Label errorLabel = null;
	private FileDialog fileOpenDialog = null;
	private FileDialog fileSaveDialog = null;
	private MenuItem nextPaperMenuItem = null;
	private MenuItem openMenuItem = null;
	private int paperCounter = 0;
	private MenuItem previousPaperMenuItem = null;
	private MenuItem saveAsMenuItem = null;
	private MenuItem saveMenuItem = null;
	
	public Swt() {
		super();
	}
	
	@Override
	public void clear() {
		super.clear();
		
		PaperComponent paperComponent = getCurrentPaperComponent();
		
		if (paperComponent != null) {
			paperComponent.clearExpressions();
			paperComponent.updateExpressions();
		}
	}
	
	@Override
	public void close() {
		CTabItem currentTabItem = cTabFolder.getSelection();
		
		super.close();
		
		if (currentTabItem != null) {
			currentTabItem.dispose();
		}
	}
	
	@Override
	public void closeAll() {
		super.closeAll();
		
		while (cTabFolder.getItemCount() > 0) {
			cTabFolder.getItem(0).dispose();
		}
	}
	
	@Override
	public void evaluate(String expression) throws InvalidExpressionException {
		super.evaluate(expression);
		
		updateCurrentTabItem();
	}
	
	@Override
	public void init() throws Exception {
		display = new Display();
		
		GridLayout mainLayout = new GridLayout(1, true);
		
		shell = new Shell();
		shell.setLayout(mainLayout);
		shell.setSize(720, 480);
		shell.setText("jMathPaper " + Version.CURRENT);
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(1, false));
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		fileMenuItem.setText("&File");
		
		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileMenuItem.setMenu(fileMenu);
		
		MenuItem newMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		newMenuItem.setAccelerator(SWT.CTRL | 'N');
		newMenuItem.setText("&New paper\tCtrl+N");
		newMenuItem.setToolTipText("Adds a new paper.");
		newMenuItem.addListener(SWT.Selection, new EventForwarder(this::new_));
		
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
		closeMenuItem.addListener(SWT.Selection, new EventForwarder(this::close));
		
		closeAllMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		closeAllMenuItem.setAccelerator(SWT.CTRL | SWT.SHIFT | 'W');
		closeAllMenuItem.setText("C&lose all\tShift+Ctrl+W");
		closeAllMenuItem.setToolTipText("Closes all papers.");
		closeAllMenuItem.addListener(SWT.Selection, new EventForwarder(this::closeAll));
		
		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		saveMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		saveMenuItem.setAccelerator(SWT.CTRL | 'S');
		saveMenuItem.setText("&Save\tCtrl+S");
		saveMenuItem.setToolTipText("Saves the current paper.");
		saveMenuItem.addListener(SWT.Selection, new EventForwarder(this::save));
		
		saveAsMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		saveAsMenuItem.setAccelerator(SWT.CTRL | SWT.SHIFT | 'S');
		saveAsMenuItem.setText("S&ave as\tShift+Ctrl+S");
		saveAsMenuItem.setToolTipText("Saves the current paper under a new name.");
		saveAsMenuItem.addListener(SWT.Selection, this::onSaveAsPushed);
		
		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		clearMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		clearMenuItem.setText("Clea&r");
		clearMenuItem.setToolTipText("Clears the current paper.");
		clearMenuItem.addListener(SWT.Selection, new EventForwarder(this::clear));
		
		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		MenuItem quitMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		quitMenuItem.setAccelerator(SWT.CTRL | 'Q');
		quitMenuItem.setText("&Quit\tCtrl+Q");
		quitMenuItem.setToolTipText("Quit jMathPaper.");
		quitMenuItem.addListener(SWT.Selection, new EventForwarder(this::quit));
		
		MenuItem viewMenuItem = new MenuItem(menu, SWT.CASCADE);
		viewMenuItem.setText("&View");
		
		Menu viewMenu = new Menu(shell, SWT.DROP_DOWN);
		viewMenuItem.setMenu(viewMenu);
		
		nextPaperMenuItem = new MenuItem(viewMenu, SWT.PUSH);
		nextPaperMenuItem.setAccelerator(SWT.CTRL | SWT.TAB);
		nextPaperMenuItem.setText("N&ext paper\tCtrl+Tab");
		nextPaperMenuItem.setToolTipText("Navigates to the next paper.");
		nextPaperMenuItem.addListener(SWT.Selection, new EventForwarder(this::next));
		
		previousPaperMenuItem = new MenuItem(viewMenu, SWT.PUSH);
		previousPaperMenuItem.setAccelerator(SWT.CTRL | SWT.SHIFT | SWT.TAB);
		previousPaperMenuItem.setText("P&revious paper\tShift+Ctrl+Tab");
		previousPaperMenuItem.setToolTipText("Navigates to the previous paper.");
		previousPaperMenuItem.addListener(SWT.Selection, new EventForwarder(this::previous));
		
		new MenuItem(viewMenu, SWT.SEPARATOR);
		
		MenuItem notesMenuItem = new MenuItem(viewMenu, SWT.CHECK);
		notesMenuItem.setAccelerator(SWT.F4);
		notesMenuItem.setSelection(true);
		notesMenuItem.setText("&Notes\tF4");
		notesMenuItem.setToolTipText("Toggles the visibility of the notes area.");
		notesMenuItem.addListener(SWT.Selection, this::onShowHideNotesSelected);
		
		cTabFolder = new CTabFolder(composite, SWT.BORDER);
		cTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		cTabFolder.addSelectionListener(new ForwardingSelectionListener(this::updateCurrentPaper));
		cTabFolder.addSelectionListener(new ForwardingSelectionListener(this::updateCurrentTabItem));
		cTabFolder.addSelectionListener(new ForwardingSelectionListener(this::updateMenuItems));
		
		errorLabel = new Label(composite, SWT.RIGHT);
		errorLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
		
		composite.getDisplay().addFilter(SWT.Traverse, this::onTraverse);
		
		composite.setVisible(true);
		
		shell.open();
	}
	
	@Override
	public void new_() {
		super.new_();
		
		cTabFolder.setSelection(attachNewTabItem(paper));
		updateMenuItems();
	}
	
	@Override
	public void open(Path file) throws InvalidExpressionException, IOException {
		Paper paperToBeClosed = null;
		
		if (papers.size() == 1
				&& paper != null
				&& paper.getEvaluatedExpressions().isEmpty()
				&& paper.getFile() == null) {
			// Seems like a new and empty paper, let's close it.
			paperToBeClosed = paper;
		}
		
		super.open(file);
		
		// If there is already a tab with the current paper, we can exit.
		for (CTabItem cTabItem : cTabFolder.getItems()) {
			if (((PaperComponent)cTabItem.getControl()).getPaper() == paper) {
				return;
			}
		}
		
		setPaper(paperToBeClosed);
		close();
		
		// Otherwise we will create a new one.
		cTabFolder.setSelection(attachNewTabItem(paper));
		updateCurrentTabItem();
	}
	
	@Override
	public void quit() {
		shell.dispose();
	}
	
	@Override
	public void reload() throws InvalidExpressionException, IOException {
		if (paper != null) {
			super.reload();
			
			PaperComponent paperComponent = getCurrentPaperComponent();
			
			if (paperComponent != null) {
				paperComponent.clearExpressions();
				paperComponent.updateExpressions();
			}
		}
	}
	
	@Override
	public void run(Arguments arguments) throws Exception {
		super.run(arguments);
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	@Override
	public void save() {
		if (paper != null) {
			if (paper.getFile() == null) {
				onSaveAsPushed(null);
				return;
			}
			
			paper.setNotes(getCurrentPaperComponent().getNotes());
			
			try {
				super.save();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void save(Path file) throws IOException {
		if (paper != null) {
			paper.setNotes(getCurrentPaperComponent().getNotes());
			
			super.save(file);
		}
	}
	
	protected CTabItem attachNewTabItem(Paper paper) {
		CTabItem cTabItem = new CTabItem(cTabFolder, SWT.CLOSE);
		
		if (paper.getFile() != null) {
			cTabItem.setText(paper.getFile().getFileName().toString());
			cTabItem.setToolTipText(paper.getFile().toAbsolutePath().toString());
		} else {
			paperCounter = paperCounter + 1;
			cTabItem.setText("*Paper #" + Integer.toString(paperCounter));
		}
		cTabItem.addListener(SWT.Dispose, new EventForwarder(this::updateMenuItems));
		
		PaperComponent paperComponent = new PaperComponent(cTabFolder, this, paper, SWT.NONE);
		paperComponent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		cTabItem.setControl(paperComponent);
		
		return cTabItem;
	}
	
	@Override
	protected void currentPaperHasBeenModified() {
		super.currentPaperHasBeenModified();
		
		if (cTabFolder.getSelection() != null) {
			CTabItem cTabItem = cTabFolder.getSelection();
			
			if (!cTabItem.getText().startsWith("*")) {
				cTabItem.setText("*" + cTabItem.getText());
			}
		}
	}
	
	@Override
	protected void currentPaperHasBeenReset() {
		CTabItem cTabItem = cTabFolder.getSelection();
		
		if (cTabItem != null && paper != null && paper.getFile() != null) {
			cTabItem.setText(paper.getFile().getFileName().toString());
			cTabItem.setToolTipText(paper.getFile().toAbsolutePath().toString());
		}
	}
	
	protected PaperComponent getCurrentPaperComponent() {
		if (cTabFolder.getSelection() != null) {
			return (PaperComponent)cTabFolder.getSelection().getControl();
		}
		
		return null;
	}
	
	@Override
	protected void openDefaultPaper() throws InvalidExpressionException, IOException {
		if (arguments.getExpression() != null) {
			open(Configuration.getGlobalPaperFile());
		} else {
			new_();
		}
	}
	
	@Override
	protected void reevaluate() throws InvalidExpressionException {
		super.reevaluate();
		
		PaperComponent paperComponent = getCurrentPaperComponent();
		
		if (paperComponent != null) {
			paperComponent.clearExpressions();
			paperComponent.updateExpressions();
		}
	}
	
	@Override
	protected void setPaper(Paper paper) {
		if (this.paper != paper) {
			super.setPaper(paper);
			
			for (CTabItem cTabItem : cTabFolder.getItems()) {
				Paper tabItemPaper = ((PaperComponent)cTabItem.getControl()).getPaper();
				
				if (tabItemPaper == paper) {
					cTabFolder.setSelection(cTabItem);
					break;
				}
			}
			
			errorLabel.setText("");
		}
		
		updateMenuItems();
	}
	
	protected void updateCurrentPaper() {
		if (cTabFolder.getSelection() != null) {
			setPaper(getCurrentPaperComponent().getPaper());
		} else {
			setPaper(null);
		}
	}
	
	protected void updateCurrentTabItem() {
		if (cTabFolder.getSelection() != null) {
			getCurrentPaperComponent().updateExpressions();
			getCurrentPaperComponent().updateNotes();
		}
	}
	
	protected void updateMenuItems() {
		if (closeMenuItem.isDisposed()) {
			return;
		}
		
		boolean hasItems = !papers.isEmpty();
		
		closeMenuItem.setEnabled(hasItems);
		closeAllMenuItem.setEnabled(hasItems);
		
		saveMenuItem.setEnabled(hasItems);
		saveAsMenuItem.setEnabled(hasItems);
		clearMenuItem.setEnabled(hasItems);
		
		boolean hasManyItems = papers.size() > 1;
		
		nextPaperMenuItem.setEnabled(hasManyItems);
		previousPaperMenuItem.setEnabled(hasManyItems);
	}
	
	private void onOpenPushed(Event event) {
		if (fileOpenDialog == null) {
			fileOpenDialog = new FileDialog(shell, SWT.OPEN);
		}
		
		if (getPaper() != null && getPaper().getFile() != null) {
			fileOpenDialog.setFilterPath(getPaper().getFile().getParent().toString());
			fileOpenDialog.setFileName(getPaper().getFile().getFileName().toString());
		}
		
		open(fileOpenDialog.open());
	}
	
	private void onSaveAsPushed(Event event) {
		if (cTabFolder.getSelection() != null) {
			CTabItem cTabItem = cTabFolder.getSelection();
			PaperComponent paperComponent = (PaperComponent)cTabItem.getControl();
			Paper paper = paperComponent.getPaper();
			
			if (fileSaveDialog == null) {
				fileSaveDialog = new FileDialog(shell, SWT.SAVE);
				fileSaveDialog.setOverwrite(true);
			}
			
			if (paper.getFile() != null) {
				fileSaveDialog.setFilterPath(paper.getFile().getParent().toString());
				fileSaveDialog.setFileName(paper.getFile().getFileName().toString());
			} else {
				fileSaveDialog.setFileName("new-paper.jmathpaper");
			}
			
			String filePath = fileSaveDialog.open();
			
			if (filePath != null) {
				paper.setFile(Paths.get(filePath));
				save();
			}
		}
	}
	
	private void onShowHideNotesSelected(Event event) {
		boolean notesVisible = ((MenuItem)event.widget).getSelection();
		
		for (CTabItem cTabItem : cTabFolder.getItems()) {
			PaperComponent paperComponent = (PaperComponent)cTabItem.getControl();
			paperComponent.setNotesVisible(notesVisible);
		}
	}
	
	private void onTraverse(Event event) {
		if ((event.stateMask | SWT.CTRL) == SWT.CTRL
				&& event.keyCode == SWT.TAB) {
			next();
			event.doit = false;
		} else if ((event.stateMask | (SWT.CTRL | SWT.SHIFT)) == (SWT.CTRL | SWT.SHIFT)
				&& event.keyCode == SWT.TAB) {
			previous();
			event.doit = false;
		}
	}
	
	private void open(String filePath) {
		if (filePath != null) {
			Path file = Paths.get(filePath);
			
			if (Files.isRegularFile(file)) {
				try {
					open(file);
				} catch (InvalidExpressionException | IOException e) {
					errorLabel.setText(e.toString());
				}
			}
		}
	}
}
