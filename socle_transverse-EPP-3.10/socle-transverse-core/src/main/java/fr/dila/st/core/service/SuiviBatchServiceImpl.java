package fr.dila.st.core.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.annotations.Transactional;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunCallback;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunVoid;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.constant.STSuiviBatchsConstants.TypeBatch;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.event.batch.BatchResultModel;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.batch.BatchLoggerModelImpl;
import fr.dila.st.core.event.batch.BatchResultModelImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;

/**
 * Implémentation du service de suivi des batchs
 * 
 */
public class SuiviBatchServiceImpl extends DefaultComponent implements SuiviBatchService {

	private static final STLogger				LOGGER		= STLogFactory.getLog(SuiviBatchServiceImpl.class);

	private static volatile PersistenceProvider	persistenceProvider;

	private static final long					TOMCAT_1	= 8180;
	private static final long					TOMCAT_2	= 8280;

	public SuiviBatchServiceImpl() {
		super();
		// Default constructor
	}

	/**
	 * Recuperation du persistence provider
	 * 
	 * @return
	 */
	private static PersistenceProvider getOrCreatePersistenceProvider() {
		if (persistenceProvider == null) {
			synchronized (SuiviBatchServiceImpl.class) {
				if (persistenceProvider == null) {
					activatePersistenceProvider();
				}
			}
		}
		return persistenceProvider;
	}

	private static void activatePersistenceProvider() {
		Thread thread = Thread.currentThread();
		ClassLoader last = thread.getContextClassLoader();
		try {
			thread.setContextClassLoader(PersistenceProvider.class.getClassLoader());
			PersistenceProviderFactory persistenceProviderFactory = Framework
					.getLocalService(PersistenceProviderFactory.class);
			persistenceProvider = persistenceProviderFactory.newProvider("sword-provider");
			persistenceProvider.openPersistenceUnit();
		} finally {
			thread.setContextClassLoader(last);
		}
	}

	@Override
	public void deactivate(ComponentContext context) throws Exception {
		deactivatePersistenceProvider();
	}

	private static void deactivatePersistenceProvider() {
		if (persistenceProvider != null) {
			synchronized (SuiviBatchServiceImpl.class) {
				if (persistenceProvider != null) {
					persistenceProvider.closePersistenceUnit();
					persistenceProvider = null;
				}
			}
		}
	}

	@Override
	public BatchLoggerModel createBatchLogger(final String eventName) throws ClientException {

		BatchLoggerModel blm = getOrCreatePersistenceProvider().run(false, new RunCallback<BatchLoggerModel>() {

			@Override
			public BatchLoggerModel runWith(EntityManager entityManager) {
				BatchLoggerModel batchLoggerModel = new BatchLoggerModelImpl();
				batchLoggerModel.setStartTime(Calendar.getInstance());
				batchLoggerModel.setName(eventName);

				final ConfigService configService = STServiceLocator.getConfigService();
				String hostServer = configService.getValue(STConfigConstants.SERVER_HOST);
				batchLoggerModel.setServer(hostServer);
				BufferedReader buf = null;
				try {
					Runtime runtime = Runtime.getRuntime();
					Process proc = runtime.exec("hostname");
					buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					String hostname = null;
					hostname = buf.readLine();
					batchLoggerModel.setServer(hostname);
					proc.waitFor();
				} catch (InterruptedException exc) {
					LOGGER.debug(null, STLogEnumImpl.FAIL_GET_HOSTNAME_TEC, exc);
				} catch (IOException exc) {
					LOGGER.debug(null, STLogEnumImpl.FAIL_GET_HOSTNAME_TEC, exc);
				} finally {
					if (buf != null) {
						try {
							buf.close();
						} catch (IOException exc) {
							LOGGER.debug(null, STLogEnumImpl.FAIL_CLOSE_STREAM_TEC, exc);
						}
					}
				}

				// utilisation du port du tomcat de la config
				long tomcatPort = Long.parseLong(configService.getValue(STConfigConstants.TOMCAT_PORT));
				if (tomcatPort == TOMCAT_1) {
					batchLoggerModel.setTomcat(1);
				} else if (tomcatPort == TOMCAT_2) {
					batchLoggerModel.setTomcat(2);
				} else {
					batchLoggerModel.setTomcat(0);
				}

				// Mise de type par défaut : TECHNIQUE
				batchLoggerModel.setType(TypeBatch.TECHNIQUE.name());
				batchLoggerModel.setErrorCount(0);

				entityManager.persist(batchLoggerModel);
				entityManager.flush();

				return batchLoggerModel;
			}
		});
		return blm;
	}

	@Override
	public BatchLoggerModel findBatchLogById(final Long idLog) throws ClientException {
		return getOrCreatePersistenceProvider().run(false, new RunCallback<BatchLoggerModel>() {

			@Override
			public BatchLoggerModel runWith(EntityManager entityManager) {
				try {
					return (BatchLoggerModel) entityManager
							.createQuery("Select log from BatchLogger log where log.idLog = :idLog")
							.setParameter("idLog", idLog).getSingleResult();
				} catch (NoResultException exc) {
					LOGGER.debug(null, STLogEnumImpl.LOG_EXCEPTION_TEC, exc);
					return null;
				}
			}
		});
	}

