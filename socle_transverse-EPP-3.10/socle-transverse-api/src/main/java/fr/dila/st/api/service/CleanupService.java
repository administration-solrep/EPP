package fr.dila.st.api.service;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Regroupe des opérations de nettoyage sur des données inutilisé
 * 
 * @author spesnel
 * 
 */
public interface CleanupService {

	/**
	 * Supprime des document d'un type donné qui sont dans l'état deleted jusqu'à ce qu'il n'y en ait plus ou pendant un
	 * maximum de 'nbSec' secondes
	 * 
	 * @param session
	 * @param documentType
	 *            type de document à traiter
	 * @param nbSec
	 *            nombre de secondes pendant lequel faire le nettoyage. 0 pour un temps illimité.
	 * @return the number of removed document
	 */
	int removeDeletedDocument(CoreSession session, String documentType, long nbSec) throws ClientException;

}
