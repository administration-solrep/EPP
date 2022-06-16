package fr.dila.ss.api.birt;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/** Une propriété du rapport. */
@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportProperty {
    /**
     * Préfixe permettant d'identifier les types de propriétés scalaires (à envoyer
     * à Birt pour la génération du rapport)
     */
    public static final String SCALAR_PTY_PREFIX = "scalar_";

    public static final String SCALAR_TYPE = "scalar_value";

    /**
     * Type de propriété scalaire : un ministère à sélectionner parmi les ministères
     * courants.
     */
    public static final String TYPE_SCALAR_ORGANIGRAMMESELECT_MIN = SCALAR_PTY_PREFIX + "organigrammeSelect_MIN";
    /**
     * Type de propriété scalaire : une unité structurelle à sélectionner parmi les
     * unités structurelles courantes.
     */
    public static final String TYPE_SCALAR_ORGANIGRAMMESELECT_UST = SCALAR_PTY_PREFIX + "organigrammeSelect_UST";

    /**
     * Type de propriété scalaire : une unité structurelle à sélectionner parmi les
     * postes courants.
     */
    public static final String TYPE_SCALAR_ORGANIGRAMMESELECT_PST = SCALAR_PTY_PREFIX + "organigrammeSelect_POSTE";

    /** Type de propriété scalaire : une valeur (value) à envoyer à Birt. */
    public static final String TYPE_SCALAR_VALUE = SCALAR_PTY_PREFIX + "value";
    /**
     * Type de propriété permettant d'afficher le rapport seulement si l'utilisateur
     * dispose d'une fonction/droit précise. La fonction en question est alors dans
     * l'attribut name.
     */
    public static final String TYPE_FUNCTION = "function";

    /** Type de la propriété */
    @XmlAttribute(name = "type")
    private String type;

    /** Nom de la propriété */
    @XmlAttribute(name = "name")
    private String name;

    /** Valeur de la propriété */
    @XmlAttribute(name = "value")
    private String value;

    private ReportProperty() {
        // Default constructor
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isScalar() {
        return getType().startsWith(SCALAR_PTY_PREFIX);
    }
}
