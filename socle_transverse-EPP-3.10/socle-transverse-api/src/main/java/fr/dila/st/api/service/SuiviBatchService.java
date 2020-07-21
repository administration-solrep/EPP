package fr.dila.st.api.service;

import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.event.batch.BatchResultModel;

/**
 * Interface du service de suivi des batchs.
 * 
 * @author JBT
 */
public interface SuiviBatchService {

	/**
	 * Creation d'un logger pour un batch
	 * 
	 * @param ssPrincipal
	 * 
	 * @return
	 * @throws ClientException
	 */
	BatchLoggerModel createBatchLogger(final String eventName) throws ClientException;

	/**
	 * Recherche de logger de batch par id
	 * 
	 * @param idLog
	 *            : id du log
	 * @return
	 * @throws ClientException
	 */
	BatchLoggerModel findBatchLogById(final Long idLog) throws ClientException;

	/**
	 * Sauvegarde du {@link BatchLoggerModel}
	 * 
	 * @param batchLoggerModel
	 * @throws ClientException
	 */
	void flushBatchLogger(final BatchLoggerModel batchLoggerModel) throws ClientException;

	/**
	 * Creation d'un résultat de logger de batch
	 * 
	 * @param batchLoggerModel
	 * @param texte
	 *            : résultats
	 * @param executionResult
	 * @param executionTime
	 *            : temps d'exécution
	 * 
	 * @throws ClientException
	 */
	BatchResultModel createBatchResultFor(final BatchLoggerModel batchLoggerModel, final String text,
			final long executionResult, final long executionTime) throws ClientException;

	BatchResultModel createBatchResultFor(final BatchLoggerModel batchLoggerModel, final String text,
			final long executionTime) throws ClientException;

	List<BatchLoggerModel> findBatchLog(final Calendar dateDebut, final Calendar dateFin, long idParent)
			throws ClientException;

	void flushBatchResult(BatchResultModel batchResultModel) throws ClientException;

	List<BatchResultModel> findBatchResult(final BatchLoggerModel currentBatchLogger) throws ClientException;

	List<List<String>> findQrtzInfo(final CoreSession session) throws ClientException;

}
