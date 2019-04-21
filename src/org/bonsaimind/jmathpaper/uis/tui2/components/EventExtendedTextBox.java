/*
 * Copyright 2018, Robert 'Bobby' Zenz
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

package org.bonsaimind.jmathpaper.uis.tui2.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.input.KeyStroke;

public class EventExtendedTextBox extends TextBox {
	private Map<KeyStroke, List<KeyStrokeHandler>> keyStrokeHandlers = new HashMap<>();
	private List<TextChangedListener> textChangedListeners = new ArrayList<>();
	
	public EventExtendedTextBox(Style style) {
		super("", style);
	}
	
	public EventExtendedTextBox addHandler(KeyStroke keyStroke, KeyStrokeHandler handler) {
		List<KeyStrokeHandler> handlers = keyStrokeHandlers.get(keyStroke);
		
		if (handlers == null) {
			handlers = new ArrayList<>();
			keyStrokeHandlers.put(keyStroke, handlers);
		}
		
		handlers.add(handler);
		
		return this;
	}
	
	public EventExtendedTextBox addTextChangedHandler(TextChangedListener handler) {
		textChangedListeners.add(handler);
		
		return this;
	}
	
	@Override
	public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
		List<KeyStrokeHandler> handlers = keyStrokeHandlers.get(keyStroke);
		
		if (handlers != null && !handlers.isEmpty()) {
			for (KeyStrokeHandler handler : handlers) {
				handler.handleKeyStroke(this);
			}
			
			return Result.HANDLED;
		} else {
			String textBefore = getText();
			
			Result result = super.handleKeyStroke(keyStroke);
			
			if (!Objects.equals(textBefore, getText())) {
				for (TextChangedListener handler : textChangedListeners) {
					handler.textChanged(this, textBefore, getText());
				}
			}
			
			return result;
		}
	}
	
	public EventExtendedTextBox removeHandler(KeyStroke keyStroke, KeyStrokeHandler handler) {
		List<KeyStrokeHandler> handlers = keyStrokeHandlers.get(keyStroke);
		
		if (handlers != null) {
			handlers.remove(handler);
		}
		
		return this;
	}
	
	public EventExtendedTextBox removeTextChangedHandler(TextChangedListener handler) {
		textChangedListeners.remove(handler);
		
		return this;
	}
	
	public interface KeyStrokeHandler {
		public void handleKeyStroke(EventExtendedTextBox textBox);
	}
	
	public interface TextChangedListener {
		public void textChanged(EventExtendedTextBox textBox, String oldText, String newText);
	}
}
