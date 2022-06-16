package fr.dila.ss.core.event.batch;

import fr.dila.cm.cases.CaseLifeCycleConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.st.api.constant.STSuiviBatchsConstants.TypeBatch;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.service.STUserManagerImpl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Batch de suppression des utilisateurs
 *
 * @author FEO
 */
public class UserDeletionBatchListener extends AbstractBatchEventListener {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(UserDeletionBatchListener.class);

    public UserDeletionBatchListener() {
        super(LOGGER, SSEventConstant.USER_DELETION_BATCH_EVENT);
    }

    @Override
    protected void processEvent(CoreSession session, Event event) {
        LOGGER.info(session, SSLogEnumImpl.INIT_B_DEL_USERS_TEC);
        batchType = TypeBatch.FONCTIONNEL;
        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        final MailboxService mailBoxService = STServiceLocator.getMailboxService();
        final long startTime = Calendar.getInstance().getTimeInMillis();
        int nbUserToDelete = 0;
        int nbUserDeleted = 0;
        try {
            STUserManagerImpl usermanager = (STUserManagerImpl) STServiceLocator.getUserManager();
            Map<String, Serializable> filter = new HashMap<String, Serializable>();
            filter.put("deleted", "TRUE");
            DocumentModelList userDocList = null;

            try {
                userDocList = usermanager.searchUsers(filter, null);
            } catch (NuxeoException ce) {
                LOGGER.error(session, SSLogEnumImpl.FAIL_PROCESS_B_DEL_USERS_TEC, ce);
                errorCount++;
            }
            final long endTime = Calendar.getInstance().getTimeInMillis();
            if (userDocList != null) {
                nbUserToDelete = userDocList.size();
                try {
                    suiviBatchService.createBatchResultFor(
                        batchLoggerModel,
                        "Nombre d'utilisateurs à supprimer",
                        nbUserToDelete,
                        endTime - startTime
                    );
                } catch (Exception e) {
                    LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
                }
                LOGGER.info(
                    session,
                    SSLogEnumImpl.PROCESS_B_DEL_USERS_TEC,
                    "Nombre d'utilisateurs à supprimer : " + nbUserToDelete
                );
                final long startTime2 = Calendar.getInstance().getTimeInMillis();

                Integer compteurDocument = 0;
                for (DocumentModel userDoc : userDocList) {
                    // Suppression du UserWorspace
                    UserWorkspaceService userWorkspaceService = STServiceLocator.getUserWorkspaceService();
                    DocumentModel userWorkspaceDoc = null;
                    try {
                        userWorkspaceDoc =
                            userWorkspaceService.getCurrentUserPersonalWorkspace(
                                userDoc.getId(),
                                session.getRootDocument()
                            );
                    } catch (NuxeoException ce) {
                        LOGGER.error(session, STLogEnumImpl.FAIL_GET_UW_TEC, userDoc, ce);
                        errorCount++;
                    }
                    if (userWorkspaceDoc != null) {
                        LOGGER.info(session, STLogEnumImpl.DEL_WORKSPACE_TEC, userWorkspaceDoc);
                        boolean removedDoc = false;
                        try {
                            session.removeDocument(userWorkspaceDoc.getRef());
                            removedDoc = true;
                        } catch (NuxeoException ce) {
                            LOGGER.error(session, STLogEnumImpl.FAIL_DEL_UW_TEC, userWorkspaceDoc, ce);
                            errorCount++;
                        }
                        if (removedDoc) {
                            // Suppression des verrous de l'utilisateur
                            final STLockService lockService = STServiceLocator.getSTLockService();
                            try {
                                lockService.unlockAllDocumentLockedByUser(session, userDoc.getId());
                            } catch (NuxeoException ce) {
                                LOGGER.warn(session, STLogEnumImpl.FAIL_UNLOCK_DOC_TEC, userDoc, ce);
                            }
                            // Suppression de l'utilisateur dans la table poste en BDD
                            STUser user = userDoc.getAdapter(STUser.class);
                            user.setPostes(new ArrayList<String>());
                            // Suppression de la corbeille de l'utilisateur
                            if (mailBoxService.hasUserPersonalMailbox(session, userDoc)) {
                                Mailbox mailbox = mailBoxService.getUserPersonalMailbox(session, userDoc);
                                if (mailbox != null) {
                                    DocumentModel mailboxDoc = mailbox.getDocument();
                                    if (
                                        mailboxDoc
                                            .getAllowedStateTransitions()
                                            .contains(CaseLifeCycleConstants.TRANSITION_DELETE)
                                    ) {
                                        try {
                                            mailboxDoc.followTransition(CaseLifeCycleConstants.TRANSITION_DELETE);
                                            mailbox.save(session);
                                            session.removeDocument(mailboxDoc.getRef());
                                        } catch (NuxeoException ce) {
                                            LOGGER.error(session, STLogEnumImpl.FAIL_GET_MAILBOX_TEC, mailboxDoc, ce);
                                        }
                                    }
                                }
                            }
                            // Suppression de l'utilisateur
                            LOGGER.debug(session, STLogEnumImpl.DEL_USER_TEC, userDoc);
                            try {
                                usermanager.physicalDeleteUser(userDoc);
                                nbUserDeleted++;
                            } catch (NuxeoException ce) {
                                LOGGER.error(session, STLogEnumImpl.FAIL_DEL_USER_TEC, userDoc, ce);
                                errorCount++;
                            }
                        }
                    }
                }
                session.save();
                compteurDocument = compteurDocument + 1;
                if (compteurDocument.equals(50)) {
                    compteurDocument = 0;
                    TransactionHelper.commitOrRollbackTransaction();
                    TransactionHelper.startTransaction();
                }
                final long endTime2 = Calendar.getInstance().getTimeInMillis();
                try {
                    suiviBatchService.createBatchResultFor(
                        batchLoggerModel,
                        "Nombre d'utilisateurs supprimés",
                        nbUserDeleted,
                        endTime2 - startTime2
                    );
                } catch (Exception e) {
                    LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
                }
            } else {
                try {
                    suiviBatchService.createBatchResultFor(
                        batchLoggerModel,
                        "Aucun utilisateur à supprimer",
                        nbUserToDelete,
                        endTime - startTime
                    );
                } catch (Exception e) {
                    LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
                }
            }
        } catch (Exception exc) {
            LOGGER.error(session, SSLogEnumImpl.FAIL_PROCESS_B_DEL_USERS_TEC, exc);
            errorCount++;
        }
        LOGGER.info(session, SSLogEnumImpl.END_B_DEL_USERS_TEC);
    }
}
