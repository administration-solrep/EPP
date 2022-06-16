package fr.dila.st.api.constant;

public final class STAlertConstant {
    public static final String ALERT_DOCUMENT_TYPE = "Alert";

    public static final String ALERT_SHEMA_PREFIX = "altr";

    public static final String ALERT_SCHEMA = "alert";

    public static final String ALERT_EVENT = "sendAlertEvent";

    public static final String ALERT_CONFIRMATION_EVENT = "sendAlertConfirmationEvent";

    public static final String SCHEDULE_ALERT_PROP = "alert";

    public static final String SCHEDULE_REQUETE_PROP = "alert";

    public static final String ALERT_PROP_IS_ACTIVATED = "altr:isActivated";

    public static final String ALERT_IS_ACTIVATED = "isActivated";

    public static final String ALERT_DATE_VALIDITY_BEGIN = "dateValidityBegin";

    public static final String ALERT_DATE_VALIDITY_END = "dateValidityEnd";

    public static final String ALERT_PERIODICITY = "periodicity";

    public static final String ALERT_RECIPIENTS = "recipients";

    public static final String ALERT_REQUETE_ID = "requeteId";

    public static final String ALERT_EXTERNAL_RECIPIENTS = "externalRecipients";

    /*
     * Propriétés liées aux demandes de confirmations de l'alerte
     */

    public static final String ALERT_DATE_DEMANDE_CONFIRMATION = "dateDemandeConfirmation";

    public static final String ALERT_PROP_DATE_DEMANDE_CONFIRMATION = "altr:dateDemandeConfirmation";

    public static final String ALERT_HAS_DEMANDE_CONFIRMATION = "hasDemandeConfirmation";

    public static final String ALERT_PROP_HAS_DEMANDE_CONFIRMATION = "altr:hasDemandeConfirmation";

    public static final String ALERT_CONFIRM_EVENT = "confirmAlertEvent";

    /**
     * utility class
     */
    private STAlertConstant() {
        // do nothing
    }
}
