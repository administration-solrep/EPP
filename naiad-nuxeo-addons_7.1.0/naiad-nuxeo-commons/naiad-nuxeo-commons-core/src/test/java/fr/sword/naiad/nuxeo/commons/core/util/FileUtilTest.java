package fr.sword.naiad.nuxeo.commons.core.util;

import org.junit.Assert;
import org.junit.Test;

public class FileUtilTest {
	@Test
	public void testFilenameExt() {
		Assert.assertEquals(null, FileUtil.getExtension(null));
		Assert.assertEquals(null, FileUtil.getExtension(""));
		Assert.assertEquals(null, FileUtil.getExtension(".css"));
		Assert.assertEquals(null, FileUtil.getExtension("."));
		Assert.assertEquals(null, FileUtil.getExtension("a."));
		Assert.assertEquals("css", FileUtil.getExtension("a.css"));

		Assert.assertEquals(null, FileUtil.getExtension("/"));
		Assert.assertEquals(null, FileUtil.getExtension("///"));
		Assert.assertEquals(null, FileUtil.getExtension("/a//"));
		Assert.assertEquals(null, FileUtil.getExtension("/a/b/"));
		Assert.assertEquals(null, FileUtil.getExtension("/a/b"));
		Assert.assertEquals(null, FileUtil.getExtension("/a/b."));
		Assert.assertEquals(null, FileUtil.getExtension("/a/.b"));
		Assert.assertEquals(null, FileUtil.getExtension("/a/."));
		Assert.assertEquals("css", FileUtil.getExtension("/a/b.css"));
	}

	@Test
	public void testFileByPath() {
		Assert.assertEquals(null, FileUtil.getFilenameByPath(null));
		Assert.assertEquals(null, FileUtil.getFilenameByPath(""));
		Assert.assertEquals(null, FileUtil.getFilenameByPath("///"));
		Assert.assertEquals(null, FileUtil.getFilenameByPath("/"));
		Assert.assertEquals(null, FileUtil.getFilenameByPath("a/"));
		Assert.assertEquals("a", FileUtil.getFilenameByPath("a"));
		Assert.assertEquals("a", FileUtil.getFilenameByPath("/a"));
		Assert.assertEquals("a.", FileUtil.getFilenameByPath("/a."));
		Assert.assertEquals(".a", FileUtil.getFilenameByPath("/.a"));
		Assert.assertEquals(".a.css", FileUtil.getFilenameByPath("/.a.css"));
	}
}
