package fr.dila.st.core.service;

import static java.util.stream.Collectors.toList;
import static org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider.CORE_SESSION_PROPERTY;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.cm.service.CaseManagementDocumentTypeService;
import fr.dila.cm.service.MailboxCreator;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.util.UnrestrictedQueryRunner;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import fr.sword.naiad.nuxeo.ufnxql.core.query.mapper.RowMapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.AbstractSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

/**
 * Implémentation du service de mailbox du socle transverse, étend et remplace celui de Nuxeo.
 *
 * @author jtremeaux
 */
public class MailboxServiceImpl implements MailboxService {
    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog(MailboxServiceImpl.class);

    private static final String KEY_TITLE = "title";
    private static final String UFNXQLQUERY_MAILBOX_TITLE_FMT =
        "SELECT m.dc:title AS " + KEY_TITLE + " FROM %s AS m WHERE m." + MailboxConstants.ID_FIELD + " = ?";
    private static final String UFNXQLQUERY_MAILBOX_FMT =
        "SELECT m.ecm:uuid AS id FROM %s as m WHERE m." + MailboxConstants.ID_FIELD + " = ?";
    private static final String SELECT_UUID = "SELECT m.ecm:uuid as id FROM ";
    private static final String AS_M_WHERE_M = " as m WHERE m.";

    private static final String QUERY_GET_ALL_MAILBOX = "GET_ALL_MAILBOX";
    private static final String QUERY_GET_MAILBOX_FROM_ID = "GET_MAILBOX_FROM_ID";

    private MailboxCreator personalMailboxCreator;

    /**
     * Default constructor
     */
    public MailboxServiceImpl() {
        super();
    }

    @Override
    public String getMailboxType() {
        final CaseManagementDocumentTypeService correspDocumentTypeService = STServiceLocator.getCaseManagementDocumentTypeService();
        return correspDocumentTypeService.getMailboxType();
    }

    /*
     * Surcharge de la méthode nuxeo afin de crééer des mailbox personnelles à des utilisateurs ayant un login supérieur
     * à 19 caractères.
     */
    @Override
    public Mailbox getUserPersonalMailbox(final CoreSession session, final String user) {
        final String mailboxId = getUserPersonalMailboxId(user);
        return getMailboxUnrestricted(session, mailboxId);
    }

    @Override
    public String getMailboxDocIdUnrestricted(final CoreSession session, final String mailboxId) {
        try {
            final String query = String.format(UFNXQLQUERY_MAILBOX_FMT, getMailboxType());
            final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(
                session,
                query,
                new String[] { mailboxId },
                1,
                0
            );
            if (ids.isEmpty()) {
                return null;
            } else {
                return ids.get(0);
            }
        } catch (final NuxeoException e) {
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
            final List<DocumentModel> docs = doUnrestrictedUFNXQLQueryAndFetchForDocuments(
                session,
                sb.toString(),
                new Object[] { mailboxId },
                1,
                0
            );
            if (docs.isEmpty()) {
                return null;
            } else {
                DocumentModel doc = docs.get(0);
                return doc.getAdapter(Mailbox.class);
            }
        } catch (final NuxeoException e) {
            LOGGER.error("Erreur de récupération de la Mailbox <" + mailboxId + ">", e);
            return null;
        }
    }

