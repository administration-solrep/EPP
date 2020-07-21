package fr.dila.st.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.schema.PrefetchInfo;

import fr.dila.cm.caselink.CaseLink;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.service.CorbeilleService;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.util.StringUtil;

/**
 * Implémentation du service Corbeille du socle transverse.
 * 
 * @author jtremeaux
 */
public class CorbeilleServiceImpl implements CorbeilleService {
	/**
	 * Serial UID.
	 */
	private static final long		serialVersionUID			= -2392698015083550568L;

	protected static final String	DOSSIER_LINK_QUERY_UFNXQL	= "SELECT dl.ecm:uuid as id FROM "
																		+ STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE
																		+ " as dl WHERE dl.cslk:caseDocumentId = ? AND dl."
																		+ STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH
																		+ " = ? AND testAcl(dl.ecm:uuid) = 1";

	private static final String		EQUAL_TODO					= " = 'todo' ";

	/**
	 * Default constructor
	 */
	public CorbeilleServiceImpl() {
		// do nothing
	}

	@Override
	public List<DocumentModel> findDossierLink(final CoreSession session, final String dossierId)
			throws ClientException {
		// DocumentModelList dossierLinkList = session.query(String.format(DOSSIER_LINK_QUERY, dossierId));
		final Object[] params = new Object[] { dossierId, "todo" };
		return QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE,
				DOSSIER_LINK_QUERY_UFNXQL, params);
	}
	
	@Override
	public List<DocumentModel> findDossierLink(final CoreSession session, final Collection<String> dossierIds, PrefetchInfo prefetch) throws ClientException {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public Set<String> getMailboxIdSet(final DocumentModel caseLinkDoc) {
		final CaseLink caseLink = caseLinkDoc.getAdapter(CaseLink.class);

		final Set<String> allMailboxIds = new HashSet<String>();
		final Map<String, List<String>> recipients = caseLink.getInitialInternalParticipants();
		if (recipients != null) {
			for (final Map.Entry<String, List<String>> recipient : recipients.entrySet()) {
				allMailboxIds.addAll(recipient.getValue());
			}
		}
		return allMailboxIds;
	}

	@Override
	public List<DocumentModel> findDossierLinkInMailbox(final CoreSession session, final String dossierId,
			final Collection<String> mailboxIdList) throws ClientException {
		return findDossierLinkInMailbox(session, Collections.singletonList(dossierId), mailboxIdList);
	}
	
	@Override
	public List<DocumentModel> findDossierLinkInMailbox(final CoreSession session, final List<String> dossiersDocsIds,
			final Collection<String> mailboxIdList) throws ClientException {
		// Si l'utilisateur n'appartient à aucun poste, alors il n'a aucun DossierLink
		if (mailboxIdList == null || mailboxIdList.isEmpty()) {
			return new ArrayList<DocumentModel>();
		}

		final StringBuilder stringBuilder = new StringBuilder("SELECT l.ecm:uuid AS id FROM ")
				.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE).append(" AS l ")
				.append(" WHERE l.cslk:caseDocumentId IN (")
				.append(StringUtil.getQuestionMark(dossiersDocsIds.size()))
				.append(") AND l.")
				.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH).append(EQUAL_TODO)
				.append(" AND l.cmdist:initial_action_internal_participant_mailboxes IN (")
				.append(StringUtil.getQuestionMark(mailboxIdList.size())).append(")");

		final List<String> paramList = new ArrayList<String>();
		paramList.addAll(dossiersDocsIds);
		paramList.addAll(mailboxIdList);

		return QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(session,
				STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, stringBuilder.toString(),
				paramList.toArray(new String[paramList.size()]));
	}

	@Override
	public List<DocumentModel> findDossierLinkUnrestricted(final CoreSession session, final String dossierId)
			throws ClientException {
		return findDossierLinkUnrestricted(session, Collections.singletonList(dossierId));
	}
	
	@Override
	public List<DocumentModel> findDossierLinkUnrestricted(final CoreSession session, final List<String> dossiersDocsIds)
			throws ClientException {
		final StringBuilder stringBuilder = new StringBuilder("SELECT l.ecm:uuid AS id FROM ");
		stringBuilder.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE);
		stringBuilder.append(" AS l WHERE l.cslk:caseDocumentId IN (")
		.append(StringUtil.getQuestionMark(dossiersDocsIds.size()))
		.append(") AND l.");
		stringBuilder.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH);
		stringBuilder.append(EQUAL_TODO);

		return QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(session,
				STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, stringBuilder.toString(),
				dossiersDocsIds.toArray(new String[dossiersDocsIds.size()]));
	}

	/**
	 * get Dosier link from step Id
	 * 
	 * @param session
	 * @param stepId
	 *            the step Id
	 * @throws ClientException
	 */
	@Override
	public DocumentModel getDossierLink(final CoreSession session, final String stepId) throws ClientException {
		DocumentModel dossierLink = null;
		final StringBuilder stringBuilder = new StringBuilder("SELECT dl.ecm:uuid as id FROM ");
		stringBuilder.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE);
		stringBuilder.append(" AS dl WHERE dl.acslk:stepDocumentId = ? ");
		stringBuilder.append(" AND dl.");
		stringBuilder.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH);
		stringBuilder.append(EQUAL_TODO);

		final List<String> paramList = new ArrayList<String>();
		paramList.add(stepId);

		final List<DocumentModel> list = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session,
				STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, stringBuilder.toString(),
				paramList.toArray(new String[paramList.size()]), 1, 0);
		if (!list.isEmpty()) {
			dossierLink = list.get(0);
		}
		return dossierLink;
	}

	@Override
	public List<String> findCurrentStepsLabel(final CoreSession session, final String dossierId) throws ClientException {

		final StringBuilder stringBuilder = new StringBuilder("SELECT dl.acslk:");
		stringBuilder.append(STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_LABEL_PROPERTY);
		stringBuilder.append(" AS label FROM ");
		stringBuilder.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE);
		stringBuilder.append(" AS dl ");
		stringBuilder.append(" WHERE dl.cslk:caseDocumentId = ? AND dl.");
		stringBuilder.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH);
		stringBuilder.append(EQUAL_TODO);

		final List<String> paramList = new ArrayList<String>();
		paramList.add(dossierId);

		IterableQueryResult res = null;
		final List<String> list = new ArrayList<String>();
		try {
			res = QueryUtils.doUFNXQLQuery(session, stringBuilder.toString(),
					paramList.toArray(new String[paramList.size()]));
			final Iterator<Map<String, Serializable>> iterator = res.iterator();
			while (iterator.hasNext()) {
				final Map<String, Serializable> mapResult = iterator.next();
				list.add((String) mapResult.get("label"));
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}

		return list;

	}
}
