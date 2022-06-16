package fr.dila.solonepp.core.domain.jeton;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.jeton.JetonDoc;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation de la liaison Jeton / document.
 *
 * @author jtremeaux
 */
public class JetonDocImpl extends fr.dila.st.core.jeton.JetonDocImpl implements JetonDoc {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur de JetonDocImpl.
     *
     * @param doc Modèle de document
     */
    public JetonDocImpl(DocumentModel doc) {
        super(doc);
    }

    @Override
    public String getNotificationType() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_TYPE_PROPERTY
        );
    }

    @Override
    public void setNotificationType(String notificationType) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_TYPE_PROPERTY,
            notificationType
        );
    }

    @Override
    public Long getWsRetryLeft() {
        return PropertyUtil.getLongProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_WS_RETRY_LEFT_PROPERTY
        );
    }

    @Override
    public void setWsRetryLeft(Long wsRetryLeft) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_WS_RETRY_LEFT_PROPERTY,
            wsRetryLeft
        );
    }

    @Override
    public String getObjetRefId() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_OBJET_REF_ID_PROPERTY
        );
    }

    @Override
    public void setObjetRefId(String objetRefId) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_OBJET_REF_ID_PROPERTY,
            objetRefId
        );
    }

    @Override
    public String getObjetRefType() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_OBJET_REF_TYPE_PROPERTY
        );
    }

    @Override
    public void setObjetRefType(String objetRefType) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_OBJET_REF_TYPE_PROPERTY,
            objetRefType
        );
    }

    @Override
    public String getEvenementId() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_EVENEMENT_ID_PROPERTY
        );
    }

    @Override
    public void setEvenementId(String evenementId) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_EVENEMENT_ID_PROPERTY,
            evenementId
        );
    }

    @Override
    public String getEvenementType() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_EVENEMENT_TYPE_PROPERTY
        );
    }

    @Override
    public void setEvenementType(String evenementType) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_EVENEMENT_TYPE_PROPERTY,
            evenementType
        );
    }

    @Override
    public String getEvenementLifeCycleState() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_EVENEMENT_LIFE_CYCLE_STATE_PROPERTY
        );
    }

    @Override
    public void setEvenementLifeCycleState(String evenementLifeCycleState) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_EVENEMENT_LIFE_CYCLE_STATE_PROPERTY,
            evenementLifeCycleState
        );
    }

    @Override
    public String getEvenementEmetteur() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_EVENEMENT_EMETTEUR_PROPERTY
        );
    }

    @Override
    public void setEvenementEmetteur(String evenementEmetteur) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_EVENEMENT_EMETTEUR_PROPERTY,
            evenementEmetteur
        );
    }

    @Override
    public String getEvenementDestinataire() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_EVENEMENT_DESTINATAIRE_PROPERTY
        );
    }

    @Override
    public void setEvenementDestinataire(String evenementDestinataire) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_EVENEMENT_DESTINATAIRE_PROPERTY,
            evenementDestinataire
        );
    }

    @Override
    public List<String> getEvenementDestinataireCopie() {
        return PropertyUtil.getStringListProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_EVENEMENT_DESTINATAIRE_COPIE_PROPERTY
        );
    }

    @Override
    public void setEvenementDestinataireCopie(List<String> evenementDestinataireCopie) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_EVENEMENT_DESTINATAIRE_COPIE_PROPERTY,
            evenementDestinataireCopie
        );
    }

    @Override
    public boolean isVersionPresencePieceJointe() {
        return PropertyUtil.getBooleanProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_PRESENCE_PIECE_JOINTE_PROPERTY
        );
    }

    @Override
    public void setVersionPresencePieceJointe(boolean versionPresencePieceJointe) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_PRESENCE_PIECE_JOINTE_PROPERTY,
            versionPresencePieceJointe
        );
    }

    @Override
    public String getVersionLifeCycleState() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_LIFE_CYCLE_STATE_PROPERTY
        );
    }

    @Override
    public void setVersionLifeCycleState(String versionLifeCycleState) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_LIFE_CYCLE_STATE_PROPERTY,
            versionLifeCycleState
        );
    }

    @Override
    public String getVersionObjet() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_OBJET_PROPERTY
        );
    }

    @Override
    public void setVersionObjet(String versionObjet) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_OBJET_PROPERTY,
            versionObjet
        );
    }

    @Override
    public Calendar getVersionHorodatage() {
        return PropertyUtil.getCalendarProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_HORODATAGE_PROPERTY
        );
    }

    @Override
    public void setVersionHorodatage(Calendar versionHorodatage) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_HORODATAGE_PROPERTY,
            versionHorodatage
        );
    }

    @Override
    public String getVersionNiveauLecture() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_NIVEAU_LECTURE_PROPERTY
        );
    }

    @Override
    public void setVersionNiveauLecture(String versionNiveauLecture) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_NIVEAU_LECTURE_PROPERTY,
            versionNiveauLecture
        );
    }

    @Override
    public Long getVersionMajorVersion() {
        return PropertyUtil.getLongProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_MAJOR_VERSION_PROPERTY
        );
    }

    @Override
    public void setVersionMajorVersion(Long versionMajorVersion) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_MAJOR_VERSION_PROPERTY,
            versionMajorVersion
        );
    }

    @Override
    public Long getVersionMinorVersion() {
        return PropertyUtil.getLongProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_MINOR_VERSION_PROPERTY
        );
    }

    @Override
    public void setVersionMinorVersion(Long versionMinorVersion) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_MINOR_VERSION_PROPERTY,
            versionMinorVersion
        );
    }

    @Override
    public String getVersionSenat() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_SENAT_PROPERTY
        );
    }

    @Override
    public void setVersionSenat(String versionSenat) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_SENAT_PROPERTY,
            versionSenat
        );
    }

    @Override
    public String getDossierId() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_DOSSIER_ID_PROPERTY
        );
    }

    @Override
    public void setDossierId(String dossierId) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_DOSSIER_ID_PROPERTY,
            dossierId
        );
    }

    @Override
    public Long getDossierAlerteCount() {
        return PropertyUtil.getLongProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_DOSSIER_ALERTE_COUNT_PROPERTY
        );
    }

    @Override
    public void setDossierAlerteCount(Long dossierAlerteCount) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_DOSSIER_ALERTE_COUNT_PROPERTY,
            dossierAlerteCount
        );
    }

    @Override
    public List<String> getMessageCorbeilleList() {
        return PropertyUtil.getStringListProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_MESSAGE_CORBEILLE_LIST_PROPERTY
        );
    }

    @Override
    public void setMessageCorbeilleList(List<String> messageCorbeilleList) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_MESSAGE_CORBEILLE_LIST_PROPERTY,
            messageCorbeilleList
        );
    }

    @Override
    public String getMessageLifeCycleState() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_MESSAGE_LIFE_CYCLE_STATE_PROPERTY
        );
    }

    @Override
    public void setMessageLifeCycleState(String messageLifeCycleState) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_MESSAGE_LIFE_CYCLE_STATE_PROPERTY,
            messageLifeCycleState
        );
    }

    @Override
    public String getMessageType() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_MESSAGE_TYPE_PROPERTY
        );
    }

    @Override
    public void setMessageType(String messageType) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_MESSAGE_TYPE_PROPERTY,
            messageType
        );
    }

    @Override
    public boolean isMessageArNecessaire() {
        return PropertyUtil.getBooleanProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_MESSAGE_AR_NECESSAIRE_PROPERTY
        );
    }

    @Override
    public void setMessageArNecessaire(boolean messageArNecessaire) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_MESSAGE_AR_NECESSAIRE_PROPERTY,
            messageArNecessaire
        );
    }

    @Override
    public Long getMessageArNonDonneCount() {
        return PropertyUtil.getLongProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_MESSAGE_AR_NON_DONNE_COUNT_PROPERTY
        );
    }

    @Override
    public void setMessageArNonDonneCount(Long messageArNonDonneCount) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_MESSAGE_AR_NON_DONNE_COUNT_PROPERTY,
            messageArNonDonneCount
        );
    }

    @Override
    public Long getVersionNiveauLectureNumero() {
        return PropertyUtil.getLongProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_NIVEAU_LECTURE_NUMERO_PROPERTY
        );
    }

    @Override
    public void setVersionNiveauLectureNumero(Long versionNiveauLectureNumero) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_DOCUMENT_SCHEMA,
            SolonEppSchemaConstant.JETON_DOC_VERSION_NIVEAU_LECTURE_NUMERO_PROPERTY,
            versionNiveauLectureNumero
        );
    }
}
