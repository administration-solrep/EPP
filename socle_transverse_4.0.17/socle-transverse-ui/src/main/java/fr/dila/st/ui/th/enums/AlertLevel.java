package fr.dila.st.ui.th.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AlertLevel {
    INFO("icon icon--lightbulb bubble-icon alert__icon alert__icon--info", "Information", false),
    DANGER("icon icon--exclamation-point bubble-icon alert__icon alert__icon--danger", "Erreur", true),
    WARNING("icon icon--information bubble-icon alert__icon alert__icon--warning", "Alerte", true),
    SUCCESS("icon icon--check bubble-icon alert__icon alert__icon--success", "Confirmation", false);

    private final String icon;
    private final String srOnly;
    private final boolean isOrdered;

    AlertLevel(String icon, String srOnly, boolean isOrdered) {
        this.icon = icon;
        this.srOnly = srOnly;
        this.isOrdered = isOrdered;
    }

    public String getIcon() {
        return icon;
    }

    public String getSrOnly() {
        return srOnly;
    }

    public boolean getIsOrdered() {
        return isOrdered;
    }
}
