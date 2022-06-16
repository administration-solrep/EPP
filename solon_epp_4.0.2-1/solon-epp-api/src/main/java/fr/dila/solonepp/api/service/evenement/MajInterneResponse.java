package fr.dila.solonepp.api.service.evenement;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Données de la reponse de la maj du visa interne
 *
 * @author asatre
 */
public class MajInterneResponse {
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
     * Document message après traitement.
     */
    private DocumentModel messageDoc;

    public DocumentModel getDossierDoc() {
        return dossierDoc;
    }

    public void setDossierDoc(DocumentModel dossierDoc) {
        this.dossierDoc = dossierDoc;
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

    public void setMessageDoc(DocumentModel messageDoc) {
        this.messageDoc = messageDoc;
    }

    public DocumentModel getMessageDoc() {
        return messageDoc;
    }
}
