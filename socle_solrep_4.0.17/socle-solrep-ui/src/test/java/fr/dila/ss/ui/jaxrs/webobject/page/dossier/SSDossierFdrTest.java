package fr.dila.ss.ui.jaxrs.webobject.page.dossier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import fr.dila.ss.ui.bean.FdrDTO;
import fr.dila.ss.ui.services.SSFdrUIService;
import fr.dila.ss.ui.services.SSSelectValueUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.services.actions.DossierLockActionService;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.services.actions.STLockActionService;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SpecificContext.class, SSUIServiceLocator.class, STActionsServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class SSDossierFdrTest {
    private static final String ID_DOSSIER = "idDossier";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SSDossierFdr controlleur;

    @Mock
    private CoreSession session;

    @Mock
    private WebContext webcontext;

    @Mock
    private SSFdrUIService fdrService;

    @Mock
    private DossierLockActionService lockActionService;

    @Mock
    private STLockActionService stLockActionService;

    @Mock
    private SpecificContext context;

    @Mock
    private ThTemplate template;

    @Mock
    private DocumentModel document;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private SSSelectValueUIService selectValueService;

    @Before
    public void before() {
        controlleur = new SSDossierFdr();
        Whitebox.setInternalState(controlleur, "context", context);

        PowerMockito.mockStatic(SSUIServiceLocator.class);
        when(SSUIServiceLocator.getSSFdrUIService()).thenReturn(fdrService);
        when(SSUIServiceLocator.getSSSelectValueUIService()).thenReturn(selectValueService);

        when(webcontext.getCoreSession()).thenReturn(session);
        when(context.getWebcontext()).thenReturn(webcontext);
        when(webcontext.getPrincipal()).thenReturn(principal);

        PowerMockito.mockStatic(STActionsServiceLocator.class);
        when(STActionsServiceLocator.getDossierLockActionService()).thenReturn(lockActionService);
        when(STActionsServiceLocator.getSTLockActionService()).thenReturn(stLockActionService);
    }

    @Test
    public void testGetFdr() {
        FdrDTO dto = new FdrDTO();
        when(fdrService.getFeuilleDeRoute(context)).thenReturn(dto);
        when(context.getCurrentDocument()).thenReturn(document);
        when(document.getId()).thenReturn(ID_DOSSIER);
        List<String> groups = new ArrayList<>();
        groups.add("profil");
        when(principal.getGroups()).thenReturn(groups);
        List<SelectValueDTO> typeEtapes = new ArrayList<>();
        typeEtapes.add(new SelectValueDTO("id", "label"));
        when(selectValueService.getRoutingTaskTypes()).thenReturn(typeEtapes);

        ThTemplate template = controlleur.getFdr();
        assertNotNull(template);
        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/dossier/onglets/fdr", template.getName());
        assertNotNull(template.getContext());

        //Données ajoutées à la map
        assertEquals(4, template.getData().size());
        assertEquals(dto, template.getData().get("dto"));
        assertEquals(typeEtapes, template.getData().get("typeEtape"));
        assertEquals(ID_DOSSIER, template.getData().get(STTemplateConstants.ID_DOSSIER));
        assertEquals(groups, template.getData().get(SSTemplateConstants.PROFIL));
    }

    @Test
    public void testSaveFdr() {
        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addSuccessToQueue("message success");
        when(context.getMessageQueue()).thenReturn(alertManager);
        JsonResponse response = (JsonResponse) controlleur.saveFdr().getEntity();
        assertNotNull(response);
        assertEquals(SolonStatus.OK, response.getStatut());
        assertNull(response.getData());
        assertTrue(response.getMessages().getInfoMessageQueue().isEmpty());
        assertTrue(response.getMessages().getWarningMessageQueue().isEmpty());
        assertTrue(response.getMessages().getDangerMessageQueue().isEmpty());
        assertEquals(1, response.getMessages().getSuccessMessageQueue().size());
    }

    @Test
    public void testGetMyTemplate() {
        ThTemplate template = controlleur.getMyTemplate();
        assertNotNull(template);
        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/dossier/onglets/fdr", template.getName());
        assertNotNull(template.getContext());
    }
}
