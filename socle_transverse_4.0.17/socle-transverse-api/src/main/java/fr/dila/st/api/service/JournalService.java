package fr.dila.st.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.audit.api.LogEntry;

/**
 * Service permettant de requêter le journal d'Audit.
 *
 * @author bby
 */
public interface JournalService {
    /**
     * Retourne le nombre total d'evenement matchant les critères (Requete pour le journal affiche dans l'espace
     * d'administration)
     *
     * @param mapFilter
     *            filtre à appliquer
     * @return retourne le nombre total d'evenement
     */
    int getEventsCount(List<String> dossierIds, Map<String, Object> mapFilter);

    /**
     * Supprime les logs anterieur à dateLimit
     */
    void purger(Date dateLimit);

    /**
     * ajouter des logs journal
     *
     * @param entries
     *            leslogs à ajouter
     */
    void addLogEntries(List<LogEntry> entries);

    /**
     * supprimer tous les logs d'un document
     *
     * @param docId
     *            the document Id
     */
    void purger(final String docId);

    List<LogEntry> queryDocumentAllLogs(
        List<String> dossierIds,
        Map<String, Object> mapFilter,
        int pageNb,
        int pageSize,
        List<SortInfo> sortInfos
    );

    List<LogEntry> getLogEntries(String dossierId);

    /**
     * Journalise une action de catégorie feuille de route
     *
     * @param session
     * @param dossierDoc
     *            DocumentModel : le dossier sur laquelle l'action a été effectuée
     * @param eventName
     *            String : le nom de l'évènement déclenché pour la journalisation
     * @param comment
     *            String : commentaire utilisé pour la journalisation
     */
    void journaliserActionFDR(CoreSession session, DocumentModel dossierDoc, String eventName, String comment);

    /**
     * Journalise une action de catégorie bordereau
     *
     * @param session
     * @param dossierDoc
     *            DocumentModel : le dossier sur laquelle l'action a été effectuée
     * @param eventName
     *            String : le nom de l'évènement déclenché pour la journalisation
     * @param comment
     *            String : commentaire utilisé pour la journalisation
     */
    void journaliserActionBordereau(CoreSession session, DocumentModel dossierDoc, String eventName, String comment);

    /**
     * Journalise une action de catégorie parapheur
     *
     * @param session
     * @param dossierDoc
     *            DocumentModel : le dossier sur laquelle l'action a été effectuée
     * @param eventName
     *            String : le nom de l'évènement déclenché pour la journalisation
     * @param comment
     *            String : commentaire utilisé pour la journalisation
     */
    void journaliserActionParapheur(CoreSession session, DocumentModel dossierDoc, String eventName, String comment);

    /**
     * Journalise une action de catégorie fond de dossier
     *
     * @param session
     * @param dossierDoc
     *            DocumentModel : le dossier sur laquelle l'action a été effectuée
     * @param eventName
     *            String : le nom de l'évènement déclenché pour la journalisation
     * @param comment
     *            String : commentaire utilisé pour la journalisation
     */
    void journaliserActionFDD(CoreSession session, DocumentModel dossierDoc, String eventName, String comment);

    /**
     * Journalise une action de catégorie administration sur un dossier
     *
     * @param session
     * @param dossierDoc
     *            DocumentModel : le dossier sur laquelle l'action a été effectuée
     * @param eventName
     *            String : le nom de l'évènement déclenché pour la journalisation
     * @param comment
     *            String : commentaire utilisé pour la journalisation
     */
    void journaliserActionAdministration(
        CoreSession session,
        DocumentModel dossierDoc,
        String eventName,
        String comment
    );

    /**
     * Journalise une action de catégorie administration
     *
     * @param session
     * @param eventName
     *            String : le nom de l'évènement déclenché pour la journalisation
     * @param comment
     *            String : commentaire utilisé pour la journalisation
     */
    void journaliserActionAdministration(CoreSession session, String eventName, String comment);

    /**
     * Journalise une action sur un dossier avec un DocumentEventContext
     *
     * @param session
     * @param dossierDoc
     *            DocumentModel : le dossier sur laquelle l'action a été effectuée
     * @param eventName
     *            String : le nom de l'évènement déclenché pour la journalisation
     * @param comment
     *            String : commentaire de la journalisation
     * @param category
     *            String : catégorie de journalisation
     */
    void journaliserAction(
        CoreSession session,
        DocumentModel dossierDoc,
        String eventName,
        String comment,
        String category
    );

    /**
     * Journalise une action sur un dossier avec un DocumentEventContext en utilisant l'utilisateur principal passé en
     * paramètre
     *
     * @param session
     * @param dossierDoc
     * @param eventName
     * @param comment
     * @param category
     */
    void journaliserActionForUser(
        CoreSession session,
        NuxeoPrincipal user,
        DocumentModel dossierDoc,
        String eventName,
        String comment,
        String category
    );

    /**
     *
     */
    void journaliserActionAdministration(CoreSession session, String idDossier, String eventName, String comment);

    /**
     * Journalise une action de catégorie administration avec un identifiant dossier et un principal donné
     * @param session
     * @param principal
     * @param idDossier
     * @param eventName
     * @param comment
     */
    void journaliserActionAdministration(
        CoreSession session,
        NuxeoPrincipal principal,
        String idDossier,
        String eventName,
        String comment
    );

    /**
     * Journalise une action de catégorie administration avec un dossier et un principal donné
     * @param session
     * @param principal
     * @param dossierDoc
     * @param eventName
     * @param comment
     */
    void journaliserActionAdministration(
        CoreSession session,
        NuxeoPrincipal principal,
        DocumentModel dossierDoc,
        String eventName,
        String comment
    );

    /**
     * Journalise une action dans le PAN (EPG) avec un EventContext en utilisant l'utilisateur principal passé en
     * paramètre
     *
     * @param session
     * @param idDossier
     * @param eventName
     * @param comment
     * @param category
     */
    void journaliserActionPAN(CoreSession session, String idDossier, String eventName, String comment, String category);

    void journaliserAction(String username, String idDossier, String eventName, String comment, String category);

    void journaliserActionAdministration(String username, String eventName, String comment);
}
