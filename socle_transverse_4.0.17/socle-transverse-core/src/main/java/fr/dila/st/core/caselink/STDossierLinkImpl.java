package fr.dila.st.core.caselink;

import static fr.dila.cm.caselink.CaseLinkConstants.CASE_DOCUMENT_ID_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.COMMENT_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.DATE_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.IS_READ_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.SENDER_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.SENDER_MAILBOX_ID_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.SENT_DATE_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.SUBJECT_FIELD;

import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.cm.cases.HasParticipants;
import fr.dila.cm.cases.HasParticipantsImpl;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.dossier.STDossier;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

/**
 * Implémentation du Dossier link dans le socle transverse
 *
 * @author ARN
 */
public abstract class STDossierLinkImpl implements STDossierLink {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 7702130784858564652L;

    protected HasParticipants recipientAdapter;

    protected DocumentModel document;

    /**
     * Constructeur de STDossierLinkImpl.
     *
     * @param doc
     *            Document
     */
    public STDossierLinkImpl(DocumentModel doc) {
        document = doc;
        recipientAdapter = new HasParticipantsImpl(doc);
    }

    @Override
    public String getDistributionMailbox() {
        Map<String, List<String>> participantsMap = getInitialInternalParticipants();
        if (participantsMap != null) {
            // Retourne le premier participant, on n'en utilise qu'un dans SOLON
            for (List<String> participantList : participantsMap.values()) {
                return participantList.iterator().next();
            }
        }
        return null;
    }

    @Override
    public String getDossierId() {
        return PropertyUtil.getStringProperty(
            document,
            CaseLinkConstants.CASE_LINK_SCHEMA,
            "caseDocumentId"
            /*
             * CaseLinkConstants
             * .
             * CASE_DOCUMENT_ID_FIELD
             */
        );
    }

    @Override
    public String getRoutingTaskId() {
        return getStringProperty(getDossierLinkSchema(), STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_ID_PROPERTY);
    }

    @Override
    public void setRoutingTaskId(String routingTaskId) {
        setProperty(getDossierLinkSchema(), STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_ID_PROPERTY, routingTaskId);
    }

    @Override
    public String getRoutingTaskType() {
        return getStringProperty(getDossierLinkSchema(), STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_TYPE_PROPERTY);
    }

    @Override
    public void setRoutingTaskType(String routingTaskType) {
        setProperty(
            getDossierLinkSchema(),
            STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_TYPE_PROPERTY,
            routingTaskType
        );
    }

    @Override
    public String getRoutingTaskLabel() {
        return getStringProperty(
            getDossierLinkSchema(),
            STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_LABEL_PROPERTY
        );
    }

    @Override
    public void setRoutingTaskLabel(String routingTaskLabel) {
        setProperty(
            getDossierLinkSchema(),
            STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_LABEL_PROPERTY,
            routingTaskLabel
        );
    }

    @Override
    public String getRoutingTaskMailboxLabel() {
        return getStringProperty(
            getDossierLinkSchema(),
            STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_MAILBOX_LABEL_PROPERTY
        );
    }

    @Override
    public void setRoutingTaskMailboxLabel(String routingTaskMailboxLabel) {
        setProperty(
            getDossierLinkSchema(),
            STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_MAILBOX_LABEL_PROPERTY,
            routingTaskMailboxLabel
        );
    }

    // ///////////////////////////////////////
    // CurrentStepProperties Method
    // ///////////////////////////////////////

    @Override
    public Boolean getCurrentStepIsMailSendProperty() {
        return getBooleanProperty(getDossierLinkSchema(), STDossierLinkConstant.DOSSIER_LINK_IS_MAIL_SEND_PROPERTY);
    }

    @Override
    public void setCurrentStepIsMailSendProperty(Boolean currentStepIsMailSendProperty) {
        setProperty(
            getDossierLinkSchema(),
            STDossierLinkConstant.DOSSIER_LINK_IS_MAIL_SEND_PROPERTY,
            currentStepIsMailSendProperty
        );
    }

