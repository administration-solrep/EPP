package fr.dila.solonepp.api.service.evenement;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Données de la réponse de l'initialisation d'evenement.
 *
 * @author asatre
 */
public class InitialiserEvenementResponse {
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

    public InitialiserEvenementResponse() {
        // do nothing
    }

    public DocumentModel getEvenementDoc() {
        return evenementDoc;
    }

    public void setEvenementDoc(DocumentModel evenementDoc) {
        this.evenementDoc = evenementDoc;
    }

    public DocumentModel getVersionDoc() {
        return versionDoc;
    }

    public void setVersionDoc(DocumentModel versionDoc) {
        this.versionDoc = versionDoc;
    }

    public DocumentModel getDossierDoc() {
        return dossierDoc;
    }

    public void setDossierDoc(DocumentModel dossierDoc) {
        this.dossierDoc = dossierDoc;
    }
}
