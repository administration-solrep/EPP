package fr.dila.solonepp.api.service;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.solonepp.api.service.corbeilletype.CorbeilleNode;

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
     * @throws ClientException
     */
    List<CorbeilleNode> getCorbeilleInstitutionTree(String institution) throws ClientException;
    
    /**
     * Retourne l'arborescence des sections / corbeilles pour une institution.
     * 
     * @param institution Type d'institution
     * @return Arborescence des noeuds de corbeille
     * @throws ClientException
     */
    List<CorbeilleNode> getCorbeilleInstitutionTreeWithCount(String institution, CoreSession session) throws ClientException;

    /**
     * Recherche la liste des corbeilles de distribution d'un message, en fonction des données
     * de l'événement et du message.
     * 
     * @param institution Institution
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     * @param evenementType Type d'événement (EVT01, ...)
     * @return Liste des corbeilles de distribution pour cette institution
     * @throws ClientException
     */
    List<String> findCorbeilleDistribution(String institution, String messageType, String evenementType) throws ClientException;
}
