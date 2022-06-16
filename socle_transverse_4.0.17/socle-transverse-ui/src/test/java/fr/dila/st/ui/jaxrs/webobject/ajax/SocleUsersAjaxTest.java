package fr.dila.st.ui.jaxrs.webobject.ajax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.STUtilisateursUIService;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.bean.UsersListForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SocleUsersAjax.class, SpecificContext.class, STUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class SocleUsersAjaxTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SocleUsersAjax page;

    @Mock
    private WebContext webContext;

    @Mock
    private SpecificContext context;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private STUtilisateursUIService service;

    @Mock
    private HttpServletRequest request;

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(STUIServiceLocator.class);
        page = new SocleUsersAjax();
        Whitebox.setInternalState(page, "context", context);

        when(context.getWebcontext()).thenReturn(webContext);
        when(webContext.getPrincipal()).thenReturn(principal);
        when(webContext.getRequest()).thenReturn(request);
        when(principal.isMemberOf("EspaceAdministrationReader")).thenReturn(true);
        when(STUIServiceLocator.getSTUtilisateursUIService()).thenReturn(service);
    }

    private STUsersList buildMockUserList() {
        STUsersList usersDtoMock = new STUsersList();

        List<UserForm> listUsers = new ArrayList<>();
        listUsers.add(new UserForm("nom", "prenom", "username", "mel", "date"));
        usersDtoMock.setListe(listUsers);

        List<String> listLettres = new ArrayList<>();
        listLettres.add("a");
        usersDtoMock.setLstLettres(listLettres);
        return usersDtoMock;
    }

    @Test
    public void testUsers() {
        STUsersList usersDtoMock = buildMockUserList();

        when(service.getListeUtilisateurs(Mockito.any())).thenReturn(usersDtoMock);
        UsersListForm form = new UsersListForm();

        ThTemplate template = page.getUsers(form);
        assertNotNull(template);
        assertNotNull(template.getData());
        Map<String, Object> map = template.getData();

        assertNotNull(map);
        assertEquals("/admin/users/liste", map.get(STTemplateConstants.DATA_URL));
        assertEquals("/ajax/recherche/users/liste", map.get(STTemplateConstants.DATA_AJAX_URL));
        assertEquals("A", map.get(STTemplateConstants.INDEX));

        // test liste users
        assertNotNull(map.get(STTemplateConstants.RESULTAT_LIST));
        List<UserForm> usersInMap = (List<UserForm>) map.get(STTemplateConstants.RESULTAT_LIST);
        assertFalse(usersInMap.isEmpty());
        assertEquals(1, usersInMap.size());
        assertEquals(usersDtoMock.getListe().get(0), usersInMap.get(0));

        // test list lettres
        assertNotNull(map.get(STTemplateConstants.LST_LETTRES));
        List<String> lettresInMap = (List<String>) map.get(STTemplateConstants.LST_LETTRES);
        assertFalse(lettresInMap.isEmpty());
        assertEquals(1, lettresInMap.size());
        assertEquals(usersDtoMock.getLstLettres().get(0), lettresInMap.get(0));
    }
}
