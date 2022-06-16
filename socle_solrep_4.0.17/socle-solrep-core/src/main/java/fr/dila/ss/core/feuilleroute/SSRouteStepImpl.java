package fr.dila.ss.core.feuilleroute;

import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.eltrunner.ElementRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.adapter.FeuilleRouteElementImpl;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation d'une étape de feuille de route du socle transverse.
 *
 * @author jtremeaux
 */
public abstract class SSRouteStepImpl extends FeuilleRouteElementImpl implements SSRouteStep {
    /**
     * UID.
     */
    private static final long serialVersionUID = -7177633331090519475L;

    /**
     * Constructeur de ReponsesRouteStepImpl.
     *
     * @param doc
     *            Modèle de document
     * @param runner
     *            Exécuteur d'étape
     */
    public SSRouteStepImpl(DocumentModel doc, ElementRunner runner) {
        super(doc, runner);
    }

    /**
     * Retourne le type d'action.
     *
     * @return Type d'action
     */
    @Override
    public String getType() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_TYPE_PROPERTY
        );
    }

    /**
     * Renseigne le type d'action.
     *
     * @param type
     *            Type d'action
     */
    @Override
    public void setType(String type) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_TYPE_PROPERTY,
            type
        );
    }

    /**
     * Retourne l'identifiant technique de la Mailbox de distribution.
     *
     * @return Identifiant technique de la Mailbox de distribution
     */
    @Override
    public String getDistributionMailboxId() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_MAILBOX_ID_PROPERTY
        );
    }

    /**
     * Renseigne l'identifiant technique de la Mailbox de distribution.
     *
     * @param distributionMailboxId
     *            Identifiant technique de la Mailbox de distribution
     */
    @Override
    public void setDistributionMailboxId(String distributionMailboxId) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_MAILBOX_ID_PROPERTY,
            distributionMailboxId
        );
    }

    /**
     * Retourne l'UUID de la feuille de route (champ dénormalisé).
     *
     * @return UUID de la feuille de route
     */
    @Override
    public String getDocumentRouteId() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY
        );
    }

    /**
     * Renseigne l'UUID de la feuille de route (champ dénormalisé).
     *
     * @param documentRouteId
     *            UUID de la feuille de route
     */
    @Override
    public void setDocumentRouteId(String documentRouteId) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY,
            documentRouteId
        );
    }

    /**
     * Retourne la date d'échéance.
     *
     * @return Date d'échéance
     */
    @Override
    public Calendar getDueDate() {
        return PropertyUtil.getCalendarProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DUE_DATE_PROPERTY
        );
    }

    /**
     * Renseigne la date d'échéance.
     *
     * @param dueDate
     *            Date d'échéance
     */
    @Override
    public void setDueDate(Calendar dueDate) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DUE_DATE_PROPERTY,
            dueDate
        );
    }

    /**
     * Retourne la date d'échéance indicative.
     *
     * @return Date d'échéance indicative
     */
    @Override
    public Long getDeadLine() {
        return PropertyUtil.getLongProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DEADLINE_PROPERTY
        );
    }

    /**
     * Renseigne la date d'échéance indicative.
     *
     * @param dueDate
     *            Date d'échéance indicative
     */
    @Override
    public void setDeadLine(Long deadline) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DEADLINE_PROPERTY,
            deadline
        );
    }

    /**
     * Retourne vrai si l'étape est validée automatiquement à la date d'échéance.
     *
     * @return Vrai si l'étape est validée automatiquement à la date d'échéance
     */
    @Override
    public boolean isAutomaticValidation() {
        return PropertyUtil.getBooleanProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_AUTOMATIC_VALIDATION_PROPERTY
        );
    }

    /**
     * Renseigne si l'étape est validée automatiquement à la date d'échéance.
     *
     * @param automaticValidation
     *            Vrai si l'étape est validée automatiquement à la date d'échéance
     */
    @Override
    public void setAutomaticValidation(boolean automaticValidation) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_AUTOMATIC_VALIDATION_PROPERTY,
            automaticValidation
        );
    }

    /**
     * Retourne l'état de validation de l'étape.
     *
     * @return État de validation de l'étape
     */
    @Override
    public String getValidationStatus() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_PROPERTY
        );
    }

    /**
     * Renseigne l'état de validation de l'étape.
     *
     * @param validationStatus
     *            État de validation de l'étape
     */
    @Override
    public void setValidationStatus(String validationStatus) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_PROPERTY,
            validationStatus
        );
    }

    /**
     * Retourne vrai si l'étape a été dupliquée.
     *
     * @return Vrai si l'étape a été dupliquée
     */
    @Override
    public boolean isAlreadyDuplicated() {
        return PropertyUtil.getBooleanProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_ALREADY_DUPLICATED_PROPERTY
        );
    }

    /**
     * Renseigne si l'étape a été dupliquée.
     *
     * @param alreadyDuplicated
     *            Vrai si l'étape a été dupliquée
     */
    @Override
    public void setAlreadyDuplicated(boolean alreadyDuplicated) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_ALREADY_DUPLICATED_PROPERTY,
            alreadyDuplicated
        );
    }

    /**
     * Retourne vrai si l'étape a été validée automatiquement.
     *
     * @return Vrai si l'étape a été validée automatiquement
     */
    @Override
    public boolean isAutomaticValidated() {
        return PropertyUtil.getBooleanProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_AUTOMATIC_VALIDATED_PROPERTY
        );
    }

    /**
     * Renseigne si l'étape a été validée automatiquement.
     *
     * @param automaticValidated
     *            Vrai si l'étape a été validée automatiquement
     */
    @Override
    public void setAutomaticValidated(boolean automaticValidated) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_AUTOMATIC_VALIDATED_PROPERTY,
            automaticValidated
        );
    }

    /**
     * Détermine si un mail a déja été envoyé à la date d'échéance.
     *
     * @return Vrai si un mail a déja été envoyé à la date d'échéance
     */
    @Override
    public boolean isMailSend() {
        return PropertyUtil.getBooleanProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_IS_MAIL_SEND_PROPERTY
        );
    }

    /**
     * Renseigne si un mail a déja été envoyé à la date d'échéance.
     *
     * @param isMailSend
     *            Vrai si un mail a déja été envoyé à la date d'échéance
     */
    @Override
    public void setMailSend(boolean isMailSend) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_IS_MAIL_SEND_PROPERTY,
            isMailSend
        );
    }

    @Override
    public boolean isObligatoire() {
        return isObligatoireSGG() || isObligatoireMinistere();
    }

    @Override
    public boolean isObligatoireSGG() {
        return PropertyUtil.getBooleanProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_SGG_PROPERTY
        );
    }

    @Override
    public void setObligatoireSGG(boolean obligatoireSGG) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_SGG_PROPERTY,
            obligatoireSGG
        );
    }

    @Override
    public boolean isObligatoireMinistere() {
        return PropertyUtil.getBooleanProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_MINISTERE_PROPERTY
        );
    }

    @Override
    public void setObligatoireMinistere(boolean obligatoireMinistere) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_MINISTERE_PROPERTY,
            obligatoireMinistere
        );
    }

    @Override
    public Calendar getDateDebutEtape() {
        return PropertyUtil.getCalendarProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DATE_DEBUT_ETAPE_PROPERTY
        );
    }

    @Override
    public void setDateDebutEtape(Calendar dateDebutEtape) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DATE_DEBUT_ETAPE_PROPERTY,
            dateDebutEtape
        );
    }

    @Override
    public Calendar getDateFinEtape() {
        return PropertyUtil.getCalendarProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DATE_FIN_ETAPE_PROPERTY
        );
    }

    @Override
    public void setDateFinEtape(Calendar dateFinEtape) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DATE_FIN_ETAPE_PROPERTY,
            dateFinEtape
        );
    }

    @Override
    public String getMinistereLabel() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_MINISTERE_LABEL_PROPERTY
        );
    }

    @Override
    public void setMinistereLabel(String ministereLabel) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_MINISTERE_LABEL_PROPERTY,
            ministereLabel
        );
    }

    @Override
    public String getMinistereId() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_MINISTERE_ID_PROPERTY
        );
    }

    @Override
    public void setMinistereId(String ministereId) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_MINISTERE_ID_PROPERTY,
            ministereId
        );
    }

    @Override
    public String getDirectionLabel() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DIRECTION_LABEL_PROPERTY
        );
    }

    @Override
    public void setDirectionLabel(String directionLabel) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DIRECTION_LABEL_PROPERTY,
            directionLabel
        );
    }

    @Override
    public String getDirectionId() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DIRECTION_ID_PROPERTY
        );
    }

    @Override
    public void setDirectionId(String directionId) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_DIRECTION_ID_PROPERTY,
            directionId
        );
    }

    @Override
    public String getPosteLabel() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_POSTE_LABEL_PROPERTY
        );
    }

    @Override
    public void setPosteLabel(String posteLabel) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_POSTE_LABEL_PROPERTY,
            posteLabel
        );
    }

    @Override
    public String getValidationUserLabel() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_USER_LABEL_PROPERTY
        );
    }

    @Override
    public void setValidationUserLabel(String validationUserLabel) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_USER_LABEL_PROPERTY,
            validationUserLabel
        );
    }

    @Override
    public String getValidationUserId() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_USER_ID_PROPERTY
        );
    }

    @Override
    public void setValidationUserId(String validationUserId) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_USER_ID_PROPERTY,
            validationUserId
        );
    }

    @Override
    public void setCreator(String creator) {
        DublincoreSchemaUtils.setCreator(document, creator);
    }

    @Override
    public void setNumberOfComments(int numberOfComments) {
        PropertyUtil.setProperty(
            getDocument(),
            STSchemaConstant.INFO_COMMENTS_SCHEMA,
            STSchemaConstant.INFO_COMMMENTS_NUMBER_OF_COMMENTS_PROPERTY,
            numberOfComments
        );
    }

    @Override
    public int getNumberOfComments() {
        Long value = PropertyUtil.getLongProperty(
            getDocument(),
            STSchemaConstant.INFO_COMMENTS_SCHEMA,
            STSchemaConstant.INFO_COMMMENTS_NUMBER_OF_COMMENTS_PROPERTY
        );
        return value.intValue();
    }

    @Override
    public boolean isEtapeAvenir() {
        return isDraft() || isValidated() || isReady();
    }

    @Override
    public boolean isValideManuellement() {
        return (
            isDone() &&
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE.equals(getValidationStatus())
        );
    }

    @Override
    public boolean isInvalide() {
        return (
            isDone() &&
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_DEFAVORABLE_VALUE.equals(getValidationStatus())
        );
    }

    @Override
    public boolean isValidationAuto() {
        return (
            isDone() &&
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_VALIDE_AUTOMATIQUEMENT_VALUE.equals(
                getValidationStatus()
            )
        );
    }

    @Override
    public boolean isNonConcerne() {
        return (
            isDone() &&
            SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_NON_CONCERNE_VALUE.equals(getValidationStatus())
        );
    }

    @Override
    public String getCurrentLifeCycleState() {
        return document.getCurrentLifeCycleState();
    }
}