	@Override
	public List<BatchLoggerModel> findBatchLog(final Calendar dateDebut, final Calendar dateFin, final long idParent)
			throws ClientException {
		return getOrCreatePersistenceProvider().run(false, new RunCallback<List<BatchLoggerModel>>() {

			@SuppressWarnings("unchecked")
			@Override
			public List<BatchLoggerModel> runWith(EntityManager entityManager) {
				try {
					StringBuilder queryBatchLogs = new StringBuilder();
					queryBatchLogs.append("Select log from BatchLogger log WHERE log.parentId = :idParent");
					if (dateDebut != null || dateFin != null) {
						queryBatchLogs.append(" AND ");
						if (dateDebut != null) {
							queryBatchLogs.append("log.startTime >= :startTime");
							if (dateFin != null) {
								queryBatchLogs.append(" AND ");
							}
						}
						if (dateFin != null) {
							queryBatchLogs.append("log.endTime <= :endTime");
						}
					}
					queryBatchLogs.append(" ORDER BY log.startTime");
					Query query = entityManager.createQuery(queryBatchLogs.toString());
					if (dateDebut != null) {
						query.setParameter("startTime", dateDebut);
					}
					if (dateFin != null) {
						query.setParameter("endTime", dateFin);
					}
					query.setParameter("idParent", idParent);
					return query.getResultList();
				} catch (NoResultException exc) {
					LOGGER.debug(null, STLogEnumImpl.LOG_EXCEPTION_TEC, exc);
					return null;
				}
			}
		});
	}

	@Override
	public void flushBatchLogger(final BatchLoggerModel batchLoggerModel) throws ClientException {

		getOrCreatePersistenceProvider().run(false, new RunVoid() {

			@Override
			public void runWith(EntityManager entityManager) {
				BatchLoggerModel model = entityManager.merge(batchLoggerModel);
				entityManager.persist(model);
				entityManager.flush();
			}
		});
	}

	@Override
	public List<BatchResultModel> findBatchResult(final BatchLoggerModel currentBatchLogger) throws ClientException {
		return getOrCreatePersistenceProvider().run(false, new RunCallback<List<BatchResultModel>>() {

			@SuppressWarnings("unchecked")
			@Override
			public List<BatchResultModel> runWith(EntityManager entityManager) {
				try {
					return entityManager.createQuery("Select log from BatchResult log where log.idLog= :idLog")
							.setParameter("idLog", currentBatchLogger.getIdLog()).getResultList();
				} catch (NoResultException exc) {
					LOGGER.debug(null, STLogEnumImpl.LOG_EXCEPTION_TEC, exc);
					return null;
				}
			}
		});
	}

	@Override
	public void flushBatchResult(final BatchResultModel batchResultModel) throws ClientException {
		getOrCreatePersistenceProvider().run(false, new RunVoid() {

			@Override
			public void runWith(EntityManager entityManager) {
				BatchResultModel model = entityManager.merge(batchResultModel);
				entityManager.persist(model);
				entityManager.flush();
			}
		});
	}

	@Override
	public BatchResultModel createBatchResultFor(final BatchLoggerModel batchLoggerModel, final String text,
			final long executionResult, final long executionTime) throws ClientException {

		BatchResultModel brm = getOrCreatePersistenceProvider().run(false, new RunCallback<BatchResultModel>() {

			@Override
			public BatchResultModel runWith(EntityManager entityManager) {
				BatchResultModel batchResultModel = new BatchResultModelImpl();
				batchResultModel.setLogId(batchLoggerModel.getIdLog());
				batchResultModel.setExecutionTime(executionTime);
				batchResultModel.setText(text);
				batchResultModel.setExecutionResult(executionResult);

				entityManager.persist(batchResultModel);
				entityManager.flush();

				return batchResultModel;
			}
		});
		return brm;
	}

	@Override
	@Transactional
	public BatchResultModel createBatchResultFor(final BatchLoggerModel batchLoggerModel, final String text,
			final long executionTime) throws ClientException {

		BatchResultModel brm = getOrCreatePersistenceProvider().run(false, new RunCallback<BatchResultModel>() {

			@Override
			public BatchResultModel runWith(EntityManager entityManager) {
				BatchResultModel batchResultModel = new BatchResultModelImpl();
				batchResultModel.setLogId(batchLoggerModel.getIdLog());
				batchResultModel.setExecutionTime(executionTime);
				batchResultModel.setText(text);

				entityManager.persist(batchResultModel);
				entityManager.flush();

				return batchResultModel;
			}
		});
		return brm;
	}

	@Override
	public List<List<String>> findQrtzInfo(final CoreSession session) throws ClientException {
		String query = "select t.job_name, t.prev_fire_time, t.next_fire_time, c.cron_expression "
				+ "FROM QRTZ_TRIGGERS t JOIN QRTZ_CRON_TRIGGERS c ON t.trigger_name=c.trigger_name";
		IterableQueryResult res = null;
		String[] colonnes = { FlexibleQueryMaker.COL_ID, "dc:title", "dc:creator", "dc:source" };
		List<List<String>> results = new ArrayList<List<String>>();
		try {
			res = QueryUtils.doSqlQuery(session, colonnes, query, null);
			if (res != null) {
				final Iterator<Map<String, Serializable>> iterator = res.iterator();
				while (iterator.hasNext()) {
					final Map<String, Serializable> row = iterator.next();
					String jobName = (String) row.get(FlexibleQueryMaker.COL_ID);
					String prevTime = (String) row.get("dc:title");
					String nextTime = (String) row.get("dc:creator");
					String cronExpr = (String) row.get("dc:source");
					List<String> result = new ArrayList<String>();
					result.add(jobName);
					result.add(prevTime);
					result.add(nextTime);
					result.add(cronExpr);
					results.add(result);
				}
			}
			return results;
		} catch (ClientException exc) {
			LOGGER.warn(session, STLogEnumImpl.FAIL_EXEC_SQL, exc.getMessage());
			LOGGER.debug(session, STLogEnumImpl.FAIL_EXEC_SQL, exc.getMessage(), exc);
			return null;
		} finally {
			if (res != null) {
				res.close();
			}
		}
	}
}
