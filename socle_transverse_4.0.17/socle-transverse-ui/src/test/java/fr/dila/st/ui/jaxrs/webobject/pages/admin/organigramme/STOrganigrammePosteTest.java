package fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.STOrganigramme;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.services.STOrganigrammeManagerService;
import fr.dila.st.ui.services.STPosteUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.PosteForm;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.URLUtils;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
public class STOrganigrammePosteTest {
    private String ID = "id";
    private String ID_PARENT = "idParent";
    private String ID_US = "idUS";
    private String LABEL_US = "labelUS";

    private static final String BASE_URL = "http://test.fr";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    STOrganigrammePoste controller = new STOrganigrammePoste();

    @Mock
    SpecificContext context;

    @Mock
    STPosteUIService posteUIService;

    @Mock
    STOrganigramme organigramme;

    @Mock
    OrganigrammeNode usNode;

    @Mock
    PosteForm posteForm;

    @Mock
    WebContext webContext;

    @Mock
    NuxeoPrincipal principal;

    @Mock
    CoreSession session;

    @Mock
    UserSession userSession;

    @Mock
    OrganigrammeTreeUIService organigrammeTreeService;

    @Mock
    STOrganigrammeManagerService organigrammeManager;

    @Mock
    OrganigrammeService organigrammeService;

    @Mock
    OrganigrammeNode poste;

    @Mock
    PosteNode posteNode;

    @Mock
    UniteStructurelleNode USNode;

    @Mock
    OrganigrammeNode uniteStructurelle;

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(STUIServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(UserSessionHelper.class);
        PowerMockito.mockStatic(STServiceLocator.class);

        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getPrincipal()).thenReturn(principal);
        when(webContext.getCoreSession()).thenReturn(session);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(context.getWebcontext()).thenReturn(webContext);
        when(principal.isMemberOf("EspaceAdministrationReader")).thenReturn(true);

