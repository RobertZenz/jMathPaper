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

package org.bonsaimind.jmathpaper.uis.swing.help;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.bonsaimind.jmathpaper.core.support.Topic;
import org.bonsaimind.jmathpaper.uis.swing.help.model.TopicTreeModel;
import org.bonsaimind.jmathpaper.uis.swing.help.model.TopicTreeNode;

public class HelpFrame extends JFrame {
	private JTextArea textArea = null;
	private JTree tree = null;
	
	public HelpFrame(Topic rootTopic) throws HeadlessException {
		super();
		
		tree = new JTree();
		tree.setModel(new TopicTreeModel(rootTopic));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.getSelectionModel().addTreeSelectionListener(this::onTreeSelectedNodeChanged);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font(Font.MONOSPACED, 0, 12));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		JScrollPane teaxtareaScrollPane = new JScrollPane();
		teaxtareaScrollPane.setViewportView(textArea);
		teaxtareaScrollPane.setAutoscrolls(true);
		
		setLayout(new BorderLayout());
		setSize(600, 380);
		setTitle("jMathPaper Help");
		add(tree, BorderLayout.WEST);
		add(teaxtareaScrollPane, BorderLayout.CENTER);
	}
	
	public void showTopic(Topic topic) {
		
	}
	
	private void onTreeSelectedNodeChanged(TreeSelectionEvent event) {
		TreePath selectedTreePath = tree.getSelectionModel().getSelectionPath();
		
		if (selectedTreePath != null) {
			TopicTreeNode topicTreeNode = (TopicTreeNode)selectedTreePath.getLastPathComponent();
			Topic topic = topicTreeNode.getTopic();
			
			textArea.setText(topic.getContent());
		} else {
			textArea.setText("");
		}
	}
}
