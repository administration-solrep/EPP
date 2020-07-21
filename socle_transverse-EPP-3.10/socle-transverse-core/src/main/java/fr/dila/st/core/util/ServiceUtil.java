package fr.dila.st.core.util;

import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentManager;
import org.nuxeo.runtime.model.ComponentName;

/**
 * Classe utilitaire permettant de récupérer des service Nuxeo, en encapsulant l'Exception dans une RuntimeException.
 * 
 * @author jtremeaux
 */
public final class ServiceUtil {

	/**
	 * utility class
	 */
	private ServiceUtil() {
		// do nothing
	}

	/**
	 * Retourne un service par sa classe.
	 * 
	 * @return Service
	 */
	public static <T, C> T getService(Class<T> c) {
		T service = null;
		try {
			service = Framework.getService(c);
		} catch (Exception e) {
			throw new RuntimeException("Service " + c.getName() + " not found.", e);
		}

		if (service == null) {
			throw new RuntimeException("Service " + c.getName() + " not found.");
		}

		return service;
	}

	/**
	 * Retourne un service local par sa classe.
	 * 
	 * @return Service
	 */
	public static <T, C> T getLocalService(Class<T> c) {
		T service = null;
		try {
			service = Framework.getLocalService(c);
		} catch (Exception e) {
			throw new RuntimeException("Local service " + c.getName() + " not found.", e);
		}

		if (service == null) {
			throw new RuntimeException("Local service " + c.getName() + " not found.");
		}

		return service;
	}

	/**
	 * Retourne une implémentation particulière d'un composant Nuxeo. Éviter d'utiliser cette méthode de façon générale,
	 * rechercher un service son interface lorsque c'est possible.
	 * 
	 * @param name
	 *            Nom du composant
	 * @return Instance d'un composant
	 */
	public static Object getComponentInstance(String name) {
		try {
			final ComponentName componentName = new ComponentName(name);
			final ComponentManager componentManager = Framework.getRuntime().getComponentManager();
			return componentManager.getComponent(componentName).getInstance();
		} catch (Exception e) {
			throw new RuntimeException("Component " + name + " not found.", e);
		}
	}
}
