package fr.dila.ss.ui.services.actions.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.ui.bean.EditionEtapeFdrDTO;
import fr.dila.ss.ui.bean.fdr.CreationEtapeDTO;
import fr.dila.ss.ui.enums.FeuilleRouteEtapeOrder;
import fr.dila.ss.ui.enums.FeuilleRouteTypeRef;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSFeuilleRouteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.actions.Action;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ IdRef.class, FeuilleRouteActionServiceImpl.class, SSUIServiceLocator.class })
public class FeuilleRouteActionServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private FeuilleRouteActionServiceImpl service;

    @Mock
    SpecificContext context;

    @Mock
    private CreationEtapeDTO creationEtapeDTO;

    @Mock
    private CoreSession session;

    @Mock
    private DocumentModel elementDoc;

    @Mock
    private SSFeuilleRoute fdr;

    @Mock
    private IdRef docRef;

    @Mock
    private FeuilleRouteElement element;

    @Mock
    private StepFolder stepFolder;

    @Mock
    private EditionEtapeFdrDTO editstep;

    @Mock
    private SSFeuilleRouteUIService feuilleRouteUIService;

    @Before
    public void brfore() throws Exception {
        service = new FeuilleRouteActionServiceImpl();

        PowerMockito.mockStatic(SSUIServiceLocator.class);
        when(SSUIServiceLocator.getSSFeuilleRouteUIService()).thenReturn(feuilleRouteUIService);

        when(context.getFromContextData(SSContextDataKey.CREATION_ETAPE_DTO)).thenReturn(creationEtapeDTO);
        when(context.getSession()).thenReturn(session);
        PowerMockito.whenNew(IdRef.class).withAnyArguments().thenReturn(docRef);
        when(session.getDocument(docRef)).thenReturn(elementDoc);
        when(elementDoc.getAdapter(FeuilleRouteElement.class)).thenReturn(element);
        when(element.getFeuilleRoute(session)).thenReturn(fdr);
    }

    @Test
    public void testHasRightAddBranch() {
        when(creationEtapeDTO.getIdBranche()).thenReturn("idBranch");
        when(creationEtapeDTO.getTypeRef()).thenReturn(FeuilleRouteTypeRef.BRANCH.getFrontValue());
        when(context.getAction(SSActionEnum.ADD_BRANCH)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(true);

        assertTrue(service.checkRightSaveEtape(context));
    }

    @Test
    public void testHasRightAddStepAfterBranch() {
        when(creationEtapeDTO.getIdBranche()).thenReturn("idBranch");
        when(creationEtapeDTO.getTypeRef()).thenReturn(FeuilleRouteTypeRef.ETAPE.getFrontValue());
        when(creationEtapeDTO.getTypeAjout()).thenReturn(FeuilleRouteEtapeOrder.AFTER.getFrontValue());
        when(context.getAction(SSActionEnum.ADD_STEP_AFTER_BRANCH)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(true);

        assertTrue(service.checkRightSaveEtape(context));
    }

    @Test
    public void testHasRightAddStepBeforeBranch() {
        when(creationEtapeDTO.getIdBranche()).thenReturn("idBranch");
        when(creationEtapeDTO.getTypeRef()).thenReturn(FeuilleRouteTypeRef.ETAPE.getFrontValue());
        when(creationEtapeDTO.getTypeAjout()).thenReturn(FeuilleRouteEtapeOrder.BEFORE.getFrontValue());
        when(context.getAction(SSActionEnum.ADD_STEP_BEFORE_BRANCH)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(true);

        assertTrue(service.checkRightSaveEtape(context));
    }

    @Test
    public void testHasRightAddStepBefore() {
        when(creationEtapeDTO.getIdBranche()).thenReturn("idBranch");
        when(creationEtapeDTO.getTypeRef()).thenReturn(FeuilleRouteTypeRef.ETAPE.getFrontValue());
        when(creationEtapeDTO.getTypeAjout()).thenReturn(FeuilleRouteEtapeOrder.BEFORE.getFrontValue());
        when(context.getAction(SSActionEnum.ADD_STEP_BEFORE)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(false);

        assertTrue(service.checkRightSaveEtape(context));
    }

    @Test
    public void testHasRightAddStepAfter() {
        when(creationEtapeDTO.getIdBranche()).thenReturn("idBranch");
        when(creationEtapeDTO.getTypeRef()).thenReturn(FeuilleRouteTypeRef.ETAPE.getFrontValue());
        when(creationEtapeDTO.getTypeAjout()).thenReturn(FeuilleRouteEtapeOrder.BEFORE.getFrontValue());
        when(context.getAction(SSActionEnum.ADD_STEP_BEFORE)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(false);

        assertTrue(service.checkRightSaveEtape(context));
    }

    @Test
    public void testHasRightDeleteStep() {
        when(elementDoc.getId()).thenReturn("idElement");
        when(context.getCurrentDocument()).thenReturn(elementDoc);
        when(context.getAction(SSActionEnum.REMOVE_STEP)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(false);

        assertTrue(service.checkRightDeleteBranchOrStep(context));
    }

    @Test
    public void testHasRightDeleteSerialStep() {
        when(elementDoc.getId()).thenReturn("idElement");
        when(context.getCurrentDocument()).thenReturn(elementDoc);
        when(elementDoc.getType()).thenReturn(FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER);
        when(elementDoc.getAdapter(StepFolder.class)).thenReturn(stepFolder);
        when(stepFolder.isSerial()).thenReturn(true);
        when(context.getAction(SSActionEnum.REMOVE_SERIAL_BRANCH)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(true);

        assertTrue(service.checkRightDeleteBranchOrStep(context));
    }

    @Test
    public void testHasRightDeleteBranch() {
        when(elementDoc.getId()).thenReturn("idElement");
        when(context.getCurrentDocument()).thenReturn(elementDoc);
        when(elementDoc.getType()).thenReturn(FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER);
        when(elementDoc.getAdapter(StepFolder.class)).thenReturn(stepFolder);
        when(stepFolder.isParallel()).thenReturn(true);
        when(context.getAction(SSActionEnum.REMOVE_STEP_BRANCH)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(true);

        assertTrue(service.checkRightDeleteBranchOrStep(context));
    }

    @Test
    public void testHasRightMoveStepUp() {
        when(elementDoc.getId()).thenReturn("idElement");
        when(context.getCurrentDocument()).thenReturn(elementDoc);
        when(context.getFromContextData(SSContextDataKey.DIRECTION_MOVE_STEP))
            .thenReturn(FeuilleRouteWebConstants.MOVE_STEP_UP);
        when(context.getAction(SSActionEnum.MOVE_STEP_UP)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(false);

        assertTrue(service.checkRightMoveStep(context));
    }

    @Test
    public void testHasRightMoveStepDown() {
        when(elementDoc.getId()).thenReturn("idElement");
        when(context.getCurrentDocument()).thenReturn(elementDoc);
        when(context.getFromContextData(SSContextDataKey.DIRECTION_MOVE_STEP))
            .thenReturn(FeuilleRouteWebConstants.MOVE_STEP_DOWN);
        when(context.getAction(SSActionEnum.MOVE_STEP_DOWN)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(false);

        assertTrue(service.checkRightMoveStep(context));
    }

    @Test
    public void testHasRightUpdateStepInstance() {
        when(context.getFromContextData(SSContextDataKey.EDITION_ETAPE_FDR_DTO)).thenReturn(editstep);
        when(editstep.getStepId()).thenReturn("idStep");
        when(editstep.getIsModele()).thenReturn(false);
        when(context.getAction(SSActionEnum.UPDATE_STEP)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(false);

        assertTrue(service.checkRightUpdateStep(context));
    }

    @Test
    public void testHasRightUpdateStepModel() {
        when(context.getFromContextData(SSContextDataKey.EDITION_ETAPE_FDR_DTO)).thenReturn(editstep);
        when(editstep.getStepId()).thenReturn("idStep");
        when(editstep.getIsModele()).thenReturn(true);
        when(context.getAction(SSActionEnum.UPDATE_STEP_MODELE)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(false);

        assertTrue(service.checkRightUpdateStep(context));
    }

    @Test
    public void testHasRightPasteBefore() {
        when(context.getFromContextData(SSContextDataKey.ID_ETAPE)).thenReturn("idEtape");
        when(context.getFromContextData(SSContextDataKey.ADD_BEFORE)).thenReturn(true);
        when(elementDoc.getType()).thenReturn(FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP);
        when(context.getAction(SSActionEnum.PASTE_STEP_BEFORE)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(false);

        assertTrue(service.checkRightPasteStep(context));
    }

    @Test
    public void testHasRightPasteAfter() {
        when(context.getFromContextData(SSContextDataKey.ID_ETAPE)).thenReturn("idEtape");
        when(context.getFromContextData(SSContextDataKey.ADD_BEFORE)).thenReturn(false);
        when(elementDoc.getType()).thenReturn(FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP);
        when(context.getAction(SSActionEnum.PASTE_STEP_AFTER)).thenReturn(new Action());
        when(elementDoc.isFolder()).thenReturn(false);

        assertTrue(service.checkRightPasteStep(context));
    }
}
