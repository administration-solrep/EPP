package fr.dila.st.core.util;

import fr.dila.st.core.test.STFeature;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.ZipUtils;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Test de la classe StringUtil.
 *
 */
@RunWith(FeaturesRunner.class)
@Features(STFeature.class)
public class TestZipUtil {

    @Test
    public void testZipFiles() throws IOException {
        File zipFile = File.createTempFile("zipTest", ".zip");
        List<File> filesToAdd = new ArrayList<File>();

        for (int i = 0; i < 10; i++) {
            filesToAdd.add(File.createTempFile("fichier" + i, ".tmp"));
        }

        ZipUtil.zipFiles(zipFile, filesToAdd, "UTF-8");

        List<String> entryNames = ZipUtils.getEntryNames(zipFile);

        Assert.assertFalse(entryNames.isEmpty());
        Assert.assertEquals(10, entryNames.size());
        int i = 0;
        for (String s : entryNames) {
            Assert.assertTrue(s.startsWith("fichier" + i++));
            Assert.assertTrue(s.endsWith(".tmp"));
        }
    }
}
