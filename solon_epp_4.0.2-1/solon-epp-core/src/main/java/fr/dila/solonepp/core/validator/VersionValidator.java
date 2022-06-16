package fr.dila.solonepp.core.validator;

import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.VersionMetaDonneesDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.solonepp.core.exception.EppNuxeoException;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import java.util.Calendar;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Validateur des versions.
 *
 * @author jtremeaux
 */
public class VersionValidator {
    private static final String PROP_OBLIGATOIRE = "La propriété %s:%s est obligatoire";

    /**
     * Constructeur de VersionValidator.
     */
    public VersionValidator() {}

    /**
     * Valide les métadonnées obligatoires de la version.
     *
     * @param versionDoc Document version à valider
     * @param evenementDoc Document événement
     */
    @SuppressWarnings("rawtypes")
    public void validateMetaObligatoire(DocumentModel versionDoc, DocumentModel evenementDoc) {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);

        // Assemble les données de la version
        final MetaDonneesService metaDonneesService = SolonEppServiceLocator.getMetaDonneesService();
        MetaDonneesDescriptor metaDonneesDescriptor = metaDonneesService.getEvenementType(evenement.getTypeEvenement());
        VersionMetaDonneesDescriptor versionMetaDonneesDescriptor = metaDonneesDescriptor.getVersion();
        for (PropertyDescriptor propertyDescriptor : versionMetaDonneesDescriptor.getProperty().values()) {
            // Les attributs renseignés par l'EPP ne sont jamais validés
            if (propertyDescriptor.isRenseignerEpp()) {
                continue;
            }

            // Vérifie les attributs obligatoires
            if (propertyDescriptor.isObligatoire()) {
                // Détermine le schéma et la propriété
                final String schema = propertyDescriptor.getSchema();
                final String property = propertyDescriptor.getName();
                Object value = versionDoc.getProperty(schema, property);
                if (value == null) {
                    String prop = "dateList".equals(property) ? "Date" : property;
                    throw new EppNuxeoException(String.format(PROP_OBLIGATOIRE, schema, prop));
                }
                if (value instanceof String && StringUtils.isBlank((String) value)) {
                    throw new EppNuxeoException(String.format(PROP_OBLIGATOIRE, schema, property));
                }
                if (propertyDescriptor.isMultiValue()) {
                    if (!((value instanceof Collection) || (value instanceof Object[]))) {
                        throw new EppNuxeoException(
                            "La propriété " +
                            schema +
                            ":" +
                            property +
                            " des données entrantes doit contenir une collection"
                        );
                    }
                    if (
                        (value instanceof Collection && ((Collection) value).isEmpty()) ||
                        (value instanceof Object[] && ((Object[]) value).length == 0)
                    ) {
                        throw new EppNuxeoException(
                            "La collection " + schema + ":" + property + " doit contenir des éléments"
                        );
                    }
                }

                if ("date".equals(propertyDescriptor.getType()) && "today".equals(propertyDescriptor.getMinDate())) {
                    Calendar today = Calendar.getInstance();
                    Calendar calendarValue = (Calendar) value;
                    if (calendarValue.before(today)) {
                        throw new EppNuxeoException(
                            "La propriété " + schema + ":" + property + " doit être postérieure à la date du jour"
                        );
                    }
                }
            }
        }
    }
}
