package fr.dila.solonepp.api.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.descriptor.metadonnees.PropertyDescriptor;

/**
 * Service permettant de gérer le cycle de vie du dossier SOLON EPP.
 * 
 * @author jtremeaux
 */
public interface DossierService extends Serializable {

    /**
     * Supprime un dossier.
     * 
     * @param session Session
     * @param dossierDoc Dossier à supprimer
     * @throws ClientException
     */
    void deleteDossier(CoreSession session, DocumentModel dossierDoc) throws ClientException;

    /**
     * Retourne le document racine des dossiers.
     * 
     * @param session Session
     * @return Document racine des dossiers
     * @throws ClientException
     */
    DocumentModel getDossierRoot(CoreSession session) throws ClientException;

    /**
     * Retourne un dossier par son identifiant technique. Cette méthode n'échoue jamais, elle retourne null si le dossier n'existe pas.
     * 
     * @param session Session
     * @param dossierId Identifiant technique du dossier
     * @return Document dossier
     * @throws ClientException
     */
    DocumentModel getDossier(CoreSession session, String dossierId) throws ClientException;

    /**
     * Crée un dossier SOLON EPP.
     * 
     * @param session Session
     * @param dossierDoc Dossier à créer
     * @return Document dossier créé
     * @throws ClientException
     */
    DocumentModel createDossier(CoreSession session, DocumentModel dossierDoc) throws ClientException;

    /**
     * Crée un nouveau document dossier (uniquement en mémoire).
     * 
     * @param session Session
     * @return Nouveau document dossier
     * @throws ClientException
     */
    DocumentModel createBareDossier(CoreSession session) throws ClientException;

    /**
     * Mise à jour de la fiche dossier avec les données de l'evenement et de la version
     * 
     * @param session
     * @param versionDoc
     * @throws ClientException
     */
    void updateFicheDossier(CoreSession session, DocumentModel versionDoc) throws ClientException;

    /**
     * Retourne la liste des champs de la fiche dossier
     * 
     * @param session
     * @param dossierDoc
     * @return map clef : schema (Version ou Evenement) et liste de descriptor correspondant
     * @throws ClientException
     */
    Map<String, List<PropertyDescriptor>> getFicheDossierFields(CoreSession session, DocumentModel dossierDoc) throws ClientException;

    /**
     * Retourne un set contenant la liste des types d'événement constituant le dossier
     * 
     * @param session
     * @param dossierDoc
     * @return
     * @throws ClientException
     */
    Set<String> getEvenementTypeDossierList(CoreSession session, DocumentModel dossierDoc) throws ClientException;

    /**
     * Retourne une liste de dossier
     * 
     * @param session
     * @param ids
     * @return
     * @throws ClientException
     */
    List<DocumentModel> getDossierList(CoreSession session, Collection<String> ids) throws ClientException;

}
