package fr.dila.st.core.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nuxeo.common.utils.ZipUtils;

import junit.framework.TestCase;

/**
 * Test de la classe StringUtil.
 * 
 */
public class TestZipUtil extends TestCase {

	public void testZipFiles() throws IOException {
		File zipFile = File.createTempFile("zipTest", ".zip");
		List<File> filesToAdd = new ArrayList<File>();

		for (int i = 0; i < 10; i++) {
			filesToAdd.add(File.createTempFile("fichier" + i, ".tmp"));
		}

		ZipUtil.zipFiles(zipFile, filesToAdd, "UTF-8");

		List<String> entryNames = ZipUtils.getEntryNames(zipFile);

		assertFalse(entryNames.isEmpty());
		assertEquals(10, entryNames.size());
		int i = 0;
		for (String s : entryNames) {
			assertTrue(s.startsWith("fichier" + i++));
			assertTrue(s.endsWith(".tmp"));
		}

	}
}
