package fr.dila.st.ui.model.param;

import fr.dila.st.ui.annot.Command;

@Command
public class ParameterValue {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
