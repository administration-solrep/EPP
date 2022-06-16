package fr.dila.st.ui.bean;

import java.io.Serializable;

public class NotificationDTO implements Serializable {
    private static final long serialVersionUID = 6059548174131149834L;

    private boolean isCorbeilleModified = false;

    private boolean isEvenementModified = false;

    public boolean isEvenementModified() {
        return isEvenementModified;
    }

    public void setEvenementModified(boolean isEvenementModified) {
        this.isEvenementModified = isEvenementModified;
    }

    public boolean isCorbeilleModified() {
        return isCorbeilleModified;
    }

    public void setCorbeilleModified(boolean isCorbeilleModified) {
        this.isCorbeilleModified = isCorbeilleModified;
    }
}
