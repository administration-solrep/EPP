package fr.dila.st.core.requete.recherchechamp.descriptor;

import fr.dila.st.core.requete.recherchechamp.ChampParameter;
import fr.dila.st.core.requete.recherchechamp.Parametre;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("champ")
public class ChampDescriptor implements Serializable {
    private static final long serialVersionUID = -941392442264348386L;

    @XNode("@name")
    private String name;

    @XNode("@label")
    private String label;

    @XNode("@field")
    private String field;

    @XNode("@typeChamp")
    private String typeChamp;

    @XNode("@nestedPath")
    private String nestedPath;

    @XNodeList(value = "parametres/parametre", type = ArrayList.class, componentType = ParametreDescriptor.class)
    private List<ParametreDescriptor> parametreDescriptors = new ArrayList<>();

    private List<Parametre> parametres = new ArrayList<>();

    @XNode("@champParameter")
    private Class<? extends ChampParameter> champParameterKlass;

    @XNode("@categorie")
    private String categorie;

    public ChampDescriptor() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTypeChamp() {
        return typeChamp;
    }

    public void setTypeChamp(String typeChamp) {
        this.typeChamp = typeChamp;
    }

    public String getNestedPath() {
        return nestedPath;
    }

    public void setNestedPath(String nestedPath) {
        this.nestedPath = nestedPath;
    }

    public List<ParametreDescriptor> getParametreDescriptors() {
        return parametreDescriptors;
    }

    public List<Parametre> getParametres() {
        return parametres;
    }

    public void setParametres(List<Parametre> parametres) {
        this.parametres = parametres;
    }

    public Class<? extends ChampParameter> getChampParameterKlass() {
        return champParameterKlass;
    }

    public void setChampParameterKlass(Class<? extends ChampParameter> champParameterKlass) {
        this.champParameterKlass = champParameterKlass;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Serializable getValueParamByName(String name) {
        return parametres
            .stream()
            .filter(p -> p.getName().equals(name))
            .findFirst()
            .map(Parametre::getValue)
            .orElse(name);
    }
}
