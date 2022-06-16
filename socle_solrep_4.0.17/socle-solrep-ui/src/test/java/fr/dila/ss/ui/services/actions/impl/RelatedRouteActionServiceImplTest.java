package fr.dila.ss.ui.services.actions.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import fr.dila.ss.api.service.DocumentRoutingService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ Framework.class })
@PowerMockIgnore("javax.management.*")
public class RelatedRouteActionServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    RelatedRouteActionServiceImpl service = new RelatedRouteActionServiceImpl();

    @Mock
    DocumentRoutingService documentRoutingService;

    @Mock
    DocumentModel currentDocument;

    @Mock
    DocumentModel document;

    @Mock
    FeuilleRoute feuille;

    @Mock
    CoreSession session;

    @Before
    public void before() {
        documentRoutingService = Mockito.mock(DocumentRoutingService.class);
        currentDocument = Mockito.mock(DocumentModel.class);
        document = Mockito.mock(DocumentModel.class);
        feuille = Mockito.mock(FeuilleRoute.class);
        session = Mockito.mock(CoreSession.class);

        PowerMockito.mockStatic(Framework.class);

        Mockito.when(Framework.getService(DocumentRoutingService.class)).thenReturn(documentRoutingService);
    }

    @Test
    public void testFindRelatedRoute() {
        Mockito
            .when(documentRoutingService.getRoutesForAttachedDocument(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());

        List<DocumentModel> liste = service.findRelatedRoute(session, null);
        assertNotNull(liste);
        assertTrue(liste.isEmpty());

        liste = service.findRelatedRoute(session, currentDocument);
        assertNotNull(liste);
        assertTrue(liste.isEmpty());

        Mockito.when(feuille.getDocument()).thenReturn(document);
        Mockito.when(document.getId()).thenReturn("monId");
        List<FeuilleRoute> feuilles = new ArrayList<>();
        feuilles.add(feuille);
        Mockito
            .when(documentRoutingService.getRoutesForAttachedDocument(Mockito.any(), Mockito.any()))
            .thenReturn(feuilles);

        liste = service.findRelatedRoute(session, currentDocument);
        assertNotNull(liste);
        assertEquals(1, liste.size());
        assertEquals("monId", liste.get(0).getId());
    }
}
