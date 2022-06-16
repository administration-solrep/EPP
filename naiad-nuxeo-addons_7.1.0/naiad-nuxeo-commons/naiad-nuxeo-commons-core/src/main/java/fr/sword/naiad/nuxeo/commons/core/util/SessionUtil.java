package fr.sword.naiad.nuxeo.commons.core.util;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;

/**
 * Classe utilitaire permettant la manipulation des sessions Nuxeo.
 * 
 * @author Florent Munch
 */
public final class SessionUtil {
	/**
	 * Constructeur privé.
	 */
	private SessionUtil() {
	}

	/**
	 * Ouvre une nouvelle session Nuxeo.
	 * 
	 * @return Session Nuxeo.
	 * @throws NuxeoException
	 *             Si le service RepositoryManager ne peut pas être récupéré ou
	 *             si le repository par défaut ne peut pas être ouvert.
	 */
	public static CloseableCoreSession openSession() throws NuxeoException {
		try {
			RepositoryManager mgr = ServiceUtil
					.getService(RepositoryManager.class);
			Repository repository = mgr.getDefaultRepository();
			return CoreInstance.openCoreSession(repository.getName());
		} catch (NuxeoException e) {
			throw e;
		} catch (Exception e) {
			throw new NuxeoException(
					"Unable to open repository to get core session", e);
		}
	}

	/**
	 * Ferme une session Nuxeo.
	 * 
	 * @param session
	 *            Session Nuxeo.
	 */
	public static void closeSession(CoreSession session) {
		if (session != null) {
			session.close();
		}
	}
}
