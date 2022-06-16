package fr.dila.st.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.api.Framework;

/**
 * @author <a href="mailto:glefter@nuxeo.com">George Lefter</a>
 */
public final class DirectoryHelper {
    private static final Log log = LogFactory.getLog(DirectoryHelper.class);

    /** Directory with a parent column. */
    public static final String XVOCABULARY_TYPE = "xvocabulary";

    public static final String[] VOCABULARY_TYPES = { "vocabulary", XVOCABULARY_TYPE };

    private static DirectoryHelper instance;

    private DirectoryHelper() {}

    public static DirectoryHelper instance() {
        if (null == instance) {
            instance = new DirectoryHelper();
        }
        return instance;
    }

    public static DirectoryService getDirectoryService() {
        return instance().getService();
    }

    private static DocumentModel getEntryThrows(String directoryName, String entryId) {
        DirectoryService dirService = getDirectoryService();
        try (Session session = dirService.open(directoryName)) {
            return session.getEntry(entryId);
        }
    }

    /**
     * Returns the entry with given id from specified directory.
     * <p>
     * Method to use from components, since JSF base class that we extend don't allow to throw proper exceptions.
     *
     * @param directoryName
     * @param entryId
     * @return the entry, or null in case of exception in callees.
     */
    public static DocumentModel getEntry(String directoryName, String entryId) {
        try {
            return getEntryThrows(directoryName, entryId);
        } catch (DirectoryException e) {
            log.error(String.format("Error retrieving the entry (dirname=%s, entryId=%s)", directoryName, entryId), e);
            return null;
        }
    }

    protected DirectoryService getService() {
        return Framework.getService(DirectoryService.class);
    }
}
