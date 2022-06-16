package fr.dila.st.api.event.batch;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Interface de log pour le suivi des batchs
 *
 * @author JBT
 *
 */
public interface BatchLoggerModel extends Serializable {
    long getIdLog();

    void setIdLog(long idLog);

    Calendar getStartTime();

    void setStartTime(Calendar startTime);

    Calendar getEndTime();

    void setEndTime(Calendar endTime);

    long getExecutionTime();

    long getErrorCount();

    void setErrorCount(long errorCount);

    String getName();

    void setName(String name);

    long getParentId();

    void setParentId(long parentId);

    String getServer();

    void setServer(String server);

    long getTomcat();

    void setTomcat(long tomcat);

    String getType();

    void setType(String type);
}
