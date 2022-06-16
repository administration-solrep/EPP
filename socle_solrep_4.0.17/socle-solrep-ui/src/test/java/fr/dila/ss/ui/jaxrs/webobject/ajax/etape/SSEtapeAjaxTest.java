package fr.dila.ss.ui.jaxrs.webobject.ajax.etape;

import static fr.dila.st.ui.th.model.SpecificContext.MESSAGE_QUEUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import fr.dila.ss.ui.bean.fdr.CreationEtapeDTO;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSFeuilleRouteUIService;
import fr.dila.ss.ui.services.SSSelectValueUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.FeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.actions.SSDocumentRoutingActionService;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.assertions.ResponseAssertions;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.ContextDataKey;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SSUIServiceLocator.class, SSActionsServiceLocator.class })
public class SSEtapeAjaxTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Spy
    private SSEtapeAjax controlleur = new SSEtapeAjax();

    @Mock
    private CoreSession session;

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private UserSession userSession;

    @Mock
    private SSFeuilleRouteUIService SSFeuilleRouteUIService;

    @Mock
    private SSDocumentRoutingActionService actionService;

    @Mock
    private SSSelectValueUIService selectValueService;

    private SolonAlertManager alertManager;

    @Mock
    private FeuilleRouteActionService fdrAction;

    @Before
    public void before() throws Exception {
        alertManager = new SolonAlertManager();

        PowerMockito.mockStatic(SSUIServiceLocator.class);
        when(SSUIServiceLocator.getSSFeuilleRouteUIService()).thenReturn(SSFeuilleRouteUIService);
        when(SSUIServiceLocator.getSSSelectValueUIService()).thenReturn(selectValueService);
        PowerMockito.mockStatic(SSActionsServiceLocator.class);
        when(SSActionsServiceLocator.getDocumentRoutingActionService()).thenReturn(actionService);
        when(SSActionsServiceLocator.getFeuilleRouteActionService()).thenReturn(fdrAction);

        PowerMockito.doCallRealMethod().when(context).putInContextData(any(ContextDataKey.class), any(Object.class));
        when(context.getSession()).thenReturn(session);
        when(context.getWebcontext()).thenReturn(webContext);
        when(context.getMessageQueue()).thenReturn(alertManager);
        when(webContext.getUserSession()).thenReturn(userSession);
    }

    @Test
    public void testAjouterEtape() {
        Whitebox.setInternalState(controlleur, "context", context);
        List<SelectValueDTO> typeEtapes = new ArrayList<>();

        WebContext webcontext = mock(WebContext.class);
        NuxeoPrincipal principal = mock(NuxeoPrincipal.class);

        when(context.getWebcontext()).thenReturn(webcontext);
        when(webcontext.getPrincipal()).thenReturn(principal);
        when(principal.getGroups()).thenReturn(ImmutableList.of(""));
        when(selectValueService.getRoutingTaskTypes()).thenReturn(typeEtapes);

        ThTemplate template = controlleur.ajouterEtape(Long.valueOf(1), "1");
        assertNotNull(template);
        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/fdr/etapeFdr", template.getName());
        assertNotNull(template.getContext());

        //Données ajoutées à la map
        assertThat(template.getData())
            .containsExactly(
                entry("profil", ImmutableList.of("")),
                entry("btnDeleteVisible", true),
                entry("typeEtape", typeEtapes),
                entry("uniqueId", "1")
            );
    }

    @Test
    public void testSaveEtape() {
        Whitebox.setInternalState(controlleur, "context", context);
        CreationEtapeDTO dto = new CreationEtapeDTO();

        when(context.getAction(SSActionEnum.ADD_BRANCH)).thenReturn(new Action());
        when(fdrAction.checkRightSaveEtape(context)).thenReturn(true);

        Response response = controlleur.saveEtape("dossierLinkId", dto);

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(context).putInContextData(SSContextDataKey.CREATION_ETAPE_DTO, dto);
        verify(context).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, "dossierLinkId");
        verify(SSFeuilleRouteUIService).addEtapes(context);
        verify(fdrAction).checkRightSaveEtape(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, MESSAGE_QUEUE, alertManager);
    }

    @Test
    public void testSupprimerBrancheOuEtape() {
        String id = "23";

        Whitebox.setInternalState(controlleur, "context", context);

        when(context.getAction(SSActionEnum.REMOVE_STEP)).thenReturn(new Action());
        when(fdrAction.checkRightDeleteBranchOrStep(context)).thenReturn(true);

        Response response = controlleur.supprimerBrancheOuEtape(id);

        verify(context).setCurrentDocument(id);
        verify(actionService).removeStep(context);
        verify(fdrAction).checkRightDeleteBranchOrStep(context);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        ResponseAssertions.assertJsonResponse(
            (JsonResponse) response.getEntity(),
            null,
            null,
            null,
            Arrays.asList(ResourceHelper.getString("fdr.toast.remove.step.success"))
        );

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, MESSAGE_QUEUE, alertManager);
    }

    @Test
    public void testMoveStep() {
        String stepId = "stepId";

        Whitebox.setInternalState(controlleur, "context", context);

        when(context.getAction(SSActionEnum.MOVE_STEP_UP)).thenReturn(new Action());
        when(fdrAction.checkRightMoveStep(context)).thenReturn(true);

        Response response = controlleur.moveStep(stepId, "up");

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(context).setCurrentDocument(stepId);
        verify(context).putInContextData(SSContextDataKey.DIRECTION_MOVE_STEP, "up");
        verify(actionService).moveRouteElement(context);
        verify(fdrAction).checkRightMoveStep(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, MESSAGE_QUEUE, alertManager);
    }
}
