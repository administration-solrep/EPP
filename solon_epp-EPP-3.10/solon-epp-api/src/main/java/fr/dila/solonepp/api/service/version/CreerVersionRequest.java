package fr.dila.solonepp.api.service.version;

import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Données de la requête pour créer une version d'un événement.
 * 
 * @author jtremeaux
 */
public class CreerVersionRequest {
    /**
     * Dossier EPP à créer / modifier.
     */
    private DocumentModel dossierDoc;
    
    /**
     * Événement à créer / modifier.
     */
    private DocumentModel evenementDoc;
    
    /**
     * Version à créer / modifier.
     */
    private final VersionContainer versionContainer;
    
    /**
     * Version supplémentaire à créer / modifier.
     * Ce conteneur est facultatif, et contient la version brouillon à reporter en mode delta.
     */
    private VersionContainer secondaryVersionContainer;
    
    /**
     * Constructeur de CreerVersionRequest.
     */
    public CreerVersionRequest() {
        versionContainer = new VersionContainer();
    }
    
    /**
     * Retourne le document de la version principale.
     *
     * @return versionDoc
     */
    public DocumentModel getVersionDoc() {
        return versionContainer.getVersionDoc();
    }

    /**
     * Renseigne le document de la version principale.
     *
     * @param versionDoc versionDoc
     */
    public void setVersionDoc(DocumentModel versionDoc) {
        versionContainer.setVersionDoc(versionDoc);
    }

    /**
     * Retourne la liste des documents pièces jointes de la version principale.
     *
     * @return pieceJointeDocList
     */
    public List<DocumentModel> getPieceJointeDocList() {
        return versionContainer.getPieceJointeDocList();
    }

    /**
     * Getter de dossierDoc.
     *
     * @return dossierDoc
     */
    public DocumentModel getDossierDoc() {
        return dossierDoc;
    }

    /**
     * Setter de dossierDoc.
     *
     * @param dossierDoc dossierDoc
     */
    public void setDossierDoc(DocumentModel dossierDoc) {
        this.dossierDoc = dossierDoc;
    }

    /**
     * Getter de evenementDoc.
     *
     * @return evenementDoc
     */
    public DocumentModel getEvenementDoc() {
        return evenementDoc;
    }

    /**
     * Setter de evenementDoc.
     *
     * @param evenementDoc evenementDoc
     */
    public void setEvenementDoc(DocumentModel evenementDoc) {
        this.evenementDoc = evenementDoc;
    }

    /**
     * Getter de secondaryVersionContainer.
     *
     * @return secondaryVersionContainer
     */
    public VersionContainer getSecondaryVersionContainer() {
        return secondaryVersionContainer;
    }

    /**
     * Setter de secondaryVersionContainer.
     *
     * @param secondaryVersionContainer secondaryVersionContainer
     */
    public void setSecondaryVersionContainer(VersionContainer secondaryVersionContainer) {
        this.secondaryVersionContainer = secondaryVersionContainer;
    }

}
