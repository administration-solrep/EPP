package fr.dila.ss.ui.services.impl;

import static fr.dila.ss.ui.enums.SSActionCategory.FDR_STEP_ACTIONS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.ss.ui.bean.FdrDTO;
import fr.dila.ss.ui.bean.fdr.FdrTableDTO;
import fr.dila.ss.ui.services.SSFeuilleRouteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.actions.SSDocumentRoutingActionService;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.actions.DossierLockActionService;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.services.actions.STLockActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        SpecificContext.class,
        SSUIServiceLocator.class,
        Framework.class,
        SSActionsServiceLocator.class,
        STActionsServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class SSFdrUIServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SSFdrUIServiceImpl service = new SSFdrUIServiceImpl();

    @Mock
    private CoreSession session;

    @Mock
    private SpecificContext context;

    @Mock
    private SSFeuilleRouteUIService ssFeuilleRouteUIService;

    @Mock
    private ActionManager actionService;

    @Mock
    private SSDocumentRoutingActionService docRoutingActionService;

    @Mock
    private DossierLockActionService lockActionService;

    @Mock
    private STLockActionService stLockActionService;

    @Before
    public void setUp() {
        service = new SSFdrUIServiceImpl();
        when(context.getSession()).thenReturn(session);

        PowerMockito.mockStatic(SSUIServiceLocator.class);
        PowerMockito.mockStatic(Framework.class);
        PowerMockito.mockStatic(SSActionsServiceLocator.class);
        when(Framework.getService(ActionManager.class)).thenReturn(actionService);
        List<Action> actions = getActionsMock();
        when(context.getActions(FDR_STEP_ACTIONS)).thenReturn(actions);

        when(SSUIServiceLocator.getSSFeuilleRouteUIService()).thenReturn(ssFeuilleRouteUIService);
        when(SSActionsServiceLocator.getDocumentRoutingActionService()).thenReturn(docRoutingActionService);

        PowerMockito.mockStatic(STActionsServiceLocator.class);
        when(STActionsServiceLocator.getDossierLockActionService()).thenReturn(lockActionService);
        when(STActionsServiceLocator.getSTLockActionService()).thenReturn(stLockActionService);

        when(Framework.expandVars(any())).thenAnswer(invocation -> invocation.getArgumentAt(0, String.class));
    }

    @Test
    public void testGetFeuilleDeRoute() {
        DocumentModel dossierDoc = mock(DocumentModel.class);
        STDossier dossierAdapter = mock(STDossier.class);
        when(dossierDoc.getAdapter(STDossier.class)).thenReturn(dossierAdapter);
        when(context.getCurrentDocument()).thenReturn(dossierDoc);
        when(dossierAdapter.getLastDocumentRoute()).thenReturn("lastDocId");
        Map<String, Object> mapContext = mock(Map.class);
        when(context.getContextData()).thenReturn(mapContext);

        FdrTableDTO tableDto = new FdrTableDTO();
        tableDto.setTotalNbLevel(5);
        when(ssFeuilleRouteUIService.getFeuilleRouteDTO(context)).thenReturn(tableDto);
        when(docRoutingActionService.canUserSubstituerFeuilleRoute(context)).thenReturn(true);

        FdrDTO dto = service.getFeuilleDeRoute(context);

        assertNotNull(dto);
        assertEquals(tableDto, dto.getTable());
        assertThat(dto.getTabActions()).isNotEmpty();
        verify(context).getCurrentDocument();
        verify(context).putInContextData(STContextDataKey.ID, "lastDocId");
        verify(context).putInContextData(SSFdrUIServiceImpl.CAN_USER_SUBSTITUER_FDR, true);
    }

    private List<Action> getActionsMock() {
        List<Action> actions = new ArrayList<>();

        Action a3 = new Action();
        a3.setLink("javascript:alert('Masquer les notes')");
        a3.setLabel("fdr.note.hide.label");
        a3.setIcon("icon--note-bubble-eye-close");
        actions.add(a3);

        return actions;
    }
}
