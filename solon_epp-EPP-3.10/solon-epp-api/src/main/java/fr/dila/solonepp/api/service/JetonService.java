package fr.dila.solonepp.api.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.event.batch.BatchLoggerModel;


/**
 * Service permettant de gérer les jetons de l'application SOLON EPP.
 * 
 * @author jtremeaux
 */
public interface JetonService extends fr.dila.st.api.service.JetonService {

    /**
     * Crée une notification lors de la création / modification / suppression logique d'un objet de référence.
     * 
     * @param session Session
     * @param objetRefType Type de l'objet de référence réinitialisé (Acteur)...
     * @param objetRefDoc Document de l'objet de référence
     * @param objetRefId Identifiant technique de l'objet de référence
     * @throws ClientException
     */
    void createNotificationObjetRefUpdate(CoreSession session, String objetRefType, DocumentModel objetRefDoc, String objetRefId) throws ClientException;

    /**
     * Crée une notification lors de la distribution / mise à jour d'un message pour la publication
     * d'une version d'un événement.
     * 
     * @param session Session
     * @param objetRefType Type de l'objet de référence réinitialisé (Acteur)...
     * @throws ClientException
     */
    void createNotificationObjetRefReset(CoreSession session, String objetRefType) throws ClientException;

    /**
     * Crée une notification lors de la distribution / mise à jour d'un message pour la publication
     * d'une version d'un événement.
     * 
     * @param session Session
     * @param dossierDoc Document dossier parent
     * @param evenementDoc Document événement parent
     * @param versionDoc Document version publié
     * @param messageDoc Document message
     * @param notificationType Type de notification
     * @throws ClientException
     */
    void createNotificationEvenement(CoreSession session, DocumentModel dossierDoc, DocumentModel evenementDoc, DocumentModel versionDoc, DocumentModel messageDoc, String notificationType) throws ClientException;

    /**
     * Crée les notifications pour tous les messages d'un événement.
     * 
     * @param session Session
     * @param evenementDoc Document événement
     * @throws ClientException
     */
    void createNotificationEvenement(CoreSession session, DocumentModel evenementDoc, String notificationType) throws ClientException;

    /**
     * Recherche les n dernières notifications.
     * 
     * @param session Session
     * @param proprietaireId Identifiant technique du propriétaire
     * @param jetonType Type de jeton  / notification
     * @param size Nombre de notifications maximum à retourner
     * @return Liste de documents JetonDoc
     * @throws ClientException
     */
    List<DocumentModel> findNotification(CoreSession session, String proprietaireId, String jetonType, long size) throws ClientException;
    
    /**
     * Retourne le tableau des paramètres correspondant à la notification d'une table de référence, afin de construire
     * le mail ou la notification.
     * 
     * @param session Session
     * @param jetonDocModel Document JetonDoc
     * @return Tableau des paramètres
     * @throws ClientException
     */
    Map<String, Object> getNotificationTableReferenceParam(CoreSession session, DocumentModel jetonDocModel) throws ClientException;

    /**
     * Retourne le tableau des paramètres correspondant à la notification d'un événement, afin de construire
     * le mail ou la notification.
     * 
     * @param session Session
     * @param jetonDocModel Document JetonDoc
     * @return Tableau des paramètres
     * @throws ClientException
     */
    Map<String, Object> getNotificationEvenementParam(CoreSession session, DocumentModel jetonDocModel) throws ClientException;

    /**
     * Envoie un Email de notification lors de la création d'une notification.
     * 
     * @param session session
     * @param jetonDoc Document JetonDoc
     * @throws ClientException
     */
    void sendNotificationEmail(CoreSession session, DocumentModel jetonDoc) throws ClientException;

    /**
     * Notifie les Web Services des institutions interfacées lors de la création d'une notification.
     * 
     * @param session session
     * @param jetonDoc Document JetonDoc
     * @throws ClientException
     */
    void sendNotificationWebService(CoreSession session, DocumentModel jetonDoc) throws ClientException;

    /**
     * Réessaie d'appeler les WS notifierEvenement des institutions après une erreur.
     * 
     * @param session Session
     * @throws ClientException
     */
    void retryNotification(CoreSession session, BatchLoggerModel batchloggerModel, Long nbError) throws ClientException;

    /**
     * Retourne le nombre de jetons sur une corbeille depuis une date donnée
     * @param corbeille
     * @param date
     * @return
     * @throws ClientException 
     */
    Long getCountJetonsCorbeilleSince(CoreSession session, String corbeille, Calendar date) throws ClientException;
    
    /**
     * Retourne le nombre de jetons sur un évènement depuis une date donnée
     * @param evenementId
     * @param date
     * @return
     * @throws ClientException 
     */
    Long getCountJetonsEvenementSince(CoreSession session, String evenementId, Calendar date) throws ClientException;
}
