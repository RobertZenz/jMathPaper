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
import java.util.List;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.AbstractInteractableComponent;
import com.googlecode.lanterna.gui2.InteractableRenderer;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class PseudoTabBar<CONTENT> extends AbstractInteractableComponent<PseudoTabBar<?>> {
	private List<CONTENT> contents = new ArrayList<>();
	private List<SelectedTabChangedListener> selectedTabChangedListeners = new ArrayList<>();
	private int selectedTabIndex = -1;
	private List<String> tabs = new ArrayList<>();
	
	public PseudoTabBar() {
		super();
	}
	
	public PseudoTabBar<CONTENT> addSelectedTabChangedListener(SelectedTabChangedListener listener) {
		selectedTabChangedListeners.add(listener);
		
		return this;
	}
	
	public PseudoTabBar<CONTENT> addTab(int index, String text, CONTENT content) {
		tabs.add(index, text);
		contents.add(index, content);
		
		setSelectedTab(index);
		
		return this;
	}
	
	public PseudoTabBar<CONTENT> addTab(String text, CONTENT content) {
		if (!tabs.isEmpty()) {
			return addTab(tabs.size() - 1, text, content);
		} else {
			return addTab(0, text, content);
		}
	}
	
	public CONTENT getContent(int index) {
		return contents.get(index);
	}
	
	public CONTENT getSelectedTabContent() {
		if (selectedTabIndex >= 0) {
			return contents.get(selectedTabIndex);
		} else {
			return null;
		}
	}
	
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}
	
	public String getTab(int index) {
		return tabs.get(index);
	}
	
	public int getTabCount() {
		return tabs.size();
	}
	
	public PseudoTabBar<CONTENT> removeSelectedTabChangedListener(SelectedTabChangedListener listener) {
		selectedTabChangedListeners.remove(listener);
		
		return this;
	}
	
	public PseudoTabBar<CONTENT> removeTab(int index) {
		tabs.remove(index);
		contents.remove(index);
		
		int oldSelectedTabIndex = selectedTabIndex;
		
		if (tabs.isEmpty()) {
			selectedTabIndex = -1;
		} else if (selectedTabIndex >= tabs.size()) {
			selectedTabIndex = tabs.size() - 1;
		}
		
		for (SelectedTabChangedListener listener : selectedTabChangedListeners) {
			listener.selectedTabChanged(this, oldSelectedTabIndex, selectedTabIndex);
		}
		
		invalidate();
		
		return this;
	}
	
	public PseudoTabBar<CONTENT> setSelectedTab(int index) {
		int oldSelectedTabIndex = selectedTabIndex;
		
		selectedTabIndex = index;
		
		if (oldSelectedTabIndex != selectedTabIndex) {
			for (SelectedTabChangedListener listener : selectedTabChangedListeners) {
				listener.selectedTabChanged(this, oldSelectedTabIndex, selectedTabIndex);
			}
		}
		
		invalidate();
		
		return this;
	}
	
	@Override
	protected InteractableRenderer<PseudoTabBar<?>> createDefaultRenderer() {
		return new DefaultRenderer<>();
	}
	
	@Override
	protected Result handleKeyStroke(KeyStroke keyStroke) {
		if (keyStroke.getKeyType() == KeyType.ArrowLeft || keyStroke.getKeyType() == KeyType.ArrowDown) {
			setSelectedTab(Math.max(0, selectedTabIndex - 1));
			return Result.HANDLED;
		} else if (keyStroke.getKeyType() == KeyType.ArrowRight || keyStroke.getKeyType() == KeyType.ArrowUp) {
			setSelectedTab(Math.min(tabs.size() - 1, selectedTabIndex + 1));
			return Result.HANDLED;
		}
		
		return super.handleKeyStroke(keyStroke);
	}
	
	public interface SelectedTabChangedListener {
		public void selectedTabChanged(PseudoTabBar<?> pseudoTabBar, int oldSelectedTabIndex, int newSelectedTabIndex);
	}
	
	private static final class DefaultRenderer<CONTENT> implements InteractableRenderer<PseudoTabBar<?>> {
		public DefaultRenderer() {
			super();
		}
		
		@Override
		public void drawComponent(TextGUIGraphics graphics, PseudoTabBar<?> component) {
			int column = 0;
			
			for (int index = 0; index < component.tabs.size(); index++) {
				String tab = component.tabs.get(index);
				
				if (index == component.selectedTabIndex) {
					graphics.applyThemeStyle(component.getTheme().getDefaultDefinition().getSelected());
				} else {
					graphics.applyThemeStyle(component.getTheme().getDefaultDefinition().getNormal());
				}
				
				graphics.putString(column, 0, tab);
				column = column + tab.length();
				
				graphics.applyThemeStyle(component.getTheme().getDefaultDefinition().getNormal());
				
				if (index != component.tabs.size() - 1) {
					graphics.putString(column, 0, " | ");
					column = column + 3;
				}
			}
		}
		
		@Override
		public TerminalPosition getCursorLocation(PseudoTabBar<?> component) {
			return null;
		}
		
		@Override
		public TerminalSize getPreferredSize(PseudoTabBar<?> component) {
			return new TerminalSize(1, 1);
		}
		
	}
}
