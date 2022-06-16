package fr.sword.naiad.commons.core.utils;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

public class WhoCalledMeTest {

	private static final Logger LOGGER = Logger.getLogger(WhoCalledMeTest.class);
	
	@Test
	public void testWhoCalledMe() throws Exception {
		
		String expected = WhoCalledMeTest.class.getCanonicalName()+".whoCalledMeMethodFinal"+":.*";
		String result = whoCalledMeMethodOne();
		Assert.assertTrue(result.matches(expected));		
	}
	
	@Test
	public void testgetCallstack() throws Exception {
		try {
			String result = getCallStackOne();
			LOGGER.debug(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private String whoCalledMeMethodOne() throws Exception {
		String result = whoCalledMeMethodTwo();
		return result;
	}
	
	
	private String whoCalledMeMethodTwo() throws Exception {
		String result = whoCalledMeMethodFinal();
		return result;
	}
	
	private String whoCalledMeMethodFinal() throws Exception {
		String result = WhoCalledMe.whoCalledMe();
		return result;
	}
	
	
	
	
	private String getCallStackOne() throws Exception {
		String result = getCallStackTwo();
		return result;
	}
	
	
	private String getCallStackTwo() throws Exception {
		String result = getCallStackFinal();
		return result;
	}
	
	private String getCallStackFinal() throws Exception {
		String result = WhoCalledMe.getCallStack();
		return result;
	}
	
}
