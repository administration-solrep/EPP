package fr.dila.ss.api.constant;

public final class SSFeuilleRouteConstant {
    // *************************************************************
    // Schéma feuille de route
    // *************************************************************
    /**
     * Schéma feuille de route.
     */
    public static final String FEUILLE_ROUTE_SCHEMA = "feuille_route";

    /**
     * Préfixe du schéma feuille de route.
     */
    public static final String FEUILLE_ROUTE_SCHEMA_PREFIX = "fdr";

    /**
     * Propriété du schéma feuille de route : Feuille de route par défaut.
     */
    public static final String FEUILLE_ROUTE_DEFAUT_PROPERTY = "feuilleRouteDefaut";

    public static final String FEUILLE_ROUTE_DEFAUT_XPATH =
        FEUILLE_ROUTE_SCHEMA_PREFIX + ":" + FEUILLE_ROUTE_DEFAUT_PROPERTY;

    /**
     * Propriété du schéma feuille de route : Ministère.
     */
    public static final String FEUILLE_ROUTE_MINISTERE_PROPERTY = "ministere";

    public static final String FEUILLE_ROUTE_MINISTERE_XPATH =
        FEUILLE_ROUTE_SCHEMA_PREFIX + ":" + FEUILLE_ROUTE_MINISTERE_PROPERTY;

    /**
     * Propriété du schéma feuille de route : État de la demande de validation
     */
    public static final String FEUILLE_ROUTE_DEMANDE_VALIDATION_PROPERTY = "demandeValidation";

    public static final String FEUILLE_ROUTE_DEMANDE_VALIDATION_XPATH =
        FEUILLE_ROUTE_SCHEMA_PREFIX + ":" + FEUILLE_ROUTE_DEMANDE_VALIDATION_PROPERTY;

    /**
     * Propriété du schéma feuille de route : Evenement à l'origine de la creation
     */
    public static final String FEUILLE_ROUTE_TYPE_CREATION_PROPERTY = "typeCreation";

    /**
     * Propriété du schéma feuille de route : numéro
     */
    public static final String FEUILLE_ROUTE_NUMERO_PROPERTY = "numero";

    public static final String FEUILLE_ROUTE_NUMERO_XPATH =
        FEUILLE_ROUTE_SCHEMA_PREFIX + ":" + FEUILLE_ROUTE_NUMERO_PROPERTY;

    // *************************************************************
    // Schéma étape de feuille de route
    // *************************************************************

    /**
     * Préfixe du schéma routing task.
     */
    public static final String ROUTING_TASK_SCHEMA_PREFIX = "rtsk";

    /**
     * Propriété du schéma routing task : type d'action.
     */
    public static final String ROUTING_TASK_TYPE_PROPERTY = "type";

    /**
     * Propriété du schéma routing task : Identifiant technique de la Mailbox de distribution.
     */
    public static final String ROUTING_TASK_MAILBOX_ID_PROPERTY = "distributionMailboxId";

    public static final String ROUTING_TASK_MAILBOX_ID_XPATH =
        ROUTING_TASK_SCHEMA_PREFIX + ":" + ROUTING_TASK_MAILBOX_ID_PROPERTY;

    /**
     * Propriété du schéma routing task : UUID de la feuille de route (champ dénormalisé).
     */
    public static final String ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY = "documentRouteId";

    /**
     * Propriété du schéma routing task : validation automatique.
     */
    public static final String ROUTING_TASK_AUTOMATIC_VALIDATION_PROPERTY = "automaticValidation";

    public static final String ROUTING_TASK_AUTOMATIC_VALIDATION_XPATH =
        ROUTING_TASK_SCHEMA_PREFIX + ":" + ROUTING_TASK_AUTOMATIC_VALIDATION_PROPERTY;

    /**
     * Propriété du schéma routing task : échéance.
     */
    public static final String ROUTING_TASK_DUE_DATE_PROPERTY = "dueDate";

    /**
     * Propriété du schéma routing task : échéance.
     */
    public static final String ROUTING_TASK_NUM_VERSION = "numeroVersion";

    /**
     * Propriété du schéma routing_task : Libellé des ministères après validation de l'étape (dénormalisation).
     */
    public static final String ROUTING_TASK_MINISTERE_LABEL_PROPERTY = "ministereLabel";

    /**
     * Propriété du schéma routing_task : ID des ministères après validation de l'étape (dénormalisation).
     */
    public static final String ROUTING_TASK_MINISTERE_ID_PROPERTY = "ministereId";

    /**
     * Propriété du schéma routing_task : Libellé de la direction après validation de l'étape (dénormalisation).
     */
    public static final String ROUTING_TASK_DIRECTION_LABEL_PROPERTY = "directionLabel";

