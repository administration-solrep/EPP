package org.nuxeo.ecm.platform.ui.web.auth.service;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.ecm.platform.ui.web.auth.BruteforceUserInfo;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.runtime.transaction.TransactionHelper;

public class BruteforceSecurityServiceImpl extends DefaultComponent implements BruteforceSecurityService {

	private PersistenceProvider provider = null;

	@Override
	public BruteforceUserInfo getLoginInfos(final String username) {
		return getOrCreatePersistenceProvider().run(true, em -> {
			return em.find(BruteforceUserInfo.class, username);
		});
	}
	@Override
	public void newAttempt(final String username) {
		boolean transactionCreated = false;
		try {
			if (!TransactionHelper.isTransactionActive()) {
				transactionCreated = TransactionHelper.startTransaction();
			}
			getOrCreatePersistenceProvider().run(true, em -> {
				Optional.ofNullable(em.find(BruteforceUserInfo.class, username))
						.map(BruteforceSecurityServiceImpl::addNewAttemptToUserInfo)
						.orElseGet(() -> createUserInfo(em, username));
			});
		} catch (RuntimeException e) {
			TransactionHelper.setTransactionRollbackOnly();
		} finally {
			if (transactionCreated) {
				TransactionHelper.commitOrRollbackTransaction();
			}
		}
	}

	private static BruteforceUserInfo addNewAttemptToUserInfo(BruteforceUserInfo userInfo) {
		userInfo.newAttempt();
		return userInfo;
	}

	private static BruteforceUserInfo createUserInfo(EntityManager em, String username) {
		BruteforceUserInfo userInfo = new BruteforceUserInfo();
		userInfo.setUsername(username);
		userInfo.newAttempt();
		em.persist(userInfo);
		return userInfo;
	}

	@Override
	public void deleteLoginInfos(final String username) {
		boolean transactionCreated = false;
		try {
			if (!TransactionHelper.isTransactionActive()) {
				transactionCreated = TransactionHelper.startTransaction();
			}
			getOrCreatePersistenceProvider().run(true, em -> {
				Optional.ofNullable(em.find(BruteforceUserInfo.class, username))
						.ifPresent(em::remove);
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
	public void setUserInfos(BruteforceUserInfo userInfos) {
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
					.getService(PersistenceProviderFactory.class);
			provider = persistenceProviderFactory.newProvider("bruteforce-infos");
			provider.openPersistenceUnit();
		} finally {
			thread.setContextClassLoader(last);
		}
	}
	
	@Override
	public void deactivate(ComponentContext context) {
		deactivatePersistenceProvider();
	}
	
	@Override
	public void activate(ComponentContext context) {
//		provider = getOrCreatePersistenceProvider();
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
