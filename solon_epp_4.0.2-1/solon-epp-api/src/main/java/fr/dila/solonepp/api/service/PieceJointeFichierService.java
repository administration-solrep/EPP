package fr.dila.solonepp.api.service;

import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer les fichier des pièces jointes.
 *
 * @author jtremeaux
 */
public interface PieceJointeFichierService extends Serializable {
    /**
     * Retourne le document racine des fichiers de pièces jointes.
     *
     * @param session Session
     * @return Document racine des fichiers de pièces jointes
     */
    DocumentModel getPieceJointeFichierRoot(CoreSession session);

    /**
     * Crée un nouveau document fichier de pièce jointe (uniquement en mémoire).
     *
     * @param session Session
     * @return Nouveau document fichier de pièce jointe
     */
    DocumentModel createBarePieceJointeFichier(CoreSession session);

    /**
     * Crée un fichier de pièce jointe.
     *
     * @param session Session
     * @param evenementDoc Document événement
     * @param pieceJointeFichierDoc Document fichier de pièce jointe à créer
     * @return Document fichier de pièce jointe créé
     */
    DocumentModel createPieceJointeFichier(
        CoreSession session,
        DocumentModel evenementDoc,
        DocumentModel pieceJointeFichierDoc
    );

    /**
     * Met à jour la liste des fichiers d'une pièce jointe.
     * Crée le nouveaux document fichiers si nécessaire, sinon met à jour les documents fichiers existants.
     * Les documents fichier non présent dans la nouvelle pièce jointe ne sont pas supprimés.
     *
     * @param session Session
     * @param evenementDoc Document événement
     * @param pieceJointeDoc Document pièce jointe à modifier
     * @param pieceJointeFichierDocList Nouvelle liste de fichiers de pièce jointe
     * @param strictMode Interdit la suppression de pièce jointe, et la modification ou suppression de fichiers
     * @return Liste des documents fichiers de pièce après modification
     */
    List<DocumentModel> updatePieceJointeFichierList(
        CoreSession session,
        DocumentModel evenementDoc,
        DocumentModel pieceJointeDoc,
        List<DocumentModel> pieceJointeFichierDocList,
        boolean strictMode
    );

    /**
     * Supprime le fichier de pièce jointe précédemment utilisé dans la pièce jointe fournie en paramètre.
     * Le fichier de pièce jointe est supprimé uniquement si il n'est pas utilisé dans d'autres pièces jointes.
     *
     * @param session Session
     * @param pieceJointeDoc Pièce jointe supprimé ou modifié
     * @param pieceJointeFichierId UUID du fichier de pièce jointe à supprimer
     */
    void removePieceJointeFichier(CoreSession session, DocumentModel pieceJointeDoc, String pieceJointeFichierId);

    /**
     * Retourne la liste des fichiers de la pièce jointe.
     *
     * @param session Session
     * @param pieceJointeDoc Document pièce jointe
     * @return Liste de documents fichiers de pièces jointes
     */
    List<DocumentModel> findAllPieceJointeFichier(CoreSession session, DocumentModel pieceJointeDoc);
}
