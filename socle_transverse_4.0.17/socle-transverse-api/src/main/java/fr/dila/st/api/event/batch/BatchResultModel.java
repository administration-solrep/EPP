package fr.dila.st.api.event.batch;

import java.io.Serializable;

/**
 * Interface de log détaillée pour le suivi des batchs
 *
 * @author JBT
 *
 */
public interface BatchResultModel extends Serializable {
    long getIdResult();

    void setIdResult(long idResult);

    long getExecutionResult();

    void setExecutionResult(long executionResult);

    String getText();

    void setText(String text);

    long getLogId();

    void setLogId(long logId);

    long getExecutionTime();

    void setExecutionTime(long executionTime);
}
