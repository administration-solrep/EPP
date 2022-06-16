package fr.dila.ss.core.service;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.DossierDistributionService;
import fr.dila.ss.api.service.SSJournalService;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Implémentation du service de distribution des dossiers du socle transverse.
 *
 * @author jtremeaux
 */
public abstract class DossierDistributionServiceImpl extends DefaultComponent implements DossierDistributionService {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -7744835777616091844L;

    private static final STLogger LOGGER = STLogFactory.getLog(DossierDistributionServiceImpl.class);

    private static final String GET_LAST_DOCUMENT_ROUTE_QUERY =
        "SELECT * FROM Dossier WHERE " +
        STSchemaConstant.DOSSIER_SCHEMA_PREFIX +
        ":" +
        STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY +
        " = '%s'";

    protected static final String WARN_DL_VALIDATION =
        "[REPARATION CL] - Case Link présent dans un état autre que 'todo' ; validation esquivée pour éviter 'Unable to follow transition' - caseLinkId : ";

    /**
     * Default constructor
     */
    public DossierDistributionServiceImpl() {
        super();
    }

    @Override
    public DocumentModel getLastDocumentRouteForDossier(CoreSession session, DocumentModel dossierDoc) {
        STDossier dossier = dossierDoc.getAdapter(STDossier.class);
        String routeInstanceDocId = dossier.getLastDocumentRoute();

        // Charge l'instance de feuille de route
        return session.getDocument(new IdRef(routeInstanceDocId));
    }

    @Override
    public List<DocumentModel> getDossierRoutes(CoreSession session, DocumentModel dossierDoc) {
        StringBuilder sb = new StringBuilder("SELECT * FROM FeuilleRoute WHERE froutinst:attachDocumentIds = '");
        sb.append(dossierDoc.getId());
        sb.append("' and ecm:isProxy = 0 ");
        sb.append(" ORDER BY dc:modified ASC ");
        return session.query(sb.toString());
    }

    @Override
    public DocumentModel substituerFeuilleRouteAndUnlockFdr(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel oldRouteInstanceDoc,
        DocumentModel newRouteModelDoc,
        String typeCreation
    ) {
        DocumentModel newRouteDoc = substituerFeuilleRoute(
            session,
            dossierDoc,
            oldRouteInstanceDoc,
            newRouteModelDoc,
            typeCreation
        );
        SSFeuilleRoute newFeuilleRoute = newRouteDoc.getAdapter(SSFeuilleRoute.class);
        SSServiceLocator.getDocumentRoutingService().unlockDocumentRoute(newFeuilleRoute, session);
        return newRouteDoc;
    }

    @Override
    public DocumentModel substituerFeuilleRoute(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel oldRouteInstanceDoc,
        DocumentModel newRouteModelDoc,
        String typeCreation
    ) {
        DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();

        // Annule l'ancienne feuille de route
        List<String> docIds = new ArrayList<>();
        if (oldRouteInstanceDoc == null) {
            docIds.add(dossierDoc.getId());
        } else {
            FeuilleRoute oldRouteInstance = oldRouteInstanceDoc.getAdapter(FeuilleRoute.class);
            docIds.addAll(oldRouteInstance.getAttachedDocuments());
        }
        // Crée la nouvelle instance de feuille de route (l'instance n'est pas encore démarrée)
        DocumentModel newRouteInstanceDoc = documentRoutingService.createNewInstance(session, newRouteModelDoc, docIds);

        deleteForbiddenSteps(session, dossierDoc, newRouteInstanceDoc);

        if (oldRouteInstanceDoc != null) {
            SSFeuilleRoute newFeuilleRoute = newRouteInstanceDoc.getAdapter(SSFeuilleRoute.class);

            DocumentModel firstStepDoc = session.getChildren(newRouteInstanceDoc.getRef()).get(0);
            documentRoutingService.lockDocumentRoute(newFeuilleRoute, session);

            // On récupère les étapes de l'ancienne fdr
            List<DocumentModel> stepsOldFdr = documentRoutingService.getOrderedRouteElement(
                oldRouteInstanceDoc.getId(),
                session
            );

            for (DocumentModel etapeDoc : stepsOldFdr) {
                if (FeuilleRouteElement.ElementLifeCycleState.done.name().equals(etapeDoc.getCurrentLifeCycleState())) {
                    DocumentModel newStepDoc = session.copy(
                        etapeDoc.getRef(),
                        newRouteInstanceDoc.getRef(),
                        etapeDoc.getName()
                    );
                    session.orderBefore(newRouteInstanceDoc.getRef(), newStepDoc.getName(), firstStepDoc.getName());
                }
            }

            List<DocumentModel> resultListDL = STServiceLocator
                .getCorbeilleService()
                .findDossierLinkUnrestricted(session, dossierDoc.getId());

            for (DocumentModel dossierLinkDoc : resultListDL) {
                session.followTransition(dossierLinkDoc.getRef(), STLifeCycleConstant.TO_DONE_TRANSITION);
            }

            FeuilleRoute oldRouteInstance = oldRouteInstanceDoc.getAdapter(FeuilleRoute.class);
            oldRouteInstance.setAttachedDocuments(new ArrayList<>());
            oldRouteInstance.save(session);
            oldRouteInstance.cancel(session);
        }

        // Lance l'évenement de substitution
        fireSubstitutionFeuilleDeRouteEvent(session, typeCreation, oldRouteInstanceDoc, newRouteInstanceDoc);
        if (oldRouteInstanceDoc != null) {
            session.followTransition(oldRouteInstanceDoc.getRef(), STLifeCycleConstant.TO_DELETE_TRANSITION);
        }

        return newRouteInstanceDoc;
    }

