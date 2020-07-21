package fr.dila.solonepp.api.descriptor.metadonnees;

import java.util.Map;

/**
 * Interface de description des metadonnees d'une version
 * @author asatre
 *
 */
public interface VersionMetaDonneesDescriptor {
	
	void setProperty(Map<String, PropertyDescriptor> property);

	Map<String, PropertyDescriptor> getProperty();

}
