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

package org.bonsaimind.jmathpaper.core.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonsaimind.jmathpaper.core.resources.ResourceLoader;

public class Topic {
	protected String content = null;
	protected String name = null;
	protected Topic parent = null;
	protected String title = null;
	protected List<Topic> topics = new ArrayList<>();
	protected Map<String, Topic> topicsByName = new HashMap<>();
	private List<Topic> topicsReadonly = null;
	
	public Topic(String name, String content) {
		super();
		
		this.name = name;
		this.content = content;
		
		updateTitle();
	}
	
	public static final Topic buildFrom(String relativeResourcePackage) {
		Topic rootNode = new Topic("", "");
		
		ResourceLoader.processResource(relativeResourcePackage + "/topics.index", (line) -> {
			String path = line;
			
			if (path.endsWith(".markdown")) {
				path = path.substring(0, path.length() - 9);
			}
			
			if (path.endsWith("/main")) {
				path = path.substring(0, path.length() - 5);
			} else if (path.equals("main")) {
				path = "";
			}
			
			rootNode.addTopic(path, ResourceLoader.readResource(relativeResourcePackage + "/" + line));
		});
		
		return rootNode;
	}
	
	public void addTopic(String path, String content) {
		Topic topic = getTopic(path);
		
		if (topic == this) {
			this.content = content;
			updateTitle();
		} else if (topic != null) {
			topic.content = content;
			topic.updateTitle();
		} else {
			String targetPath = "";
			String name = path;
			
			if (name.endsWith("/")) {
				name = name.substring(0, name.length() - 1);
			}
			
			int lastSlashIndex = name.lastIndexOf("/");
			
			if (lastSlashIndex >= 0) {
				targetPath = name.substring(0, lastSlashIndex);
				name = name.substring(lastSlashIndex + 1);
			}
			
			addTopic(targetPath, new Topic(name, content));
		}
	}
	
	public void addTopic(String path, Topic topic) {
		Topic parent = getTopic(path);
		
		if (parent == null) {
			parent = this;
			
			for (String name : path.split("/")) {
				if (!name.isEmpty()) {
					Topic subTopic = parent.getTopic(name);
					
					if (subTopic == null) {
						subTopic = new Topic(name, "");
						parent.addTopic(subTopic);
					}
					
					parent = subTopic;
				}
			}
		}
		
		Topic alreadyExistingTopic = parent.getTopic(topic.getName());
		
		if (alreadyExistingTopic != null) {
			topic.addTopics(alreadyExistingTopic.getTopics());
		}
		
		parent.addTopic(topic);
	}
	
	public void addTopic(Topic topic) {
		if (topic == null) {
			return;
		}
		
		topics.add(topic);
		
		Topic previousTopic = topicsByName.put(topic.getName(), topic);
		
		topic.setParent(this);
		
		if (previousTopic != null) {
			previousTopic.setParent(null);
			topics.remove(previousTopic);
		}
	}
	
	public void addTopics(Iterable<Topic> topics) {
		if (topics == null) {
			return;
		}
		
		for (Topic topic : topics) {
			addTopic(topic);
		}
	}
	
	public String getContent() {
		return content;
	}
	
	public String getName() {
		return name;
	}
	
	public Topic getParent() {
		return parent;
	}
	
	public Topic getRoot() {
		Topic ancestor = this;
		
		while (ancestor.getParent() != null) {
			ancestor = ancestor.getParent();
		}
		
		return ancestor;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Topic getTopic(String pathOrName) {
		if (pathOrName == null || pathOrName.isEmpty()) {
			return this;
		}
		
		Topic topic = topicsByName.get(pathOrName);
		
		if (topic == null) {
			if (pathOrName.contains("/")) {
				
				topic = this;
				
				for (String name : pathOrName.split("/")) {
					if (!name.isEmpty()) {
						topic = topic.getTopic(name);
						
						if (topic == null) {
							return null;
						}
					}
				}
			} else {
				for (Topic childTopic : topics) {
					topic = childTopic.getTopic(pathOrName);
					
					if (topic != null) {
						return topic;
					}
				}
			}
		}
		
		return topic;
	}
	
	public List<Topic> getTopics() {
		if (topicsReadonly == null) {
			topicsReadonly = Collections.unmodifiableList(topics);
		}
		
		return topicsReadonly;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	protected void setParent(Topic topic) {
		parent = topic;
	}
	
	protected void updateTitle() {
		title = name;
		
		if (content != null) {
			int newlineIndex = content.indexOf("\n");
			
			if (newlineIndex > 0) {
				title = content.substring(0, newlineIndex);
			} else if (newlineIndex < 0) {
				title = content;
			}
		}
	}
}
