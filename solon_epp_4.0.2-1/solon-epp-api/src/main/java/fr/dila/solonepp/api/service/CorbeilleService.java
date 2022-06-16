package fr.dila.solonepp.api.service;

import fr.dila.solonepp.api.service.rechercherMessage.RechercherMessageContext;
import java.io.Serializable;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Service qui permet de gérer les corbeilles institutions, c'est à dire la recherche de événements
 * via leur distribution (Messages).
 *
 * @author jtremeaux
 * @author sly
 */
public interface CorbeilleService extends Serializable {
    /**
     * Recherche les messages de l'utilisateur suivant les critères fournis en paramètre.
     *
     * @param session Session
     * @param rechercherMessageContext Contexte de recherche
     */
    void findMessage(CoreSession session, RechercherMessageContext rechercherMessageContext);

    /**
     * Retourne le nombre de messages dans une corbeille.
     *
     * @param session Session
     * @param corbeilleId Identifiant technique de la corbeille
     * @return Nombre de messages dans la corbeille
     */
    long findMessageCount(CoreSession session, String corbeilleId);

    /**
     * Indique s'il existe des communications à l'état "non_traite" ou "en_cours_redaction"
     * @param session
     * @param corbeilleId : l'id de corbeille dans lequel rechercher l'info
     * @return
     */
    boolean hasCommunicationNonTraites(CoreSession session, String corbeilleId);
}
