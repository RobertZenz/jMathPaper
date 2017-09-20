
package org.bonsaimind.jmathpaper.swt;

import org.bonsaimind.jmathpaper.Arguments;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class Swt {
	
	private Swt() {
		super();
	}
	
	public static final void run(Arguments arguments) {
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
		
		mainContent.init(arguments.getUnnamedParameters());
		
		while (!mainWindow.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
