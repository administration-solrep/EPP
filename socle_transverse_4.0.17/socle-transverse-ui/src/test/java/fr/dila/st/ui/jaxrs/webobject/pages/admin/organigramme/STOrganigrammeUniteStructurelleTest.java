package fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme;

import static fr.dila.st.ui.th.model.SpecificContext.MESSAGE_QUEUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.services.STOrganigrammeManagerService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.STUniteStructurelleUIService;
import fr.dila.st.ui.th.bean.UniteStructurelleForm;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.URLUtils;
import java.util.ArrayList;
import java.util.HashMap;
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
@PrepareForTest({ WebEngine.class, STUIServiceLocator.class, STServiceLocator.class, URLUtils.class })
@PowerMockIgnore("javax.management.*")
public class STOrganigrammeUniteStructurelleTest {
    private static final String US_FORM = "uniteStructurelleForm";
    private static final String BASE_URL = "http://test.fr";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private STOrganigrammeUniteStructurelle controller = new STOrganigrammeUniteStructurelle();

    @Mock
    private WebContext webContext;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private CoreSession session;

    @Mock
    private SpecificContext context;

    @Mock
    private UserSession userSession;

    @Mock
    private STUniteStructurelleUIService uniteStructurelleUIService;

    @Mock
    OrganigrammeTreeUIService organigrammeTreeService;

    @Mock
    private OrganigrammeNode organigrammeNodeMin;

    @Mock
    private OrganigrammeNode organigrammeNodeUS;

    @Mock
    private UniteStructurelleForm uniteStructurelleForm;

    @Mock
    STOrganigrammeManagerService organigrammeManager;

    @Mock
    OrganigrammeService organigrammeService;

    @Before
    public void before() {
        mockStatic(STUIServiceLocator.class);
        mockStatic(WebEngine.class);
        PowerMockito.mockStatic(STServiceLocator.class);

        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getPrincipal()).thenReturn(principal);
        when(webContext.getCoreSession()).thenReturn(session);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(context.getWebcontext()).thenReturn(webContext);
        when(principal.isMemberOf(Mockito.anyString())).thenReturn(true);

