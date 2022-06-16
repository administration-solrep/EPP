package fr.dila.st.core.service;

import fr.dila.st.api.administration.NotificationsSuiviBatchs;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.NotificationsSuiviBatchsService;
import fr.dila.st.api.service.STUserManager;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Service de gestion des notifications de suivi des batchs
 *
 * @author JBT
 *
 */
public class NotificationsSuiviBatchsServiceImpl implements NotificationsSuiviBatchsService {
    private static final STLogger LOGGER = STLogFactory.getLog(NotificationsSuiviBatchsServiceImpl.class);
    private static final String NOTIFS_SUIVI_BATCHS_QUERY =
        "select * from NotificationsSuiviBatchs where " + STSchemaConstant.ECM_ISPROXY_XPATH + " = 0";

    public static final String RESTRICTION_ACCESS = "RESTRICTION_ACCESS";

    public static final String RESTRICTION_DESCRIPTION = "RESTRICTION_DESCRIPTION";

    private String idNotificationsSuiviBatchs = null;

    /**
     * Default constructor
     */
    public NotificationsSuiviBatchsServiceImpl() {
        // do nothing
    }

    @Override
    public void desactiverNotifications(CoreSession session) {
        NotificationsSuiviBatchs notificationsSuiviBatchs = getNotificationsSuiviBatchsDocument(session);

        notificationsSuiviBatchs.setEtatNotification(false);

        session.saveDocument(notificationsSuiviBatchs.getDocument());
    }

    @Override
    public void activerNotifications(CoreSession session) {
        NotificationsSuiviBatchs notificationsSuiviBatchs = getNotificationsSuiviBatchsDocument(session);

        notificationsSuiviBatchs.setEtatNotification(true);

        session.saveDocument(notificationsSuiviBatchs.getDocument());
    }

    @Override
    public NotificationsSuiviBatchs getNotificationsSuiviBatchsDocument(CoreSession session) {
        DocumentModel notificationsSuiviBatchs;

        if (idNotificationsSuiviBatchs == null) {
            DocumentModelList notificationsSuiviBatchsList = session.query(NOTIFS_SUIVI_BATCHS_QUERY);
            if (notificationsSuiviBatchsList == null || notificationsSuiviBatchsList.size() != 1) {
                throw new NuxeoException(
                    "Le document de notifications de suivi des batchs n'existe pas ou est présent en plus d'un exemplaire"
                );
            }
            idNotificationsSuiviBatchs = notificationsSuiviBatchsList.get(0).getId();
            notificationsSuiviBatchs = notificationsSuiviBatchsList.get(0);
        } else {
            notificationsSuiviBatchs = session.getDocument(new IdRef(idNotificationsSuiviBatchs));
        }
        return notificationsSuiviBatchs.getAdapter(NotificationsSuiviBatchs.class);
    }

    @Override
    public List<String> getAllUserName(CoreSession session) {
        NotificationsSuiviBatchs notificationsSuiviBatchs = getNotificationsSuiviBatchsDocument(session);
        return notificationsSuiviBatchs.getReceiverMailList();
    }

    @Override
    public void updateUserNames(List<String> users, CoreSession session) {
        NotificationsSuiviBatchs notificationsSuiviBatchs = getNotificationsSuiviBatchsDocument(session);
        List<String> receivers = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(users)) {
            final List<String> admins = STServiceLocator
                .getUserManager()
                .getUsersInGroup(STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME);
            receivers.addAll(CollectionUtils.intersection(users, admins));
        }

        notificationsSuiviBatchs.setReceiverMailList(receivers);
        session.saveDocument(notificationsSuiviBatchs.getDocument());
    }

    @Override
    public boolean sontActiveesNotifications(CoreSession session) {
        NotificationsSuiviBatchs notificationsSuiviBatchs = getNotificationsSuiviBatchsDocument(session);
        return notificationsSuiviBatchs.getEtatNotification();
    }

    @Override
    public List<STUser> getAllUsers(CoreSession session) {
        NotificationsSuiviBatchs notificationsSuiviBatchs;
        final STUserManager userManager = (STUserManager) STServiceLocator.getUserManager();
        try {
            notificationsSuiviBatchs = getNotificationsSuiviBatchsDocument(session);
            List<STUser> listUser = new ArrayList<>();
            for (String username : notificationsSuiviBatchs.getReceiverMailList()) {
                DocumentModel userModel = userManager.getUserModel(username);
                if (userModel != null) {
                    STUser user = userModel.getAdapter(STUser.class);
                    if (user != null) {
                        listUser.add(user);
                    }
                } else {
                    LOGGER.error(STLogEnumImpl.FAIL_GET_USER_TEC, "L'utilisateur " + username + " n'existe pas.");
                }
            }
            return listUser;
        } catch (NuxeoException exc) {
            LOGGER.warn(session, STLogEnumImpl.FAIL_GET_USER_TEC, exc);
            return null;
        }
    }
}
