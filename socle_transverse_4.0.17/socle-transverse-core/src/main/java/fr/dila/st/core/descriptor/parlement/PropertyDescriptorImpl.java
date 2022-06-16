package fr.dila.st.core.descriptor.parlement;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.descriptor.parlement.DefaultValue;
import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur des properties d'une metadonnée.
 *
 * @author asatre
 */
@XObject("property")
public class PropertyDescriptorImpl implements PropertyDescriptor {
    /**
     * nom de la propriété
     */
    @XNode("@name")
    private String name;

    /**
     * nom de la propriété dans les webservices
     */
    @XNode("@nameWS")
    private String nameWS;

    /**
     * label de la propriété dans le bureau virtuel
     */
    @XNode("@label")
    private String label;

    /**
     * Propriété obligatoire ou non
     *
     * @default true
     */
    @XNode("@obligatoire")
    private boolean obligatoire;

    /**
     * Type de la propriété
     */
    @XNode("@type")
    private String type;

    /**
     * Propriété modifiable ou non
     *
     * @default true
     */
    @XNode("@modifiable")
    private boolean modifiable;

    /**
     * Propriété multi-valuée ou non
     *
     * @default false
     */
    @XNode("@multiValue")
    private boolean multiValue;

    /**
     * Propriété visible dans la fiche dossier ou non
     *
     * @default true
     */
    @XNode("@ficheDossier")
    private boolean ficheDossier;

    /**
     * Propriété reseigné par l'EPP ou non
     *
     * @default false
     */
    @XNode("@renseignerEpp")
    private boolean renseignerEpp;

    /**
     * Propriété
     */
    @XNode("@institutions")
    private String institutions;

    /**
     * visibilité de la propriété (true : limite la visibilité au senat)
     *
     * @default false
     */
    @XNode("@visibility")
    private boolean visibility;

    /**
     * Valeur par defaut de la propriété.
     */
    @XNode(value = "defaultValue")
    private DefaultValueImpl defaultValue;

    @XNode("@minDate")
    private String minDate;

    @XNode("@hidden")
    private boolean hidden;

    public PropertyDescriptorImpl() {
        multiValue = false;
        obligatoire = true;
        modifiable = true;
        ficheDossier = true;
        renseignerEpp = false;
        visibility = false;
        institutions = "";
        hidden = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSchema() {
        String schema = "version";
        if (STSchemaConstant.DUBLINCORE_DESCRIPTION_PROPERTY.equals(name)) {
            schema = STSchemaConstant.DUBLINCORE_SCHEMA;
        }
        return schema;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getNameWS() {
        return nameWS;
    }

    @Override
    public void setNameWS(String nameWS) {
        this.nameWS = nameWS;
    }

    @Override
    public boolean isObligatoire() {
        return obligatoire;
    }

    @Override
    public void setObligatoire(boolean obligatoire) {
        this.obligatoire = obligatoire;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean isModifiable() {
        return modifiable;
    }

    @Override
    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }

    @Override
    public boolean isMultiValue() {
        return multiValue;
    }

    @Override
    public void setMultiValue(boolean multiValue) {
        this.multiValue = multiValue;
    }

    @Override
    public void setFicheDossier(boolean ficheDossier) {
        this.ficheDossier = ficheDossier;
    }

    @Override
    public boolean isFicheDossier() {
        return ficheDossier;
    }

    @Override
    public void setRenseignerEpp(boolean renseignerEpp) {
        this.renseignerEpp = renseignerEpp;
    }

    @Override
    public boolean isRenseignerEpp() {
        return renseignerEpp;
    }

    @Override
    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    @Override
    public boolean isVisibility() {
        return visibility;
    }

    @Override
    public void setInstitutions(String institutions) {
        this.institutions = institutions;
    }

    @Override
    public String getInstitutions() {
        return institutions;
    }

    @Override
    public List<String> getListInstitutions() {
        return Stream
            .of(StringUtils.split(institutions, ","))
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
    }

    @Override
    public void setDefaultValue(DefaultValue defaultValue) {
        this.defaultValue = (DefaultValueImpl) defaultValue;
    }

    @Override
    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = true;
        if (this == obj) {
            return equals;
        } else if (obj == null) {
            equals = false;
        } else if (getClass() != obj.getClass()) {
            equals = false;
        } else {
            PropertyDescriptorImpl other = (PropertyDescriptorImpl) obj;
            if (name == null) {
                if (other.name != null) {
                    equals = false;
                }
            } else if (!name.equals(other.name)) {
                equals = false;
            }
        }
        return equals;
    }

    /**
     * @return the label
     */
    @Override
    public String getLabel() {
        return label;
    }

    /**
     * @param label
     *            the label to set
     */
    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    @Override
    public String getMinDate() {
        return this.minDate;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }
}
