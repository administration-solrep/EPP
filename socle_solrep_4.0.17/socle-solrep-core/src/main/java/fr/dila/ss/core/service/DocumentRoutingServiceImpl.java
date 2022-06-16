package fr.dila.ss.core.service;

import static fr.dila.st.core.util.ResourceHelper.getString;
import static fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.criteria.RouteStepCriteria;
import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.api.feuilleroute.DocumentRouteTableElement;
import fr.dila.ss.api.feuilleroute.DocumentRouteTreeElement;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.core.schema.RoutingTaskSchemaUtils;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.PermissionHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteEvent;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteNotLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteFolderElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTable;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRouteDisplayService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRouteService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.EventFirer;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteStepFolderSchemaUtil;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.pathsegment.PathSegmentService;
import org.nuxeo.ecm.core.schema.PrefetchInfo;
import org.nuxeo.runtime.api.Framework;

/**
 * Implémentation du service de document routing du socle SOLREP, étend et remplace celui de Nuxeo.
 *
 * @author jtremeaux
 */
public abstract class DocumentRoutingServiceImpl implements DocumentRoutingService {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOG = STLogFactory.getLog(DocumentRoutingServiceImpl.class);

    private static final String ORDERED_CHILDREN_QUERY =
        "SELECT * FROM Document WHERE ecm:parentId = '%s' AND ecm:isCheckedInVersion  = 0 AND " +
        "ecm:mixinType != 'HiddenInNavigation' AND ecm:currentLifeCycleState != 'deleted' and ecm:isProxy = 0 ORDER BY ecm:pos";

    /**
     * Nom des documents conteneur d'étape série.
     */
    protected static final String DOCUMENT_NAME_SERIE = "serie";

    /**
     * Nom des documents conteneur d'étape parallèle.
     */
    protected static final String DOCUMENT_NAME_PARALLELE = "parallele";

    @Override
    public void setRouteStepDocName(final DocumentModel routeStepDoc) {
        // Renseigne le titre avec le type d'étape
        final SSRouteStep routeStep = routeStepDoc.getAdapter(SSRouteStep.class);
        final String routingTaskType = routeStep.getType();
        final String distributionMailboxId = routeStep.getDistributionMailboxId();
        final String title = String.format("etape_%s_%s", routingTaskType, distributionMailboxId);
        routeStepDoc.setPathInfo(routeStepDoc.getPathAsString(), title);
    }

    @Override
    public DocumentModel getFeuilleRouteStep(final CoreSession session, final ActionableCaseLink acl) {
        final FeuilleRouteStep routeStep = acl.getDocumentRouteStep(session);
        if (routeStep == null) {
            throw new NuxeoException(
                "Impossible de trouver l'étape de feuille de route associée au CaseLink <" + acl.getId() + ">"
            );
        }
        return routeStep.getDocument();
    }

    /*
     * Surcharge Nuxeo : recherche la dernière instance de feuille à partir du dossier. Retourne une liste de feuilles
     * de route pour rester compatible avec Nuxeo.
     */
    @Override
    public SSFeuilleRoute getDocumentRouteForDossier(final CoreSession session, final String dossierDocId) {
        if (dossierDocId != null) {
            try {
                // Charge le dossier
                final DocumentModel dossierDoc = session.getDocument(new IdRef(dossierDocId));
                if (dossierDoc.hasFacet(SSConstant.ROUTABLE_FACET)) {
                    final STDossier dossier = dossierDoc.getAdapter(STDossier.class);
                    final String routeInstanceDocId = dossier.getLastDocumentRoute();

                    if (routeInstanceDocId != null) {
                        // Charge l'instance de feuille de route
                        final DocumentModel routeDoc = session.getDocument(new IdRef(routeInstanceDocId));
                        return routeDoc.getAdapter(SSFeuilleRoute.class);
                    }
                }
            } catch (final NuxeoException e) {
                // Gestion d'erreurs "à la Nuxeo" pour respecter la signature de cette méthode
                LOG.debug(session, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC, e);
            }
        }

        return null;
    }

