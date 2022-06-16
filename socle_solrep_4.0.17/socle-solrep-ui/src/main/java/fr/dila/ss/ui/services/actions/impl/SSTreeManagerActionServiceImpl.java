package fr.dila.ss.ui.services.actions.impl;

import static fr.dila.ss.core.service.SSServiceLocator.getSSTreeService;
import static fr.dila.st.core.util.ResourceHelper.getString;
import static fr.dila.st.ui.enums.STContextDataKey.FILE_CONTENT;
import static fr.dila.st.ui.enums.STContextDataKey.FILE_DETAILS;

import com.sun.jersey.core.header.FormDataContentDisposition;
import fr.dila.ss.api.constant.SSTreeConstants;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.service.SSTreeService;
import fr.dila.ss.api.tree.SSTreeFile;
import fr.dila.ss.api.tree.SSTreeFolder;
import fr.dila.ss.ui.services.actions.SSTreeManagerActionService;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.FileSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.BlobUtils;
import fr.dila.st.core.util.FileUtils;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.bean.DocumentDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * ActionBean de gestion de l'arborescence.
 */
public abstract class SSTreeManagerActionServiceImpl implements SSTreeManagerActionService {
    private static final STLogger LOGGER = STLogFactory.getLog(SSTreeManagerActionServiceImpl.class);
    protected static final String ACTION_CATEGORY_KEY = "actionCat";

    protected static final String SUCCESS_MSG_ADD_FILE = "fondDossier.add.file.success";
    protected static final String SUCCESS_MSG_EDIT_FILE = "fondDossier.edit.file.success";
    protected static final String SUCCESS_MSG_DELETE_FILE = "fondDossier.delete.file.success";

    protected static final String ERROR_MSG_ADD_FILE = "fondDossier.add.file.error";
    protected static final String ERROR_MSG_EDIT_FILE = "fondDossier.edit.file.error";
    protected static final String ERROR_MSG_DELETE_FILE = "fondDossier.delete.file.error";

    protected static final String SUCCESS_MSG_ADD_FOLDER = "fondDossier.add.folder.success";
    protected static final String SUCCESS_MSG_RENAME_FOLDER = "fondDossier.rename.folder.success";
    protected static final String SUCCESS_MSG_DELETE_FOLDER = "fondDossier.delete.folder.success";
    protected static final String WARN_MSG_SAME_VISIBILITY =
        "feedback.reponses.document.fdd.document.warn.same.visibility";
    protected static final String ERROR_MSG_NO_VISIBILITY_SELECTED =
        "feedback.reponses.document.fdd.document.error.unselected.visibility";

    protected String choixSuppression = SSTreeConstants.DELETE_ALL_CHOICE;

    private Class<? extends DocumentDTO> documentDtoClass = DocumentDTO.class;

    public SSTreeManagerActionServiceImpl() {
        super();
    }

    protected SSTreeManagerActionServiceImpl(Class<? extends DocumentDTO> clazz) {
        this.documentDtoClass = clazz;
    }

    private <V> V getTypeInstance(Class<V> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (
            InstantiationException
            | IllegalAccessException
            | IllegalArgumentException
            | InvocationTargetException
            | NoSuchMethodException
            | SecurityException e
        ) {
            throw new NuxeoException(String.format("Could not instantiate [%s]", clazz.getName()));
        }
    }

    /*
     * Liste de caractères non autorisés dans le nom de document
     */
    protected static final String[] forbiddenChars = { "\'" };

    // file properties

    public static final String ERROR_MSG_DOCUMENT_EMPTY = "feedback.ss.document.tree.document.error.empty.form";

    public static final String ERROR_MSG_NO_FILE_SELECTED = "feedback.ss.document.tree.document.error.unselected.file";

    public static final String ERROR_CREATION = "feedback.ss.document.tree.document.error.creation";

    public static final String ERROR_RENAME = "feedback.ss.document.tree.document.error.rename";

