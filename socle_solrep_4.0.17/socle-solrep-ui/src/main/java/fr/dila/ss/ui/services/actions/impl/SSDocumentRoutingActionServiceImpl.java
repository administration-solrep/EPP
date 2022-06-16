package fr.dila.ss.ui.services.actions.impl;

import static fr.dila.ss.core.service.SSServiceLocator.getDocumentRoutingService;
import static fr.dila.ss.ui.jaxrs.webobject.ajax.etape.SSEtapeAjax.COPIED_STEP_SESSION_KEY;
import static fr.dila.ss.ui.services.actions.SSActionsServiceLocator.getRelatedRouteActionService;
import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.LOG_EXCEPTION_TEC;
import static fr.dila.st.core.util.ResourceHelper.getString;
import static fr.dila.st.ui.services.actions.STActionsServiceLocator.getSTLockActionService;
import static org.nuxeo.ecm.core.schema.FacetNames.FOLDERISH;

import com.google.common.collect.Iterables;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.dto.EtapeFeuilleDeRouteDTO;
import fr.dila.ss.api.feuilleroute.DocumentRouteTableElement;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.DossierDistributionService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.api.service.SSFeuilleRouteService;
import fr.dila.ss.api.service.SSJournalService;
import fr.dila.ss.core.dto.activitenormative.EtapeFeuilleDeRouteDTOImpl;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.bean.fdr.CreationEtapeDTO;
import fr.dila.ss.ui.bean.fdr.CreationEtapeLineDTO;
import fr.dila.ss.ui.enums.FeuilleRouteEtapeOrder;
import fr.dila.ss.ui.enums.FeuilleRouteTypeRef;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.actions.SSDocumentRoutingActionService;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteAlreadyLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteNotLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRouteService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteStepFolderSchemaUtil;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.event.CoreEventConstants;

public class SSDocumentRoutingActionServiceImpl implements SSDocumentRoutingActionService {
    private static final STLogger LOG = STLogFactory.getLog(SSDocumentRoutingActionService.class);

    public static final String SOURCE_DOC_NAME = "source_doc_name";
    public static final String ROUTE_DOCUMENT_REF = "route_doc_ref";

    /**
     * Ajout d'une étape avant l'étape sélectionnée.
     */
    public static final String STEP_ORDER_BEFORE = "before";

    /**
     * Ajout d'une étape à l'intérieur du conteneur sélectionné.
     */
    public static final String STEP_ORDER_IN = "in";

    /**
     * Document virtuel, correspondant à la création de plusieurs conteneurs série.
     */
    public static final String ROUTE_ELEMENT_MASS_TYPE = "route_element_mass";

    /**
     * Document virtuel, correspondant à la création d'un conteneur parallèle et de plusieurs conteneurs série.
     */
    public static final String ROUTE_FORK_TYPE = "route_fork";

    /**
     * Document virtuel, correspondant à la création de plusieurs contenueurs parallèle ayant chacun un conteneur série.
     */
    private static final String ROUTE_FORK_MASS_TYPE = "route_fork_mass";
    private static final String FEUILLE_ROUTE_ETAPE_ACTION_PASTE_EMPTY_TARGET_ERROR =
        "feuilleRoute.etape.action.paste.emptyTarget.error";
    private static final String FEUILLE_ROUTE_ETAPE_ACTION_PASTE_TARGET_UNKNOWN_ERROR =
        "feuilleRoute.etape.action.paste.targetUnknown.error";
    private static final String FEUILLE_ROUTE_ETAPE_ACTION_PASTE_TARGET_PARENT_ERROR =
        "feuilleRoute.etape.action.paste.targetParent.error";
    private static final String FEUILLE_ROUTE_ETAPE_ACTION_PASTE_EMPTY_SELECTION_ERROR =
        "feuilleRoute.etape.action.paste.emptySelection.error";

    @Override
    public boolean isFeuilleRouteVisible(CoreSession session, SSPrincipal ssPrincipal, DocumentModel dossierDoc) {
        String lastDocumentRoute = dossierDoc.getAdapter(STDossier.class).getLastDocumentRoute();
        return StringUtils.isNotEmpty(lastDocumentRoute) && session.exists(new IdRef(lastDocumentRoute));
    }

    /**
     * Check if the related route to this case is started (ready or running) or not
     */
    @Override
    public boolean hasRelatedRoute(CoreSession session, DocumentModel currentDocument) {
        return !getRelatedRouteActionService().findRelatedRoute(session, currentDocument).isEmpty();
    }

    @Override
    public String startRoute(CoreSession session, DocumentModel currentDocument, List<String> attachDocIds) {
        FeuilleRoute currentRoute = currentDocument.getAdapter(FeuilleRoute.class);
        currentRoute.setAttachedDocuments(attachDocIds);
        getDocumentRoutingService().createNewInstance(session, currentDocument, currentRoute.getAttachedDocuments());
        return null;
    }

    @Override
    public SSFeuilleRoute getRelatedRoute(CoreSession session, DocumentModel currentDocument) {
        // Retourne le document chargé si c'est une feuille de route

        if (SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE.equals(currentDocument.getType())) {
            return currentDocument.getAdapter(SSFeuilleRoute.class);
        }

        // Si l'elément chargé est un élément de feuille de route, remonte à la
        // route
        if (
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equalsIgnoreCase(currentDocument.getType()) ||
            SSConstant.STEP_FOLDER_DOCUMENT_TYPE.equalsIgnoreCase(currentDocument.getType())
        ) {
            FeuilleRouteElement relatedRouteElement = currentDocument.getAdapter(FeuilleRouteElement.class);
            if (relatedRouteElement != null) {
                FeuilleRoute documentRoute = relatedRouteElement.getFeuilleRoute(session);
                if (documentRoute == null) {
                    return null;
                }
                return documentRoute.getDocument().getAdapter(SSFeuilleRoute.class);
            }
        }

        // Sinon, on doit être dans un document attaché à la route
        if (!hasRelatedRoute(session, currentDocument)) {
            return null;
        }
        DocumentModel docRoute = getRelatedRouteActionService().findRelatedRoute(session, currentDocument).get(0);
        return docRoute.getAdapter(SSFeuilleRoute.class);
    }

