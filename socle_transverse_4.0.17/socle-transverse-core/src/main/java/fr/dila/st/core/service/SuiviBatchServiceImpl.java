package fr.dila.st.core.service;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_MINUS_ONE;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.constant.STSuiviBatchsConstants.TypeBatch;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.event.batch.BatchResultModel;
import fr.dila.st.api.event.batch.QuartzInfo;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.model.Page;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.batch.BatchLoggerModelImpl;
import fr.dila.st.core.event.batch.BatchResultModelImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Implémentation du service de suivi des batchs
 *
 */
public class SuiviBatchServiceImpl extends AbstractPersistenceDefaultComponent implements SuiviBatchService {
    private static final STLogger LOGGER = STLogFactory.getLog(SuiviBatchServiceImpl.class);

    private static final long TOMCAT_1 = 8180;
    private static final long TOMCAT_2 = 8280;

    public SuiviBatchServiceImpl() {
        super();
        // Default constructor
    }

    @Override
    public BatchLoggerModel createBatchLogger(final String eventName) {
        return apply(
            false,
            entityManager -> {
                BatchLoggerModel batchLoggerModel = new BatchLoggerModelImpl();
                batchLoggerModel.setStartTime(Calendar.getInstance());
                batchLoggerModel.setName(eventName);

                final ConfigService configService = STServiceLocator.getConfigService();
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
        );
    }

    @Override
    public BatchLoggerModel findBatchLogById(final Long idLog) {
        return apply(
            false,
            entityManager -> {
                try {
                    return (BatchLoggerModel) entityManager
                        .createQuery("Select log from BatchLogger log where log.idLog = :idLog")
                        .setParameter("idLog", idLog)
                        .getSingleResult();
                } catch (NoResultException exc) {
                    LOGGER.debug(null, STLogEnumImpl.LOG_EXCEPTION_TEC, exc);
                    return null;
                }
            }
        );
    }

    @Override
    public Page<BatchLoggerModel> findBatchLog(
        final Calendar dateDebut,
        final Calendar dateFin,
        final long idParent,
        Integer page,
        Integer pageSize
    ) {
        return apply(
            false,
            entityManager -> {
                try {
                    Query query = buildFindBatchLogQuery(
                        entityManager,
                        "Select log",
                        dateDebut,
                        dateFin,
                        idParent,
                        page,
                        pageSize
                    );

                    @SuppressWarnings("unchecked")
                    List<BatchLoggerModel> list = query.getResultList();

                    final int resultsCount;
                    if (page != null && pageSize != null && page > INTEGER_MINUS_ONE && pageSize > INTEGER_MINUS_ONE) {
                        resultsCount = getResultsCount(entityManager, dateDebut, dateFin, idParent);
                    } else {
                        resultsCount = list.size();
                    }

                    return new Page<>(page, pageSize, list, resultsCount);
                } catch (NoResultException exc) {
                    LOGGER.debug(null, STLogEnumImpl.LOG_EXCEPTION_TEC, exc);
                    return new Page<>(page, pageSize, Collections.<BatchLoggerModel>emptyList(), 0);
                }
            }
        );
    }

    private int getResultsCount(
        EntityManager entityManager,
        Calendar dateDebut,
        Calendar dateFin,
        final long idParent
    ) {
        Query queryTotal = buildFindBatchLogQuery(
            entityManager,
            "Select count(log.idLog)",
            dateDebut,
            dateFin,
            idParent,
            null,
            null
        );
        return ((Long) queryTotal.getSingleResult()).intValue();
    }

    private Query buildFindBatchLogQuery(
        EntityManager entityManager,
        String selectClause,
        Calendar dateDebut,
        Calendar dateFin,
        long idParent,
        Integer page,
        Integer pageSize
    ) {
        StringBuilder queryBatchLogs = new StringBuilder(selectClause);
        queryBatchLogs.append(" from BatchLogger log WHERE log.parentId = :idParent");
        addTimeParameters(queryBatchLogs, dateDebut, dateFin);
        queryBatchLogs.append(" ORDER BY log.startTime");

        Query query = entityManager.createQuery(queryBatchLogs.toString());
        setPaginationParameters(query, page, pageSize);
        setTimeParameters(query, dateDebut, dateFin);
        query.setParameter("idParent", idParent);

        return query;
    }

    private void addTimeParameters(StringBuilder queryBatchLogs, Calendar dateDebut, Calendar dateFin) {
        if (dateDebut != null) {
            queryBatchLogs.append(" AND log.startTime >= :startTime");
        }
        if (dateFin != null) {
            queryBatchLogs.append(" AND log.endTime <= :endTime");
        }
    }

    private void setTimeParameters(Query query, Calendar dateDebut, Calendar dateFin) {
        if (dateDebut != null) {
            query.setParameter("startTime", dateDebut);
        }
        if (dateFin != null) {
            query.setParameter("endTime", dateFin);
        }
    }

    private void setPaginationParameters(Query query, Integer page, Integer pageSize) {
        if (page != null && pageSize != null && page > INTEGER_MINUS_ONE && pageSize > INTEGER_MINUS_ONE) {
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
        }
    }

    @Override
    public void flushBatchLogger(final BatchLoggerModel batchLoggerModel) {
        accept(
            false,
            entityManager -> {
                BatchLoggerModel model = entityManager.merge(batchLoggerModel);
                entityManager.persist(model);
                entityManager.flush();
            }
        );
    }

    @Override
    public List<BatchResultModel> findBatchResult(final BatchLoggerModel currentBatchLogger) {
        return apply(
            false,
            entityManager -> {
                try {
                    @SuppressWarnings("unchecked")
                    List<BatchResultModel> list = entityManager
                        .createQuery("Select log from BatchResult log where log.idLog= :idLog")
                        .setParameter("idLog", currentBatchLogger.getIdLog())
                        .getResultList();
                    return list;
                } catch (NoResultException exc) {
                    LOGGER.debug(null, STLogEnumImpl.LOG_EXCEPTION_TEC, exc);
                    return null;
                }
            }
        );
    }

    @Override
    public void flushBatchResult(final BatchResultModel batchResultModel) {
        accept(
            false,
            entityManager -> {
                BatchResultModel model = entityManager.merge(batchResultModel);
                entityManager.persist(model);
                entityManager.flush();
            }
        );
    }

    @Override
    public BatchResultModel createBatchResultFor(
        final BatchLoggerModel batchLoggerModel,
        final String text,
        final long executionResult,
        final long executionTime
    ) {
        return transactionApply(
            false,
            entityManager -> {
                BatchResultModel batchResultModel = new BatchResultModelImpl();
                batchResultModel.setLogId(batchLoggerModel.getIdLog());
                batchResultModel.setExecutionTime(executionTime);
                batchResultModel.setText(text);
                batchResultModel.setExecutionResult(executionResult);

                entityManager.persist(batchResultModel);
                entityManager.flush();

                return batchResultModel;
            }
        );
    }

    @Override
    public BatchResultModel createBatchResultFor(
        final BatchLoggerModel batchLoggerModel,
        final String text,
        final long executionTime
    ) {
        return createBatchResultFor(batchLoggerModel, text, 0, executionTime);
    }

    @Override
    public BatchResultModel createBatchResultFor(final BatchLoggerModel batchLoggerModel, final String text) {
        return createBatchResultFor(batchLoggerModel, text, 0, 0);
    }

    @Override
    public BatchResultModel updateBatchResultFor(
        final BatchResultModel batchResultModel,
        final long executionResult,
        final long executionTime
    ) {
        return transactionApply(
            false,
            entityManager -> {
                batchResultModel.setExecutionTime(executionTime);
                batchResultModel.setExecutionResult(executionResult);

                entityManager.merge(batchResultModel);
                entityManager.flush();

                return batchResultModel;
            }
        );
    }

    @Override
    public List<QuartzInfo> findQrtzInfo(final CoreSession session) {
        String query =
            "select t.job_name, t.prev_fire_time, t.next_fire_time, c.cron_expression " +
            "FROM QRTZ_TRIGGERS t JOIN QRTZ_CRON_TRIGGERS c ON t.trigger_name=c.trigger_name";
        String[] colonnes = { FlexibleQueryMaker.COL_ID, "dc:title", "dc:creator", "dc:source" };
        List<QuartzInfo> results = new ArrayList<>();
        try (IterableQueryResult res = QueryUtils.doSqlQuery(session, colonnes, query, null)) {
            if (res != null) {
                final Iterator<Map<String, Serializable>> iterator = res.iterator();
                while (iterator.hasNext()) {
                    final Map<String, Serializable> row = iterator.next();
                    String jobName = (String) row.get(FlexibleQueryMaker.COL_ID);
                    String prevTime = (String) row.get("dc:title");
                    String nextTime = (String) row.get("dc:creator");
                    String cronExpr = (String) row.get("dc:source");

                    QuartzInfo result = new QuartzInfo();

                    result.setJobName(jobName);
                    result.setPrevTime(new Date(Long.parseLong(prevTime)));
                    result.setNextTime(new Date(Long.parseLong(nextTime)));
                    result.setCronExpr(cronExpr);

                    results.add(result);
                }
            }
            return results;
        } catch (NuxeoException exc) {
            LOGGER.warn(session, STLogEnumImpl.FAIL_EXEC_SQL, exc.getMessage());
            LOGGER.debug(session, STLogEnumImpl.FAIL_EXEC_SQL, exc.getMessage(), exc);
            return new ArrayList<>();
        }
    }
}
