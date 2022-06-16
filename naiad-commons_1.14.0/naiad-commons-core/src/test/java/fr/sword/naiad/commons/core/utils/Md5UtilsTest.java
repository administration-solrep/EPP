package fr.sword.naiad.commons.core.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class Md5UtilsTest {

	private static String FILE_CONTENT = "lorem ipsum";
	private static String FILE_CONTENT_MD5       = "80a751fde577028640c419000e33eba6";
	private static String FILE_CONTENT_WRONG_MD5 = "10a751fde577028640c419000e33eba6";
	
	/**
	 * Test the getMd5CheckSum method
	 * @throws IOException 
	 * @throws Exception  
	 */
	@Test
	public void getMD5ChecksumTest() throws IOException {
		File testFile = null;
		FileOutputStream out = null;
		try {
			testFile = File.createTempFile("testFile", ".testMd5");
			out = new FileOutputStream(testFile);
			out.write(FILE_CONTENT.getBytes());
			
			Assert.assertTrue(Md5Utils.checkMd5(testFile, FILE_CONTENT_MD5));
			Assert.assertFalse(Md5Utils.checkMd5(testFile, FILE_CONTENT_WRONG_MD5));
			
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} finally {
			if (testFile != null) {
				testFile.delete();
			}
			if (out != null) {
				out.close();
			}
		}
	}
}
