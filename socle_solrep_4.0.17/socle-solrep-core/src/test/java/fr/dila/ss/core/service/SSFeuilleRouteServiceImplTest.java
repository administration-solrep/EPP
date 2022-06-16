package fr.dila.ss.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.common.collect.Lists;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.api.service.SSFeuilleRouteService;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.Collection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest({ QueryUtils.class, SSServiceLocator.class })
@Category(fr.dila.ss.core.PowerMockitoTests.class)
public class SSFeuilleRouteServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private MailboxPosteService mockMailboxPosteService;

    private SSFeuilleRouteService feuilleRouteService = Mockito.mock(
        SSFeuilleRouteServiceImpl.class,
        Mockito.CALLS_REAL_METHODS
    );

    private static final String FAKE_POSTE_MAILBOX_ID = "fakePosteMailBoxId";

    @Before
    public void setUp() {
        PowerMockito.mockStatic(QueryUtils.class);
        PowerMockito.mockStatic(SSServiceLocator.class);

        Mockito.when(SSServiceLocator.getMailboxPosteService()).thenReturn(mockMailboxPosteService);

        Mockito.when(mockMailboxPosteService.getPosteMailboxId(Mockito.anyString())).thenReturn(FAKE_POSTE_MAILBOX_ID);
    }

    @Test
    public void testGetActiveOrFutureRouteStepsForPosteId_posteNotNull() {
        String posteId = "posteId";
        CoreSession mockSession = Mockito.mock(CoreSession.class);

        DocumentModel docModel1 = Mockito.mock(DocumentModel.class);
        DocumentModel docModel2 = Mockito.mock(DocumentModel.class);
        DocumentModel docModel3 = Mockito.mock(DocumentModel.class);
        DocumentRef ref1 = Mockito.mock(DocumentRef.class);
        DocumentRef ref2 = Mockito.mock(DocumentRef.class);
        DocumentRef ref3 = Mockito.mock(DocumentRef.class);

        Mockito.when(docModel1.getRef()).thenReturn(ref1);
        Mockito.when(docModel2.getRef()).thenReturn(ref2);
        Mockito.when(docModel3.getRef()).thenReturn(ref3);

        DocumentModelList expected = new DocumentModelListImpl(Lists.newArrayList(docModel1, docModel2, docModel3));
        DocumentRef[] refs = new DocumentRef[] { docModel1.getRef(), docModel2.getRef(), docModel3.getRef() };
        Mockito.when(QueryUtils.doUFNXQLQueryForIds(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(refs);
        Mockito.when(mockSession.getDocuments(refs)).thenReturn(expected);

        Collection<DocumentModel> docs = feuilleRouteService.getFeuilleRouteWithActiveOrFutureRouteStepsForPosteId(
            mockSession,
            posteId
        );

        Mockito.verify(mockMailboxPosteService).getPosteMailboxId(posteId);
        assertThat(docs).containsExactly(docModel1, docModel2, docModel3);
    }

    @Test
    public void testGetActiveOrFutureRouteStepsForPosteId_posteNull() {
        CoreSession mockSession = Mockito.mock(CoreSession.class);

        try {
            feuilleRouteService.getFeuilleRouteWithActiveOrFutureRouteStepsForPosteId(mockSession, null);
            fail("Should have thrown error");
        } catch (NullPointerException e) {
            assertEquals("L'identifiant du poste est null", e.getMessage());
        }
    }
}
