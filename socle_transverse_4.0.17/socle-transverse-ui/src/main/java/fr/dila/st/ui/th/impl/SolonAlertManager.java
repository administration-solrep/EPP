package fr.dila.st.ui.th.impl;

import static fr.dila.st.ui.th.enums.AlertLevel.*;

import avro.shaded.com.google.common.collect.Lists;
import fr.dila.st.ui.bean.AlertContainer;
import fr.dila.st.ui.th.enums.AlertLevel;
import fr.dila.st.ui.th.enums.AlertType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class SolonAlertManager {
    private final List<AlertContainer> messageQueue = new ArrayList<>();

    private List<AlertContainer> getQueueWithSeverity(AlertLevel severity) {
        if (severity == null) {
            return messageQueue;
        }
        return messageQueue
            .stream()
            .filter(alert -> alert.getAlertType() != null && alert.getAlertType().getLevel() == severity)
            .collect(Collectors.toList());
    }

    public List<AlertContainer> getInfoQueue() {
        return getQueueWithSeverity(INFO);
    }

    public List<AlertContainer> getWarnQueue() {
        return getQueueWithSeverity(WARNING);
    }

    public List<AlertContainer> getErrorQueue() {
        return getQueueWithSeverity(DANGER);
    }

    public List<AlertContainer> getSuccessQueue() {
        return getQueueWithSeverity(SUCCESS);
    }

    public void addMessageToQueue(String message, AlertType level, String target) {
        messageQueue
            .stream()
            .filter(
                alert ->
                    level == alert.getAlertType() &&
                    (StringUtils.isAllBlank(target, alert.getAlertOrigin()) || target.equals(alert.getAlertOrigin()))
            )
            .findFirst()
            .map(alert -> alert.getAlertMessage().add(message))
            .orElseGet(() -> messageQueue.add(new AlertContainer(Lists.newArrayList(message), target, level)));
    }

    public void addMessageToQueue(String message, AlertType level) {
        addMessageToQueue(message, level, null);
    }

    /**
     * Add contextual warning message
     *
     * @param message
     *            : information to display
     * @param target
     *            : front id to bind the message
     */
    public void addWarnToQueue(String message, String target) {
        addMessageToQueue(message, AlertType.ALERT_WARNING, target);
    }

    /**
     * Add a warning message under the header
     *
     * @param message
     *            : information to display
     */
    public void addWarnToQueue(String message) {
        addMessageToQueue(message, AlertType.ALERT_WARNING, null);
    }

    /**
     * Add a contextual information message
     *
     * @param message
     *            : information to display
     * @param target
     *            : front id to bind the message
     */
    public void addInfoToQueue(String message, String target) {
        addMessageToQueue(message, AlertType.ALERT_INFO, target);
    }

    /**
     * Add an info message under the header
     *
     * @param message
     *            : information to display
     */
    public void addInfoToQueue(String message) {
        addMessageToQueue(message, AlertType.ALERT_INFO, null);
    }

    /**
     * Add contextual error message
     *
     * @param message
     *            : information to display
     * @param target
     *            : front id to bind the message
     */
    public void addErrorToQueue(String message, String target) {
        addMessageToQueue(message, AlertType.ALERT_DANGER, target);
    }

    /**
     * Add an error message under the header
     *
     * @param message
     *            : information to display
     */
    public void addErrorToQueue(String message) {
        addMessageToQueue(message, AlertType.ALERT_DANGER, null);
    }

    /**
     * Add a contextual success message
     *
     * @param message
     *            : information to display
     * @param target
     *            : front id to bind the message
     */
    public void addSuccessToQueue(String message, String target) {
        addMessageToQueue(message, AlertType.ALERT_SUCCESS, target);
    }

    /**
     * Add a success message under the header
     *
     * @param message
     *            : information to display
     */
    public void addSuccessToQueue(String message) {
        addMessageToQueue(message, AlertType.ALERT_SUCCESS, null);
    }

    public void addToastSuccess(String message) {
        addMessageToQueue(message, AlertType.TOAST_SUCCESS);
    }

    public boolean hasMessageInQueue() {
        return messageQueue.stream().anyMatch(alert -> alert.getAlertType() != null);
    }
}
