package fr.sword.naiad.nuxeo.commons.core.util;

import org.junit.Assert;
import org.junit.Test;

public class PathUtilTest {
	@Test
	public void testAppend() {
		Assert.assertNull(PathUtil.append(null, null));
		Assert.assertEquals("", PathUtil.append("", null));
		Assert.assertEquals("", PathUtil.append(null, ""));
		Assert.assertEquals("ok", PathUtil.append("ok", null));
		Assert.assertEquals("ok", PathUtil.append(null, "ok"));
		Assert.assertEquals("", PathUtil.append("", ""));
		Assert.assertEquals("/a/b", PathUtil.append("/a/b", ""));
		Assert.assertEquals("/a/b/", PathUtil.append("/a/b/", ""));
		Assert.assertEquals("c", PathUtil.append("", "c"));
		Assert.assertEquals("/a/b/c", PathUtil.append("/a/b", "c"));
		Assert.assertEquals("/a/b/c", PathUtil.append("/a/b/", "c"));
	}

	@Test
	public void testParentPath() {
		Assert.assertNull(PathUtil.parentPath(null));
		Assert.assertNull(PathUtil.parentPath(""));
		Assert.assertNull(PathUtil.parentPath("azerty"));
		Assert.assertNull(PathUtil.parentPath("/"));
		Assert.assertNull(PathUtil.parentPath("////"));
		Assert.assertEquals("azerty", PathUtil.parentPath("azerty/qwerty"));
		Assert.assertEquals("azerty", PathUtil.parentPath("azerty/qwerty/"));
		Assert.assertEquals("azerty/qwerty", PathUtil.parentPath("azerty/qwerty/qwertz"));
		Assert.assertEquals("azerty", PathUtil.parentPath("azerty/qwerty/"));
		Assert.assertEquals("azerty", PathUtil.parentPath("azerty/qwerty////"));
		Assert.assertEquals("/azerty", PathUtil.parentPath("/azerty/qwerty"));
		Assert.assertEquals("/azerty", PathUtil.parentPath("/azerty/qwerty/"));
		Assert.assertEquals("/azerty", PathUtil.parentPath("/azerty/qwerty////"));
		Assert.assertEquals("/", PathUtil.parentPath("/azerty"));
		Assert.assertEquals("/", PathUtil.parentPath("/azerty/"));
		Assert.assertEquals("//", PathUtil.parentPath("//azerty"));
		Assert.assertEquals("azerty", PathUtil.parentPath("azerty////qwerty"));
	}
}
