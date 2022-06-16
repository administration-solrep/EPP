package fr.sword.naiad.commons.xml.jaxb;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class CdataCharacterEscapeHandlerTest {

	public static final Logger logger = Logger.getLogger(CdataCharacterEscapeHandler.class);

	private static final String CDATA_START = "<![CDATA[";
	private static final String CDATA_STOP = "]]>";
	
	private static final String STR 						= "hello world éèà & < > \\ \" \'";
	private static final String STR_ESCAPE_ATTVAL_TRUE 	= "hello world éèà &amp; &lt; &gt; \\ &quot; &apos;";
	private static final String STR_ESCAPE_ATT_VAL_FALSE 	= "hello world éèà &amp; &lt; &gt; \\ &quot; &apos;";
	
	
	public CdataCharacterEscapeHandlerTest() {
		super();
	}
	
	@Test
	public void testEscapeNoCdataIsAttValTrue() {
		
		try {
			String test = STR;
			String expected = STR_ESCAPE_ATTVAL_TRUE;
			
			StringWriter writer = new StringWriter();			
			runEscape(test, true, writer);

			Assert.assertEquals(expected, writer.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
			Assert.fail(e.getMessage());
		}
	}


	
	@Test
	public void testEscapeNoCdataIsAttValFalse() {
		
		try {
			
			String test = STR;
			String expected = STR_ESCAPE_ATT_VAL_FALSE;
			
			StringWriter writer = new StringWriter();
			runEscape(test, false, writer);
			
			Assert.assertEquals(expected, writer.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
			Assert.fail(e.getMessage());
		}
	}

	
	
	@Test
	public void testEscapeCdataIsAttValTrue() {
		
		try {
			String test = CDATA_START+STR+CDATA_STOP;
			String expected = CDATA_START+STR+CDATA_STOP;
			
			StringWriter writer = new StringWriter();			
			runEscape(test, true, writer);
						
			Assert.assertEquals(expected, writer.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
			Assert.fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testEscapeCdataIsAttValFalse() {
		
		try {
			String test = CDATA_START+STR+CDATA_STOP;
			String expected = CDATA_START+STR+CDATA_STOP;
			
			StringWriter writer = new StringWriter();
			runEscape(test, false, writer);
			
			Assert.assertEquals(expected, writer.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
			Assert.fail(e.getMessage());
		}
	}
	
	private static void runEscape(String s, boolean isAttVal, Writer writer) throws IOException {
		
		CdataCharacterEscapeHandler escapeHandler = new CdataCharacterEscapeHandler();		
		
		char [] ch = s.toCharArray();
		int start = 0;
		int length = s.length();
		
		escapeHandler.escape(ch, start, length, true, writer);
	}
	
}
