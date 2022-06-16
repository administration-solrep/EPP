package fr.dila.ss.ui.services.actions.impl;

import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.bean.EditionEtapeFdrDTO;
import fr.dila.ss.ui.bean.fdr.CreationEtapeDTO;
import fr.dila.ss.ui.enums.FeuilleRouteEtapeOrder;
import fr.dila.ss.ui.enums.FeuilleRouteTypeRef;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.FeuilleRouteActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteAlreadyLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteNotLockedException;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

public class FeuilleRouteActionServiceImpl implements FeuilleRouteActionService {
    public static final String AUTH_EXEPTION_SAVE_ETAPE = "/admin/fdr/etape/saveEtape";

    @Override
    public SSFeuilleRoute getRelatedRoute(
        CoreSession session,
        DocumentModel currentDocument,
        List<DocumentModel> relatedRoutes
    ) {
        // try to see if actually the current document is a route
        SSFeuilleRoute relatedRoute = currentDocument.getAdapter(SSFeuilleRoute.class);
        if (relatedRoute != null) {
            return relatedRoute;
        }
        // try to see if the current document is a routeElement
        FeuilleRouteElement relatedRouteElement = currentDocument.getAdapter(FeuilleRouteElement.class);
        if (relatedRouteElement != null) {
            return relatedRouteElement.getFeuilleRoute(session);
        }
        // else we must be in a document attached to a route
        String relatedRouteModelDocumentId;
        if (relatedRoutes.size() <= 0) {
            return null;
        }
        relatedRouteModelDocumentId = relatedRoutes.get(0).getId();
        DocumentModel docRoute;
        docRoute = session.getDocument(new IdRef(relatedRouteModelDocumentId));
        return docRoute.getAdapter(SSFeuilleRoute.class);
    }

    @Override
    public void invalidateRouteModel(
        SpecificContext context,
        CoreSession session,
        DocumentModel currentDocument,
        List<DocumentModel> relatedRoutes
    ) {
        SSFeuilleRoute currentRouteModel = getRelatedRoute(session, currentDocument, relatedRoutes);
        try {
            SSServiceLocator.getDocumentRoutingService().lockDocumentRoute(currentRouteModel, session);
        } catch (FeuilleRouteAlreadyLockedException e) {
            context.getMessageQueue().addWarnToQueue("feedback.casemanagement.document.route.already.locked");
            return;
        }
        try {
            SSServiceLocator.getDocumentRoutingService().invalidateRouteModel(currentRouteModel, session);
        } catch (FeuilleRouteNotLockedException e) {
            context.getMessageQueue().addWarnToQueue("feedback.casemanagement.document.route.not.locked");
            return;
        }
        SSServiceLocator.getDocumentRoutingService().unlockDocumentRoute(currentRouteModel, session);
    }

    @Override
    public boolean checkRightSaveEtape(SpecificContext context) {
        return (
            hasRightAddBranch(context) ||
            hasRightAddStepBeforeBranch(context) ||
            hasRightAddStepAfterBranch(context) ||
            hasRightAddStepBefore(context) ||
            hasRightAddStepAfter(context)
        );
    }

    @Override
    public boolean checkRightDeleteBranchOrStep(SpecificContext context) {
        return hasRightDeleteStep(context) || hasRightDeleteSerialStep(context) || hasRightDeleteBranch(context);
    }

    @Override
    public boolean checkRightMoveStep(SpecificContext context) {
        return hasRightMoveStepUp(context) || hasRightMoveStepDown(context);
    }

    @Override
    public boolean checkRightUpdateStep(SpecificContext context) {
        return hasRightUpdateStepInstance(context) || hasRightUpdateStepModel(context);
    }

    @Override
    public boolean checkRightPasteStep(SpecificContext context) {
        return hasRightPasteAfter(context) || hasRightPasteBefore(context);
    }

