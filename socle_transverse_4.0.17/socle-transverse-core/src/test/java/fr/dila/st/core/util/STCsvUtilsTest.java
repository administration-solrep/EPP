package fr.dila.st.core.util;

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.AfterClass;
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
public class STCsvUtilsTest {
    private static final String DIR = "target/test-tmp-files";
    private static final String FILENAME = "STCsvUtilsTest";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private ConfigService configService;

    private final Path path = Paths.get(DIR);

    @Before
    public void setUp() throws IOException {
        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getConfigService()).thenReturn(configService);

        Files.createDirectories(path);

        when(configService.getValue(STConfigConstants.APP_FOLDER_TMP, FileUtils.DEFAULT_APP_FOLDER_TMP))
            .thenReturn(path.toString());
    }

    @AfterClass
    public static void afterClass() throws IOException {
        deleteDirectory(new File(DIR));
    }

    @Test
    public void writeAllLines() {
        List<String[]> lines = ImmutableList.of(
            new String[] { "NOR1", "Titre acte 1", "01/04/2021", "02/04/2021", "Type acte 1" },
            new String[] { "NOR2", "Titre acte 2", "03/04/2021", "04/04/2021", "Type acte 2" },
            new String[] { "NOR3", "Titre acte 3", "05/04/2021", "06/04/2021", "Type acte 3" }
        );

        File csvFile = STCsvUtils.writeAllLines(FILENAME, lines);

        String expectedLine1 = "NOR1,Titre acte 1,01/04/2021,02/04/2021,Type acte 1";
        String expectedLine2 = "NOR2,Titre acte 2,03/04/2021,04/04/2021,Type acte 2";
        String expectedLine3 = "NOR3,Titre acte 3,05/04/2021,06/04/2021,Type acte 3";

        assertThat(csvFile)
            .exists()
            .isFile()
            .hasName("STCsvUtilsTest.csv")
            .hasContent(String.join("\n", expectedLine1, expectedLine2, expectedLine3));
    }
}
