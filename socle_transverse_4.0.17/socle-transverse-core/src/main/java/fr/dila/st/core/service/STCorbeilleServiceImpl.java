package fr.dila.st.core.service;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.join;

import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.service.STCorbeilleService;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.util.StringHelper;
import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.schema.PrefetchInfo;

/**
 * Implémentation du service Corbeille du socle transverse.
 *
 * @author jtremeaux
 */
public class STCorbeilleServiceImpl implements STCorbeilleService {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -2392698015083550568L;

    protected static final String DOSSIER_LINK_QUERY_UFNXQL =
        "SELECT dl.ecm:uuid as id FROM " +
        STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE +
        " as dl WHERE dl.cslk:caseDocumentId = ? AND dl." +
        STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH +
        " = ? AND testAcl(dl.ecm:uuid) = 1";

    public static final String LIFECYCLE_EQUAL_TODO =
        STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH +
        " = '" +
        STDossierLink.CaseLinkState.todo.toString() +
        "' ";

    /**
     * Default constructor
     */
    public STCorbeilleServiceImpl() {
        // do nothing
    }

    @Override
    public List<DocumentModel> findDossierLink(final CoreSession session, final String dossierId) {
        final Object[] params = new Object[] { dossierId, STDossierLink.CaseLinkState.todo.toString() };
        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE,
            DOSSIER_LINK_QUERY_UFNXQL,
            params
        );
    }

    @Override
    public List<DocumentModel> findDossierLink(
        final CoreSession session,
        final Collection<String> dossierIds,
        PrefetchInfo prefetch
    ) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<String> getMailboxIdSet(final DocumentModel caseLinkDoc) {
        final STDossierLink caseLink = caseLinkDoc.getAdapter(STDossierLink.class);

        final Set<String> allMailboxIds = new HashSet<>();
        final Map<String, List<String>> recipients = caseLink.getInitialInternalParticipants();
        if (recipients != null) {
            for (final Map.Entry<String, List<String>> recipient : recipients.entrySet()) {
                allMailboxIds.addAll(recipient.getValue());
            }
        }
        return allMailboxIds;
    }

    @Override
    public List<DocumentModel> findDossierLinkInMailbox(
        final CoreSession session,
        final String dossierId,
        final Collection<String> mailboxIdList
    ) {
        return findDossierLinkInMailbox(session, Collections.singletonList(dossierId), mailboxIdList);
    }

    @Override
    public List<DocumentModel> findDossierLinkInMailbox(
        final CoreSession session,
        final List<String> dossiersDocsIds,
        final Collection<String> mailboxIdList
    ) {
        // Si l'utilisateur n'appartient à aucun poste, alors il n'a aucun DossierLink
        if (mailboxIdList == null || mailboxIdList.isEmpty()) {
            return new ArrayList<>();
        }

        final StringBuilder stringBuilder = new StringBuilder("SELECT l.ecm:uuid AS id FROM ")
            .append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE)
            .append(" AS l ")
            .append(" WHERE l.cslk:caseDocumentId IN (")
            .append(StringUtil.genMarksSuite(dossiersDocsIds.size()))
            .append(") AND l.")
            .append(LIFECYCLE_EQUAL_TODO)
            .append(" AND l.cmdist:initial_action_internal_participant_mailboxes IN (")
            .append(StringUtil.genMarksSuite(mailboxIdList.size()))
            .append(")");

        final List<String> paramList = new ArrayList<>();
        paramList.addAll(dossiersDocsIds);
        paramList.addAll(mailboxIdList);

        return queryDocs(session, stringBuilder.toString(), paramList);
    }

    protected List<DocumentModel> queryDocs(
        final CoreSession session,
        final String query,
        final Collection<String> paramList
    ) {
        return QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(
            session,
            query,
            paramList.toArray(new String[paramList.size()])
        );
    }

    @Override
    public List<DocumentModel> findDossierLinkUnrestricted(final CoreSession session, final String dossierId) {
        return findDossierLinkUnrestricted(session, Collections.singletonList(dossierId));
    }

    @Override
    public List<DocumentModel> findDossierLinkUnrestricted(
        final CoreSession session,
        final List<String> dossiersDocsIds
    ) {
        final StringBuilder stringBuilder = new StringBuilder("SELECT l.ecm:uuid AS id FROM ");
        stringBuilder.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE);
        stringBuilder
            .append(" AS l WHERE l.cslk:caseDocumentId IN (")
            .append(StringUtil.genMarksSuite(dossiersDocsIds.size()))
            .append(") AND l.");
        stringBuilder.append(LIFECYCLE_EQUAL_TODO);

        return queryDocsUnrestricted(session, stringBuilder.toString(), dossiersDocsIds);
    }

    /**
     * get Dosier link from step Id
     *
     * @param session
     * @param stepId
     *            the step Id
     */
    @Override
    public DocumentModel getDossierLink(final CoreSession session, final String stepId) {
        DocumentModel dossierLink = null;
        final StringBuilder stringBuilder = new StringBuilder("SELECT dl.ecm:uuid as id FROM ");
        stringBuilder.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE);
        stringBuilder.append(" AS dl WHERE dl.acslk:stepDocumentId = ? ");
        stringBuilder.append(" AND dl.");
        stringBuilder.append(LIFECYCLE_EQUAL_TODO);

        final List<String> paramList = new ArrayList<>();
        paramList.add(stepId);

        final List<DocumentModel> list = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE,
            stringBuilder.toString(),
            paramList.toArray(new String[paramList.size()]),
            1,
            0
        );
        if (!list.isEmpty()) {
            dossierLink = list.get(0);
        }
        return dossierLink;
    }

    @Override
    public List<String> findCurrentStepsLabel(final CoreSession session, final String dossierId) {
        final StringBuilder stringBuilder = new StringBuilder("SELECT dl.acslk:");
        stringBuilder.append(STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_LABEL_PROPERTY);
        stringBuilder.append(" AS label FROM ");
        stringBuilder.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE);
        stringBuilder.append(" AS dl ");
        stringBuilder.append(" WHERE dl.cslk:caseDocumentId = ? AND dl.");
        stringBuilder.append(LIFECYCLE_EQUAL_TODO);

        final List<String> paramList = new ArrayList<>();
        paramList.add(dossierId);

        IterableQueryResult res = null;
        final List<String> list = new ArrayList<>();
        try {
            res =
                QueryUtils.doUFNXQLQuery(
                    session,
                    stringBuilder.toString(),
                    paramList.toArray(new String[paramList.size()])
                );
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

    @Override
    public List<DocumentModel> findDossierLinkFromPoste(final CoreSession session, final Collection<String> postesId) {
        requireNonNull(postesId);
        Collection<String> mailboxs = getMailboxPosteIds(postesId);

        final StringBuilder stringBuilder = getDossierLinksQuery(mailboxs, true);

        return queryDocs(session, stringBuilder.toString(), mailboxs);
    }

    @Override
    public int countDossierLinksForPostes(
        final CoreSession session,
        final Collection<String> postesId,
        String... predicates
    ) {
        requireNonNull(postesId);
        Collection<String> mailboxs = getMailboxPosteIds(postesId);

        final StringBuilder stringBuilder = getDossierLinksQuery(mailboxs, true);
        stringBuilder.append(" AND testAcl(dl.ecm:uuid) = 1");

        if (ArrayUtils.isNotEmpty(predicates)) {
            stringBuilder.append(" AND " + join(predicates, " AND "));
        }
        return QueryHelper.doCountDistinctQuery(session, stringBuilder.toString(), mailboxs).intValue();
    }

    protected Collection<String> getMailboxPosteIds(final Collection<String> postesId) {
        return postesId.stream().map(p -> "poste-" + p).collect(toList());
    }

    private StringBuilder getDossierLinksQuery(Collection<String> mailboxs, boolean doCountDistinct) {
        StringBuilder stringBuilder;
        if (doCountDistinct) {
            stringBuilder = new StringBuilder("SELECT countDistinct(dl.ecm:uuid) as count FROM ");
        } else {
            stringBuilder = new StringBuilder("SELECT dl.ecm:uuid as id FROM ");
        }
        stringBuilder.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE);
        stringBuilder.append(" AS dl WHERE dl.cmdist:initial_action_internal_participant_mailboxes IN (");
        stringBuilder.append(StringHelper.getQuestionMark(mailboxs.size())).append(") AND dl.");
        stringBuilder.append(LIFECYCLE_EQUAL_TODO);
        return stringBuilder;
    }

    protected List<DocumentModel> queryDocsUnrestricted(
        final CoreSession session,
        final String query,
        final Collection<String> paramList
    ) {
        return queryDocs(session, query, paramList);
    }
}