    /**
     * Supprime les étapes interdites dans une instance de feuille de route Méthode surchargée sur EPG pour les textes
     * non publiés au JO
     */
    protected void deleteForbiddenSteps(CoreSession session, DocumentModel dossierDoc, DocumentModel routeInstanceDoc) {
        return;
    }

    /**
     * Lève un événement de substitution de feuille de route (démarre la feuille de route). Cet evenement est appelé en
     * cas de réattribution de feuille de route, mais également dans le cadre d'une réattribution.
     */
    protected void fireSubstitutionFeuilleDeRouteEvent(
        CoreSession session,
        String typeCreation,
        DocumentModel oldRouteInstanceDoc,
        DocumentModel newRouteInstanceDoc
    ) {
        EventProducer eventProducer = STServiceLocator.getEventProducer();
        Map<String, Serializable> eventProperties = new HashMap<>();
        eventProperties.put(STEventConstant.DOSSIER_DISTRIBUTION_SUBSTITUTION_ROUTE_TYPE_EVENT_PARAM, typeCreation);
        eventProperties.put(STEventConstant.DOSSIER_DISTRIBUTION_OLD_ROUTE_EVENT_PARAM, oldRouteInstanceDoc);
        eventProperties.put(STEventConstant.DOSSIER_DISTRIBUTION_NEW_ROUTE_EVENT_PARAM, newRouteInstanceDoc);
        InlineEventContext inlineEventContext = new InlineEventContext(
            session,
            session.getPrincipal(),
            eventProperties
        );
        eventProducer.fireEvent(inlineEventContext.newEvent(STEventConstant.AFTER_SUBSTITUTION_FEUILLE_ROUTE));
    }

    @Override
    public void validerEtape(CoreSession session, DocumentModel dossierDoc, DocumentModel dossierLinkDoc) {
        // Met à jour l'étape en cours avec l'avis favorable
        STDossierLink dossierLink = dossierLinkDoc.getAdapter(STDossierLink.class);
        DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
        validerEtape(session, dossierDoc, dossierLinkDoc, etapeDoc);
    }

    /**
     * Permet de valider l'étape passée en paramètre (avis favorable). Le dossier link est validé et une entrée est faite dans le journal technique
     */
    protected void validerEtape(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel dossierLinkDoc,
        DocumentModel stepDoc
    ) {
        SSRouteStep etape = stepDoc.getAdapter(SSRouteStep.class);
        LOGGER.info(session, SSLogEnumImpl.UPDATE_STEP_TEC, "Avis favorable pour l'étape <" + stepDoc.getId() + ">");

        updateStepValidationStatus(
            session,
            stepDoc,
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE,
            dossierDoc
        );

        // Valide le DossierLink
        ActionableCaseLink acl = dossierLinkDoc.getAdapter(ActionableCaseLink.class);
        if (acl.isActionnable() && acl.isTodo()) {
            acl.validate(session);
        } else {
            LOGGER.debug(session, STLogEnumImpl.FAIL_VALIDATE_DL_TEC, dossierDoc, WARN_DL_VALIDATION + acl.getId());
            LOGGER.warn(session, STLogEnumImpl.FAIL_VALIDATE_DL_TEC, WARN_DL_VALIDATION + acl.getId());
        }

        SSJournalService journalService = SSServiceLocator.getSSJournalService();
        // Journalise l'action
        journalService.journaliserActionEtapeFDR(
            session,
            etape,
            dossierDoc,
            STEventConstant.DOSSIER_AVIS_FAVORABLE,
            STEventConstant.COMMENT_AVIS_FAVORABLE
        );
    }

