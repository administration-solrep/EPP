package fr.dila.ss.ui.services.actions.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.ui.services.actions.RelatedRouteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SSActionsServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class DocumentRoutingActionServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    SSDocumentRoutingActionServiceImpl service = new SSDocumentRoutingActionServiceImpl();

    @Mock
    private DocumentModel currentDocument;

    @Mock
    private DocumentModel document;

    @Mock
    private DocumentModel parentDoc;

    @Mock
    private CoreSession session;

    @Mock
    private SpecificContext context;

    @Mock
    private DocumentRef docRef;

    @Mock
    private RelatedRouteActionService relatedRouteActionService;

    @Before
    public void before() {
        relatedRouteActionService = Mockito.mock(RelatedRouteActionService.class);
        currentDocument = Mockito.mock(DocumentModel.class);
        document = Mockito.mock(DocumentModel.class);
        session = Mockito.mock(CoreSession.class);

        PowerMockito.mockStatic(SSActionsServiceLocator.class);

        when(SSActionsServiceLocator.getRelatedRouteActionService()).thenReturn(relatedRouteActionService);

        when(context.getSession()).thenReturn(session);
    }

    @Test
    public void testHasRelatedRoute() {
        List<DocumentModel> returnListe = new ArrayList<>();
        Mockito.when(relatedRouteActionService.findRelatedRoute(session, currentDocument)).thenReturn(returnListe);
        assertEquals(false, service.hasRelatedRoute(session, currentDocument));

        returnListe.add(document);
        Mockito.when(relatedRouteActionService.findRelatedRoute(session, currentDocument)).thenReturn(returnListe);
        assertEquals(true, service.hasRelatedRoute(session, currentDocument));
    }

    @Test
    public void testIsModeleFeuilleRoute() {
        Path path = new Path(SSConstant.FDR_FOLDER_PATH + "/idModele");
        when(document.getPath()).thenReturn(path);

        assertTrue(service.isModeleFeuilleRoute(context, document));
    }
}
