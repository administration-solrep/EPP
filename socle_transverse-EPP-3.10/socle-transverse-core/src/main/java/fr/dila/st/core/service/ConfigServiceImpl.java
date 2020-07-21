package fr.dila.st.core.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nuxeo.ecm.core.event.EventServiceAdmin;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.descriptor.ConfigParameterDescriptor;

/**
 * Implémentation du service de paramétrage global de l'application.
 * 
 * @author jtremeaux
 */
public class ConfigServiceImpl extends DefaultComponent implements ConfigService {

	// private static final Log LOG = LogFactory.getLog(ConfigServiceImpl.class);

	private Map<String, String>	configParameterMap;

	/**
	 * Permet d'exclure les paramètres non renseignés dans nuxeo.conf.
	 */
	private final Pattern		excludePattern	= Pattern.compile("^\\$\\{.+\\}$");

	/**
	 * Default constructor
	 */
	public ConfigServiceImpl() {
		super();
	}

	@Override
	public void activate(ComponentContext context) throws Exception {
		super.activate(context);
		configParameterMap = new HashMap<String, String>();
	}

	@Override
	public void deactivate(ComponentContext context) throws Exception {
		super.deactivate(context);
		configParameterMap = null;
	}

	@Override
	public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
		ConfigParameterDescriptor desc = (ConfigParameterDescriptor) contribution;
		configParameterMap.put(desc.getName(), desc.getValue());
	}

	@Override
	public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
		ConfigParameterDescriptor desc = (ConfigParameterDescriptor) contribution;
		configParameterMap.remove(desc.getName());
	}

	@Override
	public String getValue(String parameterName) {
		// Récupère le paramètre à partir du fichier .conf
		String value = Framework.getProperty(parameterName);

		/*
		 * Si la variable n'est pas définie dans nuxeo.conf, le système de template de nuxeo passe une chaine du type
		 * ${la.variable} : dans ce cas on exclus le paramètre.
		 */
		if (value != null) {
			Matcher m = excludePattern.matcher(value);
			if (m.matches()) {
				value = null;
			}
		}

		if (value == null) {
			// Si le paramètre n'est pas défini, retourne la valeur par défaut
			value = configParameterMap.get(parameterName);
		}

		// Si aucune valeur n'est définie, c'est une erreur dans le code !
		if (value == null) {
			throw new RuntimeException("Paramètre de configuration [" + parameterName
					+ "] non défini, veuillez vérifier le fichier de configuration.");
		}
		return value;
	}

	@Override
	public Integer getIntegerValue(String parameterName) {
		String value = getValue(parameterName);
		return Integer.valueOf(value);
	}

	@Override
	public Boolean getBooleanValue(String parameterName) {
		String value = getValue(parameterName);
		return Boolean.valueOf(value);
	}

	@Override
	public Double getDoubleValue(String parameterName) {
		String value = getValue(parameterName);
		return Double.valueOf(value);
	}

	@Override
	public void applicationStarted(ComponentContext context) throws Exception {
		super.applicationStarted(context);

		// Activation du bulk mode
		boolean bulkMode = getBooleanValue(STConfigConstants.NUXEO_BULK_MODE);
		EventServiceAdmin eventServiceAdmin = STServiceLocator.getEventServiceAdmin();
		eventServiceAdmin.setBulkModeEnabled(bulkMode);
	}
}
