package fr.dila.solonepp.core.descriptor.metadonnees;

import fr.dila.solonepp.api.descriptor.metadonnees.EvenementMetaDonneesDescriptor;
import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import fr.dila.st.core.descriptor.parlement.PropertyDescriptorImpl;
import java.util.LinkedHashMap;
import java.util.Map;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur des metadonnées d'un evenement.
 *
 * @author asatre
 */
@XObject("evenementMetadonnees")
public class EvenementMetaDonneesDescriptorImpl implements EvenementMetaDonneesDescriptor {
    /**
     * Propriétés qui peuvent / doivent être fournies pour ce type d'evenement.
     */
    @XNodeMap(
        value = "property",
        key = "@name",
        type = LinkedHashMap.class,
        componentType = PropertyDescriptorImpl.class
    )
    private Map<String, PropertyDescriptor> property;

    /**
     * Constructeur par défaut de VersionSchemaDescriptor.
     */
    public EvenementMetaDonneesDescriptorImpl() {}

    @Override
    public void setProperty(Map<String, PropertyDescriptor> property) {
        this.property = property;
    }

    @Override
    public Map<String, PropertyDescriptor> getProperty() {
        return property;
    }
}
