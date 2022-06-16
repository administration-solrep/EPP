package fr.dila.st.ui.bean;

import fr.dila.st.core.util.ResourceHelper;

public class VersionSelectDTO {
    private String text;
    private String value;
    private boolean rejete;
    private boolean accuse;
    private String date;

    public VersionSelectDTO() {
        super();
    }

    public VersionSelectDTO(String text, String value, boolean rejete, boolean hasAR, String date) {
        super();
        this.text = text;
        this.value = value;
        this.rejete = rejete;
        this.accuse = hasAR;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isRejete() {
        return rejete;
    }

    public void setRejete(boolean rejete) {
        this.rejete = rejete;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        String description = "";
        if (isRejete()) {
            description = ResourceHelper.getString("comm.version.rejete", date);
        } else if (isAccuse()) {
            description = ResourceHelper.getString("comm.version.accusee", date);
        }

        return description;
    }

    public boolean isAccuse() {
        return accuse;
    }

    public void setAccuse(boolean accuse) {
        this.accuse = accuse;
    }
}