    @Override
    public void validerEtapeRefus(CoreSession session, DocumentModel dossierDoc, DocumentModel dossierLinkDoc) {
        SSJournalService journalService = SSServiceLocator.getSSJournalService();

        LOGGER.info(
            session,
            SSLogEnumImpl.UPDATE_STEP_TEC,
            "Avis défavorable pour l'étape <" + dossierLinkDoc.getName() + ">"
        );

        // Met à jour l'étape en cours avec l'avis défavorable
        STDossierLink dossierLink = dossierLinkDoc.getAdapter(STDossierLink.class);
        DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
        SSRouteStep etape = etapeDoc.getAdapter(SSRouteStep.class);
        updateStepValidationStatus(
            session,
            etapeDoc,
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_DEFAVORABLE_VALUE,
            dossierDoc
        );

        // Valide le DossierLink
        ActionableCaseLink acl = dossierLinkDoc.getAdapter(ActionableCaseLink.class);
        acl.refuse(session);

        // Journalise l'action
        journalService.journaliserActionEtapeFDR(
            session,
            etape,
            dossierDoc,
            STEventConstant.DOSSIER_AVIS_DEFAVORABLE,
            STEventConstant.COMMENT_AVIS_DEFAVORABLE
        );
    }

    @Override
    public void rejeterDossierLink(CoreSession session, DocumentModel dossierDoc, DocumentModel dossierLinkDoc) {
        SSJournalService journalService = SSServiceLocator.getSSJournalService();

        LOGGER.info(session, SSLogEnumImpl.UPDATE_STEP_TEC, "Refus du DossierLink <" + dossierLinkDoc.getName() + ">");

        // Met à jour l'étape en cours avec l'avis défavorable
        STDossierLink dossierLink = dossierLinkDoc.getAdapter(STDossierLink.class);
        DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
        SSRouteStep etape = etapeDoc.getAdapter(SSRouteStep.class);
        updateStepValidationStatus(
            session,
            etapeDoc,
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_DEFAVORABLE_VALUE,
            dossierDoc
        );

        // Refuse le DossierLink
        ActionableCaseLink acl = dossierLinkDoc.getAdapter(ActionableCaseLink.class);
        acl.refuse(session);

        // Journalise l'action
        journalService.journaliserActionEtapeFDR(
            session,
            etape,
            dossierDoc,
            STEventConstant.DOSSIER_AVIS_DEFAVORABLE,
            STEventConstant.COMMENT_AVIS_DEFAVORABLE
        );
    }

    @Override
    public void validerEtapeNonConcerne(CoreSession session, DocumentModel dossierDoc, DocumentModel dossierLinkDoc) {
        SSJournalService journalService = SSServiceLocator.getSSJournalService();

        LOGGER.info(
            session,
            SSLogEnumImpl.UPDATE_STEP_TEC,
            "Non concerné pour l'étape <" + dossierLinkDoc.getName() + ">"
        );

        // Met à jour l'étape en cours avec l'avis non concerné
        STDossierLink dossierLink = dossierLinkDoc.getAdapter(STDossierLink.class);
        DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
        SSRouteStep etape = etapeDoc.getAdapter(SSRouteStep.class);
        updateStepValidationStatus(
            session,
            etapeDoc,
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_NON_CONCERNE_VALUE,
            dossierDoc
        );

        // Valide le DossierLink
        ActionableCaseLink acl = dossierLinkDoc.getAdapter(ActionableCaseLink.class);
        if (acl.isActionnable() && acl.isTodo()) {
            acl.validate(session);
        } else {
            LOGGER.debug(session, STLogEnumImpl.FAIL_VALIDATE_DL_TEC, dossierDoc, WARN_DL_VALIDATION + acl.getId());
            LOGGER.warn(session, STLogEnumImpl.FAIL_VALIDATE_DL_TEC, WARN_DL_VALIDATION + acl.getId());
        }

        // Journalise l'action
        journalService.journaliserActionEtapeFDR(
            session,
            etape,
            dossierDoc,
            STEventConstant.DOSSIER_VALIDER_NON_CONCERNE_EVENT,
            STEventConstant.DOSSIER_VALIDER_NON_CONCERNE_COMMENT_PARAM
        );
    }

