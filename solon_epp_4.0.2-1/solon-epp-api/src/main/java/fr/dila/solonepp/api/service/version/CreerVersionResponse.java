package fr.dila.solonepp.api.service.version;

import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Données de la réponse de création de version.
 *
 * @author jtremeaux
 */
public class CreerVersionResponse {
    /**
     * Dossier EPP après création ou modification.
     */
    private DocumentModel dossierDoc;

    /**
     * Événement après création ou modification.
     */
    private DocumentModel evenementDoc;

    /**
     * Version après création ou modification.
     */
    private DocumentModel versionDoc;

    /**
     * Liste des pièces jointes de la version.
     */
    private final List<DocumentModel> pieceJointeDocList;

    /**
     * Constructeur de CreerVersionResponse.
     */
    public CreerVersionResponse() {
        pieceJointeDocList = new ArrayList<DocumentModel>();
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

    /**
     * Getter de pieceJointeDocList.
     *
     * @return pieceJointeDocList
     */
    public List<DocumentModel> getPieceJointeDocList() {
        return pieceJointeDocList;
    }
}
