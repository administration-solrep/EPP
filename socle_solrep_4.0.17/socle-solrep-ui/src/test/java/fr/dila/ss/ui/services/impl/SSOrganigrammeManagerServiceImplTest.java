package fr.dila.ss.ui.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Sets;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.services.impl.OrganigrammeTreeUIServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
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
public class SSOrganigrammeManagerServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    SSOrganigrammeManagerServiceImpl service = new SSOrganigrammeManagerServiceImpl();

    @Mock
    OrganigrammeService organigrammeService;

    @Mock
    UserManager userManager;

    @Mock
    STMinisteresService ministereService;

    @Mock
    CoreSession session;

    @Mock
    SSPrincipal ssPrincipal;

    @Mock
    WebContext webContext;

    @Mock
    UserSession userSession;

    @Before
    public void setUp() {
        organigrammeService = Mockito.mock(OrganigrammeService.class);
        userManager = Mockito.mock(UserManager.class);
        ministereService = Mockito.mock(STMinisteresService.class);
        session = Mockito.mock(CoreSession.class);
        ssPrincipal = Mockito.mock(SSPrincipal.class);
        webContext = Mockito.mock(WebContext.class);
        userSession = Mockito.mock(UserSession.class);

        Mockito.when(session.getPrincipal()).thenReturn(ssPrincipal);
        PowerMockito.mockStatic(STServiceLocator.class);
        Mockito.when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);
        Mockito.when(STServiceLocator.getUserManager()).thenReturn(userManager);
        Mockito.when(STServiceLocator.getSTMinisteresService()).thenReturn(ministereService);

        PowerMockito.mockStatic(WebEngine.class);
        Mockito.when(WebEngine.getActiveContext()).thenReturn(webContext);
        Mockito.when(webContext.getUserSession()).thenReturn(userSession);
    }

    @Test
    public void testGetSuggestions() {
        DocumentModel userDoc = Mockito.mock(DocumentModel.class);
        STUser user = Mockito.mock(STUser.class);
        Mockito.when(userDoc.getId()).thenReturn("user");
        Mockito.when(user.isDeleted()).thenReturn(false);
        Mockito.when(user.isActive()).thenReturn(true);
        Mockito.when(user.getFirstName()).thenReturn("User");
        Mockito.when(user.getLastName()).thenReturn("1");
        Mockito.when(user.getFullName()).thenReturn("User 1");
        Mockito.when(user.getUsername()).thenReturn("username");
        Mockito.when(user.getFullNameWithUsername()).thenReturn("User 1 (username)");
        Mockito.when(userDoc.getAdapter(STUser.class)).thenReturn(user);
        DocumentModelList userDocs = new DocumentModelListImpl();
        userDocs.add(userDoc);
        Mockito.when(userManager.searchUsers("use")).thenReturn(userDocs);

        SpecificContext context = new SpecificContext();
        context.putInContextData(SSOrganigrammeManagerServiceImpl.INPUT_KEY, "use");
        List<String> typesSelection = new ArrayList<>();
        typesSelection.add(OrganigrammeType.USER.getValue());
        context.putInContextData(SSOrganigrammeManagerServiceImpl.TYPE_SELECTION_KEY, typesSelection);
        context.putInContextData(OrganigrammeTreeUIServiceImpl.ACTIVATE_POSTE_FILTER_KEY, false);
        List<SuggestionDTO> suggestions = service.getSuggestions(context);
        assertFalse(suggestions.isEmpty());
        assertEquals("username", suggestions.get(0).getKey());
        assertEquals("User 1 (username)", suggestions.get(0).getLabel());

        PosteNode poste = new PosteNodeImpl();
        poste.setId("poste");
        poste.setLabel("Poste 1");
        poste.setDateDebut(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        poste.setDateFin(cal);
        List<OrganigrammeNode> organigrammeNodes = new ArrayList<>();
        organigrammeNodes.add(poste);
        List<OrganigrammeType> organigrammeTypes = new ArrayList<>();
        organigrammeTypes.add(OrganigrammeType.POSTE);
        typesSelection = new ArrayList<>();
        typesSelection.add(OrganigrammeType.POSTE.getValue());
        Mockito
            .when(organigrammeService.getOrganigrameLikeLabels("Pos", organigrammeTypes))
            .thenReturn(organigrammeNodes);

        context = new SpecificContext();
        context.putInContextData(SSOrganigrammeManagerServiceImpl.INPUT_KEY, "Pos");
        context.putInContextData(SSOrganigrammeManagerServiceImpl.TYPE_SELECTION_KEY, typesSelection);
        context.putInContextData(OrganigrammeTreeUIServiceImpl.ACTIVATE_POSTE_FILTER_KEY, false);
        suggestions = service.getSuggestions(context);
        assertFalse(suggestions.isEmpty());
        assertEquals("poste", suggestions.get(0).getKey());
        assertEquals("Poste 1", suggestions.get(0).getLabel());

        EntiteNode entite = new EntiteNodeImpl();
        entite.setId("entite");
        List<EntiteNode> entiteNodes = new ArrayList<>();
        entiteNodes.add(entite);
        Mockito.when(ministereService.getMinistereParentFromPoste("poste")).thenReturn(entiteNodes);

        Set<String> minSet = new HashSet<>();
        Mockito.when(ssPrincipal.getMinistereIdSet()).thenReturn(minSet);

        context = new SpecificContext();
        context.setSession(session);
        context.putInContextData(SSOrganigrammeManagerServiceImpl.INPUT_KEY, "Pos");
        context.putInContextData(SSOrganigrammeManagerServiceImpl.TYPE_SELECTION_KEY, typesSelection);
        context.putInContextData(OrganigrammeTreeUIServiceImpl.ACTIVATE_POSTE_FILTER_KEY, true);
        suggestions = service.getSuggestions(context);
        assertTrue(suggestions.isEmpty());

        minSet.add("entite");
        Mockito.when(ssPrincipal.getMinistereIdSet()).thenReturn(minSet);

        context = new SpecificContext();
        context.setSession(session);
        context.putInContextData(SSOrganigrammeManagerServiceImpl.INPUT_KEY, "Pos");
        context.putInContextData(SSOrganigrammeManagerServiceImpl.TYPE_SELECTION_KEY, typesSelection);
        context.putInContextData(OrganigrammeTreeUIServiceImpl.ACTIVATE_POSTE_FILTER_KEY, true);
        suggestions = service.getSuggestions(context);
        assertFalse(suggestions.isEmpty());
        assertEquals("poste", suggestions.get(0).getKey());
        assertEquals("Poste 1", suggestions.get(0).getLabel());

        Mockito.when(ssPrincipal.isMemberOf(STBaseFunctionConstant.ALLOW_ADD_POSTE_ALL_MINISTERE)).thenReturn(true);
        context = new SpecificContext();
        context.setSession(session);
        context.putInContextData(SSOrganigrammeManagerServiceImpl.INPUT_KEY, "Pos");
        context.putInContextData(SSOrganigrammeManagerServiceImpl.TYPE_SELECTION_KEY, typesSelection);
        context.putInContextData(OrganigrammeTreeUIServiceImpl.ACTIVATE_POSTE_FILTER_KEY, true);
        suggestions = service.getSuggestions(context);
        assertFalse(suggestions.isEmpty());
        assertEquals("poste", suggestions.get(0).getKey());
        assertEquals("Poste 1", suggestions.get(0).getLabel());
    }

    @Test
    public void testAllowUpdateOrganigramme_asAdminFonctionnel() {
        Mockito
            .when(ssPrincipal.isMemberOf(Mockito.eq(STBaseFunctionConstant.ORGANIGRAMME_UPDATER)))
            .thenReturn(Boolean.TRUE);
        Mockito.when(ssPrincipal.getMinistereIdSet()).thenReturn(new HashSet<>());

        assertTrue(service.isAllowUpdateOrganigramme(session, null));
        assertTrue(service.isAllowUpdateOrganigramme(session, "1"));
    }

    @Test
    public void testAllowUpdateOrganigramme_asAdminMinisteriel() {
        Mockito
            .when(ssPrincipal.isMemberOf(Mockito.eq(STBaseFunctionConstant.ORGANIGRAMME_UPDATER)))
            .thenReturn(Boolean.FALSE);
        Mockito
            .when(ssPrincipal.isMemberOf(Mockito.eq(STBaseFunctionConstant.ORGANIGRAMME_MINISTERE_UPDATER)))
            .thenReturn(Boolean.TRUE);
        Mockito.when(ssPrincipal.getMinistereIdSet()).thenReturn(Sets.newHashSet("1", "2"));

        assertFalse(service.isAllowUpdateOrganigramme(session, null));
        assertTrue(service.isAllowUpdateOrganigramme(session, "1"));
        assertTrue(service.isAllowUpdateOrganigramme(session, "2"));
        assertFalse(service.isAllowUpdateOrganigramme(session, "3"));
    }

    @Test
    public void testAllowUpdateOrganigramme_asLambdaUser() {
        Mockito
            .when(ssPrincipal.isMemberOf(Mockito.eq(STBaseFunctionConstant.ORGANIGRAMME_UPDATER)))
            .thenReturn(Boolean.FALSE);
        Mockito
            .when(ssPrincipal.isMemberOf(Mockito.eq(STBaseFunctionConstant.ORGANIGRAMME_MINISTERE_UPDATER)))
            .thenReturn(Boolean.FALSE);
        Mockito.when(ssPrincipal.getMinistereIdSet()).thenReturn(Sets.newHashSet("1", "2"));

        assertFalse(service.isAllowUpdateOrganigramme(session, null));
        assertFalse(service.isAllowUpdateOrganigramme(session, "1"));
        assertFalse(service.isAllowUpdateOrganigramme(session, "2"));
        assertFalse(service.isAllowUpdateOrganigramme(session, "3"));
    }
}
