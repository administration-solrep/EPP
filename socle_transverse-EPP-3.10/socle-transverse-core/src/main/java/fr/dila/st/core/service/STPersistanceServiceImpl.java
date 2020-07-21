package fr.dila.st.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunVoid;
import org.nuxeo.ecm.directory.sql.PasswordHelper;
import org.nuxeo.runtime.api.Framework;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STPersistanceService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.user.STHistoriqueMDP;

/**
 * Implémentation du service de persistence de données. Utilisation de persistence provider et entitymanager
 * 
 */
public class STPersistanceServiceImpl implements STPersistanceService {

	/**
	 * Serial Version UID
	 */
	private static final long					serialVersionUID		= -4989014118812082817L;

	private static final int					TAILLE_HISTORIQUE_MDP	= 2;

	private static final String					USERS_PROVIDER			= "sword-provider";

	private static final STLogger				LOGGER					= STLogFactory
																				.getLog(STPersistanceServiceImpl.class);

	private static volatile PersistenceProvider	persistenceProvider;

	@Override
	public void saveCurrentPassword(final String currentPassword, final String currentUser) throws ClientException {
		getOrCreatePersistenceProvider(USERS_PROVIDER).run(true, new RunVoid() {
			public void runWith(EntityManager entityManager) throws ClientException {
				savePw(entityManager, currentPassword, currentUser);
			}
		});
	}

	@Override
	public boolean checkPasswordHistory(final String currentPassword, final String currentUser) throws ClientException {
		final List<STHistoriqueMDP> historiqueMDP = new ArrayList<STHistoriqueMDP>();
		getOrCreatePersistenceProvider(USERS_PROVIDER).run(true, new RunVoid() {
			public void runWith(EntityManager entityManager) throws ClientException {
				historiqueMDP.addAll(getHistoriqueForUser(entityManager, currentUser));
			}
		});

		if (historiqueMDP != null && !historiqueMDP.isEmpty()) {
			for (STHistoriqueMDP stHistoriqueMDP : historiqueMDP) {
				if (PasswordHelper.verifyPassword(currentPassword, stHistoriqueMDP.getDernierMDP())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Sauvegarde le mot de passe dans la base. Si la taille d'historique max n'a pas été atteinte, on ajoute le mot de
	 * passe, sinon, on remplace le plus ancien
	 * 
	 * @param entityManager
	 * @param currentPassword
	 * @param currentUser
	 */
	private void savePw(EntityManager entityManager, String currentPassword, String currentUser) {
		List<STHistoriqueMDP> historiqueMDP = getHistoriqueForUser(entityManager, currentUser);

		if (historiqueMDP == null || historiqueMDP.isEmpty() || historiqueMDP.size() < TAILLE_HISTORIQUE_MDP) {
			entityManager.persist(new STHistoriqueMDP(currentUser, currentPassword, Calendar.getInstance().getTime()));
		} else {
			entityManager.persist(updateHistorique(historiqueMDP, currentPassword));
		}
		entityManager.flush();
	}

	/**
	 * Tri la liste d'historique par ordre chronologique, met à jour et retourne le premier élément
	 * 
	 * @param historiqueMDP
	 * @param newPassword
	 * @return
	 */
	private STHistoriqueMDP updateHistorique(List<STHistoriqueMDP> historiqueMDP, String newPassword) {
		Collections.sort(historiqueMDP, new DateHistoriqueComparator());
		STHistoriqueMDP historiqueToUpdate = historiqueMDP.get(0);
		historiqueToUpdate.setDernierMDP(newPassword);
		historiqueToUpdate.setDateEnregistrement(Calendar.getInstance().getTime());
		return historiqueToUpdate;
	}

	/**
	 * Must be called when the service is no longer needed
	 */
	public static void dispose() {
		deactivatePersistenceProvider();
	}

	public static PersistenceProvider getOrCreatePersistenceProvider(String providerName) {
		if (persistenceProvider == null) {
			synchronized (STPersistanceServiceImpl.class) {
				if (persistenceProvider == null) {
					activatePersistenceProvider(providerName);
				}
			}
		}
		return persistenceProvider;
	}

	private static void activatePersistenceProvider(String providerName) {
		Thread thread = Thread.currentThread();
		ClassLoader last = thread.getContextClassLoader();
		try {
			thread.setContextClassLoader(PersistenceProvider.class.getClassLoader());
			PersistenceProviderFactory persistenceProviderFactory = Framework
					.getLocalService(PersistenceProviderFactory.class);
			persistenceProvider = persistenceProviderFactory.newProvider(providerName);
			persistenceProvider.openPersistenceUnit();
		} finally {
			thread.setContextClassLoader(last);
		}
	}

	private static void deactivatePersistenceProvider() {
		if (persistenceProvider != null) {
			synchronized (STPersistanceServiceImpl.class) {
				if (persistenceProvider != null) {
					persistenceProvider.closePersistenceUnit();
					persistenceProvider = null;
				}
			}
		}
	}

	/**
	 * Récupère l'historique des mots de passe pour un utilisateur
	 * 
	 * @param entityManager
	 * @param user
	 * @return List historique des mots de passe, liste vide si aucun résultat
	 */
	@SuppressWarnings("unchecked")
	private List<STHistoriqueMDP> getHistoriqueForUser(final EntityManager entityManager, final String user) {
		final Query nativeQuery = entityManager
				.createQuery("SELECT h FROM  STHistoriqueMDP as h  WHERE  h.login = :currentUser");
		nativeQuery.setParameter("currentUser", user);
		List<STHistoriqueMDP> historiqueMDP = null;
		try {
			historiqueMDP = nativeQuery.getResultList();
		} catch (NoResultException exc) {
			historiqueMDP = null;
			LOGGER.debug(null, STLogEnumImpl.FAIL_EXEC_SQL, exc);
			LOGGER.warn(null, STLogEnumImpl.FAIL_EXEC_SQL, user);
		}
		if (historiqueMDP == null) {
			historiqueMDP = new ArrayList<STHistoriqueMDP>();
		}
		return historiqueMDP;
	}

	/**
	 * Comparateur pour les dates d'enregistrement des mots de passe
	 * 
	 */
	private static class DateHistoriqueComparator implements Comparator<STHistoriqueMDP> {
		@Override
		public int compare(STHistoriqueMDP histo1, STHistoriqueMDP histo2) {
			return histo1.getDateEnregistrement().compareTo(histo2.getDateEnregistrement());
		}
	}

}
