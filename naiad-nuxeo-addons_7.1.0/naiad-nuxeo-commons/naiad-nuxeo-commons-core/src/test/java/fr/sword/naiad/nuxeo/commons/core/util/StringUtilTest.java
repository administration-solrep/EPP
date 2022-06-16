package fr.sword.naiad.nuxeo.commons.core.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.NuxeoException;

public class StringUtilTest {

	@Test
	public void testJoin(){
		
		final String a = "a";
		final String b = "b";
		final String c = "c";
		final String sep = " , ";
		final String quote = "'";
		List <String> elts = Arrays.asList(a, b, c);
		
		String expected_sep_quote = quote + a + quote + sep + quote + b + quote + sep + quote +  c + quote;
		String expected_sep = a + sep + b +sep + c;
		String expected = a  + b  + c;
		String expected_quote = quote + a + quote + quote + b + quote + quote +  c + quote;

		Assert.assertEquals(expected, StringUtil.join(elts));

		Assert.assertEquals(expected_sep, StringUtil.join(elts, sep));

		Assert.assertEquals(expected_sep_quote, StringUtil.join(elts, sep,  quote));
		
		Assert.assertEquals(expected_sep, StringUtil.join(elts, sep, ""));
		
		Assert.assertEquals(expected, StringUtil.join(elts, "", ""));
		
		Assert.assertEquals(expected_quote, StringUtil.join(elts, "", quote));
		
		Assert.assertEquals(expected_sep, StringUtil.join(elts, sep, null));
		
		Assert.assertEquals(expected, StringUtil.join(elts, null, null));
		
		Assert.assertEquals(expected_quote, StringUtil.join(elts, null, quote));
		
		final String start = "[";
		final String end = "]";
		
		final String expected_sep_start_end = start + a + end + sep + start + b + end + sep + start + c + end;
		final String expected_sep_start = start + a + sep + start + b + sep + start + c ;
		final String expected_sep_end = a + end + sep + b + end + sep + c + end;
		final String expected_start_end = start + a + end + start + b + end + start + c + end;
		
		Assert.assertEquals(expected_sep_start_end, StringUtil.join(elts, sep, start, end));
		
		Assert.assertEquals(expected_sep_start, StringUtil.join(elts, sep, start, ""));
		
		Assert.assertEquals(expected_sep_end, StringUtil.join(elts, sep, "", end));
		
		Assert.assertEquals(expected_start_end, StringUtil.join(elts, "", start, end));
		
		Assert.assertEquals(expected_sep, StringUtil.join(elts, sep, null, null));
		
		Assert.assertEquals(expected, StringUtil.join(elts, null, null, null));
	}
	
	@Test
	public void testRenderFreeMarker() throws NuxeoException{
		
		String template = "mon test est ${a_key}.";
		String ok = "ok";
		String expectedString = "mon test est ok.";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_key", ok);
		String renderString = StringUtil.renderFreemarker("", template, paramMap);
		Assert.assertEquals(expectedString, renderString);
		
		template = "";
		expectedString = "";		
		renderString = StringUtil.renderFreemarker("", template, paramMap);
		Assert.assertEquals(expectedString, renderString);
		
		template = null;
		expectedString = "";		
		renderString = StringUtil.renderFreemarker("", template, paramMap);
		Assert.assertEquals(expectedString, renderString);
	}
	
	@Test
	public void testGenMarks(){
		Assert.assertEquals("", StringUtil.genMarksSuite(0));
		Assert.assertEquals("?", StringUtil.genMarksSuite(1));
		Assert.assertEquals("?,?,?", StringUtil.genMarksSuite(3));
	}
	
}
