package fr.dila.solonepp.api.service.version;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Données de la réponse pour accepter / rejeter / abandonner une version rectifiée.
 * 
 * @author jtremeaux
 */
public class ValiderVersionResponse {
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
     * Constructeur de ValiderVersionResponse.
     */
    public ValiderVersionResponse() {
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
