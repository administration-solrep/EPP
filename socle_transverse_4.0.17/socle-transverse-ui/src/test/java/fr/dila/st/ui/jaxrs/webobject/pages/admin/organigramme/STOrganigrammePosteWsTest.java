package fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme;

import static fr.dila.st.ui.enums.STContextDataKey.POSTE_WS_FORM;
import static fr.dila.st.ui.th.model.SpecificContext.MESSAGE_QUEUE;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.AlertContainer;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.STOrganigramme;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.services.STOrganigrammeManagerService;
import fr.dila.st.ui.services.STPosteUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.PosteWsForm;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.STAdminTemplate;
import fr.dila.st.ui.th.model.STUtilisateurTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.URLUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        STUIServiceLocator.class,
        STServiceLocator.class,
        STOrganigrammePosteWsTest.class,
        UserSessionHelper.class,
        URLUtils.class,
        WebEngine.class
    }
)
@PowerMockIgnore("javax.management.*")
public class STOrganigrammePosteWsTest {
    private static final String ID = "id";
    private static final String ID_PARENT = "idParent";

    private static final String BASE_URL = "http://test.fr";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    STOrganigrammePosteWs controller = new STOrganigrammePosteWs();

    @Mock
    SpecificContext context;

    @Mock
    STPosteUIService posteUIService;

    @Mock
    OrganigrammeService organigrammeService;

    @Mock
    OrganigrammeTreeUIService organigrammeTreeService;

    @Mock
    STOrganigramme organigramme;

    @Mock
    SolonAlertManager alertManager;

    @Mock
    OrganigrammeNode usNode;

    @Mock
    WebContext webContext;

    @Mock
    NuxeoPrincipal principal;

    @Mock
    private OrganigrammeNode ministere;

    @Mock
    private STPosteUIService stPosteUIService;

    @Mock
    STOrganigrammeManagerService organigrammeManager;

    @Mock
    OrganigrammeNode poste;

    @Mock
    PosteNode posteNode;

    @Mock
    EntiteNode minNode;

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(STUIServiceLocator.class);
        PowerMockito.mockStatic(STServiceLocator.class);

