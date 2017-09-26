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

import org.bonsaimind.jmathpaper.core.ui.AbstractPapersUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Swt extends AbstractPapersUi {
	protected MainComposite mainComposite = null;
	
	protected Shell shell = null;
	
	public Swt() {
		super();
	}
	
	@Override
	public void quit() {
		shell.dispose();
	}
	
	@Override
	protected void internalStart() throws Exception {
		Display display = new Display();
		
		GridLayout mainLayout = new GridLayout(1, true);
		
		shell = new Shell();
		shell.setLayout(mainLayout);
		shell.setSize(720, 480);
		shell.setText("jMathPaper");
		
		mainComposite = new MainComposite(shell, SWT.NONE);
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mainComposite.setVisible(true);
		
		shell.open();
		
		mainComposite.init(arguments);
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
