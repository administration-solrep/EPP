package fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

public final class LockUtils {

    private LockUtils() {
        // do nothing
    }

    public static boolean isLocked(CoreSession session, DocumentRef docRef) {
        return session.getLockInfo(docRef) != null;
    }

    public static boolean isLockedBy(CoreSession session, DocumentRef docRef, NuxeoPrincipal user) {
        return user.getName().equals(getLockOwner(session, docRef));
    }

    public static boolean isLockedByCurrentUser(CoreSession session, DocumentRef docRef) {
        NuxeoPrincipal userName = session.getPrincipal();
        return isLockedBy(session, docRef, userName);
    }

    public static boolean isLockedByAnotherUser(CoreSession session, DocumentRef docRef) {
        final String owner = getLockOwner(session, docRef);
        NuxeoPrincipal userName = session.getPrincipal();
        return owner != null && !userName.getName().equals(owner);
    }

    public static boolean lockIfNeeded(CoreSession session, DocumentRef docRef) {
        if (isLockedByCurrentUser(session, docRef)) {
            return false;
        }
        session.setLock(docRef);
        return true;
    }

    public static void unlockDocument(CoreSession session, DocumentRef docRef) {
        session.removeLock(docRef);
    }

    private static String getLockOwner(CoreSession session, DocumentRef docRef) {
        Lock lockInfo = session.getLockInfo(docRef);
        if (lockInfo == null) {
            return null;
        } else {
            return lockInfo.getOwner();
        }
    }
}
