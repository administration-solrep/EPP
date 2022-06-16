package fr.dila.st.core.util;

import static fr.dila.st.core.util.FileUtils.DEFAULT_APP_FOLDER_TMP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(STServiceLocator.class)
@PowerMockIgnore("javax.management.*")
public class FileUtilsTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private ConfigService configService;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getConfigService()).thenReturn(configService);

        when(configService.getValue(STConfigConstants.APP_FOLDER_TMP, DEFAULT_APP_FOLDER_TMP))
            .thenReturn(DEFAULT_APP_FOLDER_TMP);
    }

    @Test
    public void testGetAppTmpFolder() {
        File file = FileUtils.getAppTmpFolder();
        assertThat(file.getAbsolutePath()).isEqualTo(DEFAULT_APP_FOLDER_TMP);
    }

    @Test
    public void testGetAppTmpFilePath() {
        String path = FileUtils.getAppTmpFilePath("file.txt");
        assertThat(path).isEqualTo(DEFAULT_APP_FOLDER_TMP + "/file.txt");
    }

    @Test
    public void testGetAppTmpFile() {
        String filename = "file.txt";

        File file = FileUtils.getAppTmpFile(filename);

        assertThat(file.getName()).isEqualTo(filename);
        assertThat(file.getAbsolutePath()).isEqualTo(DEFAULT_APP_FOLDER_TMP + "/" + filename);
    }

    @Test
    public void testTrimExtension() {
        assertThat(FileUtils.trimExtension("/foo/bar.doc")).isEqualTo("bar");
        assertThat(FileUtils.trimExtension("/foo/bar")).isEqualTo("bar");
        assertThat(FileUtils.trimExtension("bar.doc")).isEqualTo("bar");
        assertThat(FileUtils.trimExtension("bar")).isEqualTo("bar");
    }

    @Test
    public void testGenerateCompletePdfFilename() {
        List<String> results = Stream
            .of("test1.pdf", "test2.PDF", "test3", null)
            .map(FileUtils::generateCompletePdfFilename)
            .collect(Collectors.toList());

        assertThat(results).containsExactly("test1.pdf", "test2.PDF", "test3.pdf", null);
    }

    @Test
    public void testGenerateCompleteDocxFilename() {
        List<String> results = Stream
            .of("test1.docx", "test2.DOCX", "test3", "test4.doc", null)
            .map(FileUtils::generateCompleteDocxFilename)
            .collect(Collectors.toList());

        assertThat(results).containsExactly("test1.docx", "test2.DOCX", "test3.docx", "test4.doc.docx", null);
    }

    @Test
    public void testGenerateCompleteCsvFilename() {
        List<String> results = Stream
            .of("test1.csv", "test2.CSV", "test3", null)
            .map(FileUtils::generateCompleteCsvFilename)
            .collect(Collectors.toList());

        assertThat(results).containsExactly("test1.csv", "test2.CSV", "test3.csv", null);
    }

    @Test
    public void testGenerateCompleteXlsFilename() {
        List<String> results = Stream
            .of("test1.xls", "test2.XLS", "test3", null)
            .map(FileUtils::generateCompleteXlsFilename)
            .collect(Collectors.toList());

        assertThat(results).containsExactly("test1.xls", "test2.XLS", "test3.xls", null);
    }

    @Test
    public void testGetShorterName() {
        getShorterNameTest(
            "nomDeFichierSuperLongPourUnAffichageGraphiqueCorrect.doc",
            "nomDeFichierSuperLongPour...ichageGraphiqueCorrect.doc"
        );
    }

    @Test
    public void testGetShorterNameWitoutExtension() {
        getShorterNameTest(
            "nomDeFichierSuperLongPourUnAffichageGraphiqueCorrect",
            "nomDeFichierSuperLongPour...ichageGraphiqueCorrect"
        );
    }

    private void getShorterNameTest(String longFilename, String shorterFilename) {
        String result = FileUtils.getShorterName(longFilename);
        assertThat(result).isEqualTo(shorterFilename);
        result = FileUtils.getShorterName(shorterFilename);
        assertThat(result).isEqualTo(shorterFilename);
    }

    @Test
    public void testGetExtensionWithSeparator() {
        List<String> filenames = ImmutableList.of("file.txt", "file.docx", "file");

        List<String> results = filenames
            .stream()
            .map(FileUtils::getExtensionWithSeparator)
            .collect(Collectors.toList());

        assertThat(results).containsExactly(".txt", ".docx", "");
    }

    @Test
    public void test_sanitizePathTraversal() {
        assertThat(FileUtils.sanitizePathTraversal("filename")).isEqualTo("filename");
        assertThat(FileUtils.sanitizePathTraversal("tmp/2349876398/foo.pdf")).isEqualTo("foo.pdf");
        assertThat(FileUtils.sanitizePathTraversal("/tmp/2349876398/foo.pdf")).isEqualTo("foo.pdf");
        assertThat(FileUtils.sanitizePathTraversal("/tmp/2349876398/../foo.pdf")).isEqualTo("foo.pdf");
        assertThat(FileUtils.sanitizePathTraversal("/tmp/2349876398/./foo.pdf")).isEqualTo("foo.pdf");
        assertThat(FileUtils.sanitizePathTraversal("../filename")).isEqualTo("filename");
        assertThat(FileUtils.sanitizePathTraversal("<filename>")).isEqualTo("_filename_");
        assertThat(FileUtils.sanitizePathTraversal("/\\file:/*'name\"?|<>")).isEqualTo("__name_____");
        assertThat(FileUtils.sanitizePathTraversal("/\\file:*'name\"?|<>")).isEqualTo("_file___name_____");
    }

    @Test
    public void testIsValidFilename() {
        List<String> filenames = ImmutableList.of(
            "test1.txt",
            "test_.txt",
            "test+.txt",
            "test-.txt",
            "test!.txt",
            "test%.txt",
            "test$.txt",
            "test@.txt",
            "test(.txt",
            "test[.txt",
            "test{.txt",
            "test#.txt",
            "test&.txt"
        );
        assertThat(filenames).allSatisfy(filename -> assertThat(FileUtils.isValidFilename(filename)).isTrue());
    }

    @Test
    public void testIsValidFilenameWithBadFilename() {
        List<String> badFilenames = ImmutableList.of(
            "test:.txt",
            "test\\.txt",
            "test/.txt",
            "test*.txt",
            "test'.txt",
            "test\".txt",
            "test?.txt",
            "test|.txt",
            "test<.txt",
            "test>.txt"
        );
        assertThat(badFilenames).allSatisfy(filename -> assertThat(FileUtils.isValidFilename(filename)).isFalse());
    }
}