    protected static final String ERROR_MSG_EXTENSION_INCORRECT =
        "feedback.ss.document.tree.document.error.incorrect.extension";

    protected static final String ERROR_MSG_INCORRECT_NAME = "feedback.ss.document.tree.document.error.incorrect.name";

    protected static final String ERROR_MSG_FILE_TOO_BIG = "feedback.ss.document.tree.document.error.file.too.big";

    /**
     * Suppression d'un fichier dans l'arborescence du document.
     *
     * @return null
     * @author ARN
     */
    @Override
    public boolean deleteFile(CoreSession session, DocumentModel currentDocument, DocumentModel fondDeDossierFileDoc) {
        LOGGER.info(session, STLogEnumImpl.DEL_FILE_FONC);

        boolean isDeletedFile = false;

        if (fondDeDossierFileDoc != null && choixSuppression != null) {
            if (SSTreeConstants.DELETE_ALL_CHOICE.equals(choixSuppression)) {
                // on supprime le document et toutes les versions qui lui sont associées
                getSSTreeService().deleteFile(session, fondDeDossierFileDoc);
                isDeletedFile = true;
            } else if (SSTreeConstants.DELETE_CURRENT_VERSION_CHOICE.equals(choixSuppression)) {
                // Restauration de l'avant dernière version du noeud dans l'arborescence du fond de dossier.
                getSSTreeService().restoreToPreviousVersion(session, fondDeDossierFileDoc.getId(), currentDocument);
                isDeletedFile = true;
            }
        }

        return isDeletedFile;
    }

    /**
     * Supprime le document sélectionné ainsi que tous ses enfants
     */
    @Override
    public void deleteDocument(CoreSession session, String selectedNodeId) {
        if (selectedNodeId != null) {
            final SSTreeService ssTreeService = getSSTreeService();
            ssTreeService.deleteFolder(session, session.getDocument(new IdRef(selectedNodeId)));
            // on deselectionner le répertoire : remise à zero des variables
            // concernant le répertoire sélectionné
        }
    }

    @Override
    public void addFile(SpecificContext context) {
        DocumentModel fileDoc = createBareFile(context);

        ImmutablePair<String, Blob> file = getFile(context);
        String fileName = file.getLeft();
        Blob content = file.getRight();

        FileSchemaUtils.setContent(fileDoc, content);
        if (checkFileCreation(context, fileDoc)) {
            try {
                createSpecificFile(content, fileName, context);
                context.getMessageQueue().addSuccessToQueue(getString(getAddFileSuccessMessage(), fileName));
            } catch (IOException e) {
                throw new NuxeoException(e);
            }
        } else {
            context.getMessageQueue().addErrorToQueue(getString(getAddFileErrorMessage(), fileName));
        }
    }

    protected abstract String getAddFileSuccessMessage();

    protected abstract String getAddFileErrorMessage();

    protected abstract void createSpecificFile(Blob content, String fileName, SpecificContext context)
        throws IOException;

    protected abstract DocumentModel createBareFile(SpecificContext context);

    protected boolean checkFileCreation(SpecificContext context, DocumentModel fileDoc) {
        Blob blob = FileSchemaUtils.getContent(fileDoc);
        boolean canFileBeCreated = true;
        if (blob != null) {
            String newFileName = blob.getFilename();
            if (blob.getLength() == 0L) {
                context
                    .getMessageQueue()
                    .addErrorToQueue(getString("feedback.ss.document.tree.document.error.file.length"));
                canFileBeCreated = false;
            } else {
                canFileBeCreated = verifyNewFileName(newFileName, context, getAllowedFileTypes(context));
            }
        }
        return canFileBeCreated;
    }

