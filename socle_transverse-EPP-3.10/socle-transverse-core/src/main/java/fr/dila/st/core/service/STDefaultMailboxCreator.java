package fr.dila.st.core.service;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.core.service.DefaultMailboxCreator;
import fr.dila.cm.exception.CaseManagementException;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.cm.service.CaseManagementDocumentTypeService;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.service.MailboxService;

/**
 * Implémentation du service de création de mailbox personnel du socle transverse, étend et remplace celui de Nuxeo.
 * 
 * @author arolin
 */
public class STDefaultMailboxCreator extends DefaultMailboxCreator {

	protected static final String	CM_DEFAULT_MAILBOX_CREATOR_SKIP	= "cm.defaultMailboxCreator.skip";

	private static final Log		LOG								= LogFactory.getLog(STDefaultMailboxCreator.class);

	private static final int		MAX_MAILBOX_ID_LENGTH			= STConstant.MAILBOX_PERSO_ID_PREFIX.length()
																			+ MailboxService.MAX_USERNAME_LENGTH;

	/**
	 * Default constructor
	 */
	public STDefaultMailboxCreator() {
		super();
	}

	/*
	 * surcharge la methode nuxeo
	 */
	@Override
	public String getPersonalMailboxId(DocumentModel userModel) {
		String userId = userModel.getId();
		return IdUtils.generateId(STConstant.MAILBOX_PERSO_ID_PREFIX + userId, "-", true, MAX_MAILBOX_ID_LENGTH);
	}

	/*
	 * surcharge de la methode nuxeo : ( IdUtils.generateId )
	 */
	@Override
	public List<Mailbox> createMailboxes(CoreSession session, String user) throws CaseManagementException {

		String skipCreation = Framework.getProperty(CM_DEFAULT_MAILBOX_CREATOR_SKIP);
		if (skipCreation != null && skipCreation.equals(Boolean.TRUE.toString())) {
			return Collections.emptyList();
		}

		try {

			// Retrieve the user
			UserManager userManager = Framework.getService(UserManager.class);
			if (userManager == null) {
				throw new CaseManagementException("User manager not found");
			}

			DocumentModel userModel = userManager.getUserModel(user);
			if (userModel == null) {
				LOG.debug(String.format("No User by that name. Maybe a wrong id or virtual user"));
				return Collections.emptyList();
			}

			// Create the personal mailbox for the user
			DocumentModel mailboxModel = session.createDocumentModel(getMailboxType());
			Mailbox mailbox = mailboxModel.getAdapter(Mailbox.class);

			// Set mailbox properties
			mailbox.setId(getPersonalMailboxId(userModel));
			mailbox.setTitle(getUserDisplayName(userModel));
			mailbox.setOwner(user);
			mailbox.setType(MailboxConstants.type.personal.name());

			// XXX: save it in first mailbox folder found for now
			DocumentModelList res = session.query(String.format("SELECT * from %s",
					MailboxConstants.MAILBOX_ROOT_DOCUMENT_TYPE));
			if (res == null || res.isEmpty()) {
				throw new CaseManagementException("Cannot find any mailbox folder");
			}

			mailboxModel.setPathInfo(res.get(0).getPathAsString(),
					IdUtils.generateId(mailbox.getTitle(), "-", true, MAX_MAILBOX_ID_LENGTH));
			mailboxModel = session.createDocument(mailboxModel);
			session.save(); // This will be queried after, needs a save to be found
			mailbox = mailboxModel.getAdapter(Mailbox.class);

			return Collections.singletonList(mailbox);

		} catch (Exception e) {
			throw new CaseManagementException("Error during mailboxes creation", e);
		}
	}

	/*
	 * surcharge la methode nuxeo
	 */
	public String getMailboxType() {
		CaseManagementDocumentTypeService correspDocumentTypeService = STServiceLocator
				.getCaseManagementDocumentTypeService();
		return correspDocumentTypeService.getMailboxType();
	}
}
