package fr.dila.solonepp.core.validator;

import java.util.Calendar;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.PropertyDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.VersionMetaDonneesDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.solonepp.core.exception.EppClientException;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;

/**
 * Validateur des versions.
 * 
 * @author jtremeaux
 */
public class VersionValidator {
	/**
	 * Constructeur de VersionValidator.
	 */
	public VersionValidator() {
	}

	/**
	 * Valide les métadonnées obligatoires de la version.
	 * 
	 * @param versionDoc Document version à valider
	 * @param evenementDoc Document événement
	 * @throws ClientException
	 */
	@SuppressWarnings("rawtypes")
	public void validateMetaObligatoire(DocumentModel versionDoc, DocumentModel evenementDoc) throws ClientException {
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
					throw new EppClientException("La propriété " + schema + ":" + property + " est obligatoire");
				}
				if (value instanceof String && StringUtils.isBlank((String)value)) {
					throw new EppClientException("La propriété " + schema + ":" + property + " est obligatoire");
				}
				if (propertyDescriptor.isMultiValue()) {
					if (!((value instanceof Collection) || (value instanceof Object[]))) {
						throw new EppClientException("La propriété " + schema + ":" + property + " des données entrantes doit contenir une collection");
					}
					if ((value instanceof Collection && ((Collection) value).isEmpty()) 
						||(value instanceof Object[] && ((Object[])value).length == 0)) {
						throw new EppClientException("La collection " + schema + ":" + property + " doit contenir des éléments");
					}
				}
				
				if ("date".equals(propertyDescriptor.getType()) && "today".equals(propertyDescriptor.getMinDate())) {
					Calendar today = Calendar.getInstance();
					Calendar calendarValue = (Calendar) value;
					if (calendarValue.before(today)) {
						throw new EppClientException("La propriété " + schema + ":" + property + " doit être postérieure à la date du jour");
					}
				}
			}
		}
	}
}
