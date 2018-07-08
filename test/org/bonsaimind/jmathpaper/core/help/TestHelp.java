
package org.bonsaimind.jmathpaper.core.help;

import org.bonsaimind.jmathpaper.core.support.Topic;
import org.junit.Assert;
import org.junit.Test;

public class TestHelp {
	@Test
	public void testInsertAndRetrieve() {
		Topic topic = new Topic("", "");
		topic.addTopic("/a/b/c", new Topic("d1", "d1"));
		topic.addTopic("/a/b/c/d2", "d2");
		
		assertTopic("a", "", topic.getTopic("a"));
		assertTopic("a", "", topic.getTopic("/a"));
		assertTopic("a", "", topic.getTopic("a/"));
		assertTopic("a", "", topic.getTopic("/a/"));
		assertTopic("b", "", topic.getTopic("/a/b"));
		assertTopic("c", "", topic.getTopic("/a/b/c"));
		
		assertTopic("d1", "d1", topic.getTopic("/a/b/c/d1"));
		assertTopic("d2", "d2", topic.getTopic("/a/b/c/d2"));
	}
	
	@Test
	public void testInsertReplacement() {
		Topic topic = new Topic("", "");
		topic.addTopic("/a/b/c", new Topic("d", "d"));
		topic.addTopic("/a/", new Topic("b", "b"));
		
		assertTopic("b", "b", topic.getTopic("/a/b"));
		assertTopic("c", "", topic.getTopic("/a/b/c"));
		assertTopic("d", "d", topic.getTopic("/a/b/c/d"));
	}
	
	@Test
	public void testLoadDefault() {
		Topic topic = Topic.buildFrom("help");
		
		Assert.assertNotNull(topic);
		Assert.assertFalse(topic.getTopics().isEmpty());
	}
	
	private final void assertTopic(String expectedName, String expectedContent, Topic topic) {
		Assert.assertNotNull(topic);
		Assert.assertEquals(expectedName, topic.getName());
		Assert.assertEquals(expectedContent, topic.getContent());
	}
}
