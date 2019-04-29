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

package org.bonsaimind.jmathpaper.uis.swing.events;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.bonsaimind.jmathpaper.core.ui.Ui;

public class UiQuittingWindowListener implements WindowListener {
	private Ui ui = null;
	
	public UiQuittingWindowListener(Ui ui) {
		super();
		
		this.ui = ui;
	}
	
	@Override
	public void windowActivated(WindowEvent event) {
		// Nothing to do.
	}
	
	@Override
	public void windowClosed(WindowEvent event) {
		// Nothing to do.
	}
	
	@Override
	public void windowClosing(WindowEvent event) {
		ui.quit();
	}
	
	@Override
	public void windowDeactivated(WindowEvent event) {
		// Nothing to do.
	}
	
	@Override
	public void windowDeiconified(WindowEvent event) {
		// Nothing to do.
	}
	
	@Override
	public void windowIconified(WindowEvent event) {
		// Nothing to do.
	}
	
	@Override
	public void windowOpened(WindowEvent event) {
		// Nothing to do.
	}
}
