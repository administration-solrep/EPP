package fr.dila.st.ui.services.impl;

import static fr.dila.st.ui.enums.STContextDataKey.USERS_LIST_FORM;
import static fr.dila.st.ui.enums.STContextDataKey.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.STUserManagerUIService;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.bean.UsersListForm;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STServiceLocator.class, STUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class STUtilisateurUIServiceImplTest {
    private static final String USERNAME = "adminsgg";
    private static final String FIRST_NAME = "admin";
    private static final String LAST_NAME = "SGG";
    private static final String EMAIL = "adminsgg@gouv.fr";
    private static final String POSTE = "60000";
    private static final String GROUP = "Administrateur";
    private static final String POSTE_LABEL = "Poste";

    @Spy
    private STUtilisateursUIServiceImpl service = new STUtilisateursUIServiceImpl();

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private SpecificContext context;

    @Mock
    private UserManager userManager;

    @Mock
    private STUserManagerUIService userManagerActionService;

    @Mock
    private OrganigrammeService orgaService;

    @Mock
    private DocumentModelList docList;

    @Mock
    private Iterator<DocumentModel> iterator;

    @Mock
    private DocumentModel mockDoc;

    @Mock
    private STUser mockUser;

    @Mock
    private CoreSession session;

    @Mock
    private NuxeoPrincipal principal;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(STServiceLocator.class);
        PowerMockito.mockStatic(STUIServiceLocator.class);

        when(context.getContextData()).thenReturn(new HashMap<>());
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
        when(STServiceLocator.getUserManager()).thenReturn(userManager);
        when(STUIServiceLocator.getSTUserManagerUIService()).thenReturn(userManagerActionService);
        when(mockDoc.getAdapter(STUser.class)).thenReturn(mockUser);
        when(mockUser.getDateDebut()).thenReturn(Calendar.getInstance());
        when(mockUser.getFirstName()).thenReturn(FIRST_NAME);
        when(mockUser.getLastName()).thenReturn(LAST_NAME);
        when(mockUser.getPostes()).thenReturn(Lists.newArrayList(POSTE));
        when(mockUser.getGroups()).thenReturn(Lists.newArrayList(GROUP));
        when(mockUser.getEmail()).thenReturn(EMAIL);
        when(mockUser.getUsername()).thenReturn(USERNAME);
        when(context.getSession()).thenReturn(session);
        when(session.getPrincipal()).thenReturn(principal);

        when(service.getOrganigrammeService()).thenReturn(orgaService);

        PosteNodeImpl poste = new PosteNodeImpl();
        poste.setId(POSTE);
        poste.setLabel(POSTE_LABEL);
        when(orgaService.getOrganigrammeNodeById(POSTE, OrganigrammeType.POSTE)).thenReturn(poste);

        when(docList.iterator()).thenReturn(iterator);

        when(iterator.next()).thenReturn(mockDoc);
    }

    @Test
    public void test_getListeUtilisateursShouldBeEmpty() {
        when(context.getFromContextData(USERS_LIST_FORM)).thenReturn(new UsersListForm());
        when(iterator.hasNext()).thenReturn(true, false);
        doNothing().when(userManagerActionService).initUserContext(Mockito.any());

        STUsersList list = service.getListeUtilisateurs(context);
        assertNotNull(list);
        assertTrue(list.getListe().isEmpty());
        assertTrue(list.getLstLettres().isEmpty());

        when(userManager.searchUsers(Mockito.anyString())).thenReturn(docList);
        list = service.getListeUtilisateurs(context);
        assertNotNull(list);
        // Pas d'utilisateur sur page A donc on positionne directement sur S
        assertTrue(CollectionUtils.isNotEmpty(list.getListe()));
        assertTrue(list.getListe().get(0).getNom().startsWith("S"));
        assertEquals(Lists.newArrayList("S"), list.getLstLettres());
    }

    @Test
    public void test_getListeUtilisateurs() {
        UsersListForm form = new UsersListForm();
        form.setIndex("s");
        when(context.getFromContextData(USERS_LIST_FORM)).thenReturn(form);

        when(userManager.searchUsers(Mockito.anyString())).thenReturn(docList);

        when(iterator.hasNext()).thenReturn(true, true, false);
        doNothing().when(userManagerActionService).initUserContext(Mockito.any());

        STUsersList list = service.getListeUtilisateurs(context);
        assertNotNull(list);
        assertEquals(Lists.newArrayList("S"), list.getLstLettres());
        assertEquals(2, list.getListe().size());
        checkUserFilled(list.getListe().get(0), false);
    }

    @Test
    public void test_getUtilisateur() {
        UserForm user = service.getUtilisateur(context);
        assertNotNull(user);
        assertNull(user.getNom());
        assertNull(user.getPrenom());
        assertNull(user.getUtilisateur());
        assertNull(user.getMel());
        assertNull(user.getTemporaire());
        assertNull(user.getDateDebut());
        assertNull(user.getPostes());
        assertThat(user.getProfils()).isEmpty();

        when(userManager.getUserModel(USERNAME)).thenReturn(mockDoc);
        when(context.getFromContextData(USER_ID)).thenReturn(USERNAME);

        user = service.getUtilisateur(context);

        checkUserFilled(user, true);
    }

    private void checkUserFilled(UserForm user, boolean fullUser) {
        assertEquals(FIRST_NAME, user.getPrenom());
        assertEquals(LAST_NAME, user.getNom());
        assertEquals(USERNAME, user.getUtilisateur());
        assertEquals(EMAIL, user.getMel());
        if (fullUser) {
            assertEquals("non", user.getTemporaire());
            assertEquals(SolonDateConverter.DATE_SLASH.format(new Date()), user.getDateDebut());
            assertEquals(Lists.newArrayList(POSTE_LABEL), user.getPostes());
            assertEquals(Lists.newArrayList(GROUP), user.getProfils());
        }
    }
}
