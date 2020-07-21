package fr.dila.st.core.service;

import static org.nuxeo.ecm.core.api.security.SecurityConstants.EVERYTHING;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.webapp.edit.lock.LockActions;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;

/**
 * Implémentation du service de verrous du socle transverse.
 * 
 * @author jtremeaux
 */
public class STLockServiceImpl implements STLockService {
	/**
	 * Serializable.
	 */
	private static final long	serialVersionUID			= -7476782975605260224L;

	private static final String	QUERY_GET_ALL_LOCKED_DOCS	= "SELECT LOCKS.ID as id FROM LOCKS WHERE LOCKS.OWNER = ?";

	public STLockServiceImpl() {
		super();
	}

	@Override
	public boolean unlockDoc(DocumentModel document, CoreSession documentManager, LockActions lockActions)
			throws ClientException {
		Map<String, Serializable> lockDetails = lockActions.getLockDetails(document);
		if (lockDetails == null) {
			return true;
		}

		NuxeoPrincipal userName = (NuxeoPrincipal) documentManager.getPrincipal();
		if (!(userName.isAdministrator()
				|| documentManager.hasPermission(document.getRef(), SecurityConstants.EVERYTHING) || userName.getName()
				.equals(lockDetails.get(LockActions.LOCKER)))) {
			return false;
		}

		if (documentManager.hasPermission(document.getRef(), SecurityConstants.WRITE_PROPERTIES)) {
			documentManager.removeLock(document.getRef());
			documentManager.save();
			removeAllLock(document, documentManager);
			return true;
		} else {
			try {
				// Here administrator should always be able to unlock so
				// we need to grant him this possibility even if it
				// doesn't have the write permission.

				new UnrestrictedUnlocker(document.getRef(), documentManager).runUnrestricted();
				documentManager.save(); // process invalidations from unrestricted session

				return true;

			} catch (Exception e) {
				throw new ClientException(e.getMessage(), e);
			}
		}
	}

	@Override
	public boolean unlockDoc(CoreSession session, DocumentModel document) throws ClientException {
		Map<String, String> lockDetails = getLockDetails(session, document);

		NuxeoPrincipal userName = (NuxeoPrincipal) session.getPrincipal();
		if (!(userName.isAdministrator() || session.hasPermission(document.getRef(), SecurityConstants.EVERYTHING) || userName
				.getName().equals(lockDetails.get(LockActions.LOCKER)))) {
			return false;
		}

		if (session.hasPermission(document.getRef(), SecurityConstants.WRITE_PROPERTIES)) {
			session.removeLock(document.getRef());
			session.save();
			removeAllLock(document, session);

			return true;
		} else {
			try {
				// Here administrator should always be able to unlock so
				// we need to grant him this possibility even if it
				// doesn't have the write permission.

				new UnrestrictedUnlocker(document.getRef(), session).runUnrestricted();
				session.save(); // process invalidations from unrestricted session

				return true;

			} catch (Exception e) {
				throw new ClientException(e.getMessage(), e);
			}
		}
	}

	@Override
	public boolean unlockDocUnrestricted(CoreSession session, DocumentModel document) throws ClientException {
		if (session.hasPermission(document.getRef(), SecurityConstants.WRITE_PROPERTIES)) {
			// Déverrouille du document
			session.removeLock(document.getRef());
			removeAllLock(document, session);
			session.save();
			return true;
		} else {
			// Si l'utilisateur n'a pas les droits de déverrouillage, on lui donne quand même
			try {
				new UnrestrictedUnlocker(document.getRef(), session).runUnrestricted();
				session.save(); // process invalidations from unrestricted session

				return true;

			} catch (Exception e) {
				throw new ClientException(e.getMessage(), e);
			}
		}
	}

	@Override
	public boolean lockDoc(CoreSession documentManager, DocumentModel document) throws ClientException {
		DocumentRef ref = document.getRef();

		if (!(documentManager.hasPermission(ref, SecurityConstants.WRITE_PROPERTIES) && documentManager
				.getLockInfo(ref) == null)) {
			return false;
		}

		documentManager.setLock(ref);
		lockAll(document, documentManager);
		documentManager.save();

		return true;
	}

