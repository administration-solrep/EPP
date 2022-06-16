package fr.dila.st.api.service;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * Interface du gestionnaire d'utilisateurs du socle transverse.
 *
 * @author jtremeaux
 */
public interface STUserManager extends UserManager {
    /**
     * Détermine si l'utilisateur est un utilisateur technique (ne reçoit pas de délégation, ...)
     *
     * @param userId
     *            Identifiant technique de l'utilisateur
     * @return Utilisateur technique
     */
    boolean isTechnicalUser(String userId);

    /**
     * Suppression physique d'un utilisateur
     *
     * @param userModel
     */
    void physicalDeleteUser(DocumentModel userModel);

    /**
     * Suppression physique d'un utilisateur
     *
     * @param userId
     */
    void physicalDeleteUser(String userId);

    void invalidateAllPrincipals();

    String getUserId(DocumentModel userModel);
}
