package fr.dila.solonepp.api.service;

import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer les metadonnées .
 *
 * @author asatre
 */
public interface MetaDonneesService extends Serializable {
    /**
     * Retourne les metadonnees correspondant au type d'evenement.
     *
     * @param evenementType Type d'événement
     * @return Description du type d'événement
     */
    MetaDonneesDescriptor getEvenementType(final String evenementType);

    /**
     *
     * @param evenementType
     * @return la map des {@link PropertyDescriptor} correspondant au type d'evenement
     */
    Map<String, PropertyDescriptor> getMapProperty(final String evenementType);

    /**
     * retourne la liste de tous les descriptors
     * @return
     */
    List<MetaDonneesDescriptor> getAll();

    /**
     * Remap les metadonnes avec des valeurs par defaut d'un document a un autre
     *
     * @param docPrecedent
     * @param docSuivant
     * @param mapProperty
     * @return
     */
    DocumentModel remapDefaultMetaDonnees(
        DocumentModel docPrecedent,
        DocumentModel docSuivant,
        DocumentModel doc,
        Map<String, PropertyDescriptor> mapProperty
    );

    /**
     * Remap les metadonnees avec des valeurs conditionnelles
     * @param eventDoc
     * @param versionDoc
     * @param mapProperty
     */
    void remapConditionnelMetaDonnees(
        DocumentModel eventDoc,
        DocumentModel versionDoc,
        Map<String, PropertyDescriptor> mapProperty
    );
}
