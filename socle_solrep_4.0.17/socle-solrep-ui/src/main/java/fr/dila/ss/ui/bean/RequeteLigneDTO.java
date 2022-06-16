package fr.dila.ss.ui.bean;

import fr.dila.ss.core.enumeration.OperatorEnum;
import fr.dila.st.core.requete.recherchechamp.descriptor.ChampDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RequeteLigneDTO implements Serializable {
    private static final long serialVersionUID = 8477599271836999347L;

    public RequeteLigneDTO() {}

    private OperatorEnum operator;

    private ChampDescriptor champ;

    private List<String> value = new ArrayList<>();

    private List<String> displayValue;

    private String andOr;

    public OperatorEnum getOperator() {
        return operator;
    }

    public void setOperator(OperatorEnum operator) {
        this.operator = operator;
    }

    public ChampDescriptor getChamp() {
        return champ;
    }

    public void setChamp(ChampDescriptor champ) {
        this.champ = champ;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public List<String> getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(List<String> displayValue) {
        this.displayValue = displayValue;
    }

    public String getAndOr() {
        return andOr;
    }

    public void setAndOr(String andOr) {
        this.andOr = andOr;
    }
}