	@Override
	public boolean isLockActionnableByUser(CoreSession session, DocumentModel document, NuxeoPrincipal user)
			throws ClientException {
		Map<String, String> lockDetails = getLockDetails(session, document);

		if (lockDetails == null || lockDetails.isEmpty() || user.isAdministrator()
				|| user.getName().equals(lockDetails.get(LockActions.LOCKER))) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isLockActionnableByUser(CoreSession session, DocumentModel document, NuxeoPrincipal user,
			LockActions lockActions) throws ClientException {
		Map<String, Serializable> lockDetails = lockActions.getLockDetails(document);
		if (lockDetails == null || lockDetails.isEmpty() || user.isAdministrator()
				|| session.hasPermission(document.getRef(), EVERYTHING)
				|| user.getName().equals(lockDetails.get(LockActions.LOCKER))) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isLockByUser(CoreSession session, DocumentModel document, NuxeoPrincipal user)
			throws ClientException {
		Map<String, String> lockDetails = getLockDetails(session, document);
		if (lockDetails != null && !lockDetails.isEmpty() && user.getName().equals(lockDetails.get(LockActions.LOCKER))) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isLockByCurrentUser(CoreSession session, DocumentModel document) throws ClientException {
		NuxeoPrincipal userName = (NuxeoPrincipal) session.getPrincipal();

		return isLockByUser(session, document, userName);
	}

	@Override
	public Map<String, String> getLockDetails(CoreSession session, DocumentModel document) throws ClientException {
		Map<String, String> lockDetails = new HashMap<String, String>();
		Lock documentKey = session.getLockInfo(document.getRef());
		if (documentKey == null) {
			return lockDetails;
		}

		lockDetails.put(LOCKER, documentKey.getOwner());
		Date creationDate = documentKey.getCreated().getTime();
		lockDetails.put(LOCK_TIME, DateUtil.formatForClient(creationDate));

		return lockDetails;
	}

	/**
	 * Cherche dans la base pour les ids des documents donnés en parametre ceux qui sont verrouillé
	 * 
	 * @param session
	 * @param docIds
	 * @return ensemble d'id verrouillé
	 * @throws ClientException
	 */
	@Override
	public Set<String> extractLocked(CoreSession session, Collection<String> docIds) throws ClientException {

		Set<String> lockDocIds = new HashSet<String>();

		if (docIds != null && !docIds.isEmpty()) {
			final String[] returnTypes = new String[] { FlexibleQueryMaker.COL_ID };
			StringBuilder sqlQuery = new StringBuilder("SELECT ID FROM LOCKS WHERE ID IN (").append(
					StringUtil.getQuestionMark(docIds.size())).append(")");
			IterableQueryResult res = null;
			try {
				res = QueryUtils.doSqlQuery(session, returnTypes, sqlQuery.toString(), docIds.toArray());
				Iterator<Map<String, Serializable>> iteratorIdLocks = res.iterator();
				while (iteratorIdLocks.hasNext()) {
					Map<String, Serializable> mapResult = iteratorIdLocks.next();
					lockDocIds.add((String) mapResult.get("id"));
				}
			} finally {
				if (res != null) {
					res.close();
				}
			}
		}

		return lockDocIds;
	}

	@Override
	public Map<String, String> extractLockedInfo(CoreSession session, Collection<String> docIds) throws ClientException {
		Map<String, String> lockDocInfo = new HashMap<String, String>();

		if (docIds != null && !docIds.isEmpty()) {
			final String[] returnTypes = new String[] { FlexibleQueryMaker.COL_ID, FlexibleQueryMaker.COL_LOCK_OWNER };
			StringBuilder sqlQuery = new StringBuilder(
					"SELECT LOCKS.ID as id, LOCKS.OWNER AS owner FROM LOCKS WHERE LOCKS.ID IN (");
			sqlQuery.append(StringUtil.getQuestionMark(docIds.size())).append(")");
			IterableQueryResult res = null;
			try {
				res = QueryUtils.doSqlQuery(session, returnTypes, sqlQuery.toString(), docIds.toArray());
				Iterator<Map<String, Serializable>> iteratorIdLocks = res.iterator();
				while (iteratorIdLocks.hasNext()) {
					Map<String, Serializable> mapResult = iteratorIdLocks.next();
					lockDocInfo.put((String) mapResult.get(FlexibleQueryMaker.COL_ID),
							(String) mapResult.get(FlexibleQueryMaker.COL_LOCK_OWNER));
				}
			} finally {
				if (res != null) {
					res.close();
				}
			}
		}

		return lockDocInfo;
	}

	/**
	 * Helper inner class to do the unrestricted unlock.
	 */
	protected class UnrestrictedUnlocker extends UnrestrictedSessionRunner {

		private final DocumentRef	docRefToUnlock;

		protected UnrestrictedUnlocker(DocumentRef docRef, CoreSession documentManager) {
			super(documentManager);
			docRefToUnlock = docRef;
		}

		/*
		 * Use an unrestricted session to unlock the document.
		 */
		@Override
		public void run() throws ClientException {
			try {
				session.removeLock(docRefToUnlock);
				removeAllLock(session.getDocument(docRefToUnlock), session);
				session.save();
			} catch (ClientException e) {
				TransactionHelper.setTransactionRollbackOnly();
				throw e;
			}

		}
	}

	/**
	 * Helper inner class to do the unrestricted lock.
	 */
	protected class UnrestrictedLocker extends UnrestrictedSessionRunner {

		private final DocumentRef	docRefToLock;

		protected UnrestrictedLocker(DocumentRef docRef, CoreSession documentManager) {
			super(documentManager);
			docRefToLock = docRef;
		}

		/*
		 * Use an unrestricted session to unlock the document.
		 */
		@Override
		public void run() throws ClientException {
			session.getPrincipal().getName();
			session.setLock(docRefToLock);
			session.save();
		}
	}

	private void removeAllLock(DocumentModel document, CoreSession session) throws ClientException {
		DocumentRoute docRoute = null;
		try {
			docRoute = document.getAdapter(DocumentRoute.class);
		} catch (Exception e) {
			// ce n'est pas une route on fait rien
			return;
		}

		if (docRoute != null && docRoute.getAttachedDocuments() != null && docRoute.getAttachedDocuments().size() > 1) {
			for (String idAttachedDocument : docRoute.getAttachedDocuments()) {
				session.removeLock(new IdRef(idAttachedDocument));
				// abi ajouté dans la boucle pour éviter les ORA60
				session.save();
			}
		}
	}

	private void lockAll(DocumentModel document, CoreSession session) throws ClientException {
		DocumentRoute docRoute = null;
		try {
			docRoute = document.getAdapter(DocumentRoute.class);
		} catch (Exception e) {
			// ce n'est pas une route on fait rien
			return;
		}
		if (docRoute != null && docRoute.getAttachedDocuments() != null && docRoute.getAttachedDocuments().size() > 1) {
			final UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(session);
			for (String idAttachedDocument : docRoute.getAttachedDocuments()) {
				DocumentModel doc = uGet.getById(idAttachedDocument);
				DocumentRef docRef = doc.getRef();
				if (session.getLockInfo(docRef) == null) {
					// new UnrestrictedLocker(docRef,session).runUnrestricted();
					session.setLock(docRef);
					// abi ajouté dans la boucle pour éviter les ORA60
					session.save();
				}
			}
		}
	}

	@Override
	public void unlockAllDocumentLockedByUser(CoreSession session, String userId) throws ClientException {

		if (StringUtils.isNotBlank(userId)) {
			final String[] returnTypes = new String[] { FlexibleQueryMaker.COL_ID };
			StringBuilder sqlQuery = new StringBuilder(QUERY_GET_ALL_LOCKED_DOCS);
			IterableQueryResult res = null;
			try {
				res = QueryUtils.doSqlQuery(session, returnTypes, sqlQuery.toString(), new String[] { userId });
				Iterator<Map<String, Serializable>> iteratorIdLocks = res.iterator();
				while (iteratorIdLocks.hasNext()) {
					Map<String, Serializable> mapResult = iteratorIdLocks.next();
					String idLock = (String) mapResult.get(FlexibleQueryMaker.COL_ID);
					DocumentModel doc = session.getDocument(new IdRef(idLock));
					unlockDoc(session, doc);
				}
			} finally {
				if (res != null) {
					res.close();
				}
			}
		}
	}
}