    private boolean hasRightPasteAfter(SpecificContext context) {
        String idStep = context.getFromContextData(SSContextDataKey.ID_ETAPE);
        Boolean before = context.getFromContextData(SSContextDataKey.ADD_BEFORE);
        DocumentModel elementDoc = context.getSession().getDocument(new IdRef(idStep));
        initRoutingActionDto(context, idStep);
        if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP.equals(elementDoc.getType())) {
            return Boolean.FALSE.equals(before) && context.getAction(SSActionEnum.PASTE_STEP_AFTER) != null;
        }
        return false;
    }

    private boolean hasRightPasteBefore(SpecificContext context) {
        String idStep = context.getFromContextData(SSContextDataKey.ID_ETAPE);
        Boolean before = context.getFromContextData(SSContextDataKey.ADD_BEFORE);
        DocumentModel elementDoc = context.getSession().getDocument(new IdRef(idStep));
        initRoutingActionDto(context, idStep);
        if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP.equals(elementDoc.getType())) {
            return Boolean.TRUE.equals(before) && context.getAction(SSActionEnum.PASTE_STEP_BEFORE) != null;
        }
        return false;
    }

    private boolean hasRightUpdateStepModel(SpecificContext context) {
        EditionEtapeFdrDTO editstep = context.getFromContextData(SSContextDataKey.EDITION_ETAPE_FDR_DTO);
        initRoutingActionDto(context, editstep.getStepId());

        return editstep.getIsModele() && context.getAction(SSActionEnum.UPDATE_STEP_MODELE) != null;
    }

    private boolean hasRightUpdateStepInstance(SpecificContext context) {
        EditionEtapeFdrDTO editstep = context.getFromContextData(SSContextDataKey.EDITION_ETAPE_FDR_DTO);
        initRoutingActionDto(context, editstep.getStepId());

        return !editstep.getIsModele() && context.getAction(SSActionEnum.UPDATE_STEP) != null;
    }

    private boolean hasRightMoveStepDown(SpecificContext context) {
        DocumentModel elementDoc = context.getCurrentDocument();
        String direction = context.getFromContextData(SSContextDataKey.DIRECTION_MOVE_STEP);
        initRoutingActionDto(context, elementDoc.getId());
        return (
            FeuilleRouteWebConstants.MOVE_STEP_DOWN.equals(direction) &&
            context.getAction(SSActionEnum.MOVE_STEP_DOWN) != null
        );
    }

    private boolean hasRightMoveStepUp(SpecificContext context) {
        DocumentModel elementDoc = context.getCurrentDocument();
        String direction = context.getFromContextData(SSContextDataKey.DIRECTION_MOVE_STEP);
        initRoutingActionDto(context, elementDoc.getId());
        return (
            FeuilleRouteWebConstants.MOVE_STEP_UP.equals(direction) &&
            context.getAction(SSActionEnum.MOVE_STEP_UP) != null
        );
    }

    private boolean hasRightDeleteBranch(SpecificContext context) {
        DocumentModel elementDoc = context.getCurrentDocument();
        initRoutingActionDto(context, elementDoc.getId());
        if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(elementDoc.getType())) {
            final StepFolder stepFolder = elementDoc.getAdapter(StepFolder.class);
            return stepFolder.isParallel() && context.getAction(SSActionEnum.REMOVE_STEP_BRANCH) != null;
        }
        return false;
    }

    private boolean hasRightDeleteSerialStep(SpecificContext context) {
        DocumentModel elementDoc = context.getCurrentDocument();
        initRoutingActionDto(context, elementDoc.getId());
        if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(elementDoc.getType())) {
            final StepFolder stepFolder = elementDoc.getAdapter(StepFolder.class);
            return stepFolder.isSerial() && context.getAction(SSActionEnum.REMOVE_SERIAL_BRANCH) != null;
        }
        return false;
    }

    private boolean hasRightDeleteStep(SpecificContext context) {
        DocumentModel elementDoc = context.getCurrentDocument();
        initRoutingActionDto(context, elementDoc.getId());

        return !elementDoc.isFolder() && context.getAction(SSActionEnum.REMOVE_STEP) != null;
    }

    private boolean hasRightAddStepAfter(SpecificContext context) {
        CreationEtapeDTO creationEtapeDTO = context.getFromContextData(SSContextDataKey.CREATION_ETAPE_DTO);
        initRoutingActionDto(context, creationEtapeDTO.getIdBranche());
        DocumentModel elementRefDoc = context.getSession().getDocument(new IdRef(creationEtapeDTO.getIdBranche()));

        return (
            FeuilleRouteTypeRef.ETAPE.getFrontValue().equals(creationEtapeDTO.getTypeRef()) &&
            FeuilleRouteEtapeOrder.BEFORE.getFrontValue().equals(creationEtapeDTO.getTypeAjout()) &&
            !elementRefDoc.isFolder() &&
            context.getAction(SSActionEnum.ADD_STEP_BEFORE) != null
        );
    }

    private boolean hasRightAddStepBefore(SpecificContext context) {
        CreationEtapeDTO creationEtapeDTO = context.getFromContextData(SSContextDataKey.CREATION_ETAPE_DTO);
        initRoutingActionDto(context, creationEtapeDTO.getIdBranche());
        DocumentModel elementRefDoc = context.getSession().getDocument(new IdRef(creationEtapeDTO.getIdBranche()));

        return (
            FeuilleRouteTypeRef.ETAPE.getFrontValue().equals(creationEtapeDTO.getTypeRef()) &&
            FeuilleRouteEtapeOrder.AFTER.getFrontValue().equals(creationEtapeDTO.getTypeAjout()) &&
            !elementRefDoc.isFolder() &&
            context.getAction(SSActionEnum.ADD_STEP_AFTER) != null
        );
    }

    private boolean hasRightAddStepBeforeBranch(SpecificContext context) {
        CreationEtapeDTO creationEtapeDTO = context.getFromContextData(SSContextDataKey.CREATION_ETAPE_DTO);
        initRoutingActionDto(context, creationEtapeDTO.getIdBranche());
        DocumentModel elementRefDoc = context.getSession().getDocument(new IdRef(creationEtapeDTO.getIdBranche()));

        return (
            FeuilleRouteTypeRef.ETAPE.getFrontValue().equals(creationEtapeDTO.getTypeRef()) &&
            FeuilleRouteEtapeOrder.BEFORE.getFrontValue().equals(creationEtapeDTO.getTypeAjout()) &&
            elementRefDoc.isFolder() &&
            context.getAction(SSActionEnum.ADD_STEP_BEFORE_BRANCH) != null
        );
    }

    private boolean hasRightAddStepAfterBranch(SpecificContext context) {
        CreationEtapeDTO creationEtapeDTO = context.getFromContextData(SSContextDataKey.CREATION_ETAPE_DTO);
        initRoutingActionDto(context, creationEtapeDTO.getIdBranche());
        DocumentModel elementRefDoc = context.getSession().getDocument(new IdRef(creationEtapeDTO.getIdBranche()));

        return (
            FeuilleRouteTypeRef.ETAPE.getFrontValue().equals(creationEtapeDTO.getTypeRef()) &&
            FeuilleRouteEtapeOrder.AFTER.getFrontValue().equals(creationEtapeDTO.getTypeAjout()) &&
            elementRefDoc.isFolder() &&
            context.getAction(SSActionEnum.ADD_STEP_AFTER_BRANCH) != null
        );
    }

    private boolean hasRightAddBranch(SpecificContext context) {
        CreationEtapeDTO creationEtapeDTO = context.getFromContextData(SSContextDataKey.CREATION_ETAPE_DTO);
        initRoutingActionDto(context, creationEtapeDTO.getIdBranche());

        return (
            FeuilleRouteTypeRef.BRANCH.getFrontValue().equals(creationEtapeDTO.getTypeRef()) &&
            context.getAction(SSActionEnum.ADD_BRANCH) != null
        );
    }

    @Override
    public void initRoutingActionDto(SpecificContext context, String id) {
        CoreSession session = context.getSession();
        DocumentModel elementDoc = session.getDocument(new IdRef(id));
        FeuilleRoute fdr = elementDoc.getAdapter(FeuilleRouteElement.class).getFeuilleRoute(session);
        context.putInContextData(SSContextDataKey.STEP_DOC, elementDoc);
        context.putInContextData(SSContextDataKey.FEUILLE_ROUTE, fdr.getDocument());
        context.setCurrentDocument(elementDoc);
        if (elementDoc.isFolder()) {
            SSUIServiceLocator.getSSFeuilleRouteUIService().initStepFolderActionsDTO(context);
        } else {
            SSUIServiceLocator.getSSFeuilleRouteUIService().initEtapeActionsDTO(context);
        }
    }
}
