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

package org.bonsaimind.jmathpaper.uis.swt.events;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class EventForwarder implements Listener {
	private Runnable target = null;
	
	public EventForwarder(Runnable target) {
		super();
		
		this.target = target;
	}
	
	@Override
	public void handleEvent(Event event) {
		target.run();
	}
}