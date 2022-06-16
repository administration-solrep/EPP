package fr.dila.solonepp.core.assembler;

import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Assembleur des données des objets pièce jointe.
 *
 * @author jtremeaux
 */
public class PieceJointeAssembler {

    /**
     * Assemble les propriétés d'une version pour modifier une version existante.
     *
     * @param pieceJointeFromDoc Nouveau document pièce jointe
     * @param pieceJointeToDoc Document pièce jointe à modifier
     */
    public void assemblePieceJointeForUpdate(DocumentModel pieceJointeFromDoc, DocumentModel pieceJointeToDoc) {
        PieceJointe pieceJointeFrom = pieceJointeFromDoc.getAdapter(PieceJointe.class);
        PieceJointe pieceJointeTo = pieceJointeToDoc.getAdapter(PieceJointe.class);

        pieceJointeTo.setNom(pieceJointeFrom.getNom());
        pieceJointeTo.setUrl(pieceJointeFrom.getUrl());
    }
}