        when(STUIServiceLocator.getSTPosteUIService()).thenReturn(posteUIService);
        when(STUIServiceLocator.getOrganigrammeTreeService()).thenReturn(organigrammeTreeService);
        when(STUIServiceLocator.getSTOrganigrammeManagerService()).thenReturn(organigrammeManager);
        when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);
        mockStatic(URLUtils.class);
        PowerMockito.doAnswer(invocation -> BASE_URL + invocation.getArgumentAt(0, String.class)).when(URLUtils.class);
        URLUtils.generateRedirectPath(anyString(), any(HttpServletRequest.class));

        Whitebox.setInternalState(controller, "context", context);
    }

    @Test
    public void testDTO() throws Exception {
        String id = "identifiant";
        String libelle = "libelle";
        String dateDebut = "24/07/2020";

        PosteForm form = new PosteForm();
        assertNotNull(form);
        form.setId(id);
        form.setLibelle(libelle);
        form.setDateDebut(dateDebut);
        HashMap<String, String> uniteStructurelleRattachement = new HashMap<>();
        uniteStructurelleRattachement.put("1", "us1");
        uniteStructurelleRattachement.put("2", "us2");
        form.setMapUnitesStructurellesRattachement(uniteStructurelleRattachement);
        assertEquals(form.getId(), id);
        assertEquals(form.getLibelle(), libelle);
        assertEquals(form.getDateDebut(), dateDebut);
        assertFalse(form.getMapUnitesStructurellesRattachement().isEmpty());
        assertEquals(form.getMapUnitesStructurellesRattachement().size(), 2);
        assertEquals(form.getMapUnitesStructurellesRattachement().get("1"), "us1");
        assertEquals(form.getMapUnitesStructurellesRattachement().get("2"), "us2");
    }

    @Test
    public void testGetPosteCreation() {
        HashMap<String, String> emptyMap = new HashMap<>();
        Whitebox.setInternalState(controller, "context", context);

        when(context.getAction(STActionEnum.CREATE_POSTE)).thenReturn(new Action());
        when(organigrammeTreeService.findNodeHavingIdAndChildType(ID_PARENT, OrganigrammeType.POSTE))
            .thenReturn(usNode);
        when(usNode.getId()).thenReturn(ID_US);
        when(usNode.getLabel()).thenReturn(LABEL_US);

        ThTemplate template = controller.getPosteCreation(ID_PARENT, null);

        assertNotNull(template);
        assertNotNull(template.getData());
        assertNotNull(template.getData().get(STOrganigrammePoste.POSTE_FORM));
        PosteForm posteFormRes = (PosteForm) template.getData().get(STOrganigrammePoste.POSTE_FORM);
        assertNotNull(posteFormRes.getUnitesStructurellesRattachement());
        assertNotEquals(emptyMap, posteFormRes.getMapUnitesStructurellesRattachement());
        assertEquals(template.getName(), "pages/organigramme/editPoste");
        assertEquals(template.getContext(), context);
    }

    @Test
    public void testGetPosteModification() {
        when(posteUIService.getPosteForm(any())).thenReturn(posteForm);
        when(context.getAction(STActionEnum.MODIFY_POSTE)).thenReturn(new Action());
        when(organigrammeService.getOrganigrammeNodeById(ID, OrganigrammeType.POSTE)).thenReturn(posteNode);
        List<UniteStructurelleNode> listUS = Collections.singletonList(USNode);
        when(posteNode.getUniteStructurelleParentList()).thenReturn(listUS);
        when(USNode.getId()).thenReturn("usId");
        when(organigrammeService.getOrganigrammeNodeById("usId", OrganigrammeType.UNITE_STRUCTURELLE))
            .thenReturn(uniteStructurelle);

        when(posteNode.getId()).thenReturn("idPoste");
        when(posteNode.getLabelWithNor(null)).thenReturn("label");
        when(posteNode.getType()).thenReturn(OrganigrammeType.POSTE);
        when(posteNode.isActive()).thenReturn(true);
        when(posteNode.getLockDate()).thenReturn(new Date());
        when(posteNode.getLockUserName()).thenReturn("userName");

        when(uniteStructurelle.getId()).thenReturn("idPoste");
        when(uniteStructurelle.getLabelWithNor(null)).thenReturn("label");
        when(uniteStructurelle.getType()).thenReturn(OrganigrammeType.POSTE);
        when(uniteStructurelle.isActive()).thenReturn(true);
        when(uniteStructurelle.getLockDate()).thenReturn(new Date());
        when(uniteStructurelle.getLockUserName()).thenReturn("userName");

        ThTemplate template = controller.getPosteModification(ID, null);

        assertNotNull(template);
        assertNotNull(template.getData());
        assertEquals(posteForm, template.getData().get(STOrganigrammePoste.POSTE_FORM));
        assertEquals(template.getName(), "pages/organigramme/editPoste");
        assertEquals(template.getContext(), context);
    }

    @Test
    public void testCreatePosteSuccess() {
        when(posteForm.getId()).thenReturn("");
        when(context.getAction(STActionEnum.CREATE_POSTE)).thenReturn(new Action());

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addSuccessToQueue("success");
        when(context.getMessageQueue()).thenReturn(alertManager);

        when(posteUIService.getPosteForm(any())).thenReturn(posteForm);

        controller.saveOrUpdatePoste(posteForm, null);

        verify(posteUIService).createPoste(any(SpecificContext.class));

        PowerMockito.verifyStatic(times(1));
        UserSessionHelper.putUserSessionParameter(
            any(SpecificContext.class),
            anyString(),
            any(SolonAlertManager.class)
        );
    }

    @Test
    public void testCreatePosteError() {
        when(posteForm.getId()).thenReturn("");
        when(context.getAction(STActionEnum.CREATE_POSTE)).thenReturn(new Action());

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addErrorToQueue("error");
        when(context.getMessageQueue()).thenReturn(alertManager);
        controller.saveOrUpdatePoste(posteForm, null);

        verify(posteUIService).createPoste(any(SpecificContext.class));
    }

    @Test
    public void testUpdatePosteSuccess() {
        when(posteForm.getId()).thenReturn("id");

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addSuccessToQueue("success");
        Mockito.when(context.getMessageQueue()).thenReturn(alertManager);
        when(posteUIService.getPosteForm(any())).thenReturn(posteForm);

        controller.saveOrUpdatePoste(posteForm, null);

        verify(posteUIService).updatePoste(any(SpecificContext.class));
        PowerMockito.verifyStatic();

        UserSessionHelper.putUserSessionParameter(
            any(SpecificContext.class),
            anyString(),
            any(SolonAlertManager.class)
        );
    }

    @Test
    public void testUpdatePosteError() {
        when(posteForm.getId()).thenReturn("id");
        when(context.getAction(STActionEnum.MODIFY_POSTE)).thenReturn(new Action());
        when(organigrammeService.getOrganigrammeNodeById(ID, OrganigrammeType.POSTE)).thenReturn(posteNode);
        List<UniteStructurelleNode> listUS = Collections.singletonList(USNode);
        when(posteNode.getUniteStructurelleParentList()).thenReturn(listUS);
        when(USNode.getId()).thenReturn("usId");
        when(organigrammeService.getOrganigrammeNodeById("usId", OrganigrammeType.UNITE_STRUCTURELLE))
            .thenReturn(uniteStructurelle);

        when(posteNode.getId()).thenReturn("idPoste");
        when(posteNode.getLabelWithNor(null)).thenReturn("label");
        when(posteNode.getType()).thenReturn(OrganigrammeType.POSTE);
        when(posteNode.isActive()).thenReturn(true);
        when(posteNode.getLockDate()).thenReturn(new Date());
        when(posteNode.getLockUserName()).thenReturn("userName");

        when(uniteStructurelle.getId()).thenReturn("idUS");
        when(uniteStructurelle.getLabelWithNor(null)).thenReturn("label");
        when(uniteStructurelle.getType()).thenReturn(OrganigrammeType.POSTE);
        when(uniteStructurelle.isActive()).thenReturn(true);
        when(uniteStructurelle.getLockDate()).thenReturn(new Date());
        when(uniteStructurelle.getLockUserName()).thenReturn("userName");

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addErrorToQueue("error");
        Mockito.when(context.getMessageQueue()).thenReturn(alertManager);
        controller.saveOrUpdatePoste(posteForm, null);

        verify(posteUIService).updatePoste(any(SpecificContext.class));
    }
}
