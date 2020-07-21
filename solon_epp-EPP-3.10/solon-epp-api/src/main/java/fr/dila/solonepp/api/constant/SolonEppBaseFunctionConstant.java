package fr.dila.solonepp.api.constant;

/**
 * Liste des fonctions unitaires de l'application SOLON EPP.
 * Ces fonctions déterminent la possibilité de cliquer sur un bouton, afficher un menu, accéder à un
 * document où à une vue.
 * 
 * @author jtremeaux
 */
public final class SolonEppBaseFunctionConstant {
    /**
     * Flux de notification : Lecture des notifications sur les tables de référence.
     */
    public static final String NOTIFICATION_TABLE_REF_READER = "NotificationTableRefReader";
    
    /**
     * Flux de notification : Destinataire des mails de notification.
     */
    public static final String NOTIFICATION_EMAIL_RECIPIENT = "NotificationEmailRecipient";
    
    /**
     * Flux de notification : Destinataire des mails d'erreur de notification.
     */
    public static final String NOTIFICATION_EMAIL_ERROR_RECIPIENT = "NotificationEmailErrorRecipient";
    
    /**
     * utility class
     */
    private SolonEppBaseFunctionConstant(){
    	// do nothing
    }
}
