package fr.dila.ss.core.service;

import avro.shaded.com.google.common.collect.ImmutableList;
import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.SSFeuilleRouteService;
import fr.dila.ss.api.service.SSJournalService;
import fr.dila.ss.core.flux.EcheanceCalculateur;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement.ElementLifeCycleState;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.adapter.FeuilleRouteStepsContainerImpl;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.impl.LifeCycleFilter;
import org.nuxeo.ecm.core.query.sql.model.DateLiteral;
import org.nuxeo.ecm.core.schema.PrefetchInfo;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Implémentation du service permettant d'effectuer des actions spécifiques sur les instances de feuille de route dans
 * le socle transverse.
 *
 * @author arolin
 * @author jtremeaux
 */
public abstract class SSFeuilleRouteServiceImpl implements SSFeuilleRouteService {
    private static final long serialVersionUID = 3838222748672986516L;

    private static final Log LOG = LogFactory.getLog(SSFeuilleRouteServiceImpl.class);
    private static final STLogger LOGGER = STLogFactory.getLog(SSFeuilleRouteServiceImpl.class);
    private static final String FAIL_VALIDATE_DOSSIER_LINK =
        "[REPARATION CL] - Case Link présent dans un état autre que 'todo' ; validation esquivée pour éviter 'Unable to follow transition' - caseLinkId : ";

    private static final String SELECT_ROUTE_STEP_BY_MAILBOX =
        "select distinct f.ecm:uuid as id from FeuilleRoute as f, FeuilleRouteStep AS r WHERE f.ecm:uuid = r.rtsk:documentRouteId and r.rtsk:distributionMailboxId = ? ";

    /**
     * Default constructor
     */
    public SSFeuilleRouteServiceImpl() {
        // do nothing
    }

