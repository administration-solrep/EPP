package fr.dila.st.core.service;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STDirectoryService;
import fr.dila.st.core.factory.STLogFactory;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;

public class STDirectoryServiceImpl implements STDirectoryService {
    private static final STLogger LOGGER = STLogFactory.getLog(STDirectoryServiceImpl.class);

    protected DirectoryService getDirectoryService() {
        return STServiceLocator.getDirectoryService();
    }

    @Override
    public List<String> getSuggestions(String keyword, String directory, String column) {
        DirectoryService directoryService = getDirectoryService();

        try (Session session = directoryService.open(directory)) {
            String schemaName = directoryService.getDirectorySchema(directory);
            return this.getSuggestions(keyword, session, schemaName, column);
        } catch (DirectoryException de) {
            LOGGER.error(STLogEnumImpl.FAIL_CLOSE_DIRECTORY_TEC, de);
        } catch (NuxeoException ce) {
            LOGGER.error(STLogEnumImpl.LOG_EXCEPTION_TEC, ce);
        }
        return Collections.emptyList();
    }

    private List<String> getSuggestions(String keyword, Session dirSession, String schemaName, String column) {
        HashSet<String> keywordSet = new HashSet<>();
        keywordSet.add(column);
        Map<String, Serializable> filter = new HashMap<>();
        filter.put(column, keyword);
        // récupération de la liste suggérée
        DocumentModelList docListResult = dirSession.query(filter, keywordSet);

        return docListResult
            .stream()
            .map(doc -> doc.getProperty(schemaName, column))
            .map(String.class::cast)
            .collect(Collectors.toList());
    }
}
