package fr.dila.st.core.descriptor;

import fr.dila.st.api.requeteur.RequeteurAddInfoField;
import java.util.HashMap;
import java.util.Map;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * La classe utilisée pour contenir les informations supplémentaires d'un champ. Elle sert par exemple à associer
 *
 * @author jgomez
 *
 */
@XObject("addInfoField")
public class RequeteurAddInfoFieldDescriptor implements RequeteurAddInfoField {
    @XNode("@name")
    private String name;

    @XNode("@newType")
    private String newType;

    @XNode("@newCategory")
    private String newCategory;

    @XNodeMap(value = "property", key = "@name", type = HashMap.class, componentType = String.class)
    private Map<String, String> properties;

    /**
     * Default constructor
     */
    public RequeteurAddInfoFieldDescriptor() {
        // do nothing
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNewType() {
        return newType;
    }

    @Override
    public String getNewCategory() {
        return newCategory;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }
}