    @Override
    public void setReadState(Boolean isRead) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.CASE_LINK_SCHEMA,
            STSchemaConstant.CASE_LINK_IS_READ_PROPERTY,
            isRead
        );
    }

    /**
     * Récupère le schéma du dossier link
     */
    protected abstract String getDossierLinkSchema();

    //////////////////////////////////////////

    public String getComment() {
        return getPropertyValue(COMMENT_FIELD);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getPropertyValue(String value) {
        return (T) document.getPropertyValue(value);
    }

    public Calendar getDate() {
        return getPropertyValue(DATE_FIELD);
    }

    @Override
    public <T extends STDossier> T getDossier(CoreSession session, Class<T> adapterClazz) {
        String envelopeDocumentId = (String) document.getPropertyValue(CASE_DOCUMENT_ID_FIELD);
        DocumentModel mailDocument = session.getDocument(new IdRef(envelopeDocumentId));
        return mailDocument.getAdapter(adapterClazz);
    }

    public String getId() {
        return document.getId();
    }

    public String getSender() {
        return getPropertyValue(SENDER_FIELD);
    }

    public String getSubject() {
        return getPropertyValue(SUBJECT_FIELD);
    }

    public String getSenderMailboxId() {
        return getPropertyValue(SENDER_MAILBOX_ID_FIELD);
    }

    public Date getSentDate() {
        return getPropertyValue(SENT_DATE_FIELD);
    }

    public boolean isRead() {
        return (Boolean) getPropertyValue(IS_READ_FIELD);
    }

    public void save(CoreSession session) {
        session.saveDocument(document);
    }

    public DocumentModel getDocument() {
        return document;
    }

    public void addInitialExternalParticipants(Map<String, List<String>> recipients) {
        recipientAdapter.addInitialExternalParticipants(recipients);
    }

    public void addInitialInternalParticipants(Map<String, List<String>> recipients) {
        recipientAdapter.addInitialInternalParticipants(recipients);
    }

    public void addParticipants(Map<String, List<String>> recipients) {
        recipientAdapter.addParticipants(recipients);
    }

    public Map<String, List<String>> getAllParticipants() {
        return recipientAdapter.getAllParticipants();
    }

    public Map<String, List<String>> getInitialExternalParticipants() {
        return recipientAdapter.getInitialExternalParticipants();
    }

    public Map<String, List<String>> getInitialInternalParticipants() {
        return recipientAdapter.getInitialInternalParticipants();
    }

    @Override
    public void setActionnable(boolean actionnable) {
        document.setPropertyValue(CaseLinkConstants.IS_ACTIONABLE_FIELD, actionnable);
    }

    @Override
    public boolean isActionnable() {
        return (Boolean) getPropertyValue(CaseLinkConstants.IS_ACTIONABLE_FIELD);
    }

    @Override
    public Calendar getDateDebutValidation() {
        return (Calendar) getPropertyValue(CaseLinkConstants.DATE_FIELD_DEBUT_VALIDATION);
    }

    @Override
    public void setDateDebutValidation(Calendar dateDebut) {
        document.setPropertyValue(CaseLinkConstants.DATE_FIELD_DEBUT_VALIDATION, dateDebut);
    }

    // ///////////////////////////////////////
    // Generic property getter & setter
    // ///////////////////////////////////////

    protected String getStringProperty(String schema, String property) {
        return PropertyUtil.getStringProperty(document, schema, property);
    }

    protected List<String> getListStringProperty(String schema, String property) {
        return PropertyUtil.getStringListProperty(document, schema, property);
    }

    protected Long getLongProperty(String schema, String property) {
        return PropertyUtil.getLongProperty(document, schema, property);
    }

    protected void setProperty(String schema, String property, Object value) {
        PropertyUtil.setProperty(document, schema, property, value);
    }

    protected Calendar getDateProperty(String schema, String property) {
        return PropertyUtil.getCalendarProperty(document, schema, property);
    }

    protected Boolean getBooleanProperty(String schema, String property) {
        return PropertyUtil.getBooleanProperty(document, schema, property);
    }
}