        when(STUIServiceLocator.getSTUniteStructurelleUIService()).thenReturn(uniteStructurelleUIService);
        when(STUIServiceLocator.getOrganigrammeTreeService()).thenReturn(organigrammeTreeService);
        when(STUIServiceLocator.getSTOrganigrammeManagerService()).thenReturn(organigrammeManager);
        when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);

        Whitebox.setInternalState(controller, "context", context);

        mockStatic(URLUtils.class);
        PowerMockito.doAnswer(invocation -> BASE_URL + invocation.getArgumentAt(0, String.class)).when(URLUtils.class);
        URLUtils.generateRedirectPath(anyString(), any(HttpServletRequest.class));
    }

    @Test
    public void testDTO() {
        String identifiant = "identifiant";
        String libelle = "libelle";
        String dateDebut = "24/07/2020";
        String ministere1 = "ministere1";
        String ministere2 = "ministere2";
        String us1 = "us1";
        String us2 = "us2";

        UniteStructurelleForm form = initDefaultUniteStructurelleForm();
        assertNotNull(form);
        assertEquals(form.getIdentifiant(), identifiant);
        assertEquals(form.getLibelle(), libelle);
        assertEquals(form.getDateDebut(), dateDebut);
        assertFalse(form.getMinisteresRatachement().isEmpty());
        assertEquals(form.getMinisteresRatachement().size(), 2);
        assertEquals(form.getMinisteresRatachement().get(0), ministere1);
        assertEquals(form.getMinisteresRatachement().get(1), ministere2);
        assertFalse(form.getUnitesStructurellesRattachement().isEmpty());
        assertEquals(form.getUnitesStructurellesRattachement().size(), 2);
        assertEquals(form.getUnitesStructurellesRattachement().get(0), us1);
        assertEquals(form.getUnitesStructurellesRattachement().get(1), us2);
    }

    private UniteStructurelleForm initDefaultUniteStructurelleForm() {
        String identifiant = "identifiant";
        String libelle = "libelle";
        String dateDebut = "24/07/2020";
        String ministere1 = "ministere1";
        String ministere2 = "ministere2";
        String us1 = "us1";
        String us2 = "us2";

        UniteStructurelleForm form = new UniteStructurelleForm();
        assertNotNull(form);
        form.setIdentifiant(identifiant);
        form.setLibelle(libelle);
        form.setDateDebut(dateDebut);
        ArrayList<String> ministeresRattachement = new ArrayList<>();
        ministeresRattachement.add(ministere1);
        ministeresRattachement.add(ministere2);
        form.setMinisteresRatachement(ministeresRattachement);
        ArrayList<String> uniteStructurelleRattachement = new ArrayList<>();
        uniteStructurelleRattachement.add(us1);
        uniteStructurelleRattachement.add(us2);
        form.setUnitesStructurellesRattachement(uniteStructurelleRattachement);

        return form;
    }

    @Test
    public void testGetUniteStructurelleCreation() {
        when(organigrammeTreeService.findNodeHavingIdAndChildType("idMin", OrganigrammeType.UNITE_STRUCTURELLE))
            .thenReturn(organigrammeNodeMin);
        when(organigrammeTreeService.findNodeHavingIdAndChildType("idUS", OrganigrammeType.UNITE_STRUCTURELLE))
            .thenReturn(organigrammeNodeUS);
        when(context.getAction(STActionEnum.CREATE_UNITE_STRUCTURELLE)).thenReturn(new Action());

        when(organigrammeNodeMin.getId()).thenReturn("idMin");
        when(organigrammeNodeMin.getLabel()).thenReturn("labelMin");
        when(organigrammeNodeMin.getType()).thenReturn(OrganigrammeType.MINISTERE);
        when(organigrammeNodeUS.getId()).thenReturn("idUS");
        when(organigrammeNodeUS.getLabel()).thenReturn("labelUS");
        when(organigrammeNodeUS.getType()).thenReturn(OrganigrammeType.UNITE_STRUCTURELLE);

        // Parent Ministère
        ThTemplate template = controller.getUniteStructurelleCreation("idMin", null);

        assertNotNull(template);
        assertNotNull(template.getData());
        assertNotNull(template.getData().get(US_FORM));
        UniteStructurelleForm usFormRes = (UniteStructurelleForm) template.getData().get(US_FORM);
        assertEquals("labelMin", usFormRes.getMapMinisteresRatachement().get("idMin"));
        assertTrue(usFormRes.getMapUnitesStructurellesRattachement().isEmpty());
        assertEquals(template.getName(), "pages/organigramme/editUniteStructurelle");
        assertEquals(template.getContext(), context);

        // Parent US
        template = controller.getUniteStructurelleCreation("idUS", null);

        assertNotNull(template);
        assertNotNull(template.getData());
        assertNotNull(template.getData().get(US_FORM));
        usFormRes = (UniteStructurelleForm) template.getData().get(US_FORM);
        assertTrue(usFormRes.getMapMinisteresRatachement().isEmpty());
        assertEquals("labelUS", usFormRes.getMapUnitesStructurellesRattachement().get("idUS"));
        assertEquals(template.getName(), "pages/organigramme/editUniteStructurelle");
        assertEquals(template.getContext(), context);

        // Parent multiple - un ministere et une Unite structurelle
        template = controller.getUniteStructurelleCreation("idUS;idMin", null);

        assertNotNull(template);
        assertNotNull(template.getData());
        assertNotNull(template.getData().get(US_FORM));
        usFormRes = (UniteStructurelleForm) template.getData().get(US_FORM);
        assertEquals("labelMin", usFormRes.getMapMinisteresRatachement().get("idMin"));
        assertEquals("labelUS", usFormRes.getMapUnitesStructurellesRattachement().get("idUS"));
        assertEquals(template.getName(), "pages/organigramme/editUniteStructurelle");
        assertEquals(template.getContext(), context);
    }

    @Test
    public void testGetUniteStructurelleModification() {
        when(uniteStructurelleUIService.getUniteStructurelleForm(Mockito.any())).thenReturn(uniteStructurelleForm);
        when(context.getAction(STActionEnum.MODIFY_UNITE_STRUCTURELLE)).thenReturn(new Action());

        ThTemplate template = controller.getUniteStructurelleModification("id", null);

        assertNotNull(template);
        assertNotNull(template.getData());
        assertEquals(uniteStructurelleForm, template.getData().get(US_FORM));
        assertEquals(template.getName(), "pages/organigramme/editUniteStructurelle");
        assertEquals(template.getContext(), context);
    }

    @Test
    public void testCreateUniteStructurelleSucess() {
        when(uniteStructurelleForm.getIdentifiant()).thenReturn("");
        when(context.getAction(STActionEnum.CREATE_UNITE_STRUCTURELLE)).thenReturn(new Action());

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addSuccessToQueue("success");
        when(context.getMessageQueue()).thenReturn(alertManager);
        controller.saveOrUpdateUniteStructurelle(uniteStructurelleForm, null);

        verify(uniteStructurelleUIService).createUniteStructurelle(context);
        verify(userSession).put(MESSAGE_QUEUE, alertManager);
    }

    @Test
    public void testCreateUniteStructurelleError() {
        when(uniteStructurelleForm.getIdentifiant()).thenReturn("");
        when(context.getAction(STActionEnum.CREATE_UNITE_STRUCTURELLE)).thenReturn(new Action());

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addErrorToQueue("error");
        when(context.getMessageQueue()).thenReturn(alertManager);

        controller.saveOrUpdateUniteStructurelle(uniteStructurelleForm, null);

        verify(uniteStructurelleUIService).createUniteStructurelle(context);
    }

    @Test
    public void testUpdateUniteStructurelleSuccess() {
        when(uniteStructurelleForm.getIdentifiant()).thenReturn("id");

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addSuccessToQueue("success");
        when(context.getMessageQueue()).thenReturn(alertManager);

        controller.saveOrUpdateUniteStructurelle(uniteStructurelleForm, null);

        verify(uniteStructurelleUIService).updateUniteStructurelle(context);
        verify(userSession).put(MESSAGE_QUEUE, alertManager);
    }

    @Test
    public void testUpdateUniteStructurelleError() {
        when(uniteStructurelleUIService.getUniteStructurelleForm(any())).thenReturn(uniteStructurelleForm);
        when(uniteStructurelleForm.getIdentifiant()).thenReturn("id");
        when(uniteStructurelleForm.getKeyNors()).thenReturn(new ArrayList<>());
        when(uniteStructurelleForm.getMapMinisteresRatachement()).thenReturn(new HashMap<>());
        when(context.getAction(STActionEnum.MODIFY_UNITE_STRUCTURELLE)).thenReturn(new Action());

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addErrorToQueue("error");
        when(context.getMessageQueue()).thenReturn(alertManager);

        controller.saveOrUpdateUniteStructurelle(uniteStructurelleForm, null);

        verify(uniteStructurelleUIService).updateUniteStructurelle(context);
    }
}
