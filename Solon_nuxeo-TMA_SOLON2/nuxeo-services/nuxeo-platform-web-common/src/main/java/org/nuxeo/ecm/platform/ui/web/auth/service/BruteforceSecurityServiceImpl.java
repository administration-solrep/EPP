package org.nuxeo.ecm.platform.ui.web.auth.service;

import javax.persistence.EntityManager;

import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunCallback;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunVoid;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.ecm.platform.ui.web.auth.BruteforceUserInfo;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.runtime.transaction.TransactionHelper;

public class BruteforceSecurityServiceImpl extends DefaultComponent implements BruteforceSecurityService {

	private PersistenceProvider provider = null;

	public static final String ENTITY = "BruteforceUserInfo";
	// private static final String SELECT_INFO = String.format("SELECT %s FROM %s
	// WHERE username = :login", ENTITY, ENTITY);

	@Override
	public BruteforceUserInfo getLoginInfos(final String username) throws Exception {
		return provider.run(true, new RunCallback<BruteforceUserInfo>() {
			@Override
			public BruteforceUserInfo runWith(EntityManager em) {
				return em.find(BruteforceUserInfo.class, username);
			};
		});
	}

	@Override
	public void newAttempt(final String username) throws Exception {
		boolean transactionCreated = false;
		try {
			if (!TransactionHelper.isTransactionActive()) {
				transactionCreated = TransactionHelper.startTransaction();
			}
			provider.run(true, new RunVoid() {
				@Override
				public void runWith(EntityManager em) {
					BruteforceUserInfo currUser = em.find(BruteforceUserInfo.class, username);
					// null -> la ligne n'existe pas
					if (currUser == null) {
						currUser = new BruteforceUserInfo();
						currUser.setUsername(username);
						em.persist(currUser);
					}
					currUser.newAttempt();
				};
			});
		} catch (RuntimeException e) {
			TransactionHelper.setTransactionRollbackOnly();
		} finally {
			if (transactionCreated) {
				TransactionHelper.commitOrRollbackTransaction();
			}
		}
	}

	@Override
	public void deleteLoginInfos(final String username) throws Exception {
		boolean transactionCreated = false;
		try {
			if (!TransactionHelper.isTransactionActive()) {
				transactionCreated = TransactionHelper.startTransaction();
			}
			provider.run(true, new RunCallback<BruteforceUserInfo>() {
				@Override
				public BruteforceUserInfo runWith(EntityManager em) {
					BruteforceUserInfo currUser = em.find(BruteforceUserInfo.class, username);
					// null -> la ligne n'existe pas
					if (currUser != null) {
						em.remove(currUser);
					}
					return currUser;
				};
			});
		} catch (RuntimeException e) {
			TransactionHelper.setTransactionRollbackOnly();
		} finally {
			if (transactionCreated) {
				TransactionHelper.commitOrRollbackTransaction();
			}
		}
	}

	@Override
	public void setUserInfos(BruteforceUserInfo userInfos) throws Exception {
		// TODO Auto-generated method stub
	}
	
	protected PersistenceProvider getOrCreatePersistenceProvider() {
		if (provider == null) {
			synchronized (BruteforceSecurityServiceImpl.class) {
				if (provider == null) {
					activatePersistenceProvider();
				}
			}
		}
		return provider;
	}

	protected void activatePersistenceProvider() {
		Thread thread = Thread.currentThread();
		ClassLoader last = thread.getContextClassLoader();
		try {
			thread.setContextClassLoader(PersistenceProvider.class.getClassLoader());
			PersistenceProviderFactory persistenceProviderFactory = Framework
					.getLocalService(PersistenceProviderFactory.class);
			provider = persistenceProviderFactory.newProvider("bruteforce-infos");
			provider.openPersistenceUnit();
		} finally {
			thread.setContextClassLoader(last);
		}
	}

	@Override
	public void deactivate(ComponentContext context) throws Exception {
		deactivatePersistenceProvider();
	}
	
	@Override
	public void activate(ComponentContext context) throws Exception {
		provider = getOrCreatePersistenceProvider();
	}
	
	private void deactivatePersistenceProvider() {
		if (provider != null) {
			synchronized (BruteforceSecurityServiceImpl.class) {
				if (provider != null) {
					provider.closePersistenceUnit();
					provider = null;
				}
			}
		}
	}

}
