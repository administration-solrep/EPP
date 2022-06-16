package fr.dila.st.ui.th.enums;

import static fr.dila.st.ui.th.enums.AlertLevel.DANGER;
import static fr.dila.st.ui.th.enums.AlertLevel.INFO;
import static fr.dila.st.ui.th.enums.AlertLevel.SUCCESS;
import static fr.dila.st.ui.th.enums.AlertLevel.WARNING;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AlertType {
    ALERT_INFO(INFO, "alert alert--info"),
    ALERT_DANGER(DANGER, "alert alert--danger"),
    ALERT_WARNING(WARNING, "alert alert--warning"),
    ALERT_SUCCESS(SUCCESS, "alert alert--success"),
    LIGHT_INFO(INFO, "alert__light alert alert--info"),
    LIGHT_DANGER(DANGER, "alert__light alert alert--danger"),
    LIGHT_WARNING(WARNING, "alert__light alert alert--warning"),
    LIGHT_SUCCESS(SUCCESS, "alert__light alert alert--success"),
    PROGRESS_INFO(INFO, "bar-close alert alert--info"),
    PROGRESS_DANGER(DANGER, "bar-close alert alert--danger"),
    PROGRESS_WARNING(WARNING, "bar-close alert alert--warning"),
    PROGRESS_SUCCESS(SUCCESS, "bar-close alert alert--success"),
    TOAST_INFO(INFO, "alert alert--info toast toast--shadow alert--fixed"),
    TOAST_DANGER(DANGER, "alert alert--danger toast toast--shadow"),
    TOAST_WARNING(WARNING, "alert alert--warning toast toast--shadow"),
    TOAST_SUCCESS(SUCCESS, "alert alert--success toast toast--shadow");

    private static final String ALERT = "alert";
    private static final String STATUS = "status";

    private final AlertLevel level;
    private final String cssClass;
    private final String role;
    private final boolean hasCloseButton;

    AlertType(AlertLevel level, String cssClass) {
        this.level = level;
        this.cssClass = cssClass;
        if (name().contains("TOAST")) {
            hasCloseButton = true;
            if (name().contains("DANGER")) {
                role = ALERT;
            } else {
                role = STATUS;
            }
        } else {
            role = null;
            hasCloseButton = false;
        }
    }

    public AlertLevel getLevel() {
        return level;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getRole() {
        return role;
    }

    public boolean getHasCloseButton() {
        return hasCloseButton;
    }
}
