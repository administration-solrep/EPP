package fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.STOrganigramme;
import fr.dila.st.ui.services.STGouvernementUIService;
import fr.dila.st.ui.services.STOrganigrammeManagerService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.GouvernementForm;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.URLUtils;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ WebEngine.class, STServiceLocator.class, STUIServiceLocator.class, URLUtils.class })
@PowerMockIgnore("javax.management.*")
public class STOrganigrammeGouvernementTest {
    private static final int REDIRECT_STATUS = 303;
    private static final String BASE_URL = "http://test.fr";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private STOrganigrammeGouvernement controller;

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private CoreSession session;

    @Mock
    private UserSession userSession;

    @Mock
    private STGouvernementUIService gouvernementUIService;

    @Mock
    private STOrganigrammeManagerService organigrammeManager;

    @Mock
    private OrganigrammeService organigrammeService;

    @Before
    public void before() throws Exception {
        controller = new STOrganigrammeGouvernement();

        Whitebox.setInternalState(controller, "context", context);

        mockStatic(WebEngine.class);
        mockStatic(STUIServiceLocator.class);
        mockStatic(STServiceLocator.class);

        PowerMockito.whenNew(SpecificContext.class).withNoArguments().thenReturn(context);
        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getPrincipal()).thenReturn(principal);
        when(webContext.getCoreSession()).thenReturn(session);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(context.getWebcontext()).thenReturn(webContext);
        when(principal.isMemberOf("EspaceAdministrationReader")).thenReturn(true);
        when(STUIServiceLocator.getSTGouvernementUIService()).thenReturn(gouvernementUIService);
        when(STUIServiceLocator.getSTOrganigrammeManagerService()).thenReturn(organigrammeManager);
        when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);

        mockStatic(URLUtils.class);
        PowerMockito.doAnswer(invocation -> BASE_URL + invocation.getArgumentAt(0, String.class)).when(URLUtils.class);
        URLUtils.generateRedirectPath(anyString(), any(HttpServletRequest.class));
    }

    @Test
    public void testGetGouvernementCreation() {
        when(context.getAction(STActionEnum.CREATE_GOUVERNEMENT)).thenReturn(new Action());
        ThTemplate template = controller.getGouvernementCreation();

        assertNotNull(template);
        assertNotNull(template.getData());
        assertNotNull(template.getData().get("gouvernementForm"));
        GouvernementForm createGvForm = (GouvernementForm) template.getData().get("gouvernementForm");
        assertNotNull(createGvForm);
        assertEquals("pages/organigramme/editGouvernement", template.getName());
        assertEquals(context, template.getContext());
    }

    @Test
    public void testSaveOrUpateGouvernementCreation() {
        GouvernementForm gvForm = new GouvernementForm();

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addSuccessToQueue("success");
        when(context.getMessageQueue()).thenReturn(alertManager);

        Response response = (Response) controller.saveOrUpdateGouvernement(gvForm);

        assertThat(response.getStatus()).isEqualTo(REDIRECT_STATUS);
        assertThat(getResponseUrl(response)).isEqualTo(BASE_URL + STOrganigramme.ORGANIGRAMME_URL);

        verify(gouvernementUIService).createGouvernement(context);
        verify(context).putInContextData(STContextDataKey.GVT_FORM, gvForm);
    }

    @Test
    public void testSaveOrUpateGouvernementUpdate() {
        GouvernementForm gvForm = new GouvernementForm();
        gvForm.setId("id");

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addSuccessToQueue("success");
        when(context.getMessageQueue()).thenReturn(alertManager);

        Response response = (Response) controller.saveOrUpdateGouvernement(gvForm);

        assertThat(response.getStatus()).isEqualTo(REDIRECT_STATUS);
        assertThat(getResponseUrl(response)).isEqualTo(BASE_URL + STOrganigramme.ORGANIGRAMME_URL);

        verify(gouvernementUIService).updateGouvernement(context);
        verify(context).putInContextData(STContextDataKey.GVT_FORM, gvForm);
    }

    @Test
    public void testGetGouvernementModification() {
        when(context.getAction(STActionEnum.MODIFY_GOUVERNEMENT)).thenReturn(new Action());
        doCallRealMethod().when(context).putInContextData(STContextDataKey.ID, "id");
        ThTemplate template = controller.getGouvernementModification("id");

        verify(gouvernementUIService).getGouvernementForm(context);

        assertNotNull(template);
        assertNotNull(template.getData());
        assertEquals(1, template.getData().size());
        assertEquals("pages/organigramme/editGouvernement", template.getName());
    }

    private String getResponseUrl(Response response) {
        return response.getMetadata().get("Location").get(0).toString();
    }
}
