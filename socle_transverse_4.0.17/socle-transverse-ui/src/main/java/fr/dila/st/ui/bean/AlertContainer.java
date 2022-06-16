package fr.dila.st.ui.bean;

import fr.dila.st.ui.th.enums.AlertType;
import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlertContainer implements Serializable {
    private static final long serialVersionUID = 1632918695019894526L;

    private List<String> alertMessage = new ArrayList<>();
    private String alertOrigin;
    private AlertType alertType;

    public AlertContainer() {}

    public AlertContainer(List<String> alertMessage, String alertOrigin, AlertType alertType) {
        super();
        this.alertMessage = alertMessage;
        this.alertOrigin = alertOrigin;
        this.alertType = alertType;
    }

    public List<String> getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(List<String> alertMessage) {
        this.alertMessage = alertMessage;
    }

    public String getAlertOrigin() {
        return alertOrigin;
    }

    public void setAlertOrigin(String alertOrigin) {
        this.alertOrigin = alertOrigin;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    @Override
    @Transient
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alertMessage == null) ? 0 : alertMessage.hashCode());
        result = prime * result + ((alertOrigin == null) ? 0 : alertOrigin.hashCode());
        result = prime * result + ((alertType == null) ? 0 : alertType.hashCode());
        return result;
    }

    @Override
    @Transient
    public boolean equals(Object obj) {
        boolean result = true;
        if (this == obj) {
            result = true;
        }
        if (obj == null) {
            result = false;
        } else if (getClass() != obj.getClass()) {
            result = false;
        }
        AlertContainer other = (AlertContainer) obj;
        if (result) {
            if (alertMessage == null) {
                if (other.alertMessage != null) {
                    result = false;
                }
            } else if (!alertMessage.equals(other.alertMessage)) {
                result = false;
            }
            if (alertOrigin == null) {
                if (other.alertOrigin != null) {
                    result = false;
                }
            } else if (!alertOrigin.equals(other.alertOrigin)) {
                result = false;
            }
            if (alertType != other.alertType) {
                result = false;
            }
        }
        return result;
    }
}
