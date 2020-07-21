package fr.dila.solonepp.api.constant;

/**
 * Paramètres applicatifs administrables de l'application SOLON EPP.
 * 
 * @author jtremeaux
 */
public final class SolonEppParametreConstant {
    /**
     * Mail de notification des tables de références (objet).
     */
    public static final String MAIL_NOTIFICATION_TABLE_REFERENCE_OBJET = "mail-notification-table-reference-objet";

    /**
     * Mail de notification des tables de références (corps).
     */
    public static final String MAIL_NOTIFICATION_TABLE_REFERENCE_CORPS = "mail-notification-table-reference-corps";

    /**
     * Mail de notification des événements (objet).
     */
    public static final String MAIL_NOTIFICATION_EVENEMENT_OBJET = "mail-notification-evenement-objet";

    /**
     * Mail de notification des événements (corps).
     */
    public static final String MAIL_NOTIFICATION_EVENEMENT_CORPS = "mail-notification-evenement-corps";

    /**
     * Mail de notification des erreurs de notification (objet).
     */
    public static final String MAIL_NOTIFICATION_ERROR_OBJET = "mail-notification-error-objet";

    /**
     * Mail de notification des erreurs de notification (corps).
     */
    public static final String MAIL_NOTIFICATION_ERROR_CORPS = "mail-notification-error-corps";
    
    /**
     * Mail transmettre événement par mèl (corps).
     */
    public static final String MAIL_TRANSMETTRE_EVENEMENT_CORPS = "mail-transmettre-evenement-corps";
    
    
    /**
     * Nombre de jours d'affichage des messages
     */
    public static final String NB_JOUR_MESSAGE_AFFICHABLE = "nb-jour-message-affichable";

	/**
	 * Paramétrage correspondance entités epg/insitutions
	 */
	public static final String CREER_DOSSIER_PARAM_PATH = "/case-management/parametre-root/parametrage-ws-epg";

    /**
     * utility class
     */
    private SolonEppParametreConstant(){
    	// do nothing
    }
}