    /**
     * Met l'étape courante à l'état passé en param
     */
    @Override
    public void updateStepValidationStatus(
        CoreSession session,
        DocumentModel etapeDoc,
        String validationStatus,
        DocumentModel dossierDoc
    ) {
        // Cas de la validation automatique d'étape lors du changement d'état de question par web services. On tombe
        // parfois sur des conteneurs parallèles,
        // il faut donc les visiter pour changer les statuts des étapes qu'ils contiennent
        if (SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(etapeDoc.getType())) {
            SSRouteStep etape = etapeDoc.getAdapter(SSRouteStep.class);
            etape.setAutomaticValidated(false);
            etape.setValidationStatus(validationStatus);
            session.saveDocument(etape.getDocument());
        } else if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(etapeDoc.getType())) {
            DocumentModelList dml = session.getChildren(etapeDoc.getRef());
            for (DocumentModel dm : dml) {
                updateStepValidationStatus(session, dm, validationStatus, dossierDoc);
            }
        }
    }

    @Override
    public void restartDossier(CoreSession session, DocumentModel dossierDoc) {
        LOGGER.info(session, STLogEnumImpl.UPDATE_DOSSIER_TEC, "Redémarrage du dossier <" + dossierDoc.getName() + ">");

        // Vérifie que le dossier est à l'état terminé
        if (!dossierDoc.getCurrentLifeCycleState().equals(STDossier.DossierState.done.name())) {
            throw new SSException("Le dossier doit être à l'état done");
        }

        // Vérifie que la feuille de route est à l'état terminé
        DocumentModel routeInstanceDoc = getLastDocumentRouteForDossier(session, dossierDoc);
        if (!routeInstanceDoc.getCurrentLifeCycleState().equals(STDossier.DossierState.done.name())) {
            throw new SSException("La feuille de route doit être à l'état done");
        }

        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                dossierDoc.followTransition(STDossier.DossierTransition.backToRunning.name());

                // Redémarre la feuille de route
                SSFeuilleRoute feuilleRouteInstance = routeInstanceDoc.getAdapter(SSFeuilleRoute.class);
                feuilleRouteInstance.backToReady(session);

                ServiceUtil.getRequiredService(EventService.class).waitForAsyncCompletion();
                feuilleRouteInstance.run(session);
            }
        }
        .runUnrestricted();
    }

    /**
     * valider une etape pour la reprise
     */
    @Override
    public void validerEtapePourReprise(CoreSession session, DocumentModel dossierLinkDoc) {
        // Met à jour l'étape en cours avec l'avis favorable
        STDossierLink dossierLink = dossierLinkDoc.getAdapter(STDossierLink.class);
        DocumentModel dossierDoc = dossierLink.getDossier(session, STDossier.class).getDocument();
        DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
        updateStepValidationStatus(
            session,
            etapeDoc,
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE,
            dossierDoc
        );

        // Valide le DossierLink
        ActionableCaseLink acl = dossierLinkDoc.getAdapter(ActionableCaseLink.class);
        if (acl.isActionnable() && acl.isTodo()) {
            acl.validate(session);
        } else {
            LOGGER.debug(session, STLogEnumImpl.FAIL_VALIDATE_DL_TEC, dossierDoc, WARN_DL_VALIDATION + acl.getId());
            LOGGER.warn(session, STLogEnumImpl.FAIL_VALIDATE_DL_TEC, WARN_DL_VALIDATION + acl.getId());
        }
    }

    @Override
    public DocumentModel getDossierFromDocumentRouteId(CoreSession session, String documentRouteId) {
        Objects.requireNonNull(documentRouteId, "documentRouteId null");
        Objects.requireNonNull(session, "La session ne peut pas etre nulle");

        return session
            .query(String.format(GET_LAST_DOCUMENT_ROUTE_QUERY, documentRouteId))
            .stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new NuxeoException("Aucun dossier ne correspond à l'id de la feuille de route : " + documentRouteId)
            );
    }
}
