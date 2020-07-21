package fr.dila.solonepp.core.domain.message;

import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation des Mailbox de l'application SOLON EPP.
 * 
 * @author jtremeaux
 */
public class MessageImpl implements Message {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 8820645351060312648L;

    /**
     * Modèle de document.
     */
    protected DocumentModel document;

    /**
     * Constructeur de MessageImpl.
     * 
     * @param document Modèle de document
     */
    public MessageImpl(DocumentModel document) {
        this.document = document;
    }

    // *************************************************************
    // Propriété de Nuxeo (schéma case_link)
    // *************************************************************
    @Override
    public String getTitle() {
    	return DublincoreSchemaUtils.getTitle(document);
    }

    @Override
    public void setTitle(String title) {
    	DublincoreSchemaUtils.setTitle(document, title);
    }

    @Override
    public boolean isDraft() {
        return PropertyUtil.getBooleanProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_DRAFT_PROPERTY);
    }

    @Override
    public void setDraft(boolean draft) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_DRAFT_PROPERTY, draft);
    }

    @Override
    public String getCaseRepositoryName() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_CASE_REPOSITORY_NAME_PROPERTY);
    }

    @Override
    public void setCaseRepositoryName(String caseRepositoryName) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_CASE_REPOSITORY_NAME_PROPERTY,
                caseRepositoryName);
    }

    @Override
    public String getCaseDocumentId() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_CASE_DOCUMENT_ID_PROPERTY);
    }

    @Override
    public void setCaseDocumentId(String caseDocumentId) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_CASE_DOCUMENT_ID_PROPERTY, caseDocumentId);
    }

    @Override
    public String getSenderMailboxId() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_SENDER_MAILBOX_ID_PROPERTY);
    }

    @Override
    public void setSenderMailboxId(String senderMailboxId) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_SENDER_MAILBOX_ID_PROPERTY, senderMailboxId);
    }

    @Override
    public Calendar getDate() {
        return PropertyUtil.getCalendarProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_DATE_PROPERTY);
    }

    @Override
    public void setDate(Calendar date) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_DATE_PROPERTY, date);
    }

    @Override
    public String getSender() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_SENDER_PROPERTY);
    }

    @Override
    public void setSender(String sender) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_SENDER_PROPERTY, sender);
    }

    @Override
    public boolean getIsSent() {
        return PropertyUtil.getBooleanProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_IS_SENT_PROPERTY);
    }

    @Override
    public void setIsSent(boolean isSent) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_IS_SENT_PROPERTY, isSent);
    }

    // *************************************************************
    // Propriétés du message
    // *************************************************************
    @Override
    public String getMessageType() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_PROPERTY);
    }

    @Override
    public void setMessageType(String messageType) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_PROPERTY, messageType);
    }

    @Override
    public String getActiveVersionId() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.CASE_LINK_SCHEMA,
                SolonEppSchemaConstant.CASE_LINK_ACTIVE_VERSION_ID_PROPERTY);
    }

    @Override
    public void setActiveVersionId(String activeVersionId) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.CASE_LINK_ACTIVE_VERSION_ID_PROPERTY,
                activeVersionId);
    }

    @Override
    public List<String> getCorbeilleList() {
        return PropertyUtil.getStringListProperty(document, STSchemaConstant.CASE_LINK_SCHEMA,
                SolonEppSchemaConstant.CASE_LINK_CORBEILLE_LIST_PROPERTY);
    }

    @Override
    public void setCorbeilleList(List<String> corbeilleList) {
        PropertyUtil
                .setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.CASE_LINK_CORBEILLE_LIST_PROPERTY, corbeilleList);
    }

    @Override
    public Calendar getDateTraitement() {
        return PropertyUtil.getCalendarProperty(document, STSchemaConstant.CASE_LINK_SCHEMA,
                SolonEppSchemaConstant.CASE_LINK_DATE_TRAITEMENT_PROPERTY);
    }

    @Override
    public void setDateTraitement(Calendar dateTraitement) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.CASE_LINK_DATE_TRAITEMENT_PROPERTY,
                dateTraitement);
    }

    @Override
    public String getEtatMessage() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.CASE_LINK_ETAT_MESSAGE_PROPERTY);
    }

    @Override
    public void setEtatMessage(String etatMessage) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.CASE_LINK_ETAT_MESSAGE_PROPERTY, etatMessage);
    }

    // *************************************************************
    // Propriétés du message de l'émetteur uniquement
    // *************************************************************
    @Override
    public boolean isArNecessaire() {
        return PropertyUtil.getBooleanProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.CASE_LINK_AR_NECESSAIRE_PROPERTY);
    }

    @Override
    public void setArNecessaire(boolean arNecessaire) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.CASE_LINK_AR_NECESSAIRE_PROPERTY, arNecessaire);
    }

    @Override
    public long getArNonDonneCount() {
        return PropertyUtil
                .getLongProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.CASE_LINK_AR_NON_DONNE_COUNT_PROPERTY);
    }

    @Override
    public void setArNonDonneCount(long arNonDonneCount) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.CASE_LINK_AR_NON_DONNE_COUNT_PROPERTY,
                arNonDonneCount);
    }

    // *************************************************************
    // Propriétés calculées sur l'état du cycle de vie.
    // *************************************************************
    @Override
    public boolean isEtatNonTraite() {
        return SolonEppLifecycleConstant.MESSAGE_NON_TRAITE_STATE.equals(PropertyUtil.getCurrentLifeCycleState(document));
    }

    @Override
    public boolean isEtatEnCours() {
        return SolonEppLifecycleConstant.MESSAGE_EN_COURS_STATE.equals(PropertyUtil.getCurrentLifeCycleState(document));
    }

    @Override
    public boolean isEtatTraite() {
        return SolonEppLifecycleConstant.MESSAGE_TRAITE_STATE.equals(PropertyUtil.getCurrentLifeCycleState(document));
    }

    // *************************************************************
    // Propriétés calculées sur le type de message.
    // *************************************************************
    @Override
    public boolean isTypeEmetteur() {
        return SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE.equals(getMessageType());
    }

    @Override
    public boolean isTypeDestinataire() {
        return SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE.equals(getMessageType());
    }

    @Override
    public boolean isTypeCopie() {
        return SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE.equals(getMessageType());
    }

    @Override
    public void setVisaInternes(List<String> visaInternes) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.CASE_LINK_VISA_INTERNES_PROPERTY, visaInternes);
    }

    @Override
    public List<String> getVisaInternes() {
        return PropertyUtil.getStringListProperty(document, STSchemaConstant.CASE_LINK_SCHEMA,
                SolonEppSchemaConstant.CASE_LINK_VISA_INTERNES_PROPERTY);
    }

    @Override
    public DocumentModel getDocument() {
        return document;
    }

    @Override
    public void setIdEvenement(String idEvenement) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.ID_EVENEMENT, idEvenement);
    }
    
    @Override
    public String getIdEvenement() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.ID_EVENEMENT);
    }

    @Override
    public void setIdDossier(String idDossier) {
        PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.ID_DOSSIER, idDossier);

    }

    @Override
    public String getIdDossier() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.CASE_LINK_SCHEMA, SolonEppSchemaConstant.ID_DOSSIER);
    }
}