    @Override
    public DocumentModel findPreviousStepInFolder(
        final CoreSession session,
        final DocumentModel routeStepDoc,
        final Filter routeStepFilter,
        final boolean includedEtapeParallel
    ) {
        // Charge le conteneur parent de l'étape fournie
        DocumentModel etapeEncours = routeStepDoc;
        DocumentModel containerRouteElementDoc = session.getDocument(routeStepDoc.getParentRef());

        if (includedEtapeParallel) {
            if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(containerRouteElementDoc.getType())) {
                containerRouteElementDoc = session.getDocument(containerRouteElementDoc.getParentRef());
                etapeEncours = containerRouteElementDoc;
                if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(containerRouteElementDoc.getType())) {
                    containerRouteElementDoc = session.getDocument(containerRouteElementDoc.getParentRef());
                }
            }
        } else {
            if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(containerRouteElementDoc.getType())) {
                final StepFolder stepFolder = containerRouteElementDoc.getAdapter(StepFolder.class);
                if (stepFolder.isParallel()) {
                    // Le parent est un conteneur d'éatpes parallèles, remonter n'a pas de sens
                    return null;
                }
            }
        }

        // Récupère toutes les étape du conteneur parent
        final DocumentModelList routeStepList = SSServiceLocator
            .getDocumentRoutingService()
            .getOrderedRouteElement(containerRouteElementDoc.getId(), session);

        // /!\ Renseigne la position de l'étape en cours (effet de bord)
        final int currentStepIndex = routeStepList.indexOf(etapeEncours);
        routeStepDoc.putContextData(STConstant.POS_DOC_CONTEXT, currentStepIndex);

        // Remonte les étapes jusqu'à trouver une étape acceptée
        for (int index = currentStepIndex - 1; index >= 0; --index) {
            final DocumentModel routeElementDoc = routeStepList.get(index);

            // Si le document n'est pas une étape (c'est donc un conteneur), on arrête la recherche
            if (!SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(routeElementDoc.getType())) {
                return null;
            }

            // Vérifie si cette étape est satisfaisante
            if (routeStepFilter == null || routeStepFilter.accept(routeElementDoc)) {
                return routeElementDoc;
            }
        }

        return null;
    }

    /**
     * Méthode qui renvoie la liste des étapes suivantes immédiates à l'étape en cours
     * Ne retourne que les étapes de même "profondeur" que la première étape suivant l'étape en cours (Potentiel Anomalie)
     *
     * @return List<DocumentModel>
     */
    @Override
    public List<DocumentModel> findNextSteps(
        final CoreSession session,
        final String feuilleRouteDocId,
        final DocumentModel currentStepDoc,
        final Filter routeStepFilter
    ) {
        final List<DocumentModel> listNextStepsDoc = new ArrayList<>();

        // Récupère les étapes de feuille de route à plat
        final DocumentModel feuilleRouteDoc = session.getDocument(new IdRef(feuilleRouteDocId));
        final SSFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(SSFeuilleRoute.class);
        final List<RouteTableElement> routeElements = SSServiceLocator
            .getDocumentRoutingService()
            .getFeuilleRouteElements(feuilleRoute, session);

        // Suppression des StepFolder
        final List<RouteTableElement> elementToRemove = new ArrayList<>();
        for (final RouteTableElement routeElement : routeElements) {
            if (routeElement.getElement() instanceof FeuilleRouteStepsContainerImpl) {
                elementToRemove.add(routeElement);
            }
        }
        routeElements.removeAll(elementToRemove);

        // Recherche du step courant dans la feuille de route
        Integer currentIndex = null;
        for (final RouteTableElement routeElement : routeElements) {
            final DocumentModel stepDoc = routeElement.getElement().getDocument();
            if (stepDoc.getId().equals(currentStepDoc.getId())) {
                currentIndex = routeElements.indexOf(routeElement);
            }
        }

        if (currentIndex == null) {
            return listNextStepsDoc;
        }

        if (currentIndex + 1 >= routeElements.size()) {
            return listNextStepsDoc;
        }

        final RouteTableElement currentRouteTableElement = routeElements.get(currentIndex);
        Integer nextIndex = currentIndex + 1;
        RouteTableElement nextRouteTableElement = null;

        do {
            nextRouteTableElement = routeElements.get(nextIndex);
            final DocumentModel stepDoc = nextRouteTableElement.getElement().getDocument();

            if (routeStepFilter == null || routeStepFilter.accept(stepDoc)) {
                // par priorité :
                // 1. même parent et même profondeur (série)
                if (
                    currentRouteTableElement.getDepth() == nextRouteTableElement.getDepth() &&
                    currentStepDoc.getParentRef().equals(stepDoc.getParentRef()) &&
                    listNextStepsDoc.isEmpty()
                ) {
                    listNextStepsDoc.add(stepDoc);
                    break;
                }

                // 2. profondeur suivante < profondeur actuelle (fin de branche parallèle)
                if (
                    currentRouteTableElement.getDepth() > nextRouteTableElement.getDepth() &&
                    stepDoc
                        .getPath()
                        .removeLastSegments(1)
                        .isPrefixOf(currentStepDoc.getPath().removeLastSegments(1)) &&
                    listNextStepsDoc.isEmpty()
                ) {
                    listNextStepsDoc.add(stepDoc);
                    break;
                }

                // 3. profondeur suivante > profondeur actuelle (début de branche parallèle)
                if (
                    currentRouteTableElement.getDepth() < nextRouteTableElement.getDepth() &&
                    currentStepDoc.getPath().removeLastSegments(1).isPrefixOf(stepDoc.getPath().removeLastSegments(1))
                ) {
                    // on ajoute toutes les étapes de parents différents (donc de branches parallèles différentes) mais
                    // de profondeur identique
                    Boolean addStepToList = true;
                    for (final DocumentModel nextStepDoc : listNextStepsDoc) {
                        if (
                            nextStepDoc.getParentRef().equals(stepDoc.getParentRef()) ||
                            !nextStepDoc.getPath().removeLastSegments(2).equals(stepDoc.getPath().removeLastSegments(2))
                        ) {
                            addStepToList = false;
                        }
                    }
                    if (addStepToList) {
                        listNextStepsDoc.add(stepDoc);
                    }
                }
            }
            ++nextIndex;
        } while (nextIndex < routeElements.size());

        return listNextStepsDoc;
    }

    /**
     * Méthode qui renvoie la liste de toutes les étapes de la feuille de route à plat
     *
     * @return List<DocumentModel>
     */
    @Override
    public List<DocumentModel> findAllSteps(CoreSession session, final String feuilleRouteDocId) {
        // Récupère les étapes de feuille de route à plat
        final DocumentModel feuilleRouteDoc = session.getDocument(new IdRef(feuilleRouteDocId));
        final SSFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(SSFeuilleRoute.class);
        final List<RouteTableElement> routeElements = SSServiceLocator
            .getDocumentRoutingService()
            .getFeuilleRouteElements(feuilleRoute, session);

        // On conserve uniquement les étapes de la fdr
        final List<DocumentModel> routeSteps = new ArrayList<>();
        for (final RouteTableElement routeElement : routeElements) {
            if (!(routeElement.getElement() instanceof FeuilleRouteStepsContainerImpl)) {
                routeSteps.add(routeElement.getDocument());
            }
        }
        return routeSteps;
    }

    @Override
    public List<DocumentModel> findAllStepsIndexation(CoreSession session, final String feuilleRouteDocId) {
        // Récupère les étapes de feuille de route à plat
        final DocumentModel feuilleRouteDoc = QueryHelper.getDocument(session, feuilleRouteDocId);
        return QueryHelper.doUFNXQLQueryAndFetchForDocuments(
            session,
            "SELECT d.ecm:uuid as id FROM " +
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE +
            " as d WHERE isChildOf(d.ecm:uuid,select r.ecm:uuid from " +
            SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE +
            " AS r WHERE r.ecm:uuid = ?)=1",
            new String[] { feuilleRouteDoc.getId() },
            0,
            0,
            new PrefetchInfo(STSchemaConstant.DUBLINCORE_SCHEMA + "," + STSchemaConstant.ROUTING_TASK_SCHEMA)
        );
    }

    /**
     * Méthode qui renvoie la liste des étapes suivantes à l'étape en cours
     * Est une copie de la méthode findNextSteps pour la correction du mantis 158720
     *
     * @return List<DocumentModel>
     */
    @Override
    public List<DocumentModel> findAllNextSteps(
        final CoreSession session,
        final String feuilleRouteDocId,
        final DocumentModel currentStepDoc,
        final Filter routeStepFilter
    ) {
        final List<DocumentModel> listNextStepsDoc = new ArrayList<>();

        // Récupère les étapes de feuille de route à plat
        final DocumentModel feuilleRouteDoc = session.getDocument(new IdRef(feuilleRouteDocId));
        final SSFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(SSFeuilleRoute.class);
        final List<RouteTableElement> routeElements = SSServiceLocator
            .getDocumentRoutingService()
            .getFeuilleRouteElements(feuilleRoute, session);

        // Suppression des StepFolder
        final List<RouteTableElement> elementToRemove = new ArrayList<>();
        for (final RouteTableElement routeElement : routeElements) {
            if (routeElement.getElement() instanceof FeuilleRouteStepsContainerImpl) {
                elementToRemove.add(routeElement);
            }
        }
        routeElements.removeAll(elementToRemove);

        // Recherche du step courant dans la feuille de route
        Integer currentIndex = null;
        for (final RouteTableElement routeElement : routeElements) {
            final DocumentModel stepDoc = routeElement.getElement().getDocument();
            if (stepDoc.getId().equals(currentStepDoc.getId())) {
                currentIndex = routeElements.indexOf(routeElement);
            }
        }

        if (currentIndex == null) {
            return listNextStepsDoc;
        }

        if (currentIndex + 1 >= routeElements.size()) {
            return listNextStepsDoc;
        }

        final RouteTableElement currentRouteTableElement = routeElements.get(currentIndex);
        Integer nextIndex = currentIndex + 1;
        RouteTableElement nextRouteTableElement = null;

        do {
            nextRouteTableElement = routeElements.get(nextIndex);
            final DocumentModel stepDoc = nextRouteTableElement.getElement().getDocument();

            if (routeStepFilter == null || routeStepFilter.accept(stepDoc)) {
                // par priorité :
                if ( // 1. même parent et même profondeur (série)
                    currentRouteTableElement.getDepth() == nextRouteTableElement.getDepth() &&
                    currentStepDoc.getParentRef().equals(stepDoc.getParentRef())
                ) {
                    listNextStepsDoc.add(stepDoc);
                } else if ( // 2. profondeur suivante < profondeur actuelle (fin de branche parallèle)
                    currentRouteTableElement.getDepth() > nextRouteTableElement.getDepth() &&
                    stepDoc.getPath().removeLastSegments(1).isPrefixOf(currentStepDoc.getPath().removeLastSegments(1))
                ) {
                    listNextStepsDoc.add(stepDoc);
                } else if ( // 3. profondeur suivante > profondeur actuelle (début de branche parallèle)
                    currentRouteTableElement.getDepth() < nextRouteTableElement.getDepth() &&
                    currentStepDoc.getPath().removeLastSegments(1).isPrefixOf(stepDoc.getPath().removeLastSegments(1))
                ) {
                    // on ajoute toutes les étapes de parents différents (donc de branches parallèles différentes) mais
                    // de profondeur identique
                    Boolean addStepToList = true;
                    for (final DocumentModel nextStepDoc : listNextStepsDoc) {
                        if (nextStepDoc.getParentRef().equals(stepDoc.getParentRef())) {
                            addStepToList = false;
                        }
                    }
                    if (addStepToList) {
                        listNextStepsDoc.add(stepDoc);
                    }
                }
            }
            ++nextIndex;
        } while (nextIndex < routeElements.size());

        return listNextStepsDoc;
    }

    @Override
    public List<DocumentModel> getRunningSteps(final CoreSession session, final String routeInstanceDocId) {
        // Pas de feuille de route, pas d'étape courante ...
        if (routeInstanceDocId == null) {
            return null;
        }
        // Récupération des étapes running de la feuille de route
        final StringBuilder sb = new StringBuilder();
        sb.append(" select s.ecm:uuid as id from RouteStep AS s WHERE s.rtsk:");
        sb.append(SSFeuilleRouteConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY);
        sb.append(" = ? ");
        sb.append("	AND s.");
        sb.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH);
        sb.append(" = 'running' ");

        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            "RouteStep",
            sb.toString(),
            new String[] { routeInstanceDocId }
        );
    }

    @Override
    public boolean isFirstStepInBranchOrParallel(final CoreSession session, final String routingTaskId) {
        // Récupere le step courant
        final DocumentModel currentStepDoc = session.getDocument(new IdRef(routingTaskId));
        final String parentDocId = (String) currentStepDoc.getParentRef().reference();

        final DocumentModel parentRouteElementDoc = session.getParentDocument(currentStepDoc.getRef());
        // on ajoute pas d'étape si on est dans une branche parallèle
        if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(parentRouteElementDoc.getType())) {
            final StepFolder stepFolder = parentRouteElementDoc.getAdapter(StepFolder.class);
            if (stepFolder.isParallel()) {
                // Le parent est un conteneur de tâches parallèles copie de tâche impossible
                return true;
            }
        }

        // récupération des étapes contenues dans parent
        final DocumentModelList steps = SSServiceLocator
            .getDocumentRoutingService()
            .getOrderedRouteElement(parentDocId, session);

        final int currentStepIndex = steps.indexOf(currentStepDoc);

        return currentStepIndex <= 0;
    }

    /**
     * Vérifie si le type est "Pour arbitrage" Si c'est le cas, l'exception est levée et le message d'erreur contient le
     * label à afficher à l'utilisateur Methode Surchargée sur REPONSES
     *
     * @param type
     */
    protected void checkArbitrage(String type) throws SSException {
        // Do nothing
    }

    protected FeuilleRouteStep copyStep(final CoreSession session, final FeuilleRouteStep step) {
        final DocumentModel instanceModel = step.getDocument();
        // Copie l'instance
        final DocumentModel newStepModel = session.createDocumentModel(instanceModel.getType());
        newStepModel.copyContent(instanceModel);
        final SSRouteStep newStep = newStepModel.getAdapter(SSRouteStep.class);

        // on met certaines propriétés à leurs valeurs par défauts
        newStep.setAutomaticValidated(false);
        newStep.setAlreadyDuplicated(false);
        newStep.setValidationStatus(null);
        newStep.setDateDebutEtape(null);
        newStep.setDateFinEtape(null);
        newStep.setDueDate(null);
        newStep.setMailSend(false);
        newStep.setValidationUserId(null);
        newStep.setValidationUserLabel(null);

        // marque l'instance comme ayant déjà servie à une copie
        final SSRouteStep instanceStep = instanceModel.getAdapter(SSRouteStep.class);
        instanceStep.setAlreadyDuplicated(true);
        session.saveDocument(instanceModel);
        return newStep;
    }

    @Override
    public List<DocumentModel> getSteps(final CoreSession session, final DocumentModel dossierDoc) {
        final STDossier dossier = dossierDoc.getAdapter(STDossier.class);
        final String routeInstanceDocId = dossier.getLastDocumentRoute();
        return getSteps(session, routeInstanceDocId);
    }

    @Override
    public List<DocumentModel> getSteps(final CoreSession session, final String routeId) {
        final DocumentRoutingService docRoutingService = SSServiceLocator.getDocumentRoutingService();
        return docRoutingService.getOrderedRouteElement(routeId, session);
    }

    /**
     * Methode pour savoir si le type d'étape de feuille de route "pour réattribution" La valeur par défaut est faux :
     * cette méthode est surchargée dans l'application réponse et est utilisé dans la méthode "addStepAfterReject"
     *
     * @return Boolean
     */
    protected Boolean isTypeEtapeReattribution(final String typeEtape) {
        return false;
    }

    @Override
    public boolean isActiveRouteForPosteId(final CoreSession session, final String posteId) {
        if (posteId == null) {
            throw new NuxeoException("ID Poste null");
        }

        final String mailboxId = SSServiceLocator.getMailboxPosteService().getPosteMailboxId(posteId);

        final String query =
            "select r.ecm:uuid from FeuilleRouteStep AS r WHERE r.rtsk:distributionMailboxId = ? AND isChildOf(r.ecm:uuid, select f.ecm:uuid from FeuilleRoute AS f WHERE f.ecm:currentLifeCycleState in ('running', 'ready', 'validated', 'draft')) = 1";
        final Object[] params = new Object[] { mailboxId };
        IterableQueryResult res = null;
        try {
            res = QueryUtils.doQueryAndFetchPaginatedForUFNXQL(session, query, 1, 0, params);
            final Iterator<Map<String, Serializable>> iterator = res.iterator();
            return iterator.hasNext();
        } finally {
            if (res != null) {
                res.close();
            }
        }
    }

    @Override
    public Collection<DocumentModel> getFeuilleRouteWithActiveOrFutureRouteStepsForPosteId(
        final CoreSession session,
        final String posteId
    ) {
        return getFeuilleRouteWithStepsForPosteIdByLifeCycle(
            session,
            posteId,
            ImmutableList.of(
                ElementLifeCycleState.running,
                ElementLifeCycleState.ready,
                ElementLifeCycleState.validated,
                ElementLifeCycleState.draft
            )
        );
    }

    @Override
    public Collection<DocumentModel> getFeuilleRouteWithActiveOrFutureRouteStepsInInstanceForPosteId(
        final CoreSession session,
        final String posteId
    ) {
        return getFeuilleRouteWithStepsForPosteIdByLifeCycle(
            session,
            posteId,
            ImmutableList.of(ElementLifeCycleState.running, ElementLifeCycleState.ready)
        );
    }

    @Override
    public Collection<DocumentModel> getFeuilleRouteWithStepsInModeleFdrForPosteId(
        final CoreSession session,
        final String posteId
    ) {
        return getFeuilleRouteWithStepsForPosteIdByLifeCycle(
            session,
            posteId,
            ImmutableList.of(ElementLifeCycleState.validated, ElementLifeCycleState.draft)
        );
    }

    private Collection<DocumentModel> getFeuilleRouteWithStepsForPosteIdByLifeCycle(
        final CoreSession session,
        final String posteId,
        Collection<ElementLifeCycleState> lifeCycles
    ) {
        Objects.requireNonNull(posteId, "L'identifiant du poste est null");
        final String mailboxId = SSServiceLocator.getMailboxPosteService().getPosteMailboxId(posteId);

        final String query =
            SELECT_ROUTE_STEP_BY_MAILBOX + "AND r.ecm:currentLifeCycleState in " + convertLifeCycles(lifeCycles);
        final Object[] params = new Object[] { mailboxId };

        final DocumentRef[] refs = QueryUtils.doUFNXQLQueryForIds(session, query, params);
        return session.getDocuments(refs);
    }

    private static String convertLifeCycles(Collection<ElementLifeCycleState> lifeCycles) {
        return lifeCycles
            .stream()
            .map(Enum::name)
            .map(lifecycle -> StringUtils.wrap(lifecycle, "'"))
            .collect(Collectors.joining(",", "(", ")"));
    }

    @Override
    public void updateRouteStepFieldAfterValidation(
        final CoreSession session,
        final SSRouteStep routeStep,
        final List<DocumentModel> dossierDocList,
        final List<STDossierLink> caseLinkList
    ) {
        // Détermine le poste destinataire de l'étape
        final String mailboxId = routeStep.getDistributionMailboxId();
        final String posteId = SSServiceLocator.getMailboxPosteService().getPosteIdFromMailboxId(mailboxId);

        // Renseigne le libellé du poste à la validation de l'étape
        PosteNode posteNode = null;
        try {
            posteNode = STServiceLocator.getSTPostesService().getPoste(posteId);
        } catch (final Exception exc) {
            LOGGER.warn(session, STLogEnumImpl.FAIL_GET_POSTE_TEC, exc);
        }
        if (posteNode != null) {
            routeStep.setPosteLabel(posteNode.getLabel());

            // Renseigne les ministères destinataires de l'étape
            final List<EntiteNode> entiteList = STServiceLocator
                .getSTMinisteresService()
                .getMinistereParentFromPoste(posteId);
            String ministereLabel = "";
            String ministereId = "";
            if (entiteList != null && !entiteList.isEmpty()) {
                final List<String> ministereList = new ArrayList<>();
                final List<String> ministereIdList = new ArrayList<>();
                for (final EntiteNode node : entiteList) {
                    ministereList.add(node.getEdition());
                    ministereIdList.add(node.getId());
                }
                ministereLabel = StringUtils.join(ministereList, ", ");
                ministereId = StringUtils.join(ministereIdList, ", ");
            }
            routeStep.setMinistereLabel(ministereLabel);
            routeStep.setMinistereId(ministereId);

            // Renseigne la direction destinataire de l'étape
            final List<OrganigrammeNode> uniteStructurelleList = STServiceLocator
                .getSTUsAndDirectionService()
                .getDirectionFromPoste(posteId);
            String directionLabel = "";
            if (uniteStructurelleList != null && !uniteStructurelleList.isEmpty()) {
                final List<String> directionList = new ArrayList<>();
                for (final OrganigrammeNode node : uniteStructurelleList) {
                    directionList.add(node.getLabel());
                }
                directionLabel = StringUtils.join(directionList, ", ");
                routeStep.setDirectionId(uniteStructurelleList.get(0).getId());
            }
            routeStep.setDirectionLabel(directionLabel);
        }

        // Renseigne le nom de l'agent qui a validé l'étape
        if (!getTypeEtapeInformation().equals(routeStep.getType()) && routeStep.getValidationStatus() != null) {
            final NuxeoPrincipal principal = session.getPrincipal();
            String userId = principal.getOriginatingUser();
            if (userId == null) {
                userId = principal.getName();
            }
            final String userFullName = STServiceLocator.getSTUserService().getUserFullNameAndCivilite(userId);
            routeStep.setValidationUserLabel(userFullName);
            routeStep.setValidationUserId(userId);
        }

        // note : la date de fin doit être définie lorsque l'étape est terminée pas lorsqu'elle est running

        updateApplicationFieldsAfterValidation(session, routeStep.getDocument(), dossierDocList, caseLinkList);

        routeStep.save(session);
    }

    @Override
    public void doAutomaticValidationBatch(
        final CoreSession session,
        final BatchLoggerModel batchLoggerModel,
        Long nbError
    ) {
        final long now = Calendar.getInstance().getTimeInMillis();
        final String dateLiteral = DateLiteral.dateTimeFormatter.print(now);
        final SSJournalService journalService = SSServiceLocator.getSSJournalService();
        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        long nbDossiersValides = 0;
        /*
         * Si automaticValidation = 1 et qu'une date est renseignée : - Envoyer un Mail -Valider automatiquement le
         * dossier - Mettre à jour les metas de l'étape validée, pour signaler que cette étape a été validée
         * automatiquement
         */

        // cas 1 : on valide automatiquement les documents dont l'option validation automatique a été sélectionnée, dont
        // l'échéance est passé et que l'on n'a pas déjà validé.
        long startTime = Calendar.getInstance().getTimeInMillis();
        String query = String.format(getDossierLinkListToValidateQuery(), dateLiteral);
        DocumentModelList dueDossierLinks = session.query(query);
        LOGGER.info(
            session,
            STLogEnumImpl.VALIDATE_DOSSIER_TEC,
            "Dossier à valider automatiquement : " + dueDossierLinks.size()
        );
        for (final DocumentModel dossierLinkdoc : dueDossierLinks) {
            LOGGER.info(
                session,
                STLogEnumImpl.VALIDATE_DOSSIER_TEC,
                "Debut de validation automatique du Dossier : " + dossierLinkdoc.getTitle()
            );
            try {
                // si le dossierLink est verrouillé, on le déverrouille
                if (dossierLinkdoc.isLocked()) {
                    session.removeLock(dossierLinkdoc.getRef());
                }
                // si le dossier est verrouillé, on le déverrouille
                final STDossierLink dossierLink = dossierLinkdoc.getAdapter(STDossierLink.class);
                final DocumentModel dossierDoc = dossierLink.getDossier(session, STDossier.class).getDocument();
                if (dossierDoc.isLocked()) {
                    session.removeLock(dossierDoc.getRef());
                }

                final ActionableCaseLink acl = dossierLinkdoc.getAdapter(ActionableCaseLink.class);
                final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
                // récupère l'étape courante du dossier lié au dossier link
                final DocumentModel etapeDoc = documentRoutingService.getFeuilleRouteStep(session, acl);
                final SSRouteStep ssRouteStep = etapeDoc.getAdapter(SSRouteStep.class);
                // maj des informations de l'étape de feuille de route
                ssRouteStep.setAutomaticValidated(true);
                ssRouteStep.setValidationStatus(
                    SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_VALIDE_AUTOMATIQUEMENT_VALUE
                );
                ssRouteStep.save(session);
                // validation de l'étape
                if (acl.isActionnable() && acl.isTodo()) {
                    acl.validate(session);
                } else {
                    LOGGER.warn(session, STLogEnumImpl.FAIL_VALIDATE_DL_TEC, FAIL_VALIDATE_DOSSIER_LINK + acl.getId());
                }
                LOGGER.info(
                    session,
                    STLogEnumImpl.VALIDATE_DOSSIER_TEC,
                    "Dossier validé automatiquement : " + dossierDoc.getTitle()
                );
                // Ajout de log dans le journal pour préciser l'avis favorable
                journalService.journaliserActionEtapeFDR(
                    session,
                    ssRouteStep,
                    dossierDoc,
                    STEventConstant.DOSSIER_AVIS_FAVORABLE,
                    STEventConstant.COMMENT_AVIS_FAVORABLE
                );
                session.save();
                TransactionHelper.commitOrRollbackTransaction();
                TransactionHelper.startTransaction();
                ++nbDossiersValides;
            } catch (final Exception e) {
                // note : on récupère l'erreur afin que l'échec d'un dossier ne bloque pas les autres
                LOGGER.error(
                    session,
                    STLogEnumImpl.FAIL_VALIDATE_DL_TEC,
                    "Erreur lors de la validation automatique du DossierLink " + dossierLinkdoc.getId(),
                    e
                );
                ++nbError;
            }
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        suiviBatchService.createBatchResultFor(
            batchLoggerModel,
            "Nombre de validation automatique de Dossier",
            nbDossiersValides,
            endTime - startTime
        );

        /*
         * Si automaticValidation = 0 et qu'une date est renseignée : - Envoyer un Mail - Passer une méta à 1, pour
         * signaler que le mail a été envoyé (et ne pas le renvoyer plusieurs fois)
         */

        // cas 2 : on valide automatiquement les documents dont l'option validation automatique n'a pas été
        // sélectionnée, dont l'échéance est passé et que l'on n'a pas déjà validé.
        startTime = Calendar.getInstance().getTimeInMillis();
        nbDossiersValides = 0;
        query = String.format(getDossierLinkListToEmailForAutomaticValidationQuery(), dateLiteral);
        dueDossierLinks = session.query(query);
        LOG.info("Notification à envoyer suite au dépassement d'échéance : " + dueDossierLinks.size());
        for (final DocumentModel dossierLinkdoc : dueDossierLinks) {
            LOG.info("Debut de notification suite au dépassement d'échéance du Dossier : " + dossierLinkdoc.getTitle());
            try {
                // si le dossierLink est verrouillé, on le déverrouille
                if (dossierLinkdoc.isLocked()) {
                    session.removeLock(dossierLinkdoc.getRef());
                }

                // si le dossier est verrouillé, on le déverrouille
                final STDossierLink dossierLink = dossierLinkdoc.getAdapter(STDossierLink.class);
                final DocumentModel dossierDoc = dossierLink.getDossier(session, STDossier.class).getDocument();
                if (dossierDoc.isLocked()) {
                    session.removeLock(dossierDoc.getRef());
                }

                final ActionableCaseLink acl = dossierLinkdoc.getAdapter(ActionableCaseLink.class);

                // envoi d'un mail au poste de l'étape de feuille de route courante pour signaler que l'échéance est
                // atteinte
                sendMailInfoEcheanceAtteinte(session, acl);

                final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
                // récupère l'étape courante du dossier lié au dossier link
                final DocumentModel etapeDoc = documentRoutingService.getFeuilleRouteStep(session, acl);
                final SSRouteStep ssRouteStep = etapeDoc.getAdapter(SSRouteStep.class);
                // maj des informations de l'étape de feuille de route
                ssRouteStep.setAutomaticValidated(false);
                // pas de mise à jour du statut
                // stRouteStep.setValidationStatus(STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_FAVORABLE_AVEC_CORRECTION_VALUE);
                ssRouteStep.save(session);

                // maj des informations du dossier link
                dossierLink.setCurrentStepIsMailSendProperty(true);
                dossierLink.save(session);
                session.save();
                TransactionHelper.commitOrRollbackTransaction();
                TransactionHelper.startTransaction();
                LOG.info("Dossier notifié automatiquement : " + dossierDoc.getTitle());
                ++nbDossiersValides;
            } catch (final Exception e) {
                // note : on récupère l'erreur afin que l'échec d'un dossier ne bloque pas les autres
                LOGGER.error(
                    session,
                    STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                    "Erreur lors de l'envoi de mail suite au dépassement de l'échéance du DossierLink " +
                    dossierLinkdoc.getId(),
                    e
                );
                ++nbError;
            }
        }
        endTime = Calendar.getInstance().getTimeInMillis();
        suiviBatchService.createBatchResultFor(
            batchLoggerModel,
            "Nombre de notification suite au dépassement d'échéance de Dossier",
            nbDossiersValides,
            endTime - startTime
        );
    }

    /**
     * Requête récupérant les dossiers link contenant les étapes de fdr à valider automatiquement cad : l'état du
     * dossier link permet sa validation (le dossierLink n'est pas déjà validé ) l'option validation automatique est
     * sélectionné, la date d'échéance est dépassé
     */
    protected String getDossierLinkListToValidateQuery() {
        final StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE);
        query.append(" WHERE ");
        query.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH);
        query.append(" = 'todo' AND  ");
        query.append(STSchemaConstant.ACTIONABLE_CASE_LINK_SCHEMA_PREFIX);
        query.append(":");
        query.append(STSchemaConstant.ACTIONABLE_CASE_LINK_AUTOMATIC_VALIDATION_PROPERTY);
        query.append(" = 1 AND ");
        query.append(STSchemaConstant.ACTIONABLE_CASE_LINK_SCHEMA_PREFIX);
        query.append(":");
        query.append(STSchemaConstant.ACTIONABLE_CASE_LINK_DUE_DATE_PROPERTY);
        query.append(" < TIMESTAMP '%s' ");
        query.append(" and ecm:isProxy = 0 ");

        return query.toString();
    }

    /**
     * Envoi d'un mail de notification aux utilisateurs concernés par le dépassement de délai (les utilisateurs
     * concernés sont ceux qui sont affectés aux postes destinataires de l'étape courante de la feuille de route).
     */
    protected void sendMailInfoEcheanceAtteinte(final CoreSession session, final ActionableCaseLink acl) {
        try {
            // récupère l'étape courante du dossier lié au dossier link
            final DocumentModel etapeDoc = SSServiceLocator
                .getDocumentRoutingService()
                .getFeuilleRouteStep(session, acl);
            final SSRouteStep etapeCourante = etapeDoc.getAdapter(SSRouteStep.class);

            // note : optimiser la récupération des utilisateurs
            // on récupère le poste des destinataires
            final String mailboxId = etapeCourante.getDistributionMailboxId();
            if (mailboxId == null) {
                return;
            }
            final String posteId = SSServiceLocator.getMailboxPosteService().getPosteIdFromMailboxId(mailboxId);
            if (posteId == null) {
                return;
            }

            final PosteNode posteNode = STServiceLocator.getSTPostesService().getPoste(posteId);

            final List<STUser> userList = posteNode.getUserList();
            if (CollectionUtils.isEmpty(userList)) {
                LOGGER.info(session, STLogEnumImpl.FAIL_GET_USER_TEC, "Le poste ne contient pas d'utilisateur !");
                return;
            }

            List<String> mailUserList = userList
                .stream()
                .map(STUser::getEmail)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

            final STParametreService paramService = STServiceLocator.getSTParametreService();

            final String message = paramService.getParametreValue(
                session,
                STParametreConstant.MAIL_EXPIRATION_NO_VAL_AUTOMATIQUE_TEXT
            );

            final String objet = paramService.getParametreValue(
                session,
                STParametreConstant.MAIL_EXPIRATION_NO_VAL_AUTOMATIQUE_OBJET
            );

            Map<String, Object> params = getMailTemplateParameters(acl);

            // envoi du mail
            STServiceLocator.getSTMailService().sendTemplateMail(mailUserList, objet, message, params);
        } catch (final Exception e) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                "Erreur lors de l'envoi de mail pour la validation automatique",
                e
            );
        }
    }

    protected abstract Map<String, Object> getMailTemplateParameters(ActionableCaseLink acl);

    @Override
    public void calculEcheanceFeuilleRoute(final CoreSession session, final STDossier dossier) {
        // on récupére la date de début de échéances
        final Calendar dateDebutEcheance = getDateDebutEcheance(session, dossier);
        if (dateDebutEcheance == null) {
            return;
        }
        final Calendar dateDebutEcheanceTemp = Calendar.getInstance();
        dateDebutEcheanceTemp.setTime(dateDebutEcheance.getTime());

        // on récupère les étapes de la feuille de route
        if (!dossier.hasFeuilleRoute()) {
            return;
        }
        final String idFeuilleRoute = dossier.getLastDocumentRoute();
        final DocumentModelList childDocModelList = session.getChildren(new IdRef(idFeuilleRoute));
        if (CollectionUtils.isEmpty(childDocModelList)) {
            return;
        }
        for (final DocumentModel etapeDoc : childDocModelList) {
            // calcul de l'échéance prévisionnelle et opérationnelle
            calculEcheancePrevisionnelleEtOperationnelle(session, etapeDoc, dateDebutEcheanceTemp);
        }
    }

    @Override
    public void deleteNextSteps(final CoreSession session, final String routeId) {
        final DocumentRoutingService docRoutingService = SSServiceLocator.getDocumentRoutingService();
        // Récupère toutes les étape du conteneur parent
        final DocumentModelList routeStepList = docRoutingService.getOrderedRouteElement(routeId, session);
        boolean runningStepVisited = false;
        for (DocumentModel routeStep : routeStepList) {
            if (runningStepVisited && !STLifeCycleConstant.RUNNING_STATE.equals(routeStep.getCurrentLifeCycleState())) {
                // On supprime les étapes et documents qui ne sont pas en running si on a déjà passé les étapes en
                // running
                docRoutingService.softDeleteStep(session, routeStep);
            } else {
                if (STLifeCycleConstant.RUNNING_STATE.equals(routeStep.getCurrentLifeCycleState())) {
                    runningStepVisited = true;
                }
            }
        }
        session.save();
    }

    @Override
    public boolean hasMoreThanOneStep(final CoreSession session, final String routeId) {
        return getSteps(session, routeId).size() > 1;
    }

    /**
     * Mise à jour du délai prévisonnel et opérationnel pour les étapes d'un step_folder de type parallèle et mise à
     * jour de la nouvelle date de début.
     *
     * @param session
     * @param etapeParallelDoc
     * @param dateDebut
     */
    protected Calendar calculDateEcheanceEtapeParallele(
        final CoreSession session,
        final DocumentModel etapeParallelDoc,
        Calendar dateDebut
    ) {
        final DocumentModelList childEtapeParallelModelList = session.getChildren(etapeParallelDoc.getRef());
        if (CollectionUtils.isEmpty(childEtapeParallelModelList)) {
            return dateDebut;
        }
        // on récupère la date de début d'échéance
        final Calendar newDateDebut = Calendar.getInstance();
        newDateDebut.setTime(dateDebut.getTime());
        LOG.debug("entre etape parallele :" + dateDebut.getTime());
        // on parcourt les éléments de l'étape parallèle
        for (final DocumentModel etapeDoc : childEtapeParallelModelList) {
            // initialisation de la date de début temporaire
            final Calendar dateDebutTemp = Calendar.getInstance();
            dateDebutTemp.setTime(newDateDebut.getTime());
            // calcul de l'échéance prévisionnelle
            calculEcheancePrevisionnelleEtOperationnelle(session, etapeDoc, dateDebutTemp);
            // on prend la nouvelle date de debut comme référence si elle est supérieur à la date de debut actuelle
            LOG.debug(dateDebutTemp.getTime() + " > " + dateDebut.getTime());
            if (dateDebutTemp.compareTo(dateDebut) > 0) {
                dateDebut = dateDebutTemp;
            }
        }
        LOG.debug("sortie etape parallele :" + dateDebut.getTime());
        return dateDebut;
    }

    /**
     * Mise à jour du délai prévisonnelle pour les étapes d'un step_folder de type serial et mise à jour de la nouvelle
     * date de début.
     *
     * @param session
     * @param etapeSerialDoc
     * @param dateDebutEcheance
     */
    protected void calculDateEcheanceEtapeSerial(
        final CoreSession session,
        final DocumentModel etapeSerialDoc,
        final Calendar dateDebutEcheance
    ) {
        final DocumentModelList childEtapeParallelModelList = session.getChildren(etapeSerialDoc.getRef());
        if (CollectionUtils.isEmpty(childEtapeParallelModelList)) {
            return;
        }
        LOG.debug("entre etape serie :" + dateDebutEcheance.getTime());
        // on parcourt les éléments de l'étape serial
        for (final DocumentModel etapeDoc : childEtapeParallelModelList) {
            // calcul de l'échéance prévisionnelle et opérationnelle
            calculEcheancePrevisionnelleEtOperationnelle(session, etapeDoc, dateDebutEcheance);
        }
        LOG.debug("sortie etape serie :" + dateDebutEcheance.getTime());
    }

    /**
     * Mise à jour du délai prévisionnel et de la nouvelle date de début en fonction du type d'étape.
     *
     * @param session
     * @param etapeDoc
     * @param dateDebutEcheance
     */
    protected void calculEcheancePrevisionnelleEtOperationnelle(
        final CoreSession session,
        final DocumentModel etapeDoc,
        Calendar dateDebutEcheance
    ) {
        if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(etapeDoc.getType())) {
            final StepFolder stepFolder = etapeDoc.getAdapter(StepFolder.class);
            if (stepFolder.isParallel()) {
                dateDebutEcheance.setTime(
                    calculDateEcheanceEtapeParallele(session, etapeDoc, dateDebutEcheance).getTime()
                );
                LOG.debug("newdate parallel:" + dateDebutEcheance);
            } else if (stepFolder.isSerial()) {
                calculDateEcheanceEtapeSerial(session, etapeDoc, dateDebutEcheance);
            }
        } else if (SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(etapeDoc.getType())) {
            final SSRouteStep etapeRepDoc = etapeDoc.getAdapter(SSRouteStep.class);
            if (etapeRepDoc == null) {
                return;
            }
            // si l'étape est terminée, on prend sa date de fin comme date de début d'échéance
            if (etapeRepDoc.getDateFinEtape() != null) {
                dateDebutEcheance.setTime(etapeRepDoc.getDateFinEtape().getTime());
                return;
            }
            // si l'étape n'a pas de délai, on passe à l'étape suivante
            if (etapeRepDoc.getDeadLine() == null && !etapeRepDoc.isAutomaticValidation()) {
                return;
            }
            // sinon on calcule d'échéance prévisionnelle de l'étape
            if (etapeRepDoc.getDeadLine() != null) {
                dateDebutEcheance =
                    EcheanceCalculateur.getEcheanceCompteUniquementJourOuvre(
                        dateDebutEcheance,
                        etapeRepDoc.getDeadLine().intValue()
                    );
            }
            final Date date = dateDebutEcheance.getTime();
            LOG.debug("newdate :" + date);
            // on fixe la date de fin prévisonnelle
            etapeRepDoc.setDueDate(DateUtil.toCalendarFromNotNullDate(date));
            etapeRepDoc.save(session);
        }
    }

    private Stream<StepFolder> streamStepFolders(RouteTableElement docRouteTableElement) {
        return docRouteTableElement
            .getFirstChildFolders()
            .stream()
            .map(r -> r.getRouteElement().getDocument().getAdapter(StepFolder.class));
    }

    private boolean hasStepFolders(RouteTableElement docRouteTableElement) {
        return streamStepFolders(docRouteTableElement).findFirst().isPresent();
    }

    @Override
    public boolean hasStepFolders(List<RouteTableElement> routeTableElementList) {
        return routeTableElementList.stream().anyMatch(this::hasStepFolders);
    }

    @Override
    public List<StepFolder> getStepFolders(RouteTableElement docRouteTableElement) {
        return streamStepFolders(docRouteTableElement).collect(Collectors.toList());
    }

    @Override
    public List<String> getEtapesAVenir(CoreSession session, String feuilleRouteId) {
        List<String> etapesAVenir = new ArrayList<>();
        Set<DocumentModel> nextSteps = new LinkedHashSet<>();
        List<DocumentModel> runningSteps = getRunningSteps(session, feuilleRouteId);

        if (runningSteps != null) {
            for (DocumentModel routeStepDoc : runningSteps) {
                nextSteps.addAll(
                    findNextSteps(
                        session,
                        feuilleRouteId,
                        routeStepDoc,
                        new LifeCycleFilter(ElementLifeCycleState.deleted.name(), false)
                    )
                );
            }

            if (!nextSteps.isEmpty()) {
                for (DocumentModel nextStep : nextSteps) {
                    if (!nextStep.isFolder()) {
                        etapesAVenir.add(getLabelForStep(nextStep));
                    }
                }
            }
        }
        return etapesAVenir;
    }

    protected abstract String getLabelForStep(DocumentModel stepDoc);

    @Override
    public String getCurrentStep(CoreSession session, STDossierLink dossierLink) {
        DocumentModel stepDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
        return getLabelForStep(stepDoc);
    }

    @Override
    public Date getLastStepDate(CoreSession session, String feuilleRouteId) {
        List<DocumentModel> steps = findAllSteps(session, feuilleRouteId);
        List<Date> dates = steps
            .stream()
            .map(doc -> doc.getAdapter(SSRouteStep.class))
            .map(SSRouteStep::getDateFinEtape)
            .filter(Objects::nonNull)
            .map(Calendar::getTime)
            .collect(Collectors.toList());

        if (!dates.isEmpty()) {
            return Collections.max(dates);
        } else {
            return null;
        }
    }
}
