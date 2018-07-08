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

package org.bonsaimind.jmathpaper.uis.swing.help.model;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import org.bonsaimind.jmathpaper.core.support.Topic;

public class TopicTreeNode implements TreeNode {
	protected TreeNode parent = null;
	protected Topic topic = null;
	
	public TopicTreeNode(Topic topic, TreeNode parent) {
		super();
		
		this.topic = topic;
		this.parent = parent;
	}
	
	@Override
	public Enumeration children() {
		return new Vector<>(topic.getTopics()).elements();
	}
	
	@Override
	public boolean getAllowsChildren() {
		return !topic.getTopics().isEmpty();
	}
	
	@Override
	public TreeNode getChildAt(int childIndex) {
		return new TopicTreeNode(topic.getTopics().get(childIndex), this);
	}
	
	@Override
	public int getChildCount() {
		return topic.getTopics().size();
	}
	
	@Override
	public int getIndex(TreeNode node) {
		return topic.getTopics().indexOf(((TopicTreeNode)node).getTopic());
	}
	
	@Override
	public TreeNode getParent() {
		return parent;
	}
	
	public Topic getTopic() {
		return topic;
	}
	
	@Override
	public boolean isLeaf() {
		return topic.getTopics().isEmpty();
	}
	
	@Override
	public String toString() {
		return topic.getTitle();
	}
}
