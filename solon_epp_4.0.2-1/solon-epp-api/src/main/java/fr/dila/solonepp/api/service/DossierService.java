package fr.dila.solonepp.api.service;

import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

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
     */
    void deleteDossier(CoreSession session, DocumentModel dossierDoc);

    /**
     * Retourne le document racine des dossiers.
     *
     * @param session Session
     * @return Document racine des dossiers
     */
    DocumentModel getDossierRoot(CoreSession session);

    /**
     * Retourne un dossier par son identifiant technique. Cette méthode n'échoue jamais, elle retourne null si le dossier n'existe pas.
     *
     * @param session Session
     * @param dossierId Identifiant technique du dossier
     * @return Document dossier
     */
    DocumentModel getDossier(CoreSession session, String dossierId);

    /**
     * Crée un dossier SOLON EPP.
     *
     * @param session Session
     * @param dossierDoc Dossier à créer
     * @return Document dossier créé
     */
    DocumentModel createDossier(CoreSession session, DocumentModel dossierDoc);

    /**
     * Crée un nouveau document dossier (uniquement en mémoire).
     *
     * @param session Session
     * @return Nouveau document dossier
     */
    DocumentModel createBareDossier(CoreSession session);

    /**
     * Mise à jour de la fiche dossier avec les données de l'evenement et de la version
     *
     * @param session
     * @param versionDoc
     */
    void updateFicheDossier(CoreSession session, DocumentModel versionDoc);

    /**
     * Retourne la liste des champs de la fiche dossier
     *
     * @param session
     * @param dossierDoc
     * @return map clef : schema (Version ou Evenement) et liste de descriptor correspondant
     */
    Map<String, List<PropertyDescriptor>> getFicheDossierFields(CoreSession session, DocumentModel dossierDoc);

    /**
     * Retourne un set contenant la liste des types d'événement constituant le dossier
     *
     * @param session
     * @param dossierDoc
     * @return
     */
    Set<String> getEvenementTypeDossierList(CoreSession session, DocumentModel dossierDoc);

    /**
     * Retourne une liste de dossier
     *
     * @param session
     * @param ids
     * @return
     */
    List<DocumentModel> getDossierList(CoreSession session, Collection<String> ids);

    /**
     * Vérifie la validité de l'identifiant du dossier.
     *
     * @param idDossier
     * @return
     */
    boolean isIdDossierValid(String idDossier);
}
