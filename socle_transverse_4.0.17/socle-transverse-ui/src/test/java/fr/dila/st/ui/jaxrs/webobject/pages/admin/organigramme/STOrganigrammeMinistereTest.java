package fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.STOrganigramme;
import fr.dila.st.ui.services.STMinistereUIService;
import fr.dila.st.ui.services.STOrganigrammeManagerService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.EntiteForm;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.STAdminTemplate;
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
import org.mockito.Mockito;
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
@PrepareForTest(
    { STUIServiceLocator.class, STServiceLocator.class, WebEngine.class, UserSessionHelper.class, URLUtils.class }
)
@PowerMockIgnore("javax.management.*")
public class STOrganigrammeMinistereTest {
    private static final int REDIRECT_STATUS = 303;
    private static final String BASE_URL = "http://test.fr";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    STOrganigrammeMinistere ministereController = new STOrganigrammeMinistere();

    @Mock
    SpecificContext context;

    @Mock
    STMinistereUIService ministereUIService;

    @Mock
    OrganigrammeService organigrammeService;

    @Mock
    OrganigrammeElementDTO dtoGouvernement;

    @Mock
    EntiteForm entiteForm;

    @Mock
    OrganigrammeNode gouvernementNode;

    @Mock
    WebContext webContext;

    @Mock
    NuxeoPrincipal principal;

    @Mock
    CoreSession session;

    @Mock
    UserSession userSession;

    @Mock
    SolonWebObject webObject;

    @Mock
    STOrganigrammeManagerService organigrammeManager;

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(STUIServiceLocator.class);
        PowerMockito.mockStatic(STServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(UserSessionHelper.class);

        Mockito.when(WebEngine.getActiveContext()).thenReturn(webContext);
        Mockito.when(webContext.getPrincipal()).thenReturn(principal);
        Mockito.when(webContext.getCoreSession()).thenReturn(session);
        Mockito.when(webContext.getUserSession()).thenReturn(userSession);
        Mockito.when(context.getWebcontext()).thenReturn(webContext);
        Mockito.when(principal.isMemberOf("EspaceAdministrationReader")).thenReturn(true);

        Mockito.when(STUIServiceLocator.getSTMinistereUIService()).thenReturn(ministereUIService);
        Mockito.when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);

        Mockito.when(STUIServiceLocator.getSTOrganigrammeManagerService()).thenReturn(organigrammeManager);

        mockStatic(URLUtils.class);
        PowerMockito.doAnswer(invocation -> BASE_URL + invocation.getArgumentAt(0, String.class)).when(URLUtils.class);
        URLUtils.generateRedirectPath(anyString(), any(HttpServletRequest.class));

