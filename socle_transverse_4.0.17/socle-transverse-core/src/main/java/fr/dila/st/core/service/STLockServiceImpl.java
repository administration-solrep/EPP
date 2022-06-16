package fr.dila.st.core.service;

import static org.nuxeo.ecm.core.api.security.SecurityConstants.EVERYTHING;

import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.service.STLockService;
import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Implémentation du service de verrous du socle transverse.
 *
 * @author jtremeaux
 */
public class STLockServiceImpl implements STLockService {
    /**
     * Serializable.
     */
    private static final long serialVersionUID = -7476782975605260224L;

    private static final String QUERY_GET_ALL_LOCKED_DOCS = "SELECT LOCKS.ID as id FROM LOCKS WHERE LOCKS.OWNER = ?";

    public STLockServiceImpl() {
        super();
    }

    @Override
    public boolean unlockDoc(DocumentModel document, CoreSession documentManager) {
        Lock lock = getLockDetails(documentManager, document);

        if (lock == null) {
            return true;
        }

        NuxeoPrincipal userName = (NuxeoPrincipal) documentManager.getPrincipal();
        if (
            !(
                userName.isAdministrator() ||
                documentManager.hasPermission(document.getRef(), SecurityConstants.EVERYTHING) ||
                userName.getName().equals(lock.getOwner())
            )
        ) {
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
                throw new NuxeoException(e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean unlockDoc(CoreSession session, DocumentModel document) {
        Lock lockDetails = getLockDetails(session, document);

        NuxeoPrincipal userName = (NuxeoPrincipal) session.getPrincipal();
        if (
            !(
                userName.isAdministrator() ||
                session.hasPermission(document.getRef(), SecurityConstants.EVERYTHING) ||
                userName.getName().equals(lockDetails.getOwner())
            )
        ) {
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
                throw new NuxeoException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void unlockDossierAndRoute(CoreSession session, STDossier dossier) {
        DocumentModel dossierDoc = dossier.getDocument();
        // Unlock du dossier et de la route si nécessaire
        Lock dossierLockInfo = getLockDetails(session, dossierDoc);
        if (dossierLockInfo != null && dossierLockInfo.getOwner() != null) {
            unlockDocUnrestricted(session, dossierDoc);
        }
        String idRoute = dossier.getLastDocumentRoute();
        if (idRoute != null) {
            DocumentModel routeDoc = session.getDocument(new IdRef(idRoute));
            Lock routeLockInfo = getLockDetails(session, routeDoc);
            if (routeLockInfo != null && routeLockInfo.getOwner() != null) {
                unlockDocUnrestricted(session, routeDoc);
            }
        }
    }

    @Override
    public boolean unlockDocUnrestricted(CoreSession session, DocumentModel document) {
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
                throw new NuxeoException(e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean lockDoc(CoreSession documentManager, DocumentModel document) {
        DocumentRef ref = document.getRef();

        if (
            !(
                documentManager.hasPermission(ref, SecurityConstants.WRITE_PROPERTIES) &&
                documentManager.getLockInfo(ref) == null
            )
        ) {
            return false;
        }

        documentManager.setLock(ref);
        lockAll(document, documentManager);
        documentManager.save();

        return true;
    }

    @Override
    public boolean isLocked(CoreSession session, DocumentModel document) {
        return session.getLockInfo(document.getRef()) != null;
    }

    @Override
    public boolean isLockActionnableByUser(CoreSession session, DocumentModel document, NuxeoPrincipal user) {
        Lock lock = getLockDetails(session, document);

        if (
            lock == null ||
            user.isAdministrator() ||
            session.hasPermission(user, document.getRef(), EVERYTHING) ||
            user.getName().equals(lock.getOwner())
        ) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isLockByUser(CoreSession session, DocumentModel document, NuxeoPrincipal user) {
        Lock lock = getLockDetails(session, document);
        return lock != null && user.getName().equals(lock.getOwner());
    }

    @Override
    public boolean isLockByCurrentUser(CoreSession session, DocumentModel document) {
        NuxeoPrincipal userName = (NuxeoPrincipal) session.getPrincipal();

        return isLockByUser(session, document, userName);
    }

    @Override
    public Lock getLockDetails(CoreSession session, DocumentModel document) {
        return session.getLockInfo(document.getRef());
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
    public Set<String> extractLocked(CoreSession session, Collection<String> docIds) {
        Set<String> lockDocIds = new HashSet<String>();

        if (docIds != null && !docIds.isEmpty()) {
            final String[] returnTypes = new String[] { FlexibleQueryMaker.COL_ID };
            StringBuilder sqlQuery = new StringBuilder("SELECT ID FROM LOCKS WHERE ID IN (")
                .append(StringUtil.genMarksSuite(docIds.size()))
                .append(")");
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
    public Map<String, String> extractLockedInfo(CoreSession session, Collection<String> docIds) {
        Map<String, String> lockDocInfo = new HashMap<String, String>();

        if (docIds != null && !docIds.isEmpty()) {
            final String[] returnTypes = new String[] { FlexibleQueryMaker.COL_ID, FlexibleQueryMaker.COL_LOCK_OWNER };
            StringBuilder sqlQuery = new StringBuilder(
                "SELECT LOCKS.ID as id, LOCKS.OWNER AS owner FROM LOCKS WHERE LOCKS.ID IN ("
            );
            sqlQuery.append(StringUtil.genMarksSuite(docIds.size())).append(")");
            IterableQueryResult res = null;
            try {
                res = QueryUtils.doSqlQuery(session, returnTypes, sqlQuery.toString(), docIds.toArray());
                Iterator<Map<String, Serializable>> iteratorIdLocks = res.iterator();
                while (iteratorIdLocks.hasNext()) {
                    Map<String, Serializable> mapResult = iteratorIdLocks.next();
                    lockDocInfo.put(
                        (String) mapResult.get(FlexibleQueryMaker.COL_ID),
                        (String) mapResult.get(FlexibleQueryMaker.COL_LOCK_OWNER)
                    );
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
        private final DocumentRef docRefToUnlock;

        protected UnrestrictedUnlocker(DocumentRef docRef, CoreSession documentManager) {
            super(documentManager);
            docRefToUnlock = docRef;
        }

        /*
         * Use an unrestricted session to unlock the document.
         */
        @Override
        public void run() {
            try {
                session.removeLock(docRefToUnlock);
                removeAllLock(session.getDocument(docRefToUnlock), session);
                session.save();
            } catch (NuxeoException e) {
                TransactionHelper.setTransactionRollbackOnly();
                throw e;
            }
        }
    }

    /**
     * Helper inner class to do the unrestricted lock.
     */
    protected class UnrestrictedLocker extends UnrestrictedSessionRunner {
        private final DocumentRef docRefToLock;

        protected UnrestrictedLocker(DocumentRef docRef, CoreSession documentManager) {
            super(documentManager);
            docRefToLock = docRef;
        }

        /*
         * Use an unrestricted session to unlock the document.
         */
        @Override
        public void run() {
            session.getPrincipal().getName();
            session.setLock(docRefToLock);
            session.save();
        }
    }

    protected void removeAllLock(DocumentModel document, CoreSession session) {
        // do nothing : do something only for FeuilleRoute (done in socle_solrep)
    }

    protected void lockAll(DocumentModel document, CoreSession session) {
        // do nothing : do something only for FeuilleRoute (done in socle_solrep)
    }

    @Override
    public void unlockAllDocumentLockedByUser(CoreSession session, String userId) {
        if (StringUtils.isNotBlank(userId)) {
            final String[] returnTypes = new String[] { FlexibleQueryMaker.COL_ID };
            StringBuilder sqlQuery = new StringBuilder(QUERY_GET_ALL_LOCKED_DOCS);
            try (
                IterableQueryResult res = QueryUtils.doSqlQuery(
                    session,
                    returnTypes,
                    sqlQuery.toString(),
                    new String[] { userId }
                )
            ) {
                Iterator<Map<String, Serializable>> iteratorIdLocks = res.iterator();
                while (iteratorIdLocks.hasNext()) {
                    Map<String, Serializable> mapResult = iteratorIdLocks.next();
                    String idLock = (String) mapResult.get(FlexibleQueryMaker.COL_ID);
                    DocumentModel doc = session.getDocument(new IdRef(idLock));
                    unlockDoc(session, doc);
                }
            }
        }
    }
}
