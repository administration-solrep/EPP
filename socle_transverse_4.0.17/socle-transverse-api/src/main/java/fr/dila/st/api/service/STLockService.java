package fr.dila.st.api.service;

import fr.dila.st.api.dossier.STDossier;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

//import org.nuxeo.ecm.webapp.edit.lock.LockActions;

/**
 * Interface du service de verrous du socle transverse.
 *
 * @author jtremeaux
 */
public interface STLockService extends Serializable {
    String LOCKER = "document.locker";

    String LOCK_TIME = "document.lock.time";

    boolean unlockDoc(DocumentModel document, CoreSession documentManager);

    /**
     * Verrouille un document.
     *
     * @param session
     *            Session
     * @param document
     *            Document à verrouiller
     * @return Succès
     *
     */
    boolean lockDoc(CoreSession session, DocumentModel document);

    /**
     * Unlock du dossier et de la route si nécessaire
     *
     * @param session
     * @param dossier
     *
     */
    void unlockDossierAndRoute(CoreSession session, STDossier dossier);

    /**
     * Déverrouille un document, même si l'utilisateur ne possède pas les droits nécessaires.
     *
     * @param session
     *            Session
     * @param document
     *            Document à déverrouiller
     * @return Succès
     *
     */
    boolean unlockDocUnrestricted(CoreSession session, DocumentModel document);

    /**
     * Déverrouille un document.
     *
     * @param session
     *            Session
     * @param document
     *            Document à déverrouiller
     * @return Succès
     *
     */
    boolean unlockDoc(CoreSession session, DocumentModel document);

    /**
     * Retourne les données de verrou du document.
     *
     * @param session
     *            Session
     * @param document
     *            Document
     * @return Données de verrou du document
     *
     */
    Lock getLockDetails(CoreSession session, DocumentModel document);

    /**
     * Retourne vrai si le verrou est verrouillé.
     *
     * @param session
     *            Session
     * @param document
     *            Document
     * @return Vrai si le document est verrouillé
     *
     */
    boolean isLocked(CoreSession session, DocumentModel document);

    /**
     * Retourne vrai si le verrou est modifiable par l'utilisateur.
     *
     * @param session
     *            Session
     * @param document
     *            Document
     * @param user
     *            Utilisateur
     * @return Vrai si le verrou est modifible par l'utilisateur
     *
     */
    boolean isLockActionnableByUser(CoreSession session, DocumentModel document, NuxeoPrincipal user);

    /**
     * Retourne vrai si le document est verrouillé par l'utilisateur.
     *
     * @param session
     *            Session
     * @param document
     *            Document
     * @param user
     *            Utilisateur
     * @return Vrai si le verrou est modifible par l'utilisateur
     *
     */
    boolean isLockByUser(CoreSession session, DocumentModel document, NuxeoPrincipal user);

    /**
     * Retourne vrai si le document est verrouillé par l'utilisateur courrant.
     *
     * @param session
     *            Session
     * @param document
     *            Document
     * @return Vrai si le verrou est modifible par l'utilisateur
     *
     */
    boolean isLockByCurrentUser(CoreSession session, DocumentModel document);

    /**
     * Cherche dans la base pour les ids des documents donnés en parametre ceux qui sont verrouillé
     *
     * @param session
     * @param docIds
     * @return ensemble d'id verrouillé
     *
     */
    Set<String> extractLocked(CoreSession session, Collection<String> docIds);

    /**
     * Cherche dans la base pour les ids des documents donnés en parametre ceux qui sont verrouillé et le login de
     * l'utilisateur ayant locké le document.
     *
     * @param session
     * @param docIds
     * @return ensemble d'id verrouillé
     *
     */
    Map<String, String> extractLockedInfo(CoreSession session, Collection<String> docIds);

    /**
     * unlock tous les documents lockés par l'utilisateur
     *
     * @param session
     * @param userId
     *
     */
    void unlockAllDocumentLockedByUser(CoreSession session, String userId);
}