    /**
     * Propriété du schéma routing_task : Identifiant de la direction après validation de l'étape (dénormalisation).
     */
    public static final String ROUTING_TASK_DIRECTION_ID_PROPERTY = "directionId";

    /**
     * Propriété du schéma routing_task : Libellé du poste de distribution après validation de l'étape
     * (dénormalisation).
     */
    public static final String ROUTING_TASK_POSTE_LABEL_PROPERTY = "posteLabel";

    /**
     * Propriété du schéma routing_task : Nom de l'utilisateur qui a validé l'étape après validation de l'étape
     * (dénormalisation).
     */
    public static final String ROUTING_TASK_VALIDATION_USER_LABEL_PROPERTY = "validationUserLabel";

    /**
     * Propriété du schéma routing_task : Identifiant du poste de distribution après validation de l'étape
     * (dénormalisation).
     */
    public static final String ROUTING_TASK_POSTE_ID_PROPERTY = "posteId";

    /**
     * Propriété du schéma routing_task : Identifiant de l'utilisateur qui a validé l'étape après validation de l'étape
     * (dénormalisation).
     */
    public static final String ROUTING_TASK_VALIDATION_USER_ID_PROPERTY = "validationUserId";

    /**
     * Propriété du schéma routing task : état de validation.
     */
    public static final String ROUTING_TASK_VALIDATION_STATUS_PROPERTY = "validationStatus";

    /**
     * Valeur de la propriété du schéma routing task : État de validation "avis favorable".
     */
    public static final String ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE = "1";

    /**
     * Valeur de la propriété du schéma routing task : État de validation "avis défavorable".
     */
    public static final String ROUTING_TASK_VALIDATION_STATUS_AVIS_DEFAVORABLE_VALUE = "2";

    /**
     * Valeur de la propriété du schéma routing task : État de validation "validé automatiquement".
     */
    public static final String ROUTING_TASK_VALIDATION_STATUS_VALIDE_AUTOMATIQUEMENT_VALUE = "3";

    /**
     * Valeur de la propriété du schéma routing task : État de validation "non concerné".
     */
    public static final String ROUTING_TASK_VALIDATION_STATUS_NON_CONCERNE_VALUE = "4";

    /**
     * Propriété du schéma routing task : date de début de l'étape.
     */
    public static final String ROUTING_TASK_DATE_DEBUT_ETAPE_PROPERTY = "dateDebutEtape";

    /**
     * Propriété du schéma routing task : date de fin de l'étape.
     */
    public static final String ROUTING_TASK_DATE_FIN_ETAPE_PROPERTY = "dateFinEtape";

    /**
     * Propriété du schéma routing task : alreadyValidated.
     */
    public static final String ROUTING_TASK_ALREADY_VALIDATED_PROPERTY = "alreadyValidated";

    /**
     * Propriété du schéma routing task : automaticValidated.
     */
    public static final String ROUTING_TASK_AUTOMATIC_VALIDATED_PROPERTY = "automaticValidated";

    /**
     * Propriété du schéma routing task : isSendMail.
     */
    public static final String ROUTING_TASK_IS_MAIL_SEND_PROPERTY = "isMailSend";

    /**
     * Propriété du schéma routing task : deadline (durée de traitement indicatif d'une étape en jours).
     */
    public static final String ROUTING_TASK_DEADLINE_PROPERTY = "deadline";

    /**
     * Propriété du schéma routing task : étape déjà dupliquée.
     */
    public static final String ROUTING_TASK_ALREADY_DUPLICATED_PROPERTY = "alreadyDuplicated";

    /**
     * Propriété du schéma routing task : étape définie comme obligatoire par le SGG.
     */
    public static final String ROUTING_TASK_OBLIGATOIRE_SGG_PROPERTY = "obligatoireSGG";

    public static final String ROUTING_TASK_OBLIGATOIRE_SGG_XPATH =
        ROUTING_TASK_SCHEMA_PREFIX + ":" + ROUTING_TASK_OBLIGATOIRE_SGG_PROPERTY;

    /**
     * Propriété du schéma routing task : étape définie comme obligatoire par le ministère.
     */
    public static final String ROUTING_TASK_OBLIGATOIRE_MINISTERE_PROPERTY = "obligatoireMinistere";

    public static final String ROUTING_TASK_OBLIGATOIRE_MINISTERE_XPATH =
        ROUTING_TASK_SCHEMA_PREFIX + ":" + ROUTING_TASK_OBLIGATOIRE_MINISTERE_PROPERTY;
}
