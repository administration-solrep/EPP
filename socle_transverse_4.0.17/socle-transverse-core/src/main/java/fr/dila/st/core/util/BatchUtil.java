package fr.dila.st.core.util;

import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.CleanupService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Classe utilitaire pour les batchs
 *
 */
public class BatchUtil {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(BatchUtil.class);

    /**
     * Timeout
     */
    private static final int TIMEOUT_NB_SEC = 60 * 30; // 30 minutes

    private static final int MILISEC_TO_SEC = 1000;

    /**
     * utility class
     */
    private BatchUtil() {
        // do nothing
    }

    public static long removeDocDeletedState(
        final CoreSession session,
        final String documentType,
        final BatchLoggerModel batchLoggerModel
    ) {
        long nbError = 0L;
        StringBuilder infosBatch = new StringBuilder();
        infosBatch.append("Batch de suppression des ").append(documentType);
        try {
            LOGGER.info(session, STLogEnumImpl.PROCESS_BATCH_TEC, infosBatch.toString());
            final CleanupService cleanupService = STServiceLocator.getCleanupService();
            final long starttime = Calendar.getInstance().getTimeInMillis();
            int nbRemoved = cleanupService.removeDeletedDocument(session, documentType, TIMEOUT_NB_SEC);
            final long timeSpent = (Calendar.getInstance().getTimeInMillis() - starttime) / MILISEC_TO_SEC;
            infosBatch.append(" : ").append(nbRemoved);
            infosBatch.append(nbRemoved > 1 ? " suppressions en " : " suppression en ");
            infosBatch.append(timeSpent).append(" sec.");
            long endTime = Calendar.getInstance().getTimeInMillis();

            if (batchLoggerModel != null) {
                SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
                suiviBatchService.createBatchResultFor(
                    batchLoggerModel,
                    infosBatch.toString(),
                    nbRemoved,
                    endTime - starttime
                );
            }

            LOGGER.info(session, STLogEnumImpl.PROCESS_BATCH_TEC, infosBatch.toString());
        } catch (NuxeoException exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_PROCESS_BATCH_TEC, exc);
            ++nbError;
        }
        return nbError;
    }
}