    private List<DocumentModel> doUnrestrictedUFNXQLQueryAndFetchForDocuments(
        final CoreSession initSession,
        final String queryString,
        final Object[] params,
        final long limit,
        final long offset
    ) {
        final List<DocumentModel> documentList = new ArrayList<>();
        CoreInstance.doPrivileged(
            initSession,
            session -> {
                final DocumentRef[] refs = QueryUtils.doUFNXQLQueryForIds(session, queryString, params, limit, offset);
                if (refs != null) {
                    for (final DocumentRef ref : refs) {
                        DocumentModel doc = session.getDocument(ref);
                        doc.detach(true);
                        documentList.add(doc);
                    }
                }
            }
        );
        return documentList;
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
            final List<DocumentModel> docs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
                session,
                mailBoxType,
                stringBuilder.toString(),
                new Object[] { mailboxId },
                1,
                0
            );
            if (docs.isEmpty()) {
                return null;
            } else {
                return docs.get(0).getAdapter(Mailbox.class);
            }
        } catch (final NuxeoException e) {
            LOGGER.error("Erreur de récupération de la Mailbox <" + mailboxId + ">", e);
            return null;
        }
    }

    @Override
    public List<Mailbox> getMailbox(
        final CoreSession session,
        final Collection<String> mailboxIdList,
        String... prefetch
    ) {
        if (CollectionUtils.isEmpty(mailboxIdList)) {
            return Collections.emptyList();
        }
        return QueryHelper
            .doUFNXQLQueryAndFetchForDocuments(
                session,
                getMailboxQuery(mailboxIdList),
                mailboxIdList.toArray(),
                0,
                0,
                prefetch
            )
            .stream()
            .map(this::toMailbox)
            .collect(toList());
    }

    @Override
    public List<Mailbox> getMailboxUnrestricted(
        final CoreSession session,
        final Collection<String> mailboxIdList,
        String... prefetch
    ) {
        return CoreInstance.doPrivileged(
            session,
            uSession -> {
                return getMailbox(uSession, mailboxIdList, prefetch);
            }
        );
    }

    @Override
    public List<String> getMailboxDocIds(final CoreSession session, final Collection<String> mailboxIdList) {
        if (CollectionUtils.isEmpty(mailboxIdList)) {
            return Collections.emptyList();
        }
        return QueryUtils.doUFNXQLQueryForIdsList(session, getMailboxQuery(mailboxIdList), mailboxIdList.toArray());
    }

    @Override
    public Map<String, String> getMapMailboxDocIdsIds(
        final CoreSession session,
        final Collection<String> mailboxIdList
    ) {
        if (CollectionUtils.isEmpty(mailboxIdList)) {
            return Collections.emptyMap();
        }

        String queryFmt =
            "Select m.ecm:uuid as id, m.mlbx:mailbox_id as mid From %s as m WHERE m.mlbx:mailbox_id in (%s)";
        String query = String.format(queryFmt, getMailboxType(), StringUtil.genMarksSuite(mailboxIdList.size()));
        return QueryUtils
            .doUFNXQLQueryAndMapping(
                session,
                query,
                mailboxIdList.toArray(),
                (Map<String, Serializable> rowData) ->
                    new ImmutablePair<>((String) rowData.get("id"), (String) rowData.get("mid"))
            )
            .stream()
            .collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    private String getMailboxQuery(Collection<String> mailboxIdList) {
        return new StringBuilder(SELECT_UUID)
            .append(getMailboxType())
            .append(AS_M_WHERE_M)
            .append(MailboxConstants.ID_FIELD)
            .append(" IN (")
            .append(StringUtil.genMarksSuite(mailboxIdList.size()))
            .append(") ")
            .toString();
    }

    @Override
    public DocumentModel getMailboxRoot(final CoreSession session) {
        final String queryString = String.format(
            "SELECT * from %s where ecm:isProxy = 0 ",
            MailboxConstants.MAILBOX_ROOT_DOCUMENT_TYPE
        );
        final DocumentModel mailboxRoot = new UnrestrictedQueryRunner(session, queryString).getFirst();

        if (mailboxRoot == null) {
            throw new NuxeoException("Aucune MailboxRoot trouvée");
        }

        return mailboxRoot;
    }

    @Override
    public String getMailboxTitle(final CoreSession session, final String mailboxId) {
        if (session == null) {
            return null;
        }

        final String query = String.format(UFNXQLQUERY_MAILBOX_TITLE_FMT, getMailboxType());
        final List<String> titles = QueryUtils.doUFNXQLQueryAndMapping(
            session,
            query,
            new String[] { mailboxId },
            1,
            0,
            new RowMapper<String>() {

                @Override
                public String doMapping(final Map<String, Serializable> rowData) {
                    return (String) rowData.get(KEY_TITLE);
                }
            }
        );

        if (titles.isEmpty()) {
            return null;
        } else {
            return titles.get(0);
        }
    }

    @Override
    public List<Mailbox> getAllMailboxPoste(final CoreSession session) {
        final StringBuilder stringBuilder = new StringBuilder(SELECT_UUID);
        stringBuilder.append(getMailboxType());
        stringBuilder.append(AS_M_WHERE_M);
        stringBuilder.append(MailboxConstants.ID_FIELD);
        stringBuilder.append(" like '%poste%' ");

        try {
            final List<DocumentModel> docList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
                session,
                getMailboxType(),
                stringBuilder.toString(),
                null
            );

            final List<Mailbox> mailboxList = new ArrayList<Mailbox>();
            for (final DocumentModel doc : docList) {
                mailboxList.add(doc.getAdapter(Mailbox.class));
            }
            return mailboxList;
        } catch (final NuxeoException e) {
            LOGGER.error("Erreur de récupération des mailbox poste", e);
            return null;
        }
    }

    @Override
    public Mailbox getUserPersonalMailboxUFNXQL(final CoreSession session, final String user) {
        final String mailboxId = getUserPersonalMailboxId(user);

        final String mailBoxType = getMailboxType();

        final StringBuilder stringBuilder = new StringBuilder(SELECT_UUID);
        stringBuilder.append(mailBoxType);
        stringBuilder.append(AS_M_WHERE_M);
        stringBuilder.append(MailboxConstants.ID_FIELD);
        stringBuilder.append(" = ? ");

        final List<DocumentModel> docs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            mailBoxType,
            stringBuilder.toString(),
            new Object[] { mailboxId },
            1,
            0
        );

        if (docs.isEmpty()) {
            return null;
        } else {
            return docs.get(0).getAdapter(Mailbox.class);
        }
    }

    /////////////////////////////

    @Override
    public DocumentModel getMailboxDocumentModel(CoreSession session, String muid) {
        if (muid == null) {
            return null;
        }

        List<DocumentModel> res = executeQueryModel(session, QUERY_GET_MAILBOX_FROM_ID, new Object[] { muid }, 2);

        if (res == null || res.isEmpty()) {
            return null;
        }

        if (res.size() > 1) {
            LOGGER.warn(String.format("Several mailboxes with id %s, returning first found", muid));
        }

        return res.get(0);
    }

    @Override
    public boolean hasMailbox(CoreSession session, String muid) {
        return Framework.doPrivileged(
            () -> {
                return !getMailboxes(session, Collections.singletonList(muid)).isEmpty();
            }
        );
    }

    @Override
    public List<Mailbox> getMailboxes(CoreSession session, List<String> muids) {
        if (muids == null) {
            return null;
        }

        List<DocumentModel> docs = new ArrayList<DocumentModel>();

        for (String muid : muids) {
            List<DocumentModel> res = executeQueryModel(session, QUERY_GET_MAILBOX_FROM_ID, new Object[] { muid }, 2);
            if (res == null || res.isEmpty()) {
                continue;
            }
            if (res.size() > 1) {
                LOGGER.warn(String.format("Several mailboxes with id %s, returning first found", muid));
            }

            docs.add(res.get(0));
        }
        return MailboxConstants.getMailboxList(docs);
    }

    @Override
    public List<Mailbox> getUserMailboxes(CoreSession session) {
        // return all mailboxes user has access to
        List<DocumentModel> res = executeQueryModel(session, QUERY_GET_ALL_MAILBOX, new Object[] {}, 0);

        List<Mailbox> mailboxes = new ArrayList<Mailbox>();

        // Load all the Mailbox adapters
        if (res != null && !res.isEmpty()) {
            for (DocumentModel mbModel : res) {
                Mailbox mb = mbModel.getAdapter(Mailbox.class);
                mailboxes.add(mb);
            }
        }
        return mailboxes;
    }

    @Override
    public Mailbox getUserPersonalMailbox(CoreSession session, DocumentModel userModel) {
        String mailboxId = getUserPersonalMailboxId(userModel);
        return getMailbox(session, mailboxId);
    }

    @Override
    public Mailbox createPersonalMailboxes(CoreSession session, DocumentModel userModel) {
        // First check if mailbox exists using unrestricted session to
        // avoid creating multiple personal mailboxes for a given user in
        // case there's something wrong with Read rights on mailbox folder
        String muid = getUserPersonalMailboxId(userModel);
        if (hasMailbox(session, muid)) {
            LOGGER.error(
                String.format(
                    "Cannot create personal mailbox for user '%s': " + "it already exists with id '%s'",
                    userModel.getName(),
                    muid
                )
            );
            return getMailbox(session, muid);
        }
        return getPersonalMailboxCreator().createMailboxes(session, userModel);
    }

    @Override
    public Mailbox getUserPersonalMailboxForEmail(CoreSession session, String userEmail) {
        if (userEmail == null || StringUtils.isEmpty(userEmail)) {
            return null;
        }

        UserManager userManager = ServiceUtil.getRequiredService(UserManager.class);
        Directory dir = getDirService().getDirectory(userManager.getUserDirectoryName());

        List<String> userIds = null;

        try (Session dirSession = dir.getSession()) {
            Map<String, Serializable> filter = new HashMap<String, Serializable>();
            filter.put(userManager.getUserEmailField(), userEmail);
            userIds = dirSession.getProjection(filter, dir.getIdField());
        }
        if (userIds != null && !userIds.isEmpty() && !StringUtils.isEmpty(userIds.get(0))) {
            // return first found
            return getUserPersonalMailbox(session, userIds.get(0));
        }

        return null;
    }

    @Override
    public boolean hasUserPersonalMailbox(CoreSession session, DocumentModel userModel) {
        return getUserPersonalMailbox(session, userModel) != null;
    }

    /**
     * Retrieves the Personal Mailbox Id from the Mailbox Creator.
     *
     * @param user
     *            Owner of the mailbox
     * @return The personal Mailbox Id
     */
    @Override
    public String getUserPersonalMailboxId(String user) {
        return getPersonalMailboxCreator().getPersonalMailboxId(user);
    }

    @Override
    public String getUserPersonalMailboxId(DocumentModel userModel) {
        if (userModel == null) {
            LOGGER.debug(String.format("No User by that name. Maybe a wrong id or virtual user"));
            return null;
        }
        return getUserPersonalMailboxId(userModel.getId());
    }

    /**
     * Encapsulates lookup and exception management.
     *
     * @return The DirectoryService, guaranteed not null
     */
    protected DirectoryService getDirService() {
        return ServiceUtil.getRequiredService(DirectoryService.class);
    }

    /**
     * Gets the current page of a page provider
     *
     * @param pageProvider
     *            The name of the page provider
     * @param params
     *            params of the page provider
     * @return the corresponding documentModels
     */
    protected List<DocumentModel> executeQueryModel(
        CoreSession session,
        String pageProvider,
        Object[] params,
        final int nbResult
    ) {
        Map<String, Serializable> props = Collections.singletonMap(CORE_SESSION_PROPERTY, (AbstractSession) session);

        PageProviderService providerService = ServiceUtil.getRequiredService(PageProviderService.class);
        @SuppressWarnings("unchecked")
        PageProvider<DocumentModel> provider = (PageProvider<DocumentModel>) providerService.getPageProvider(
            pageProvider,
            null,
            null,
            Long.valueOf(nbResult),
            0L,
            props,
            params
        );

        return provider.getCurrentPage();
    }

    protected MailboxCreator getPersonalMailboxCreator() {
        if (personalMailboxCreator == null) {
            personalMailboxCreator = STServiceLocator.getMailboxCreator();
        }
        return personalMailboxCreator;
    }

    void setPersonalMailboxCreator(MailboxCreator personalMailboxCreator) {
        this.personalMailboxCreator = personalMailboxCreator;
    }

    private Mailbox toMailbox(DocumentModel doc) {
        return doc.getAdapter(Mailbox.class);
    }
}
