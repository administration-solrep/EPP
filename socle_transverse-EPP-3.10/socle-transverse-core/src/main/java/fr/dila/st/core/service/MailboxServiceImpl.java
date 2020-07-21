package fr.dila.st.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.core.service.MailboxManagementServiceImpl;
import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.cm.service.CaseManagementDocumentTypeService;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.core.util.UnrestrictedQueryRunner;

/**
 * Implémentation du service de mailbox du socle transverse, étend et remplace celui de Nuxeo.
 * 
 * @author jtremeaux
 */
public class MailboxServiceImpl extends MailboxManagementServiceImpl implements MailboxService {
	/**
	 * Serial ID.
	 */
	private static final long	serialVersionUID				= 1L;

	/**
	 * Logger.
	 */
	private static final Log	LOGGER							= LogFactory.getLog(MailboxServiceImpl.class);

	private static final String	KEY_TITLE						= "title";
	private static final String	UFNXQLQUERY_MAILBOX_TITLE_FMT	= "SELECT m.dc:title AS " + KEY_TITLE
																		+ " FROM %s AS m WHERE m."
																		+ MailboxConstants.ID_FIELD + " = ?";
	private static final String	UFNXQLQUERY_MAILBOX_FMT			= "SELECT m.ecm:uuid AS id FROM %s as m WHERE m."
																		+ MailboxConstants.ID_FIELD + " = ?";
	private static final String	SELECT_UUID						= "SELECT m.ecm:uuid as id FROM ";
	private static final String	AS_M_WHERE_M					= " as m WHERE m.";

	/**
	 * Default constructor
	 */
	public MailboxServiceImpl() {
		super();
	}

	@Override
	public String getMailboxType() {
		final CaseManagementDocumentTypeService correspDocumentTypeService = STServiceLocator
				.getCaseManagementDocumentTypeService();
		return correspDocumentTypeService.getMailboxType();
	}

	/*
	 * Surcharge de la méthode nuxeo afin de crééer des mailbox personnelles à des utilisateurs ayant un login supérieur
	 * à 19 caractères.
	 */
	@Override
	public Mailbox getUserPersonalMailbox(final CoreSession session, final String user) {
		final String mailboxId = getPersoMailboxId(user);
		return getMailboxUnrestricted(session, mailboxId);
	}

	/*
	 * Surcharge de la méthode nuxeo afin de crééer des mailbox personnelles à des utilisateurs ayant un login supérieur
	 * à 19 caractères.
	 */
	@Override
	public List<Mailbox> createPersonalMailboxes(final CoreSession session, final String user) {
		if (personalMailboxCreator == null) {
			// on récupère le mailBoxCreator du socle transverse STDefaultMailboxCreator
			personalMailboxCreator = STServiceLocator.getMailboxCreator();
			// throw new
			// CaseManagementRuntimeException("Cannot create personal mailbox: missing creator configuration");
		}
		// First check if mailbox exists using unrestricted session to
		// avoid creating multiple personal mailboxes for a given user in
		// case there's something wrong with Read rights on mailbox folder
		final String muid = getUserPersonalMailboxId(user);
		if (hasMailbox(session, muid)) {
			LOGGER.warn(String.format("Cannot create personal mailbox for user '%s': "
					+ "it already exists with id '%s'", user, muid));
			return Arrays.asList(getMailbox(session, muid));
		}
		try {
			return personalMailboxCreator.createMailboxes(session, user);
		} catch (final Exception e) {
			throw new CaseManagementRuntimeException(e.getMessage(), e);
		}
	}

	// TODO limiter la taille du login dans l'IHM !
	@Override
	public String getPersoMailboxId(final String userName) {
		return IdUtils.generateId(STConstant.MAILBOX_PERSO_ID_PREFIX + userName, "-", true,
				STConstant.MAILBOX_PERSO_ID_PREFIX.length() + MAX_USERNAME_LENGTH);
	}

