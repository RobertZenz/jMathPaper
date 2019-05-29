/*
 * Copyright 2009 SIB Visions GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *
 * History
 *
 * 12.01.2008 - [HM] - creation
 * 06.07.2011 - [JR] - #414: reduced split arrow size and support for "no border"
 * 09.12.2012 - [JR] - dividerSize <= 1 disables one-touch expandable
 * 13.01.2015 - [JR] - #1139: forward background to coverage panel(s)
 */

package com.sibvisions.rad.ui.swing.ext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * SplitPane implementation that allows top, left, right, right and relative
 * anchored divider.
 * 
 * @author Martin Handsteiner
 * @see javax.swing.JSplitPane
 */
public class JVxSplitPane extends JSplitPane {
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Class members
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/** The Divider is fixed to the bottom border during resize. */
	public static final int DIVIDER_BOTTOM_RIGHT = 1;
	
	/** The Divider is fixed to the right border during resize. */
	public static final int DIVIDER_RELATIVE = 2;
	
	/** The Divider is fixed to the top border during resize. */
	public static final int DIVIDER_TOP_LEFT = 0;
	
	/** No minimum size is set. */
	private static final Dimension NO_MINIMUM_SIZE = new Dimension(0, 0);
	
	/** whether updateUI is active. */
	private boolean bUpdateUI = false;
	
	/** The divider alignment. */
	private int dividerAlignment = DIVIDER_RELATIVE;
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Initialization
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * Creates a new <code>JVxSplitPane</code> configured to arrange the child
	 * components side-by-side horizontally with no continuous layout, using two
	 * buttons for the components.
	 */
	public JVxSplitPane() {
		super();
	}
	
	/**
	 * Creates a new <code>JVxSplitPane</code> configured with the specified
	 * orientation and no continuous layout.
	 *
	 * @param pOrientation <code>JVxSplitPane.HORIZONTAL_SPLIT</code> or
	 *        <code>JVxSplitPane.VERTICAL_SPLIT</code>
	 * @exception IllegalArgumentException if <code>orientation</code> is not
	 *            one of HORIZONTAL_SPLIT or VERTICAL_SPLIT.
	 */
	public JVxSplitPane(int pOrientation) {
		super(pOrientation);
	}
	
	/**
	 * Creates a new <code>JVxSplitPane</code> with the specified orientation
	 * and redrawing style.
	 *
	 * @param pOrientation <code>JVxSplitPane.HORIZONTAL_SPLIT</code> or
	 *        <code>JVxSplitPane.VERTICAL_SPLIT</code>
	 * @param pContinuousLayout a boolean, true for the components to redraw
	 *        continuously as the divider changes position, false to wait until
	 *        the divider position stops changing to redraw
	 * @exception IllegalArgumentException if <code>orientation</code> is not
	 *            one of HORIZONTAL_SPLIT or VERTICAL_SPLIT
	 */
	public JVxSplitPane(int pOrientation, boolean pContinuousLayout) {
		super(pOrientation, pContinuousLayout);
	}
	
	/**
	 * Creates a new <code>JVxSplitPane</code> with the specified orientation
	 * and redrawing style, and with the specified components.
	 *
	 * @param pOrientation <code>JVxSplitPane.HORIZONTAL_SPLIT</code> or
	 *        <code>JVxSplitPane.VERTICAL_SPLIT</code>
	 * @param pContinuousLayout a boolean, true for the components to redraw
	 *        continuously as the divider changes position, false to wait until
	 *        the divider position stops changing to redraw
	 * @param pLeftComponent the <code>Component</code> that will appear on the
	 *        left of a horizontally-split pane, or at the top of a
	 *        vertically-split pane
	 * @param pRightComponent the <code>Component</code> that will appear on the
	 *        right of a horizontally-split pane, or at the bottom of a
	 *        vertically-split pane
	 * @exception IllegalArgumentException if <code>orientation</code> is not
	 *            one of HORIZONTAL_SPLIT or VERTICAL_SPLIT
	 */
	public JVxSplitPane(int pOrientation, boolean pContinuousLayout, Component pLeftComponent, Component pRightComponent) {
		super(pOrientation, pContinuousLayout, pLeftComponent, pRightComponent);
	}
	
