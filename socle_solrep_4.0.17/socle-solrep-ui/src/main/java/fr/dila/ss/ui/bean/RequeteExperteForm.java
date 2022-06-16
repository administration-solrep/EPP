package fr.dila.ss.ui.bean;

import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import javax.ws.rs.FormParam;

@SwBean
public class RequeteExperteForm {

    public RequeteExperteForm() {}

    @FormParam("operator")
    private String operator;

    @FormParam("champ")
    private String champ;

    @FormParam("value")
    private ArrayList<String> value;

    @FormParam("displayValue")
    private ArrayList<String> displayValue;

    @FormParam("andOr")
    private String andOr;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getChamp() {
        return champ;
    }

    public void setChamp(String champ) {
        this.champ = champ;
    }

    public ArrayList<String> getValue() {
        return value;
    }

    public void setValue(ArrayList<String> value) {
        this.value = value;
    }

    public ArrayList<String> getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(ArrayList<String> displayValue) {
        this.displayValue = displayValue;
    }

    public String getAndOr() {
        return andOr;
    }

    public void setAndOr(String andOr) {
        this.andOr = andOr;
    }
}
