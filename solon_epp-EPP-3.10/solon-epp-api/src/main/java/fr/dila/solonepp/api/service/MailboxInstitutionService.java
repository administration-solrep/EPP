package fr.dila.solonepp.api.service;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer les Mailbox des institutions.
 * 
 * @author jtremeaux
 */
public interface MailboxInstitutionService extends Serializable {
    /**
     * Génère l'identifiant technique d'une Mailbox institution, à partir du nom de l'institution. Cette méthode supprime les caractères spéciaux, la casse, etc.
     * 
     * @param institutionName Nom de l'institution (doit être unique)
     * @return Identifiant technique de la Mailbox
     */
    String getInstitutionMailboxId(String institutionName);

    /**
     * Retourne l'identifiant technique de l'institution correspondante à la mailbox.
     * 
     * @param mailboxId Identifiant technique de la mailbox
     * @return Identifiant technique de l'institution correspondant à la mailbox
     */
    String getInstitutionIdFromMailboxId(String mailboxId);

    /**
     * Retourne la mailbox d'un poste à partir de son identifiant technique. Si la Mailbox n'existe pas, retourne null.
     * 
     * @param session Session
     * @param institutionId Identifiant technique de l'institution
     * @return Document Mailbox
     * @throws ClientException
     */
    DocumentModel getMailboxInstitution(CoreSession session, String institutionId) throws ClientException;

    /**
     * Retourne la mailbox d'un poste à partir de son identifiant technique. Si la Mailbox n'existe pas, retourne null.
     * 
     * @param session Session
     * @param institutionId Identifiant technique de l'institution
     * @return Document Mailbox
     * @throws ClientException
     */
    DocumentModel getMailboxInstitutionUnrestricted(CoreSession session, String institutionId) throws ClientException;

    /**
     * Crée si nécessaire toutes les Mailbox institution.
     * 
     * @param session Session
     * @param mailboxRoot Racine des mailbox
     * @throws ClientException ClientException
     */
    void createAllMailboxInstitution(CoreSession session, DocumentModel mailboxRoot) throws ClientException;
}
