package fr.dila.solonepp.api.service;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer les actions disponibles sur les versions.
 * 
 * @author jtremeaux
 */
public interface VersionActionService extends Serializable {
    /**
     * Retourne la liste des actions possibles pour une institution et une version particulière d'un événement.
     * 
     * @param session Session
     * @param evenementDoc Document événement
     * @param versionDoc Document version
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     * @return Liste d'actions possibles
     * @throws ClientException
     */
    List<String> findActionPossible(CoreSession session, DocumentModel evenementDoc, DocumentModel versionDoc, String messageType, String etatMessage) throws ClientException;
}
