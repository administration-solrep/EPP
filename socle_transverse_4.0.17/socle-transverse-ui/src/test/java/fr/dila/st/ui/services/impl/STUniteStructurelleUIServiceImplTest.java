package fr.dila.st.ui.services.impl;

import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static fr.dila.st.ui.enums.STContextDataKey.UNITE_STRUCTURELLE_FORM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.th.bean.UniteStructurelleForm;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class STUniteStructurelleUIServiceImplTest {
    private static final String ID_US = "idUS";

    STUniteStructurelleUIServiceImpl uniteStrurelleUIService;

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    SpecificContext context;

    @Mock
    OrganigrammeService organigrammeService;

    @Mock
    UniteStructurelleNode uniteStructurelleNode;

    @Mock
    UniteStructurelleForm uniteStructurelleForm;

    @Mock
    STUsAndDirectionService usAndDirectionService;

    @Mock
    UniteStructurelleNode uniteStructurelleNodeParent;

    @Mock
    EntiteNode entiteNodeParent;

    @Mock
    STMinisteresService ministeresService;

    @Mock
    private CoreSession session;

    @Mock
    private SolonAlertManager messageQueue;

    @Captor
    private ArgumentCaptor<Consumer<UniteStructurelleNode>> updaterCaptor;

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(STServiceLocator.class);

        when(context.getMessageQueue()).thenReturn(messageQueue);
        when(context.getSession()).thenReturn(session);

        NuxeoPrincipal principal = mock(NuxeoPrincipal.class);
        when(principal.getName()).thenReturn("user");
        when(session.getPrincipal()).thenReturn(principal);

        when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);
        when(STServiceLocator.getSTUsAndDirectionService()).thenReturn(usAndDirectionService);
        when(STServiceLocator.getSTMinisteresService()).thenReturn(ministeresService);
        when(organigrammeService.getOrganigrammeNodeById(ID_US, OrganigrammeType.UNITE_STRUCTURELLE))
            .thenReturn(uniteStructurelleNode);

        uniteStrurelleUIService = spy(new STUniteStructurelleUIServiceImpl());
        when(uniteStructurelleForm.getIdentifiant()).thenReturn(ID_US);
    }

    @Test
    public void testGetUniteStructurelleForm() {
        Calendar calendar = new GregorianCalendar(2000, Calendar.JANUARY, 1);
        Date date = calendar.getTime();

        when(uniteStructurelleNode.getId()).thenReturn(ID_US);
        when(uniteStructurelleNode.getLabel()).thenReturn("Label uniteStructurelle");
        when(uniteStructurelleNode.getTypeValue()).thenReturn("US");
        when(uniteStructurelleNode.getDateDebut()).thenReturn(date);
        when(uniteStructurelleNode.getDateFin()).thenReturn(date);
        when(context.getFromContextData(ID)).thenReturn(ID_US);

        EntiteNode entiteParent = new EntiteNodeImpl();
        entiteParent.setId("idParentMin");
        entiteParent.setLabel("label Parent min");
        List<EntiteNode> entiteNodeParent = new ArrayList<>();
        entiteNodeParent.add(entiteParent);
        when(uniteStructurelleNode.getEntiteParentList()).thenReturn(entiteNodeParent);

        UniteStructurelleNode usParent = new UniteStructurelleNodeImpl();
        usParent.setId("idParentUS");
        usParent.setLabel("label Parent US");
        List<UniteStructurelleNode> usNodeParent = new ArrayList<>();
        usNodeParent.add(usParent);
        when(uniteStructurelleNode.getUniteStructurelleParentList()).thenReturn(usNodeParent);

        when(organigrammeService.lockOrganigrammeNode(Mockito.eq(session), Mockito.any())).thenReturn(true);

        UniteStructurelleForm uniteStructurelleForm = uniteStrurelleUIService.getUniteStructurelleForm(context);

        assertNotNull(uniteStructurelleForm);
        assertEquals(ID_US, uniteStructurelleForm.getIdentifiant());
        assertEquals("Label uniteStructurelle", uniteStructurelleForm.getLibelle());
        assertEquals("US", uniteStructurelleForm.getType());
        assertEquals("01/01/2000", uniteStructurelleForm.getDateDebut());
        assertEquals("01/01/2000", uniteStructurelleForm.getDateFin());
        assertNotNull(uniteStructurelleForm.getMapMinisteresRatachement());
        HashMap<String, String> mapMinistere = new HashMap<>();
        mapMinistere.put("idParentMin", "label Parent min");
        assertEquals(mapMinistere, uniteStructurelleForm.getMapMinisteresRatachement());
        List<String> listIdParentMin = new ArrayList<>();
        listIdParentMin.add("idParentMin");
        assertEquals(listIdParentMin, uniteStructurelleForm.getMinisteresRatachement());
        assertNotNull(uniteStructurelleForm.getMapUnitesStructurellesRattachement());
        HashMap<String, String> mapUs = new HashMap<>();
        mapUs.put("idParentUS", "label Parent US");
        assertEquals(mapUs, uniteStructurelleForm.getMapUnitesStructurellesRattachement());
        List<String> listIdParentUS = new ArrayList<>();
        listIdParentUS.add("idParentUS");
        assertEquals(listIdParentUS, uniteStructurelleForm.getUnitesStructurellesRattachement());
    }

    @Test
    public void testGetUniteStructurelleFormLockedByOtherUser() {
        // Given
        when(context.getFromContextData(ID)).thenReturn(ID_US);

        when(uniteStructurelleNode.getLockUserName()).thenReturn("user1");
        when(uniteStructurelleNode.getLockDate()).thenReturn(new Date());

        when(organigrammeService.lockOrganigrammeNode(Mockito.eq(session), Mockito.any())).thenReturn(false);

        // When
        uniteStrurelleUIService.getUniteStructurelleForm(context);

        // Then
        verify(messageQueue).addErrorToQueue(Mockito.contains("user1"));
    }

    @Test
    public void testCreateUniteStructurelle() {
        List<String> listIdParentMin = new ArrayList<>();
        listIdParentMin.add("idParentMin");
        HashMap<String, String> mapMinistere = new HashMap<>();
        mapMinistere.put("idParentMin", "label Parent min");
        List<String> listIdParentUS = new ArrayList<>();
        listIdParentUS.add("idParentUS");
        HashMap<String, String> mapUs = new HashMap<>();
        mapUs.put("idParentUS", "label Parent US");

        when(uniteStructurelleForm.getLibelle()).thenReturn("Label uniteStructurelle");
        when(uniteStructurelleForm.getType()).thenReturn("UST");
        when(uniteStructurelleForm.getDateDebut()).thenReturn("01/01/2000");
        when(uniteStructurelleForm.getDateFin()).thenReturn("01/01/2000");
        when(uniteStructurelleForm.getMinisteresRatachement()).thenReturn(listIdParentMin);
        when(uniteStructurelleForm.getMapMinisteresRatachement()).thenReturn(mapMinistere);
        when(uniteStructurelleForm.getUnitesStructurellesRattachement()).thenReturn(listIdParentUS);
        when(uniteStructurelleForm.getMapUnitesStructurellesRattachement()).thenReturn(mapUs);
        when(organigrammeService.checkUniqueLabel(any())).thenReturn(true);
        when(context.getFromContextData(UNITE_STRUCTURELLE_FORM)).thenReturn(uniteStructurelleForm);
        uniteStrurelleUIService.createUniteStructurelle(context);

        verify(usAndDirectionService).createUniteStructurelle(any(UniteStructurelleNode.class));
    }

    @Test
    public void testUpdateUniteStructurelle() {
        List<String> listIdParentMin = new ArrayList<>();
        listIdParentMin.add("idParentMin");
        HashMap<String, String> mapMinistere = new HashMap<>();
        mapMinistere.put("idParentMin", "label Parent min");
        List<String> listIdParentUS = new ArrayList<>();
        listIdParentUS.add("idParentUS");
        HashMap<String, String> mapUs = new HashMap<>();
        mapUs.put("idParentUS", "label Parent US");
        List<EntiteNode> parentEntiteList = new ArrayList<>();
        parentEntiteList.add(new EntiteNodeImpl());
        List<UniteStructurelleNode> parentUSList = new ArrayList<>();
        parentUSList.add(new UniteStructurelleNodeImpl());

        when(uniteStructurelleForm.getLibelle()).thenReturn("Label uniteStructurelle");
        when(uniteStructurelleForm.getType()).thenReturn("UNITE_STRUCTURELLE");
        when(uniteStructurelleForm.getDateDebut()).thenReturn("01/01/2000");
        when(uniteStructurelleForm.getDateFin()).thenReturn("01/01/2000");
        when(uniteStructurelleForm.getMinisteresRatachement()).thenReturn(listIdParentMin);
        when(uniteStructurelleForm.getMapMinisteresRatachement()).thenReturn(mapMinistere);
        when(uniteStructurelleForm.getUnitesStructurellesRattachement()).thenReturn(listIdParentUS);
        when(uniteStructurelleForm.getMapUnitesStructurellesRattachement()).thenReturn(mapUs);
        when(organigrammeService.checkUniqueLabel(any())).thenReturn(true);
        when(uniteStructurelleNode.getEntiteParentList()).thenReturn(parentEntiteList);
        when(uniteStructurelleNode.getUniteStructurelleParentList()).thenReturn(parentUSList);
        when(context.getFromContextData(ID)).thenReturn(ID_US);
        when(context.getFromContextData(UNITE_STRUCTURELLE_FORM)).thenReturn(uniteStructurelleForm);

        uniteStrurelleUIService.updateUniteStructurelle(context);

        verify(uniteStrurelleUIService)
            .performNodeUpdate(
                eq(context),
                eq(uniteStructurelleNode),
                updaterCaptor.capture(),
                eq("organigramme.succes.update.uniteStructurelle")
            );

        updaterCaptor.getValue().accept(uniteStructurelleNode);
        verify(usAndDirectionService).updateUniteStructurelle(uniteStructurelleNode);
    }

    @Test
    public void testUpdateUniteStructurelleNode() {
        UniteStructurelleNodeImpl uniteStructurelleNode = new UniteStructurelleNodeImpl();
        Calendar calendar = new GregorianCalendar(2000, Calendar.JANUARY, 1);
        Date date = calendar.getTime();

        List<String> listIdParentMin = new ArrayList<>();
        listIdParentMin.add("idParentMin");
        HashMap<String, String> mapMinistere = new HashMap<>();
        mapMinistere.put("idParentMin", "label Parent min");
        List<String> listIdParentUS = new ArrayList<>();
        listIdParentUS.add("idParentUS");
        HashMap<String, String> mapUs = new HashMap<>();
        mapUs.put("idParentUS", "label Parent US");

        when(uniteStructurelleForm.getLibelle()).thenReturn("Label uniteStructurelle");
        when(uniteStructurelleForm.getType()).thenReturn("UNITE_STRUCTURELLE");
        when(uniteStructurelleForm.getDateDebut()).thenReturn("01/01/2000");
        when(uniteStructurelleForm.getDateFin()).thenReturn("01/01/2000");
        when(uniteStructurelleForm.getMinisteresRatachement()).thenReturn(listIdParentMin);
        when(uniteStructurelleForm.getMapMinisteresRatachement()).thenReturn(mapMinistere);
        when(uniteStructurelleForm.getUnitesStructurellesRattachement()).thenReturn(listIdParentUS);
        when(uniteStructurelleForm.getMapUnitesStructurellesRattachement()).thenReturn(mapUs);

        when(organigrammeService.getOrganigrammeNodeById("idParentUS", OrganigrammeType.UNITE_STRUCTURELLE))
            .thenReturn(uniteStructurelleNodeParent);
        when(organigrammeService.getOrganigrammeNodeById("idParentMin", OrganigrammeType.MINISTERE))
            .thenReturn(entiteNodeParent);

        UniteStructurelleNode uniteStructurelleNodeRes = STUniteStructurelleUIServiceImpl.updateUniteStructurelleNode(
            uniteStructurelleNode,
            uniteStructurelleForm
        );

        assertNotNull(uniteStructurelleNodeRes);
        assertEquals("Label uniteStructurelle", uniteStructurelleNodeRes.getLabel());
        assertEquals(OrganigrammeType.UNITE_STRUCTURELLE, uniteStructurelleNodeRes.getType());
        assertEquals(date, uniteStructurelleNodeRes.getDateDebut());
        assertEquals(date, uniteStructurelleNodeRes.getDateFin());
        assertNotNull(uniteStructurelleNodeRes.getEntiteParentList());
        assertEquals("idParentMin", uniteStructurelleNodeRes.getParentEntiteId());
        assertNotNull(uniteStructurelleNodeRes.getUniteStructurelleParentList());
        assertEquals("idParentUS", uniteStructurelleNodeRes.getParentUniteId());
    }
}
