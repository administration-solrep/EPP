package fr.dila.solonepp.api.service;

import fr.dila.solonepp.api.dao.criteria.PieceJointeCriteria;
import fr.dila.solonepp.api.dto.PieceJointeDTO;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer les pièces jointes des versions.
 *
 * @author jtremeaux
 */
public interface PieceJointeService extends Serializable {
    /**
     * Recherche des pièces jointes par critère.
     *
     * @param session Session
     * @param criteria Critères de recherche
     * @return Liste de pièces jointes
     */
    List<DocumentModel> findPieceJointeByCriteria(CoreSession session, PieceJointeCriteria criteria);

    /**
     * Crée un nouveau document pièce jointe (uniquement en mémoire).
     *
     * @param session Session
     * @return Nouveau document pièce jointe
     */
    DocumentModel createBarePieceJointe(CoreSession session);

    /**
     * Retourne les pièces jointes d'une version d'un événement.
     *
     * @param session Session
     * @param versionDoc document version
     * @return Liste des documents pièces jointes de la version
     */
    List<DocumentModel> getVersionPieceJointeList(CoreSession session, DocumentModel versionDoc);

    /**
     * Retourne les pièces jointes d'une version d'un événement.
     * Charge également la liste des fichiers de pièces jointes.
     *
     * @param session Session
     * @param versionDoc document version
     * @return Liste des documents pièces jointes de la version (contiennent les documents fichiers)
     */
    List<DocumentModel> getVersionPieceJointeListWithFichier(CoreSession session, DocumentModel versionDoc);

    /**
     * Crée une pièce jointe.
     *
     * @param session Session
     * @param evenementDoc Document Événement
     * @param versionDoc Document version
     * @param pieceJointeDoc Pièce jointe
     * @return Pièce jointe créée
     */
    DocumentModel createPieceJointe(
        CoreSession session,
        DocumentModel evenementDoc,
        DocumentModel versionDoc,
        DocumentModel pieceJointeDoc
    );

    /**
     * Crée, modifie ou supprime les pièces jointes d'une version.
     *
     * @param session Session
     * @param evenementDoc Événement
     * @param versionDoc Version
     * @param pieceJointeDocList Pièces jointes de la nouvelle version
     * @param strictMode Interdit la suppression de pièce jointe, et la modification ou suppression de fichiers
     * @return Liste des pièces jointes de la nouvelle version
     */
    List<DocumentModel> updatePieceJointeList(
        CoreSession session,
        DocumentModel evenementDoc,
        DocumentModel versionDoc,
        List<DocumentModel> pieceJointeDocList,
        boolean strictMode
    );

    /**
     * Retourne le liste des pieces jointes fichiers par piece jointe
     *
     * @param typePieceJointe
     * @param idVersion
     * @param session
     * @return
     */
    List<PieceJointeDTO> findAllPieceJointeFichierByVersionAndType(
        String typePieceJointe,
        String idVersion,
        CoreSession session
    );

    /**
     *
     * @param session
     * @param typePieceJointe
     * @param evenementDoc
     * @param versionDoc
     * @return
     */
    List<PieceJointeDTO> getDeletedPieceJointeList(
        CoreSession session,
        String typePieceJointe,
        DocumentModel evenementDoc,
        DocumentModel versionDoc
    );

    String getDeletedUrl(
        CoreSession session,
        String typePieceJointe,
        DocumentModel evenementDoc,
        DocumentModel versionDoc
    );

    /**
     *
     * @param session
     * @param typePieceJointe
     * @param evenementDoc
     * @param versionDoc
     * @return
     */
    Set<String> getNewPieceJointeTitreList(
        CoreSession session,
        String typePieceJointe,
        DocumentModel evenementDoc,
        DocumentModel versionDoc
    );

    /**
     *
     * @param session
     * @param versionDoc
     * @param evenementDoc
     * @return
     */
    List<String> getPieceJointeAjouteeListe(CoreSession session, DocumentModel versionDoc, DocumentModel evenementDoc);

    /**
     *
     * @param session
     * @param versionDoc
     * @param evenementDoc
     * @return
     */
    List<String> getPieceJointeSupprimeeListe(
        CoreSession session,
        DocumentModel versionDoc,
        DocumentModel evenementDoc
    );
}
