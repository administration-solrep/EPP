package fr.dila.st.api.service;

import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.event.batch.BatchResultModel;
import fr.dila.st.api.event.batch.QuartzInfo;
import fr.dila.st.api.model.Page;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Interface du service de suivi des batchs.
 *
 * @author JBT
 */
public interface SuiviBatchService {
    /**
     * Creation d'un logger pour un batch
     *
     * @param eventName
     *
     * @return
     */
    BatchLoggerModel createBatchLogger(final String eventName);

    /**
     * Recherche de logger de batch par id
     *
     * @param idLog
     *            : id du log
     * @return
     */
    BatchLoggerModel findBatchLogById(final Long idLog);

    /**
     * Sauvegarde du {@link BatchLoggerModel}
     *
     * @param batchLoggerModel
     */
    void flushBatchLogger(final BatchLoggerModel batchLoggerModel);

    /**
     * Creation d'un résultat de logger de batch
     *
     * @param batchLoggerModel
     * @param text
     *            : résultats
     * @param executionResult
     * @param executionTime
     *            : temps d'exécution
     *
     */
    BatchResultModel createBatchResultFor(
        final BatchLoggerModel batchLoggerModel,
        final String text,
        final long executionResult,
        final long executionTime
    );

    BatchResultModel createBatchResultFor(
        final BatchLoggerModel batchLoggerModel,
        final String text,
        final long executionTime
    );

    Page<BatchLoggerModel> findBatchLog(
        final Calendar dateDebut,
        final Calendar dateFin,
        long idParent,
        Integer page,
        Integer pageSize
    );

    void flushBatchResult(BatchResultModel batchResultModel);

    List<BatchResultModel> findBatchResult(final BatchLoggerModel currentBatchLogger);

    List<QuartzInfo> findQrtzInfo(final CoreSession session);

    BatchResultModel updateBatchResultFor(
        final BatchResultModel batchResultModel,
        final long executionResult,
        final long executionTime
    );

    BatchResultModel createBatchResultFor(BatchLoggerModel batchLoggerModel, String text);
}