        PowerMockito.mockStatic(UserSessionHelper.class);
        PowerMockito.mockStatic(WebEngine.class);
        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getPrincipal()).thenReturn(principal);

        when(principal.isMemberOf(Mockito.anyString())).thenReturn(true);

        when(STUIServiceLocator.getSTPosteUIService()).thenReturn(stPosteUIService);
        when(STUIServiceLocator.getOrganigrammeTreeService()).thenReturn(organigrammeTreeService);
        when(STUIServiceLocator.getSTOrganigrammeManagerService()).thenReturn(organigrammeManager);

        Breadcrumb breadcrumb = new Breadcrumb("/admin/organigramme/poste", null, Breadcrumb.TITLE_ORDER);
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(breadcrumb);
        when(context.getNavigationContext()).thenReturn(breadcrumbs);
        when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);
        when(organigrammeService.getOrganigrammeNodeById(ID_PARENT, OrganigrammeType.MINISTERE)).thenReturn(ministere);
        when(ministere.getId()).thenReturn("6000002");
        when(ministere.getLabel()).thenReturn("ministere");
        //PowerMockito.whenNew(ReponsesOrganigramme.class).withNoArguments().thenReturn(organigramme);
        when(context.getMessageQueue()).thenReturn(alertManager);
        Whitebox.setInternalState(controller, "context", context);

        mockStatic(URLUtils.class);
        PowerMockito.doAnswer(invocation -> BASE_URL + invocation.getArgumentAt(0, String.class)).when(URLUtils.class);
        URLUtils.generateRedirectPath(anyString(), any(HttpServletRequest.class));
    }

    @Test
    public void testGetMyTemplate() throws Exception {
        when(principal.isMemberOf(Mockito.anyString())).thenReturn(true);
        when(context.getAction(STActionEnum.CREATE_POSTE_WS)).thenReturn(new Action());

        ThTemplate template = controller.getPosteWsCreation(null);
        assertNotNull(template);
        assertTrue(template instanceof STAdminTemplate);

        when(principal.isMemberOf(Mockito.anyString())).thenReturn(false);
        template = controller.getPosteWsCreation(null);
        assertNotNull(template);
        assertTrue(template instanceof STUtilisateurTemplate);
    }

    @Test
    public void testGetPosteWsCreation() {
        when(context.getAction(STActionEnum.CREATE_POSTE_WS)).thenReturn(new Action());

        when(ministere.getId()).thenReturn("idMin");
        when(ministere.getLabelWithNor(null)).thenReturn("label");
        when(ministere.getType()).thenReturn(OrganigrammeType.POSTE);
        when(ministere.isActive()).thenReturn(true);
        when(ministere.getLockDate()).thenReturn(new Date());
        when(ministere.getLockUserName()).thenReturn("userName");

        ThTemplate template = controller.getPosteWsCreation(ID_PARENT);

        assertNotNull(template);
        assertTrue(template instanceof STAdminTemplate);

        assertEquals("pages/organigramme/editPosteWs", template.getName());
        assertNotNull(template.getData());
        assertEquals(1, template.getData().size());
        assertNotNull(template.getData().get("posteWsForm"));
        assertEquals(context, template.getContext());
        verify(context, times(1)).setNavigationContextTitle(any(Breadcrumb.class));
    }

    @Test
    public void testGetPosteWsModification() {
        PosteWsForm myForm = getPosteWsForm();

        when(stPosteUIService.getPosteWsForm(context)).thenReturn(myForm);
        when(context.getAction(STActionEnum.MODIFY_POSTE_WS)).thenReturn(new Action());
        when(organigrammeService.getOrganigrammeNodeById(ID, OrganigrammeType.POSTE)).thenReturn(posteNode);
        List<EntiteNode> listUS = Collections.singletonList(minNode);
        when(posteNode.getEntiteParentList()).thenReturn(listUS);
        when(minNode.getId()).thenReturn("minId");
        when(minNode.getType()).thenReturn(OrganigrammeType.MINISTERE);
        when(organigrammeService.getOrganigrammeNodeById("minId", OrganigrammeType.MINISTERE)).thenReturn(ministere);

        when(posteNode.getId()).thenReturn("idPoste");
        when(posteNode.getLabelWithNor(null)).thenReturn("label");
        when(posteNode.getType()).thenReturn(OrganigrammeType.POSTE);
        when(posteNode.isActive()).thenReturn(true);
        when(posteNode.getLockDate()).thenReturn(new Date());
        when(posteNode.getLockUserName()).thenReturn("userName");
        when(posteNode.getFirstEntiteParent()).thenReturn(minNode);

        when(ministere.getId()).thenReturn("idPoste");
        when(ministere.getLabelWithNor(null)).thenReturn("label");
        when(ministere.getType()).thenReturn(OrganigrammeType.POSTE);
        when(ministere.isActive()).thenReturn(true);
        when(ministere.getLockDate()).thenReturn(new Date());
        when(ministere.getLockUserName()).thenReturn("userName");

        ThTemplate template = controller.getPosteWsModification(ID);

        assertNotNull(template);
        assertTrue(template instanceof STAdminTemplate);
        assertEquals("pages/organigramme/editPosteWs", template.getName());
        assertNotNull(template.getData());
        assertEquals(1, template.getData().size());
        assertNotNull(template.getData().get("posteWsForm"));
        assertEquals("monMockId", ((PosteWsForm) template.getData().get("posteWsForm")).getId());
        assertEquals(context, template.getContext());

        verify(context).setNavigationContextTitle(any(Breadcrumb.class));
    }

    @Test
    public void testCreatePosteWs() {
        // Given
        PosteWsForm myForm = getPosteWsForm();
        myForm.setId(null);
        when(stPosteUIService.getPosteWsForm(context)).thenReturn(myForm);
        when(alertManager.getSuccessQueue()).thenReturn(singletonList(mock(AlertContainer.class)));
        when(context.getAction(STActionEnum.CREATE_POSTE_WS)).thenReturn(new Action());
        // When
        Response response = (Response) controller.createOrUpdatePosteWs(myForm);

        // Then
        assertThat(response.getStatus()).isEqualTo(303);
        assertThat(getResponseUrl(response)).isEqualTo(BASE_URL + STOrganigramme.ORGANIGRAMME_URL);
        verify(context).putInContextData(POSTE_WS_FORM, myForm);
        verify(context, never()).putInContextData(STContextDataKey.ID, "monMockId");
        verify(stPosteUIService, never()).updatePosteWs(context);
        verify(stPosteUIService).createPosteWs(context);
        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, MESSAGE_QUEUE, context.getMessageQueue());
    }

    @Test
    public void testUpdatePosteWs() {
        // Given
        PosteWsForm myForm = getPosteWsForm();
        when(stPosteUIService.getPosteWsForm(context)).thenReturn(myForm);
        Whitebox.setInternalState(controller, "context", context);

        // When
        Object response = controller.createOrUpdatePosteWs(myForm);

        // Then
        assertThat(response).isNull();
        verify(context).putInContextData(POSTE_WS_FORM, myForm);
        verify(stPosteUIService).updatePosteWs(context);
        verify(stPosteUIService, never()).createPosteWs(context);
    }

    private PosteWsForm getPosteWsForm() {
        PosteWsForm myForm = new PosteWsForm();
        myForm.setId("monMockId");
        return myForm;
    }

    private String getResponseUrl(Response response) {
        return response.getMetadata().get("Location").get(0).toString();
    }
}
