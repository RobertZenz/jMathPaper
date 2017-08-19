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

import org.bonsaimind.jmathpaper.components.MainComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class Main {
	private Main() {
		// No instancing required.
	}
	
	public static final void main(String[] arguments) {
		Display display = new Display();
		
		GridLayout mainLayout = new GridLayout(1, true);
		
		Shell mainWindow = new Shell();
		mainWindow.setLayout(mainLayout);
		mainWindow.setSize(720, 480);
		mainWindow.setText("jMathPaper");
		
		MainComposite mainContent = new MainComposite(mainWindow, SWT.NONE);
		mainContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mainContent.setVisible(true);
		
		mainWindow.open();
		
		mainContent.init();
		
		while (!mainWindow.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