        Whitebox.setInternalState(ministereController, "context", context);
    }

    @Test
    public void testGetMinistereCreation() {
        Mockito
            .when(organigrammeService.getOrganigrammeNodeById("string", OrganigrammeType.GOUVERNEMENT))
            .thenReturn(gouvernementNode);

        Mockito.when(gouvernementNode.getId()).thenReturn("idGouvernement");
        Mockito.when(gouvernementNode.getLabel()).thenReturn("LabelGouvernement");
        Mockito.when(gouvernementNode.getType()).thenReturn(OrganigrammeType.GOUVERNEMENT);
        Mockito.when(gouvernementNode.isActive()).thenReturn(true);
        when(context.getAction(STActionEnum.CREATE_ENTITE)).thenReturn(new Action());

        ThTemplate template = ministereController.getMinistereCreation("string");

        assertNotNull(template);
        assertNotNull(template.getData());
        assertNotNull(template.getData().get(STOrganigrammeMinistere.ENTITE_FORM));
        EntiteForm entiteFormRes = (EntiteForm) template.getData().get(STOrganigrammeMinistere.ENTITE_FORM);
        assertNotNull(entiteFormRes.getGouvernement());
        assertEquals(template.getName(), "pages/organigramme/editMinistere");
        assertEquals(template.getContext(), context);
    }

    @Test
    public void testGetMinistereModification() {
        Mockito.when(ministereUIService.getEntiteForm(Mockito.any())).thenReturn(entiteForm);

        Mockito
            .when(organigrammeService.getOrganigrammeNodeById("string", OrganigrammeType.MINISTERE))
            .thenReturn(gouvernementNode);
        Mockito.when(gouvernementNode.getId()).thenReturn("string");
        Mockito.when(gouvernementNode.getType()).thenReturn(OrganigrammeType.MINISTERE);

        when(context.getAction(STActionEnum.MODIFY_ENTITE)).thenReturn(new Action());

        ThTemplate template = ministereController.getMinistereModification("string");

        assertNotNull(template);
        assertNotNull(template.getData());
        assertEquals(entiteForm, template.getData().get(STOrganigrammeMinistere.ENTITE_FORM));
        assertEquals(template.getName(), "pages/organigramme/editMinistere");
        assertEquals(template.getContext(), context);
    }

    @Test
    public void testCreateMinistereSucess() {
        Mockito.when(entiteForm.getIdentifiant()).thenReturn("");
        when(context.getAction(STActionEnum.CREATE_ENTITE)).thenReturn(new Action());

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addSuccessToQueue("success");
        Mockito.when(context.getMessageQueue()).thenReturn(alertManager);

        Response response = (Response) ministereController.saveOrUpdateMinistere(entiteForm);

        assertThat(response.getStatus()).isEqualTo(REDIRECT_STATUS);
        assertThat(getResponseUrl(response)).isEqualTo(BASE_URL + STOrganigramme.ORGANIGRAMME_URL);

        Mockito.verify(ministereUIService).createEntite(Mockito.any(SpecificContext.class));
        PowerMockito.verifyStatic();

        UserSessionHelper.putUserSessionParameter(
            Mockito.any(SpecificContext.class),
            Mockito.anyString(),
            Mockito.any(SolonAlertManager.class)
        );
    }

    @Test
    public void testCreateMinistereError() {
        Mockito.when(entiteForm.getIdentifiant()).thenReturn("");
        when(context.getAction(STActionEnum.CREATE_ENTITE)).thenReturn(new Action());

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addErrorToQueue("error");
        Mockito.when(context.getMessageQueue()).thenReturn(alertManager);

        STAdminTemplate response = (STAdminTemplate) ministereController.saveOrUpdateMinistere(entiteForm);

        assertThat(response.getName()).isEqualTo("pages/organigramme/editMinistere");

        Mockito.verify(ministereUIService).createEntite(Mockito.any(SpecificContext.class));
    }

    @Test
    public void testUpdateMinistereSuccess() {
        Mockito.when(entiteForm.getIdentifiant()).thenReturn("id");

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addSuccessToQueue("success");
        Mockito.when(context.getMessageQueue()).thenReturn(alertManager);

        Response response = (Response) ministereController.saveOrUpdateMinistere(entiteForm);

        assertThat(response.getStatus()).isEqualTo(REDIRECT_STATUS);
        assertThat(getResponseUrl(response)).isEqualTo(BASE_URL + STOrganigramme.ORGANIGRAMME_URL);

        Mockito.verify(ministereUIService).updateEntite(Mockito.any(SpecificContext.class));
        PowerMockito.verifyStatic();

        UserSessionHelper.putUserSessionParameter(
            Mockito.any(SpecificContext.class),
            Mockito.anyString(),
            Mockito.any(SolonAlertManager.class)
        );
    }

    @Test
    public void testUpdateMinistereError() {
        Mockito.when(entiteForm.getIdentifiant()).thenReturn("id");

        Mockito
            .when(organigrammeService.getOrganigrammeNodeById("id", OrganigrammeType.MINISTERE))
            .thenReturn(gouvernementNode);
        Mockito.when(gouvernementNode.getId()).thenReturn("id");
        Mockito.when(gouvernementNode.getType()).thenReturn(OrganigrammeType.MINISTERE);

        when(context.getAction(STActionEnum.MODIFY_ENTITE)).thenReturn(new Action());

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addErrorToQueue("error");
        Mockito.when(context.getMessageQueue()).thenReturn(alertManager);

        Mockito.when(ministereUIService.getEntiteForm(Mockito.any())).thenReturn(entiteForm);

        STAdminTemplate response = (STAdminTemplate) ministereController.saveOrUpdateMinistere(entiteForm);

        assertThat(response.getName()).isEqualTo("pages/organigramme/editMinistere");

        Mockito.verify(ministereUIService).updateEntite(Mockito.any(SpecificContext.class));
    }

    private String getResponseUrl(Response response) {
        return response.getMetadata().get("Location").get(0).toString();
    }
}
