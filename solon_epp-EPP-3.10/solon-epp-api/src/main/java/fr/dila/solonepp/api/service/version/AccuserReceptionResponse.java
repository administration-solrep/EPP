package fr.dila.solonepp.api.service.version;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Données de la réponse de l'accusé de réception d'une version.
 * 
 * @author jtremeaux
 */
public class AccuserReceptionResponse {
    /**
     * Dossier EPP après traitement.
     */
    private DocumentModel dossierDoc;
    
    /**
     * Événement après traitement.
     */
    private DocumentModel evenementDoc;
    
    /**
     * Version après traitement.
     */
    private DocumentModel versionDoc;

    /**
     * Constructeur de CreerVersionResponse.
     */
    public AccuserReceptionResponse() {
    	// do nothing
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
     * Getter de versionDoc.
     *
     * @return versionDoc
     */
    public DocumentModel getVersionDoc() {
        return versionDoc;
    }

    /**
     * Setter de versionDoc.
     *
     * @param versionDoc versionDoc
     */
    public void setVersionDoc(DocumentModel versionDoc) {
        this.versionDoc = versionDoc;
    }
}
