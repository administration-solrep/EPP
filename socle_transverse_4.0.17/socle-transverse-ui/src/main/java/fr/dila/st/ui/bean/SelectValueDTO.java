package fr.dila.st.ui.bean;

import com.google.gson.Gson;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class SelectValueDTO implements Serializable {
    private static final long serialVersionUID = -4700162309852517537L;

    private final String id;
    private final String label;

    public SelectValueDTO() {
        this(null, null);
    }

    public SelectValueDTO(String id) {
        this.id = id;
        this.label = id;
    }

    public SelectValueDTO(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getKey() {
        return id;
    }

    public String getValue() {
        return label;
    }

    public static Comparator<SelectValueDTO> getLabelComparator() {
        return Comparator.comparing(SelectValueDTO::getLabel);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        SelectValueDTO other = (SelectValueDTO) obj;
        return Objects.equals(id, other.id) && Objects.equals(label, other.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
