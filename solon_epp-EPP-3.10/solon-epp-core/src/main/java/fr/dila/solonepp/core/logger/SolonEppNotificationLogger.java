package fr.dila.solonepp.core.logger;

import fr.dila.st.core.logger.NotificationLogger;

/**
 * Implémentation du logger Log4J pour l'application SOLON EPP.
 * 
 * @see NotificationLogger
 * @author bby, jtremeaux
 */
public class SolonEppNotificationLogger extends NotificationLogger {
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new instance of Log.
     * 
     * @param name Nom du logger
     */
    public SolonEppNotificationLogger(String name) {
        super(name);
    }

    @Override
    protected String getMessage() {
        // TODO Loguer systématiquement certaines infos : login, identifiant de session, identifiant du dossier en cours.
        return "SolonEpp";
    }
}
