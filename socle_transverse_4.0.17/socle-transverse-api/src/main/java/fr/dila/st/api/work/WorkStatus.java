package fr.dila.st.api.work;

public enum WorkStatus {
    PENDING("En attente de traitement"),
    EXECUTING("En cours de traitement"),
    DONE("Terminé");

    /**
     * Version lisible du status
     */
    private final String statusStr;

    WorkStatus(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getStatus() {
        return statusStr;
    }
}
