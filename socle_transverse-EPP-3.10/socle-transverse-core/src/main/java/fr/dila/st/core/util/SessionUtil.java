package fr.dila.st.core.util;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;

import fr.dila.st.core.service.STServiceLocator;

/**
 * Classe utilitaire sur les sessions Nuxeo.
 * 
 * @author jtremeaux
 */
public final class SessionUtil {

	/**
	 * utility class
	 */
	private SessionUtil() {
		// do nothing
	}

	/**
	 * Ouvre une nouvelle session sur le repository et retourne la session. /!\ Attention /!\ La session doit
	 * impérativement être fermée dans un bloc finally !
	 * 
	 * @return Nouvelle session
	 * @throws ClientException
	 */
	public static CoreSession getCoreSession() throws ClientException {
		RepositoryManager repositoryManager = STServiceLocator.getRepositoryManager();
		Repository repo = repositoryManager.getDefaultRepository();

		try {
			return repo.open();
		} catch (Exception e) {
			throw new ClientException("Cannot open CoreSession", e);
		}
	}

	/**
	 * Ferme la session ouverte avec la méthode getCoreSession ci-dessus.
	 * 
	 * @param session
	 *            Session
	 */
	public static void close(CoreSession session) {
		if (session != null) {
			Repository.close(session);
		}
	}
}
