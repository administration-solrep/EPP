package fr.dila.st.core.service;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.CleanupService;
import fr.dila.st.core.factory.STLogFactory;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Implémentation du service de nettoyage des documents à l'état deleted
 *
 */
public class CleanupServiceImpl implements CleanupService {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(CleanupServiceImpl.class);

    private static final int NB_GROUP_SIZE_TO_REMOVE = 10;
    private static final int CONVERT_TO_MILLISECONDS = 1000;

    private static final String QUERY_DELETED_FMT =
        "SELECT d." +
        STSchemaConstant.ECM_UUID_XPATH +
        " AS id FROM %s AS d WHERE d." +
        STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH +
        " = 'deleted'";

    /**
     * Default constructor
     */
    public CleanupServiceImpl() {
        // do nothing
    }

    /**
     * commit la transaction regulièrements
     */
    @Override
    public int removeDeletedDocument(CoreSession session, String documentType, long nbSec) {
        final long endTime = Calendar.getInstance().getTimeInMillis() + nbSec * CONVERT_TO_MILLISECONDS;
        long curTime = Calendar.getInstance().getTimeInMillis();

        boolean hasMore = true;
        int totalRemoved = 0;
        while (hasMore && (nbSec == 0 || curTime < endTime)) {
            int nbRemoved = removeTheNFirstDocument(session, documentType, NB_GROUP_SIZE_TO_REMOVE);
            hasMore = nbRemoved == NB_GROUP_SIZE_TO_REMOVE;
            totalRemoved += nbRemoved;
            curTime = Calendar.getInstance().getTimeInMillis();
            session.save();
            TransactionHelper.commitOrRollbackTransaction();
            TransactionHelper.startTransaction();
        }

        return totalRemoved;
    }

    /**
     * remove 'nb' document of a given type return if has more document to be removed
     *
     * @param session
     * @param documentType
     * @param nbDocsToDelete
     * @return the number of removed document
     */
    private int removeTheNFirstDocument(CoreSession session, String documentType, int nbDocsToDelete) {
        String query = String.format(QUERY_DELETED_FMT, documentType);
        List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null, nbDocsToDelete, 0);
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        for (String id : ids) {
            DocumentRef docRef = new IdRef(id);
            try {
                LOGGER.info(session, STLogEnumImpl.DEL_DOC_TEC, docRef);
                session.removeDocument(docRef);
                session.save();
            } catch (NuxeoException e) {
                // probably remove failed because already removed (linked to recursive removing of child)
                LOGGER.debug(
                    session,
                    STLogEnumImpl.DEL_DOC_TEC,
                    docRef,
                    "Failed to remove [" + id + "] : " + e.getMessage(),
                    e
                );
            }
        }
        return ids.size();
    }
}
