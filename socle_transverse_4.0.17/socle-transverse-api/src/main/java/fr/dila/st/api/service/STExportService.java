package fr.dila.st.api.service;

import java.util.Calendar;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;

public interface STExportService {
    /**
     * indique si l'utilisateur a déjà une demande d'export en cours
     *
     * @param session
     * @param exportDocumentDoc
     * @return
     *
     */
    boolean isCurrentlyExporting(CoreSession session, DocumentModel exportDocumentDoc);

    /**
     * Modifie le document d'export pour indiquer qu'une demande d'export est en cours
     *
     * @param session
     * @param exportDocumentDoc
     * @return
     *
     */
    boolean flagInitExport(CoreSession session, DocumentModel exportDocumentDoc);

    /**
     * Modifie le document d'export pour indiquer que la demande d'export est terminée
     *
     * @param session
     * @param exportDocumentDoc
     *            le parent contenant les documents d'export
     *
     */
    void flagEndExport(CoreSession session, DocumentModel exportDocumentDoc);

    /**
     * récupère la date de la dernière demande d'export au format dd/MM/yyyy hh:mm
     *
     * @param session
     * @param exportDocumentDoc
     * @return
     *
     */
    String getExportHorodatageRequest(CoreSession session, DocumentModel exportDocumentDoc);

    /**
     * Récupère les documentModel des documents d'export en fonction du parent demandé
     *
     * @param session
     * @param parentRef
     * @return
     *
     */
    DocumentModelList getExportDocumentDocs(CoreSession session, DocumentRef parentRef);

    /**
     * Retourne le chemin du répertoire racine pour le stockage des documents export
     *
     * @param session
     * @param parentPath
     *            le chemin du parent qui contient le répertoire racine des documents d'export
     * @param folderExportName
     *            nom du répertoire
     * @param folderExportType
     *            type de répertoire
     * @return
     *
     */
    String getOrCreateExportDocumentRootPath(
        CoreSession session,
        String parentPath,
        String folderExportName,
        String folderExportType
    );

    /**
     * Retourne le répertoire racine pour le stockage des documents export
     *
     * @param session
     * @param parentPath
     *            le chemin du parent qui contient le répertoire racine des documents d'export
     * @param folderExportName
     *            nom du répertoire
     * @param folderExportType
     *            type de répertoire
     * @return
     *
     */
    DocumentModel getOrCreateExportDocumentRootDoc(
        CoreSession session,
        String parentPath,
        String folderExportName,
        String folderExportType
    );

    /**
     * Supprime les documents d'export dont la dateRequest &lt; dateLimit
     *
     * @param session
     * @param dateLimit
     * @param typeExport
     * @return nombre de documents supprimés
     *
     */
    int removeOldExport(CoreSession session, Calendar dateLimit, String typeExport);
}
