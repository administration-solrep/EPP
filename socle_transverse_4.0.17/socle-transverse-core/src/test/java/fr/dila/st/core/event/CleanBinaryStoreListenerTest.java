package fr.dila.st.core.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.google.common.collect.ImmutableMap;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ QueryUtils.class, Files.class, STServiceLocator.class, File.class, CleanBinaryStoreListener.class })
public class CleanBinaryStoreListenerTest {
    private static final String FILE_KEY = "filekey";
    private static final String FILE_PATH = "myPath/data/fi/le/" + FILE_KEY;

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private CoreSession session;

    @Mock
    private DocumentModel documentModel;

    private static final String DOCUMENT_ID = "documentId";

    @Mock
    private IterableQueryResult iterableQueryResult1;

    @Mock
    private IterableQueryResult iterableQueryResult2;

    @Mock
    private Iterator<Map<String, Serializable>> iteratorFile;

    @Mock
    private Iterator<Map<String, Serializable>> iteratorFiles;

    @Mock
    private ConfigService configService;

    @Captor
    private ArgumentCaptor<Path> pathCaptor;

    @Before
    public void before() throws Exception {
        when(documentModel.getId()).thenReturn(DOCUMENT_ID);

        mockStatic(QueryUtils.class);
        when(
                QueryUtils.doSqlQuery(
                    session,
                    new String[] { FlexibleQueryMaker.COL_COUNT },
                    CleanBinaryStoreListener.QUERY_FILE,
                    new Object[] { FILE_KEY, DOCUMENT_ID }
                )
            )
            .thenReturn(iterableQueryResult1);
        when(iterableQueryResult1.iterator()).thenReturn(iteratorFile);
        when(iteratorFile.hasNext()).thenReturn(true);

        when(
                QueryUtils.doSqlQuery(
                    session,
                    new String[] { FlexibleQueryMaker.COL_COUNT },
                    CleanBinaryStoreListener.QUERY_FILES,
                    new Object[] { FILE_KEY, DOCUMENT_ID }
                )
            )
            .thenReturn(iterableQueryResult2);
        when(iterableQueryResult2.iterator()).thenReturn(iteratorFiles);
        when(iteratorFiles.hasNext()).thenReturn(true);

        mockStatic(Files.class);
        when(Files.deleteIfExists(any(Path.class))).thenReturn(true);

        mockStatic(STServiceLocator.class);
        when(STServiceLocator.getConfigService()).thenReturn(configService);
        when(configService.getValue(STConfigConstants.REPOSITORY_BINARY_STORE_PATH)).thenReturn("myPath");
    }

    private void prepareMock(long file, long files) {
        when(iteratorFile.next()).thenReturn(ImmutableMap.of(FlexibleQueryMaker.COL_COUNT, file));
        when(iteratorFiles.next()).thenReturn(ImmutableMap.of(FlexibleQueryMaker.COL_COUNT, files));
    }

    private void verify(boolean checkFileDeletion) throws IOException {
        if (checkFileDeletion) {
            PowerMockito.verifyStatic();
            Files.deleteIfExists(pathCaptor.capture());
            assertThat(pathCaptor.getValue().toString()).isEqualTo(FILE_PATH);
        } else {
            PowerMockito.verifyStatic(never());
            Files.deleteIfExists(any(Path.class));
        }
    }

    @Test
    public void test_shouldDelete() throws IOException {
        prepareMock(0l, 0l);

        CleanBinaryStoreListener.cleanBlobIfPossible(session, documentModel.getId(), FILE_KEY);

        verify(true);
    }

    @Test
    public void test_shouldNotDeleteBecauseFDD() throws IOException {
        prepareMock(1l, 0l);

        CleanBinaryStoreListener.cleanBlobIfPossible(session, documentModel.getId(), FILE_KEY);

        verify(false);
    }

    @Test
    public void test_shouldNotDeleteBecauseActu() throws IOException {
        prepareMock(0l, 1l);

        CleanBinaryStoreListener.cleanBlobIfPossible(session, documentModel.getId(), FILE_KEY);

        verify(false);
    }
}
