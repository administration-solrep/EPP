package fr.dila.cm.core.caselink;

import static fr.dila.cm.caselink.CaseLinkConstants.CASE_DOCUMENT_ID_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.COMMENT_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.DATE_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.SENDER_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.SENDER_MAILBOX_ID_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.SUBJECT_FIELD;
import static fr.dila.st.api.constant.STSchemaConstant.ACTIONABLE_CASE_LINK_STEP_DOCUMENT_ID_XPATH;
import static fr.dila.st.core.service.STServiceLocator.getWorkManager;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseManagementDocumentTypeService;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.core.caselink.RemoveDuplicateCaseLinksForCaseWork;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * A creator of {@link CaseLink}.
 *
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 */
public class CreateCaseLink {
    private static final Log LOG = LogFactory.getLog(CreateCaseLink.class);

    private static final String QUERY_NB_DOSSIER_LINKS =
        "Select count(d.acslk:stepDocumentId) as count From DossierLink AS d WHERE d.cslk:caseDocumentId = ? AND d.acslk:stepDocumentId = ? GROUP BY d.cslk:caseDocumentId, d.acslk:stepDocumentId";

    protected CoreSession session;

    protected STDossierLink createdPost;

    protected final String subject;

    protected final String comment;

    protected final STDossier envelope;

    protected final Mailbox sender;

    protected final String recipientId;

    protected final Map<String, List<String>> internalRecipients;

    protected final Map<String, List<String>> externalRecipients;

    protected STDossierLink draft;

    protected Mailbox recipient;

    protected String validateId;

    protected String refuseId;

    protected String stepId;

    public STDossierLink getCreatedPost() {
        return createdPost;
    }

    /**
     * @param draft
     *            The draft used to sent this envelope
     * @param session
     * @param subject
     *            The subject of the post.
     * @param comment
     *            The comment of the post.
     * @param envelope
     *            The envelope sent.
     * @param sender
     *            The mailbox of the sender.
     * @param internalRecipients
     *            A map of recipients keyed by type of Message and keyed with a list of mailboxes.
     * @param isInitial
     *            Is it an initial sent?
     */
    public CreateCaseLink(
        STDossierLink draft,
        CoreSession session,
        String subject,
        String comment,
        STDossier envelope,
        Mailbox sender,
        String recipientId,
        Map<String, List<String>> internalRecipients,
        Map<String, List<String>> externalRecipients
    ) {
        this.draft = draft;
        this.comment = comment;
        this.envelope = envelope;
        this.subject = subject;
        this.sender = sender;
        this.recipientId = recipientId;
        this.internalRecipients = internalRecipients;
        this.externalRecipients = externalRecipients;

        this.session = session;
    }

    public void create() {
        String dossierId = envelope.getDocument().getId();
        String stepDocId = (String) draft.getDocument().getPropertyValue(ACTIONABLE_CASE_LINK_STEP_DOCUMENT_ID_XPATH);
        long nbLinks = getNbDossierLinksForCaseAndStep(dossierId, stepDocId);
        if (nbLinks > 0L) {
            LOG.error(
                String.format(
                    "ERREUR DOUBLON DOSSIER LINK : Un dossier link existe déjà pour le dossier %s et l'étape %s. Création annulée. StackTrace : %s",
                    dossierId,
                    stepDocId,
                    ExceptionUtils.getStackTrace(new NuxeoException())
                )
            );
            return;
        }

        final MailboxService mailboxService = STServiceLocator.getMailboxService();
        Mailbox mailbox = mailboxService.getMailbox(session, recipientId);
        if (mailbox == null) {
            throw new NuxeoException("Can't send post because sender mailbox does not exist.");
        }

        recipient = mailbox;
        final CaseManagementDocumentTypeService correspDocumentTypeService = ServiceUtil.getRequiredService(
            CaseManagementDocumentTypeService.class
        );

        DocumentModel doc = session.createDocumentModel(
            recipient.getDocument().getPathAsString(),
            UUID.randomUUID().toString(),
            correspDocumentTypeService.getCaseLinkType()
        );

        if (draft != null) {
            doc.copyContent(draft.getDocument());
        }
        STDossierLink post = doc.getAdapter(STDossierLink.class);
        post.setActionnable(true);
        doc.putContextData(
            LifeCycleConstants.INITIAL_LIFECYCLE_STATE_OPTION_NAME,
            STDossierLink.CaseLinkState.todo.name()
        );

        post.addParticipants(internalRecipients);
        post.addParticipants(externalRecipients);
        setPostValues(doc);
        doc = session.createDocument(doc);
        getWorkManager().schedule(new RemoveDuplicateCaseLinksForCaseWork(dossierId), true);
        if (LOG.isTraceEnabled()) {
            LOG.trace("CreateCaseLink : created : " + doc.getId());
        }
        createdPost = doc.getAdapter(STDossierLink.class);
    }

    /**
     * Sets the values of the document.
     */
    protected void setPostValues(DocumentModel doc) {
        doc.setPropertyValue(SUBJECT_FIELD, subject);
        doc.setPropertyValue(CASE_DOCUMENT_ID_FIELD, envelope.getDocument().getId());
        if (sender != null) {
            doc.setPropertyValue(SENDER_MAILBOX_ID_FIELD, sender.getId());
            doc.setPropertyValue(SENDER_FIELD, sender.getOwner());
        }
        doc.setPropertyValue(DATE_FIELD, Calendar.getInstance().getTime());
        doc.setPropertyValue(COMMENT_FIELD, comment);
    }

    private long getNbDossierLinksForCaseAndStep(String dossierId, String stepId) {
        Objects.requireNonNull(dossierId);
        Objects.requireNonNull(stepId);

        List<Serializable> data = QueryUtils.doUFNXQLQueryAndMapping(
            session,
            QUERY_NB_DOSSIER_LINKS,
            new String[] { dossierId, stepId },
            (Map<String, Serializable> rowData) -> rowData.get("count")
        );

        if (data.isEmpty()) {
            return 0L;
        }

        return Long.valueOf((String) data.get(0));
    }
}
