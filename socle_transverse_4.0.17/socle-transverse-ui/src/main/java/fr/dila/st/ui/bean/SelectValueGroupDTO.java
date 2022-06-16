package fr.dila.st.ui.bean;

import java.util.ArrayList;
import java.util.List;

public class SelectValueGroupDTO extends SelectValueDTO {
    private List<SelectValueDTO> selectValues;

    public SelectValueGroupDTO() {
        this(null);
    }

    public SelectValueGroupDTO(String label) {
        this(label, new ArrayList<>());
    }

    public SelectValueGroupDTO(String label, List<SelectValueDTO> selectValues) {
        super(null, label);
        this.selectValues = selectValues;
    }

    public List<SelectValueDTO> getSelectValues() {
        return selectValues;
    }

    public void setSelectValues(List<SelectValueDTO> selectValues) {
        this.selectValues = selectValues;
    }
}
