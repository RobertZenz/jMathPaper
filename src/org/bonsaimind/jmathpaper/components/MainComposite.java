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

package org.bonsaimind.jmathpaper.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class MainComposite extends Composite {
	private PaperComponent paperComponent = null;
	
	public MainComposite(Composite parent, int style) {
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
		
		paperComponent = new PaperComponent(this, SWT.NONE);
		paperComponent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (visible) {
			paperComponent.setFocus();
		}
	}
	
	private void onQuitPushed(Event event) {
		getShell().setVisible(false);
		getShell().dispose();
	}
	
	private void onShowHideNotesSelected(Event event) {
		paperComponent.setNotesVisible(((MenuItem)event.widget).getSelection());
	}
}
