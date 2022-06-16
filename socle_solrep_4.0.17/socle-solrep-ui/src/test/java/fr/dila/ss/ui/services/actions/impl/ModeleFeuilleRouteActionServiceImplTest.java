package fr.dila.ss.ui.services.actions.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.services.actions.STLockActionService;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement.ElementLifeCycleState;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ LockUtils.class, STActionsServiceLocator.class, SSServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class ModeleFeuilleRouteActionServiceImplTest {
    private static final String ID_MINISTERE = "60001234";

    private static final String FDR_NAME = "fdrName";
    private static final String CREATOR_NAME = "creatorName";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Spy
    private ModeleFeuilleRouteActionServiceImpl service;

    @Mock
    private CoreSession mockSession;

    @Mock
    private NuxeoPrincipal mockPrincipal;

    @Mock
    private STLockActionService mockLockActionService;

    @Mock
    private DocumentModel mockDocModel1;

    @Mock
    private SSFeuilleRoute mockFeuilleRoute;

    @Mock
    private DocumentModel mockDocModel2;

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private FeuilleRouteModelService feuilleRouteModelService;

    @Mock
    private ModeleFdrForm modeleForm;

    @Before
    public void before() {
        PowerMockito.mockStatic(LockUtils.class);
        PowerMockito.mockStatic(STActionsServiceLocator.class);
        PowerMockito.mockStatic(SSServiceLocator.class);

        when(LockUtils.isLockedByCurrentUser(eq(mockSession), any(DocumentRef.class))).thenReturn(true);
        when(STActionsServiceLocator.getSTLockActionService()).thenReturn(mockLockActionService);
        when(SSServiceLocator.getFeuilleRouteModelService()).thenReturn(feuilleRouteModelService);

        when(mockLockActionService.getCanLockDoc(mockDocModel1, mockSession)).thenReturn(true);

        when(mockFeuilleRoute.getMinistere()).thenReturn(ID_MINISTERE);

        when(mockSession.getPrincipal()).thenReturn(mockPrincipal);

        List<String> groups = Lists.newArrayList(
            SSConstant.GROUP_MINISTERE_PREFIX + ID_MINISTERE,
            STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER
        );
        when(mockPrincipal.getGroups()).thenReturn(groups);

        doReturn(true).when(service).isFeuilleDeRouteCreeParAdminFonctionnel(any(SpecificContext.class));

        when(mockFeuilleRoute.getDocument()).thenReturn(mockDocModel2);

        when(mockDocModel1.getAdapter(SSFeuilleRoute.class)).thenReturn(mockFeuilleRoute);
        when(context.getSession()).thenReturn(mockSession);
        when(context.getCurrentDocument()).thenReturn(mockDocModel1);
        when(context.getWebcontext()).thenReturn(webContext);
        when(webContext.getPrincipal()).thenReturn(mockPrincipal);
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
    }

    @Test
    public void testCanUserModifyRoute() {
        when(mockPrincipal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR)).thenReturn(true);
        assertTrue(service.canUserModifyRoute(context));
    }

    @Test
    public void testCanUserCreateRoute() {
        when(context.getAction(SSActionEnum.MODELE_ACTION_CREER_MODELE)).thenReturn(new Action());
        assertTrue(service.canUserCreateRoute(context));
    }

    @Test
    public void testCanUserDeleteRoute() {
        when(mockPrincipal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR)).thenReturn(true);
        assertTrue(service.canUserDeleteRoute(context));
    }

    @Test
    public void testCanInvalidateRoute() {
        when(mockPrincipal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR)).thenReturn(true);
        doReturn(mockFeuilleRoute).when(service).getRelatedRoute(eq(mockSession), eq(mockDocModel1));
        when(mockDocModel2.getCurrentLifeCycleState()).thenReturn(ElementLifeCycleState.validated.name());

        assertTrue(service.canInvalidateRoute(context));
    }

    @Test
    public void testInitFeuilleRoute() {
        when(modeleForm.getIntitule()).thenReturn(FDR_NAME);
        when(
                mockSession.createDocumentModel(
                    SSConstant.FDR_FOLDER_PATH,
                    FDR_NAME,
                    SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE
                )
            )
            .thenReturn(mockDocModel1);

        service.initFeuilleRoute(mockSession, modeleForm, CREATOR_NAME);

        verify(mockSession)
            .createDocumentModel(SSConstant.FDR_FOLDER_PATH, FDR_NAME, SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE);
        PowerMockito.verifyStatic();
        DublincoreSchemaUtils.setTitle(mockDocModel1, FDR_NAME);
    }

    @Test
    public void testCreateDocumentSucess() {
        when(mockFeuilleRoute.getMinistere()).thenReturn("");
        when(feuilleRouteModelService.isIntituleUnique(mockSession, mockFeuilleRoute)).thenReturn(true);

        service.createDocument(context);
        verify(mockSession).createDocument(mockDocModel2);
    }

    @Test
    public void testCreateDocumentFail() {
        when(mockFeuilleRoute.getMinistere()).thenReturn("");
        when(feuilleRouteModelService.isIntituleUnique(mockSession, mockFeuilleRoute)).thenReturn(false);

        DocumentModel route = service.createDocument(context);
        assertNull(route);
        String message = context.getMessageQueue().getErrorQueue().get(0).getAlertMessage().get(0);
        assertNotNull(message);
        assertEquals("Un modèle de feuille de route avec cet intitulé existe déjà dans le ministère spécifié", message);
    }
}
