package fr.dila.st.api.service;

import org.nuxeo.ecm.core.api.NuxeoPrincipal;

/**
 * Le service de gestion des token d'authentification.
 *
 * @author slefevre
 */
public interface STTokenService {
    /**
     * Récupère l'utilisateur associé à un token
     *
     * @param token
     */
    String getUserByToken(String token);

    String acquireToken(NuxeoPrincipal userAccount, String comment);

    void revokeToken(String token);

    void purgeInvalidTokens();
}
