package fr.dila.ss.api.constant;

public final class SSEventConstant {
    public static final String CLEAN_DELETED_STEP_EVENT = "cleanDeletedStep";

    /**
     * Evenement pour lancer la vérification de blocage de dossier
     */
    public static final String BLOCKED_ROUTES_ALERT_EVENT = "blockedRoutesAlertEvent";

    public static final String INCOHERENT_DOSSIER_LINK_EVENT = "dossierLinkIncoherentBatchEvent";

    public static final String VALIDATE_CASELINK_EVENT = "validateCaseLink";

    /**
     * Event de suppression physique des fichiers du fond de dossiers deleted
     */
    public static final String CLEAN_DELETED_REQUETE_EVENT = "cleanDeletedRequeteEvent";

    /**
     * Event de suppression physique des fichiers du fond de dossiers deleted
     */
    public static final String CLEAN_DELETED_FDD_FILE_EVENT = "cleanDeletedFddFileEvent";

    public static final String USER_DELETION_BATCH_EVENT = "userDeletionBatch";

    public static final String USER_DESACTIVATION_BATCH_EVENT = "userDesactivationEvent";

    public static final String SEND_ALERT_BATCH_EVENT = "sendAlertEvent";

    public static final String CLEAN_DELETED_DL_EVENT = "cleanDeletedDossierLinkEvent";

    public static final String CLEAN_DELETED_ALERT_EVENT = "cleanDeletedAlertEvent";

    public static final String CLEAN_DELETED_PARAPHEUR_FILE_EVENT = "cleanDeletedParapheurFileEvent";

    public static final String BATCH_EVENT_CLOSE_USERS_CONNECTIONS = "closeUsersConnectionsEvent";

    public static final String MIGRATION_GVT_DATA = "migrationGvtData";

    public static final String MIGRATION_GVT_EVENT = "migrationGvtEvent";

    public static final String SEND_MIGRATION_DETAILS_DETAILS_PROPERTY = "details";

    public static final String SEND_MIGRATION_DETAILS_RECIPIENT_PROPERTY = "recipient";

    public static final String SEND_MIGRATION_DETAILS_LOGGER_PROPERTY = "migrationLogger";

    public static final String SEND_MIGRATION_DETAILS_EVENT = "sendMigrationDetails";

    /**
     * Event Modele FDR
     */
    /**
     * creation d'un modèle de feuille de route.
     */
    public static final String CREATE_MODELE_FDR_EVENT = "creationModeleFdrEvent";

    /**
     * modification d'un modèle de feuille de route.
     */
    public static final String UPDATE_MODELE_FDR_EVENT = "modificationModeleFdrEvent";

    /**
     * suppression d'un modèle de feuille de route.
     */
    public static final String DELETE_MODELE_FDR_EVENT = "suppressionModeleFdrEvent";

    /**
     * duplication d'un modèle de feuille de route.
     */
    public static final String DUPLICATION_MODELE_FDR_EVENT = "duplicationModeleFdrEvent";

    /**
     * utility class
     */
    private SSEventConstant() {
        // do nothing
    }
}
