package fr.dila.st.api.event.batch;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Interface de log pour l'affichage du suivi des batchs
 *
 * @author JBT
 *
 */
public interface BatchLoggerResultModel extends Serializable {
    String getName();

    void setName(String name);

    String getType();

    void setType(String type);

    Calendar getStartTime();

    void setStartTime(Calendar startTime);

    Calendar getEndTime();

    void setEndTime(Calendar endTime);

    long getExecutionTime();

    void setExecutionTime(long executionTime);

    long getExecutionResult();

    void setExecutionResult(long executionResult);

    long getErrorCount();

    void setErrorCount(long errorCount);

    String getText();

    void setText(String text);
}
