package fr.dila.st.ui.services.actions.impl;

import static org.nuxeo.ecm.core.api.security.SecurityConstants.EVERYTHING;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.WRITE_PROPERTIES;

import fr.dila.st.api.service.STLockService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.services.actions.STLockActionService;
import fr.dila.st.ui.th.enums.AlertType;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

public class STLockActionServiceImpl implements STLockActionService {
    private static final Log LOG = LogFactory.getLog(STLockActionService.class);

    private static final String UNLOCK_FAILED = "document.unlock.failed";

    private static final String UNLOCK = "document.unlock";

    private static final String LOCKER = "document.locker";

    private static final String LOCK_CREATED = "document.lock.created";

    private static final String UNLOCK_NOT_PERMITTED = "document.unlock.not.permitted";

    private static final String LOCK_FAILED = "document.lock.failed";

    private static final String LOCK_SUCCESS = "document.lock";

    private static final String UNLOCK_DOC_DEBUG = "Unlock documents ...";

    private static final String FORMAT_DOCUMENT = "document";

    public static final String EVENT_LOCK_DOCUMENTS = "stLockActions.lockDocuments";

    public static final String EVENT_UNLOCK_DOCUMENTS = "stLockActions.unlockDocuments";

    public static final String LOCK_LOST_ERROR_MSG = "lock.action.lockLost.error";

    public static final String ALREADY_LOCK_ERROR_MSG = "lock.action.alreadyLock.error";

    @Override
    public boolean lockDocuments(
        SpecificContext context,
        CoreSession session,
        List<DocumentModel> documents,
        String type
    ) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Lock several documents ...");
        }

        boolean result = true;
        String message = LOCK_SUCCESS;
        final STLockService socleLockActionsService = STServiceLocator.getSTLockService();
        for (DocumentModel document : documents) {
            if (!socleLockActionsService.lockDoc(session, document)) {
                result = false;
                message = LOCK_FAILED;
            }
        }

        if (type != null) {
            message = message.replace(FORMAT_DOCUMENT, type);
        }
        // afficher un toast quand le vérrouillage se passe bien sinon afficher
        // une alert
        if (result) {
            context.getMessageQueue().addMessageToQueue(message, AlertType.TOAST_INFO, null);
        } else {
            context.getMessageQueue().addWarnToQueue(message);
        }

        return result;
    }

    @Override
    public String unlockDocumentsUnrestricted(SpecificContext context, List<DocumentModel> documents, String type) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(UNLOCK_DOC_DEBUG);
        }
        CoreSession session = context.getSession();
        String message = UNLOCK_FAILED;
        boolean result = false;
        final STLockService socleLockActionsService = STServiceLocator.getSTLockService();
        for (DocumentModel document : documents) {
            if (socleLockActionsService.unlockDocUnrestricted(session, document)) {
                message = UNLOCK;
                result = true;
            } else {
                message = UNLOCK_NOT_PERMITTED;
            }
        }
        if (type != null) {
            message = message.replace(FORMAT_DOCUMENT, type);
        }

        // afficher un toast quand le dévérrouillage se passe bien sinon afficher
        // une alerte
        if (result) {
            context.getMessageQueue().addMessageToQueue(message, AlertType.TOAST_INFO);
        } else {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString(message));
        }
        return null;
    }

    @Override
    public boolean getCanLockDoc(DocumentModel document, CoreSession session) {
        boolean canLock;
        if (document == null) {
            LOG.warn("Can't evaluate lock action : currentDocument is null");
            canLock = false;
        } else if (document.isProxy()) {
            canLock = false;
        } else {
            NuxeoPrincipal userName = session.getPrincipal();
            Lock lock = session.getLockInfo(document.getRef());
            canLock =
                lock == null &&
                (
                    userName.isAdministrator() ||
                    isManagerOnDocument(document.getRef(), session) ||
                    session.hasPermission(document.getRef(), WRITE_PROPERTIES)
                ) &&
                !document.isVersion();
        }
        return canLock;
    }

    @Override
    public boolean getCanUnlockDoc(DocumentModel document, CoreSession session) {
        boolean canUnlock;
        if (document == null) {
            canUnlock = false;
        } else {
            try {
                NuxeoPrincipal userName = session.getPrincipal();
                Map<String, String> lockDetails = getLockDetails(document, session);
                if (lockDetails.isEmpty() || document.isProxy()) {
                    canUnlock = false;
                } else {
                    canUnlock =
                        (
                            (userName.isAdministrator() || session.hasPermission(document.getRef(), EVERYTHING))
                                ? true
                                : (
                                    userName.getName().equals(lockDetails.get(LOCKER)) &&
                                    session.hasPermission(document.getRef(), WRITE_PROPERTIES)
                                )
                        ) &&
                        !document.isVersion();
                }
            } catch (Exception e) {
                LOG.debug(
                    "evaluation of document lock " +
                    document.getName() +
                    " failed (" +
                    e.getMessage() +
                    ": returning false"
                );
                canUnlock = false;
            }
        }
        return canUnlock;
    }

    protected boolean isManagerOnDocument(DocumentRef ref, CoreSession session) {
        return session.hasPermission(ref, EVERYTHING);
    }

    @Override
    public Map<String, String> getLockDetails(DocumentModel document, CoreSession session) {
        if (document == null) {
            return null;
        }

        Map<String, String> lockDetails = new HashMap<>();
        try {
            Lock lock = session.getLockInfo(document.getRef());
            if (lock == null) {
                return null;
            }

            lockDetails.put(LOCKER, lock.getOwner());
            if (lock.getCreated() != null) {
                lockDetails.put(
                    LOCK_CREATED,
                    SolonDateConverter.getClientConverter().format(lock.getCreated().getTime())
                );
            }
        } catch (NuxeoException e) {
            LOG.error("error getting lock details", e);
        }

        return lockDetails;
    }

    @Override
    public String getLockTime(DocumentModel document, CoreSession session) {
        Map<String, String> lockDetails = getLockDetails(document, session);
        if (lockDetails == null || lockDetails.get(LOCK_CREATED) == null) {
            return null;
        } else {
            return lockDetails.get(LOCK_CREATED);
        }
    }

    @Override
    public String getLockOwnerName(DocumentModel document, CoreSession session) {
        Map<String, String> lockDetails = getLockDetails(document, session);
        if (lockDetails == null) {
            return "";
        }

        return lockDetails.get(LOCKER);
    }

    @Override
    public boolean currentDocIsLockActionnableByCurrentUser(
        CoreSession session,
        DocumentModel currentDocument,
        NuxeoPrincipal currentUser
    ) {
        return STServiceLocator.getSTLockService().isLockActionnableByUser(session, currentDocument, currentUser);
    }
}