    protected boolean verifyNewFileName(String newFileName, SpecificContext context, List<String> acceptedTypes) {
        boolean fileNameOk = true;
        if (StringUtils.isBlank(newFileName)) {
            context.getMessageQueue().addErrorToQueue(getString(ERROR_MSG_NO_FILE_SELECTED));
            fileNameOk = false;
        } else if (!FileUtils.isValidFilename(newFileName)) {
            context.getMessageQueue().addErrorToQueue(getString(ERROR_MSG_INCORRECT_NAME, newFileName));
            fileNameOk = false;
        } else if (
            CollectionUtils.isNotEmpty(acceptedTypes) &&
            !FilenameUtils.isExtension(newFileName.toLowerCase(), acceptedTypes)
        ) {
            context.getMessageQueue().addErrorToQueue(getString(ERROR_MSG_EXTENSION_INCORRECT, newFileName));
            fileNameOk = false;
        }
        return fileNameOk;
    }

    protected abstract List<String> getAllowedFileTypes(SpecificContext context);

    protected void createFolder(
        SpecificContext context,
        CoreSession session,
        DocumentModel parentDoc,
        String documentType,
        String folderName
    ) {
        final SSTreeService ssTreeService = getSSTreeService();
        try {
            LOGGER.info(session, STLogEnumImpl.CREATE_FOLDER_FONC, "Into : " + parentDoc.getId());
            ssTreeService.createNewFolder(session, documentType, parentDoc, folderName);
            context.getMessageQueue().addToastSuccess(SUCCESS_MSG_ADD_FOLDER);
        } catch (SSException sse) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_FOLDER_FONC, sse);
            context.getMessageQueue().addErrorToQueue("Erreur lors de la création du répertoire");
        }
    }

    /**
     * Nettoie le nom du fichier : si le nom du fichier correspond au chemin sur
     * le disque, on ne récupère que le nom du fichier => Utilisé pour IE
     */
    protected String cleanFileName(String fileNameToClean) {
        String fileName = fileNameToClean;
        int lastSlashChar = fileName.lastIndexOf('/');
        if (lastSlashChar != -1) {
            fileName = fileName.substring(lastSlashChar + 1);
        }
        int lastBackSlashChar = fileName.lastIndexOf('\\');
        if (lastBackSlashChar != -1) {
            fileName = fileName.substring(lastBackSlashChar + 1);
        }
        return fileName;
    }

    /**
     * vérifie la validité d'une chaine de caractère en fonction des caractères
     * non autorisés
     *
     * @param filename
     * @return vrai si la string en paramètre ne contient pas l'un des
     *         caractères interdits, faux sinon
     */
    protected static boolean isFileNameCorrect(final String filename) {
        for (final String s : forbiddenChars) {
            if (filename.contains(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Vérifie la taille du fichier en fonction des paramètres de l'application
     *
     * @return Vrai si la taille du fichier est inférieur au paramètre ou si la
     *         paramètre est null, faux sinon
     */
    protected boolean verifyFileLength(CoreSession session, Blob content) {
        boolean isFileOK = false;
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        String param = null;
        try {
            param = paramService.getParametreValue(session, STParametreConstant.PARAMETRE_TAILLE_PIECES_JOINTES);
        } catch (NuxeoException e) {
            LOGGER.warn(session, STLogEnumImpl.CREATE_FILE_FONC, e);
            return true;
        }
        try {
            Long.parseLong(param);
        } catch (Exception e) {
            LOGGER.warn(session, STLogEnumImpl.CREATE_FILE_FONC, e);
            return true;
        }
        if (param == null || (content.getLength() <= (Long.parseLong(param) * 1048576))) {
            isFileOK = true;
        }
        return isFileOK;
    }

    protected String getChoixSuppression() {
        return choixSuppression;
    }

    protected void setChoixSuppression(String choixSuppression) {
        this.choixSuppression = choixSuppression;
    }

    @Override
    public boolean isFolderEmpty(SpecificContext context, CoreSession session, SSTreeFolder folder) {
        final SSTreeService ssTreeService = getSSTreeService();
        try {
            return ssTreeService.isFolderEmpty(session, folder);
        } catch (NuxeoException exc) {
            if (folder != null) {
                logExcAndDisplayMessage(
                    context,
                    session,
                    STLogEnumImpl.FAIL_GET_INFORMATION_TEC,
                    exc,
                    folder.getDocument()
                );
            } else {
                logExcAndDisplayMessage(context, session, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, exc, null);
            }
        }
        return true;
    }

    protected void logExcAndDisplayMessage(
        SpecificContext context,
        CoreSession session,
        STLogEnum log,
        Throwable exception,
        DocumentModel doc
    ) {
        if (doc == null) {
            LOGGER.error(session, log, exception);
        } else {
            LOGGER.error(session, log, doc, exception);
        }

        context.getMessageQueue().addWarnToQueue(log.getText());
    }

    /**
     * récupère le document sélectionné par l'utilisateur à partir d"un
     * identifiant.
     *
     * @return DocumentModel
     */
    protected static DocumentModel getSelectedDocument(CoreSession session, final String selectedNodeId) {
        return session.getDocument(new IdRef(selectedNodeId));
    }

    protected void renameFolder(
        SpecificContext context,
        CoreSession session,
        DocumentModel docToRename,
        String newName
    ) {
        final SSTreeService ssTreeService = getSSTreeService();
        try {
            LOGGER.info(session, STLogEnumImpl.UPDATE_FOLDER_FONC);
            ssTreeService.renameFolder(session, docToRename, newName);
            context.getMessageQueue().addToastSuccess(SUCCESS_MSG_RENAME_FOLDER);
        } catch (NuxeoException sse) {
            LOGGER.error(session, STLogEnumImpl.FAIL_UPDATE_FOLDER_FONC, sse);
            context.getMessageQueue().addErrorToQueue("Erreur lors du rennomage du répertoire");
        }
    }

    public ImmutablePair<String, Blob> getFile(SpecificContext context) {
        FormDataContentDisposition fileDetails = context.getFromContextData(FILE_DETAILS);
        if (fileDetails == null) {
            throw new STValidationException("fondDossier.file.details.error");
        }

        String fileName = null;
        if (fileDetails.getFileName() != null) {
            fileName =
                new String(fileDetails.getFileName().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }

        Blob content;
        try (InputStream fileContent = context.getFromContextData(FILE_CONTENT)) {
            content = BlobUtils.createSerializableBlob(fileContent, fileName, null);
        } catch (IOException ioe) {
            throw new NuxeoException(getString("fondDossier.server.file.creation.error"), ioe);
        }

        return ImmutablePair.of(fileName, content);
    }

    public DocumentDTO toDocumentDTO(SpecificContext context, SSTreeFile ssTreeFile) {
        DocumentDTO documentDTO = createDocumentDTO();
        documentDTO.setId(ssTreeFile.getId());
        documentDTO.setNom(FilenameUtils.removeExtension(FileUtils.getShorterName(ssTreeFile.getSafeFilename())));
        documentDTO.setAuteur(STServiceLocator.getSTUserService().getUserFullName(ssTreeFile.getLastContributor()));
        documentDTO.setDate(SolonDateConverter.DATE_SLASH.format(ssTreeFile.getModifiedDate()));
        documentDTO.setExtension(FileUtils.getExtensionWithSeparator(ssTreeFile.getSafeFilename()));
        documentDTO.setVersion(ssTreeFile.getMajorVersion().toString());
        documentDTO.setLstActions(context.getActions(context.getFromContextData(ACTION_CATEGORY_KEY)));
        return documentDTO;
    }

    @SuppressWarnings("unchecked")
    private <T extends DocumentDTO> T createDocumentDTO() {
        return (T) getTypeInstance(documentDtoClass);
    }

    public DocumentModel getSelectedDocument(SpecificContext context) {
        return getSelectedDocument(context.getSession(), context.getFromContextData(STContextDataKey.ID));
    }
}
