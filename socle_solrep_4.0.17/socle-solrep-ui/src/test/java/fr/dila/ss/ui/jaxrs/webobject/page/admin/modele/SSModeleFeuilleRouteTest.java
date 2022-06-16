package fr.dila.ss.ui.jaxrs.webobject.page.admin.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.services.SSModeleFdrFicheUIService;
import fr.dila.ss.ui.services.SSSelectValueUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.ModeleFeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.actions.impl.NavigationActionServiceImpl;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.helper.DtoJsonHelper;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
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
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        SSUIServiceLocator.class,
        SSModeleFeuilleRoute.class,
        SpecificContext.class,
        WebEngine.class,
        Framework.class,
        DtoJsonHelper.class,
        UserSessionHelper.class,
        SSActionsServiceLocator.class,
        STUIServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class SSModeleFeuilleRouteTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SSModeleFeuilleRoute page;

    @Mock
    private WebContext webContext;

    @Mock
    private SpecificContext context;

    @Mock
    private SSModeleFdrFicheUIService modeleFDRFicheUIService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private CoreSession session;

    @Mock
    private DocumentModel documentModel;

    @Mock
    private ModeleFeuilleRouteActionService modeleService;

    @Mock
    private SSSelectValueUIService selectValueService;

    @Before
    public void before() throws Exception {
        page = Mockito.spy(new SSModeleFeuilleRoute());
        PowerMockito.spy(UserSession.class);

        Whitebox.setInternalState(page, "context", context);

        when(context.getWebcontext()).thenReturn(webContext);
        when(webContext.getRequest()).thenReturn(request);
        when(webContext.getPrincipal()).thenReturn(principal);
        PowerMockito.doCallRealMethod().when(context).setContextData(Mockito.anyMap());
        context.setContextData(new HashMap<>());
        PowerMockito.doCallRealMethod().when(context).setNavigationContextTitle(any(Breadcrumb.class));
        when(context.getSession()).thenReturn(session);
        when(session.getDocument(any(DocumentRef.class))).thenReturn(documentModel);

        Breadcrumb breadcrumb = new Breadcrumb(NavigationActionServiceImpl.ESPACE_ADMIN, null, Breadcrumb.TITLE_ORDER);
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(breadcrumb);
        Mockito.when(context.getNavigationContext()).thenReturn(breadcrumbs);

        PowerMockito.mockStatic(DtoJsonHelper.class);
        PowerMockito.mockStatic(UserSessionHelper.class);
        PowerMockito.mockStatic(SSActionsServiceLocator.class);
        PowerMockito.mockStatic(SSUIServiceLocator.class);
        PowerMockito.mockStatic(STUIServiceLocator.class);
        when(SSUIServiceLocator.getSSModeleFdrFicheUIService()).thenReturn(modeleFDRFicheUIService);
        when(SSActionsServiceLocator.getModeleFeuilleRouteActionService()).thenReturn(modeleService);
        when(SSUIServiceLocator.getSSSelectValueUIService()).thenReturn(selectValueService);
    }

    @Test
    public void testGetSuppressionEtape() {
        List<String> idModeles = new ArrayList<>();
        idModeles.add("modele1");
        idModeles.add("modele2");

        List<SelectValueDTO> typeEtapes = new ArrayList<>();
        typeEtapes.add(new SelectValueDTO("1", "type"));

        when(selectValueService.getRoutingTaskTypes()).thenReturn(typeEtapes);

        List<Action> actions = context.getActions(SSActionCategory.MODELE_FDR_SUPPR_MASSE_ACTIONS);
        ThTemplate template = page.suppressionEtape(idModeles);

        assertNotNull(template);
        assertNotNull(template.getData());
        assertEquals(3, template.getData().size());
        assertEquals(typeEtapes, template.getData().get("typeEtape"));
        assertEquals(idModeles, template.getData().get("idModeles"));
        assertEquals(actions, template.getData().get(STTemplateConstants.ACTION));
        assertEquals(template.getName(), "pages/admin/modele/deleteStepModeleFDR");
        assertEquals(template.getContext(), context);
        assertNotNull(template.getContext().getContextData());
        verify(selectValueService).getRoutingTaskTypes();
    }
}
