package fr.dila.solonepp.api.service.version;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Objet de transfert pour créer / modifier une version et les PJ associées.
 * 
 * @author jtremeaux
 */
public class VersionContainer {
    /**
     * Version à créer / modifier.
     */
    private DocumentModel versionDoc;
    
    /**
     * Liste des pièces jointes à créer / modifier.
     */
    private final List<DocumentModel> pieceJointeDocList;
    
    /**
     * Constructeur de VersionContainer.
     */
    public VersionContainer() {
        pieceJointeDocList = new ArrayList<DocumentModel>();
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
