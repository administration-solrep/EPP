package fr.dila.epp.ui.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.dila.epp.ui.services.HistoriqueDossierUIService;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.MessageService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.ui.bean.DossierHistoriqueEPP;
import fr.dila.st.ui.bean.MessageVersion;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SolonEppServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class HistoriqueDossierUIServiceImplTest {
    private HistoriqueDossierUIService service;

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private DossierService dossierService;

    @Mock
    private EvenementService evenementService;

    @Mock
    private EvenementTypeService evenementTypeService;

    @Mock
    private MessageService messageService;

    @Mock
    private VersionService versionService;

    @Before
    public void before() {
        service = new HistoriqueDossierUIServiceImpl();

        PowerMockito.mockStatic(SolonEppServiceLocator.class);
        when(SolonEppServiceLocator.getDossierService()).thenReturn(dossierService);
        when(SolonEppServiceLocator.getEvenementService()).thenReturn(evenementService);
        when(SolonEppServiceLocator.getMessageService()).thenReturn(messageService);
        when(SolonEppServiceLocator.getVersionService()).thenReturn(versionService);
        when(SolonEppServiceLocator.getEvenementTypeService()).thenReturn(evenementTypeService);
    }

    @Test
    public void testGetHistoriqueDossier() {
        SpecificContext context = new SpecificContext();
        DocumentModel evenementDoc = buildEvenementDoc("EVT01");
        context.setCurrentDocument(evenementDoc);
        DocumentModel dossierDoc = mock(DocumentModel.class);
        when(dossierService.getDossier(any(CoreSession.class), anyString())).thenReturn(dossierDoc);

        List<DocumentModel> evtList = Arrays.asList(buildEvenementDoc("EVT01"), buildEvenementDoc("EVT02"));
        when(evenementService.getEvenementsRacineDuDossier(any(CoreSession.class), eq(dossierDoc))).thenReturn(evtList);

        List<DocumentModel> evtChildList = Arrays.asList(buildEvenementDoc("EVT03"), buildEvenementDoc("EVT04"));
        when(evenementService.findEvenementSuccessif(any(CoreSession.class), eq("EVT02"))).thenReturn(evtChildList);

        List<DocumentModel> msgList = Arrays.asList(
            buildMessageDoc("M1", "brouillon"),
            buildMessageDoc("M2", "publié"),
            buildMessageDoc("M3", "brouillon"),
            buildMessageDoc("M4", "publié")
        );
        when(messageService.getMessageByEvenementId(any(CoreSession.class), eq("EVT01"))).thenReturn(msgList.get(0));
        when(messageService.getMessageByEvenementId(any(CoreSession.class), eq("EVT02"))).thenReturn(msgList.get(1));
        when(messageService.getMessageByEvenementId(any(CoreSession.class), eq("EVT03"))).thenReturn(msgList.get(2));
        when(messageService.getMessageByEvenementId(any(CoreSession.class), eq("EVT04"))).thenReturn(msgList.get(3));

        List<DocumentModel> verList = Arrays.asList(
            buildVersionDoc("AN"),
            buildVersionDoc("AN"),
            buildVersionDoc("Sénat"),
            buildVersionDoc("AN")
        );
        when(versionService.getVersionActive(any(CoreSession.class), eq(evtList.get(0)), eq("brouillon")))
            .thenReturn(verList.get(0));
        when(versionService.getVersionActive(any(CoreSession.class), eq(evtList.get(1)), eq("publié")))
            .thenReturn(verList.get(1));
        when(versionService.getVersionActive(any(CoreSession.class), eq(evtChildList.get(0)), eq("brouillon")))
            .thenReturn(verList.get(2));
        when(versionService.getVersionActive(any(CoreSession.class), eq(evtChildList.get(1)), eq("publié")))
            .thenReturn(verList.get(3));

        EvenementTypeDescriptor descriptor1 = new EvenementTypeDescriptor();
        descriptor1.setLabel("Evenement 01");
        EvenementTypeDescriptor descriptor2 = new EvenementTypeDescriptor();
        descriptor2.setLabel("Evenement 02");
        EvenementTypeDescriptor descriptor3 = new EvenementTypeDescriptor();
        descriptor3.setLabel("Evenement 03");
        EvenementTypeDescriptor descriptor4 = new EvenementTypeDescriptor();
        descriptor4.setLabel("Evenement 04");
        when(evenementTypeService.getEvenementType("EVT01")).thenReturn(descriptor1);
        when(evenementTypeService.getEvenementType("EVT02")).thenReturn(descriptor2);
        when(evenementTypeService.getEvenementType("EVT03")).thenReturn(descriptor3);
        when(evenementTypeService.getEvenementType("EVT04")).thenReturn(descriptor4);

        DossierHistoriqueEPP historique = service.getHistoriqueDossier(context);

        assertEquals(3, historique.getLstVersions().size());

        MessageVersion mv1 = historique.getLstVersions().get(0);
        assertEquals("M1", mv1.getCommunicationId());
        assertEquals("Evenement 01", mv1.getCommunicationLabel());
        assertEquals("EVT01", mv1.getCommunicationType());
        assertEquals(true, mv1.isCourant());
        assertEquals(false, mv1.isAnnule());
        assertEquals(true, mv1.getLstChilds().isEmpty());

        MessageVersion mv2 = historique.getLstVersions().get(1);
        assertEquals("M2", mv2.getCommunicationId());
        assertEquals("Evenement 02", mv2.getCommunicationLabel());
        assertEquals("EVT02", mv2.getCommunicationType());
        assertEquals(false, mv2.isCourant());
        assertEquals(false, mv2.isAnnule());
        assertEquals(false, mv2.getLstChilds().isEmpty());

        MessageVersion mv3 = historique.getLstVersions().get(2);
        assertEquals("M3", mv3.getCommunicationId());
        assertEquals("Evenement 03", mv3.getCommunicationLabel());
        assertEquals("EVT03", mv3.getCommunicationType());
        assertEquals(false, mv3.isCourant());
        assertEquals(false, mv3.isAnnule());
        assertEquals(true, mv3.getLstChilds().isEmpty());

        MessageVersion mv4 = historique.getLstVersions().get(1).getLstChilds().get(0);
        assertEquals("M4", mv4.getCommunicationId());
        assertEquals("Evenement 04", mv4.getCommunicationLabel());
        assertEquals("EVT04", mv4.getCommunicationType());
        assertEquals(false, mv4.isCourant());
        assertEquals(false, mv4.isAnnule());
        assertEquals(true, mv4.getLstChilds().isEmpty());
    }

    private DocumentModel buildEvenementDoc(String title) {
        DocumentModel evenementDoc = mock(DocumentModel.class);
        Evenement evenement = mock(Evenement.class);
        when(evenementDoc.getAdapter(Evenement.class)).thenReturn(evenement);
        when(evenementDoc.getTitle()).thenReturn(title);
        when(evenementDoc.getId()).thenReturn(title);
        when(evenement.getTitle()).thenReturn(title);
        when(evenement.getTypeEvenement()).thenReturn(title);
        return evenementDoc;
    }

    private DocumentModel buildMessageDoc(String id, String type) {
        DocumentModel messageDoc = mock(DocumentModel.class);
        Message message = mock(Message.class);
        when(messageDoc.getAdapter(Message.class)).thenReturn(message);
        when(messageDoc.getId()).thenReturn(id);
        when(message.getMessageType()).thenReturn(type);
        return messageDoc;
    }

    private DocumentModel buildVersionDoc(String niveauLecture) {
        DocumentModel versionDoc = mock(DocumentModel.class);
        Version version = mock(Version.class);
        when(versionDoc.getAdapter(Version.class)).thenReturn(version);
        when(version.getNiveauLecture()).thenReturn(niveauLecture);
        return versionDoc;
    }
}
