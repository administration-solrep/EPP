package fr.dila.solonepp.api.service;

import fr.dila.solonepp.api.service.corbeilletype.CorbeilleNode;
import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Service permettant de gérer les types de corbeilles.
 *
 * @author jtremeaux
 */
public interface CorbeilleTypeService extends Serializable {
    /**
     * Retourne l'arborescence des sections / corbeilles pour une institution.
     *
     * @param institution Type d'institution
     * @return Arborescence des noeuds de corbeille
     */
    List<CorbeilleNode> getCorbeilleInstitutionTree(String institution);

    /**
     * Retourne l'arborescence des sections / corbeilles pour une institution.
     *
     * @param institution Type d'institution
     * @return Arborescence des noeuds de corbeille
     */
    List<CorbeilleNode> getCorbeilleInstitutionTreeWithCount(String institution, CoreSession session);

    /**
     * Recherche la liste des corbeilles de distribution d'un message, en fonction des données
     * de l'événement et du message.
     *
     * @param institution Institution
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     * @param evenementType Type d'événement (EVT01, ...)
     * @return Liste des corbeilles de distribution pour cette institution
     */
    List<String> findCorbeilleDistribution(String institution, String messageType, String evenementType);
}