	/**
	 * Creates a new <code>JVxSplitPane</code> with the specified orientation
	 * and with the specified components that do not do continuous redrawing.
	 *
	 * @param pOrientation <code>JVxSplitPane.HORIZONTAL_SPLIT</code> or
	 *        <code>JVxSplitPane.VERTICAL_SPLIT</code>
	 * @param pLeftComponent the <code>Component</code> that will appear on the
	 *        left of a horizontally-split pane, or at the top of a
	 *        vertically-split pane
	 * @param pRightComponent the <code>Component</code> that will appear on the
	 *        right of a horizontally-split pane, or at the bottom of a
	 *        vertically-split pane
	 * @exception IllegalArgumentException if <code>orientation</code> is not
	 *            one of: HORIZONTAL_SPLIT or VERTICAL_SPLIT
	 */
	public JVxSplitPane(int pOrientation, Component pLeftComponent, Component pRightComponent) {
		super(pOrientation, pLeftComponent, pRightComponent);
	}
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Overwritten methods
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addNotify() {
		super.addNotify();
		
		// called from LaF
		if (bUpdateUI) {
			return;
		}
		
		bUpdateUI = true;
		
		try {
			updateUI();
		} finally {
			bUpdateUI = false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doLayout() {
		if (getDividerLocation() < 0) // Set correct initial divider position.
		{
			Rectangle bounds = getBounds();
			Insets ins = getInsets();
			int size;
			int first;
			int second;
			
			if (getOrientation() == VERTICAL_SPLIT) {
				size = bounds.height;
				if (getTopComponent() == null || getBottomComponent() == null) {
					first = 25;
					second = 25;
				} else {
					first = JVxUtil.getPreferredSize(((Container)getTopComponent()).getComponent(0)).height;
					second = JVxUtil.getPreferredSize(((Container)getBottomComponent()).getComponent(0)).height;
				}
			} else {
				size = bounds.width;
				if (getTopComponent() == null || getBottomComponent() == null) {
					first = 25;
					second = 25;
				} else {
					first = JVxUtil.getPreferredSize(((Container)getLeftComponent()).getComponent(0)).width;
					second = JVxUtil.getPreferredSize(((Container)getRightComponent()).getComponent(0)).width;
				}
			}
			if (dividerAlignment == DIVIDER_TOP_LEFT) {
				setDividerLocation(Math.min(first + ins.left + getDividerSize(), size * 9 / 10));
			} else if (dividerAlignment == DIVIDER_BOTTOM_RIGHT) {
				setDividerLocation(Math.max(size - second - getDividerSize() - ins.right, size * 1 / 10));
			} else {
				if (first <= 0) {
					first = 1;
				}
				if (second <= 0) {
					second = 1;
				}
				setDividerLocation(Math.round((float)size * (float)first / (first + second)) - getDividerSize() / 2);
			}
		}
		if (getLeftComponent() != null) // if minimum size is set, use
										// JSplitPane functionality to prevent
										// panel getting smaller
		{
			Component leftComponent = ((Container)getLeftComponent()).getComponent(0);
			if (leftComponent.isMinimumSizeSet()) {
				getLeftComponent().setMinimumSize(leftComponent.getMinimumSize());
			} else {
				getLeftComponent().setMinimumSize(NO_MINIMUM_SIZE);
			}
		}
		if (getRightComponent() != null) // if minimum size is set, use
											// JSplitPane functionality to
											// prevent panel getting smaller
		{
			Component rightComponent = ((Container)getRightComponent()).getComponent(0);
			if (rightComponent.isMinimumSizeSet()) {
				getRightComponent().setMinimumSize(rightComponent.getMinimumSize());
			} else {
				getRightComponent().setMinimumSize(NO_MINIMUM_SIZE);
			}
		}
		
		super.doLayout();
		
		// Fix JSplitPanel Bug if panel was smaller than minimum size, the
		// divider is not corrected anymore.
		int size;
		Insets ins = getInsets();
		int location = getDividerLocation();
		int posSize;
		if (dividerAlignment == DIVIDER_TOP_LEFT && getLeftComponent() != null) {
			Component comp = ((Container)getLeftComponent()).getComponent(0);
			if (comp.isMinimumSizeSet()) {
				Dimension minSize = comp.getMinimumSize();
				if (getOrientation() == HORIZONTAL_SPLIT) {
					posSize = getSize().width - getDividerSize() - ins.left;
					size = minSize.width + ins.left;
				} else {
					posSize = getSize().height - getDividerSize() - ins.top;
					size = minSize.height + ins.top;
				}
				if (posSize < size && location < posSize) {
					setDividerLocation(posSize);
					validate();
				}
			}
		} else if (dividerAlignment == DIVIDER_BOTTOM_RIGHT && getRightComponent() != null) {
			Component comp = ((Container)getRightComponent()).getComponent(0);
			if (comp.isMinimumSizeSet()) {
				Dimension minSize = comp.getMinimumSize();
				if (getOrientation() == HORIZONTAL_SPLIT) {
					posSize = getSize().width - getDividerSize() - ins.right;
					size = posSize - minSize.width;
				} else {
					posSize = getSize().height - getDividerSize() - ins.bottom;
					size = posSize - minSize.height;
				}
				if (size < ins.left && location > ins.left) {
					setDividerLocation(ins.left);
					validate();
				}
			}
		}
	}
	
	/**
	 * Gets the divider alignment.
	 * 
	 * @return the divider alignment: {@link #DIVIDER_TOP_LEFT},
	 *         {@link #DIVIDER_BOTTOM_RIGHT}, {@link #DIVIDER_RELATIVE}
	 */
	public int getDividerAlignment() {
		return dividerAlignment;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(Component pComponent) {
		if (pComponent.getParent() instanceof CoveragePanel && pComponent.getParent().getParent() == this) {
			super.remove(pComponent.getParent());
		} else {
			super.remove(pComponent);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(int pIndex) {
		Component comp = getComponent(pIndex);
		
		super.remove(pIndex);
		
		if (comp instanceof CoveragePanel) {
			((CoveragePanel)comp).removeAll();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBackground(Color pColor) {
		super.setBackground(pColor);
		
		if (ui instanceof BasicSplitPaneUI && ((BasicSplitPaneUI)ui).getDivider() != null) {
			((BasicSplitPaneUI)ui).getDivider().setBackground(pColor);
		}
		
		Component comp;
		
		// #1139
		for (int i = 0; i < getComponentCount(); i++) {
			comp = getComponent(i);
			
			if (comp instanceof CoveragePanel) {
				comp.setBackground(pColor);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBorder(Border pBorder) {
		// called from LaF
		if (bUpdateUI) {
			return;
		}
		
		super.setBorder(pBorder);
	}
	
	/**
	 * Sets the divider alignment.
	 * 
	 * @param pDividerAlignment the divider alignment:
	 *        {@link #DIVIDER_TOP_LEFT}, {@link #DIVIDER_BOTTOM_RIGHT},
	 *        {@link #DIVIDER_RELATIVE}
	 */
	public void setDividerAlignment(int pDividerAlignment) {
		dividerAlignment = pDividerAlignment;
		switch (dividerAlignment) {
			case DIVIDER_TOP_LEFT:
				setResizeWeight(0);
				break;
			case DIVIDER_BOTTOM_RIGHT:
				setResizeWeight(1);
				break;
			default:
				setResizeWeight(0.5);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDividerLocation(int pLocation) {
		// If maximum size is set ensure, that the divider does not get greater
		// than the maximum size.
		Insets ins = getInsets();
		int size;
		boolean layoutNecessary = false;
		if (dividerAlignment == DIVIDER_TOP_LEFT && getLeftComponent() != null) {
			Component comp = ((Container)getLeftComponent()).getComponent(0);
			if (comp.isMaximumSizeSet()) {
				Dimension maxSize = comp.getMaximumSize();
				if (getOrientation() == HORIZONTAL_SPLIT) {
					size = maxSize.width + ins.left;
				} else {
					size = maxSize.height + ins.top;
				}
				if (pLocation > size) {
					pLocation = size;
					layoutNecessary = true;
				}
			}
		} else if (dividerAlignment == DIVIDER_BOTTOM_RIGHT && getRightComponent() != null) {
			Component comp = ((Container)getRightComponent()).getComponent(0);
			if (comp.isMaximumSizeSet()) {
				Dimension maxSize = comp.getMaximumSize();
				if (getOrientation() == HORIZONTAL_SPLIT) {
					size = getSize().width - maxSize.width - ins.right - getDividerSize();
				} else {
					size = getSize().height - maxSize.height - ins.bottom - getDividerSize();
				}
				if (pLocation < size) {
					pLocation = size;
					layoutNecessary = true;
				}
			}
		}
		super.setDividerLocation(pLocation);
		if (layoutNecessary) {
			validate();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDividerSize(int pSize) {
		super.setDividerSize(pSize);
		
		if (pSize <= 1) {
			setOneTouchExpandable(false);
		} else {
			setOneTouchExpandable(true);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUI(SplitPaneUI ui) {
		super.setUI(ui);
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("F6"), "none");
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("F8"), "none");
	}
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// User-defined methods
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateUI() {
		super.updateUI();
		
		if (getBorder() == null) {
			((BasicSplitPaneUI)ui).getDivider().setBorder(BorderFactory.createEmptyBorder());
			((BasicSplitPaneUI)ui).getDivider().setBackground(getBackground());
		} else {
			((BasicSplitPaneUI)ui).getDivider().setBackground(getBackground());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addImpl(Component pComponent, Object pConstraints, int pIndex) {
		if (DIVIDER.equals(pConstraints)) {
			super.addImpl(pComponent, pConstraints, pIndex);
		} else {
			JPanel coverage;
			if (pComponent.getParent() instanceof CoveragePanel) {
				// The component was already covered in any JVxSplitPane.
				coverage = (CoveragePanel)pComponent.getParent();
			} else {
				// Cover component to allow panel getting smaller if minimum
				// size is not explizitly set.
				coverage = new CoveragePanel();
				coverage.add(pComponent, BorderLayout.CENTER);
			}
			
			// #1139
			if (isBackgroundSet()) {
				coverage.setBackground(getBackground());
			}
			
			super.addImpl(coverage, pConstraints, pIndex);
		}
	}
	
	// ****************************************************************
	// Subclass definition
	// ****************************************************************
	
	/**
	 * Coverage Panel is needed to change minimum size other than defined in
	 * from the given component.
	 * 
	 * @author Martin Handsteiner
	 */
	public static class CoveragePanel extends JPanel {
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Initialization
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		
		/**
		 * Constructs a new CoveragePanel.
		 */
		public CoveragePanel() {
			super(new BorderLayout());
		}
		
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Overwritten methods
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove(Component pComponent) {
			super.remove(pComponent);
			
			removeFromParent();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove(int pIndex) {
			super.remove(pIndex);
			
			removeFromParent();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeAll() {
			super.removeAll();
			
			removeFromParent();
		}
		
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// User-defined methods
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		
		/**
		 * Removes this panel from its parent, if needed.
		 */
		private void removeFromParent() {
			Container parent = getParent();
			
			if (parent != null) {
				parent.remove(this);
			}
		}
		
	} // CoveragePanel
	
} // JVxSplitPane
