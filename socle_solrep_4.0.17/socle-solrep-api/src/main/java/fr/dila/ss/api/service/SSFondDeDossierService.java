package fr.dila.ss.api.service;

import fr.dila.ss.api.exception.SSException;
import fr.dila.st.api.dossier.STDossier;
import java.util.List;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface SSFondDeDossierService extends SSTreeService {
    /**
     * Créé un nouveau fichier dans l'arborescence de fond de dossier dans le répertoire parent
     *
     * @param session
     * @param parent
     *            le répertoire dans lequel placer le fichier
     * @param fileName
     *            le nom du fichier
     * @param content
     *            le contenu du fichier
     * @return le DocumentModel du fichier créé
     * @throws SSException
     */
    DocumentModel createFileInFolder(CoreSession session, DocumentModel parent, String fileName, Blob content)
        throws SSException;

    /**
     * Créé un FondDeDossierFichier non persisté
     *
     * @param session
     * @return DocumentModel du FondDeDossierFichier non persisté
     */
    DocumentModel createBareFondDeDossierFichier(CoreSession session) throws SSException;

    /**
     * Créé un FondDeDossierFichier non persisté
     *
     * @param session
     * @param parent
     *            le répertoire du fichier
     * @param filename
     *            le nom du fichier
     * @param content
     *            le contenu du fichier
     * @return DocumentModel du FondDeDossierFichier non persisté
     */
    DocumentModel createBareFondDeDossierFichier(
        CoreSession session,
        DocumentModel parent,
        String filename,
        Blob content
    )
        throws SSException;

    /**
     * Créé un FondDeDossierFichier non persisté
     *
     * @param session
     * @param parent
     *            le répertoire du fichier
     * @param filename
     *            le nom du fichier
     * @return DocumentModel du FondDeDossierFichier non persisté
     */
    DocumentModel createBareFondDeDossierFichier(CoreSession session, DocumentModel parent, String filename)
        throws SSException;

    /**
     * Créé un répertoire de fond de dossier
     *
     * @param session
     * @param parent
     * @param docName
     * @return
     */
    DocumentModel createFondDeDossierRepertoire(CoreSession session, DocumentModel parentDoc, String docName)
        throws SSException;

    /**
     * @return le type de document pour le répertoire de fond de dossier
     */
    String getFondDeDossierRepertoireType();

    void initFondDeDossier(CoreSession session, DocumentModel fondDeDossierDoc);

    /**
     * Supprime un fichier en lui passant le type de fichier à supprimer
     */
    void deleteFileWithType(final CoreSession session, DocumentModel document, String fileType) throws SSException;

    List<DocumentModel> getFddDocuments(CoreSession session, STDossier stDossier);
}
