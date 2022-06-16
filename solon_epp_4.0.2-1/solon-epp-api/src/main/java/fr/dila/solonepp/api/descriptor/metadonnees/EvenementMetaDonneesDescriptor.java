package fr.dila.solonepp.api.descriptor.metadonnees;

import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import java.util.Map;

/**
 * Interface de description des metadonnees d'un evenement
 * @author asatre
 *
 */
public interface EvenementMetaDonneesDescriptor {
    void setProperty(Map<String, PropertyDescriptor> property);

    Map<String, PropertyDescriptor> getProperty();
}
