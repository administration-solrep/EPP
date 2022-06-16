package fr.dila.ss.ui.services.actions;

import fr.dila.ss.api.dto.EtapeFeuilleDeRouteDTO;
import fr.dila.ss.api.feuilleroute.DocumentRouteTableElement;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.bean.fdr.CreationEtapeDTO;
import fr.dila.ss.ui.enums.FeuilleRouteEtapeOrder;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface SSDocumentRoutingActionService {
    boolean isFeuilleRouteVisible(CoreSession session, SSPrincipal ssPrincipal, DocumentModel dossierDoc);

    boolean hasRelatedRoute(CoreSession session, DocumentModel currentDocument);

    String startRoute(CoreSession session, DocumentModel currentDocument, List<String> attachDocIds);

    SSFeuilleRoute getRelatedRoute(CoreSession session, DocumentModel currentDocument);

    boolean isSerialStepFolder(SpecificContext context);

    boolean isParallelStepFolder(SpecificContext context);
    /**
     * Retourne vrai si l'étape est un conteneur.
     *
     * @param stepDoc
     *            Etape de feuille de route
     * @return Vrai si l'étape est un conteneur
     */
    boolean isRouteFolder(DocumentModel stepDoc);

    boolean isInProgressStep(SpecificContext context);

    boolean isEditableStep(
        SpecificContext context,
        CoreSession session,
        DocumentModel currentDocument,
        DocumentModel stepDoc
    );

    boolean isEditableRouteElement(SpecificContext context);

    /**
     * Retourne vrai si l'utilisateur peut modifier l'instance de feuille de
     * route.
     *
     * @return Vrai si l'utilisateur peut modifier l'instance de feuille de
     *         route.
     */
    boolean isFeuilleRouteUpdatable(SpecificContext context, CoreSession session, DocumentModel currentDocument);

    /**
     * Retourne vrai si l'utilisateur peut modifier l'instance de feuille de
     * route.
     *
     * @return Vrai si l'utilisateur peut modifier l'instance de feuille de
     *         route.
     */
    boolean isModeleFeuilleRoute(SpecificContext context, DocumentModel currentDocument);

    void cancelRoute(CoreSession session, DocumentModel currentDocument);

    void validateRouteModel(SpecificContext context, CoreSession session, DocumentModel currentDocument);

    void startRouteRelatedToCurrentDocument(
        SpecificContext context,
        CoreSession session,
        DocumentModel currentDocument
    );

    /**
     * returns true if the routeStarted on the current Document is editable (is Ready)
     */
    boolean routeRelatedToCurrentDocumentIsRunning(CoreSession session, DocumentModel currentDocument);

    String getTypeDescription(DocumentRouteTableElement localizable);

    boolean isStep(DocumentModel doc);

    boolean currentRouteModelIsDraft(DocumentModel relatedRouteModel);

    /**
     * Supprime une étape de feuille de route.
     */
    void removeStep(SpecificContext context);

    List<EtapeFeuilleDeRouteDTO> getLstEtapes(CoreSession session, String hiddenSourceDocId, String hiddenDocOrder);

    /**
     * Creation simple d'une routeStep pour la création en masse
     *
     * @return DocumentModel la nouvelle étape à créer
     */
    DocumentModel newSimpleRouteStep(CoreSession session, String hiddenSourceDocId, String hiddenDocOrder);

    void removeFromLstEtapeFeuilleDeRoute(
        CoreSession session,
        String hiddenSourceDocId,
        String hiddenDocOrder,
        EtapeFeuilleDeRouteDTO eFDR
    );

    boolean isCurrentRouteLockedByCurrentUser(CoreSession session, DocumentModel currentDocument);

    boolean isCurrentRouteLocked(CoreSession session, DocumentModel currentDocument);

    boolean canUnlockRoute(CoreSession session, DocumentModel currentDocument);

    boolean canLockRoute(CoreSession session, DocumentModel currentDocument);

    boolean canStepBeDeleted(SpecificContext context);

    boolean canFolderBeDeleted(SpecificContext context);

    Map<String, String> getCurrentRouteLockDetails(CoreSession session, DocumentModel currentDocument);

    void lockCurrentRoute(SpecificContext context, CoreSession session, DocumentModel currentDocument);

    void unlockCurrentRoute(CoreSession session, DocumentModel currentDocument);

    boolean isEmptyFork(CoreSession session, DocumentModel forkDoc);

    DocumentModel createRouteElement(
        CoreSession session,
        DocumentModel currentDocument,
        String typeName,
        String hiddenSourceDocId,
        FeuilleRouteEtapeOrder hiddenDocOrder
    );

    /**
     * Déplace d'un cran une étape de feuille de route.
     */
    void moveRouteElement(SpecificContext context);

    void saveRouteElement(SpecificContext context, DocumentModel newDocument);

    /**
     * Création des étapes de feuilles de routes en série en masse.
     */
    void saveRouteElementSerialMass(SpecificContext context, CreationEtapeDTO etape);

    /**
     * Sauvegarde les étapes de feuille de route en masse.
     */
    void saveRouteElementMass(SpecificContext context);

    void updateRouteElement(SpecificContext context, DocumentModel changeableDocument);

    /**
     * Détermine si l'utilisateur peut substituer la feuille de route.
     *
     * @return Vrai si on peut substituer la feuille de route
     */
    boolean canUserSubstituerFeuilleRoute(SpecificContext context);

    /*
     * Vérifie s'il y a des étapes copiées en session
     */
    boolean isStepCopied(SpecificContext context);

    /**
     * Colle les étapes avant une étape.
     */
    int pasteBefore(SpecificContext context);

    /**
     * Colle les étapes après une étape.
     */
    int pasteAfter(SpecificContext context);

    boolean canEditStep(SpecificContext context);

    boolean isStepInitialisation(SpecificContext context);
}
