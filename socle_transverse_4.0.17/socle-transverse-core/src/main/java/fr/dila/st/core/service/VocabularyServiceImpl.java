/**
 *
 */
package fr.dila.st.core.service;

import static fr.dila.st.api.constant.STVocabularyConstants.COLUMN_LABEL;
import static java.util.stream.Collectors.toList;

import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.service.VocabularyService;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.directory.BaseSession;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.api.Framework;

/**
 * @author jgomez
 *
 */
public class VocabularyServiceImpl implements VocabularyService {
    public static final String UNKNOWN_ENTRY = "**unknown entry**";

    private static final long serialVersionUID = 1L;
    private static final Log LOGGER = LogFactory.getLog(VocabularyServiceImpl.class);

    private static final String ERROR_DIR_EXCEPTION = "Erreur lors de la fermeture du directory";
    private static final String ERROR_CLIENT_EXCEPTION = "Client Exception occured";

    /**
     * Default constructor
     */
    public VocabularyServiceImpl() {
        // do nothing
    }

    /**
     * @see fr.dila.st.api.service.VocabularyService#checkData(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean checkData(String directoryName, String columName, String valueToCheck) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                String.format("VocabularyService checkData : %s,/%s,%s", directoryName, columName, valueToCheck)
            );
        }

        final DirectoryService directoryService = STServiceLocator.getDirectoryService();

        Map<String, Serializable> filter = new HashMap<>();
        filter.put(columName, valueToCheck);
        try (Session vocabularySession = directoryService.open(directoryName)) {
            DocumentModelList listEntries = vocabularySession.query(filter);
            return listEntries != null && !listEntries.isEmpty();
        } catch (DirectoryException de) {
            LOGGER.error(ERROR_DIR_EXCEPTION, de);
        } catch (NuxeoException ce) {
            LOGGER.error(ERROR_CLIENT_EXCEPTION, ce);
        }
        return false;
    }

    /**
     *
     * Méthode d'aide pour la suggestion
     *
     * @param keyword
     * @param dirSession
     * @param schemaName
     * @return
     * @throws ClientException
     */
    private List<String> getSuggestions(String keyword, Session dirSession, String schemaName) {
        HashSet<String> keywordSet = new HashSet<>();
        keywordSet.add(STVocabularyConstants.COLUMN_LABEL);
        Map<String, Serializable> filter = new HashMap<>();
        filter.put(STVocabularyConstants.COLUMN_LABEL, keyword);
        // récupération de la liste suggérée
        DocumentModelList docListResult = dirSession.query(filter, keywordSet);

        List<String> suggestionsList = docListResult
            .stream()
            .map(doc -> (String) doc.getProperty(schemaName, COLUMN_LABEL))
            .filter(StringUtils::isNotBlank)
            .collect(toList());
        if (suggestionsList.size() != docListResult.size()) {
            LOGGER.warn(
                "Un vovabulaire avec le schema [" + schemaName + "] a un libéllé vide ou n'a pas de colonne 'label'"
            );
        }

        return suggestionsList;
    }

    /**
     * Méthode d'aide pour la suggestion
     *
     * @param keyword
     * @param dirSession
     * @param schemaName
     * @return DocumentModelList
     * @throws DirectoryException
     * @throws ClientException
     */
    protected List<DocumentModel> getDocumentModelListSuggestions(
        String keyword,
        Session dirSession,
        String schemaName
    ) {
        HashSet<String> keywordSet = new HashSet<>();
        keywordSet.add(STVocabularyConstants.COLUMN_LABEL);
        Map<String, Serializable> filter = new HashMap<>();
        filter.put(STVocabularyConstants.COLUMN_LABEL, keyword);
        // récupération de la liste suggérée
        return dirSession.query(filter, keywordSet);
    }

    /**
     * @see fr.dila.st.api.service.VocabularyService#getSuggestions(java.lang.String, java.lang.String)
     */
    @Override
    public List<String> getSuggestions(String keyword, String vocName) {
        final DirectoryService directoryService = STServiceLocator.getDirectoryService();
        try (Session session = directoryService.open(vocName)) {
            String schemaName = directoryService.getDirectorySchema(vocName);
            return this.getSuggestions(keyword, session, schemaName);
        } catch (DirectoryException de) {
            LOGGER.error(ERROR_DIR_EXCEPTION, de);
        } catch (NuxeoException ce) {
            LOGGER.error(ERROR_CLIENT_EXCEPTION, ce);
        }
        return null;
    }

    /**
     * @see fr.dila.st.api.service.VocabularyService#getSuggestions(java.lang.String, java.util.List)
     */

