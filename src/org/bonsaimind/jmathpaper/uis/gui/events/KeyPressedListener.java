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

package org.bonsaimind.jmathpaper.uis.gui.events;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyPressedListener implements KeyListener {
	protected Runnable action = null;
	protected int key = -1;
	
	public KeyPressedListener(int key, Runnable action) {
		super();
		
		this.key = key;
		this.action = action;
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == key) {
			action.run();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent event) {
		// Nothing to do.
	}
	
	@Override
	public void keyTyped(KeyEvent event) {
		// Nothing to do.
	}
}
