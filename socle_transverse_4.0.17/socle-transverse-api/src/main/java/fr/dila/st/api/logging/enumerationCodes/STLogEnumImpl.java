package fr.dila.st.api.logging.enumerationCodes;

import fr.dila.st.api.logging.STLogEnum;

/**
 * Enumération des logs codifiés du ST Code : 000_000_000 <br>
 * &lt;=&gt; [{@link STTypesEnum}]_[{@link STPorteesEnum}]_[{@link STObjetsEnum}] <br>
 */
public enum STLogEnumImpl implements STLogEnum {
    DEFAULT(STTypesEnum.DEFAULT, STPorteesEnum.DEFAULT, STObjetsEnum.DEFAULT, "Valeur de log par défaut"),
    LOG_EXCEPTION_TEC(STTypesEnum.ACTION_LOG, STPorteesEnum.TECHNIQUE, STObjetsEnum.LOGGER, "Exception remontée"),
    LOG_DEBUG_TEC(STTypesEnum.ACTION_LOG, STPorteesEnum.TECHNIQUE, STObjetsEnum.DEFAULT, "DEBUG"),
    LOG_INFO_TEC(STTypesEnum.ACTION_LOG, STPorteesEnum.TECHNIQUE, STObjetsEnum.DEFAULT, "INFO"),
    // ************************** SUPPRESSION DE DOCUMENTS ***********************************************
    /**
     * Suppression workspace utilisateur : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#USER_WORKSPACE}
     */
    DEL_WORKSPACE_TEC(
        STTypesEnum.DELETE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.USER_WORKSPACE,
        "Suppression workspace utilisateur"
    ),
    /**
     * Suppression favoris : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#FAV}
     */
    DEL_FAVORIS_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, STObjetsEnum.FAV, "Suppression favoris"),
    /**
     * Suppression dossier link : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOSSIER_LINK}
     */
    DEL_DL_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, STObjetsEnum.DOSSIER_LINK, "Suppression dossier link"),
    /**
     * Suppression Dossier : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#DOSSIER}
     */
    DEL_DOSSIER_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, STObjetsEnum.DOSSIER, "Suppression dossier"),
    /**
     * Suppression Comment : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#COMMENT}
     */
    DEL_COMMENT_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, STObjetsEnum.COMMENT, "Suppression comment"),
    /**
     * Suppression technique Document : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOCUMENT}
     */
    DEL_DOC_TEC(
        STTypesEnum.DELETE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOCUMENT,
        "Suppression technique de document"
    ),
    /**
     * Suppression fonctionnelle Document : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#DOCUMENT}
     */
    DEL_DOC_FONC(
        STTypesEnum.DELETE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.DOCUMENT,
        "Suppression fonctionnelle de document"
    ),
    /**
     * Suppression fonctionnelle d'une alerte : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#ALERT}
     */
    DEL_ALERT_FONC(
        STTypesEnum.DELETE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.ALERT,
        "Suppression fonctionnelle d'une alerte"
    ),
    /**
     * Suppression technique d'une alerte : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#ALERT}
     */
    DEL_ALERT_TEC(
        STTypesEnum.DELETE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.ALERT,
        "Suppression technique d'une alerte"
    ),
    /**
     * Suppression fonctionnelle requête experte : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#REQ_EXPERTE}
     */
    DEL_REQ_EXP_FONC(
        STTypesEnum.DELETE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.REQ_EXPERTE,
        "Suppression fonctionnelle d'une requête experte"
    ),
    /**
     * Suppression technique d'une requête experte : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#REQ_EXPERTE}
     */
    DEL_REQ_EXP_TEC(
        STTypesEnum.DELETE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.REQ_EXPERTE,
        "Suppression technique d'une requête experte"
    ),
    /**
     * Suppression utilisateur : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#USER}
     */
    DEL_USER_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, STObjetsEnum.USER, "Suppression d'un utilisateur"),
    /**
     * Suppression technique File : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#FILE}
     */
    DEL_FILE_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, STObjetsEnum.FILE, "Suppression technique de fichier"),
    /**
     * Suppression technique File : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#FILE}
     */
    DEL_FILE_FONC(
        STTypesEnum.DELETE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FILE,
        "Suppression fonctionnelle de fichier"
    ),
    /**
     * Suppression technique folder : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#FOLDER}
     */
    DEL_FOLDER_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, STObjetsEnum.FOLDER, "Suppression de répertoire"),
    /**
     * Suppression d'un fichier téléchargé : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#FILE_UPLOAD}
     */
    DEL_FILE_UPLOAD_FONC(
        STTypesEnum.DELETE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FILE_UPLOAD,
        "Suppression de fichier de la liste des fichiers pré-chargés"
    ),
    /**
     * Suppression d'un jeton : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#JETON}
     */
    DEL_JETON_DOC_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, STObjetsEnum.JETON, "Suppression de jeton"),

    // ****************************************RECUPERATION*******************************************************
    /**
     * Récupération dossier : {@link STTypesEnum#GET}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#DOSSIER}
     */
    GET_DOSSIER_TEC(STTypesEnum.GET, STPorteesEnum.TECHNIQUE, STObjetsEnum.DOSSIER, "Récupération dossier"),
    /**
     * Récupération dossier : {@link STTypesEnum#GET}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#DOSSIER}
     */
    GET_TDR_TEC(
        STTypesEnum.GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.TABLE_REFERENCE,
        "Récupération table de référence"
    ),
    /**
     * Récupération de paramètre : {@link STTypesEnum#GET}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#PARAM}
     */
    GET_PARAM_TEC(STTypesEnum.GET, STPorteesEnum.TECHNIQUE, STObjetsEnum.PARAM, "Récupération de paramètre"),

    // ******************************************** ACTIONS ******************************************************
    /**
     * Validation dossier : {@link STTypesEnum#VALIDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#DOSSIER}
     */
    VALIDATE_DOSSIER_TEC(STTypesEnum.VALIDATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.DOSSIER, "Validation de dossier"),
    /**
     * Démarrage de dossier : {@link STTypesEnum#START}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#DOSSIER}
     */
    START_DOSSIER_TEC(STTypesEnum.START, STPorteesEnum.TECHNIQUE, STObjetsEnum.DOSSIER, "Démarrage de dossier"),
    /**
     * Sauvegarde du dossier : {@link STTypesEnum#SAVE}_{@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#DOSSIER}
     */
    SAVE_DOSSIER_FONC(STTypesEnum.SAVE, STPorteesEnum.FONCTIONNELLE, STObjetsEnum.DOSSIER, "Sauvegarde du dossier"),

    // **************************************** ECHEC D'ACTIONS **************************************************
    // ------------Suppression--------------
    /**
     * Echec delete commentaire : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMENT}
     */
    FAIL_DEL_COM_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMENT,
        "Echec de suppression du commentaire"
    ),
    /**
     * Echec delete étape : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMENT}
     */

    FAIL_DEL_STEP_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.STEP,
        "Echec de suppression d'une étape"
    ),
    /**
     * Echec delete alerte : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#ALERT}
     */
    FAIL_DEL_ALERT_FONC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.ALERT,
        "Echec de suppression fonctionnelle de l'alerte"
    ),
    /**
     * Echec de suppression de l'alerte : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#ALERT}
     */
    FAIL_DEL_ALERT_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.ALERT,
        "Echec de suppression technique de l'alerte"
    ),
    /**
     * Echec de suppression du workspace utilisateur : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#USER_WORKSPACE}
     */
    FAIL_DEL_UW_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.USER_WORKSPACE,
        "Echec de suppression du workspace utilisateur"
    ),
    /**
     * Echec de suppression de l'utilisateur : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#USER}
     */
    FAIL_DEL_USER_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.USER,
        "Echec de suppression de l'utilisateur"
    ),
    /**
     * Echec de suppression de requete : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#REQ_EXPERTE}
     */
    FAIL_DEL_REQ_EXP_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.REQ_EXPERTE,
        "Echec de suppression d'une requête experte"
    ),
    /**
     * Echec de suppression d'un fichier de pièce jointe : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#PIECE_JOINTE}
     */
    FAIL_DEL_PIECE_JOINTE_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PIECE_JOINTE,
        "Echec de suppression d'un fichier de pièce jointe"
    ),
    /**
     * Echec de suppression de la liste des fichiers upload : {@link STTypesEnum#FAIL_DEL}_
     * {@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#FILE_UPLOAD}
     */
    FAIL_DEL_FILE_UPLOAD_FONC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FILE_UPLOAD,
        "Echec lors de la suppression de la liste des fichiers upload"
    ),
    /**
     * Echec de suppression de document : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOCUMENT}
     */
    FAIL_DEL_DOC_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOCUMENT,
        "Echec de suppression de document"
    ),
    /**
     * Echec de suppression de document : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#FILE}
     */
    FAIL_DEL_FILE_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.FILE,
        "Echec de suppression de fichier"
    ),

    // ------------Récupération-------------
    /**
     * Echec récupération d'un fichier de pièce jointe : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#PIECE_JOINTE}
     */
    FAIL_GET_PIECE_JOINTE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PIECE_JOINTE,
        "Echec récupération d'un fichier de pièce jointe"
    ),
    /**
     * Echec de récupération du profil utilisateur : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#PROFIL_UTILISATEUR}
     */
    FAIL_GET_PROFIL_UTILISATEUR_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PROFIL_UTILISATEUR,
        "Echec de récupération du profil utilisateur"
    ),
    /**
     * Echec de récupération du profil utilisateur : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#PROFIL_UTILISATEUR}
     */
    FAIL_GET_PROFIL_UTILISATEUR_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.PROFIL_UTILISATEUR,
        "Echec de récupération du profil utilisateur (fonc)"
    ),
    /**
     * Echec récupération principal : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#PRINCIPAL}
     */
    FAIL_GET_PRIN_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PRINCIPAL,
        "Echec de récupération du principal"
    ),
    /**
     * Echec récupération mandat : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#MANDAT}
     */
    FAIL_GET_MANDAT_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.MANDAT,
        "Echec de récupération de mandat"
    ),
    /**
     * Echec récupération document : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOCUMENT}
     */
    FAIL_GET_DOCUMENT_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOCUMENT,
        "Echec de récupération du document"
    ),
    /**
     * Echec récupération document : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#DOCUMENT}
     */
    FAIL_GET_DOCUMENT_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.DOCUMENT,
        "Echec de récupération du document (fonc)"
    ),
    /**
     * Echec récupération dossier : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOSSIER}
     */
    FAIL_GET_DOSSIER_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOSSIER,
        "Echec de récupération du dossier"
    ),
    /**
     * Echec récupération evenement : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#EVENT}
     */
    FAIL_GET_EVENT_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.EVENT,
        "Echec de récupération d'un evenement"
    ),
    /**
     * Echec récupération dun paramètre d'evenement : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#EVENT_PARAM}
     */
    FAIL_GET_EVENT_PARAM_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.EVENT_PARAM,
        "Echec de récupération d'un paramètre d'événement"
    ),
    /**
     * Echec récupération jeton : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#JETON}
     */
    FAIL_GET_JETON_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.JETON,
        "Echec de récupération du jeton"
    ),
    /**
     * Echec récupération dossier Link : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOSSIER_LINK}
     */
    FAIL_GET_DOSSIER_LINK_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOSSIER_LINK,
        "Echec de récupération du dossier Link"
    ),
    /**
     * Echec récupération EntiteNode : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#ENTITE_NODE}
     */
    FAIL_GET_ENTITE_NODE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.ENTITE_NODE,
        "Echec de récupération de l'entiteNode"
    ),
    /**
     * Echec récupération d'information : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#INFORMATION}
     */
    FAIL_GET_INFORMATION_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.INFORMATION,
        "Echec de récupération de l'information"
    ),
    /**
     * Echec récupération Jeton Service : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#JETON_SERVICE}
     */
    FAIL_GET_JETON_SERVICE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.JETON_SERVICE,
        "Echec de récupération du JetonService"
    ),
    /**
     * FAIL_GET_US_TEC : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#UNITE_STRUCTURELLE}
     */
    FAIL_GET_US_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.UNITE_STRUCTURELLE,
        "Echec de récupération d'unité structurelle"
    ),
    /**
     * FAIL_GET_RECHERCHE_TEC : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#RECHERCHE}
     */
    FAIL_GET_RECHERCHE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.RECHERCHE,
        "Echec de la recherche"
    ),
    /**
     * Echec de récupération de l'alerte : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#ALERT}
     */
    FAIL_GET_ALERT_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.ALERT,
        "Echec de récupération de l'alerte"
    ),
    /**
     * Echec de récupération de paramètre : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#PARAM}
     */
    FAIL_GET_PARAM_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PARAM,
        "Echec de récupération de paramètre"
    ),
    /**
     * Echec de récupération du workspace utilisateur : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#USER_WORKSPACE}
     */
    FAIL_GET_UW_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.USER_WORKSPACE,
        "Echec de récupération du workspace utilisateur"
    ),
    /**
     * Echec de récupération de l'utilisateur : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#USER}
     */
    FAIL_GET_USER_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.USER,
        "Echec de récupération de l'utilisateur"
    ),
    /**
     * Impossible de trouver le solver de class : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#SOLVER}
     */
    FAIL_GET_SOLVER_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.SOLVER,
        "Impossible de trouver le solver de class"
    ),
    /**
     * Echec de récupération de l'adresse mail de l'utilisateur : {@link STTypesEnum#FAIL_GET}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#MAIL}
     */
    FAIL_GET_MAIL_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.MAIL,
        "Echec de récupération de l'adresse mail de l'utilisateur"
    ),
    /**
     * Echec de récupération d'un poste : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#POSTE}
     */
    FAIL_GET_POSTE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.POSTE,
        "Echec de récupération du poste"
    ),

    /**
     * Echec de récupération d'un tableau dynamique : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#TABLEAU_DYNAMIQUE}
     */
    FAIL_GET_TABLEAU_DYNAMIQUE(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.TABLEAU_DYNAMIQUE,
        "Echec de récupération du tableau dynamique"
    ),
    /**
     * Echec de récupération d'un poste : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#POSTE}
     */
    FAIL_GET_POSTE_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.POSTE,
        "Echec de récupération du poste (fonc)"
    ),
    /**
     * Echec de récupération de la session : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#SESSION}
     */
    FAIL_GET_SESSION_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.SESSION,
        "Echec de récupération de la session"
    ),
    /**
     * Echec de récupération de la session : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#SESSION}
     */
    FAIL_GET_SESSION_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.SESSION,
        "Echec de récupération de la session (fonc)"
    ),
    /**
     * Echec de récupération de stream : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#STREAM}
     */
    FAIL_GET_STREAM_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.STREAM,
        "Echec de récupération de stream (io)"
    ),
    /**
     * Echec de récupération du ou des fichiers : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#FILE}
     */
    FAIL_GET_FILE_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FILE,
        "Echec de récupération du ou des fichiers (fonc)"
    ),

    /**
     * Echec de récupération des différentes version du fichier : {@link STTypesEnum#FAIL_GET}_
     * {@link STPorteesEnum#FONCTIONNELLE}_ {@link STObjetsEnum#FILE}
     */
    FAIL_GET_VERSION_FILE_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FILE,
        "Echec de récupération des versions du fichier (fonc)"
    ),

    /**
     * Echec de récupération du ou des fichiers : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#FILE}
     */
    FAIL_GET_FILE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.FILE,
        "Echec de récupération du ou des fichiers (tec)"
    ),
    /**
     * Echec de récupération de doctype : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOCTYPE}
     */
    FAIL_GET_DOCTYPE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOCTYPE,
        "Echec de récupération de doctype"
    ),
    /**
     * Echec de récupération de schéma : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#SCHEMA}
     */
    FAIL_GET_SCHEMA_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.SCHEMA,
        "Echec de récupération de schéma"
    ),
    /**
     * Echec de récupération du hostname : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#HOSTNAME}
     */
    FAIL_GET_HOSTNAME_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.HOSTNAME,
        "Echec de récupération du hostname"
    ),
    /**
     * Echec de récupération du user workspace : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#USER_WORKSPACE}
     */
    FAIL_GET_WORKSPACE_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.USER_WORKSPACE,
        "Echec de récupération du user workspace"
    ),
    /**
     * Echec de récupération du gouvernement : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#GOUVERNEMENT}
     */
    FAIL_GET_GOUVERNEMENT_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.GOUVERNEMENT,
        "Erreur lors de récupération d'un gouvernement (tec)"
    ),
    /**
     * Echec de récupération du gouvernement : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#GOUVERNEMENT}
     */
    FAIL_GET_GOUVERNEMENT_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.GOUVERNEMENT,
        "Erreur lors de récupération d'un gouvernement (fonc)"
    ),
    /**
     * Echec de récupération du ministère : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#MINISTERE}
     */
    FAIL_GET_MINISTERE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.MINISTERE,
        "Erreur lors de récupération d'un ministère"
    ),
    /**
     * Corbeille non trouvée : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#CORBEILLE}
     */
    FAIL_GET_CORBEILLE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.CORBEILLE,
        "Corbeille non trouvée"
    ),
    /**
     * Convertisseur non trouvé : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#CONVERTER}
     */
    FAIL_GET_CONVERTER_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.CONVERTER,
        "Converter non trouvé"
    ),
    /**
     * Echec de récupération de la mailbox : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#MAIL_BOX}
     */
    FAIL_GET_MAILBOX_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.MAIL_BOX,
        "Echec de récupération de la mailbox"
    ),
    /**
     * Echec de récupération de l'icone : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#ICON}
     */
    FAIL_GET_ICON_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.ICON,
        "Echec de récupération de l'icône"
    ),
    /**
     * Echec de récupération de l'icone : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#ICON}
     */
    FAIL_GET_DOCUMENT_BANNIERE_INFO(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOCUMENT,
        "Echec de récupération du document de la banniere d'information"
    ),
    /**
     * Echec de modification de la feuille de route : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#FDR}
     */

    FAIL_UPDATE_FDR(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FDR,
        "Echec de modification de la Feuille de route"
    ),
    /**
     * Echec de modification de la feuille de route : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#FDR}
     */

    FAIL_GET_VOC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.VOC,
        "Echec de récupération d'un vocabulaire"
    ),

    // ------------Création-------------
    /**
     * Echec de création d'un commentaire : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMENT}
     */
    FAIL_CREATE_COM_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMENT,
        "Echec de création d'un commentaire"
    ),
    /**
     * Echec de création d'une etape : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#STEP}
     */
    FAIL_CREATE_STEP_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.STEP,
        "Echec de création d'une étape"
    ),
    /**
     * Echec de création d'un gouvernement : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#GOUVERNEMENT}
     */
    FAIL_CREATE_GOUVERNEMENT_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.GOUVERNEMENT,
        "Echec de création d'un gouvernement"
    ),
    /**
     * Echec de création d'un ministère : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#MINISTERE}
     */
    FAIL_CREATE_MINISTERE_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.MINISTERE,
        "Echec de création d'un ministère"
    ),
    /**
     * Echec de création d'un mandat : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#MANDAT}
     */
    FAIL_CREATE_MANDAT_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.MANDAT,
        "Echec de création d'un mandat"
    ),
    /**
     * Echec de création d'une identité : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#IDENTITE_TDR}
     */
    FAIL_CREATE_IDENTITE_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.IDENTITE_TDR,
        "Echec de création d'une identité"
    ),
    /**
     * Echec de création d'un acteur : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#ACTEUR_TDR}
     */
    FAIL_CREATE_ACTEUR_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.ACTEUR_TDR,
        "Echec de création d'un acteur"
    ),
    /**
     * Echec création communication : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMUNICATION}
     */
    FAIL_CREATE_COMM_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Echec de création de la communication"
    ),
    /**
     * Echec de création du jeton : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#JETON}
     */
    FAIL_CREATE_JETON_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.JETON,
        "Echec de création du jeton"
    ),
    /**
     * Echec de création d'alerte : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#ALERT}
     */
    FAIL_CREATE_ALERT_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.ALERT,
        "Echec de création d'alerte"
    ),
    /**
     * Echec de création du fichier excel : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#EXCEL}
     */
    FAIL_CREATE_EXCEL_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.EXCEL,
        "Echec de création du fichier excel"
    ),
    /**
     * Echec de création du fichier pdf : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#PDF}
     */
    FAIL_CREATE_PDF_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PDF,
        "Echec de création du fichier pdf"
    ),
    /**
     * Echec de création de fichier dans un répertoire : {@link STTypesEnum#FAIL_CREATE}_
     * {@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#FILE}
     */
    FAIL_CREATE_FILE_FONC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FILE,
        "Echec de création de fichier dans un répertoire"
    ),
    /**
     * Echec de création de fichier dans un répertoire : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}
     * _{@link STObjetsEnum#FILE}
     */
    FAIL_CREATE_FILE_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.FILE,
        "Echec d'ajout ou de modification de fichier dans un répertoire"
    ),
    /**
     * Echec de création de repertoire : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#FOLDER}
     */
    FAIL_CREATE_FOLDER_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.FOLDER,
        "Echec de création d'un répertoire (tec)"
    ),
    /**
     * Echec de création d'un répertoire d'arborescence : {@link STTypesEnum#FAIL_CREATE}_
     * {@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#FOLDER}
     */
    FAIL_CREATE_FOLDER_FONC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FOLDER,
        "Echec de création d'un répertoire (fonc)"
    ),
    /**
     * Echec de création d'un dossier : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOSSIER}
     */
    FAIL_CREATE_DOSSIER_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOSSIER,
        "Echec de création d'un dossier"
    ),
    /**
     * Echec de création d'un document : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOCUMENT}
     */
    FAIL_CREATE_DOCUMENT_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOCUMENT,
        "Echec de création d'un document"
    ),

    // ------------Modification--------------
    /**
     * Echec de mise à jour d'un commentaire' : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMENT}
     */
    FAIL_UPDATE_COM_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMENT,
        "Echec de mise à jour d'un commentaire'"
    ),
    /**
     * Echec mise à jour dossier : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOSSIER}
     */
    FAIL_UPDATE_DOSSIER_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOSSIER,
        "Echec de mise à jour du dossier"
    ),
    /**
     * Echec mise à jour dossier : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#DOSSIER}
     */
    FAIL_UPDATE_DOSSIER_FONC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.DOSSIER,
        "Echec de mise à jour fonctionnelle du dossier"
    ),
    /**
     * Echec de modification d'alerte : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#ALERT}
     */
    FAIL_UPDATE_ALERT_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.ALERT,
        "Echec de modification de l'alerte"
    ),
    /**
     * Echec de modification de la requête : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#REQ_EXPERTE}
     */
    FAIL_UPDATE_REQUETE_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.REQ_EXPERTE,
        "Echec de modification de la requête"
    ),
    /**
     * Echec de modification du fichier : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#FILE}
     */
    FAIL_UPDATE_FILE_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.FILE,
        "Echec de mise à jour de fichier dans un répertoire (tec)"
    ),
    /**
     * Echec de modification du fichier : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#FILE}
     */
    FAIL_UPDATE_FILE_FONC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FILE,
        "Echec de mise à jour de fichier dans un répertoire (fonc)"
    ),
    /**
     * Echec de création de repertoire : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#FOLDER}
     */
    FAIL_UPDATE_FOLDER_FONC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FOLDER,
        "Echec de mise à jour d'un répertoire (fonc)"
    ),
    /**
     * Echec de mise à jour de repertoire : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#FOLDER}
     */
    FAIL_UPDATE_FOLDER_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.FOLDER,
        "Echec de mise à jour d'un répertoire (tec)"
    ),
    /**
     * Echec de rafraichissement du navigation context : {@link STTypesEnum#FAIL_UPDATE}_
     * {@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#NAV_CONTEXT}
     */
    FAIL_UPDATE_NAV_CONTEXT_FONC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.NAV_CONTEXT,
        "Echec de rafraichissement du navigationContext"
    ),
    /**
     * Echec de mise à jour d'une identité : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#IDENTITE_TDR}
     */
    FAIL_UPDATE_IDENTITE_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.IDENTITE_TDR,
        "Echec de la mise à jour d'une identité"
    ),
    /**
     * Echec de mise à jour d'un mandat : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#MANDAT}
     */
    FAIL_UPDATE_MANDAT_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.MANDAT,
        "Echec de la mise à jour d'un mandat"
    ),
    /**
     * Echec de mise à jour d'un gouvernement: {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#GOUVERNEMENT}
     */
    FAIL_UPDATE_GOUVERNEMENT_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.GOUVERNEMENT,
        "Echec de la mise à jour d'un gouvernement"
    ),
    /**
     * Echec de mise à jour d'un ministère : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#MINISTERE}
     */
    FAIL_UPDATE_MINISTERE_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.MINISTERE,
        "Echec de la mise à jour d'un ministère"
    ),
    /**
     * Echec de la sauvegarde d'un document nuxeo : {@link STTypesEnum#FAIL_SAVE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#DOCUMENT}
     */
    FAIL_SAVE_DOCUMENT_FONC(
        STTypesEnum.FAIL_SAVE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.DOCUMENT,
        "Echec sauvegarde document"
    ),

    // ------------Communication---------------
    /**
     * Echec initialisation communication : {@link STTypesEnum#FAIL_INITIALISATION}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMUNICATION}
     */
    FAIL_INIT_COMM_TEC(
        STTypesEnum.FAIL_INITIALISATION,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Erreur lors de l'initilisation de la communication."
    ),
    /**
     * Echec initialisation communication : {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMUNICATION}
     */
    FAIL_UPDATE_COMM_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Erreur lors de la mise à jour de la communication."
    ),
    /**
     * Erreur lors de suppression de la communication.: {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMUNICATION}
     */
    FAIL_REMOVE_COMM_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Erreur lors de suppression de la communication."
    ),

    // ------------Divers-------------------
    /**
     * Echec de commit Transaction : {@link STTypesEnum#FAIL_COMMIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#TRANSACTION}
     */
    FAIL_COMMIT_TRANSACTION_TEC(
        STTypesEnum.FAIL_COMMIT,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.TRANSACTION,
        "Echec commit transaction"
    ),
    /**
     * Echec de compression du fichier : {@link STTypesEnum#FAIL_ZIP}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#FILE}
     */
    FAIL_ZIP_FILE_TEC(
        STTypesEnum.FAIL_ZIP,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.FILE,
        "Echec de compression du fichier"
    ),
    /**
     * Echec de validation du dossier link : {@link STTypesEnum#FAIL_VALIDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOSSIER_LINK}
     */
    FAIL_VALIDATE_DL_TEC(
        STTypesEnum.FAIL_VALIDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOSSIER_LINK,
        "Echec de validation du dossier link"
    ),
    /**
     * Echec de la construction de l'arborescence des corbeilles : {@link STTypesEnum#FAIL_BUILD}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#CORBEILLE}
     */
    FAIL_BUILD_CORBEILLE_TREE_TEC(
        STTypesEnum.FAIL_BUILD,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.CORBEILLE,
        "Echec de la construction de l'arborescence des corbeilles"
    ),
    /**
     * Echec appel logger : {@link STTypesEnum#FAIL_LOG}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#LOGGER}
     */
    FAIL_LOG_TEC(STTypesEnum.FAIL_LOG, STPorteesEnum.TECHNIQUE, STObjetsEnum.LOGGER, "Echec d'utilisation du logger"),
    /**
     * NOTIFICATION AUDIT LOGGER : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#LOGGER}
     */
    PROCESS_NOTIFICATION_AUDIT_EVENT_LOGGER_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.LOGGER,
        "NotificationAuditEventLogger handleEvent() "
    ),
    /**
     * Echec de navigation au dossier : {@link STTypesEnum#FAIL_NAVIGATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOSSIER}
     */
    FAIL_NAVIGATE_TO_DOSSIER_TEC(
        STTypesEnum.FAIL_NAVIGATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOSSIER,
        "Erreur lors de la navigation au dossier"
    ),
    /**
     * FAIL_SEND_MAIL_TEC : {@link STTypesEnum#FAIL_SEND}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#MAIL}
     */
    FAIL_SEND_MAIL_TEC(STTypesEnum.FAIL_SEND, STPorteesEnum.TECHNIQUE, STObjetsEnum.MAIL, "Echec d'envoi du mail"),
    /**
     * FAIL_SEND_MAIL_TEC : {@link STTypesEnum#FAIL_SEND}_{@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#MAIL}
     */
    FAIL_SEND_MAIL_FONC(
        STTypesEnum.FAIL_SEND,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.MAIL,
        "Echec d'envoi du mail (fonc)"
    ),
    /**
     * Echec de sauvegarde de l'alerte : {@link STTypesEnum#FAIL_SAVE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#ALERT}
     */
    FAIL_SAVE_ALERT_TEC(
        STTypesEnum.FAIL_SAVE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.ALERT,
        "Echec de sauvegarde de l'alerte"
    ),
    /**
     * Echec de déverrouillage documents : {@link STTypesEnum#FAIL_UNLOCK}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOCUMENT}
     */
    FAIL_UNLOCK_DOC_TEC(
        STTypesEnum.FAIL_UNLOCK,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOCUMENT,
        "Echec de déverrouillage document"
    ),
    /**
     * Echec de déverrouillage documents : {@link STTypesEnum#FAIL_UNLOCK}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#DOCUMENT}
     */
    FAIL_UNLOCK_DOC_FONC(
        STTypesEnum.FAIL_UNLOCK,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.DOCUMENT,
        "Echec de déverrouillage fonctionnelle du document"
    ),
    /**
     * Echec de sauvegarde du jeton : {@link STTypesEnum#FAIL_SAVE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#JETON}
     */
    FAIL_SAVE_JETON_TEC(
        STTypesEnum.FAIL_SAVE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.JETON,
        "Echec de sauvegarde du jeton"
    ),
    /**
     * Echec d'exécution de requête SQL : {@link STTypesEnum#FAIL_EXECUTE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#SQL}
     */
    FAIL_EXEC_SQL(
        STTypesEnum.FAIL_EXECUTE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.SQL,
        "Echec d'exécution de requête SQL"
    ),
    /**
     * Echec de lancement de l'event : {@link STTypesEnum#FAIL_SEND}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#EVENT}
     */
    FAIL_SEND_EVENT_TEC(
        STTypesEnum.FAIL_SEND,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.EVENT,
        "Echec de lancement de l'event"
    ),
    /**
     * FAIL FILTER CORBEILLE: {@link STTypesEnum#FAIL_FILTER}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#CORBEILLE}
     */
    FAIL_FILTER_CORBEILLE_TEC(
        STTypesEnum.FAIL_FILTER,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.CORBEILLE,
        "Erreur lors du filtrage d'une corbeille"
    ),
    /**
     * FAIL CONVERT DATE: {@link STTypesEnum#FAIL_CONVERT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#CONVERSION}
     */
    FAIL_CONVERT_DATE_TEC(
        STTypesEnum.FAIL_CONVERT,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.CONVERSION,
        "Erreur lors de la conversion d'une date"
    ),
    /**
     * Erreur d'exécution ST.Update.List : {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#OPERATION_UPDATE_LIST}
     */
    FAIL_PROCESS_OPERATION_UPDATE_LIST_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.OPERATION_UPDATE_LIST,
        "Erreur d'exécution de l'opération ST.Update.List"
    ),
    /**
     * Echec de traitement de fichier excel : {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#EXCEL}
     */
    FAIL_PROCESS_EXCEL_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.EXCEL,
        "Echec de traitement du fichier excel"
    ),
    /**
     * Erreur d'exécution de l'injection de Gouvernement : {@link STTypesEnum#FAIL_PROCESS}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#INJECTION_GOUVERNEMENT}
     */
    FAIL_PROCESS_INJECTION_GOUVERNEMENT_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.INJECTION_GOUVERNEMENT,
        "Erreur lors de l'injection du Gouvernement"
    ),
    /**
     * Echec du traitement de l'action de masse : {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#MASS}
     */
    FAIL_PROCESS_MASS_FONC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.MASS,
        "Echec du traitement de l'action de masse"
    ),
    /**
     * Echec de nettoyage : {@link STTypesEnum#FAIL_CLEAR}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#FILE}
     */
    FAIL_CLEAR_UPLOADED_DATA_TEC(
        STTypesEnum.FAIL_CLEAR,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.FILE,
        "Erreur lors du nettoyage des données téléchargées"
    ),
    /**
     * Echec de sauvegarde de la session : {@link STTypesEnum#FAIL_SAVE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#SESSION}
     */
    FAIL_SAVE_SESSION_TEC(
        STTypesEnum.FAIL_SAVE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.SESSION,
        "Echec de la sauvegarde de la session"
    ),
    /**
     * Echec de fermeture du flux : {@link STTypesEnum#FAIL_CLOSE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#STREAM}
     */
    FAIL_CLOSE_STREAM_TEC(
        STTypesEnum.FAIL_CLOSE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.STREAM,
        "Echec de fermeture du flux"
    ),
    /**
     * Echec de validation de la propriété : {@link STTypesEnum#FAIL_VALIDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#PROPERTY}
     */
    FAIL_VALIDATE_PROPERTY_TEC(
        STTypesEnum.FAIL_VALIDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PROPERTY,
        "Echec de validation de la propriété"
    ),
    /**
     * Echec lors du verrouillage d'un document : {@link STTypesEnum#FAIL_LOCK}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOCUMENT}
     */
    FAIL_LOCK_DOC_TEC(
        STTypesEnum.FAIL_LOCK,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOCUMENT,
        "Echec lors du verrouillage d'un document"
    ),
    /**
     * Echec de la génération du HTML : {@link STTypesEnum#FAIL_GENERATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#HTML}
     */
    FAIL_GENERATE_HTML_TEC(
        STTypesEnum.FAIL_GENERATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.HTML,
        "Echec de la génération du HTML"
    ),
    /**
     * Echec de login : {@link STTypesEnum#FAIL_LOGIN}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#USER}
     */
    FAIL_LOGIN_USER_TEC(STTypesEnum.FAIL_LOGIN, STPorteesEnum.TECHNIQUE, STObjetsEnum.USER, "Echec de login"),
    /**
     * Echec de logout : {@link STTypesEnum#FAIL_LOGOUT}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#USER}
     */
    FAIL_LOGOUT_USER_TEC(STTypesEnum.FAIL_LOGOUT, STPorteesEnum.TECHNIQUE, STObjetsEnum.USER, "Echec de logout"),
    /**
     * Ajout de documents à la sélection
     */
    ADD_DOCUMENT_TO_SELECTION(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOCUMENT,
        "Ajout de documents à la sélection"
    ),
    /**
     * Échec de récupération de la statistique : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#STAT}
     */
    FAIL_GET_STAT_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.STAT,
        "Échec de récupération de la statistique"
    ),
    /**
     * Échec d'ouverture de session directory : {@link STTypesEnum#FAIL_OPEN}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#DIRECTORY}
     */
    FAIL_OPEN_DIRECTORY_TEC(
        STTypesEnum.FAIL_OPEN,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DIRECTORY,
        "Échec d'ouverture de session directory"
    ),
    /**
     * Échec de fermeture de session directory : {@link STTypesEnum#FAIL_CLOSE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#DIRECTORY}
     */
    FAIL_CLOSE_DIRECTORY_TEC(
        STTypesEnum.FAIL_CLOSE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DIRECTORY,
        "Échec de fermeture de session directory"
    ),
    /**
     * Échec d'ajout dans le journal du PAN :
     * {@link STTypesEnum#FAIL_ADD}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#PAN}
     */
    FAIL_AJOUT_JOURNAL_PAN(
        STTypesEnum.FAIL_ADD,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PAN,
        "Échec d'ajout dans le journal du PAN"
    ),

    // **************************************** ANOMALIES **************************************************
    /**
     * Anomalie d'occurrence de rattachement d'un poste aux ministères : {@link STTypesEnum#OCCURRENCE_MULTI}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#POSTE}
     */
    ANO_PST_MULTI_MIN_TEC(
        STTypesEnum.OCCURRENCE_MULTI,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.POSTE,
        "Poste rattaché à plusieurs ministères"
    ),
    /**
     * Anomalie d'occurrence de rattachement d'un poste à aucun ministère : {@link STTypesEnum#OCCURRENCE_NONE}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#POSTE}
     */
    ANO_PST_NONE_MIN_TEC(
        STTypesEnum.OCCURRENCE_NONE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.POSTE,
        "Poste rattaché à aucun ministère"
    ),
    /**
     * Evenement inattendu : {@link STTypesEnum#UNEXPECTED}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#EVENT}
     */
    UNEXPECTED_EVT_TEC(
        STTypesEnum.UNEXPECTED,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.EVENT,
        "Evenement (event) inattendu"
    ),
    /**
     * Schema inattendu : {@link STTypesEnum#UNEXPECTED}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#SCHEMA}
     */
    UNEXPECTED_SCHEMA_TEC(
        STTypesEnum.UNEXPECTED,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.SCHEMA,
        "Schéma inattendu - Document non reconnu"
    ),
    /**
     * Jeton inattendu : {@link STTypesEnum#UNEXPECTED}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#SCHEMA}
     */
    UNEXPECTED_JETON_TEC(STTypesEnum.UNEXPECTED, STPorteesEnum.TECHNIQUE, STObjetsEnum.JETON, "Jeton inattendu"),
    /**
     * Session inattendue : {@link STTypesEnum#UNEXPECTED}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#SESSION}
     */
    UNEXPECTED_SESSION_TEC(STTypesEnum.UNEXPECTED, STPorteesEnum.TECHNIQUE, STObjetsEnum.SESSION, "Session inattendue"),
    /**
     * Valeur null pour un paramètre : {@link STTypesEnum#NPE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#PARAM_METHODE}
     */
    NPE_PARAM_METH_TEC(
        STTypesEnum.NPE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PARAM_METHODE,
        "Paramètre de la méthode est null"
    ),
    /**
     * Plusieurs dossiers link trouvés : {@link STTypesEnum#OCCURRENCE_MULTI}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOSSIER_LINK}
     */
    ANO_DL_MULTI_TEC(
        STTypesEnum.OCCURRENCE_MULTI,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOSSIER_LINK,
        "Plus d'un DossierLink trouvé, selection du premier"
    ),
    /**
     * Plus d'un userworksapce trouvé : {@link STTypesEnum#OCCURRENCE_MULTI}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#USER_WORKSPACE}
     */
    ANO_WORKSPACE_MULTI_TEC(
        STTypesEnum.OCCURRENCE_MULTI,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.USER_WORKSPACE,
        "Plus d'un userworkspace trouvé pour l'utilisateur"
    ),
    /**
     * Transaction invalide (inactive ou marquée pour rollback) : {@link STTypesEnum#UNEXPECTED}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#TRANSACTION}
     */
    ANO_UNEXPECTED_TRANSACTION_TEC(
        STTypesEnum.UNEXPECTED,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.TRANSACTION,
        "Transaction invalide (inactive ou marquée pour rollback)"
    ),

    // **************************************** ACTIONS TECHNIQUES *****************************************
    /**
     * SEND_MAIL_TEC : {@link STTypesEnum#SEND}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#MAIL}
     */
    SEND_MAIL_TEC(STTypesEnum.SEND, STPorteesEnum.TECHNIQUE, STObjetsEnum.MAIL, "Envoi de mail"),
    /**
     * Dénormalisation de données d'un poste : {@link STTypesEnum#DENORMALISATION}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#POSTE}
     */
    DENO_PST_TEC(
        STTypesEnum.DENORMALISATION,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.POSTE,
        "Dénormalisation données du poste"
    ),
    /**
     * Début opération ST.Update.List : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#OPERATION_UPDATE_LIST}
     */
    INIT_OPERATION_UPDATE_LIST_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.OPERATION_UPDATE_LIST,
        "Début opération ST.Update.List"
    ),
    /**
     * Fin opération ST.Update.List : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#OPERATION_UPDATE_LIST}
     */
    END_OPERATION_UPDATE_LIST_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.OPERATION_UPDATE_LIST,
        "Fin opération ST.Update.List"
    ),
    /**
     * Opération ST.Update.List en cours : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#OPERATION_UPDATE_LIST}
     */
    PROCESS_OPERATION_UPDATE_LIST_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.OPERATION_UPDATE_LIST,
        "Opération ST.Update.List en cours"
    ),
    /**
     * Injection de Gouvernement en cours : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#INJECTION_GOUVERNEMENT}
     */
    PROCESS_INJECTION_GOUVERNEMENT_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.INJECTION_GOUVERNEMENT,
        "Injection de Gouvernement en cours"
    ),
    /**
     * Parser pour transformer une requete "xsd" en ufnxql. : {@link STTypesEnum#PARSE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#UFNXQL}
     */
    PARSE_XSD_TO_UFNXQL_TEC(
        STTypesEnum.PARSE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.UFNXQL,
        "Parser pour transformer une requete xsd en ufnxql"
    ),

    /**
     * Échec du parsing de la valeur : {@link STTypesEnum#PARSE}_{@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#PARAM_METHODE}
     */
    FAIL_PARSE_FONC(
        STTypesEnum.PARSE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.PARAM_METHODE,
        "Échec du parsing de la valeur"
    ),

    /**
     * Échec de création de la clé : {@link STTypesEnum#FAIL_GENERATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#KEY}
     */
    FAIL_GENERATE_KEY_TEC(
        STTypesEnum.FAIL_GENERATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.KEY,
        "Échec de création de la clé"
    ),

    /**
     * Échec de lecture du fichier : {@link STTypesEnum#READ}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#FILE}
     */
    FAIL_READ_FILE(STTypesEnum.READ, STPorteesEnum.TECHNIQUE, STObjetsEnum.FILE, "Échec de lecture d'un fichier"),

    // **************************************** CRÉATIONS *****************************************
    /**
     * Création d'un acteur : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#ACTEUR_TDR}
     */
    CREATE_ACTEUR_TEC(STTypesEnum.CREATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.ACTEUR_TDR, "Création d'un acteur"),
    /**
     * Création d'alerte fonctionnelle : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#ALERT}
     */
    CREATE_ALERT_FONC(
        STTypesEnum.CREATE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.ALERT,
        "Création d'alerte fonctionnelle"
    ),
    /**
     * Création d'alerte technique : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#ALERT}
     */
    CREATE_ALERT_TEC(STTypesEnum.CREATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.ALERT, "Création d'alerte technique"),
    /**
     * Création Dossier : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#DOSSIER}
     */
    CREATE_DOSSIER_TEC(STTypesEnum.CREATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.DOSSIER, "Création dossier"),
    /**
     * Création instance Feuille route : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#FDR}
     */
    CREATE_FDR_TEC(STTypesEnum.CREATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.FDR, "Création instance Feuille route"),
    /**
     * Création étape feuille route : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#STEP}
     */
    CREATE_STEP_TEC(STTypesEnum.CREATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.STEP, "Création étape feuille route"),
    /**
     * Création du lien pour l'événement : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#EVENT}
     */
    CREATE_EVENT_LINK_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.EVENT,
        "Création du lien pour l'événement"
    ),
    /**
     * Ajout fichier : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#FILE}
     */
    CREATE_FILE_FONC(
        STTypesEnum.CREATE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FILE,
        "Ajout d'un fichier dans un répertoire (fonc)"
    ),
    /**
     * Ajout fichier : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#FILE}
     */
    CREATE_FILE_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.FILE,
        "Ajout d'un fichier dans un répertoire (tec)"
    ),
    /**
     * Ajout repertoire : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#FOLDER}
     */
    CREATE_FOLDER_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.FOLDER,
        "Création d'un répertoire (tec)"
    ),
    /**
     * Ajout repertoire : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#FOLDER}
     */
    CREATE_FOLDER_FONC(
        STTypesEnum.CREATE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FOLDER,
        "Création d'un répertoire (fonc)"
    ),
    /**
     * Création d'un gouvernement : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#GOUVERNEMENT}
     */
    CREATE_GOUVERNEMENT_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.GOUVERNEMENT,
        "Création d'un gouvernement"
    ),
    /**
     * Création d'une identité : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#IDENTITE_TDR}
     */
    CREATE_IDENTITE_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.IDENTITE_TDR,
        "Création d'un identité"
    ),
    /**
     * Création d'un ministère : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#MINISTERE}
     */
    CREATE_MINISTERE_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.MINISTERE,
        "Création d'un ministère"
    ),
    /**
     * Création de jeton : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#JETON}
     */
    CREATE_JETON_TEC(STTypesEnum.CREATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.JETON, "Création de jeton"),

    // **************************************** UPDATE *****************************************
    /**
     * Mise à jour d'un acteur : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#ACTEUR_TDR}
     */
    UPDATE_ACTEUR_TEC(STTypesEnum.UPDATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.ACTEUR_TDR, "Mise à jour d'un acteur"),
    /**
     * mise à jour alerte : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#ALERT}
     */
    UPDATE_ALERT_TEC(STTypesEnum.UPDATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.ALERT, "Mise à jour alerte"),
    /**
     * mise à jour d'un commentaire' : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMENT}
     */
    UPDATE_COM_TEC(STTypesEnum.UPDATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.COMMENT, "Mise à jour d'un commentaire'"),
    /**
     * mise à jour dossier : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#DOSSIER}
     */
    UPDATE_DOSSIER_TEC(STTypesEnum.UPDATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.DOSSIER, "Mise à jour du dossier"),
    /**
     * mise à jour dossier : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#DOSSIER}
     */
    UPDATE_DOSSIER_FONC(
        STTypesEnum.UPDATE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.DOSSIER,
        "Mise à jour fonctionnelle du dossier"
    ),
    /**
     * mise à jour fichier : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#FILE}
     */
    UPDATE_FILE_TEC(STTypesEnum.UPDATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.FILE, "Mise à jour du fichier"),
    /**
     * mise à jour fichier : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#FILE}
     */
    UPDATE_FILE_FONC(
        STTypesEnum.UPDATE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FILE,
        "Mise à jour fonctionnelle du fichier"
    ),
    /**
     * mise à jour repertoire : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#FOLDER}
     */
    UPDATE_FOLDER_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.FOLDER,
        "Mise à jour d'un répertoire (tec)"
    ),
    /**
     * mise à jour repertoire : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#FOLDER}
     */
    UPDATE_FOLDER_FONC(
        STTypesEnum.UPDATE,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.FOLDER,
        "Mise à jour d'un répertoire (fonc)"
    ),
    /**
     * Mise à jour d'un gouvernement : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#GOUVERNEMENT}
     */
    UPDATE_GOUVERNEMENT_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.GOUVERNEMENT,
        "Mise à jour d'un gouvernement"
    ),
    /**
     * Mise à jour d'une identité : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#IDENTITE_TDR}
     */
    UPDATE_IDENTITE_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.IDENTITE_TDR,
        "Mise à jour d'un identité"
    ),
    /**
     * Mise à jour LDAP : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#LDAP}
     */
    UPDATE_LDAP_TEC(STTypesEnum.UPDATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.LDAP, "Mise à jour LDAP"),
    /**
     * Mise à jour d'un mandat : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#MANDAT}
     */
    UPDATE_MANDAT_TEC(STTypesEnum.UPDATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.MANDAT, "Mise à jour d'un mandat"),
    /**
     * Mise à jour d'un ministère : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#MINISTERE}
     */
    UPDATE_MINISTERE_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.MINISTERE,
        "Mise à jour d'un ministère"
    ),
    /**
     * mise à jour requête experte : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#REQ_EXPERTE}
     */
    UPDATE_REQ_EXP_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.REQ_EXPERTE,
        "Mise à jour requête experte"
    ),
    /**
     * Mise à jour de table de référence : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#TABLE_REFERENCE}
     */
    UPDATE_TABLE_REFERENCE_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.TABLE_REFERENCE,
        "Mise à jour de table de référence"
    ),
    /**
     * Mise à jour d'acls : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#ACLS}
     */
    UPDATE_ACLS_TEC(STTypesEnum.UPDATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.ACLS, "Mise à jour d'acls"),

    // *************************************** DENOMBREMENT *****************************************
    /**
     * Dénombrement de document : {@link STTypesEnum#COUNT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOCUMENT}
     */
    COUNT_DOCUMENT_TEC(STTypesEnum.COUNT, STPorteesEnum.TECHNIQUE, STObjetsEnum.DOCUMENT, "Dénombrement de document"),

    // **************************************** UTILISATIONS *****************************************
    /**
     * Utilisation de l'objet solver de classe : {@link STTypesEnum#USE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#SOLVER}
     */
    USE_SOLVER_TEC(
        STTypesEnum.USE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.SOLVER,
        "Utilisation de l'objet solver de classe"
    ),

    // **************************************** LECTURES *****************************************
    /**
     * Lecture requête UFNXQL : {@link STTypesEnum#READ}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#UFNXQL}
     */
    READ_UFNXQL_TEC(STTypesEnum.READ, STPorteesEnum.TECHNIQUE, STObjetsEnum.UFNXQL, "Lecture requête UFNXQL"),
    /**
     * Erreur lors de l'execution d'une requête UFNXQL : {@link STTypesEnum#FAIL_EXECUTE}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#UFNXQL}
     */
    FAIL_EXECUTE_UFNXQL_TEC(
        STTypesEnum.FAIL_EXECUTE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.UFNXQL,
        "Erreur lors de l'execution d'une requête UFNXQL"
    ),

    FAIL_GET_UFNXQL_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.UFNXQL,
        "Echec de récupération requête UFNXQL"
    ),

    // **************************************** QUERY ASSEMBLER *****************************************
    /**
     * Utilisation de l'objet query assembler : {@link STTypesEnum#USE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#QUERY_ASSEMBLER}
     */
    USE_QUERYASSEMBLER_TEC(
        STTypesEnum.USE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.QUERY_ASSEMBLER,
        "Utilisation de l'objet assembler"
    ),
    /**
     * Assembler queryAssembler non trouvé : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#QUERY_ASSEMBLER}
     */
    FAIL_GET_QUERYASSEMBLER_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.QUERY_ASSEMBLER,
        "Assembler de classe non trouvé"
    ),
    /**
     * Propriété non trouvé : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#PROPERTY}
     */
    FAIL_GET_PROPERTY_TEC(STTypesEnum.FAIL_GET, STPorteesEnum.TECHNIQUE, STObjetsEnum.PROPERTY, "Propriété non trouvé"),
    /**
     * Impossible d'assigner une propriété : {@link STTypesEnum#FAIL_SET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#PROPERTY}
     */
    FAIL_SET_PROPERTY_TEC(
        STTypesEnum.FAIL_SET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PROPERTY,
        "Impossible d'assigner une propriété"
    ),

    // **************************************** LDAP *****************************************
    /**
     * FAIL UNREGISTER LDAP DIRECTORY: {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#LDAP}
     */
    FAIL_UNREGISTER_LDAP_DIRECTORY_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.LDAP,
        "DirectoryFactory a rencontré une erreur."
    ),
    /**
     * FAIL STARTUP AND CONFIGURE APACHE DS: {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#LDAP}
     */
    FAIL_STARTSUP_AND_CONFIGURES_APACHEDS_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.LDAP,
        "Erreur lors du démarrage et la configuration du serveur apache"
    ),
    /**
     * FAIL CREATE LDAP GROUP: {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#LDAP}
     */
    FAIL_CREATE_LDAP_GROUP_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.LDAP,
        "Erreur lors de la création d'un groupe LDAP"
    ),
    /**
     * FAIL CREATE LDAP USER: {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#LDAP}
     */
    FAIL_CREATE_LDAP_USER_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.LDAP,
        "Erreur lors de la création d'un utilisateur."
    ),
    /**
     * FAIL_INIT_LDAP_SCHEMA: {@link STTypesEnum#FAIL_INITIALISATION}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#LDAP}
     */
    FAIL_INIT_LDAP_SCHEMA_TEC(
        STTypesEnum.FAIL_INITIALISATION,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.LDAP,
        "Impossible d'initialiser le schéma"
    ),
    /**
     * Distribution de la communication. : {@link STTypesEnum#DISTRIBUTION}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMUNICATION}
     */
    DISTRIBUTION_COMM_IN_MAIL_BOX_TEC(
        STTypesEnum.DISTRIBUTION,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Distribution de la communication dans la mailbox."
    ),
    /**
     * Création d'une nouvelle communication. : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMUNICATION}
     */
    CREATE_COMMUNICATION_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Création d'une communication"
    ),
    /**
     * Création d'une pièce jointe : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#PIECE_JOINTE}
     */
    CREATE_PIECE_JOINTE_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PIECE_JOINTE,
        "Création d'une pièce jointe"
    ),
    /**
     * Modification d'une pièce jointe pour la communication. : {@link STTypesEnum#CREATE}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#COMMUNICATION}
     */
    UPDATE_PIECE_JOINTE_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PIECE_JOINTE,
        "Modification d'une pièce jointe"
    ),
    /**
     * Modification de la communication. : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMUNICATION}
     */
    UPDATE_COMMUNICATION_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Modification de la communication"
    ),
    /**
     * Publication de la communication. : {@link STTypesEnum#PUBLICATION}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMUNICATION}
     */
    PUBLICATION_COMMUNICATION_TEC(
        STTypesEnum.PUBLICATION,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Publication de la communication"
    ),
    /**
     * Demande d'annulation de la communication. : {@link STTypesEnum#REQUEST}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMUNICATION}
     */
    REQUEST_COMMUNICATION_CANCELLATION_TEC(
        STTypesEnum.REQUEST,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Demande d'annulation de la communication"
    ),
    /**
     * Annulation de la communication. : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMUNICATION}
     */
    CANCEL_COMMUNICATION_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Annulation de la communication"
    ),
    /**
     * Suppression de la communication. : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMUNICATION}
     */
    DEL_COMMUNICATION_TEC(
        STTypesEnum.DELETE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Suppression de la communication"
    ),
    /**
     * Accusé de réception du message de l'émetteur pour la communication. : {@link STTypesEnum#ACKNOWLEDGMENT}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#COMMUNICATION}
     */
    ACKNOWLEDGMENT_MESSAGE_FROM_COMM_TRANSMITTER_TEC(
        STTypesEnum.ACKNOWLEDGMENT,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Accusé de réception du message de l'émetteur pour la communication."
    ),
    /**
     * Echec d'envoi de la notification : {@link STTypesEnum#FAIL_SEND}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMUNICATION}
     */
    FAIL_SEND_NOTIFICATION_COMMUNICATION_WS_TEC(
        STTypesEnum.FAIL_SEND,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Echec d'envoi de la notification"
    ),
    /**
     * Erreur de récupération de l'url de l'application dans les parametres : {@link STTypesEnum#FAIL_GET}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#URL}
     */
    FAIL_GET_APPLICATION_URL_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.URL,
        "Erreur de récupération de l'url de l'application dans les parametres"
    ),

    // *****************************************MAIL_BOX**************************
    /**
     * Récupération de la mailbox de l'institution : {@link STTypesEnum#GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#MAIL_BOX}
     */
    GET_INSTITUTION_MAIL_BOX(
        STTypesEnum.GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.MAIL_BOX,
        "Récupération de la mailbox de l'institution"
    ),

    /**
     * Récupération de la mailbox de l'institution : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#MAIL_BOX}
     */
    FAIL_GET_INSTITUTION_MAIL_BOX(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.MAIL_BOX,
        "Echec récupération de la mailbox de l'institution"
    ),

    // ********************************TABLE DE REFERENCE***************************************
    /**
     * Erreur lors de la mise à jour d'une table de référence: {@link STTypesEnum#FAIL_UPDATE}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#TABLE_REFERENCE}
     */
    FAIL_UPDATE_TABLE_REFERENCE_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.TABLE_REFERENCE,
        "Erreur lors de la mise à jour d'une table de référence."
    ),
    /**
     * Echec de récupération d'une table de référence: {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#TABLE_REFERENCE}
     */
    FAIL_GET_TABLE_REFERENCE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.TABLE_REFERENCE,
        "Echec de récupération d'une table de référence."
    ),
    /**
     * LOCK ORGANIGRAMME NODE : {@link STTypesEnum#LOCK}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#ORGANIGRAMME}
     */
    LOCK_ORGANIGRAMME_TEC(
        STTypesEnum.LOCK,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.ORGANIGRAMME,
        "Verrouiller le noeud de l'organigramme"
    ),
    /**
     * Navigation vers la vue corbeille : {@link STTypesEnum#NAVIGATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#CORBEILLE}
     */
    NAVIGATION_TO_CORBEILLE_VIEW_TEC(
        STTypesEnum.NAVIGATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.CORBEILLE,
        "Navigation vers la vue corbeille"
    ),
    /**
     * Erreur lors de la récupération d'un type d'evenement. {@link STTypesEnum#FAIL_GET}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#EVENT_TYPE}
     */
    FAIL_GET_EVENT_TYPE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.EVENT_TYPE,
        "Erreur lors de la récupération d'un type d'evenement"
    ),
    /**
     * Chargement de l'historique du dossier. {@link STTypesEnum#LOADING}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOSSIER}
     */
    LOADING_DOSSIER_HISTORY_TEC(
        STTypesEnum.LOADING,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOSSIER,
        "Chargement de l'historique du dossier"
    ),
    /**
     * Navigation vers la communication. {@link STTypesEnum#FAIL_NAVIGATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#COMMUNICATION}
     */
    FAIL_NAVIGATE_TO_COMMUNICATION_TEC(
        STTypesEnum.FAIL_NAVIGATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Navigation vers la communication"
    ),

    // *************************************************Modules
    // deployment******************************************************************
    /**
     * REMOVE WORKSPACE ACTIONS BEAN : {@link STTypesEnum#DESTROY}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BEAN}
     */
    REMOVE_WORKSPACE_ACTIONS_BEAN_TEC(
        STTypesEnum.DESTROY,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BEAN,
        "Removing user workspace actions bean"
    ),
    /**
     * Remove FileTreeManagerActionsBean : {@link STTypesEnum#DESTROY}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BEAN}
     */
    DESTROY_FILE_TREE_MANAGER_BEAN_TEC(
        STTypesEnum.DESTROY,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BEAN,
        "Unregistering FileTreeManagerBean"
    ),
    /**
     * Remove FondDeDossierManagerActionsBean : {@link STTypesEnum#DESTROY}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BEAN}
     */
    DESTROY_FDD_MANAGER_BEAN_TEC(
        STTypesEnum.DESTROY,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BEAN,
        "Unregistering FondDeDossierManagerActionsBean"
    ),
    /**
     * Init Bean : {@link STTypesEnum#DESTROY}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BEAN}
     */
    INIT_BEAN_TEC(STTypesEnum.INIT, STPorteesEnum.TECHNIQUE, STObjetsEnum.BEAN, "Initialisation Bean"),
    /**
     * Remove Bean : {@link STTypesEnum#DESTROY}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BEAN}
     */
    DESTROY_BEAN_TEC(STTypesEnum.DESTROY, STPorteesEnum.TECHNIQUE, STObjetsEnum.BEAN, "Unregistering Bean"),
    /**
     * Read Bean : {@link STTypesEnum#READ}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BEAN}
     */
    READ_BEAN_TEC(STTypesEnum.READ, STPorteesEnum.TECHNIQUE, STObjetsEnum.BEAN, "Read Bean"),
    /**
     * Save Bean : {@link STTypesEnum#SAVE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BEAN}
     */
    SAVE_BEAN_TEC(STTypesEnum.SAVE, STPorteesEnum.TECHNIQUE, STObjetsEnum.BEAN, "Save Bean"),

    // *********************************************************** USER *****************************************
    /**
     * Impossible de valider les mots de passe : {@link STTypesEnum#FAIL_VALIDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#USER_MANAGER}
     */
    FAIL_VALIDATE_PASSWORDS_TEC(
        STTypesEnum.FAIL_VALIDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.USER_MANAGER,
        "Impossible de valider les mots de passe"
    ),
    /**
     * Impossible d'enregistrer le mot de passe : {@link STTypesEnum#FAIL_SAVE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#PASSWORD}
     */
    FAIL_SAVE_PWD_TEC(
        STTypesEnum.FAIL_SAVE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PASSWORD,
        "Impossible d'enregistrer le mot de passe"
    ),
    /**
     * Notification password : {@link STTypesEnum#NOTIFICATION}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#PASSWORD}
     */
    NOTIFICATION_PASSWORD_FONC(
        STTypesEnum.NOTIFICATION,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.PASSWORD,
        "Notification password"
    ),
    /**
     * Mise à jour user : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#USER}
     */
    UPDATE_USER_TEC(STTypesEnum.UPDATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.USER, "Mise à jour de l'utilisateur"),
    /**
     * Création user : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#USER}
     */
    CREATE_USER_TEC(STTypesEnum.CREATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.USER, "Création de l'utilisateur"),

    /**
     * Echec de mise à jour de l'utilisateur : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#USER}
     */
    FAIL_UPDATE_USER(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.USER,
        "Echec de mise à jour de l'utilisateur"
    ),
    /**
     * Echec de mise à jour du poste : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#POSTE}
     */
    FAIL_UPDATE_POSTE_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.POSTE,
        "Echec de mise à jour du poste"
    ),

    // ***********************************************************META DONNEE*****************************************
    /**
     * FAIL GET META DONNEE : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#META_DONNEE}
     */
    FAIL_GET_META_DONNEE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.META_DONNEE,
        "Erreur lors de la récupération des metadonnées"
    ),
    /**
     * FAIL GET META DONNEE PIECE JOINTE : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#PIECE_JOINTE}
     */
    FAIL_GET_METADONNEES_PIECE_JOINTE(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.PIECE_JOINTE,
        "Erreur lors de la récupération d'une piece jointe"
    ),
    /**
     * Mise à jour de métadonnée : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#META_DONNEE}
     */
    UPDATE_METADONNEE_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.META_DONNEE,
        "Mise à jour de métadonnée"
    ),
    /**
     * Récupération métadonnée : {@link STTypesEnum#GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#META_DONNEE}
     */
    GET_METADONNEE_TEC(STTypesEnum.GET, STPorteesEnum.TECHNIQUE, STObjetsEnum.META_DONNEE, "Récupération métadonnée"),

    // **************************************** BATCHS ***********************************************************
    /**
     * Initialisation batch : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BATCH}
     */
    INIT_BATCH_TEC(STTypesEnum.INIT, STPorteesEnum.TECHNIQUE, STObjetsEnum.BATCH, "Lancement batch"),
    /**
     * Fin de batch : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BATCH}
     */
    END_BATCH_TEC(STTypesEnum.END, STPorteesEnum.TECHNIQUE, STObjetsEnum.BATCH, "Fin de batch"),
    /**
     * Exécution du batch : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BATCH}
     */
    PROCESS_BATCH_TEC(STTypesEnum.PROCESS, STPorteesEnum.TECHNIQUE, STObjetsEnum.BATCH, "Traitement du batch"),
    /**
     * Echec d'exécution du batch : {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH}
     */
    FAIL_PROCESS_BATCH_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH,
        "Echec de l'exécution du batch"
    ),
    /**
     * Début du batch de déverrouillage des documents : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_UNLOCK_DOC}
     */
    INIT_B_UNLOCK_DOC_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_UNLOCK_DOC,
        "Début du batch de déverrouillage des documents"
    ),
    /**
     * Echec dans l'excécution du batch de déverrouillage des documents : {@link STTypesEnum#FAIL_PROCESS}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BATCH_UNLOCK_DOC}
     */
    FAIL_PROCESS_B_UNLOCK_DOC_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_UNLOCK_DOC,
        "Echec dans l'exécution du batch de déverrouillage des documents"
    ),
    /**
     * Fin du batch de déverrouillage des documents : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_UNLOCK_DOC}
     */
    END_B_UNLOCK_DOC_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_UNLOCK_DOC,
        "Fin du batch de déverrouillage des documents"
    ),
    /**
     * Annulation du batch de déverrouillage des documents : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_UNLOCK_DOC}
     */
    CANCEL_B_UNLOCK_DOC_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_UNLOCK_DOC,
        "Annulation du batch de déverrouillage des documents"
    ),
    /**
     * Exécution du batch d'envoi des alertes : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_SEND_ALERTS}
     */
    PROCESS_B_SEND_ALERTS_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_SEND_ALERTS,
        "Exécution du batch d'envoi des alertes"
    ),
    /**
     * Annulation du batch d'envoi des alertes : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_SEND_ALERTS}
     */
    CANCEL_B_SEND_ALERTS_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_SEND_ALERTS,
        "Annulation du batch d'envoi des alertes"
    ),
    /**
     * Exécution du batch de suppression des dossiers links : {@link STTypesEnum#PROCESS}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BATCH_DEL_DL}
     */
    PROCESS_B_DEL_DL_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_DEL_DL,
        "Exécution du batch de suppression des dossiers links"
    ),
    /**
     * Début du batch de notification aux WS : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_NOTIF_WS}
     */
    INIT_B_NOTIF_WS_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_NOTIF_WS,
        "Début du batch de notification aux WS"
    ),
    /**
     * Echec dans l'exécution du batch de notification aux WS : {@link STTypesEnum#FAIL_PROCESS}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BATCH_NOTIF_WS}
     */
    FAIL_PROCESS_B_NOTIF_WS_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_NOTIF_WS,
        "Echec dans l'exécution du batch de notification aux WS"
    ),
    /**
     * Fin du batch de notification aux WS : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_NOTIF_WS}
     */
    END_B_NOTIF_WS_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_NOTIF_WS,
        "Fin du batch de notification aux WS"
    ),
    /**
     * Début du batch de suppression des documents : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_DEL_DOC}
     */
    INIT_B_DEL_DOC_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_DEL_DOC,
        "Début du batch de suppression des documents"
    ),
    /**
     * Excécution du batch de suppression des documents : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_DEL_DOC}
     */
    PROCESS_B_DEL_DOC_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_DEL_DOC,
        "Exécution du batch de suppression des documents"
    ),
    /**
     * Fin du batch de suppression des documents : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_DEL_DOC}
     */
    END_B_DEL_DOC_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_DEL_DOC,
        "Fin du batch de suppression des documents"
    ),
    /**
     * Annulation du batch de suppression des documents : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_DEL_DOC}
     */
    CANCEL_B_DEL_DOC_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_DEL_DOC,
        "Annulation du batch de suppression des documents"
    ),
    /**
     * Début du batch de prévenance de renouvellement de mot de passe : {@link STTypesEnum#INIT}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BATCH_DAILY_REMINDER_CHANGE_PASS}
     */
    INIT_B_DAILY_MAIL_CHANGE_PASS_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_DAILY_REMINDER_CHANGE_PASS,
        "Début du batch de prévenance de renouvellement de mot de passe"
    ),
    /**
     * Echec du batch d'envoi journalier de mèl de prévenance de renouvellement de mot de passe :
     * {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_DAILY_REMINDER_CHANGE_PASS}
     */
    FAIL_B_DAILY_MAIL_CHANGE_PASS_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_DAILY_REMINDER_CHANGE_PASS,
        "Echec du batch de prévenance de renouvellement de mot de passe"
    ),
    /**
     * Fin du batch d'envoi journalier de mèl de prévenance de renouvellement de mot de passe : {@link STTypesEnum#END}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BATCH_DAILY_REMINDER_CHANGE_PASS}
     */
    END_B_DAILY_MAIL_CHANGE_PASS_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_DAILY_REMINDER_CHANGE_PASS,
        "Fin du batch de prévenance de renouvellement de mot de passe"
    ),
    /**
     * Annulation du batch d'envoi journalier de mèl de prévenance de renouvellement de mot de passe : {@link STTypesEnum#CANCEL}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BATCH_DAILY_REMINDER_CHANGE_PASS}
     */
    CANCEL_B_DAILY_MAIL_CHANGE_PASS_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_DAILY_REMINDER_CHANGE_PASS,
        "Annulation du batch de prévenance de renouvellement de mot de passe"
    ),

    INIT_B_LANCEUR_GENERAL_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_LANCEUR_GENERAL,
        "Début du batch lanceur général"
    ),
    /**
     * Echec dans l'exécution du batch lanceur général : {@link STTypesEnum#FAIL_PROCESS}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BATCH_LANCEUR_GENERAL}
     */
    FAIL_PROCESS_B_LANCEUR_GENERAL_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_LANCEUR_GENERAL,
        "Echec dans l'exécution du batch lanceur général"
    ),
    /**
     * Fin du batch de lanceur général : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_LANCEUR_GENERAL}
     */
    END_B_LANCEUR_GENERAL_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_LANCEUR_GENERAL,
        "Fin du batch lanceur général"
    ),
    /**
     * Annulation du batch de lanceur général : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_LANCEUR_GENERAL}
     */
    CANCEL_B_LANCEUR_GENERAL_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_LANCEUR_GENERAL,
        "Annulation du batch lanceur général"
    ),
    /**
     * Echec de création de BatchResult : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_RESULT}
     */
    FAIL_CREATE_BATCH_RESULT_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_RESULT,
        "Echec de création de log de résultat de batch"
    ),
    /**
     * Début du batch de déverrouillage de l'organigramme : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BATCH_UNLOCK_ORGA}
     */
    INIT_B_UNLOCK_ORGA_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_UNLOCK_ORGA,
        "Début du batch de déverrouillage de l'organigramme"
    ),
    /**
     * Fin du batch de déverrouillage de l'organigramme : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BATCH_UNLOCK_ORGA}
     */
    END_B_UNLOCK_ORGA_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_UNLOCK_ORGA,
        "Fin du batch de déverrouillage de l'organigramme"
    ),
    /**
     * Annulation du batch de déverrouillage de l'organigramme : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#BATCH_UNLOCK_ORGA}
     */
    CANCEL_B_UNLOCK_ORGA_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_UNLOCK_ORGA,
        "Annulation du batch de déverrouillage de l'organigramme"
    ),

    // ************************************** FEV550 : Purge de la table tentatives de connexion ***********************************************
    /**
     * Début du batch de purge de la table tentatives de connexion :
     * {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_PURGE_TENTATIVES_CONNEXION}
     */
    INIT_B_PURGE_TENTATIVES_CONNEXION_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_PURGE_TENTATIVES_CONNEXION,
        "Début du batch de purge de la table des tentatives de connexion"
    ),
    /**
     * Fin du batch de purge de la table tentatives de connexion :
     * {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_PURGE_TENTATIVES_CONNEXION}
     */
    END_B_PURGE_TENTATIVES_CONNEXION_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_PURGE_TENTATIVES_CONNEXION,
        "Fin du batch de purge de la table des tentatives de connexion"
    ),
    /**
     * Annulation du batch de purge de la table tentatives de connexion :
     * {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#BATCH_PURGE_TENTATIVES_CONNEXION}
     */
    CANCEL_B_PURGE_TENTATIVES_CONNEXION_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.BATCH_PURGE_TENTATIVES_CONNEXION,
        "Annulation du batch de purge de la table des tentatives de connexion"
    ),

    // *******************************************************MIGRATION*************************************************************************************
    /**
     * Migration d'entité : {@link STTypesEnum#MIGRATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#ENTITE_NODE}
     */
    MIGRATE_ENTITE_TEC(STTypesEnum.MIGRATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.ENTITE_NODE, "Migration d'entité"),
    /**
     * Migration ministère : {@link STTypesEnum#MIGRATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#MINISTERE}
     */
    MIGRATE_MINISTERE_TEC(STTypesEnum.MIGRATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.MINISTERE, "Migration ministère"),
    /**
     * Migration dossier : {@link STTypesEnum#MIGRATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#DOSSIER}
     */
    MIGRATE_DOSSIER_TEC(STTypesEnum.MIGRATE, STPorteesEnum.TECHNIQUE, STObjetsEnum.DOSSIER, "Migration dossier"),
    /**
     * Echec de migration du dossier : {@link STTypesEnum#FAIL_MIGRATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#DOSSIER}
     */
    FAIL_MIGRATE_DOSSIER_TEC(
        STTypesEnum.FAIL_MIGRATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOSSIER,
        "Echec de migration du dossier"
    ),
    /**
     * Echec de migration du ministère : {@link STTypesEnum#FAIL_MIGRATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#MINISTERE}
     */
    FAIL_MIGRATE_MINISTERE_TEC(
        STTypesEnum.FAIL_MIGRATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.MINISTERE,
        "Echec de migration du ministère"
    ),
    // ******************************************************************EVENT***************************************************************
    /**
     * Début de l'évènement : {@link STTypesEnum#START}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#EVENT}
     */
    START_EVENT_TEC(STTypesEnum.START, STPorteesEnum.TECHNIQUE, STObjetsEnum.EVENT, "Début de l'évènement"),
    /**
     * Fin de l'évènement : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#EVENT}
     */
    END_EVENT_TEC(STTypesEnum.END, STPorteesEnum.TECHNIQUE, STObjetsEnum.EVENT, "Fin de l'évènement"),

    /**
     * Alerte danger
     */
    WARNING_TEC(STTypesEnum.DEFAULT, STPorteesEnum.TECHNIQUE, STObjetsEnum.DEFAULT, "Attention danger !");

    private STCodes type;
    private STCodes portee;
    private STCodes objet;
    private String text;

    STLogEnumImpl(STTypesEnum type, STPorteesEnum portee, STObjetsEnum objet, String text) {
        this.type = type;
        this.portee = portee;
        this.objet = objet;
        this.text = text;
    }

    @Override
    public String getCode() {
        StringBuilder code = new StringBuilder();
        code
            .append(type.getCodeNumberStr())
            .append(SEPARATOR_CODE)
            .append(portee.getCodeNumberStr())
            .append(SEPARATOR_CODE)
            .append(objet.getCodeNumberStr());
        return code.toString();
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public String toString() {
        StringBuilder loggable = new StringBuilder();
        loggable.append("[CODE:").append(getCode()).append("] ").append(this.text);
        return loggable.toString();
    }
}
