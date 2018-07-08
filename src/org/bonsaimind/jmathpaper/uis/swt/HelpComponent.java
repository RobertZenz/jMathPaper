
package org.bonsaimind.jmathpaper.uis.swt;

import org.bonsaimind.jmathpaper.core.support.Topic;
import org.bonsaimind.jmathpaper.uis.swt.events.ForwardingSelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class HelpComponent extends SashForm {
	private StyledText contentText = null;
	private Tree topicsTree = null;
	
	public HelpComponent(Composite parent, int style) {
		super(parent, style);
		
		topicsTree = new Tree(this, SWT.BORDER);
		topicsTree.addSelectionListener(new ForwardingSelectionListener(this::onTopicsTreeSelectionChanged));
		
		contentText = new StyledText(this, SWT.BORDER | SWT.H_SCROLL | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		contentText.setAlwaysShowScrollBars(false);
		contentText.setEditable(false);
		contentText.setWordWrap(true);
		
		setWeights(new int[] { 1, 2 });
	}
	
	public void showHelp(Topic topic) {
		rebuildTree(topic.getRoot());
		
		if (topicsTree.getItemCount() > 0) {
			TreeItem treeItem = topicsTree.getItem(0);
			
			if (treeItem.getData() != topic) {
				treeItem = findTreeItem(topic, topicsTree.getItem(0));
			}
			
			if (treeItem != null) {
				topicsTree.select(treeItem);
			}
		}
		
		onTopicsTreeSelectionChanged();
	}
	
	private void attachTopics(Topic topic, TreeItem parentTreeItem) {
		if (topic == null) {
			return;
		}
		
		TreeItem treeItem = setupTreeItem(new TreeItem(parentTreeItem, SWT.NONE), topic);
		
		for (Topic subStopic : topic.getTopics()) {
			attachTopics(subStopic, treeItem);
		}
		
		treeItem.setExpanded(true);
	}
	
	private TreeItem findTreeItem(Topic topic, TreeItem currentTreeItem) {
		for (TreeItem treeItem : currentTreeItem.getItems()) {
			if (treeItem.getData() == topic) {
				return treeItem;
			} else {
				TreeItem foundTreeItem = findTreeItem(topic, treeItem);
				
				if (foundTreeItem != null) {
					return foundTreeItem;
				}
			}
		}
		
		return null;
	}
	
	private void onTopicsTreeSelectionChanged() {
		if (topicsTree.getSelectionCount() > 0) {
			TreeItem selectedTreeItem = topicsTree.getSelection()[0];
			Topic selectedTopic = (Topic)selectedTreeItem.getData();
			
			contentText.setText(selectedTopic.getContent());
		}
	}
	
	private void rebuildTree(Topic topic) {
		topicsTree.removeAll();
		
		TreeItem treeItem = setupTreeItem(new TreeItem(topicsTree, SWT.NONE), topic);
		
		for (Topic subStopic : topic.getTopics()) {
			attachTopics(subStopic, treeItem);
		}
		
		treeItem.setExpanded(true);
	}
	
	private TreeItem setupTreeItem(TreeItem treeItem, Topic topic) {
		treeItem.setData(topic);
		treeItem.setText(topic.getTitle());
		
		return treeItem;
	}
}
