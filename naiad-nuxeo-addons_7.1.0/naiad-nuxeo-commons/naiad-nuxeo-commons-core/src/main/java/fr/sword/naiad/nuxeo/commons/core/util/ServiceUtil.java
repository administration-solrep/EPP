package fr.sword.naiad.nuxeo.commons.core.util;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.api.Framework;

/**
 * Class utilitaire : manipulation des services
 * 
 * @author SPL
 *
 */
public final class ServiceUtil {

	/**
	 * Classe utilitaire
	 */
	private ServiceUtil(){
		// do nothing
	}
	
	/**
	 * Permet de recuperer un service
	 * @param clazz l'interface requise
	 * @return Un objet proposant le service associé à l'interface recherché, null si non trouvé
	 * @throws NuxeoException lancé si une exception est renvoyé par Framework.getService
	 */
	public static <T> T getService(Class<T> clazz) throws NuxeoException {
		try {
			return Framework.getService(clazz);
		} catch(Exception e){
			throw new NuxeoException("Service not found ["+clazz.toString()+"]", e);
		}
	}
	
	/**
	 * Permet de récupérer un service (à la différence de getService , une exception est levée si aucune implementation
	 * du service n'est trouvée
	 * @param clazz l'interface requise
	 * @return Un objet proposant le service associé à l'interface recherché, une exception est levée si non trouvé
	 * @throws NuxeoException lancé si une exception est renvoyé par Framework.getService ou si aucune implementation
	 * du service n'est trouvée
	 */
	public static <T> T getRequiredService(Class<T> clazz) throws NuxeoException {
		final T service = getService(clazz);
		if(service == null){
			throw new NuxeoException("No implementation for " + clazz);
		}
		return service;
	}
	
}
