package fr.dila.ss.api.service;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.st.api.dossier.STDossier;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

/**
 * Service de document routing du socle SOLREP, étend et remplace celui de Nuxeo. Ce service permet de gérer les
 * traitements liés aux feuilles de route et aux modèles de feuilles de route. En revanche, il ne permet pas de gérer le
 * catalogue des modèles, ni la distribution des dossiers.
 *
 * @author jtremeaux
 */
public interface DocumentRoutingService {
    /**
     * Renseigne le nom du document étape de feuille de route à sa création.
     *
     * @param routeStepDoc
     *            Document étape de feuille de route
     *
     */
    void setRouteStepDocName(DocumentModel routeStepDoc);

    /**
     * Recherche et retourne l'étape de feuille de route liée à un CaseLink.
     *
     * @param session
     *            Session
     * @param acl
     *            ActionableCaseLink
     * @return Étape de la feuille de route
     *
     */
    DocumentModel getFeuilleRouteStep(CoreSession session, ActionableCaseLink acl);

    /**
     * Demande la validation d'un modèle de feuille de route à l'état brouillon.
     *
     * @param routeModel
     *            Modèle de feuille de route
     * @param session
     *            Session
     * @param demandeValidation
     *            Demande la validation ou annule la demande
     * @return Modèle de feuille de route
     *
     */
    SSFeuilleRoute requestValidateRouteModel(
        final SSFeuilleRoute routeModel,
        CoreSession session,
        boolean demandeValidation
    );

    /**
     * Dévalide un modèle de feuille de route (passe son lifeCycle de "validated" à "draft").
     *
     * @param routeModel
     *            Modèle de feuille de route
     * @param session
     *            Session
     * @return Feuille de route validée
     *
     */
    SSFeuilleRoute invalidateRouteModel(final SSFeuilleRoute routeModel, CoreSession session);

    /**
     * Dévalide un modèle de feuille de route (passe son lifeCycle de "validated" à "draft"). Attention cette methode
     * n'est pas en unrestricted contrairement a invalidateRouteModel(final FeuilleRoute routeModel, CoreSession
     * session)
     *
     * @param routeModel
     *            Modèle de feuille de route
     * @param session
     *            Session
     * @return Feuille de route validée
     *
     */
    SSFeuilleRoute invalidateRouteModelNotUnrestricted(final SSFeuilleRoute routeModel, CoreSession session);

    /**
     * Duplique un modèle de feuille de route.
     *
     * @param session
     *            Session
     * @param routeToCopyDoc
     *            Document modèle de feuille de route à dupliquer
     * @param ministere
     *            Si renseigné et que la feuille de route dupliquée est sans ministère, la nouvelle feuille de route
     *            sera assignée à ce ministère
     * @return Nouveau modèle de feuille de route
     *
     */
    SSFeuilleRoute duplicateRouteModel(CoreSession session, DocumentModel routeToCopyDoc, String ministere);

    /**
     * Crée un nouveau document dans un conteneur d'éléments de feuille de route.
     *
     * @param session
     *            Session
     * @param FeuilleRouteDoc
     *            Document feuille de route parent
     * @param parentDocumentRef
     *            Document parent
     * @param sourceName
     *            Element après lequel ajouter (peut être nul)
     * @param routeElementDoc
     *            Document élement à ajouter
     * @return Element nouvellement créé
     *
     */
    DocumentModel addFeuilleRouteElementToRoute(
        CoreSession session,
        DocumentModel FeuilleRouteDoc,
        DocumentRef parentDocumentRef,
        String sourceName,
        DocumentModel routeElementDoc
    );

    /**
     * Retourne le document précédent un document spécifié dans un document Forderish ordonné. Si le document spécifié
     * est le premier de la liste, retourne null.
     *
     * @param session
     *            Session
     * @param parentId
     *            Identifiant du document parent
     * @param elementId
     *            Identifiant du document spécifié
     * @return Document précédent le document spécifié
     *
     */
    DocumentModel getDocumentBefore(CoreSession session, String parentId, String elementId);

    /**
     * Retourne la liste des étapes liée à la feuille de route.
     *
     * @param routeDocument
     *            {@link FeuilleRoute}.
     * @param session
     *            The session used to query the {@link FeuilleRoute}.
     * @param A
     *            list of {@link FeuilleRouteElement}
     */
    List<RouteTableElement> getFeuilleRouteElements(SSFeuilleRoute route, CoreSession session);

    boolean hasEtapeEnCoursOfType(CoreSession session, STDossier dossier, String type);

    /**
     * Duplique une liste d'étapes de feuille de route dans un conteneur cible.
     *
     * @param session
     *            Session
     * @param FeuilleRouteDoc
     *            Document feuille de route cible
     * @param parentDoc
     *            Document conteneur cible (feuille de route, ou conteneur d'étapes)
     * @param relativeDocument
     *            Insertion relative à ce document (frère), peut être nul
     * @param before
     *            Insertion avant le document (sinon après), non pris en compte si relativeDocument est nul
     * @param documents
     *            Liste d'étapes à dupliquer
     * @return Liste des nouveaux documents créés
     *
     */
    List<DocumentModel> pasteRouteStepIntoRoute(
        CoreSession session,
        DocumentModel FeuilleRouteDoc,
        DocumentModel parentDoc,
        DocumentModel relativeDocument,
        boolean before,
        List<DocumentModel> documents
    );

