package fr.dila.st.ui.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.organigramme.GouvernementNodeImpl;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.bean.TreeElementDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STServiceLocator.class, WebEngine.class })
@PowerMockIgnore("javax.management.*")
public class OrganigrammeTreeServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    OrganigrammeTreeUIServiceImpl service = new OrganigrammeTreeUIServiceImpl();

    @Mock
    OrganigrammeService organigrammeService;

    @Mock
    UserManager userManager;

    @Mock
    CoreSession session;

    @Mock
    WebContext webContext;

    @Mock
    UserSession userSession;

    @Mock
    STMinisteresService ministereService;

    @Before
    public void setUp() {
        organigrammeService = Mockito.mock(OrganigrammeService.class);
        userManager = Mockito.mock(UserManager.class);
        session = Mockito.mock(CoreSession.class);
        webContext = Mockito.mock(WebContext.class);
        userSession = Mockito.mock(UserSession.class);

        PowerMockito.mockStatic(STServiceLocator.class);
        Mockito.when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);
        Mockito.when(STServiceLocator.getUserManager()).thenReturn(userManager);
        Mockito.when(STServiceLocator.getSTMinisteresService()).thenReturn(ministereService);

        PowerMockito.mockStatic(WebEngine.class);
        Mockito.when(WebEngine.getActiveContext()).thenReturn(webContext);
        Mockito.when(webContext.getCoreSession()).thenReturn(session);
        Mockito.when(webContext.getUserSession()).thenReturn(userSession);
    }

    @Test
    public void testGetOrganigramme() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        List<OrganigrammeElementDTO> tree = service.getOrganigramme(new SpecificContext());
        assertNotNull(tree);
        assertTrue(tree.isEmpty());

        GouvernementNode gouvInactif = new GouvernementNodeImpl();
        gouvInactif.setId("gouvInactif");
        gouvInactif.setDateDebut(cal);
        gouvInactif.setDateFin(cal);
        assertEquals(false, gouvInactif.isActive());
        GouvernementNode gouvActif = new GouvernementNodeImpl();
        gouvActif.setId("gouvActif");
        gouvActif.setDateDebut(cal);
        assertEquals(true, gouvActif.isActive());
        List<GouvernementNode> rootNodes = new ArrayList<>();
        rootNodes.add(gouvInactif);
        rootNodes.add(gouvActif);
        Mockito.when(organigrammeService.getRootNodes()).thenAnswer(x -> rootNodes);

        Set<String> openNodes = new HashSet<>();
        SpecificContext context = new SpecificContext();
        context.putInContextData(STContextDataKey.SHOW_DEACTIVATED, false);
        context.putInContextData(OrganigrammeTreeUIServiceImpl.OPEN_NODES_ID_KEY, "test");
        Mockito.when(userSession.get("test")).thenReturn(openNodes);
        tree = service.getOrganigramme(context);
        assertNotNull(tree);
        assertEquals(1, tree.size());
        assertEquals("gouvActif", tree.get(0).getKey());
        assertEquals(false, tree.get(0).getIsOpen());

        openNodes.add("gouvActif");
        tree = service.getOrganigramme(new SpecificContext());
        assertNotNull(tree);
        assertEquals(1, tree.size());
        assertEquals("gouvActif", tree.get(0).getKey());
        assertEquals(true, tree.get(0).getIsOpen());

        context = new SpecificContext();
        context.putInContextData(STContextDataKey.SHOW_DEACTIVATED, true);
        context.putInContextData(OrganigrammeTreeUIServiceImpl.OPEN_NODES_ID_KEY, "test");
        tree = service.getOrganigramme(context);
        assertNotNull(tree);
        assertEquals(2, tree.size());
        assertEquals("gouvInactif", tree.get(0).getKey());
        assertEquals(false, tree.get(0).getIsOpen());
        assertEquals("gouvActif", tree.get(1).getKey());
        assertEquals(true, tree.get(1).getIsOpen());
    }

    @Test
    public void testAddSubGroups() {
        GouvernementNode gouv = new GouvernementNodeImpl();
        gouv.setId("gouv");
        OrganigrammeElementDTO treeNode = new OrganigrammeElementDTO(session, gouv);
        Set<String> openNodes = new HashSet<>();

        service.addSubGroups(session, treeNode, false, openNodes);
        assertTrue(treeNode.getChilds().isEmpty());
        assertTrue(treeNode.getIsLastLevel());

        EntiteNode entite = new EntiteNodeImpl();
        entite.setId("entite");
        List<OrganigrammeNode> childrenList = new ArrayList<>();
        childrenList.add(entite);
        Mockito.when(organigrammeService.getChildrenList(session, gouv, false)).thenReturn(childrenList);
        treeNode.setIsLastLevel(false);
        openNodes.add("gouv");

        service.addSubGroups(session, treeNode, false, openNodes);
        assertEquals(false, treeNode.getChilds().isEmpty());
        assertEquals(false, treeNode.getIsLastLevel());
        TreeElementDTO child = treeNode.getChilds().get(0);
        assertEquals(true, child.getChilds().isEmpty());
        assertEquals(true, child.getIsLastLevel());
    }

    @Test
    public void testAddUsers() {
        PosteNode poste = new PosteNodeImpl();
        poste.setId("poste");
        OrganigrammeElementDTO treeNode = new OrganigrammeElementDTO(session, poste);

        service.addUsers(treeNode, false);
        assertTrue(treeNode.getChilds().isEmpty());

        List<String> members = new ArrayList<>();
        members.add("user");
        poste.setMembers(members);
        DocumentModel userDoc = Mockito.mock(DocumentModel.class);
        STUser user = Mockito.mock(STUser.class);
        Mockito.when(user.isDeleted()).thenReturn(false);
        Mockito.when(user.isActive()).thenReturn(true);
        Mockito.when(user.getFirstName()).thenReturn("User");
        Mockito.when(user.getLastName()).thenReturn("1");
        Mockito.when(userDoc.getAdapter(STUser.class)).thenReturn(user);
        Mockito.when(userManager.getUserModel("user")).thenReturn(userDoc);
        Mockito.when(user.getFullName()).thenReturn("User 1");
        service.addUsers(treeNode, false);
        assertFalse(treeNode.getChilds().isEmpty());
        TreeElementDTO child = treeNode.getChilds().get(0);
        assertEquals("user", child.getKey());
        assertEquals("User 1", child.getLabel());
        assertTrue(child.getChilds().isEmpty());
        assertTrue(child.getIsLastLevel());

        Mockito.when(user.isActive()).thenReturn(false);
        service.addUsers(treeNode, false);
        assertTrue(treeNode.getChilds().isEmpty());

        service.addUsers(treeNode, true);
        assertFalse(treeNode.getChilds().isEmpty());
        child = treeNode.getChilds().get(0);
        assertEquals("user", child.getKey());
        assertEquals("User 1", child.getLabel());
        assertTrue(child.getChilds().isEmpty());
        assertTrue(child.getIsLastLevel());
    }

    @Test
    public void testCopyNode() {
        SpecificContext context = new SpecificContext();
        EntiteNode entiteNode = new EntiteNodeImpl();
        entiteNode.setId("0001");
        entiteNode.setLabel("Entite 1");
        Mockito
            .when(organigrammeService.getOrganigrammeNodeById("0001", OrganigrammeType.MINISTERE))
            .thenReturn(entiteNode);

        // Test sans paramètres
        service.copyNode(context, null, null);
        Mockito
            .verify(userSession, Mockito.times(0))
            .put(OrganigrammeTreeUIServiceImpl.SELECTED_NODE_FOR_COPY_KEY, entiteNode);
        assertEquals(1, context.getMessageQueue().getWarnQueue().get(0).getAlertMessage().size());
        assertEquals(
            "Erreur lors de la copie",
            context.getMessageQueue().getWarnQueue().get(0).getAlertMessage().get(0)
        );

        // Test avec identifiant qui n'existe pas
        service.copyNode(context, "0000", OrganigrammeType.MINISTERE.getValue());
        Mockito
            .verify(userSession, Mockito.times(0))
            .put(OrganigrammeTreeUIServiceImpl.SELECTED_NODE_FOR_COPY_KEY, entiteNode);
        assertEquals(2, context.getMessageQueue().getWarnQueue().get(0).getAlertMessage().size());
        assertEquals(
            "Erreur lors de la copie",
            context.getMessageQueue().getWarnQueue().get(0).getAlertMessage().get(1)
        );

        // Test avec identifiant valide
        service.copyNode(context, "0001", OrganigrammeType.MINISTERE.getValue());
        Mockito.verify(userSession).put(OrganigrammeTreeUIServiceImpl.SELECTED_NODE_FOR_COPY_KEY, entiteNode);
        assertEquals(2, context.getMessageQueue().getWarnQueue().get(0).getAlertMessage().size());
        assertEquals(1, context.getMessageQueue().getInfoQueue().get(0).getAlertMessage().size());
        assertEquals(
            String.format("Copie de \"%s\"", entiteNode.getLabel()),
            context.getMessageQueue().getInfoQueue().get(0).getAlertMessage().get(0)
        );
    }

    @Test
    public void testPasteNode() {
        SpecificContext context = new SpecificContext();
        EntiteNode entiteNode = new EntiteNodeImpl();
        entiteNode.setLabel("Entite 1");
        UniteStructurelleNode usNode = new UniteStructurelleNodeImpl();
        usNode.setLabel("Unité Structurelle 1");

        Mockito.when(ministereService.getEntiteNode("0001")).thenReturn(entiteNode);
        Mockito
            .doThrow(new NullPointerException())
            .when(organigrammeService)
            .copyNodeWithoutUser(
                Mockito.any(),
                Mockito.isNull(OrganigrammeNode.class),
                Mockito.isNull(OrganigrammeNode.class)
            );
        Mockito
            .doThrow(new NuxeoException("error.message"))
            .when(organigrammeService)
            .copyNodeWithoutUser(Mockito.any(), Mockito.isNull(OrganigrammeNode.class), Mockito.eq(entiteNode));

        // Test de pasteWithoutUser sans paramètres
        service.pasteNodeWithoutUser(context, null, null);
        assertEquals(1, context.getMessageQueue().getWarnQueue().get(0).getAlertMessage().size());
        assertEquals(
            "Erreur lors de la duplication",
            context.getMessageQueue().getWarnQueue().get(0).getAlertMessage().get(0)
        );
        Mockito.verify(userSession, Mockito.times(1)).remove(OrganigrammeTreeUIServiceImpl.SELECTED_NODE_FOR_COPY_KEY);

        // Test de pasteWithoutUser avec identifiant valide mais sans élément à copier
        service.pasteNodeWithoutUser(context, "0001", OrganigrammeType.MINISTERE.getValue());
        assertEquals(2, context.getMessageQueue().getWarnQueue().get(0).getAlertMessage().size());
        assertEquals(
            "Erreur lors de la duplication",
            context.getMessageQueue().getWarnQueue().get(0).getAlertMessage().get(1)
        );
        Mockito.verify(userSession, Mockito.times(2)).remove(OrganigrammeTreeUIServiceImpl.SELECTED_NODE_FOR_COPY_KEY);

        // Test de pasteWithoutUser avec identifiant valide et élément à copier
        Mockito.when(userSession.get(OrganigrammeTreeUIServiceImpl.SELECTED_NODE_FOR_COPY_KEY)).thenReturn(usNode);
        service.pasteNodeWithoutUser(context, "0001", OrganigrammeType.MINISTERE.getValue());
        assertEquals(2, context.getMessageQueue().getWarnQueue().get(0).getAlertMessage().size());
        assertEquals(1, context.getMessageQueue().getInfoQueue().get(0).getAlertMessage().size());
        assertEquals(
            String.format("Duplication, sans utilisateurs, de \"%s\"", usNode.getLabel()),
            context.getMessageQueue().getInfoQueue().get(0).getAlertMessage().get(0)
        );
        Mockito.verify(userSession, Mockito.times(3)).remove(OrganigrammeTreeUIServiceImpl.SELECTED_NODE_FOR_COPY_KEY);

        // Test de pasteWithUsers avec identifiant valide et élément à copier
        Mockito.when(userSession.get(OrganigrammeTreeUIServiceImpl.SELECTED_NODE_FOR_COPY_KEY)).thenReturn(usNode);
        service.pasteNodeWithUsers(context, "0001", OrganigrammeType.MINISTERE.getValue());
        assertEquals(2, context.getMessageQueue().getWarnQueue().get(0).getAlertMessage().size());
        assertEquals(2, context.getMessageQueue().getInfoQueue().get(0).getAlertMessage().size());
        assertEquals(
            String.format("Duplication, avec utilisateurs, de \"%s\"", usNode.getLabel()),
            context.getMessageQueue().getInfoQueue().get(0).getAlertMessage().get(1)
        );
        Mockito.verify(userSession, Mockito.times(4)).remove(OrganigrammeTreeUIServiceImpl.SELECTED_NODE_FOR_COPY_KEY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteNode_notFound() {
        Mockito.when(organigrammeService.getOrganigrammeNodeById(Mockito.anyString(), Mockito.any())).thenReturn(null);

        String nodeId = "123456";
        service.deleteNode(OrganigrammeType.POSTE, nodeId, new SpecificContext());
    }
}
