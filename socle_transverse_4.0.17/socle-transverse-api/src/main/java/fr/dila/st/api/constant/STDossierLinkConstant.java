package fr.dila.st.api.constant;

/**
 * Constantes du dossier link (caseLink).
 *
 * @author ARN
 */
public final class STDossierLinkConstant {
    /**
     * Type de document DossierLink.
     */
    public static final String DOSSIER_LINK_DOCUMENT_TYPE = "DossierLink";

    /**
     * Propriété du schéma dossier_link : booleen indiquant si un mail a déjà été envoyé.
     */
    public static final String DOSSIER_LINK_IS_MAIL_SEND_PROPERTY = "isMailSend";

    /**
     * Propriété du schéma dossier_link : identifiant technique de l'étape de feuille de route en cours.
     */
    public static final String DOSSIER_LINK_ROUTING_TASK_ID_PROPERTY = "routingTaskId";

    /**
     * Propriété du schéma dossier_link : type de l'étape de feuille de route en cours.
     */
    public static final String DOSSIER_LINK_ROUTING_TASK_TYPE_PROPERTY = "routingTaskType";

    /**
     * Propriété du schéma dossier_link : libellé de l'étape en cours (champ dénormalisé).
     */
    public static final String DOSSIER_LINK_ROUTING_TASK_LABEL_PROPERTY = "routingTaskLabel";

    /**
     * Propriété du schéma dossier_link : libellé de la mailbox de distribution (champ dénormalisé).
     */
    public static final String DOSSIER_LINK_ROUTING_TASK_MAILBOX_LABEL_PROPERTY = "routingTaskMailboxLabel";

    /**
     * Propriété du schéma dossier_link : identifiant du ministère attributaire
     */
    public static final String DOSSIER_LINK_ID_MINISTERE_ATTRIBUTAIRE = "idMinistereAttributaire";

    /**
     * utility class
     */
    private STDossierLinkConstant() {
        // do nothing
    }
}
