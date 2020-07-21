package fr.dila.solonepp.core.descriptor.metadonnees;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

import fr.dila.solonepp.api.descriptor.metadonnees.PropertyDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.VersionMetaDonneesDescriptor;

/**
 * Descripteur des métadonnées d'une version.
 * 
 * @author asatre
 */
@XObject("versionMetadonnees")
public class VersionMetaDonneesDescriptorImpl implements VersionMetaDonneesDescriptor {

    
    /**
     * Propriétés qui peuvent / doivent être fournies pour ce type de version.
     */
    @XNodeMap(value = "property", key = "@name", type = HashMap.class, componentType = PropertyDescriptorImpl.class)
    private Map<String, PropertyDescriptor> property;

    /**
     * Constructeur par défaut de VersionSchemaDescriptor.
     */
    public VersionMetaDonneesDescriptorImpl() {

    }

	@Override
	public void setProperty(Map<String, PropertyDescriptor> property) {
		this.property = property;
	}

	@Override
	public Map<String, PropertyDescriptor> getProperty() {
		return property;
	}

}
