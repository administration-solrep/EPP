package fr.dila.ss.ui.jaxrs.webobject.ajax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.ss.ui.jaxrs.webobject.ajax.modele.SSModeleFeuilleRouteAjax;
import fr.dila.ss.ui.services.SSModeleFdrFicheUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.ModeleFeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.th.bean.ModeleFdrEtapeSupprimeForm;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.impl.SolonAlertManager;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    { UserSessionHelper.class, SSModeleFeuilleRouteAjax.class, SSActionsServiceLocator.class, SSUIServiceLocator.class }
)
@PowerMockIgnore("javax.management.*")
public class SSModeleFeuilleRouteAjaxTest {
    private SSModeleFeuilleRouteAjax controlleur;

    @Mock
    private CoreSession session;

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private SpecificContext context;

    @Mock
    private ModeleFeuilleRouteActionService modeleAction;

    @Mock
    private SSModeleFdrFicheUIService modeleFDRFicheUIService;

    @Before
    public void before() throws Exception {
        controlleur = new SSModeleFeuilleRouteAjax();

        Whitebox.setInternalState(controlleur, "context", context);

        PowerMockito.mockStatic(UserSessionHelper.class);
        PowerMockito.mockStatic(SSActionsServiceLocator.class);
        PowerMockito.mockStatic(SSUIServiceLocator.class);

        when(context.getSession()).thenReturn(session);
        when(SSActionsServiceLocator.getModeleFeuilleRouteActionService()).thenReturn(modeleAction);
        when(SSUIServiceLocator.getSSModeleFdrFicheUIService()).thenReturn(modeleFDRFicheUIService);
    }

    @Test
    public void testSupprimerModele() {
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
        when(modeleAction.canUserDeleteRoute(context)).thenReturn(true);

        JsonResponse reponse = (JsonResponse) controlleur.supprimerModele("id").getEntity();

        verify(modeleAction).deleteModele(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        assertEquals(SolonStatus.OK, reponse.getStatut());
    }

    @Test
    public void testUnlockModele() {
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
        when(modeleAction.canUserLibererVerrou(context)).thenReturn(true);

        JsonResponse reponse = (JsonResponse) controlleur.unlockModele("id").getEntity();

        verify(modeleAction).libererVerrou(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        assertEquals(SolonStatus.OK, reponse.getStatut());
    }

    @Test
    public void testDemandeValidationModele() {
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
        when(modeleAction.canRequestValidateRoute(context)).thenReturn(true);

        JsonResponse reponse = (JsonResponse) controlleur.demandeValidationModele("id").getEntity();

        verify(modeleAction).requestValidateRouteModel(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        assertEquals(SolonStatus.OK, reponse.getStatut());
    }

    @Test
    public void testAnnulerDemandeValidationModele() {
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
        when(modeleAction.canCancelRequestValidateRoute(context)).thenReturn(true);

        JsonResponse reponse = (JsonResponse) controlleur.annulerDemandeValidationModele("id").getEntity();

        verify(modeleAction).cancelRequestValidateRouteModel(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        assertEquals(SolonStatus.OK, reponse.getStatut());
    }

    @Test
    public void testAccepterDemandeValidationModele() {
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
        when(modeleAction.canValidateRoute(context)).thenReturn(true);

        JsonResponse reponse = (JsonResponse) controlleur.accepterDemandeValidationModele("id").getEntity();

        verify(modeleAction).validateRouteModel(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        assertEquals(SolonStatus.OK, reponse.getStatut());
    }

    @Test
    public void testRefusDemandeValidationModele() {
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
        when(modeleAction.canRefuseValidateRoute(context)).thenReturn(true);

        JsonResponse reponse = (JsonResponse) controlleur.refusDemandeValidationModele("id").getEntity();

        verify(modeleAction).refuseValidateRouteModel(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        assertEquals(SolonStatus.OK, reponse.getStatut());
    }

    @Test
    public void testModifierModele() {
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
        when(modeleAction.canInvalidateRoute(context)).thenReturn(true);

        JsonResponse reponse = (JsonResponse) controlleur.modifierModele("id").getEntity();

        verify(modeleAction).invalidateRouteModel(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        assertEquals(SolonStatus.OK, reponse.getStatut());
    }

    @Test
    public void testShowContentModalSupprimerEtape() {
        List<String> idModeles = new ArrayList<>();
        idModeles.add("modele1");
        idModeles.add("modele2");
        List<ModeleFdrEtapeSupprimeForm> lModeleFdrEtapeSupprimeForm = new ArrayList<>();
        ModeleFdrEtapeSupprimeForm modeleFdrEtapeSupprimeForm = new ModeleFdrEtapeSupprimeForm();
        lModeleFdrEtapeSupprimeForm.add(modeleFdrEtapeSupprimeForm);
        when(modeleAction.listStepsToDelete(context)).thenReturn(lModeleFdrEtapeSupprimeForm);
        ThTemplate template = controlleur.showContentModalSupprimerEtape("idEtape", "idPoste", idModeles);

        verify(modeleAction).listStepsToDelete(context);
        assertNotNull(template);
        assertNotNull(template.getData());
        assertEquals(1, template.getData().size());
        assertNotNull(template.getData().get("listModeleFDR"));
        assertEquals(lModeleFdrEtapeSupprimeForm, template.getData().get("listModeleFDR"));
        assertEquals(template.getName(), "pages/admin/modele/deleteStepModeleFDRModalContent");
        assertEquals(template.getContext(), context);
        assertNotNull(template.getContext().getContextData());
    }

    @Test
    public void testSupprimerEtapeMasse() {
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
        List<String> idModeles = new ArrayList<>();
        idModeles.add("modele1");
        idModeles.add("modele2");

        JsonResponse reponse = (JsonResponse) controlleur
            .supprimerEtapeMasse("idTypeEtape", "idPoste", idModeles)
            .getEntity();

        verify(modeleAction).deleteMultipleStepsFromRoute(context);
        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        assertEquals(SolonStatus.OK, reponse.getStatut());
    }
}
