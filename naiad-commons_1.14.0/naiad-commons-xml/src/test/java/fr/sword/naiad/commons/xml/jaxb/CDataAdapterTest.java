package fr.sword.naiad.commons.xml.jaxb;

import junit.framework.Assert;

import org.junit.Test;

public class CDataAdapterTest {

	private static final String STR = "<test><hello>Hello World !</hello></test>";
	private static final String CDATA_START = "<![CDATA[";
	private static final String CDATA_END = "]]>";
	
	
	private static final String STR_CDATA= CDATA_START+STR+CDATA_END;
	
	private static final String STR_NOCDATA = STR;
	private static final String STR_CDATA_START_ONLY = CDATA_START+STR;
	private static final String STR_CDATA_END_ONLY = STR+CDATA_END;
	
	private static final String STR_CDATA_BROKEN_START = "z"+STR_CDATA;
	private static final String STR_CDATA_BROKEN_END = STR_CDATA+"z";
	
	@Test
	public void testIsCdata() {
		
		Assert.assertTrue(CDataAdapter.isCdata(STR_CDATA));
		Assert.assertFalse(CDataAdapter.isCdata(STR_NOCDATA));
		Assert.assertFalse(CDataAdapter.isCdata(STR_CDATA_START_ONLY));
		Assert.assertFalse(CDataAdapter.isCdata(STR_CDATA_END_ONLY));
		Assert.assertFalse(CDataAdapter.isCdata(STR_CDATA_BROKEN_START));
		Assert.assertFalse(CDataAdapter.isCdata(STR_CDATA_BROKEN_END));
	}
	
	@Test
	public void testParseNoParse() {
		String result = CDataAdapter.parse(STR_NOCDATA);
		Assert.assertEquals(STR_NOCDATA, result);
	}

	@Test
	public void testParseWithParse() {
		String result = CDataAdapter.parse(STR_CDATA);
		Assert.assertEquals(STR_NOCDATA, result);
	}

	
	@Test
	public void testPrintNoCData() {
		String result = CDataAdapter.print(STR_NOCDATA);
		Assert.assertEquals(STR_CDATA, result);
	}

	
	@Test
	public void testPrintCata() {
		String result = CDataAdapter.print(STR_CDATA);
		Assert.assertEquals(STR_CDATA, result);
	}
	
}
