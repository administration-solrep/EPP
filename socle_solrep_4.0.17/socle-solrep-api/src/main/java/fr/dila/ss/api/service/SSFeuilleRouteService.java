package fr.dila.ss.api.service;

import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Filter;

/**
 * Service permettant d'effectuer des actions spécifiques sur les instances de feuille de route dans le socle
 * transverse.
 *
 * @author jtremeaux
 */
public interface SSFeuilleRouteService extends Serializable {
    /**
     * Recherche dans le conteneur de routeStepDoc, une étape précédent routeStepDoc en remontant dans l'ordre
     * d'exécution des étapes. Si le conteneur n'est pas de type série ou que l'étape n'est pas trouvée, retourne null.
     *
     * @param session
     *            Session
     * @param routeStepDoc
     *            Étape à partir de laquelle chercher
     * @param routeStepFilter
     *            Filtre à appliquer
     * @return Étape précédente
     *
     */
    DocumentModel findPreviousStepInFolder(
        CoreSession session,
        DocumentModel routeStepDoc,
        Filter routeStepFilter,
        boolean includedEtapeParallel
    );

    /**
     * Recherche la ou les étapes suivantes dans l'ordre d'exécution de la feuille de route. Si l'étape n'est pas
     * trouvée, retourne null.
     *
     * @param session
     *            Session
     * @param routeInstanceDocId
     *            UUID de la feuille de route
     * @param routeStepDoc
     *            Etape à partir de laquelle chercher
     * @param routeStepFilter
     *            Filtre à appliquer
     * @return Etape suivante
     *
     */
    List<DocumentModel> findNextSteps(
        CoreSession session,
        String feuilleRouteDocId,
        DocumentModel routeStepDoc,
        Filter routeStepFilter
    );

    /**
     * Retourne l'étape courante correspondant au dossier.
     *
     * @param session
     *            Session
     * @param routeInstanceDocId
     *            l'id de l'instance de la feuille de route
     * @return Etape courante
     *
     */
    List<DocumentModel> getRunningSteps(CoreSession session, String routeInstanceDocId);

    /**
     * Retourne vrai si l'étape est la première de la branche ou qu'il est dans une branche parallèle.
     *
     * @param session
     *            session
     * @param routingTaskId
     *            id de l'étape
     * @return true si première étape ou dans une branche parallèle
     *
     */
    boolean isFirstStepInBranchOrParallel(CoreSession session, String routingTaskId);

    /**
     * Retourne la liste des étapes de feuille de route de premier niveau d'un dossier.
     *
     * @param session
     *            Session
     * @param dossierDoc
     *            Document dossier
     * @return Liste des étapes de feuille de route d'un dossier
     *
     */
    List<DocumentModel> getSteps(CoreSession session, DocumentModel dossierDoc);

    /**
     * Retourne la liste des étapes de feuille de route de premier niveau d'un dossier.
     */
    List<DocumentModel> getSteps(CoreSession session, String routeId);

    /**
     * Methode pour récupérer le type d'étape de feuille de route "pour information"
     *
     * @return valueur de la constante du type d'étape
     */
    String getTypeEtapeInformation();

    /**
     * Methode pour récupérer le type d'étape de feuille de route "pour impression"
     *
     * @return
     */
    String getTypeEtapeImpression();

    /**
     * Methode pour récupérer le type d'étape de feuille de route "pour attribution"
     *
     * @return
     */
    String getTypeEtapeAttribution();

    /**
     * Renvoie true si au moins une feuille de route est active et contient un fils Attention, requete coûteuse, à
     * optimiser
     *
     * @param session
     *            session
     * @return
     *
     */
    boolean isActiveRouteForPosteId(CoreSession session, String posteId);

    /**
     * Renvoie true si on doit créer les caselink à la distribution de cette étape
     *
     * @param session
     * @param routeStep
     * @return
     */
    boolean canDistributeStep(CoreSession session, SSRouteStep routeStep, List<DocumentModel> docs);

    /**
     *
     * @param session
     * @param routeStep
     * @param dossierDocList
     *
     */
    void doValidationAutomatiqueOperation(
        CoreSession session,
        SSRouteStep routeStep,
        List<DocumentModel> dossierDocList
    );

    /**
     * Renvoie la liste (potentiellement vide) des étapes de feuille de route
     * (modèle ou instance) pour lesquelles le poste est affecté et qui sont actives
     * ou à venir
     *
     * @param session session
     * @posteId identifiant du poste
     * @return une collection vide ou remplie d'objet FeuilleRouteStep
     *         (DocumentModel).
     */
    Collection<DocumentModel> getFeuilleRouteWithActiveOrFutureRouteStepsForPosteId(
        CoreSession session,
        String posteId
    );

    /**
     * Renvoie la liste (potentiellement vide) des étapes de feuille de route
     * d'instance de fdr pour lesquelles le poste est affecté et qui sont actives
     * ou à venir
     *
     * @param session session
     * @posteId identifiant du poste
     * @return une collection vide ou remplie d'objet FeuilleRouteStep
     *         (DocumentModel).
     */
    Collection<DocumentModel> getFeuilleRouteWithActiveOrFutureRouteStepsInInstanceForPosteId(
        CoreSession session,
        String posteId
    );

