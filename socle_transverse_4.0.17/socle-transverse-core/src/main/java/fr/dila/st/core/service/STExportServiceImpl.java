package fr.dila.st.core.service;

import fr.dila.st.api.domain.ExportDocument;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STExportService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.SolonDateConverter;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;

/**
 * Service d'export
 *
 */
public class STExportServiceImpl implements STExportService {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(STExportServiceImpl.class);

    private static final String FOLDER_PATH_SPEPARATOR = "/";

    public STExportServiceImpl() {
        // Default constructor
    }

    @Override
    public boolean isCurrentlyExporting(CoreSession session, DocumentModel exportDocumentDoc) {
        if (exportDocumentDoc == null) {
            return false;
        } else {
            ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);
            return exportDocument.isExporting();
        }
    }

    @Override
    public boolean flagInitExport(CoreSession session, DocumentModel exportDocumentDoc) {
        if (exportDocumentDoc == null) {
            return false;
        } else {
            final ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);
            synchronized (exportDocument) {
                if (exportDocument.isExporting()) {
                    return false;
                } else {
                    exportDocument.setDateRequest(Calendar.getInstance());
                    exportDocument.setExporting(true);
                    exportDocument.save(session);
                    session.save();
                    return true;
                }
            }
        }
    }

    @Override
    public String getExportHorodatageRequest(CoreSession session, DocumentModel exportDocumentDoc) {
        if (exportDocumentDoc == null) {
            return "Aucune donnée trouvée pour les paramètres fournis";
        } else {
            ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);
            return SolonDateConverter.DATETIME_SLASH_MINUTE_COLON.format(exportDocument.getDateRequest());
        }
    }

    @Override
    public DocumentModelList getExportDocumentDocs(CoreSession session, DocumentRef parentRef) {
        if (session == null || parentRef == null) {
            LOGGER.error(session, STLogEnumImpl.NPE_PARAM_METH_TEC, "La session, ou la requête est null");
            return null;
        }

        return session.getChildren(parentRef);
    }

    @Override
    public String getOrCreateExportDocumentRootPath(
        CoreSession session,
        String parentPath,
        String folderExportName,
        String folderExportType
    ) {
        DocumentModel exportDocumentRoot = getOrCreateExportDocumentRootDoc(
            session,
            parentPath,
            folderExportName,
            folderExportType
        );
        if (exportDocumentRoot == null) {
            return null;
        } else {
            return exportDocumentRoot.getPathAsString();
        }
    }

    @Override
    public DocumentModel getOrCreateExportDocumentRootDoc(
        CoreSession session,
        String parentPath,
        String folderExportName,
        String folderExportType
    ) {
        DocumentModel exportDocumentRoot = null;
        StringBuilder parentPathFolder = new StringBuilder(parentPath);
        if (parentPath.endsWith(FOLDER_PATH_SPEPARATOR)) {
            parentPathFolder.append(folderExportName);
        } else {
            parentPathFolder.append(FOLDER_PATH_SPEPARATOR).append(folderExportName);
        }
        final PathRef folderExportPathRef = new PathRef(parentPathFolder.toString());
        if (session.exists(folderExportPathRef)) {
            exportDocumentRoot = session.getDocument(folderExportPathRef);
        } else {
            exportDocumentRoot = session.createDocumentModel(parentPath, folderExportName, folderExportType);
            exportDocumentRoot = session.createDocument(exportDocumentRoot);
            session.saveDocument(exportDocumentRoot);
            session.save();
        }
        return exportDocumentRoot;
    }

    @Override
    public void flagEndExport(CoreSession session, DocumentModel exportDocumentDoc) {
        if (exportDocumentDoc != null) {
            ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);
            if (exportDocument.isExporting()) {
                exportDocument.setExporting(false);
                exportDocument.save(session);
                session.save();
            }
        }
    }

    @Override
    public int removeOldExport(CoreSession session, Calendar dateLimit, String typeExport) {
        String query =
            "SELECT e.ecm:uuid as id FROM " +
            typeExport +
            " as e where e.expdoc:dateRequest < ? OR e.expdoc:dateRequest is null";
        int nbSuppression = 0;
        DocumentRef[] refsToDelete = QueryUtils.doUFNXQLQueryForIds(session, query, new Object[] { dateLimit });
        LOGGER.info(session, STLogEnumImpl.DEL_DOC_TEC, refsToDelete);
        if (refsToDelete != null) {
            nbSuppression = refsToDelete.length;
        }
        session.removeDocuments(refsToDelete);
        session.save();
        return nbSuppression;
    }
}
