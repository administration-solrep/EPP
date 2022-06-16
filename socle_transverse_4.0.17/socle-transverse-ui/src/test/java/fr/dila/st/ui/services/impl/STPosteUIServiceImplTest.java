package fr.dila.st.ui.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.bean.PosteForm;
import fr.dila.st.ui.th.bean.PosteWsForm;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STServiceLocator.class, Framework.class })
@PowerMockIgnore("javax.management.*")
public class STPosteUIServiceImplTest {
    private static final String ID_POSTE = "idPoste";
    private static final String LABEL_POSTE = "label poste";
    private static final String ID_PARENT = "idParent";
    private static final String LABEL_PARENT = "label parent";

    STPosteUIServiceImpl posteUIService;

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    OrganigrammeService organigrammeService;

    @Mock
    PosteNode posteNode;

    PosteForm posteForm = new PosteForm();

    @Mock
    STPostesService postesService;

    @Mock
    UniteStructurelleNode uniteStructurelleNodeParent;

    @Mock
    STMinisteresService ministeresService;

    @Mock
    private CoreSession session;

    @Mock
    private SolonAlertManager messageQueue;

    @Captor
    private ArgumentCaptor<Consumer<PosteNode>> updaterCaptor;

    @Mock
    private SpecificContext context;

    @Captor
    private ArgumentCaptor<PosteNode> posteNodeCaptor;

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(STServiceLocator.class, Framework.class);

        // Crypto passphrase and salt
        when(Framework.getProperty("solon.ws.passphrase")).thenReturn("test passphrase");
        when(Framework.getProperty("solon.ws.salt")).thenReturn("test salt");

        when(context.getMessageQueue()).thenReturn(messageQueue);
        when(context.getSession()).thenReturn(session);

        NuxeoPrincipal principal = mock(NuxeoPrincipal.class);
        when(principal.getName()).thenReturn("user");
        when(session.getPrincipal()).thenReturn(principal);

