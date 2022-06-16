package fr.dila.st.core.event;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;

/**
 * Suppression de fichiers du binary store s'ils ne sont plus utilisés.
 *
 * @author tlombard
 *
 */
public class CleanBinaryStoreListener extends AbstractPostCommitEventListener {
    private static final String INFO_REMOVED_FILE = "Fichier supprimé : %s";

    private static final STLogger LOGGER = STLogFactory.getLog(CleanBinaryStoreListener.class);

    /**
     * Requête permettant d'identifier les objets de la table content placeless
     * (reliés à un objet qui n'est plus dans la hiérarchie) et dont la donnée
     * correspond à celle du fichier en cours de suppression. Cette requête concerne les objets FILE.
     */
    protected static final String QUERY_FILE =
        "SELECT count(*) FROM CONTENT c " +
        "JOIN HIERARCHY h1 on h1.id=c.id " +
        "JOIN HIERARCHY h2 on h2.id=h1.parentid " +
        "WHERE data = ? " +
        "AND h2.PARENTID is NOT NULL " +
        "AND h2.BASEVERSIONID IS NOT NULL " +
        "AND h2.ID <> ?";

    /**
     * Requête permettant d'identifier les objets de la table content placeless
     * (reliés à un objet qui n'est plus dans la hiérarchie) et dont la donnée
     * correspond à celle du fichier en cours de suppression. Cette requête concerne les objets FILES.
     */
    protected static final String QUERY_FILES =
        "SELECT count(*) FROM CONTENT c " +
        "JOIN HIERARCHY h1 ON h1.id=c.id " +
        "JOIN HIERARCHY h2 on h2.id=h1.parentid " +
        "JOIN HIERARCHY h3 on h3.id=h2.parentid " +
        "WHERE data = ? " +
        "AND h2.PRIMARYTYPE = 'file' " +
        "AND h3.id <> ?";

    @Override
    protected void handleEvent(Event event, CoreSession session) {
        EventContext ctx = event.getContext();
        String docId = getDocId(ctx);
        Collection<String> fileKeys = getFileKeys(ctx);

        fileKeys.forEach(fileKey -> cleanBlobIfPossible(session, docId, fileKey));
    }

    protected static void cleanBlobIfPossible(CoreSession session, String docId, String fileKey) {
        Long countFile = countFile(session, docId, fileKey);
        Long countFiles = countFiles(session, docId, fileKey);

        if (countFile == 0L && countFiles == 0L) {
            String binariesFolder = STServiceLocator
                .getConfigService()
                .getValue(STConfigConstants.REPOSITORY_BINARY_STORE_PATH);

            File fileToRemove = new File(
                StringUtils.join(
                    new String[] { binariesFolder, "data", fileKey.substring(0, 2), fileKey.substring(2, 4) },
                    File.separator
                ),
                fileKey
            );

            try {
                if (Files.deleteIfExists(fileToRemove.toPath())) {
                    LOGGER.info(
                        session,
                        STLogEnumImpl.DEL_FILE_TEC,
                        String.format(INFO_REMOVED_FILE, fileToRemove.getPath())
                    );
                }
            } catch (Exception e) {
                LOGGER.error(session, STLogEnumImpl.DEL_FILE_TEC, e);
            }
        }
    }

    private static Long countFile(CoreSession session, String docId, String fileKey) {
        return getCountColumnValue(session, docId, fileKey, QUERY_FILE);
    }

    private static Long countFiles(CoreSession session, String docId, String fileKey) {
        return getCountColumnValue(session, docId, fileKey, QUERY_FILES);
    }

    private static Long getCountColumnValue(CoreSession session, String docId, String fileKey, String query) {
        try (
            IterableQueryResult queryResult = QueryUtils.doSqlQuery(
                session,
                new String[] { FlexibleQueryMaker.COL_COUNT },
                query,
                new Object[] { fileKey, docId }
            )
        ) {
            Long count = -1L;

            Iterator<Map<String, Serializable>> iterator = queryResult.iterator();
            if (iterator.hasNext()) {
                count = (Long) iterator.next().get(FlexibleQueryMaker.COL_COUNT);
            }

            return count;
        }
    }

    @Override
    protected boolean accept(Event event) {
        EventContext ctx = event.getContext();
        String docId = getDocId(ctx);
        Collection<String> fileKeys = getFileKeys(ctx);

        return docId != null && CollectionUtils.isNotEmpty(fileKeys);
    }

    private String getDocId(EventContext ctx) {
        return (String) ctx.getProperty(RemoveFileListener.DOC_ID_PROP);
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getFileKeys(EventContext ctx) {
        return (Collection<String>) ctx.getProperty(RemoveFileListener.FILE_KEYS_PROP);
    }
}