    public DocumentModel getDossierAttachedToCurrentRoute(DocumentModel currentDocument) {
        if (!currentDocument.hasFacet(SSConstant.ROUTABLE_FACET)) {
            return null;
        }
        return currentDocument;
    }

    /**
     * Retourne vrai si l'étape est un conteneur.
     *
     * @param stepDoc
     *            Etape de feuille de route
     * @return Vrai si l'étape est un conteneur
     */
    @Override
    public boolean isRouteFolder(DocumentModel stepDoc) {
        return stepDoc.hasFacet("Folderish");
    }

    @Override
    public boolean isSerialStepFolder(SpecificContext context) {
        DocumentModel stepFolder = context.getCurrentDocument();
        if (SSConstant.STEP_FOLDER_DOCUMENT_TYPE.equals(stepFolder.getType()) && stepFolder.isFolder()) {
            FeuilleRouteExecutionType typeFolder = FeuilleRouteStepFolderSchemaUtil.getExecutionType(stepFolder);
            return typeFolder == FeuilleRouteExecutionType.serial;
        }

        return false;
    }

    @Override
    public boolean isParallelStepFolder(SpecificContext context) {
        DocumentModel stepFolder = context.getCurrentDocument();
        if (SSConstant.STEP_FOLDER_DOCUMENT_TYPE.equals(stepFolder.getType()) && stepFolder.isFolder()) {
            FeuilleRouteExecutionType typeFolder = FeuilleRouteStepFolderSchemaUtil.getExecutionType(stepFolder);
            return typeFolder == FeuilleRouteExecutionType.parallel;
        }

        return false;
    }

    @Override
    public boolean isEditableStep(
        SpecificContext context,
        CoreSession session,
        DocumentModel currentDocument,
        DocumentModel stepDoc
    ) {
        // If fork, is not simple editable step
        if (isRouteFolder(stepDoc)) {
            return false;
        }

        return isEditableRouteElement(context);
    }

    @Override
    public boolean isEditableRouteElement(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel elementDoc = context.getCurrentDocument();
        FeuilleRouteElement element = elementDoc.getAdapter(FeuilleRouteElement.class);
        FeuilleRoute route = element.getFeuilleRoute(session);
        return (
            LockUtils.isLockedByCurrentUser(session, route.getDocument().getRef()) &&
            (isLastStepDoneFromDoneRouteInstance(context, elementDoc) || element.isModifiable())
        );
    }

    @Override
    public boolean isInProgressStep(SpecificContext context) {
        DocumentModel elementDoc = context.getCurrentDocument();
        return !FeuilleRouteElement.ElementLifeCycleState.done.name().equals(elementDoc.getCurrentLifeCycleState());
    }

    @Override
    public boolean canEditStep(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel stepDoc = context.getFromContextData(SSContextDataKey.STEP_DOC);

        boolean isEditableRouteElement = isEditableRouteElement(context);
        if (!stepDoc.hasFacet(FOLDERISH)) {
            return (
                isEditableRouteElement &&
                getDocumentRoutingService().canDoActionIfStepObligatoire(session.getPrincipal(), stepDoc)
            );
        }
        return isEditableRouteElement;
    }

    /**
     * Vérifie si on est la dernière étape terminée d'une instance terminée.
     *
     * @param stepDoc
     *            Étape de feuille de route
     * @return Dernière étape
     */
    protected boolean isLastStepDoneFromDoneRouteInstance(SpecificContext context, DocumentModel elementDoc) {
        CoreSession session = context.getSession();
        FeuilleRoute route = elementDoc.getAdapter(FeuilleRouteElement.class).getFeuilleRoute(session);
        if (
            FeuilleRouteElement.ElementLifeCycleState.done.name().equals(elementDoc.getCurrentLifeCycleState()) &&
            FeuilleRouteElement.ElementLifeCycleState.done.name().equals(route.getDocument().getCurrentLifeCycleState())
        ) {
            SSFeuilleRouteService routeService = SSServiceLocator.getSSFeuilleRouteService();
            List<DocumentModel> stepsDoc = routeService.getSteps(session, route.getDocument().getId());
            return Iterables.getLast(stepsDoc).getId().equals(elementDoc.getId());
        }
        return false;
    }

    /**
     * do the same thing as computeRelatedRouteElements() but takes the id in
     * parameter instead of using the first id in relatedRoutes
     *
     * @param id
     * @return
     */
    protected List<RouteTableElement> computeRelatedRouteElements(CoreSession session, String id) {
        if (id == null || id.isEmpty()) {
            return new ArrayList<>();
        }
        DocumentModel relatedRouteDocumentModel = session.getDocument(new IdRef(id));
        SSFeuilleRoute currentRoute = relatedRouteDocumentModel.getAdapter(SSFeuilleRoute.class);

        DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        return documentRoutingService.getFeuilleRouteElements(currentRoute, session);
    }

