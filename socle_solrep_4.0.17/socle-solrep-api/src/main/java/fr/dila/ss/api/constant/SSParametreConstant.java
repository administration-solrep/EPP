package fr.dila.ss.api.constant;

/**
 * Paramètres applicatifs administrables dans l'IHM de l'application.
 */
public class SSParametreConstant {
    /**
     * Paramètre Exports : nombre de dossiers maximum
     */
    public static final String EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME = "export-nombre-max-dossiers";
    public static final String EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME_TITRE =
        "Exports : Nombre de dossiers maximum pour l'envoi des exports par mél";
    public static final String EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME_DESCRIPTION =
        "Nombre de dossiers maximum au-delà duquel les résultats ne sont pas transmis par mél (concerne les alertes, les recherches et plus généralement tout tableau de dossiers).";
    public static final String EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME_UNIT = "entier";
    public static final String EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME_VALUE = "5000";

    /**
     * Paramètre Exports : objet du courriel de demande d'export
     */
    public static final String OBJET_MAIL_EXPORT_NAME = "object-mel-export";
    public static final String OBJET_MAIL_EXPORT_NAME_TITRE = "Export : Objet du courriel d'une demande d'export";
    public static final String OBJET_MAIL_EXPORT_NAME_DESCRIPTION =
        "« Objet » du mél courriel d'une demande d'export (ex : résultat de recherche experte)";
    public static final String OBJET_MAIL_EXPORT_NAME_UNIT = "objet";
    public static final String OBJET_MAIL_EXPORT_NAME_VALUE = "Votre demande d'export";

    /**
     * Paramètre Exports : texte du courriel de demande d'export en cas de succès
     */
    public static final String CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME = "corps-success-mel-export";
    public static final String CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME_TITRE =
        "Export : Texte de succès du courriel d'une demande d'export";
    public static final String CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME_DESCRIPTION =
        " « Texte » de succès du mél d'une demande d'export (ex : résultat de recherche experte)";
    public static final String CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME_UNIT = "text";
    public static final String CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME_VALUE =
        "Bonjour, l'export demandé le ${date_demande}, est terminé.";

    /**
     * Paramètre Exports : texte du courriel de demande d'export en cas d'erreur
     */
    public static final String CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME = "corps-error-mel-export";
    public static final String CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME_TITRE =
        "Export : Texte d'erreur du courriel d'une demande d'export";
    public static final String CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME_DESCRIPTION =
        "Texte » d'erreur du mél courriel d'une demande d'export (ex : résultat de recherche experte)";
    public static final String CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME_UNIT = "text";
    public static final String CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME_VALUE =
        "Bonjour, l'export demandé le ${date_demande}, a échoué. Le message remonté est le suivant : <br/>";

    /**
     * Paramètre Exports : message d'erreur lorsque le fichier d'export n'a pas pu être généré
     */
    public static final String ERROR_MESSAGE_MAIL_EXPORT_NAME = "error-message-mel-export";
    public static final String ERROR_MESSAGE_MAIL_EXPORT_NAME_TITRE = "Export : Texte d'erreur d'une demande d'export";
    public static final String ERROR_MESSAGE_MAIL_EXPORT_NAME_DESCRIPTION =
        "« Texte » d'erreur lors de la génération d'un fichier d'export (ex : résultat de recherche experte)";
    public static final String ERROR_MESSAGE_MAIL_EXPORT_NAME_UNIT = "text";
    public static final String ERROR_MESSAGE_MAIL_EXPORT_NAME_VALUE = "Impossible de générer le fichier d'export";

    /**
     * Paramètre Alertes : objet du mail dont la limite max de résultats est atteinte
     */
    public static final String ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME = "alertes-objet-mel-limite";
    public static final String ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME_TITRE =
        "Alertes : Objet du courriel de résultat d'une alerte dont la limite a été atteinte";
    public static final String ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME_DESCRIPTION =
        "« Objet » du mél d'envoi des résultats d'une alerte dont la limite a été atteinte.";
    public static final String ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME_UNIT = "objet";
    public static final String ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME_VALUE = "Résultats de l'alerte ${titre_alerte}";

    /**
     * Paramètre Alertes : texte du mail dont la limite max de résultats est atteinte
     */
    public static final String ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME = "alertes-texte-mel-limite";
    public static final String ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME_TITRE =
        "Alertes : Texte du courriel de résultat d'alerte dont la limite a été atteinte";
    public static final String ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME_DESCRIPTION =
        "« Texte » du mél d'envoi des résultats d'une alerte dont la limite a été atteinte.";
    public static final String ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME_UNIT = "texte";
    public static final String ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME_VALUE =
        "Bonjour, l'alerte que vous avez demandée renvoie trop de résultats, merci d'exécuter la requête correspondante depuis l'application.";

    private SSParametreConstant() {
        // Private default constructor
    }
}
