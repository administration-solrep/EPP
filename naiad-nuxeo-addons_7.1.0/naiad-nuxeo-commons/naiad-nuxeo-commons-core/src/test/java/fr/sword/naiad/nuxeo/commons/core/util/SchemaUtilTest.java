package fr.sword.naiad.nuxeo.commons.core.util;

import org.junit.Assert;
import org.junit.Test;

public class SchemaUtilTest {

	@Test
	public void testXPath(){
		
		final String prefix = "a_prefix";
		final String prop = "a_prop";
		final String expected = prefix + ":" + prop;
		
		Assert.assertEquals(expected,  SchemaUtil.xpath(prefix, prop));
		
	}
	
}

