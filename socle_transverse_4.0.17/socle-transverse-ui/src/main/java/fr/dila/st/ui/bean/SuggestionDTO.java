package fr.dila.st.ui.bean;

import java.io.Serializable;

public class SuggestionDTO implements Serializable {
    private static final long serialVersionUID = -3293126217284718760L;

    private String key;
    private String label;

    public SuggestionDTO() {}

    public SuggestionDTO(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
