package fr.dila.st.ui.bean;

import java.util.ArrayList;
import java.util.List;

public class MessageVersion {
    private String communicationId;
    private String communicationLabel;
    private String communicationType;
    private List<MessageVersion> lstChilds = new ArrayList<>();
    private boolean courant;
    private boolean annule;

    public MessageVersion() {
        // default constructor
    }

    public MessageVersion(
        String communicationId,
        String communicationLabel,
        String communicationType,
        boolean courant,
        boolean annule
    ) {
        this.communicationId = communicationId;
        this.communicationLabel = communicationLabel;
        this.communicationType = communicationType;
        this.courant = courant;
        this.annule = annule;
    }

    public String getCommunicationId() {
        return communicationId;
    }

    public void setCommunicationId(String communicationId) {
        this.communicationId = communicationId;
    }

    public String getCommunicationLabel() {
        return communicationLabel;
    }

    public void setCommunicationLabel(String label) {
        this.communicationLabel = label;
    }

    public String getCommunicationType() {
        return communicationType;
    }

    public void setCommunicationType(String communicationType) {
        this.communicationType = communicationType;
    }

    public List<MessageVersion> getLstChilds() {
        return lstChilds;
    }

    public void setLstChilds(List<MessageVersion> lstChilds) {
        this.lstChilds = lstChilds;
    }

    public boolean isCourant() {
        return courant;
    }

    public void setCourant(boolean courant) {
        this.courant = courant;
    }

    public boolean isAnnule() {
        return annule;
    }

    public void setAnnule(boolean annule) {
        this.annule = annule;
    }
}
