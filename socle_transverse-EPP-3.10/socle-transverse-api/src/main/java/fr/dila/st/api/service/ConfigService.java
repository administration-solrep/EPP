package fr.dila.st.api.service;

import org.nuxeo.runtime.model.Component;

/**
 * Service permettant d'obtenir les paramètrages globaux de l'application. Il s'agit des paramètres spécifiques à
 * l'application (Réponses / SOLON EPG / SOLON EPG). Les paramètres sont renseignés sous forme de couples (clé / valeur)
 * dans le fichier nuxeo.conf. De plus, un fichier default-config.xml contient les valeurs par défaut pour chaque
 * application.
 * 
 * @author jtremeaux
 */
public interface ConfigService extends Component {

	/**
	 * Retourne la valeur d'un paramètre de configuration.
	 * 
	 * @param parameterName
	 *            Nom d'un paramètre de configuration
	 * @return Valeur du paramètre
	 */
	String getValue(String parameterName);

	/**
	 * Retourne la valeur d'un paramètre de configuration. Convertit le paramètre en entier.
	 * 
	 * @param parameterName
	 *            Nom d'un paramètre de configuration
	 * @return Valeur du paramètre
	 */
	Integer getIntegerValue(String parameterName);

	/**
	 * Retourne la valeur d'un paramètre de configuration. Convertit le paramètre en booléen.
	 * 
	 * @param parameterName
	 *            Nom d'un paramètre de configuration
	 * @return Valeur du paramètre
	 */
	Boolean getBooleanValue(String parameterName);

	/**
	 * Retourne la valeur d'un paramètre de configuration. Convertit le paramètre en double.
	 * 
	 * @param parameterName
	 *            Nom d'un paramètre de configuration
	 * @return Valeur du paramètre
	 */
	Double getDoubleValue(String parameterName);
}
