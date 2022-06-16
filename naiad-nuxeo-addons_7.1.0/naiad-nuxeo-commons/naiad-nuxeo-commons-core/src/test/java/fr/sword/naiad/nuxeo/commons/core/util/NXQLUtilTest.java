package fr.sword.naiad.nuxeo.commons.core.util;

import org.junit.Assert;
import org.junit.Test;

public class NXQLUtilTest {
	@Test
	public void test1() {
		Assert.assertNull(NXQLUtil.escapeParameter(null));
		Assert.assertEquals("", NXQLUtil.escapeParameter(""));
		Assert.assertEquals("a", NXQLUtil.escapeParameter("a"));

		Assert.assertEquals("\\'a\\'", NXQLUtil.escapeParameter("'a'"));
		Assert.assertEquals("\\'\\'a\\'", NXQLUtil.escapeParameter("''a'"));
		Assert.assertEquals("\\'a\\'", NXQLUtil.escapeParameter("'a'"));
		Assert.assertEquals("a\\'qsd", NXQLUtil.escapeParameter("a'qsd"));
		Assert.assertEquals("a\\\\\\'qsd", NXQLUtil.escapeParameter("a\\'qsd"));
	}

	@Test
	public void test2() {
		Assert.assertNull(NXQLUtil.escapeParameter(null, null));
		Assert.assertEquals("", NXQLUtil.escapeParameter("", null));
		Assert.assertNull(NXQLUtil.escapeParameter(null, ""));
		Assert.assertEquals("a", NXQLUtil.escapeParameter("a", null));
		Assert.assertEquals("a", NXQLUtil.escapeParameter("a", "\""));

		Assert.assertEquals("a'", NXQLUtil.escapeParameter("a'", "\""));
		Assert.assertEquals("'a'", NXQLUtil.escapeParameter("'a'", "\""));
		Assert.assertEquals("'\\\"a'", NXQLUtil.escapeParameter("'\"a'", "\""));
		Assert.assertEquals("'\\\"a\\\\'", NXQLUtil.escapeParameter("'\"a\\'", "\""));
	}

	@Test
	public void test3() {
		Assert.assertNull(NXQLUtil.escapeParameter(null, null, null));
		Assert.assertEquals("", NXQLUtil.escapeParameter("", null, null));
		Assert.assertNull(NXQLUtil.escapeParameter(null, "", null));
		Assert.assertNull(NXQLUtil.escapeParameter(null, null, ""));
		Assert.assertEquals("a", NXQLUtil.escapeParameter("a", null, null));
		Assert.assertEquals("a", NXQLUtil.escapeParameter("a", "\"", null));
		Assert.assertEquals("a", NXQLUtil.escapeParameter("a", "\"", "|"));

		Assert.assertEquals("a'", NXQLUtil.escapeParameter("a'", "\"", "|"));
		Assert.assertEquals("|\"a'", NXQLUtil.escapeParameter("\"a'", "\"", "|"));
		Assert.assertEquals("|\"a\\'", NXQLUtil.escapeParameter("\"a\\'", "\"", "|"));
		Assert.assertEquals("|\"a\\||'", NXQLUtil.escapeParameter("\"a\\|'", "\"", "|"));
	}
}
