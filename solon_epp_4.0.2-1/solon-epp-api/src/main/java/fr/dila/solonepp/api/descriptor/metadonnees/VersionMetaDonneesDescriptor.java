package fr.dila.solonepp.api.descriptor.metadonnees;

import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
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