    @Override
    public List<String> getSuggestions(String keyword, List<String> vocList) {
        List<String> resultList = new ArrayList<>();
        for (String vocName : vocList) {
            resultList.addAll(getSuggestions(keyword, vocName));
        }
        return new ArrayList<>(new HashSet<>(resultList));
    }

    /*
     * (non-Javadoc)
     * @see fr.dila.st.api.service.VocabularyService#getDocumentModelListSuggestions(java.lang.String, java.lang.String)
     */
    @Override
    public List<DocumentModel> getListDocumentModelSuggestions(String keyword, String vocName) {
        final DirectoryService directoryService = STServiceLocator.getDirectoryService();
        try (Session session = directoryService.open(vocName)) {
            String schemaName = directoryService.getDirectorySchema(vocName);
            return this.getDocumentModelListSuggestions(keyword, session, schemaName);
        } catch (DirectoryException de) {
            LOGGER.warn(ERROR_DIR_EXCEPTION, de);
        } catch (NuxeoException ce) {
            LOGGER.warn(ERROR_CLIENT_EXCEPTION, ce);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.dila.st.api.service.VocabularyService#getDocumentModelListSuggestions(java.lang.String, java.util.List)
     */
    @Override
    public List<DocumentModel> getListDocumentModelSuggestions(String keyword, List<String> vocList) {
        List<DocumentModel> suggestionsDocList = new ArrayList<>();
        for (String vocName : vocList) {
            suggestionsDocList.addAll(getListDocumentModelSuggestions(keyword, vocName));
        }
        return suggestionsDocList;
    }

    @Override
    public String getEntryLabel(String directoryName, String entryId, String labelProperty) {
        final DirectoryService directoryService = STServiceLocator.getDirectoryService();
        // Récupère l'entrée
        Directory directory = directoryService.getDirectory(directoryName);
        String label = "";
        try (Session session = directory.getSession()) {
            DocumentModel doc = session.getEntry(entryId);
            if (doc == null) {
                label = UNKNOWN_ENTRY;
                LOGGER.warn("no entry for id " + entryId);
            } else {
                // Récupère le libellé
                String schema = directoryService.getDirectorySchema(directoryName);
                label = PropertyUtil.getStringProperty(doc, schema, labelProperty);
            }
        } catch (DirectoryException de) {
            label = UNKNOWN_ENTRY;
            LOGGER.warn(ERROR_DIR_EXCEPTION, de);
        } catch (NuxeoException ce) {
            label = UNKNOWN_ENTRY;
            LOGGER.warn(ERROR_CLIENT_EXCEPTION, ce);
        }

        return label;
    }

    @Override
    public String getEntryLabel(String directoryName, String entryId) {
        if (entryId.isEmpty()) {
            return entryId;
        }
        return getEntryLabel(directoryName, entryId, STVocabularyConstants.COLUMN_LABEL);
    }

    @Override
    public String getLabelFromId(String vocabularyDirectoryName, String idValue, String labelColumName) {
        final DirectoryService directoryService = STServiceLocator.getDirectoryService();
        LOGGER.debug(
            "VocabularyService getLabelFromId : " + vocabularyDirectoryName + " / " + idValue + " / " + labelColumName
        );
        Session vocabularySession = null;
        try {
            vocabularySession = directoryService.open(vocabularyDirectoryName);
            String schemaName = directoryService.getDirectorySchema(vocabularyDirectoryName);
            DocumentModel documentModel = vocabularySession.getEntry(idValue);

            if (documentModel != null) {
                return (String) documentModel.getProperty(schemaName, labelColumName);
            }
        } catch (Exception e) {
            LOGGER.error("Erreur lors de la récupération du label du vocbulaire à partir de son identifiant", e);
        } finally {
            if (vocabularySession != null) {
                try {
                    vocabularySession.close();
                } catch (DirectoryException e) {
                    // do nothing
                    LOGGER.warn(ERROR_DIR_EXCEPTION, e);
                }
            }
        }
        return null;
    }

    @Override
    public DocumentModelList getAllEntry(String vocabularyDirectoryName) {
        final DirectoryService directoryService = STServiceLocator.getDirectoryService();
        try (Session vocabularySession = directoryService.open(vocabularyDirectoryName)) {
            Map<String, Serializable> filter = new HashMap<>();
            return vocabularySession.query(filter);
        } catch (NuxeoException de) {
            LOGGER.error(ERROR_DIR_EXCEPTION, de);
            return null;
        }
    }

    @Override
    public Map<String, String> getAllEntries(String vocabularyDirectoryName) {
        DocumentModelList docList = getAllEntry(vocabularyDirectoryName);

        if (CollectionUtils.isNotEmpty(docList)) {
            return docList
                .stream()
                .collect(
                    Collectors.toMap(
                        DocumentModel::getId,
                        doc ->
                            (String) doc.getProperty(
                                STVocabularyConstants.VOCABULARY,
                                STVocabularyConstants.COLUMN_LABEL
                            )
                    )
                );
        }

        return new HashMap<>();
    }

    @Override
    public List<DocumentModel> getEntries(String vocabularyDirectoryName, List<String> ids) {
        final DirectoryService directoryService = STServiceLocator.getDirectoryService();
        Session vocabularySession = null;
        List<DocumentModel> list = new ArrayList<>();
        try {
            vocabularySession = directoryService.open(vocabularyDirectoryName);
            for (String id : ids) {
                list.add(vocabularySession.getEntry(id));
            }
        } catch (Exception e) {
            LOGGER.error("Erreur lors de la récupération du vocabulaire", e);
        } finally {
            if (vocabularySession != null) {
                try {
                    vocabularySession.close();
                } catch (DirectoryException e) {
                    // do nothing
                    LOGGER.warn(ERROR_DIR_EXCEPTION, e);
                }
            }
        }
        return list;
    }

    @Override
    public DocumentModel getNewEntry(String vocabularyDirectoryName) {
        final DirectoryService directoryService = STServiceLocator.getDirectoryService();
        try (Session vocabularySession = directoryService.open(vocabularyDirectoryName)) {
            String schema = directoryService.getDirectorySchema(vocabularyDirectoryName);
            return BaseSession.createEntryModel(null, schema, null, null);
        }
    }

    @Override
    public void createDirectoryEntry(String vocabularyDirectoryName, DocumentModel creationDirectoryEntry) {
        final DirectoryService dirService = STServiceLocator.getDirectoryService();
        try (Session dirSession = dirService.open(vocabularyDirectoryName)) {
            // check if entry already exists
            // String schema = dirService.getDirectorySchema(vocabularyDirectoryName);
            // String idField = dirService.getDirectoryIdField(vocabularyDirectoryName);
            // Object id = creationDirectoryEntry.getProperty(schema, idField);

            // if (id instanceof String && dirSession.hasEntry((String) id)) {
            // facesMessages.add(FacesMessage.SEVERITY_ERROR,
            // resourcesAccessor.getMessages().get(
            // "vocabulary.entry.identifier.already.exists"));
            // return;
            // }
            dirSession.createEntry(creationDirectoryEntry);
        }
    }

    @Override
    public void deleteDirectoryEntry(String vocabularyDirectoryName, DocumentModel deleteDirectoryEntry) {
        final DirectoryService dirService = STServiceLocator.getDirectoryService();
        try (Session dirSession = dirService.open(vocabularyDirectoryName)) {
            dirSession.deleteEntry(deleteDirectoryEntry);
        }
    }

    @Override
    public boolean hasDirectoryEntry(String vocabularyDirectoryName, String entryId) {
        final DirectoryService dirService = STServiceLocator.getDirectoryService();
        try (Session dirSession = dirService.open(vocabularyDirectoryName)) {
            // check if entry already exists

            if (dirSession.hasEntry(entryId)) {
                return true;
            }
            return false;
        }
    }

    @Override
    public void createEntryIfNotExists(String directorySchema, String directoryName, String entryId) {
        if (directoryName == null || entryId == null) {
            return;
        }

        if (directorySchema == null) {
            directorySchema = STVocabularyConstants.VOCABULARY;
        }

        if (!hasDirectoryEntry(directoryName, entryId)) {
            DocumentModel newEntry = getNewEntry(directoryName);
            PropertyUtil.setProperty(newEntry, directorySchema, STVocabularyConstants.COLUMN_ID, entryId);
            PropertyUtil.setProperty(newEntry, directorySchema, STVocabularyConstants.COLUMN_LABEL, entryId);
            createDirectoryEntry(directoryName, newEntry);
        }
    }

    @Override
    public void createDirectoryEntryPrivileged(String vocabularyDirectoryName, DocumentModel creationDirectoryEntry) {
        Framework.doPrivileged(() -> createDirectoryEntry(vocabularyDirectoryName, creationDirectoryEntry));
    }

    @Override
    public void deleteDirectoryEntryPrivileged(String vocabularyDirectoryName, DocumentModel deleteDirectoryEntry) {
        Framework.doPrivileged(() -> deleteDirectoryEntry(vocabularyDirectoryName, deleteDirectoryEntry));
    }

    @Override
    public DocumentModel getEntry(String directoryName, String id) {
        final DirectoryService dirService = STServiceLocator.getDirectoryService();
        try (Session dirSession = dirService.open(directoryName)) {
            return dirSession.getEntry(id);
        }
    }
}