    @Override
    public SSFeuilleRoute requestValidateRouteModel(
        final SSFeuilleRoute routeModel,
        final CoreSession session,
        final boolean demandeValidation
    ) {
        if (!LockUtils.isLockedByCurrentUser(session, routeModel.getDocument().getRef())) {
            throw new FeuilleRouteNotLockedException();
        }
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                final SSFeuilleRoute route = session
                    .getDocument(routeModel.getDocument().getRef())
                    .getAdapter(SSFeuilleRoute.class);
                if (demandeValidation) {
                    EventFirer.fireEvent(
                        session,
                        route.getDocument(),
                        null,
                        STEventConstant.BEFORE_REQUEST_VALIDATE_FEUILLE_ROUTE
                    );
                } else {
                    EventFirer.fireEvent(
                        session,
                        route.getDocument(),
                        null,
                        STEventConstant.BEFORE_CANCEL_REQUEST_VALIDATE_FEUILLE_ROUTE
                    );
                }
                route.setDemandeValidation(demandeValidation);
                session.saveDocument(route.getDocument());
                session.save();
                if (demandeValidation) {
                    EventFirer.fireEvent(
                        session,
                        route.getDocument(),
                        null,
                        STEventConstant.AFTER_REQUEST_VALIDATE_FEUILLE_ROUTE
                    );
                } else {
                    EventFirer.fireEvent(
                        session,
                        route.getDocument(),
                        null,
                        STEventConstant.AFTER_CANCEL_REQUEST_VALIDATE_FEUILLE_ROUTE
                    );
                }
            }
        }
        .runUnrestricted();

        return session.getDocument(routeModel.getDocument().getRef()).getAdapter(SSFeuilleRoute.class);
    }

    @Override
    public void sendValidationMail(final CoreSession session, final SSFeuilleRoute routeModel) {
        // Détermine l'objet et le corps du mail
        final STParametreService parametreService = STServiceLocator.getSTParametreService();
        final String mailObjet = parametreService.getParametreValue(
            session,
            STParametreConstant.NOTIFICATION_MAIL_VALIDATION_FEUILLE_ROUTE_OBJET
        );
        final String mailTexte = parametreService.getParametreValue(
            session,
            STParametreConstant.NOTIFICATION_MAIL_VALIDATION_FEUILLE_ROUTE_TEXT
        );

        // Détermine les destinataires
        final ProfileService profileService = STServiceLocator.getProfileService();
        final List<STUser> users = profileService.getUsersFromBaseFunction(
            STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR
        );

        final List<String> idsFdr = new ArrayList<>();
        idsFdr.add(routeModel.getDocument().getId());

        // Envoi de l'email
        final STMailService mailService = STServiceLocator.getSTMailService();
        mailService.sendHtmlMailToUserListWithLinkToDossiers(session, users, mailObjet, mailTexte, idsFdr);
    }

    @Override
    public SSFeuilleRoute invalidateRouteModel(final SSFeuilleRoute routeModel, final CoreSession session) {
        if (!LockUtils.isLockedByCurrentUser(session, routeModel.getDocument().getRef())) {
            throw new FeuilleRouteNotLockedException();
        }
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                EventFirer.fireEvent(
                    session,
                    routeModel.getDocument(),
                    null,
                    STEventConstant.BEFORE_INVALIDATE_FEUILLE_ROUTE
                );
                routeModel.followTransition(FeuilleRouteElement.ElementLifeCycleTransistion.toDraft, session, true);
                EventFirer.fireEvent(
                    session,
                    routeModel.getDocument(),
                    null,
                    STEventConstant.AFTER_INVALIDATE_FEUILLE_ROUTE
                );
            }
        }
        .runUnrestricted();

        routeModel.getDocument().detach(false);
        routeModel.getDocument().attach(session.getSessionId());

        return routeModel.getDocument().getAdapter(SSFeuilleRoute.class);
    }

    @Override
    public SSFeuilleRoute invalidateRouteModelNotUnrestricted(
        final SSFeuilleRoute routeModel,
        final CoreSession session
    ) {
        if (!LockUtils.isLockedByCurrentUser(session, routeModel.getDocument().getRef())) {
            throw new FeuilleRouteNotLockedException();
        }

        EventFirer.fireEvent(session, routeModel.getDocument(), null, STEventConstant.BEFORE_INVALIDATE_FEUILLE_ROUTE);
        routeModel.followTransition(FeuilleRouteElement.ElementLifeCycleTransistion.toDraft, session, false);
        EventFirer.fireEvent(session, routeModel.getDocument(), null, STEventConstant.AFTER_INVALIDATE_FEUILLE_ROUTE);

        return routeModel;
    }

    @Override
    public SSFeuilleRoute validateRouteModel(final SSFeuilleRoute routeModel, final CoreSession session) {
        if (!LockUtils.isLockedByCurrentUser(session, routeModel.getDocument().getRef())) {
            throw new FeuilleRouteNotLockedException();
        }

        checkAndMakeAllStateStepDraft(session, routeModel);

        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                final SSFeuilleRoute route = session
                    .getDocument(routeModel.getDocument().getRef())
                    .getAdapter(SSFeuilleRoute.class);
                EventFirer.fireEvent(session, route.getDocument(), null, FeuilleRouteEvent.beforeRouteValidated.name());

                // Valide la feuille de route
                route.setValidated(session);
                // Annule la demande de validation
                route.setDemandeValidation(false);
                session.saveDocument(route.getDocument());
                session.save();
                EventFirer.fireEvent(session, route.getDocument(), null, FeuilleRouteEvent.afterRouteValidated.name());
            }
        }
        .runUnrestricted();
        return session.getDocument(routeModel.getDocument().getRef()).getAdapter(SSFeuilleRoute.class);
    }

    @Override
    public void checkAndMakeAllStateStepDraft(final CoreSession session, final SSFeuilleRoute route) {
        final DocumentModel routeModel = route.getDocument();
        final DocumentModelList steps = session.getChildren(routeModel.getRef());
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                makeDraftStepsIfNot(session, steps);
            }
        }
        .runUnrestricted();
    }

    private void makeDraftStepsIfNot(final CoreSession session, final DocumentModelList steps) {
        final Iterator<DocumentModel> stepIterator = steps.iterator();
        DocumentModel document;
        while (stepIterator.hasNext()) {
            document = stepIterator.next();
            if (document.isFolder()) {
                // Dans le cas d'un dossier, on est dans le cas d'étape parallèles donc on doit aller plus loin
                makeDraftStepsIfNot(session, session.getChildren(document.getRef()));
            } else {
                final FeuilleRouteStep step = document.getAdapter(FeuilleRouteStep.class);
                if (!step.isDraft()) {
                    step.followTransition(FeuilleRouteElement.ElementLifeCycleTransistion.toDraft, session, false);
                }
            }
        }
    }

    @Override
    public SSFeuilleRoute duplicateRouteModel(
        final CoreSession session,
        final DocumentModel routeToCopyDoc,
        final String ministere
    ) {
        // Copie le modèle de feuille de route
        final SSFeuilleRoute routeToCopy = routeToCopyDoc.getAdapter(SSFeuilleRoute.class);
        final String newTitle = routeToCopy.getName() + " (Copie)";
        String escapedTitle = newTitle.replace('/', '-');
        escapedTitle = escapedTitle.replace('\\', '-');
        DocumentModel newFeuilleRouteDoc = session.copy(
            routeToCopyDoc.getRef(),
            routeToCopyDoc.getParentRef(),
            escapedTitle
        );

        final SSFeuilleRoute newFeuilleRoute = newFeuilleRouteDoc.getAdapter(SSFeuilleRoute.class);
        lockDocumentRoute(newFeuilleRoute, session);

        // Si le modèle copié était validé, invalide le nouveau modèle
        if (newFeuilleRoute.isValidated()) {
            invalidateRouteModel(newFeuilleRoute, session);
        }

        // Redonne le bon titre au modèle de feuille de route
        newFeuilleRouteDoc.setPropertyValue("dc:title", newTitle);

        // Réinitialise l'état de la feuille de route
        newFeuilleRoute.setTitle(newTitle);
        newFeuilleRoute.setCreator(session.getPrincipal().getName());
        newFeuilleRoute.setDemandeValidation(false);
        newFeuilleRoute.setFeuilleRouteDefaut(false);
        if (StringUtils.isNotEmpty(ministere) && StringUtils.isEmpty(newFeuilleRoute.getMinistere())) {
            newFeuilleRoute.setMinistere(ministere);
        }

        newFeuilleRouteDoc = session.saveDocument(newFeuilleRouteDoc);
        session.save();

        // Initialise les étapes de la nouvelle feuille de route
        initFeuilleRouteStep(session, newFeuilleRouteDoc);

        // Si un ministère est fourni et que la feuille de route est non affecté
        unlockDocumentRoute(newFeuilleRoute, session);

        return newFeuilleRouteDoc.getAdapter(SSFeuilleRoute.class);
    }

    /**
     * Initialise les étapes d'une feuille de route après la duplication. Les étapes sont réattachée à la nouvelle
     * feuille de route (champ dénormalisé).
     *
     * @param session
     *            Session
     * @param feuilleRouteDoc
     *            Document feuille de route
     */
    @Override
    public void initFeuilleRouteStep(final CoreSession session, final DocumentModel feuilleRouteDoc) {
        final RouteStepCriteria routeStepCriteria = new RouteStepCriteria();
        routeStepCriteria.setDocumentRouteId(feuilleRouteDoc.getId());

        final List<DocumentModel> routeStepDocList = findRouteStepByCriteria(session, routeStepCriteria);

        for (final DocumentModel routeStepDoc : routeStepDocList) {
            final SSRouteStep routeStep = routeStepDoc.getAdapter(SSRouteStep.class);
            routeStep.setDocumentRouteId(feuilleRouteDoc.getId());
            routeStep.setCreator(session.getPrincipal().getName());
            session.saveDocument(routeStepDoc);
        }
    }

    @Override
    public DocumentModel getDocumentBefore(final CoreSession session, final String parentId, final String elementId) {
        final String query = String.format(ORDERED_CHILDREN_QUERY, parentId);
        final DocumentModelList orderedChildren = session.query(query);
        DocumentModel docBefore = null;
        for (final DocumentModel doc : orderedChildren) {
            if (doc.getId().equals(elementId)) {
                return docBefore;
            }
            docBefore = doc;
        }
        return docBefore;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RouteTableElement> getFeuilleRouteElements(final SSFeuilleRoute route, final CoreSession session) {
        FeuilleRouteDisplayService fdrService = ServiceUtil.getRequiredService(FeuilleRouteDisplayService.class);
        List<? extends RouteTableElement> routeElements = fdrService.getRouteElements(route, session);
        return (List<RouteTableElement>) routeElements;
    }

    /**
     * Construit un tableau contenant les étapes d'une feuille de route.
     *
     * @param doc
     *            Document parent (conteneur d'étapes)
     * @param elements
     *            Tableau d'éléments modifié par effet de bord
     * @param table
     *            Table
     * @param session
     *            Session
     * @param depth
     *            Niveau
     * @param folder
     *            Folder
     */
    protected void getDocumentRouteTableElementInFolder(
        final DocumentModel doc,
        final List<DocumentRouteTableElement> elements,
        final RouteTable table,
        final CoreSession session,
        final int depth,
        final RouteFolderElement folder
    ) {
        final List<DocumentModel> children = getNotDeletedChildren(session, doc.getRef());
        getDocumentRouteTableElementInFolder(elements, table, session, depth, folder, children);
    }

    /**
     * construction recursive
     *
     * @param doc
     * @param elements
     * @param table
     * @param session
     * @param depth
     * @param folder
     */
    protected void getDocumentRouteTableElementInFolder(
        final List<DocumentRouteTableElement> elements,
        final RouteTable table,
        final CoreSession session,
        final int depth,
        final RouteFolderElement folder,
        final List<DocumentModel> children
    ) {
        boolean first = true;
        for (final DocumentModel child : children) {
            final FeuilleRouteElement childElement = child.getAdapter(FeuilleRouteElement.class);
            List<DocumentModel> subchildren = null;
            if (child.isFolder()) {
                subchildren = getNotDeletedChildren(session, child.getRef());
            }
            if (subchildren != null && !subchildren.isEmpty()) {
                final RouteFolderElement thisFolder = new RouteFolderElement(childElement, table, first, folder, depth);
                getDocumentRouteTableElementInFolder(elements, table, session, depth + 1, thisFolder, subchildren);
            } else {
                if (folder != null) {
                    folder.increaseTotalChildCount();
                } else {
                    table.increaseTotalChildCount();
                }

                // Ajoute une ligne correspondant à l'étape
                elements.add(new DocumentRouteTableElement(childElement, table, depth, folder, first));
            }
            first = false;
        }
    }

    /**
     * Construit un tableau contenant les étapes d'une feuille de route. Ne prend pas en compte les dossiers vides.
     *
     * @param session
     *            Session
     * @param element
     *            Noeud de l'arbre
     */
    protected void getDocumentRouteTreeElementInFolder(
        final CoreSession session,
        final DocumentRouteTreeElement element
    ) {
        final DocumentModel folderDoc = element.getDocument();
        final List<DocumentModel> childrenDocs = getNotDeletedChildren(session, folderDoc.getRef());
        getDocumentRouteTreeElementInFolder(session, element, childrenDocs);
    }

    /**
     * Construit récursivement un tableau contenant les étapes d'une feuille de route. Ne prend pas en compte les
     * dossiers vides.
     *
     * @param session
     *            Session
     * @param element
     *            Noeud de l'arbre
     */
    private void getDocumentRouteTreeElementInFolder(
        final CoreSession session,
        final DocumentRouteTreeElement element,
        final List<DocumentModel> childrenDocs
    ) {
        for (final DocumentModel child : childrenDocs) {
            final DocumentRouteTreeElement childrenElement = new DocumentRouteTreeElement(child, element);
            element.getChildrenList().add(childrenElement);

            if (child.isFolder()) {
                final List<DocumentModel> subChildrenDocs = getNotDeletedChildren(session, child.getRef());
                if (!subChildrenDocs.isEmpty()) {
                    getDocumentRouteTreeElementInFolder(session, childrenElement, subChildrenDocs);
                }
            }
        }
    }

    /**
     * Retourne la liste des enfants d'un document qui ne sont pas supprimés
     *
     * @return
     */
    protected List<DocumentModel> getNotDeletedChildren(final CoreSession session, final DocumentRef docRef) {
        final List<DocumentModel> notdeleted = new ArrayList<>();
        final DocumentModelList children = session.getChildren(docRef);
        for (final DocumentModel child : children) {
            if (!STLifeCycleConstant.DELETED_STATE.equals(child.getCurrentLifeCycleState())) {
                notdeleted.add(child);
            }
        }
        return notdeleted;
    }

    @Override
    public List<DocumentModel> pasteRouteStepIntoRoute(
        final CoreSession session,
        final DocumentModel documentRouteDoc,
        final DocumentModel parent,
        DocumentModel relativeDocument,
        boolean before,
        final List<DocumentModel> documents
    ) {
        // Vérifie les paramètres
        if (documentRouteDoc == null) {
            throw new IllegalArgumentException("Document route document must be defined");
        }

        if (parent == null) {
            throw new IllegalArgumentException("Parent destination must be defined");
        }

        if (documents == null) {
            throw new IllegalArgumentException("Documents to paste must be defined");
        }

        // Vérifie si la route destination est verrouillée
        final FeuilleRoute parentRoute = getParentRouteModel(parent.getRef(), session);
        if (!LockUtils.isLockedByCurrentUser(session, parentRoute.getDocument().getRef())) {
            throw new FeuilleRouteNotLockedException();
        }

        // Conserve uniquement les documents autorisés dans ce conteneur
        if (documents.isEmpty()) {
            return documents;
        }

        // Charge l'arborescence complete des documents à copier
        // On suppose que tous les éléments sont du meme arbre (pas de vérification, trop couteux)
        final DocumentRouteTreeElement sourceTree = getDocumentRouteTree(session, documents.get(0));

        // Marque les documents à coller et leurs parents
        final Set<String> documentToPasteIdSet = new HashSet<>();
        for (final DocumentModel doc : documents) {
            documentToPasteIdSet.add(doc.getId());
        }
        markElementToPaste(sourceTree, documentToPasteIdSet);

        // Construit des nouveaux arbres avec uniquement les éléments à copier
        final List<DocumentRouteTreeElement> destTreeList = pruneDocumentRouteTree(sourceTree, parent);

        // Duplique les documents du nouvel arbre récursivement
        for (final DocumentRouteTreeElement routeTree : destTreeList) {
            LOG.info(
                STLogEnumImpl.LOG_INFO_TEC,
                "Collage de l'élements relativement au document " +
                (relativeDocument != null ? relativeDocument.getName() : "-") +
                " before=" +
                before
            );
            relativeDocument =
                pasteRouteTreeIntoRoute(session, documentRouteDoc, routeTree, parent, relativeDocument, before);
            before = false;
        }

        return documents;
    }

    /**
     * Duplique une arborescence de feuille de route dans une autre arborescence.
     *
     * @param session
     *            Session
     * @param documentRouteDoc
     *            Document feuille de route
     * @param routeTree
     *            Arborescence à dupliquer
     * @param parent
     *            Document model container cible (identique fdr ou container de la feuille de route)
     * @param relativeDocument
     *            Insertion relative à ce document (frère), peut être nul
     * @param before
     *            Insertion avant le document (sinon après), non pris en compte si relativeDocument est nul
     * @return Racine des documents dupliqués
     */
    protected DocumentModel pasteRouteTreeIntoRoute(
        final CoreSession session,
        final DocumentModel documentRouteDoc,
        final DocumentRouteTreeElement routeTree,
        final DocumentModel containerCibleDoc,
        final DocumentModel relativeDocument,
        final boolean before
    ) {
        final DocumentModel containerDoc = routeTree.getDocument();
        if (routeTree.getDocument().isFolder()) {
            DocumentModel newContainerDoc = getContainerDoc(containerDoc, containerCibleDoc, session);
            newContainerDoc =
                addRouteElementToRoute(session, containerCibleDoc.getRef(), relativeDocument, before, newContainerDoc);
            for (final DocumentRouteTreeElement children : routeTree.getChildrenList()) {
                pasteRouteTreeIntoRoute(session, documentRouteDoc, children, newContainerDoc, null, false);
            }
            return newContainerDoc;
        } else {
            return copyRouteElementIntoRoute(
                session,
                documentRouteDoc,
                containerCibleDoc,
                relativeDocument,
                before,
                containerDoc
            );
        }
    }

    /**
     * Crée un nouveau conteneur pour le coller dans l'arbre. Si le conteneur d'origine est une feuille de route, le
     * document cible sera un conteneur série.
     *
     * @param sourceContainer
     *            Conteneur à copier
     * @param sourceContainer
     *            Conteneur cible (du conteneur copié)
     * @return Nouveau conteneur
     */
    protected DocumentModel getContainerDoc(
        final DocumentModel sourceContainer,
        final DocumentModel destContainer,
        CoreSession session
    ) {
        DocumentModel newContainerDoc = session.createDocumentModel(
            destContainer.getPathAsString(),
            sourceContainer.getName(),
            TYPE_FEUILLE_ROUTE_STEP_FOLDER
        );

        final String executionType = FeuilleRouteStepFolderSchemaUtil.getExecution(sourceContainer);
        FeuilleRouteStepFolderSchemaUtil.setExecution(newContainerDoc, executionType);

        return newContainerDoc;
    }

    @Override
    public DocumentModel addFeuilleRouteElementToRoute(
        final CoreSession session,
        final DocumentModel documentRouteDoc,
        final DocumentRef parentDocumentRef,
        final String sourceName,
        DocumentModel routeElementDoc
    ) {
        if (!LockUtils.isLockedByCurrentUser(session, documentRouteDoc.getRef())) {
            throw new FeuilleRouteNotLockedException();
        }

        // Renseigne l'UUID de la feuille de route dans l'étape (champ dénormalisé)
        String docName = null;
        final PathSegmentService pathSegmentService = STServiceLocator.getPathSegmentService();
        final DocumentModel parentDocument = session.getDocument(parentDocumentRef);
        if (SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(routeElementDoc.getType())) {
            final SSRouteStep routeStep = routeElementDoc.getAdapter(SSRouteStep.class);
            routeStep.setDocumentRouteId(documentRouteDoc.getId());
            docName = pathSegmentService.generatePathSegment(routeElementDoc);
        } else if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(routeElementDoc.getType())) {
            final StepFolder stepFolder = routeElementDoc.getAdapter(StepFolder.class);
            if (stepFolder.isParallel()) {
                docName = DOCUMENT_NAME_PARALLELE;
            } else {
                docName = DOCUMENT_NAME_SERIE;
            }
        }
        routeElementDoc.setPathInfo(parentDocument.getPathAsString(), docName);
        final String lifecycleState = parentDocument
                .getCurrentLifeCycleState()
                .equals(FeuilleRouteElement.ElementLifeCycleState.draft.name())
            ? FeuilleRouteElement.ElementLifeCycleState.draft.name()
            : FeuilleRouteElement.ElementLifeCycleState.ready.name();
        routeElementDoc.putContextData(LifeCycleConstants.INITIAL_LIFECYCLE_STATE_OPTION_NAME, lifecycleState);

        routeElementDoc = session.createDocument(routeElementDoc);
        session.orderBefore(parentDocumentRef, routeElementDoc.getName(), sourceName);
        session.save();

        if (LOG.isInfoEnable()) {
            LOG.info(
                STLogEnumImpl.FAIL_LOG_TEC,
                "Création dans le conteneur " +
                parentDocument.getName() +
                " de l'élément " +
                routeElementDoc.getName() +
                " " +
                " avant l'élément " +
                sourceName
            );
        }

        return routeElementDoc;
    }

    @Override
    public void addRouteElementToRoute(
        final DocumentRef parentDocumentRef,
        final int idx,
        final FeuilleRouteElement routeElement,
        final CoreSession session
    ) {
        final FeuilleRoute route = getParentRouteModel(parentDocumentRef, session);
        if (!LockUtils.isLockedByCurrentUser(session, route.getDocument().getRef())) {
            throw new FeuilleRouteNotLockedException();
        }

        // Renseigne l'UUID de la feuille de route dans l'étape (champ dénormalisé)
        if (SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(routeElement.getDocument().getType())) {
            final SSRouteStep routeStep = routeElement.getDocument().getAdapter(SSRouteStep.class);
            routeStep.setDocumentRouteId(route.getDocument().getId());
        }

        FeuilleRouteService routeService = ServiceUtil.getRequiredService(FeuilleRouteService.class);
        routeService.addRouteElementToRoute(parentDocumentRef, idx, routeElement, session);
    }

    private FeuilleRoute getParentRouteModel(final DocumentRef documentRef, final CoreSession session) {
        final DocumentModel parentDoc = session.getDocument(documentRef);
        if (parentDoc.hasFacet(FeuilleRouteConstant.FACET_FEUILLE_ROUTE)) {
            return parentDoc.getAdapter(FeuilleRoute.class);
        }
        final FeuilleRouteElement rElement = parentDoc.getAdapter(FeuilleRouteElement.class);
        return rElement.getFeuilleRoute(session);
    }

    /**
     * Ajout d'un élément dans une feuille de route à l'emplacement indiqué.
     *
     * @param session
     *            Session
     * @param parentDocumentRef
     *            Ref. du document parent
     * @param relativeDocument
     *            Insertion relative à ce document (frère), peut être nul
     * @param before
     *            Insertion avant le document (sinon après), non pris en compte si relativeDocument est nul
     * @param doc
     *            Document à insérer
     * @return Document nouvellement créé
     *
     */
    protected DocumentModel addRouteElementToRoute(
        final CoreSession session,
        final DocumentRef parentDocumentRef,
        final DocumentModel relativeDocument,
        final boolean before,
        DocumentModel doc
    ) {
        final PathSegmentService pss = STServiceLocator.getPathSegmentService();

        // Réinitialise l'étape nouvellement créée
        final DocumentModel parentDocument = session.getDocument(parentDocumentRef);
        doc.setPathInfo(parentDocument.getPathAsString(), pss.generatePathSegment(doc));
        final String lifecycleState = parentDocument
                .getCurrentLifeCycleState()
                .equals(FeuilleRouteElement.ElementLifeCycleState.draft.name())
            ? FeuilleRouteElement.ElementLifeCycleState.draft.name()
            : FeuilleRouteElement.ElementLifeCycleState.ready.name();
        doc.putContextData(LifeCycleConstants.INITIAL_LIFECYCLE_STATE_OPTION_NAME, lifecycleState);
        doc = session.createDocument(doc);

        // Ordonne l'étape nouvellement créée
        String relativeDocName = null;
        if (relativeDocument != null) {
            if (before) {
                relativeDocName = relativeDocument.getName();
                session.orderBefore(parentDocument.getRef(), doc.getName(), relativeDocName);
            } else {
                final DocumentModelList orderedChilds = session.getChildren(parentDocumentRef);
                final int relativeDocIndex = orderedChilds.indexOf(relativeDocument);
                if (relativeDocIndex < orderedChilds.size() - 1) {
                    relativeDocName = orderedChilds.get(relativeDocIndex + 1).getName();
                }
                session.orderBefore(parentDocument.getRef(), doc.getName(), relativeDocName);
            }
        }
        session.save();

        if (LOG.isInfoEnable()) {
            LOG.info(
                STLogEnumImpl.LOG_INFO_TEC,
                "Création dans le conteneur " +
                parentDocument.getName() +
                " de l'élément " +
                doc.getName() +
                " " +
                " avant l'élément " +
                relativeDocName
            );
        }

        return doc;
    }

    @Override
    public void removeRouteElement(final FeuilleRouteElement routeElement, final CoreSession session) {
        final FeuilleRoute parentRoute = routeElement.getFeuilleRoute(session);
        if (!LockUtils.isLockedByCurrentUser(session, parentRoute.getDocument().getRef())) {
            throw new FeuilleRouteNotLockedException();
        }

        final DocumentModel routeElementDoc = routeElement.getDocument();
        if (!isRouteElementObligatoireUpdater(session, routeElementDoc)) {
            throw new NuxeoException(
                ResourceHelper.getString("feedback.reponses.dossier.fdr.action.delete.obligatoire.error")
            );
        }

        DocumentModel branchToDelete = getBranchToDeleteIfLastElement(routeElement, session);
        if (branchToDelete != null) {
            // Si l'élément est le dernier d'une branche parallele alors on supprimer cette branche
            softDeleteStep(session, branchToDelete);
        } else {
            // Sinon on supprime l'élément
            softDeleteStep(session, routeElement.getDocument());
        }
    }

    /*
     * Check si l'element est le dernière element d'une branche parallèle.
     * Si c'est le cas retourne le documentModel de ma branch a supprimer
     * Si non retourne null
     */
    private DocumentModel getBranchToDeleteIfLastElement(
        final FeuilleRouteElement routeElement,
        final CoreSession session
    ) {
        DocumentModel parent = session.getParentDocument(new IdRef(routeElement.getDocument().getId()));
        if (SSConstant.STEP_FOLDER_DOCUMENT_TYPE.equals(parent.getType())) {
            DocumentModelList children = session.getChildren(parent.getRef());
            StepFolder folder = parent.getAdapter(StepFolder.class);
            if (folder.isSerial() && children.size() == 1) {
                DocumentModel stepToDelete = getBranchToDeleteIfLastElement(
                    parent.getAdapter(FeuilleRouteElement.class),
                    session
                );
                return stepToDelete != null ? stepToDelete : parent;
            } else if (session.getChildren(parent.getRef()).size() == 1) {
                return parent;
            }
        }
        return null;
    }

    /**
     * Détermine si un élément de feuille de route peut être modifié vis-à-vis de son état obligatoire.
     *
     * @param session
     *            Session
     * @param routeElementDoc
     *            Élément de la feuille de route (conteneur ou étape)
     * @return Vrai si l'utilisateur peut modifier le conteneur ou l'étape
     */
    protected boolean isRouteElementObligatoireUpdater(final CoreSession session, final DocumentModel routeElementDoc) {
        // Si l'élément est une étape, calcule son état modifiable
        if (!FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(routeElementDoc.getType())) {
            return isEtapeObligatoireUpdater(session, routeElementDoc);
        }

        // Si l'élément est un conteneur, parcoure ses fils récursivement
        for (final DocumentModel childrenDoc : session.getChildren(routeElementDoc.getRef())) {
            if (!isRouteElementObligatoireUpdater(session, childrenDoc)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Duplication d'un élément d'une feuille de route à l'emplacement spécifié
     *
     * @param session
     *            Session
     * @param documentRouteDoc
     *            Document feuille de route
     * @param containerCibleDoc
     *            Document model cible pour la copie
     * @param relativeDocument
     *            Insertion relative à ce document (frère), peut être nul
     * @param before
     *            Insertion avant le document (sinon après), non pris en compte si relativeDocument est nul
     * @param doc
     *            Document à dupliquer
     * @return Document nouvellement créé
     */
    public DocumentModel copyRouteElementIntoRoute(
        final CoreSession session,
        final DocumentModel documentRouteDoc,
        final DocumentModel containerCibleDoc,
        final DocumentModel relativeDocument,
        final boolean before,
        final DocumentModel doc
    ) {
        final NuxeoPrincipal principal = session.getPrincipal();

        // création d'une copie
        final DocumentModel docCopy = duplicateRouteStep(session, doc);
        final SSRouteStep routeStep = docCopy.getAdapter(SSRouteStep.class);

        // Reset de obligatoireMinistere en fonction des permissions
        if (
            !principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR) &&
            !principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER)
        ) {
            routeStep.setObligatoireMinistere(false);
        }

        // Ordonne l'étape nouvellement créée
        DocumentModel createdDoc;
        {
            DocumentModel computedNextDocument = null;
            if (relativeDocument != null) {
                if (before) {
                    computedNextDocument = relativeDocument;
                } else {
                    final DocumentModelList orderedChilds = session.getChildren(containerCibleDoc.getRef());
                    final int relativeDocIndex = orderedChilds.indexOf(relativeDocument);
                    if (relativeDocIndex < orderedChilds.size() - 1) {
                        computedNextDocument = orderedChilds.get(relativeDocIndex + 1);
                    }
                }
            }

            // Ajout de la copie au bon emplacement dans la liste des étapes (avant le computedNextDocument)
            createdDoc =
                this.addFeuilleRouteElementToRoute(
                        session,
                        documentRouteDoc,
                        containerCibleDoc.getRef(),
                        (computedNextDocument != null ? computedNextDocument.getName() : null),
                        docCopy
                    );
        }

        return createdDoc;
    }

    /**
     * Crée un nouveau RouteStep en copiant les valeurs de celui passé en parametre.
     * Les valeurs copiées sont celles visibles dans l'écran de création d'une étape de feuille de route.
     * (type, distributionMailboxId, AutomaticValidation, ObligatoireSGG, ObligatoireMinistere, DeadLine)
     * @param session
     * @param docToCopy
     * @return
     *
     */
    protected DocumentModel duplicateRouteStep(final CoreSession session, final DocumentModel docToCopy) {
        // SOLON: la copie Nuxeo CoreSession.copy utilisée à l'origine pour les feuilles de route a été remplacée
        // par une copie manuelle. On copie les attributs généraux sans les attributs d'état.
        // On nomme la copie (name) en ajoutant '_copie' mais on ne change pas le dc:title
        // Historiquement, solon-epg a utilisé une version du code qui ajoutait le _copie au dc:title
        FeuilleRouteService routeService = ServiceUtil.getRequiredService(FeuilleRouteService.class);
        DocumentModel newDocument = session.createDocumentModel(
            routeService.getOrCreateRootOfRouteInstancePath(session),
            docToCopy.getName() + "_copie",
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE
        );
        DublincorePropertyUtil.setTitle(newDocument, docToCopy.getTitle());

        SSRouteStep routeStepDesired = newDocument.getAdapter(SSRouteStep.class);
        SSRouteStep routeStepToCopy = docToCopy.getAdapter(SSRouteStep.class);

        routeStepDesired.setType(routeStepToCopy.getType());
        routeStepDesired.setDistributionMailboxId(routeStepToCopy.getDistributionMailboxId());
        routeStepDesired.setAutomaticValidation(routeStepToCopy.isAutomaticValidation());
        routeStepDesired.setObligatoireSGG(routeStepToCopy.isObligatoireSGG());
        routeStepDesired.setObligatoireMinistere(routeStepToCopy.isObligatoireMinistere());
        routeStepDesired.setDeadLine(routeStepToCopy.getDeadLine());
        routeStepToCopy.getName();

        // alreadyDuplicated, automaticValidated ignoré de manière volontaire

        return newDocument;
    }

    /**
     * Parcours récursivement l'arborescence de la feuille de route, et marque les éléments à coller dans l'arborescence
     * cible, ainsi que tous leurs conteneurs.
     *
     * @param sourceTree
     *            Conteneur de la feuille de route
     * @param docToPasteIdSet
     *            Ensemble d'ID des éléments à coller (sans les conteneurs)
     */
    protected void markElementToPaste(final DocumentRouteTreeElement sourceTree, final Set<String> docToPasteIdSet) {
        for (final DocumentRouteTreeElement children : sourceTree.getChildrenList()) {
            final DocumentModel childrenDoc = children.getDocument();
            if (docToPasteIdSet.contains(childrenDoc.getId())) {
                // Marque l'élément
                children.setToPaste(true);

                // Marque les conteneurs intermédiaires
                markParentContainers(children.getParent());
            } else if (childrenDoc.isFolder()) {
                markElementToPaste(children, docToPasteIdSet);
            }
        }
    }

    /**
     * Remonte récursivement dans l'arbre de feuille de route et marque les conteneurs intermédiaires afin d'éviter
     * qu'il y ait des "trous" dans l'arbre des documents collés.
     *
     * @param container
     *            Conteneur actuel
     */
    protected void markParentContainers(final DocumentRouteTreeElement container) {
        container.setToPaste(true);
        markParentContainers(container.getParent(), new LinkedList<>());
    }

    /**
     * Remonte récursivement dans l'arbre de feuille de route et marque les conteneurs intermédiaires afin d'éviter
     * qu'il y ait des "trous" dans l'arbre des documents collés.
     *
     * @param container
     *            Conteneur actuel
     * @param containerToMarkList
     *            Liste des conteneurs à marquer (construite dynamiquement)
     */
    protected void markParentContainers(
        final DocumentRouteTreeElement container,
        final List<DocumentRouteTreeElement> containerToMarkList
    ) {
        // Si on est remonté à la racine : condition d'arrêt, ne marque rien
        if (container == null) {
            return;
        }

        int childrenToPast = 0;
        DocumentModel containerDoc = container.getDocument();
        for (DocumentRouteTreeElement children : container.getChildrenList()) {
            if (children.isToPaste()) {
                childrenToPast++;
            }
        }
        if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(containerDoc.getType()) && childrenToPast > 0) {
            container.setToPaste(true);
        }
        markParentContainers(container.getParent(), null);
    }

    /**
     * Charge l'arbre complet de la feuille de route à partir d'un de ses éléments.
     *
     * @param session
     *            Session
     * @param routeElementDoc
     *            N'importe lequel des élément de l'arbre à construire
     * @return Arbre complet de la feuille de route
     *
     */
    protected DocumentRouteTreeElement getDocumentRouteTree(
        final CoreSession session,
        final DocumentModel routeElementDoc
    ) {
        // Remonte à la route à partir de l'élément
        final FeuilleRouteElement routeElement = routeElementDoc.getAdapter(FeuilleRouteElement.class);

        // Si l'elément chargé est un élément de feuille de route, remonte à la route
        if (routeElement == null) {
            throw new NuxeoException("Document is not a route element");
        }

        final FeuilleRoute documentRoute = routeElement.getFeuilleRoute(session);
        if (documentRoute == null) {
            throw new NuxeoException("Cannot get document route from route element");
        }

        final DocumentModel routeDoc = documentRoute.getDocument();
        final DocumentRouteTreeElement tree = new DocumentRouteTreeElement(routeDoc, null);
        getDocumentRouteTreeElementInFolder(session, tree);

        return tree;
    }

    /**
     * Parcours l'arbre des documents à copier, et construit des nouveaux arbres contenant uniquement les étapes
     * marquées.
     *
     * @param sourceContainer
     *            Conteneur source
     * @param destContainer
     *            Conteneur cible
     * @return Racines des sous-arbres
     *
     */
    protected List<DocumentRouteTreeElement> pruneDocumentRouteTree(
        final DocumentRouteTreeElement sourceContainer,
        final DocumentModel destContainer
    ) {
        // Construit des arbres avec uniquement les étapes marquées
        final List<DocumentRouteTreeElement> rootList = new LinkedList<>();
        pruneDocumentRouteTree(sourceContainer, null, rootList);

        // Retire les étapes racines qui génèreront deux conteneurs séries ou parallèle successifs
        final List<DocumentRouteTreeElement> simplifiedRootList = new LinkedList<>();
        for (final DocumentRouteTreeElement root : rootList) {
            final DocumentModel rootDoc = root.getDocument();
            boolean simplify = false;
            if (rootDoc.isFolder()) {
                final String executionType1 = FeuilleRouteStepFolderSchemaUtil.getExecution(rootDoc);
                final String executionType2 = FeuilleRouteStepFolderSchemaUtil.getExecution(destContainer);
                if (executionType1.equals(executionType2)) {
                    simplify = true;
                }
            }
            if (simplify) {
                for (final DocumentRouteTreeElement element : root.getChildrenList()) {
                    simplifiedRootList.add(element);
                }
            } else {
                simplifiedRootList.add(root);
            }
        }
        return simplifiedRootList;
    }

    /**
     * Parcours l'arbre des documents à copier, et construit des nouveaux arbres contenant uniquement les étapes
     * marquées.
     *
     * @param sourceContainer
     *            Conteneur source
     * @param destContainer
     *            Conteneur cible
     * @param rootList
     *            Liste des racines (construite par effet de bord)
     * @return Racines des sous-arbres
     */
    protected void pruneDocumentRouteTree(
        final DocumentRouteTreeElement sourceContainer,
        DocumentRouteTreeElement destContainer,
        final List<DocumentRouteTreeElement> rootList
    ) {
        if (sourceContainer.isToPaste()) {
            if (destContainer == null) {
                // Rencontre un conteneur marqué, qui n'a pas encore de parent destination : c'est une nouvelle racine
                destContainer = new DocumentRouteTreeElement(sourceContainer.getDocument(), null);
                rootList.add(destContainer);
            } else {
                final DocumentRouteTreeElement newContainer = new DocumentRouteTreeElement(
                    sourceContainer.getDocument(),
                    null
                );
                destContainer.getChildrenList().add(newContainer);
                destContainer = newContainer;
            }
        }
        for (final DocumentRouteTreeElement children : sourceContainer.getChildrenList()) {
            final DocumentModel childrenDoc = children.getDocument();
            if (childrenDoc.isFolder()) {
                pruneDocumentRouteTree(children, destContainer, rootList);
            } else {
                if (children.isToPaste()) {
                    destContainer.getChildrenList().add(children);
                }
            }
        }
    }

    @Override
    public DocumentModel createNewInstance(
        final CoreSession session,
        final DocumentModel routeModelDoc,
        final List<String> docIds
    ) {
        final SSFeuilleRoute model = routeModelDoc.getAdapter(SSFeuilleRoute.class);
        FeuilleRouteService routeService = ServiceUtil.getRequiredService(FeuilleRouteService.class);
        final FeuilleRoute routeInstance = routeService.createNewInstance(model, docIds, session, false);

        final DocumentModel routeInstancedoc = routeInstance.getDocument();

        // Initialise les étapes de la nouvelle feuille de route, attache l'identifiant de la feuille de route aux
        // étapes (dénormalisation).
        initFeuilleRouteStep(session, routeInstancedoc);
        return routeInstancedoc;
    }

    @Override
    public DocumentModel createNewInstance(final CoreSession session, final String name, final String caseDocId) {
        if (LOG.isDebugEnable()) {
            LOG.debug(
                STLogEnumImpl.CREATE_FDR_TEC,
                "Création d'une nouvelle instance de feuille de route sur le cas <" + caseDocId
            );
        }

        FeuilleRouteService routeService = ServiceUtil.getRequiredService(FeuilleRouteService.class);
        DocumentModel route1 = session.createDocumentModel(
            routeService.getOrCreateRootOfRouteInstancePath(session),
            name,
            SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE
        );
        DublincorePropertyUtil.setTitle(route1, name);
        route1 = session.createDocument(route1);
        final List<String> docIds = new ArrayList<>();
        docIds.add(caseDocId);
        final SSFeuilleRoute routeInstance = route1.getAdapter(SSFeuilleRoute.class);
        routeInstance.setAttachedDocuments(docIds);

        // Initialise les étapes de la nouvelle feuille de route, attache l'identifiant de la feuille de route aux
        // étapes (dénormalisation).
        initFeuilleRouteStep(session, route1);
        return routeInstance.getDocument();
    }

    @Override
    public FeuilleRouteStep createNewRouteStep(
        final CoreSession session,
        final String mailboxId,
        final String routingTaskType
    ) {
        if (LOG.isDebugEnable()) {
            LOG.debug(STLogEnumImpl.CREATE_STEP_TEC, "Création d'une nouvelle étape de feuille de route");
        }

        final DocumentModel routeStepDoc = session.createDocumentModel(SSConstant.ROUTE_STEP_DOCUMENT_TYPE);
        final SSRouteStep routeStep = routeStepDoc.getAdapter(SSRouteStep.class);
        routeStep.setType(routingTaskType);
        routeStep.setDistributionMailboxId(mailboxId);

        return routeStep;
    }

    @Override
    public void moveRouteStep(final CoreSession session, final String routeStepId, final boolean moveUp) {
        final DocumentModel routeElementDocToMove = session.getDocument(new IdRef(routeStepId));
        final DocumentModel parentDoc = session.getDocument(routeElementDocToMove.getParentRef());
        final FeuilleRouteExecutionType executionType = FeuilleRouteStepFolderSchemaUtil.getExecutionType(parentDoc);
        if (FeuilleRouteExecutionType.parallel.equals(executionType)) {
            throw new FeuilleRouteException(
                getString("feedback.casemanagement.document.route.cant.move.steps.in.parallel.container")
            );
        }
        final DocumentModelList orderedChilds = getOrderedRouteElement(parentDoc.getId(), session);
        final int selectedDocumentIndex = orderedChilds.indexOf(routeElementDocToMove);
        if (moveUp) {
            // Interdit le déplacement si l'étape déplacée est la première étape
            if (selectedDocumentIndex == 0) {
                throw new FeuilleRouteException(
                    getString("feedback.casemanagement.document.route.already.first.step.in.container")
                );
            }
            final DocumentModel stepMoveBefore = orderedChilds.get(selectedDocumentIndex - 1);

            // Interdit le déplacement avant une étape en cours ou terminée
            final FeuilleRouteElement stepElementMoveBefore = stepMoveBefore.getAdapter(FeuilleRouteElement.class);
            if (!stepElementMoveBefore.isDraft() && !stepElementMoveBefore.isReady()) {
                throw new FeuilleRouteException(
                    getString("feedback.casemanagement.document.route.cant.move.step.after.no.modifiable.step")
                );
            }

            // Interdit le déplacement avant une étape obligatoire
            if (!isEtapeObligatoireUpdater(session, stepMoveBefore)) {
                throw new FeuilleRouteException(getString("feuilleRoute.action.moveUp.avantEtapeObligatoire.error"));
            }

            // Valide que l'on peut déplacer l'étape vers le haut
            validateMoveRouteStepBefore(stepMoveBefore);

            // Déplace l'étape
            session.orderBefore(parentDoc.getRef(), routeElementDocToMove.getName(), stepMoveBefore.getName());
        } else {
            // Interdit le déplacement si l'étape déplacée est la dernière étape
            if (selectedDocumentIndex == orderedChilds.size() - 1) {
                throw new FeuilleRouteException(
                    getString("feedback.casemanagement.document.already.last.step.in.container")
                );
            }

            // Interdit le déplacement après une étape obligatoire
            final DocumentModel stepMoveAfter = orderedChilds.get(selectedDocumentIndex + 1);
            if (!isEtapeObligatoireUpdater(session, stepMoveAfter)) {
                throw new FeuilleRouteException(getString("feuilleRoute.action.moveUp.apresEtapeObligatoire.error"));
            }

            // Déplace l'étape
            session.orderBefore(parentDoc.getRef(), stepMoveAfter.getName(), routeElementDocToMove.getName());
        }
    }

    @Override
    public boolean isEtapeObligatoireUpdater(final CoreSession session, final DocumentModel routeStepDoc) {
        // Traite uniquement les étapes de feuille de route et pas les conteneurs
        if (routeStepDoc.hasFacet("Folderish")) {
            return true;
        }
        final SSRouteStep routeStep = routeStepDoc.getAdapter(SSRouteStep.class);
        final NuxeoPrincipal nuxeoPrincipal = session.getPrincipal();
        final List<String> groups = nuxeoPrincipal.getGroups();

        // Seul l'administrateur fonctionnel a le droit de modifier les étapes obligatoires SGG
        final boolean isAdminFonctionnel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
        if (routeStep.isObligatoireSGG() && !isAdminFonctionnel) {
            return false;
        }

        // Seul l'administrateur ministériel a le droit de modifier les étapes obligatoires ministère
        final boolean isAdminMinisteriel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER);
        if (routeStep.isObligatoireMinistere() && !isAdminMinisteriel) {
            return false;
        }

        return true;
    }

    /**
     * Recherche d'étapes de feuille de route par critères de recherche.
     *
     * @param criteria
     *            Critères de recherche
     * @return Liste d'étapes de feuille de route
     *
     */
    private List<DocumentModel> findRouteStepByCriteria(final CoreSession session, final RouteStepCriteria criteria) {
        final StringBuilder sb = new StringBuilder("SELECT s.ecm:uuid as id FROM ")
            .append(SSConstant.ROUTE_STEP_DOCUMENT_TYPE)
            .append(" AS s ");

        final List<Object> paramList = new ArrayList<>();
        if (StringUtils.isNotBlank(criteria.getDocumentRouteId())) {
            sb
                .append("WHERE isChildOf(s.")
                .append(STSchemaConstant.ECM_UUID_XPATH)
                .append(", SELECT r.")
                .append(STSchemaConstant.ECM_UUID_XPATH)
                .append(" FROM ")
                .append(SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE)
                .append(" AS r WHERE r.")
                .append(STSchemaConstant.ECM_UUID_XPATH)
                .append(" = ?) = 1 ");
            paramList.add(criteria.getDocumentRouteId());
        }
        return CoreInstance.doPrivileged(
            session,
            systemSession -> {
                return QueryHelper.doUFNXQLQueryAndFetchForDocuments(
                    systemSession,
                    sb.toString(),
                    paramList.toArray(),
                    0,
                    0,
                    new PrefetchInfo(STSchemaConstant.ROUTING_TASK_SCHEMA + ", " + STSchemaConstant.DUBLINCORE_SCHEMA)
                );
            }
        );
    }

    @Override
    public void softDeleteStep(final CoreSession session, final DocumentModel doc) {
        if ("true".equals(Framework.getProperty("socle.solrep.routestep.soft.delete", "true"))) {
            removeDocumentRouteIdData(session, doc);
            session.move(doc.getRef(), session.getDocument(new PathRef("/case-management/trash-root")).getRef(), null);
            if (!STLifeCycleConstant.DELETED_STATE.equals(doc.getCurrentLifeCycleState())) {
                session.followTransition(doc.getRef(), STLifeCycleConstant.TO_DELETE_TRANSITION);
            }
            session.save();
        } else {
            LOG.info(session, SSLogEnumImpl.DEL_STEP_TEC, doc);
            session.removeDocument(doc.getRef());
            session.save();
        }
    }

    private void removeDocumentRouteIdData(final CoreSession session, final DocumentModel doc) {
        if (doc.isFolder()) {
            final List<DocumentModel> children = session.getChildren(doc.getRef());
            for (final DocumentModel child : children) {
                removeDocumentRouteIdData(session, child);
            }
        }
        if (doc.hasSchema(STSchemaConstant.ROUTING_TASK_SCHEMA)) {
            RoutingTaskSchemaUtils.setDocumentRouteId(doc, "");
            session.saveDocument(doc);
        }
    }

    @Override
    public void lockDocumentRoute(SSFeuilleRoute routeModel, CoreSession session) {
        FeuilleRouteService routeService = ServiceUtil.getRequiredService(FeuilleRouteService.class);
        routeService.lockDocumentRoute(routeModel, session);
    }

    @Override
    public void unlockDocumentRoute(SSFeuilleRoute routeModel, CoreSession session) {
        FeuilleRouteService routeService = ServiceUtil.getRequiredService(FeuilleRouteService.class);
        routeService.unlockDocumentRoute(routeModel, session);
    }

    @Override
    public DocumentModelList getOrderedRouteElement(String routeElementId, CoreSession session) {
        FeuilleRouteService routeService = ServiceUtil.getRequiredService(FeuilleRouteService.class);
        return routeService.getOrderedRouteElement(routeElementId, session);
    }

    @Override
    public List<FeuilleRoute> getRoutesForAttachedDocument(CoreSession session, String attachedDocId) {
        FeuilleRouteService routeService = ServiceUtil.getRequiredService(FeuilleRouteService.class);
        return routeService.getDocumentRoutesForAttachedDocument(session, attachedDocId, null);
    }

    @Override
    public boolean hasEtapeEnCoursOfType(CoreSession session, STDossier dossier, String type) {
        boolean hasEtape = false;

        if (dossier.getLastDocumentRoute() != null) {
            final DocumentModel route = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));
            FeuilleRouteElement feuilleRouteElement = route.getAdapter(FeuilleRouteElement.class);
            SSFeuilleRoute feuilleRoute = feuilleRouteElement.getFeuilleRoute(session);

            hasEtape =
                SSServiceLocator
                    .getDocumentRoutingService()
                    .getFeuilleRouteElements(feuilleRoute, session)
                    .stream()
                    .filter(e -> e.getElement().isRunning())
                    .map(RouteTableElement::getDocument)
                    .filter(d -> !d.isFolder())
                    .anyMatch(d -> type.equals(d.getAdapter(SSRouteStep.class).getType()));
        }

        return hasEtape;
    }

    @Override
    public boolean canDoActionIfStepObligatoire(NuxeoPrincipal principal, DocumentModel stepDoc) {
        SSRouteStep routeStep = stepDoc.getAdapter(SSRouteStep.class);
        return canDoActionAccordingToStepObligatoireProperty(principal, routeStep);
    }

    @Override
    public boolean canDoActionAccordingToStepObligatoireProperty(NuxeoPrincipal principal, SSRouteStep step) {
        if (PermissionHelper.isAdminFonctionnel(principal)) {
            return true;
        }

        return (
            !step.isObligatoire() ||
            (step.isObligatoireSGG() && PermissionHelper.isSuperviseurSgg(principal)) ||
            (
                step.isObligatoireMinistere() &&
                (PermissionHelper.isAdminMinisteriel(principal) || PermissionHelper.isSuperviseurSgg(principal))
            )
        );
    }

    @Override
    public boolean canFolderBeDeleted(CoreSession session, NuxeoPrincipal principal, DocumentRef idRefRouteElement) {
        DocumentModelList children = session.getChildren(idRefRouteElement);
        for (DocumentModel child : children) {
            if (child.isFolder()) {
                if (!canFolderBeDeleted(session, principal, child.getRef())) {
                    return false;
                }
            } else {
                SSRouteStep step = child.getAdapter(SSRouteStep.class);
                return canDoActionAccordingToStepObligatoireProperty(principal, step);
            }
        }
        return true;
    }
}