	@Override
	public String getMailboxDocIdUnrestricted(final CoreSession session, final String mailboxId) {
		try {
			final String query = String.format(UFNXQLQUERY_MAILBOX_FMT, getMailboxType());
			final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, new String[] { mailboxId }, 1,
					0);
			if (ids.isEmpty()) {
				return null;
			} else {
				return ids.get(0);
			}
		} catch (final ClientException e) {
			LOGGER.error("Erreur de récupération de l'id du document pour la Mailbox <" + mailboxId + ">", e);
			return null;
		}
	}

	@Override
	public Mailbox getMailboxUnrestricted(final CoreSession session, final String mailboxId) {

		final String mailBoxType = getMailboxType();
		final StringBuilder sb = new StringBuilder(SELECT_UUID);
		sb.append(mailBoxType);
		sb.append(AS_M_WHERE_M);
		sb.append(MailboxConstants.ID_FIELD);
		sb.append(" = ? ");

		try {
			final List<DocumentModel> docs = QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(session,
					mailBoxType, sb.toString(), new Object[] { mailboxId }, 1, 0);
			if (docs.isEmpty()) {
				return null;
			} else {
				return docs.get(0).getAdapter(Mailbox.class);
			}
		} catch (final ClientException e) {
			LOGGER.error("Erreur de récupération de la Mailbox <" + mailboxId + ">", e);
			return null;
		}
	}

	@Override
	public Mailbox getMailbox(final CoreSession session, final String mailboxId) {
		final String mailBoxType = getMailboxType();
		final StringBuilder stringBuilder = new StringBuilder(SELECT_UUID);
		stringBuilder.append(mailBoxType);
		stringBuilder.append(AS_M_WHERE_M);
		stringBuilder.append(MailboxConstants.ID_FIELD);
		stringBuilder.append(" = ? ");

		try {
			final List<DocumentModel> docs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, mailBoxType,
					stringBuilder.toString(), new Object[] { mailboxId }, 1, 0);
			if (docs.isEmpty()) {
				return null;
			} else {
				return docs.get(0).getAdapter(Mailbox.class);
			}
		} catch (final ClientException e) {
			LOGGER.error("Erreur de récupération de la Mailbox <" + mailboxId + ">", e);
			return null;
		}
	}

	@Override
	public List<Mailbox> getMailbox(final CoreSession session, final Collection<String> mailboxIdList) {

		if (mailboxIdList == null || mailboxIdList.isEmpty()) {
			return Collections.emptyList();
		}
		final String mailBoxType = getMailboxType();
		final StringBuilder stringBuilder = new StringBuilder(SELECT_UUID);
		stringBuilder.append(mailBoxType);
		stringBuilder.append(AS_M_WHERE_M);
		stringBuilder.append(MailboxConstants.ID_FIELD);
		stringBuilder.append(" IN (");
		stringBuilder.append(StringUtil.getQuestionMark(mailboxIdList.size()));
		stringBuilder.append(") ");

		try {
			final List<DocumentModel> docList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, mailBoxType,
					stringBuilder.toString(), mailboxIdList.toArray());

			final List<Mailbox> mailboxList = new ArrayList<Mailbox>();
			for (final DocumentModel doc : docList) {
				mailboxList.add(doc.getAdapter(Mailbox.class));
			}
			return mailboxList;
		} catch (final ClientException e) {
			LOGGER.error("Erreur de récupération des mailbox poste de l'utilisateur", e);
			return null;
		}
	}

	@Override
	public DocumentModel getMailboxRoot(final CoreSession session) throws ClientException {
		final String queryString = String.format("SELECT * from %s where ecm:isProxy = 0 ",
				MailboxConstants.MAILBOX_ROOT_DOCUMENT_TYPE);
		final DocumentModel mailboxRoot = new UnrestrictedQueryRunner(session, queryString).getFirst();

		if (mailboxRoot == null) {
			throw new ClientException("Aucune MailboxRoot trouvée");
		}

		return mailboxRoot;
	}

	@Override
	public String getMailboxTitle(final CoreSession session, final String mailboxId) throws ClientException {
		if (session == null) {
			return null;
		}

		final String query = String.format(UFNXQLQUERY_MAILBOX_TITLE_FMT, getMailboxType());
		final List<String> titles = QueryUtils.doUFNXQLQueryAndMapping(session, query, new String[] { mailboxId }, 1,
				0, new QueryUtils.RowMapper<String>() {

					@Override
					public String doMapping(final Map<String, Serializable> rowData) throws ClientException {
						return (String) rowData.get(KEY_TITLE);
					}

				});

		if (titles.isEmpty()) {
			return null;
		} else {
			return titles.get(0);
		}
	}

	@Override
	public List<Mailbox> getAllMailboxPoste(final CoreSession session) throws ClientException {

		final StringBuilder stringBuilder = new StringBuilder(SELECT_UUID);
		stringBuilder.append(getMailboxType());
		stringBuilder.append(AS_M_WHERE_M);
		stringBuilder.append(MailboxConstants.ID_FIELD);
		stringBuilder.append(" like '%poste%' ");

		try {
			final List<DocumentModel> docList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, getMailboxType(),
					stringBuilder.toString(), null);

			final List<Mailbox> mailboxList = new ArrayList<Mailbox>();
			for (final DocumentModel doc : docList) {
				mailboxList.add(doc.getAdapter(Mailbox.class));
			}
			return mailboxList;

		} catch (final ClientException e) {
			LOGGER.error("Erreur de récupération des mailbox poste", e);
			return null;
		}
	}

	@Override
	public String getUserPersonalMailboxId(DocumentModel userModel) {
		if (userModel == null) {
			LOGGER.debug(String.format("No User by that name. Maybe a wrong id or virtual user"));
			return null;
		}
		if (personalMailboxCreator == null) {
			// on récupère le mailBoxCreator du socle transverse STDefaultMailboxCreator
			personalMailboxCreator = STServiceLocator.getMailboxCreator();
			// throw new
			// CaseManagementRuntimeException("Cannot create personal mailbox: missing creator configuration");
		}
		return personalMailboxCreator.getPersonalMailboxId(userModel);
	}

	@Override
	public Mailbox getUserPersonalMailboxUFNXQL(final CoreSession session, final String user) throws ClientException {
		final String mailboxId = getPersoMailboxId(user);

		final String mailBoxType = getMailboxType();

		final StringBuilder stringBuilder = new StringBuilder(SELECT_UUID);
		stringBuilder.append(mailBoxType);
		stringBuilder.append(AS_M_WHERE_M);
		stringBuilder.append(MailboxConstants.ID_FIELD);
		stringBuilder.append(" = ? ");

		final List<DocumentModel> docs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, mailBoxType,
				stringBuilder.toString(), new Object[] { mailboxId }, 1, 0);

		if (docs.isEmpty()) {
			return null;
		} else {
			return docs.get(0).getAdapter(Mailbox.class);
		}
	}

}
