package fr.dila.ss.api.service;

import fr.dila.ss.api.tree.SSTreeFile;
import fr.dila.ss.api.tree.SSTreeFolder;
import fr.dila.ss.api.tree.SSTreeNode;
import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

public interface SSTreeService extends Serializable {
    // /////////////////
    // méthodes concernant les fichiers
    // ////////////////
    /**
     * Créé (persiste) un fichier dans un répertoire
     *
     * @param session
     *            session utilisée pour créé le fichier
     * @param bareFileDoc
     *            le documentModel du fichier non persisté
     * @return le documentModel du fichier créé
     */
    DocumentModel persistFileInFolder(final CoreSession session, DocumentModel bareFileDoc);

    /**
     * Créé un fichier dans un répertoire parent
     *
     * @param session
     *            session utilisée pour créé le fichier
     * @param parent
     *            le répertoire parent du document
     * @param documentType
     *            le type de document à créer
     * @param fileName
     *            le nom du document à créer
     * @param content
     *            le contenu du fichier (peut être null)
     * @return le documentModel du fichier créé
     */
    DocumentModel createFileInFolder(
        final CoreSession session,
        DocumentModel parent,
        String documentType,
        String fileName,
        Blob content
    );

    /**
     * Créé un fichier <b>non persisté</b> dans un répertoire parent
     *
     * @param session
     *            session utilisée pour créé le fichier
     * @param parent
     *            le répertoire parent du document
     * @param documentType
     *            le type de document à créer
     * @param fileName
     *            le nom du document à créer
     * @param content
     *            le contenu du fichier (peut être null)
     * @return le documentModel du fichier initialisé
     */
    DocumentModel createBareFileInFolder(
        final CoreSession session,
        DocumentModel parent,
        String documentType,
        String fileName,
        Blob content
    );

    /**
     * Supprime un fichier
     */
    void deleteFile(final CoreSession session, DocumentModel document);

    // /////////////////
    // methodes concernant l'ajout et la suppression de répertoires
    // ////////////////
    /**
     * creation d'un nouveau répertoire dans le répertoire sélectionné.
     *
     */
    DocumentModel createNewFolder(
        final CoreSession session,
        final String folderType,
        final DocumentModel parent,
        final String folderName
    );

    /**
     * creation d'un nouveau répertoire avant le répertoire sélectionné.
     *
     */
    DocumentModel createNewFolderBefore(
        final CoreSession session,
        final String folderType,
        final DocumentModel currentDoc,
        final String folderName
    );

    /**
     * creation d'un nouveau répertoire après le répertoire sélectionné.
     *
     */
    DocumentModel createNewFolderAfter(
        final CoreSession session,
        final String folderType,
        final DocumentModel currentDoc,
        final String folderName
    );

    /**
     * Change le titre du répertoire sélectionné.
     */
    DocumentModel renameFolder(final CoreSession session, final DocumentModel folderDoc, final String newTitle);

    /**
     * Supprime le document sélectionné on supprime aussi les documents fils du répertoire.
     */
    void deleteFolder(final CoreSession session, final DocumentModel folderDoc);

    // /////////////////
    // methodes concernant la récupération
    // ////////////////
    /**
     * Récupère les enfants du parent Renseigne la profondeur des SSTreeNode enfant à profondeur parent +1
     */
    List<SSTreeNode> getChildrenNode(final CoreSession session, final SSTreeNode parent);

    /**
     * Récupère les enfants du parent Renseigne la profondeur des SSTreeNode enfant à profondeur parent +1
     */
    List<SSTreeNode> getChildrenNode(final CoreSession session, final DocumentRef parentRef, final int parentDepth);

    /**
     *
     * @param session
     * @param parentDoc
     * @return
     */
    List<? extends SSTreeFolder> getChildrenFolder(final CoreSession session, final DocumentModel parentDoc);

    /**
     * Récupère les enfants répertoire d'un parent
     *
     * @param session
     * @param parentDocRef
     * @return
     */
    List<? extends SSTreeFolder> getChildrenFolder(final CoreSession session, final DocumentRef parentDocRef);

    /**
     *
     * @param session
     * @param parentDoc
     * @return
     */
    List<? extends SSTreeFile> getChildrenFile(final CoreSession session, final DocumentModel parentDoc);

    /**
     * Récupère les enfants fichier d'un parent
     *
     * @param session
     * @param parentDocRef
     * @return
     */
    List<? extends SSTreeFile> getChildrenFile(final CoreSession session, final DocumentRef parentDocRef);

    /**
     * restaure la précédente version du document de l'arborescence
     *
     * @param session
     * @param currentDocId
     *            id du document à restaurer
     * @param dossierDoc
     *            le dossier lié à l'arborescence
     */
    void restoreToPreviousVersion(CoreSession session, String currentDocId, DocumentModel dossierDoc);

    /**
     * détermine si un répertoire est vide
     *
     * @param documentManager
     * @param folder
     * @return true si vide, false sinon
     */
    boolean isFolderEmpty(CoreSession documentManager, SSTreeFolder folder);

    /**
     * Met à jour un fichier de l'arborescence (montée de version, modification nom, contenu, auteur)
     *
     * @param session
     * @param fichier
     * @param blob
     * @param currentUser
     * @param dossierDocument
     */
    void updateFile(
        CoreSession session,
        DocumentModel fichier,
        Blob blob,
        NuxeoPrincipal currentUser,
        DocumentModel dossierDocument
    );

    /**
     * Renomme un document et met à jour son path en conséquence
     * @param session
     * @param document
     * @param newTitle
     * @return
     */
    DocumentModel renameDocument(CoreSession session, DocumentModel document, String newTitle);
}
