package fr.dila.ss.api.service;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import org.nuxeo.ecm.core.api.CoreSession;

public interface SSOrganigrammeService extends OrganigrammeService {
    /**
     * Déplace le contenu du noeud 'nodeToCopy' dans le noeud 'destinationNode'.
     *
     * @param coreSession
     * @param nodeToCopy
     * @param destinationNode
     * @
     */
    void migrateNode(CoreSession coreSession, OrganigrammeNode nodeToCopy, OrganigrammeNode destinationNode);

    /**
     * Récupère un OrganigrammeNode d'id nodeId sans en indiquer le type. Le premier
     * objet correspondant est renvoyé.
     *
     * @param nodeId id du noeud de l'organigramme
     * @return un OrganigrammeNode
     */
    OrganigrammeNode getOrganigrammeNodeById(String nodeId);
}
