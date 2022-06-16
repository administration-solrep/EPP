package fr.dila.st.api.service;

import fr.dila.st.api.parametre.STParametre;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer les paramètres applicatifs administrables dans l'IHM de l'application.
 *
 * @author fesposito
 * @author jtremeaux
 */
public interface STParametreService {
    /**
     * Retourne la valeur du paramètre avec l'identifiant id.
     *
     * @param session
     *            Session
     * @param anid
     *            Identifiant technique du paramètre
     * @return Valeur du paramètre
     *
     */
    String getParametreValue(CoreSession session, String anid);

    /**
     * Retourne le document paramètre avec l'identifieant id .
     *
     * @param session
     *            Session
     * @param anid
     *            Identifiant technique du paramètre
     * @return Objet métier paramètre
     *
     */
    STParametre getParametre(CoreSession session, String anid);

    /**
     * Retourne la racine des paramètres.
     *
     * @param session
     *            Session
     * @return Document racine des paramètres
     *
     */
    DocumentModel getParametreFolder(CoreSession session);

    /**
     * Vide le cache
     */
    void clearCache();

    /**
     * Retourne la valeur du paramètre avec l'identifiant id.
     * (Utiliser cette méthode uniquement lorsqu'on n'a pas encore de session d'ouverte)
     *
     * @param anid
     *            Identifiant technique du paramètre
     * @return Valeur du paramètre
     *
     */
    String getParametreWithoutSession(String anid);
}
