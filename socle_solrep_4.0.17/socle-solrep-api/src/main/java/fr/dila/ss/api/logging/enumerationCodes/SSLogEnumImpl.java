package fr.dila.ss.api.logging.enumerationCodes;

import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.enumerationCodes.STCodes;
import fr.dila.st.api.logging.enumerationCodes.STObjetsEnum;
import fr.dila.st.api.logging.enumerationCodes.STPorteesEnum;
import fr.dila.st.api.logging.enumerationCodes.STTypesEnum;

/**
 * Enumération des logs info codifiés du SS
 * Code : 000_000_000 <br />
 */
public enum SSLogEnumImpl implements STLogEnum {
    //************************** SUPPRESSION DE DOCUMENTS ***********************************************
    /**
     * DELETE_STEP : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#STEP}
     */
    DEL_STEP_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, SSObjetsEnum.STEP, "Suppression étape"),
    /**
     * DELETE_FDR : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#FDR}
     */
    DEL_FDR_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, SSObjetsEnum.FDR, "Suppression feuille de route"),
    /**
     * DELETE FILE FDD : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#FILE_FDD}
     */
    DEL_FILE_FDD_TEC(
        STTypesEnum.DELETE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.FDD_FILE,
        "Suppression fichier de fond de dossier"
    ),
    /**
     * Suppression rapport birt : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BIRT}
     */
    DEL_BIRT_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, SSObjetsEnum.BIRT, "Suppression rapport birt"),
    /**
     * Suppression fichier parapheur : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#PARAPHEUR_FILE}
     */
    DEL_PARAPHEUR_FILE_TEC(
        STTypesEnum.DELETE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.PARAPHEUR_FILE,
        "Suppression fichier de parapheur"
    ),
    //************************** CREATION DE DOCUMENTS ***********************************************
    /**
     * Création de fichier de fond de dossier : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#FDD_FILE}
     */
    CREATE_FILE_FDD_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.FDD_FILE,
        "Création de fichier de fond de dossier"
    ),
    /**
     * Génération d'un rapport birt : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BIRT}
     */
    CREATE_BIRT_TEC(STTypesEnum.CREATE, STPorteesEnum.TECHNIQUE, SSObjetsEnum.BIRT, "Génération d'un rapport birt"),

    /**
     * Génération d'un modèle de feuille de route : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#FDR}
     */
    CREATE_FDR_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.FDR,
        "Génération d'un modèle de feuille de route"
    ),

    //**************************************** RECUPERATIONS **************************************************
    /**
     * Récupération d'étape de feuille de route : {@link STTypesEnum#GET}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#STEP}
     */
    GET_STEP_TEC(
        STTypesEnum.GET,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.STEP,
        "Récupération d'étape de feuille de route"
    ),
    /**
     * Récupération de modèle de feuille de route : {@link STTypesEnum#GET}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#MODELE_FDR}
     */
    GET_MOD_FDR_TEC(
        STTypesEnum.GET,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.MODELE_FDR,
        "Récupération de modèle de feuille de route"
    ),

    //**************************************** ECHEC D'ACTIONS **************************************************
    /**
     * FAIL_GET_STEP : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#STEP}
     */
    FAIL_GET_STEP_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.STEP,
        "Echec récupération étape de feuille de route"
    ),
    /**
     * FAIL_UPDATE_STEP : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#STEP}
     */
    FAIL_UPDATE_STEP_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.STEP,
        "Echec modification étape"
    ),
    /**
     * FAIL_GET_FDR : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#FDR}
     */
    FAIL_GET_FDR_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.FDR,
        "Echec récupération de feuille de route"
    ),
    /**
     * Echec de récupération de feuille de route : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#FONCTIONNELLE}_{@link SSObjetsEnum#FDR}
     */
    FAIL_GET_FDR_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.FDR,
        "Echec récupération de feuille de route (fonc)"
    ),
    /**
     * FAIL_UPDATE_FDR : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#FDR}
     */
    FAIL_UPDATE_FDR_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.FDR,
        "Echec modification de feuille de route"
    ),
    /**
     * FAIL_UPDATE_FDR : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#FONCTIONNELLE}_{@link SSObjetsEnum#FDR}
     */
    FAIL_UPDATE_FDR_FONC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.FONCTIONNELLE,
        SSObjetsEnum.FDR,
        "Echec modification de feuille de route (fonc)"
    ),
    /**
     * FAIL_CREATE_GROUPE_POLITIQUE : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#GROUPE_POLITIQUE}
     */
    FAIL_CREATE_GROUPE_POLITIQUE_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.GROUPE_POLITIQUE,
        "Echec création de groupe politique"
    ),
    /**
     * Echec de génération du rapport birt : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BIRT}
     */
    FAIL_CREATE_BIRT_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BIRT,
        "Echec de génération du rapport birt"
    ),
    /**
     * Echec de mise à jour du fichier de fond de dossier : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#FDD_FILE}
     */
    FAIL_UPDATE_FDD_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.FDD_FILE,
        "Echec de mise à jour du fichier de fond de dossier"
    ),
    /**
     * Echec de suppression du fichier de fond de dossier : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#FDD_FILE}
     */
    FAIL_DEL_FILE_FDD_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.FDD_FILE,
        "Echec de suppression du fichier de fond de dossier"
    ),
    /**
     * Echec de suppression du rapport birt : {@link STTypesEnum#FAIL_DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BIRT}
     */
    FAIL_DEL_BIRT_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BIRT,
        "Echec lors de la suppression du rapport birt"
    ),
    /**
     * Echec de sauvegarde du rapport birt : {@link STTypesEnum#FAIL_SAVE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BIRT}
     */
    FAIL_SAVE_BIRT_TEC(
        STTypesEnum.FAIL_SAVE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BIRT,
        "Echec de la sauvegarde du rapport birt"
    ),
    /**
     * Echec de mise à jour du fichier de parapheur : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#PARAPHEUR_FILE}
     */
    FAIL_UPDATE_PARAPHEUR_FILE_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.PARAPHEUR_FILE,
        "Echec de mise à jour du fichier de parapheur"
    ),
    /**
     * Echec de suppression du fichier de fond de dossier : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#FDD_FILE}
     */
    FAIL_DEL_PARAPHEUR_FILE_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.PARAPHEUR_FILE,
        "Echec de suppression du fichier de parapheur"
    ),
    /**
     * Echec de création d'un répertoire de fond de dossier : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#FDD_FOLDER}
     */
    FAIL_CREATE_FDD_FOLDER_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.FDD_FOLDER,
        "Echec de création d'un répertoire de fond de dossier"
    ),
    /**
     * Erreur lors de l'export des statistiques : {@link STTypesEnum#FAIL_EXPORT}_{@link STPorteesEnum#FONCTIONNELLE}_{@link SSObjetsEnum#STATS}
     */
    FAIL_EXPORT_STATS_FONC(
        STTypesEnum.FAIL_EXPORT,
        STPorteesEnum.FONCTIONNELLE,
        SSObjetsEnum.STATS,
        "Erreur d'export des statistiques"
    ),
    /**
     * Erreur lors de l'export des statistiques : {@link STTypesEnum#FAIL_EXPORT}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#STATS}
     */
    FAIL_EXPORT_STATS_TEC(
        STTypesEnum.FAIL_EXPORT,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.STATS,
        "Erreur d'export des statistiques"
    ),
    /**
     * Echec de réupération du fond de dossier : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#FDD}
     */
    FAIL_GET_FDD_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.FDD,
        "Echec de réupération du fond de dossier"
    ),
    /**
     * Echec d'ajout d'une note : {@link STTypesEnum#FAIL_ADD}_{@link STPorteesEnum#FONCTIONNELLE}_{@link SSObjetsEnum#NOTE}
     */
    FAIL_ADD_NOTE_FONC(
        STTypesEnum.FAIL_ADD,
        STPorteesEnum.FONCTIONNELLE,
        SSObjetsEnum.NOTE,
        "Echec d'ajout d'une note"
    ),
    /**
     * Echec d'ajout d'une note : {@link STTypesEnum#FAIL_ADD}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#NOTE}
     */
    FAIL_ADD_NOTE_TEC(
        STTypesEnum.FAIL_ADD,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.NOTE,
        "Echec d'ajout d'une note (tec)"
    ),
    /**
     * Echec de réattribution d'un dossier : {@link SSTypesEnum#FAIL_REATTR}_{@link STPorteesEnum#FONCTIONNELLE}_{@link STObjetsEnum#DOSSIER}
     */
    FAIL_REATTR_DOSSIER_FONC(
        SSTypesEnum.FAIL_REATTR,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.DOSSIER,
        "Echec de réattribution d'un dossier"
    ),
    /**
     * Echec de réattribution d'un dossier : {@link SSTypesEnum#FAIL_REATTR}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#DOSSIER}
     */
    FAIL_REATTR_DOSSIER_TEC(
        SSTypesEnum.FAIL_REATTR,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.DOSSIER,
        "Echec de réattribution d'un dossier (tec)"
    ),
    /**
     * Echec de validation d'étape : {@link STTypesEnum#FAIL_VALIDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#STEP}
     */
    FAIL_VALIDATE_STEP_TEC(
        STTypesEnum.FAIL_VALIDATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.STEP,
        "Echec de validation d'étape"
    ),
    /**
     * Echec de publication du rapport birt : {@link STTypesEnum#FAIL_PUBLISH}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BIRT}
     */
    FAIL_PUBLISH_BIRT_TEC(
        STTypesEnum.FAIL_PUBLISH,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BIRT,
        "Echec de publication du rapport birt"
    ),
    /**
     * Echec de récupération de modèle de feuille de route : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#MODELE_FDR}
     */
    FAIL_GET_MOD_FDR_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.MODELE_FDR,
        "Echec récupération de modèle de feuille de route"
    ),

    //**************************************** ACTIONS TECHNIQUES *****************************************
    /**
     * Mise à jour d'étapes de feuille de route : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#STEP}
     */
    UPDATE_STEP_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.STEP,
        "Mise à jour d'étapes de feuille de route"
    ),
    /**
     * Démarrage feuille de route : {@link STTypesEnum#START}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#FDR}
     */
    START_FDR_TEC(STTypesEnum.START, STPorteesEnum.TECHNIQUE, SSObjetsEnum.FDR, "Démarrage Feuille de route"),

    //**************************************** BATCH *****************************************
    /**
     * Execution du batch de dossiers bloqués : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_DOSS_BLOQUES}
     */
    PROCESS_B_DOSS_BLOQUES_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_DOSS_BLOQUES,
        "Exécution du batch de dossiers bloqués"
    ),
    /**
     * Annulation du batch de dossiers bloqués : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_DOSS_BLOQUES}
     */
    CANCEL_B_DOSS_BLOQUES_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_DOSS_BLOQUES,
        "Annulation du batch de dossiers bloqués"
    ),
    /**
     * Exécution du batch de detection incohérences des dossiers links : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_DL_INCOHERENT}
     */
    PROCESS_B_DL_INCOHERENT_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_DL_INCOHERENT,
        "Exécution du batch de détection d'incohérence des dossiers links"
    ),
    /**
     * Annulation du batch de detection incohérences des dossiers links : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_DL_INCOHERENT}
     */
    CANCEL_B_DL_INCOHERENT_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_DL_INCOHERENT,
        "Annulation du batch de détection d'incohérence des dossiers links"
    ),
    /**
     * Execution du batch de validation auto d'étapes : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_VALID_AUTO_STEP}
     */
    PROCESS_B_VALID_STEP_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_VALID_AUTO_STEP,
        "Execution batch validation automatique d'étapes"
    ),
    /**
     * Annulation du batch de validation auto d'étapes : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_VALID_AUTO_STEP}
     */
    CANCEL_B_VALID_STEP_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_VALID_AUTO_STEP,
        "Annulation batch validation automatique d'étapes"
    ),
    /**
     * Début du batch de déverrouillage de l'organigramme : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_UNLOCK_ORGA}
     */
    INIT_B_UNLOCK_ORGA_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_UNLOCK_ORGA,
        "Début du batch de déverrouillage de l'organigramme"
    ),
    /**
     * Fin du batch de déverrouillage de l'organigramme : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_UNLOCK_ORGA}
     */
    END_B_UNLOCK_ORGA_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_UNLOCK_ORGA,
        "Fin du batch de déverrouillage de l'organigramme"
    ),
    /**
     * Début du batch de suppression des utilisateurs : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_USERS_DELETION}
     */
    INIT_B_DEL_USERS_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_USERS_DELETION,
        "Début du batch de suppression des utilisateurs"
    ),
    /**
     * Fin du batch de suppression des utilisateurs : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_USERS_DELETION}
     */
    END_B_DEL_USERS_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_USERS_DELETION,
        "Fin du batch de suppression des utilisateurs"
    ),
    /**
     * Annulation du batch de suppression des utilisateurs : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_USERS_DELETION}
     */
    CANCEL_B_DEL_USERS_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_USERS_DELETION,
        "Annulation du batch de suppression des utilisateurs"
    ),
    /**
     * Echec dans l'exécution du batch de suppression des utilisateurs : {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_USERS_DELETION}
     */
    FAIL_PROCESS_B_DEL_USERS_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_USERS_DELETION,
        "Echec dans l'exécution du batch de suppression des utilisateurs"
    ),
    /**
     * Exécution du batch de suppression des utilisateurs : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_USERS_DELETION}
     */
    PROCESS_B_DEL_USERS_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_USERS_DELETION,
        "Exécution du batch de suppression des utilisateurs"
    ),
    /**
     * Début opération SS.FeuilleRoute.RunStep : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#OPERATION_RUN_STEP}
     */
    INIT_OPERATION_RUN_STEP(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.OPERATION_RUN_STEP,
        "Début opération SS.FeuilleRoute.RunStep"
    ),
    /**
     * Fin opération SS.FeuilleRoute.RunStep : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#OPERATION_RUN_STEP}
     */
    END_OPERATION_RUN_STEP(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.OPERATION_RUN_STEP,
        "Fin opération SS.FeuilleRoute.RunStep"
    ),
    /**
     * Opération SS.FeuilleRoute.RunStep en cours : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#OPERATION_RUN_STEP}
     */
    PROCESS_OPERATION_RUN_STEP(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.OPERATION_RUN_STEP,
        "Opération SS.FeuilleRoute.RunStep en cours"
    ),
    /**
     * Erreur lors du traitement SS.FeuilleRoute.RunStep : {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#OPERATION_RUN_STEP}
     */
    FAIL_OPERATION_RUN_STEP(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.OPERATION_RUN_STEP,
        "Erreur lors du traitement SS.FeuilleRoute.RunStep"
    ),

    //*******************************************************IUC************************************************************
    /**
     * GET_IUC_TEC : {@link STTypesEnum#GET}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#INFO_UTILISATEUR_CO}
     */
    GET_IUC_TEC(
        STTypesEnum.GET,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.INFO_UTILISATEUR_CO,
        "Récupération info utilisateur connexion"
    ),
    /**
     * UPDATE_IUC_TEC : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#INFO_UTILISATEUR_CO}
     */
    UPDATE_IUC_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.INFO_UTILISATEUR_CO,
        "Mise à jour info utilisateur connexion"
    ),
    /**
     * DELETE INFOUTILISATEURCONNECTION : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#INFO_UTILISATEUR_CO}
     */
    DEL_INFO_USER_CO_TEC(
        STTypesEnum.DELETE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.INFO_UTILISATEUR_CO,
        "Suppression infoUtilisateurConnexion"
    ),
    /**
     * Début du batch de déconnexion des utilisateurs : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_CLOSE_USERS_CONNEC}
     */
    INIT_B_CLOSE_USERS_CONNEC_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_CLOSE_USERS_CONNEC,
        "Début du batch de déconnexion des utilisateurs"
    ),
    /**
     * Echec dans l'execution du batch de déconnexion des utilisateurs : {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_CLOSE_USERS_CONNEC}
     */
    FAIL_PROCESS_B_CLOSE_USERS_CONNEC_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_CLOSE_USERS_CONNEC,
        "Echec dans l'exécution du batch de déconnexion des utilisateurs"
    ),
    /**
     * Fin du batch de déconnexion des utilisateurs : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_CLOSE_USERS_CONNEC}
     */
    END_B_CLOSE_USERS_CONNEC_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_CLOSE_USERS_CONNEC,
        "Fin du batch de déconnexion des utilisateurs"
    ),
    /**
     * Annulation du batch de déconnexion des utilisateurs : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#BATCH_CLOSE_USERS_CONNEC}
     */
    CANCEL_B_CLOSE_USERS_CONNEC_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_CLOSE_USERS_CONNEC,
        "Annulation du batch de déconnexion des utilisateurs"
    ),
    //****************************************************MIGRATION*********************************************************
    /**
     * Migration de modèles de feuille de route : {@link SSTypesEnum.MIGRATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#MODELE_FDR}
     */
    MIGRATE_MOD_FDR_TEC(
        STTypesEnum.MIGRATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.MODELE_FDR,
        "Migration modèles de feuilles de route"
    ),
    /**
     * Echec de migration des feuilles de route : {@link SSTypesEnum.FAIL_MIGRATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#FDR}
     */
    FAIL_MIGRATE_FDR_TEC(
        STTypesEnum.FAIL_MIGRATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.FDR,
        "Echec de migration des feuilles de route"
    ),
    /**
     * Echec de migration des modèles de feuille de route : {@link SSTypesEnum.FAIL_MIGRATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#MODELE_FDR}
     */
    FAIL_MIGRATE_MOD_FDR_TEC(
        STTypesEnum.FAIL_MIGRATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.MODELE_FDR,
        "Echec de migration des modèles de feuille de route"
    ),
    /**
     * Echec de migration erreur de brisure du cachet serveur sur un dossier {@link SSTypesEnum.FAIL_MIGRATE}_{@link STPorteesEnum#TECHNIQUE}_{@link SSObjetsEnum#SIGNATURE}
     */
    FAIL_BRISURE_SIGNATURE_TEC(
        STTypesEnum.FAIL_MIGRATE,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.SIGNATURE,
        "Echec de brisure du cachet des dossiers"
    ),
    /**
     * Début du batch de désactivation des utilisateurs : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_DEACTIVATE_USERS}
     */
    INIT_B_DEACTIVATE_USERS_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_DEACTIVATE_USERS,
        "Début du batch de désactivation des utilisateurs"
    ),
    /**
     * Exécution du batch de désactivation des utilisateurs : {@link STTypesEnum#PROCESS}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_DEACTIVATE_USERS}
     */
    PROCESS_B_DEACTIVATE_USERS_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_DEACTIVATE_USERS,
        "Exécution du batch de désactivation des utilisateurs"
    ),
    /**
     * Fin du batch de désactivation des utilisateurs : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_DEACTIVATE_USERS}
     */
    END_B_DEACTIVATE_USERS_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_DEACTIVATE_USERS,
        "Fin du batch de désactivation des utilisateurs"
    ),

    /**
     * Birt batch generation
     */
    BIRT_BATCH_TEC(STTypesEnum.ACTION_LOG, STPorteesEnum.TECHNIQUE, STObjetsEnum.DEFAULT, "Birt");

    private STCodes type;
    private STCodes portee;
    private STCodes objet;
    private String text;

    SSLogEnumImpl(STCodes type, STCodes portee, STCodes objet, String text) {
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