    /**
     * Crée une nouvelle instance de feuille de route à partir d'un modèle. L'instance n'est pas encore démarrée.
     *
     * @param session
     *            Session
     * @param routeModelDoc
     *            Modèle de feuille de route
     * @param caseDocId
     *            Cas
     * @return Nouvelle instance de feuille de route
     *
     */
    DocumentModel createNewInstance(CoreSession session, DocumentModel routeModelDoc, List<String> docIds);

    /**
     * Crée une nouvelle instance de feuille de route directement, sans cloner un modèle. L'instance n'est pas encore
     * démarrée.
     *
     * @param session
     *            Session
     * @param name
     *            nom de l'instance de la feuille de route
     * @param caseDocId
     *            Cas
     * @return Nouvelle instance de feuille de route
     *
     */
    DocumentModel createNewInstance(CoreSession session, String name, String caseDocId);

    /**
     * Crée une nouvelle étape de feuille de route
     *
     * @param session
     *            Session
     * @param mailboxId
     *            Identifiant technique de la Mailbox
     * @param routingTaskType
     *            Type d'étape
     * @return Nouvelle étape
     *
     */
    FeuilleRouteStep createNewRouteStep(CoreSession session, String mailboxId, String routingTaskType);

    /**
     * Déplace une étape dans son conteneur parent dans la direction indiquée. Si l'étape est dans un conteneur
     * parallèle, elle ne peut pas être déplacée. La route doit être déjà verrouillée avant d'effectuer cette action.
     *
     * @param session
     *            Session
     * @param routeStepId
     *            Identifiant technique de l'étape à déplacer
     * @param moveUp
     *            Déplace l'étape vers le haut (sinon vers le bas)
     *
     */
    void moveRouteStep(CoreSession session, String routeStepId, boolean moveUp);

    /**
     * Valide que l'on peut déplacer une étape avant cet étape.
     *
     * @param routeStepDoc
     *            Document étape à tester
     *
     */
    void validateMoveRouteStepBefore(DocumentModel routeStepDoc);

    /**
     * /** Détermine si l'utilisateur a le droit de modifier l'étape vis-à-vis de son état obligatoire.
     *
     * @param session
     *            Session
     * @param routeStepDoc
     *            Document étape de feuille de route
     * @return Vrai si l'utilisateur a le droit de modifier l'étape
     *
     */
    boolean isEtapeObligatoireUpdater(CoreSession session, DocumentModel routeStepDoc);

    /**
     * Envoi d'un mail aux utilisateurs capables de valider le modèle de feuille de route
     *
     * @param session
     *            CoreSession
     * @param routeModel
     *            Modèle de feuille de route
     *
     */
    void sendValidationMail(CoreSession session, SSFeuilleRoute routeModel);

    /**
     * Initialise les étapes d'une feuille de route après la duplication. Les étapes sont réattachée à la nouvelle
     * feuille de route (champ dénormalisé).
     *
     * @param session
     *            Session
     * @param feuilleRouteDoc
     *            Document feuille de route
     *
     */
    void initFeuilleRouteStep(CoreSession session, DocumentModel feuilleRouteDoc);

    /**
     * Pour un FeuilleRoute, parcourt les étapes contenues et vérifie qu'elles sont à l'état draft Si ce n'est pas le
     * cas, les met à draft
     *
     * @param session
     * @param route
     * @return
     *
     */
    void checkAndMakeAllStateStepDraft(CoreSession session, SSFeuilleRoute route);

    void softDeleteStep(CoreSession session, DocumentModel doc);

    SSFeuilleRoute getDocumentRouteForDossier(final CoreSession session, final String dossierDocId);

    List<FeuilleRoute> getRoutesForAttachedDocument(CoreSession session, String attachedDocId);

    void lockDocumentRoute(SSFeuilleRoute routeModel, CoreSession session);

    void unlockDocumentRoute(SSFeuilleRoute routeModel, CoreSession session);

    DocumentModelList getOrderedRouteElement(String routeElementId, CoreSession session);

    void addRouteElementToRoute(
        DocumentRef parentDocumentRef,
        int idx,
        FeuilleRouteElement routeElement,
        CoreSession session
    );

    void removeRouteElement(FeuilleRouteElement routeElement, CoreSession session);

    SSFeuilleRoute validateRouteModel(final SSFeuilleRoute routeModel, CoreSession userSession);

    boolean canDoActionIfStepObligatoire(NuxeoPrincipal principal, DocumentModel stepDoc);

    boolean canDoActionAccordingToStepObligatoireProperty(NuxeoPrincipal principal, SSRouteStep step);

    boolean canFolderBeDeleted(CoreSession session, NuxeoPrincipal principal, DocumentRef idRefRouteElement);
}