        when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);

        when(STServiceLocator.getSTPostesService()).thenReturn(postesService);
        when(organigrammeService.getOrganigrammeNodeById(ID_POSTE, OrganigrammeType.POSTE)).thenReturn(posteNode);
        when(context.getFromContextData(STContextDataKey.POSTE_WS_FORM)).thenReturn(getForm());

        posteUIService = spy(new STPosteUIServiceImpl());
        List<String> listIdParentUS = new ArrayList<>();
        listIdParentUS.add(ID_PARENT);
        HashMap<String, String> mapUs = new HashMap<>();
        mapUs.put(ID_PARENT, LABEL_PARENT);

        posteForm.setId(ID_POSTE);
        posteForm.setLibelle(LABEL_POSTE);
        posteForm.setSuperviseur("false");
        posteForm.setChargeMission("false");
        posteForm.setPosteBDC("true");
        posteForm.setDateDebut("01/01/2000");
        posteForm.setDateFin("01/01/2000");
        posteForm.getUnitesStructurellesRattachement().addAll(listIdParentUS);
        posteForm.setMapUnitesStructurellesRattachement(mapUs);
    }

    @Test
    public void testGetPosteForm() {
        Calendar calendar = new GregorianCalendar(2000, Calendar.JANUARY, 1);
        Date date = calendar.getTime();
        when(context.getFromContextData(STContextDataKey.ID)).thenReturn(ID_POSTE);

        when(posteNode.getId()).thenReturn(ID_POSTE);
        when(posteNode.getLabel()).thenReturn(LABEL_POSTE);
        when(posteNode.isSuperviseurSGG()).thenReturn(false);
        when(posteNode.isChargeMissionSGG()).thenReturn(false);
        when(posteNode.isPosteBdc()).thenReturn(true);
        when(posteNode.getDateDebut()).thenReturn(date);
        when(posteNode.getDateFin()).thenReturn(date);

        UniteStructurelleNodeImpl usParent = new UniteStructurelleNodeImpl();
        usParent.setId(ID_PARENT);
        usParent.setLabel(LABEL_PARENT);
        List<UniteStructurelleNode> UniteStructurelleNodeParent = new ArrayList<>();
        UniteStructurelleNodeParent.add(usParent);
        when(posteNode.getUniteStructurelleParentList()).thenReturn(UniteStructurelleNodeParent);

        when(organigrammeService.lockOrganigrammeNode(Mockito.eq(session), Mockito.any())).thenReturn(true);

        PosteForm posteForm = posteUIService.getPosteForm(context);

        assertNotNull(posteForm);
        assertEquals(ID_POSTE, posteForm.getId());
        assertEquals(LABEL_POSTE, posteForm.getLibelle());
        assertEquals("01/01/2000", posteForm.getDateDebut());
        assertEquals("01/01/2000", posteForm.getDateFin());
        assertNotNull(posteForm.getMapUnitesStructurellesRattachement());
        HashMap<String, String> mapUS = new HashMap<>();
        mapUS.put(ID_PARENT, LABEL_PARENT);
        assertEquals(mapUS, posteForm.getMapUnitesStructurellesRattachement());
        List<String> listIdParent = new ArrayList<>();
        listIdParent.add(ID_PARENT);
        assertEquals(listIdParent, posteForm.getUnitesStructurellesRattachement());
    }

    @Test
    public void testGetPosteFormLockedByOtherUser() {
        // Given
        when(context.getFromContextData(STContextDataKey.ID)).thenReturn(ID_POSTE);

        when(posteNode.getDateDebut()).thenReturn(new Date());
        when(posteNode.getDateFin()).thenReturn(new Date());
        when(posteNode.getLockUserName()).thenReturn("user1");
        when(posteNode.getLockDate()).thenReturn(new Date());

        when(organigrammeService.lockOrganigrammeNode(Mockito.eq(session), Mockito.any())).thenReturn(false);

        // When
        posteUIService.getPosteForm(context);

        // Then
        verify(messageQueue).addErrorToQueue(Mockito.contains("user1"));
    }

    @Test
    public void testCreatePoste() {
        when(context.getFromContextData(STContextDataKey.POSTE_FORM)).thenReturn(posteForm);

        when(organigrammeService.checkUniqueLabel(any())).thenReturn(true);

        posteUIService.createPoste(context);

        verify(postesService, times(1)).createPoste(any(CoreSession.class), any(PosteNode.class));
    }

    @Test
    public void testUpdatePoste() {
        when(context.getFromContextData(STContextDataKey.POSTE_FORM)).thenReturn(posteForm);

        when(organigrammeService.checkUniqueLabel(any())).thenReturn(true);

        posteUIService.updatePoste(context);

        verify(posteUIService)
            .performNodeUpdate(
                eq(context),
                eq(posteNode),
                updaterCaptor.capture(),
                eq("organigramme.success.update.poste")
            );

        updaterCaptor.getValue().accept(posteNode);
        verify(postesService).updatePoste(session, posteNode);
    }

    @Test
    public void testUpdatePosteNode() throws ParseException {
        PosteNodeImpl posteNode = new PosteNodeImpl();
        Calendar calendar = new GregorianCalendar(2000, Calendar.JANUARY, 1);
        Date date = calendar.getTime();

        when(organigrammeService.getOrganigrammeNodeById(ID_PARENT, OrganigrammeType.UNITE_STRUCTURELLE))
            .thenReturn(uniteStructurelleNodeParent);

        PosteNode posteNodeRes = STPosteUIServiceImpl.updatePosteNode(posteNode, posteForm);

        assertNotNull(posteNodeRes);
        assertEquals(LABEL_POSTE, posteNodeRes.getLabel());
        assertFalse(posteNodeRes.isSuperviseurSGG());
        assertFalse(posteNodeRes.isChargeMissionSGG());
        assertTrue(posteNodeRes.isPosteBdc());
        assertEquals(date, posteNodeRes.getDateDebut());
        assertEquals(date, posteNodeRes.getDateFin());
        assertNotNull(posteNodeRes.getUniteStructurelleParentList());
        assertEquals(ID_PARENT, posteNodeRes.getParentUniteId());
    }

    @Test
    public void testCreatePostWs() {
        when(context.getFromContextData(STContextDataKey.ID)).thenReturn("5000");

        when(organigrammeService.checkUniqueLabel(any())).thenReturn(true);
        posteUIService.createPosteWs(context);

        verify(postesService).createPoste(eq(session), posteNodeCaptor.capture());

        PosteNode createdPosteNode = posteNodeCaptor.getValue();

        assertThat(createdPosteNode.getLabel()).isEqualTo("label");
        assertThat(createdPosteNode.getWsUser()).isEqualTo("user");
        assertThat(createdPosteNode.getWsUrl()).isEqualTo("url");
        assertThat(createdPosteNode.getWsPassword()).isEqualTo("mdp");
    }

    @Test
    public void testUpdatePostWs() {
        posteForm.setId("5000");
        when(context.getFromContextData(STContextDataKey.POSTE_WS_FORM)).thenReturn(getForm());

        PosteNode posteNode = new PosteNodeImpl();
        posteNode.setId("5000");
        posteNode.setLockUserName("user");

        when(organigrammeService.checkUniqueLabel(any())).thenReturn(true);
        when(organigrammeService.getOrganigrammeNodeById("5000", OrganigrammeType.POSTE)).thenReturn(posteNode);

        posteUIService.updatePosteWs(context);
        verify(postesService).updatePoste(session, posteNode);
    }

    @Test
    public void testGetPosteWsForm() {
        when(context.getFromContextData(STContextDataKey.ID)).thenReturn("5000");
        // Given
        PosteNode posteNode = new PosteNodeImpl();
        posteNode.setLockUserName("user");
        when(organigrammeService.getOrganigrammeNodeById("5000", OrganigrammeType.POSTE)).thenReturn(posteNode);

        when(organigrammeService.lockOrganigrammeNode(context.getSession(), posteNode)).thenReturn(true);

        // When
        posteUIService.getPosteWsForm(context);

        // Then
        verify(organigrammeService).lockOrganigrammeNode(context.getSession(), posteNode);
        verify(context.getMessageQueue()).addSuccessToQueue("organigramme.success.node.locked");
    }

    @Test
    public void shouldNotUpdatePostWs() {
        posteForm.setId("5000");
        when(context.getFromContextData(STContextDataKey.POSTE_WS_FORM)).thenReturn(getForm());
        // same label
        when(organigrammeService.checkUniqueLabel(Mockito.any())).thenReturn(Boolean.FALSE);
        PosteNode posteNode = new PosteNodeImpl();
        when(organigrammeService.getOrganigrammeNodeById("5000", OrganigrammeType.POSTE)).thenReturn(posteNode);

        posteUIService.updatePosteWs(context);
        verify(postesService, Mockito.never()).updatePoste(session, posteNode);
    }

    private PosteWsForm getForm() {
        PosteWsForm form = new PosteWsForm();
        form.setId("5000");
        form.setLibelle("label");
        form.setUrlWs("url");
        form.setUtilisateurWs("user");
        form.setMdpWs("mdp");
        form.setKeystore("ks");
        form.setDateDebut("01/01/1980");
        form.setMinisteres(new ArrayList<>(Arrays.asList("100", "101")));
        return form;
    }
}
