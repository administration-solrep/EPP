package fr.dila.st.web.requeteur;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.convert.Converter;

import org.jboss.seam.annotations.Name;
import org.nuxeo.ecm.webapp.security.UserDisplayConverter;

import fr.dila.st.api.constant.STRequeteurExpertConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.requeteur.RequeteurWidgetDescription;
import fr.dila.st.api.service.STRequeteurWidgetGeneratorService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.converter.DateClauseConverter;
import fr.dila.st.web.converter.OrganigrammeMultiIdToLabelConverter;
import fr.dila.st.web.converter.VocabularyIdsConverter;

/**
 * Bean seam de conversion pour les requêteurs experts
 * 
 * @author jgomez
 * 
 */
@Name("stRequeteurConverterActions")
public class STRequeteurConverterActions implements Serializable {

	private static final long		serialVersionUID	= 15568L;

	private static final STLogger	LOGGER				= STLogFactory.getLog(STRequeteurConverterActions.class);

	/**
	 * Retourne le converter approprié à partir du searchField
	 * 
	 * @param searchField
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	public Converter getConverter(String searchField, String requeteurExpertId) {
		Map<String, Class<? extends Converter>> typeToConverterClassMap = getTypeToConverterClassMap();
		final STRequeteurWidgetGeneratorService generateurService = STServiceLocator
				.getSTRequeteurWidgetGeneratorService();
		RequeteurWidgetDescription description = generateurService.getRequeteurContribution(requeteurExpertId)
				.getWidgetDescriptionBySearchField(searchField);
		if (description == null) {
			LOGGER.error(null, STLogEnumImpl.FAIL_GET_PROPERTY_TEC,
					"Le requeteur expert a rencontré une erreur, on veut traduire le champ " + searchField
							+ ". Ce champ n'existe pas dans la liste des widgets");
			return null;
		}

		String type = description.getType();
		Map<String, String> extraProperties = description.getExtraProperties();
		// On regarde s'il est explicitement dit dans le widget qu'un convertisseur doit être utilisé
		if (extraProperties != null
				&& extraProperties.containsKey(STRequeteurExpertConstants.REQUETEUR_PROPERTIES_CONVERTER)) {
			if (Boolean.TRUE.toString().equalsIgnoreCase(
					extraProperties.get(STRequeteurExpertConstants.REQUETEUR_PROPERTIES_CONVERTER))) {
				String converterClass = extraProperties
						.get(STRequeteurExpertConstants.REQUETEUR_PROPERTIES_CONVERTER_CLASS);
				if (converterClass == null) {
					LOGGER.error(null, STLogEnumImpl.FAIL_GET_CONVERTER_TEC, "La classe n'a pas été renseignée");
				} else {
					try {
						Class<?> klass = Class.forName(converterClass);
						if (Converter.class.isAssignableFrom(klass)) {
							typeToConverterClassMap.put(type, (Class<? extends Converter>) klass);
						}
					} catch (ClassNotFoundException e) {
						LOGGER.error(null, STLogEnumImpl.FAIL_GET_CONVERTER_TEC, "La classe n'a pas été trouvée");
					}
				}
			}
		}

		if (!typeToConverterClassMap.containsKey(type)) {
			return null;
		}

		Class<? extends Converter> classConverter = typeToConverterClassMap.get(type);
		Converter converter;
		try {
			converter = classConverter.newInstance();
		} catch (Exception e) {
			LOGGER.error(null, STLogEnumImpl.FAIL_GET_CONVERTER_TEC,
					"La conversion des labels pour le requêteur expert a echoué pour la classe " + classConverter
							+ ". Verifiez le converter");
			// On retourne sans message d'erreur, et sans convertir.
			return null;
		}

		// Propriétés du converter de vocabulaire
		if (converter instanceof VocabularyIdsConverter) {
			((VocabularyIdsConverter) converter).setDirectoryName(description.getDirectoryName());
			((VocabularyIdsConverter) converter).setHasToConvertLabel(description.getHasToConvertLabel());
		}
		return converter;
	}

	/**
	 * Retourne une map <type, converter>
	 * 
	 * @return une map qui représente une équivalence entre un type de widget et un converter de valeur.
	 */
	protected Map<String, Class<? extends Converter>> getTypeToConverterClassMap() {
		Map<String, Class<? extends Converter>> typeToConverterClassMap = new HashMap<String, Class<? extends Converter>>();
		typeToConverterClassMap.put(STRequeteurExpertConstants.REQUETEUR_TYPE_ORGANIGRAMME,
				OrganigrammeMultiIdToLabelConverter.class);
		typeToConverterClassMap.put(STRequeteurExpertConstants.REQUETEUR_TYPE_POSTEORGANIGRAMME,
				OrganigrammeMultiIdToLabelConverter.class);
		typeToConverterClassMap.put(STRequeteurExpertConstants.REQUETEUR_TYPE_DATE, DateClauseConverter.class);
		typeToConverterClassMap.put(STRequeteurExpertConstants.REQUETEUR_TYPE_DIRECTORY, VocabularyIdsConverter.class);
		typeToConverterClassMap.put(STRequeteurExpertConstants.REQUETEUR_TYPE_ETAPE, VocabularyIdsConverter.class);
		typeToConverterClassMap.put(STRequeteurExpertConstants.REQUETEUR_TYPE_USER, UserDisplayConverter.class);
		return typeToConverterClassMap;
	}
}