    /**
     * Renvoie la liste (potentiellement vide) des étapes de feuille de route
     * de modèle de fdr pour lesquelles le poste est affecté et qui sont actives
     * ou à venir
     *
     * @param session session
     * @posteId identifiant du poste
     * @return une collection vide ou remplie d'objet FeuilleRouteStep
     *         (DocumentModel).
     */
    Collection<DocumentModel> getFeuilleRouteWithStepsInModeleFdrForPosteId(CoreSession session, String posteId);

    /**
     * Met à jour les champs de l'étape de feuille de route après la validation de l'étape.
     *
     * @param session
     *            Session
     * @param routeStep
     *            Étape de feuille de route
     * @param dossierDocList
     *            Liste des dossiers distribués
     * @param caseLinkList
     *            CaseLink de l'étape (facultatif, dans le cas d'une étape "pour information")
     *
     */
    void updateRouteStepFieldAfterValidation(
        CoreSession session,
        SSRouteStep routeStep,
        List<DocumentModel> dossierDocList,
        List<STDossierLink> caseLinkList
    );

    /**
     * Met à jour les champs spécifique à l'application de l'étape de feuille de route après la validation de l'étape
     *
     * @param session
     * @param routeStepDoc
     * @param dossierDocList
     * @param caseLinkList
     */
    void updateApplicationFieldsAfterValidation(
        CoreSession session,
        DocumentModel routeStepDoc,
        List<DocumentModel> dossierDocList,
        List<STDossierLink> caseLinkList
    );

    /**
     * Lance les opérations du batch de la validation automatique des étapes de feuilles de route lorsqu'elles
     * atteignent la date d'échéance.
     *
     * @param batchLoggerModel
     *            pour l'enregistrement des logs de batch
     * @param session
     *            session
     *
     *
     */
    void doAutomaticValidationBatch(CoreSession session, BatchLoggerModel batchLoggerModel, Long nbError);

    /**
     * Methode pour récupérer la requête des dossiersLinks liés aux étapes de feuille de route dont on doit envoyer un
     * mail de notification car elles ont atteint la date d'échéance. note : utilisé pour le batch de validation
     * automatique : méthode
     * <p>
     * doValidationAutomatiqueBatch
     * <p>
     *
     * @return la requête des dossiersLinks liés aux étapes de feuille de route dont on doit envoyer un mail de
     *         notification car elles ont atteint la date d'échéance.
     */
    String getDossierLinkListToEmailForAutomaticValidationQuery();

    /**
     * Calcul et modification des échéances prévisionnelles et opérationnelles de la feuille de route associée au
     * dossier.
     *
     * @param session
     * @param dossier
     *
     */
    void calculEcheanceFeuilleRoute(CoreSession session, STDossier dossier);

    /**
     * Récupère la date de début pour le calcul des échéances
     *
     * @param session
     *            STDossier
     * @param dossier
     *            STDossier
     * @return la date de début pour le calcul des échéances
     */
    Calendar getDateDebutEcheance(CoreSession session, STDossier dossier);

    /**
     * Supprime les étapes à venir de la feuille de route.
     *
     * @param session
     * @param routeId
     *
     */
    void deleteNextSteps(CoreSession session, String routeId);

    /**
     * Vérifie que la FdR à plus d'une étape (autrement dit, qu'elle à plus que l'étape d'initialisation)
     *
     * @param session
     * @param routeId
     *
     */
    boolean hasMoreThanOneStep(CoreSession session, String routeId);

    /**
     * Recherche la ou les étapes suivantes dans l'ordre d'exécution de la feuille de route. Si l'étape n'est pas
     * trouvée, retourne null.
     *
     * Copie de la méthode findNextSteps modifié pour le mantis 158720
     *
     * @param session
     *            Session
     * @param routeInstanceDocId
     *            UUID de la feuille de route
     * @param routeStepDoc
     *            Etape à partir de laquelle chercher
     * @param routeStepFilter
     *            Filtre à appliquer
     * @return Etape suivante
     *
     */
    List<DocumentModel> findAllNextSteps(
        CoreSession session,
        String feuilleRouteDocId,
        DocumentModel currentStepDoc,
        Filter routeStepFilter
    );

    /**
     * Méthode qui renvoie la liste de toutes les étapes de la feuille de route à plat
     *
     * @return List<DocumentModel>
     */
    List<DocumentModel> findAllSteps(CoreSession session, String feuilleRouteDoc);

    /**
     * Méthode qui renvoie la liste de toutes les étapes de la feuille de route à plat avec prefetch pour indexation
     *
     * @return List<DocumentModel>
     */
    List<DocumentModel> findAllStepsIndexation(CoreSession session, final String feuilleRouteDocId);

    boolean hasStepFolders(List<RouteTableElement> routeTableElementList);

    List<StepFolder> getStepFolders(RouteTableElement docRouteTableElement);

    /**
     * Récupère la liste des libellés des étapes à venir sous la forme 'Type étape - Poste'
     *
     * @param session
     * @param feuilleRouteId
     * @return
     */
    List<String> getEtapesAVenir(CoreSession session, String feuilleRouteId);

    /**
     * Récupère le libellé de l'étape sélectionnée sous la forme Type étape - Poste'
     *
     * @param session
     * @param dossierLink
     * @return
     */
    String getCurrentStep(CoreSession session, STDossierLink dossierLink);

    /**
     * Récupère la date de validation de la dernière étape de la feuille de route
     *
     * @param session
     * @param feuilleRouteId
     * @return
     */
    Date getLastStepDate(CoreSession session, String feuilleRouteId);
}
