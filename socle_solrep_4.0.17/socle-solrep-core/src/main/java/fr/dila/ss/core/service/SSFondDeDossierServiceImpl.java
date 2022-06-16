package fr.dila.ss.core.service;

import fr.dila.ss.api.constant.SSFondDeDossierConstants;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.fondDeDossier.SSFondDeDossierFile;
import fr.dila.ss.api.fondDeDossier.SSFondDeDossierFolder;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.SSFondDeDossierService;
import fr.dila.ss.api.tree.SSTreeFile;
import fr.dila.ss.api.tree.SSTreeFolder;
import fr.dila.ss.api.tree.SSTreeNode;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;

public class SSFondDeDossierServiceImpl extends SSTreeServiceImpl implements SSFondDeDossierService {
    private static final long serialVersionUID = 1L;
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(SSFondDeDossierServiceImpl.class);

    public SSFondDeDossierServiceImpl() {
        super();
        // Default constructor
    }

    @Override
    public DocumentModel createFileInFolder(CoreSession session, DocumentModel parent, String fileName, Blob content)
        throws SSException {
        LOGGER.info(session, SSLogEnumImpl.CREATE_FILE_FDD_TEC);
        return super.createFileInFolder(
            session,
            parent,
            SSFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE,
            fileName,
            content
        );
    }

    @Override
    public void deleteFile(final CoreSession session, final DocumentModel document) throws SSException {
        String documentType = document.getType();
        if (documentType.equals(SSFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE)) {
            super.deleteFile(session, document);
        } else {
            LOGGER.error(session, SSLogEnumImpl.FAIL_DEL_FILE_FDD_TEC, document);
            throw new SSException("Type de document inconnu");
        }
    }

    @Override
    public void deleteFileWithType(final CoreSession session, final DocumentModel document, final String fileType)
        throws SSException {
        String documentType = document.getType();
        if (documentType.equals(fileType)) {
            super.deleteFile(session, document);
        } else {
            LOGGER.error(session, SSLogEnumImpl.FAIL_DEL_FILE_FDD_TEC, document);
            throw new SSException("Type de document inconnu");
        }
    }

    @Override
    public DocumentModel createBareFondDeDossierFichier(
        CoreSession session,
        DocumentModel parent,
        String filename,
        Blob content
    )
        throws SSException {
        return super.createBareFileInFolder(
            session,
            parent,
            SSFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE,
            filename,
            content
        );
    }

    @Override
    public DocumentModel createBareFondDeDossierFichier(CoreSession session, DocumentModel parent, String filename)
        throws SSException {
        return createBareFondDeDossierFichier(session, parent, filename, null);
    }

    @Override
    public DocumentModel createBareFondDeDossierFichier(CoreSession session) throws SSException {
        DocumentModel fondDeDossierFichier;
        try {
            fondDeDossierFichier =
                session.createDocumentModel(SSFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE);
        } catch (NuxeoException exc) {
            throw new SSException("Impossible de créer le fichier de fond de dossier", exc);
        }
        return fondDeDossierFichier;
    }

    @Override
    public DocumentModel createFondDeDossierRepertoire(CoreSession session, DocumentModel parentDoc, String docName)
        throws SSException {
        if (parentDoc == null) {
            LOGGER.error(session, SSLogEnumImpl.FAIL_CREATE_FDD_FOLDER_TEC);
            throw new SSException("id document parent not found");
        }

        DocumentModel repertoireDoc = null;
        try {
            // create a document with the right type
            repertoireDoc =
                session.createDocumentModel(parentDoc.getPathAsString(), docName, getFondDeDossierRepertoireType());

            // set document name
            DublincoreSchemaUtils.setTitle(repertoireDoc, docName);

            DocumentModelList children = session.getChildren(parentDoc.getRef());
            DocumentModel childFound = null;
            for (DocumentModel child : children) {
                if (child.getTitle().equals(docName)) {
                    childFound = child;
                    break;
                }
            }

            if (childFound == null) {
                // create document in session
                repertoireDoc = session.createDocument(repertoireDoc);
            } else {
                repertoireDoc = childFound;
            }
        } catch (NuxeoException exc) {
            LOGGER.error(session, SSLogEnumImpl.FAIL_CREATE_FDD_FOLDER_TEC, exc);
            throw new SSException("Erreur de création du répertoire de fond de dossier", exc);
        }

        return repertoireDoc;
    }

    @Override
    public String getFondDeDossierRepertoireType() {
        // A surcharger dans les services des applications
        // à cause de type différent dans rep et epg
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void initFondDeDossier(CoreSession session, DocumentModel fondDeDossierDoc) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    protected SSTreeFolder getFolderImplFromDoc(DocumentModel doc) {
        return doc.getAdapter(SSFondDeDossierFolder.class);
    }

    @Override
    protected SSTreeFile getFileImplFromDoc(DocumentModel doc) {
        return doc.getAdapter(SSFondDeDossierFile.class);
    }

    public static class AlphabeticalNameComparator implements Comparator<SSTreeNode> {

        @Override
        public int compare(SSTreeNode node1, SSTreeNode node2) {
            return node1.getName().compareToIgnoreCase(node2.getName());
        }
    }

    @Override
    public List<DocumentModel> getFddDocuments(CoreSession session, STDossier stDossier) {
        // Must be override on each application
        return new ArrayList<>();
    }
}