    /**
     * Retourne vrai si l'utilisateur peut modifier l'instance de feuille de
     * route.
     *
     * @return Vrai si l'utilisateur peut modifier l'instance de feuille de
     *         route.
     */
    @Override
    public boolean isFeuilleRouteUpdatable(
        SpecificContext context,
        CoreSession session,
        DocumentModel currentDocument
    ) {
        SSFeuilleRoute route = getRelatedRoute(session, currentDocument);
        if (route == null) {
            return false;
        }

        // Si la feuille de route est terminée, l'utilisateur doit avoir le
        // droit de la redémarrer
        return (
            !route.isDone() &&
            !(route.isDraft() && route.isDemandeValidation()) ||
            (
                route.isDone() &&
                session.getPrincipal().isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_INSTANCE_RESTARTER)
            )
        );
    }

    @Override
    public boolean isModeleFeuilleRoute(SpecificContext context, DocumentModel feuilleRouteDoc) {
        return SSConstant.FDR_FOLDER_PATH.equals(feuilleRouteDoc.getPath().removeLastSegments(1).toString());
    }

    @Override
    public void cancelRoute(CoreSession session, DocumentModel currentDocument) {
        DocumentModel doc = getRelatedRouteActionService().findRelatedRoute(session, currentDocument).get(0);
        FeuilleRoute route = doc.getAdapter(FeuilleRoute.class);
        route.cancel(session);
        session.save();
    }

    @Override
    public void validateRouteModel(SpecificContext context, CoreSession session, DocumentModel currentDocument) {
        SSFeuilleRoute currentRouteModel = getRelatedRoute(session, currentDocument);
        DocumentRoutingService docRoutingServ = getDocumentRoutingService();
        try {
            docRoutingServ.lockDocumentRoute(currentRouteModel, session);
        } catch (FeuilleRouteAlreadyLockedException e) {
            context
                .getMessageQueue()
                .addWarnToQueue(getString("feedback.casemanagement.document.route.already.locked"));
            return;
        }
        try {
            docRoutingServ.validateRouteModel(currentRouteModel, session);
        } catch (FeuilleRouteNotLockedException e) {
            context.getMessageQueue().addWarnToQueue(getString("feedback.casemanagement.document.route.not.locked"));
            return;
        }
        docRoutingServ.unlockDocumentRoute(currentRouteModel, session);
    }

    protected List<RouteTableElement> computeRouteElements(CoreSession session, DocumentModel currentDocument) {
        SSFeuilleRoute currentRoute = currentDocument.getAdapter(SSFeuilleRoute.class);
        return getElements(session, currentRoute);
    }

    protected List<RouteTableElement> computeRelatedRouteElements(CoreSession session, DocumentModel currentDocument) {
        if (!hasRelatedRoute(session, currentDocument)) {
            return new ArrayList<>();
        }
        DocumentModel relatedRouteDocumentModel = getRelatedRouteActionService()
            .findRelatedRoute(session, currentDocument)
            .get(0);
        SSFeuilleRoute currentRoute = relatedRouteDocumentModel.getAdapter(SSFeuilleRoute.class);
        return getElements(session, currentRoute);
    }

    protected List<RouteTableElement> getElements(CoreSession session, SSFeuilleRoute currentRoute) {
        return getDocumentRoutingService().getFeuilleRouteElements(currentRoute, session);
    }

    @Override
    public void startRouteRelatedToCurrentDocument(
        SpecificContext context,
        CoreSession session,
        DocumentModel currentDocument
    ) {
        FeuilleRoute route = getRelatedRoute(session, currentDocument);
        if (route == null) {
            context
                .getMessageQueue()
                .addWarnToQueue(getString("feedback.casemanagement.document.route.no.valid.route"));
            return;
        }

        List<String> documentIds = new ArrayList<>();
        documentIds.add(currentDocument.getId());
        route.setAttachedDocuments(documentIds);
        getDocumentRoutingService().createNewInstance(session, route.getDocument(), route.getAttachedDocuments());
    }

    @Override
    public boolean routeRelatedToCurrentDocumentIsRunning(CoreSession session, DocumentModel currentDocument) {
        FeuilleRoute route = getRelatedRoute(session, currentDocument);
        if (route == null) {
            return false;
        }
        return route.isRunning();
    }

    @Override
    public String getTypeDescription(DocumentRouteTableElement localizable) {
        return depthFormatter(localizable.getDepth(), localizable.getElement().getDocument().getType());
    }

    private String depthFormatter(int depth, String type) {
        StringBuilder depthFormatter = new StringBuilder();
        for (int i = 0; i < depth - 1; i++) {
            depthFormatter.append("__");
        }
        depthFormatter.append(type);
        return depthFormatter.toString();
    }

    @Override
    public boolean isStep(DocumentModel doc) {
        return doc.hasFacet(FeuilleRouteConstant.FACET_FEUILLE_ROUTE_STEP);
    }

    @Override
    public boolean currentRouteModelIsDraft(DocumentModel relatedRouteModel) {
        FeuilleRoute routeModel = relatedRouteModel.getAdapter(FeuilleRoute.class);
        if (routeModel == null) {
            return false;
        }
        return routeModel.isDraft();
    }

    /**
     * Supprime une étape de feuille de route.
     */
    @Override
    public void removeStep(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel routeElementDoc = context.getCurrentDocument();

        DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        // Supprime l'étape de feuille de route
        boolean alreadyLockedByCurrentUser = false;
        FeuilleRouteElement relatedRouteElement = routeElementDoc.getAdapter(FeuilleRouteElement.class);
        SSFeuilleRoute feuilleRoute = relatedRouteElement.getFeuilleRoute(session);
        if (LockUtils.isLockedByCurrentUser(session, feuilleRoute.getDocument().getRef())) {
            alreadyLockedByCurrentUser = true;
        } else {
            lockRoute(context, session, feuilleRoute);
        }
        try {
            documentRoutingService.removeRouteElement(relatedRouteElement, session);
        } catch (FeuilleRouteNotLockedException e) {
            context.getMessageQueue().addWarnToQueue(getString("feedback.casemanagement.document.route.not.locked"));
            return;
        }

        if (!alreadyLockedByCurrentUser) {
            documentRoutingService.unlockDocumentRoute(feuilleRoute, session);
        }

        // Récupère le dossier attaché si on est sur une instance de feuille de
        // route
        if (feuilleRoute.isFeuilleRouteInstance()) {
            DossierDistributionService dossierDistributionService = SSServiceLocator.getDossierDistributionService();
            DocumentModel dossierDoc = dossierDistributionService.getDossierFromDocumentRouteId(
                session,
                feuilleRoute.getDocument().getId()
            );
            if (dossierDoc != null) {
                JournalService journalService = STServiceLocator.getJournalService();
                journalService.journaliserActionFDR(
                    session,
                    dossierDoc,
                    STEventConstant.EVENT_FEUILLE_ROUTE_STEP_DELETE,
                    STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_DELETE
                );
            }
        }
    }

    @Override
    public List<EtapeFeuilleDeRouteDTO> getLstEtapes(
        CoreSession session,
        String hiddenSourceDocId,
        String hiddenDocOrder
    ) {
        List<EtapeFeuilleDeRouteDTO> lstEtapes = new ArrayList<>();
        DocumentModel etapeDoc = newSimpleRouteStep(session, hiddenSourceDocId, hiddenDocOrder);
        SSRouteStep routeStepDesired = etapeDoc.getAdapter(SSRouteStep.class);
        lstEtapes.add(new EtapeFeuilleDeRouteDTOImpl(routeStepDesired));
        return lstEtapes;
    }

    @Override
    public DocumentModel newSimpleRouteStep(CoreSession session, String hiddenSourceDocId, String hiddenDocOrder) {
        DocumentRef sourceDocRef = new IdRef(hiddenSourceDocId);

        DocumentModel sourceDoc = getSourceDocFromRef(session, sourceDocRef);

        String parentPath = null;
        if (STEP_ORDER_IN.equals(hiddenDocOrder)) {
            parentPath = sourceDoc.getPathAsString();
        } else {
            DocumentModel parentDoc = session.getParentDocument(sourceDocRef);
            parentPath = parentDoc.getPathAsString();
        }

        UUID newId = UUID.randomUUID();
        DocumentModel desiredDocument = session.createDocumentModel(SSConstant.ROUTE_STEP_DOCUMENT_TYPE);
        desiredDocument.setPathInfo(parentPath, newId.toString());

        return desiredDocument;
    }

    @Override
    public void removeFromLstEtapeFeuilleDeRoute(
        CoreSession session,
        String hiddenSourceDocId,
        String hiddenDocOrder,
        EtapeFeuilleDeRouteDTO eFDR
    ) {
        List<EtapeFeuilleDeRouteDTO> lstEtapes = getLstEtapes(session, hiddenSourceDocId, hiddenDocOrder);
        Iterator<EtapeFeuilleDeRouteDTO> itEtapes = lstEtapes.iterator();

        while (itEtapes.hasNext()) {
            EtapeFeuilleDeRouteDTO etape = itEtapes.next();

            if (etape == eFDR || etape.equals(eFDR)) {
                lstEtapes.remove(etape);
            }
        }
    }

    protected DocumentRef getRouteRefFromDocument(DocumentModel currentDocument) {
        DocumentRef routeRef = currentDocument.getRef();
        if (currentDocument.getType().equals(STConstant.DOSSIER_DOCUMENT_TYPE)) {
            STDossier dossier = currentDocument.getAdapter(STDossier.class);
            routeRef = new IdRef(dossier.getLastDocumentRoute());
        }
        return routeRef;
    }

    protected DocumentModel getSourceDocFromRef(CoreSession session, DocumentRef sourceDocRef) {
        DocumentModel sourceDoc = session.getDocument(sourceDocRef);
        if (sourceDoc.getType().equals(STConstant.DOSSIER_DOCUMENT_TYPE)) {
            STDossier dossier = sourceDoc.getAdapter(STDossier.class);
            sourceDoc = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));
        }
        return sourceDoc;
    }

    protected String getSourceDocName(
        CoreSession session,
        DocumentModel sourceDoc,
        DocumentModel parentDoc,
        FeuilleRouteEtapeOrder hiddenDocOrder
    ) {
        String sourceDocName = null;
        if (FeuilleRouteEtapeOrder.BEFORE == hiddenDocOrder) {
            sourceDocName = sourceDoc.getName();
        } else {
            DocumentModelList orderedChilds = getDocumentRoutingService()
                .getOrderedRouteElement(parentDoc.getId(), session);
            int selectedDocumentIndex = orderedChilds.indexOf(sourceDoc);
            int nextIndex = selectedDocumentIndex + 1;
            if (nextIndex >= orderedChilds.size()) {
                sourceDocName = null;
            } else {
                sourceDocName = orderedChilds.get(nextIndex).getName();
            }
        }
        return sourceDocName;
    }

    @Override
    public boolean isCurrentRouteLockedByCurrentUser(CoreSession session, DocumentModel currentDocument) {
        return (
            getRelatedRoute(session, currentDocument) != null &&
            LockUtils.isLockedByCurrentUser(session, getRelatedRoute(session, currentDocument).getDocument().getRef())
        );
    }

    @Override
    public boolean isCurrentRouteLocked(CoreSession session, DocumentModel currentDocument) {
        return LockUtils.isLocked(session, getRelatedRoute(session, currentDocument).getDocument().getRef());
    }

    @Override
    public boolean canUnlockRoute(CoreSession session, DocumentModel currentDocument) {
        return getSTLockActionService()
            .getCanUnlockDoc(getRelatedRoute(session, currentDocument).getDocument(), session);
    }

    @Override
    public boolean canLockRoute(CoreSession session, DocumentModel currentDocument) {
        return getSTLockActionService().getCanLockDoc(getRelatedRoute(session, currentDocument).getDocument(), session);
    }

    @Override
    public boolean canStepBeDeleted(SpecificContext context) {
        NuxeoPrincipal principal = context.getWebcontext().getPrincipal();
        SSRouteStep step = context.getCurrentDocument().getAdapter(SSRouteStep.class);
        return getDocumentRoutingService().canDoActionAccordingToStepObligatoireProperty(principal, step);
    }

    @Override
    public boolean canFolderBeDeleted(SpecificContext context) {
        NuxeoPrincipal principal = context.getWebcontext().getPrincipal();
        CoreSession session = context.getSession();
        DocumentModel routeElementDoc = context.getCurrentDocument();
        return getDocumentRoutingService().canFolderBeDeleted(session, principal, routeElementDoc.getRef());
    }

    @Override
    public Map<String, String> getCurrentRouteLockDetails(CoreSession session, DocumentModel currentDocument) {
        return getSTLockActionService()
            .getLockDetails(getRelatedRoute(session, currentDocument).getDocument(), session);
    }

    @Override
    public void lockCurrentRoute(SpecificContext context, CoreSession session, DocumentModel currentDocument) {
        FeuilleRoute docRouteElement = getRelatedRoute(session, currentDocument);
        lockRoute(context, session, docRouteElement);
    }

    protected boolean lockRoute(SpecificContext context, CoreSession session, FeuilleRoute docRouteElement) {
        try {
            getDocumentRoutingService().lockDocumentRoute(docRouteElement.getFeuilleRoute(session), session);
            return true;
        } catch (FeuilleRouteAlreadyLockedException e) {
            LOG.error(session, STLogEnumImpl.FAIL_LOCK_DOC_TEC, e);
            context
                .getMessageQueue()
                .addWarnToQueue(getString("feedback.casemanagement.document.route.already.locked"));
            return false;
        }
    }

    @Override
    public void unlockCurrentRoute(CoreSession session, DocumentModel currentDocument) {
        SSFeuilleRoute route = getRelatedRoute(session, currentDocument);
        getDocumentRoutingService().unlockDocumentRoute(route, session);
    }

    @Override
    public boolean isEmptyFork(CoreSession session, DocumentModel forkDoc) {
        return forkDoc.hasFacet("Folderish") && !session.hasChildren(forkDoc.getRef());
    }

    @Override
    public DocumentModel createRouteElement(
        CoreSession session,
        DocumentModel currentDocument,
        String typeName,
        String hiddenSourceDocId,
        FeuilleRouteEtapeOrder hiddenDocOrder
    ) {
        DocumentRef routeRef = getRouteRefFromDocument(currentDocument);
        DocumentRef sourceDocRef = new IdRef(hiddenSourceDocId);

        DocumentModel sourceDoc = getSourceDocFromRef(session, sourceDocRef);

        String sourceDocName = null;
        String parentPath = null;
        if (hiddenDocOrder == FeuilleRouteEtapeOrder.IN) {
            parentPath = sourceDoc.getPathAsString();
        } else {
            DocumentModel parentDoc = session.getParentDocument(sourceDocRef);
            parentPath = parentDoc.getPathAsString();
            sourceDocName = getSourceDocName(session, sourceDoc, parentDoc, hiddenDocOrder);
        }

        /* Creation en masse */
        if (ROUTE_ELEMENT_MASS_TYPE.equals(typeName)) {
            // Création en masse de branche en série
            return null;
        }

        // Pour le type virtuel "route_fork", crée un document StepFolder
        String typeToCreateName = typeName;
        if (ROUTE_FORK_TYPE.equals(typeName) || ROUTE_FORK_MASS_TYPE.equals(typeName)) {
            typeToCreateName = SSConstant.STEP_FOLDER_DOCUMENT_TYPE;
        }

        DocumentModel changeableDocument = session.createDocumentModel(typeToCreateName);
        Map<String, Serializable> context = changeableDocument.getContextData();
        context.put(CoreEventConstants.PARENT_PATH, parentPath);
        context.put(SOURCE_DOC_NAME, sourceDocName);
        context.put(ROUTE_DOCUMENT_REF, routeRef);
        return changeableDocument;
    }

    @Override
    public void moveRouteElement(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel stepDoc = context.getCurrentDocument();
        String direction = context.getFromContextData(SSContextDataKey.DIRECTION_MOVE_STEP);

        DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        JournalService journalService = STServiceLocator.getJournalService();
        boolean moveUp = FeuilleRouteWebConstants.MOVE_STEP_UP.equals(direction);

        try {
            documentRoutingService.moveRouteStep(session, stepDoc.getId(), moveUp);
        } catch (FeuilleRouteException e) {
            context.getMessageQueue().addErrorToQueue(e.getMessage());
            LOG.error(session, LOG_EXCEPTION_TEC, stepDoc, e);
            return;
        }

        // Récupère le dossier attaché si on est sur une instance de feuille de
        // route
        FeuilleRouteElement feuilleRouteElement = stepDoc.getAdapter(FeuilleRouteElement.class);
        FeuilleRoute feuilleRoute = feuilleRouteElement.getFeuilleRoute(session);
        if (feuilleRoute.isFeuilleRouteInstance()) {
            DossierDistributionService dossierDistributionService = SSServiceLocator.getDossierDistributionService();
            DocumentModel dossierDoc = dossierDistributionService.getDossierFromDocumentRouteId(
                session,
                feuilleRoute.getDocument().getId()
            ); // Journalise l'action de déplacement de l'étape
            if (dossierDoc != null) {
                journalService.journaliserActionFDR(
                    session,
                    dossierDoc,
                    STEventConstant.EVENT_FEUILLE_ROUTE_STEP_MOVE,
                    STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_MOVE
                );
            }
        }
    }

    @Override
    public void saveRouteElement(SpecificContext context, DocumentModel newDocument) {
        // Document has already been created if it has an id.
        // This will avoid creation of many documents if user hit create button
        // too many times.
        if (newDocument.getId() != null) {
            LOG.debug(STLogEnumImpl.LOG_DEBUG_TEC, "Document " + newDocument.getName() + " already created");
            return;
        }
        String parentDocumentPath = (String) newDocument.getContextData().get(CoreEventConstants.PARENT_PATH);
        String sourceDocumentName = (String) newDocument.getContextData().get(SOURCE_DOC_NAME);
        DocumentRef routeDocRef = (DocumentRef) newDocument.getContextData().get(ROUTE_DOCUMENT_REF);
        final CoreSession session = context.getSession();
        DocumentModel documentRouteDoc = session.getDocument(routeDocRef);
        try {
            getDocumentRoutingService()
                .addFeuilleRouteElementToRoute(
                    session,
                    documentRouteDoc,
                    new PathRef(parentDocumentPath),
                    sourceDocumentName,
                    newDocument
                );
        } catch (FeuilleRouteNotLockedException e) {
            context.getMessageQueue().addWarnToQueue(getString("feedback.casemanagement.document.route.not.locked"));
            return;
        }

        context.getMessageQueue().addInfoToQueue(getString("fdr.form.addstep.success"));

        DocumentModel dossierDoc = context.getCurrentDocument();

        // Journalise l'action de modification d'une instance de feuille de route
        if (dossierDoc != null) {
            if (SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(newDocument.getType())) {
                SSRouteStep routeStep = newDocument.getAdapter(SSRouteStep.class);
                SSServiceLocator
                    .getSSJournalService()
                    .journaliserActionEtapeFDR(
                        session,
                        routeStep,
                        dossierDoc,
                        STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE,
                        STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE
                    );
            } else {
                STServiceLocator
                    .getJournalService()
                    .journaliserActionFDR(
                        session,
                        dossierDoc,
                        STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE,
                        STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE
                    );
            }
        }
    }

    @Override
    public void updateRouteElement(SpecificContext context, DocumentModel changeableDocument) {
        final CoreSession session = context.getSession();
        boolean alreadyLockedByCurrentUser = false;
        FeuilleRouteElement docRouteElement = changeableDocument.getAdapter(FeuilleRouteElement.class);
        SSFeuilleRoute route = docRouteElement.getFeuilleRoute(session);
        if (LockUtils.isLockedByCurrentUser(session, route.getDocument().getRef())) {
            alreadyLockedByCurrentUser = true;
        } else {
            if (!lockRoute(context, session, route)) {
                return;
            }
        }
        try {
            validateStepMailbox(session, changeableDocument);
            ServiceUtil.getRequiredService(FeuilleRouteService.class).updateRouteElement(docRouteElement, session);
        } catch (FeuilleRouteNotLockedException e) {
            context
                .getMessageQueue()
                .addWarnToQueue(getString("feedback.casemanagement.document.route.already.locked"));
            return;
        }
        context.getMessageQueue().addInfoToQueue(getString("fdr.form.updatestep.success"));
        // Release the lock only when currentUser had locked it before entering
        // this method.
        if (!alreadyLockedByCurrentUser) {
            getDocumentRoutingService().unlockDocumentRoute(route, session);
        }
    }

    /**
     * Création des étapes de feuilles de routes en série en masse.
     */
    @Override
    public void saveRouteElementSerialMass(SpecificContext context, CreationEtapeDTO etape) {
        SSJournalService journalService = SSServiceLocator.getSSJournalService();
        CoreSession session = context.getSession();

        DocumentModel currentDocument = context.getCurrentDocument();

        FeuilleRouteEtapeOrder order = etape.getTypeAjoutEnum();

        if (order == FeuilleRouteEtapeOrder.AFTER) {
            Collections.reverse(etape.getLines());
        }

        for (CreationEtapeLineDTO changeableDocument : etape.getLines()) {
            DocumentModel newDocument = createAndMapRouteStep(context, changeableDocument, etape.getIdBranche(), order);
            String parentDocumentPath = (String) newDocument.getContextData().get(CoreEventConstants.PARENT_PATH);
            String sourceDocumentName = (String) newDocument.getContextData().get(SOURCE_DOC_NAME);
            DocumentRef routeDocRef = (DocumentRef) newDocument.getContextData().get(ROUTE_DOCUMENT_REF);
            DocumentModel documentRouteDoc = session.getDocument(routeDocRef);
            try {
                validateStepMailbox(session, newDocument);
                DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
                documentRoutingService.addFeuilleRouteElementToRoute(
                    session,
                    documentRouteDoc,
                    new PathRef(parentDocumentPath),
                    sourceDocumentName,
                    newDocument
                );
            } catch (FeuilleRouteNotLockedException e) {
                context
                    .getMessageQueue()
                    .addWarnToQueue(getString("feedback.casemanagement.document.route.not.locked"));
                return;
            }

            // journalisation
            if (currentDocument != null && currentDocument.getType().equals(STConstant.DOSSIER_DOCUMENT_TYPE)) {
                if (SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(newDocument.getType())) {
                    SSRouteStep routeStep = newDocument.getAdapter(SSRouteStep.class);
                    journalService.journaliserActionEtapeFDR(
                        session,
                        routeStep,
                        currentDocument,
                        STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE,
                        STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE
                    );
                } else {
                    journalService.journaliserActionFDR(
                        session,
                        currentDocument,
                        STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE,
                        STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE
                    );
                }
            }
        }

        context.getMessageQueue().addInfoToQueue(getString("fdr.form.addstep.success"));
    }

    protected DocumentModel createAndMapRouteStep(
        SpecificContext context,
        CreationEtapeLineDTO changeableDocument,
        String idBranch,
        FeuilleRouteEtapeOrder order
    ) {
        DocumentModel newDocument = createRouteElement(
            context.getSession(),
            context.getCurrentDocument(),
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
            idBranch,
            order
        );
        MapDoc2Bean.beanToDoc(changeableDocument, newDocument);
        return newDocument;
    }

    /**
     * Permet de s'assurer que le champ distributionMailboxId contient bien le
     * préfixe "poste-" dans le cas par exemple ou le poste est renseigné depuis
     * le widget d'organigramme. De plus, la mailbox poste est créé si elle
     * n'existe pas.
     */
    protected void validateStepMailbox(CoreSession session, DocumentModel routeStepDoc) {
        if (!SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(routeStepDoc.getType())) {
            return;
        }

        MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        SSRouteStep routeStep = routeStepDoc.getAdapter(SSRouteStep.class);
        if (routeStep != null) {
            String mailboxId = routeStep.getDistributionMailboxId();
            if (!mailboxId.contains(SSConstant.MAILBOX_POSTE_ID_PREFIX)) {
                mailboxId = mailboxPosteService.getPosteMailboxId(mailboxId);
                routeStep.setDistributionMailboxId(mailboxId);
            }
            String posteId = mailboxPosteService.getPosteIdFromMailboxId(mailboxId);
            mailboxPosteService.getOrCreateMailboxPoste(session, posteId);
        }
    }

    /**
     * Sauvegarde les étapes de feuille de route en masse stoquees dans
     * lstEtapes. Si serial vaut "true" alors les étapes sont créées en série,
     * sinon elles sont créées en parallele.
     */
    @Override
    public void saveRouteElementMass(SpecificContext context) {
        CreationEtapeDTO etapeDto = context.getFromContextData(SSContextDataKey.CREATION_ETAPE_DTO);
        validateCreationEtapeDTO(context, etapeDto);

        if (etapeDto.getExecutionType() == FeuilleRouteExecutionType.serial) {
            saveRouteElementSerialMass(context, etapeDto);
        } else if (etapeDto.getExecutionType() == FeuilleRouteExecutionType.parallel) {
            saveRouteElementParallelMass(context, etapeDto);
        } else {
            throw new NuxeoException("Type d'éxécution inconnu");
        }
    }

    private void validateCreationEtapeDTO(SpecificContext context, CreationEtapeDTO etapeDto) {
        List<CreationEtapeLineDTO> lines = etapeDto.getLines();
        CoreSession session = context.getSession();
        DocumentModel branch = session.getDocument(new IdRef(etapeDto.getIdBranche()));

        if (
            (
                FeuilleRouteEtapeOrder.IN.equals(etapeDto.getTypeAjoutEnum()) ||
                etapeDto.getExecutionType() == FeuilleRouteExecutionType.serial
            ) &&
            lines.isEmpty() ||
            etapeDto.getExecutionType() == FeuilleRouteExecutionType.parallel &&
            !FeuilleRouteEtapeOrder.IN.equals(etapeDto.getTypeAjoutEnum()) &&
            lines.size() < 2
        ) {
            throw new STValidationException("fdr.form.addstep.insufficientLinesAmount");
        }

        String type = branch.getType();
        if (
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(type) ||
            SSConstant.STEP_FOLDER_DOCUMENT_TYPE.equals(type) ||
            SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE.equals(type)
        ) {
            validateDestinataireEtape(context, lines);
        }

        lines
            .stream()
            .filter(line -> StringUtils.isBlank(line.getTypeEtape()))
            .findFirst()
            .ifPresent(
                s -> {
                    throw new STValidationException("fdr.form.addstep.requiredTypeEtape");
                }
            );

        if (!validationElementType(session, branch)) {
            throw new STValidationException("fdr.form.addstep.unsupportedElement");
        }
    }

    // Context nécessaire pour la surcharge dans EPG
    protected void validateDestinataireEtape(SpecificContext context, List<CreationEtapeLineDTO> lines) {
        lines
            .stream()
            .filter(line -> StringUtils.isBlank(line.getDestinataire()))
            .findFirst()
            .ifPresent(
                s -> {
                    throw new STValidationException("fdr.form.addstep.requiredDestinataire");
                }
            );
    }

    protected boolean validationElementType(CoreSession session, DocumentModel branch) {
        return (
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(branch.getType()) ||
            SSConstant.STEP_FOLDER_DOCUMENT_TYPE.equals(branch.getType()) &&
            FeuilleRouteStepFolderSchemaUtil.getExecutionType(branch) == FeuilleRouteExecutionType.parallel ||
            SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE.equals(branch.getType()) &&
            QueryHelper.isFolderEmpty(session, branch.getId())
        );
    }

    /**
     * Création d'étapes en paralleles en masse
     */
    protected void saveRouteElementParallelMass(SpecificContext context, CreationEtapeDTO etapeDto) {
        DocumentModel currentDocument = context.getCurrentDocument();
        CoreSession session = context.getSession();
        DocumentModel parallelStepFolderDoc;

        try {
            if (FeuilleRouteTypeRef.ETAPE.equals(etapeDto.getTypeRefAjoutEnum())) {
                /* Création du conteneur parallele */
                parallelStepFolderDoc = createBranchParallele(context, etapeDto);
            } else {
                parallelStepFolderDoc = session.getDocument(new IdRef(etapeDto.getIdBranche()));
            }

            SSFeuilleRoute documentRoute = getRelatedRoute(session, parallelStepFolderDoc);
            DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();

            // Pour chaque ligne à ajouter, créer le stepFolder serie et ajouter l'étape dedans
            for (CreationEtapeLineDTO etapeLineDto : etapeDto.getLines()) {
                // Création du conteneur
                DocumentModel serialStepFolderDoc = session.createDocumentModel(SSConstant.STEP_FOLDER_DOCUMENT_TYPE);
                FeuilleRouteStepFolderSchemaUtil.setExecution(serialStepFolderDoc, FeuilleRouteExecutionType.serial);
                serialStepFolderDoc =
                    documentRoutingService.addFeuilleRouteElementToRoute(
                        session,
                        documentRoute.getDocument(),
                        parallelStepFolderDoc.getRef(),
                        null,
                        serialStepFolderDoc
                    );

                DocumentModel serialRouteStepFolderDoc = createAndMapRouteStep(
                    context,
                    etapeLineDto,
                    etapeDto.getIdBranche(),
                    FeuilleRouteEtapeOrder.fromString(etapeDto.getTypeAjout())
                );

                // rajout dans le conteneur
                documentRoutingService.addFeuilleRouteElementToRoute(
                    session,
                    documentRoute.getDocument(),
                    serialStepFolderDoc.getRef(),
                    null,
                    serialRouteStepFolderDoc
                );
            }

            context.getMessageQueue().addInfoToQueue(getString("fdr.form.addstep.success"));

            // Journalise l'action de modification d'une instance de feuille de
            // route
            if (currentDocument != null && currentDocument.getType().equals(STConstant.DOSSIER_DOCUMENT_TYPE)) {
                JournalService journalService = STServiceLocator.getJournalService();
                journalService.journaliserActionFDR(
                    session,
                    currentDocument,
                    STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE,
                    STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE
                );
            }
        } catch (FeuilleRouteNotLockedException e) {
            LOG.error(
                session,
                STLogEnumImpl.LOG_INFO_TEC,
                getString("feedback.casemanagement.document.route.not.locked", e)
            );
            context.getMessageQueue().addErrorToQueue(getString("feedback.casemanagement.document.route.not.locked"));
        }
    }

    /**
     * Détermine si l'utilisateur peut substituer la feuille de route.
     *
     * @return Vrai si on peut substituer la feuille de route
     */
    @Override
    public boolean canUserSubstituerFeuilleRoute(SpecificContext context) {
        /*
         * La substitution est possible seulement si le dossier est verrouillé et que la première étape de la feuille de route est en cours.
         */
        CoreSession session = context.getSession();
        DocumentModel currentDoc = context.getCurrentDocument();
        if (LockUtils.isLockedByCurrentUser(session, currentDoc.getRef())) {
            final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
            final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(currentDoc.getId(), session);
            if (CollectionUtils.isNotEmpty(steps)) {
                final SSRouteStep firstStep = steps.get(0).getAdapter(SSRouteStep.class);
                return firstStep.isRunning();
            }
        }
        return false;
    }

    @Override
    public boolean isStepCopied(SpecificContext context) {
        return CollectionUtils.isNotEmpty(
            UserSessionHelper.getUserSessionParameter(context, COPIED_STEP_SESSION_KEY, List.class)
        );
    }

    @Override
    public int pasteBefore(SpecificContext context) {
        return paste(context, true);
    }

    @Override
    public int pasteAfter(SpecificContext context) {
        return paste(context, false);
    }

    private int paste(SpecificContext context, boolean before) {
        // Récupère l'étape cible
        String stepId = context.getFromContextData(SSContextDataKey.ID_ETAPE);
        if (StringUtils.isEmpty(stepId)) {
            throw new NuxeoException(getString(FEUILLE_ROUTE_ETAPE_ACTION_PASTE_EMPTY_TARGET_ERROR));
        }

        DocumentModel relativeDoc = context.getSession().getDocument(new IdRef(stepId));
        if (relativeDoc == null) {
            throw new NuxeoException(getString(FEUILLE_ROUTE_ETAPE_ACTION_PASTE_TARGET_UNKNOWN_ERROR));
        }

        CoreSession session = context.getSession();
        DocumentModel targetDoc = session.getDocument(relativeDoc.getParentRef());
        if (targetDoc == null) {
            throw new NuxeoException(getString(FEUILLE_ROUTE_ETAPE_ACTION_PASTE_TARGET_PARENT_ERROR));
        }

        // Récupère les étapes à coller
        List<String> stepIds = context.getFromContextData(SSContextDataKey.ROUTE_STEP_IDS);

        List<DocumentModel> docToPasteList = session.getDocuments(stepIds, null);
        if (CollectionUtils.isEmpty(docToPasteList)) {
            throw new NuxeoException(getString(FEUILLE_ROUTE_ETAPE_ACTION_PASTE_EMPTY_SELECTION_ERROR));
        }

        // Duplique les étapes sélectionnées dans le conteneur cible
        DocumentModel currentDocument = context.getCurrentDocument();
        DocumentModel documentRouteDoc = getRelatedRoute(session, currentDocument).getDocument();
        List<DocumentModel> newDocs = getDocumentRoutingService()
            .pasteRouteStepIntoRoute(session, documentRouteDoc, targetDoc, relativeDoc, before, docToPasteList);

        SSJournalService journalService = SSServiceLocator.getSSJournalService();
        // journalisation
        if (currentDocument != null && currentDocument.getType().equals(STConstant.DOSSIER_DOCUMENT_TYPE)) {
            newDocs
                .stream()
                .map(doc -> doc.getAdapter(SSRouteStep.class))
                .forEach(
                    step -> {
                        journalService.journaliserActionEtapeFDR(
                            session,
                            step,
                            currentDocument,
                            STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE,
                            STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE
                        );
                    }
                );
        }

        return newDocs.size();
    }

    /**
     * Création d'un conteneur parallele
     */
    private DocumentModel createBranchParallele(SpecificContext context, CreationEtapeDTO etapeDto) {
        DocumentModel dossierDoc = context.getCurrentDocument();
        CoreSession session = context.getSession();

        /* Création des étapes en parallele */
        DocumentModel parallelStepFolderDoc = createRouteElement(
            session,
            dossierDoc,
            SSConstant.STEP_FOLDER_DOCUMENT_TYPE,
            etapeDto.getIdBranche(),
            etapeDto.getTypeAjoutEnum()
        );
        FeuilleRouteStepFolderSchemaUtil.setExecution(parallelStepFolderDoc, FeuilleRouteExecutionType.parallel);

        String parentDocumentPath = (String) parallelStepFolderDoc.getContextData().get(CoreEventConstants.PARENT_PATH);
        String sourceDocumentName = (String) parallelStepFolderDoc.getContextData().get(SOURCE_DOC_NAME);
        DocumentRef routeDocRef = (DocumentRef) parallelStepFolderDoc.getContextData().get(ROUTE_DOCUMENT_REF);
        DocumentModel documentRouteDoc = session.getDocument(routeDocRef);
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        // Crée le StepFolder conteneur parallèle
        return documentRoutingService.addFeuilleRouteElementToRoute(
            session,
            documentRouteDoc,
            new PathRef(parentDocumentPath),
            sourceDocumentName,
            parallelStepFolderDoc
        );
    }

    @Override
    public boolean isStepInitialisation(SpecificContext context) {
        // Pas d'étape pour initialisation dans réponses
        return false;
    }
}
