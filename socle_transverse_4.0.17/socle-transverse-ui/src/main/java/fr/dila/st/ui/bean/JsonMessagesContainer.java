package fr.dila.st.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonMessagesContainer implements Serializable {
    private static final long serialVersionUID = -8787324958878171679L;

    private List<AlertContainer> infoMessageQueue = new ArrayList<>();
    private List<AlertContainer> warningMessageQueue = new ArrayList<>();
    private List<AlertContainer> dangerMessageQueue = new ArrayList<>();
    private List<AlertContainer> successMessageQueue = new ArrayList<>();

    public JsonMessagesContainer() {}

    public JsonMessagesContainer(
        List<AlertContainer> infoMessageQueue,
        List<AlertContainer> warningMessageQueue,
        List<AlertContainer> dangerMessageQueue,
        List<AlertContainer> successMessageQueue
    ) {
        this.infoMessageQueue = infoMessageQueue;
        this.warningMessageQueue = warningMessageQueue;
        this.dangerMessageQueue = dangerMessageQueue;
        this.successMessageQueue = successMessageQueue;
    }

    public List<AlertContainer> getInfoMessageQueue() {
        return infoMessageQueue;
    }

    public void setInfoMessageQueue(List<AlertContainer> infoMessageQueue) {
        this.infoMessageQueue = infoMessageQueue;
    }

    public List<AlertContainer> getWarningMessageQueue() {
        return warningMessageQueue;
    }

    public void setWarningMessageQueue(List<AlertContainer> warningMessageQueue) {
        this.warningMessageQueue = warningMessageQueue;
    }

    public List<AlertContainer> getDangerMessageQueue() {
        return dangerMessageQueue;
    }

    public void setDangerMessageQueue(List<AlertContainer> dangerMessageQueue) {
        this.dangerMessageQueue = dangerMessageQueue;
    }

    public List<AlertContainer> getSuccessMessageQueue() {
        return successMessageQueue;
    }

    public void setSuccessMessageQueue(List<AlertContainer> successMessageQueue) {
        this.successMessageQueue = successMessageQueue;
    }
}
