package fr.dila.st.api.constant;

import static fr.dila.st.api.constant.STPathConstant.PATH_SEP;

import org.nuxeo.ecm.core.api.security.SecurityConstants;

/**
 * Constantes du socle transverse.
 *
 * @author jtremeaux
 */
public final class STConstant {
    // *************************************************************
    // Types de documents (constantes de Nuxeo).
    // *************************************************************

    /**
     * Type de document commentaire.
     */
    public static final String COMMENT_DOCUMENT_TYPE = "Comment";

    public static final String COMMENT_SCHEMA = "comment";

    // *************************************************************
    // Variables de contexte Document
    // *************************************************************
    /**
     * Variable de contexte permettant de stocker l'UUID d'un document afin de le rechercher par son UUID.
     */
    public static final String UUID_CONTEXT = "ST.UUID";

    /**
     * Position du document dans son conteneur (pour éviter de requéter le conteneur parent).
     */
    public static final String POS_DOC_CONTEXT = "ST.Position";

    // *************************************************************
    // Dossier
    // *************************************************************
    /**
     * Type de document dossier.
     */
    public static final String DOSSIER_DOCUMENT_TYPE = "Dossier";

    // *************************************************************
    // Répertoires
    // *************************************************************
    /**
     * Répertoire des profils.
     */
    public static final String ORGANIGRAMME_PROFILE_DIR = "groupDirectory";

    /**
     * Répertoire des fonctions unitaires.
     */
    public static final String ORGANIGRAMME_BASE_FUNCTION_DIR = "fonctionsDirectory";

    /**
     * Nom de la contribution d'organigramme pour les utilisateurs.
     */
    public static final String ORGANIGRAMME_USER_DIR = "userDirectory";

    /**
     * Nom de la contribution d'organigramme pour les ministères.
     */
    public static final String ORGANIGRAMME_ENTITE_DIR = "entiteDirectory";

    /**
     * Nom de la contribution d'organigramme pour les unités structurelles.
     */
    public static final String ORGANIGRAMME_UNITE_STRUCTURELLE_DIR = "uniteStructurelleDirectory";

    public static final String TRUE = "TRUE";

    public static final String FALSE = "FALSE";

    /**
     * Ldap groupName
     */
    public static final String LDAP_GROUP_NAME_PROPERTY = "groupName";

    // *************************************************************
    // Preferences
    // *************************************************************
    /**
     * Type de document racine des préférences.
     */
    public static final String PREFERENCES_ROOT_DOCUMENT_TYPE = "PreferencesRoot";

    // *************************************************************
    // Gestions des jetons
    // *************************************************************

    /**
     * Type de document racine des jetons docs.
     */
    public static final String JETON_DOC_FOLDER_DOCUMENT_TYPE = "JetonDocFolder";

    /**
     * Type de jeton doc.
     */
    public static final String JETON_DOC_TYPE = "JetonDoc";
    public static final String JETON_DOC_SCHEMA = "jeton_doc";

    // *************************************************************
    // Mailbox
    // *************************************************************
    /**
     * Préfixe de l'ID technique des Mailbox personnelles.
     */
    public static final String MAILBOX_PERSO_ID_PREFIX = "user-";

    /**
     * Taille max de l'ID technique des Mailbox poste.
     */
    public static final int MAILBOX_POSTE_ID_MAX_LENGTH = 50;

    // *************************************************************
    // Journal
    // *************************************************************
    /**
     * Filtre sur la date de debut
     */
    public static final String FILTER_DATE_DEBUT = "dateDebut";

    /**
     * Filtre sur la date de fin
     */
    public static final String FILTER_DATE_FIN = "dateFin";

    /**
     * Filtre sur les categories d'actions (Bordereau, feuille de route)
     */
    public static final String FILTER_CATEGORY = "category";

    /**
     * Filtre sur les categories d'actions (Bordereau, feuille de route)
     */
    public static final String FILTER_LIST_CATEGORY = "categoryList";

    /**
     * Filtre sur la référence du dossier (espace administration)
     */
    public static final String FILTER_DOSSIER_REF = "dossierRef";

    /**
     * Filtre sur les docType
     */
    public static final String FILTER_DOCTYPE = "docType";

    /**
     * Filtre sur les actions
     */
    public static final String FILTER_ACTIONS = "eventId";

    /**
     * Filtre sur le nom des utilisateurs
     */
    public static final String FILTER_USER = "principalName";

    /**
     * Filtre sur les commentaires
     */
    public static final String FILTER_COMMENT = "comment";

    /**
     * Nom de l'utilisateur système par défaut dans nuxeo
     */
    public static final String NUXEO_SYSTEM_USERNAME = SecurityConstants.SYSTEM_USERNAME;

    /**
     * Nom de l'utilisateur système
     */
    public static final String SYSTEM_USERNAME = "système";

    // *************************************************************
    // Parametre
    // *************************************************************
    /**
     * Type de document paramètre.
     */
    public static final String PARAMETRE_DOCUMENT_TYPE = "Parametre";

    /**
     * Type de document racine des modèles de paramètre.
     */
    public static final String PARAMETRE_FOLDER_DOCUMENT_TYPE = "ParametreFolder";

    // *************************************************************
    // Utilisateurs
    // *************************************************************
    /**
     * Utilisateur technique administrateur.
     */
    public static final String USER_ADMINISTRATOR = "Administrator";

    // *************************************************************
    // Mails
    // *************************************************************
    public static final String MAIL_SEND_ERROR = "mailSendError";

    public static final String STEP_DATE_FIN_ETAPE_PROPERTY_NAME = "rtsk:dateFinEtape";

    public static final String STEP_VALIDATION_USER_LABEL_PROPERTY_NAME = "rtsk:validationUserLabel";

    // *************************************************************
    // Messages erreur
    // *************************************************************

    public static final String NO_SUCH_DOCUMENT_ERROR_MSG = "st.document.action.missingDocument.error";

    // *************************************************************
    // Provider
    // *************************************************************

    public static final String KEY_ID_SELECTION = "docIdForSelection";

    // *************************************************************
    // Types de documents.
    // *************************************************************
    public static final String ETAT_APPLICATION_DOCUMENT_TYPE = "EtatApplication";

    public static final String EXPORT_DOCUMENT_DOCUMENT_TYPE = "ExportDocument";

    public static final String FILE_DOCUMENT_TYPE = "File";

    public static final String FILES_SCHEMA = "files";
    public static final String FILES_PROPERTY_FILES = "files";

    public static final String NOTIFICATIONS_SUIVI_BATCHS_DOCUMENT_TYPE = "NotificationsSuiviBatchs";

    public static final String STBLOB_DOCUMENT_TYPE = "STBlob";

    // *************************************************************
    // Préfixes associés à l'organigramme
    // *************************************************************
    /**
     * Préfixe ajouté à l'identifiant technique du noeud de type ministère.
     */
    public static final String PREFIX_MIN = "min-";

    /**
     * Préfixe ajouté à l'identifiant technique du noeud de type unité structurelle.
     */
    public static final String PREFIX_US = "us-";

    /**
     * Préfixe ajouté à l'identifiant technique du noeud.
     */
    public static final String PREFIX_POSTE = "poste-";

    public static final String CASE_MANAGEMENT_ID = "case-management";
    public static final String CASE_MANAGEMENT_PATH = PATH_SEP + CASE_MANAGEMENT_ID;

    public static final String SORT_ORDER_SUFFIX = "Order";

    public static final String LOG_MSG_DURATION = "{}---DURATION : {}ms ---\n";

    /**
     * utility class
     */
    private STConstant() {
        // do nothing
    }
}
