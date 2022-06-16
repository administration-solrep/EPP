package fr.dila.st.core.service;

import static fr.dila.st.core.service.STServiceLocator.getCaseManagementDocumentTypeService;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.cm.service.MailboxCreator;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.service.MailboxService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.api.Framework;

/**
 * Implémentation du service de création de mailbox personnel du socle transverse, étend et remplace celui de Nuxeo.
 *
 * @author arolin
 */
public class STDefaultMailboxCreator implements MailboxCreator {
    protected static final String CM_DEFAULT_MAILBOX_CREATOR_SKIP = "cm.defaultMailboxCreator.skip";

    private static final Log LOG = LogFactory.getLog(STDefaultMailboxCreator.class);

    private static final int MAX_MAILBOX_ID_LENGTH =
        STConstant.MAILBOX_PERSO_ID_PREFIX.length() + MailboxService.MAX_USERNAME_LENGTH;

    /**
     * Default constructor
     */
    public STDefaultMailboxCreator() {
        super();
    }

    @Override
    public String getPersonalMailboxId(String userId) {
        return IdUtils.generateId(STConstant.MAILBOX_PERSO_ID_PREFIX + userId, "-", true, MAX_MAILBOX_ID_LENGTH);
    }

    @Override
    public Mailbox createMailboxes(CoreSession session, DocumentModel userModel) {
        String skipCreation = Framework.getProperty(CM_DEFAULT_MAILBOX_CREATOR_SKIP);
        if (skipCreation != null && skipCreation.equals(Boolean.TRUE.toString())) {
            return null;
        }

        // Retrieve the user

        if (userModel == null) {
            LOG.debug(String.format("No User by that name. Maybe a wrong id or virtual user"));
            return null;
        }

        // Create the personal mailbox for the user
        final String mailboxType = getCaseManagementDocumentTypeService().getMailboxType();
        DocumentModel mailboxModel = session.createDocumentModel(mailboxType);
        Mailbox mailbox = mailboxModel.getAdapter(Mailbox.class);

        // Set mailbox properties
        mailbox.setId(getPersonalMailboxId(userModel.getId()));
        mailbox.setTitle(getUserDisplayName(userModel));
        mailbox.setOwner(userModel.getId());
        mailbox.setType(MailboxConstants.type.personal);

        // XXX: save it in first mailbox folder found for now
        DocumentModelList res = session.query(
            String.format("SELECT * from %s", MailboxConstants.MAILBOX_ROOT_DOCUMENT_TYPE)
        );
        if (res == null || res.isEmpty()) {
            throw new NuxeoException("Cannot find any mailbox folder");
        }

        mailboxModel.setPathInfo(res.get(0).getPathAsString(), generateMailboxName(mailbox.getTitle()));
        mailboxModel = session.createDocument(mailboxModel);
        session.save(); // This will be queried after, needs a save to be found
        mailbox = mailboxModel.getAdapter(Mailbox.class);

        return mailbox;
    }

    public static String generateMailboxName(String mailboxTitle) {
        return IdUtils.generateId(mailboxTitle, "-", true, MAX_MAILBOX_ID_LENGTH);
    }

    protected String getUserSchemaName() {
        return "user";
    }

    protected String getUserDisplayName(DocumentModel userModel) {
        String schemaName = getUserSchemaName();
        String first = (String) userModel.getProperty(schemaName, "firstName");
        String last = (String) userModel.getProperty(schemaName, "lastName");
        if (StringUtils.isEmpty(first)) {
            if (StringUtils.isEmpty(last)) {
                return userModel.getId();
            } else {
                return last;
            }
        } else {
            if (StringUtils.isEmpty(last)) {
                return first;
            } else {
                return first + ' ' + last;
            }
        }
    }
}
