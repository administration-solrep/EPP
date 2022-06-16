package fr.dila.st.core.requete.recherchechamp;

import fr.dila.st.core.requete.recherchechamp.descriptor.ParametreDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Parametre implements Serializable {
    private static final long serialVersionUID = 3956610008402500233L;

    private String name;
    private Serializable value;

    public Parametre() {}

    public Parametre(String name, Serializable value) {
        this.name = name;
        this.value = value;
    }

    public <T extends Serializable>Parametre(String name, List<T> values) {
        this(name, (Serializable) new ArrayList<>(values));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Serializable getValue() {
        return value;
    }

    public void setValue(Serializable value) {
        this.value = value;
    }

    public static Parametre asParametre(ParametreDescriptor param) {
        return new Parametre(param.getName(), param.getValue());
    }
}
