package fr.dila.ss.api.logging.enumerationCodes;

import fr.dila.st.api.logging.enumerationCodes.STCodes;

/**
 * Énumération de l'objet des actions <br />
 * Décompte sur 3 chiffres, le premier (1) indique qu'il s'agit d'un objet du SS <br />
 * 100 : défaut <br />
 * 101 : Feuille de route (FDR) <br />
 * 102 : Étape de feuille de route (STEP) <br />
 * 103 : Groupe politique <br />
 * 104 : Birt <br />
 * 105 : Fichier du fond de dossier <br />
 * 106 : Fichier du parapheur <br />
 * 107 : Répertoire du fond de dossier <br />
 * 108 : Modèle de feuille de route <br />
 * 109 : statistiques <br />
 * 110 : Info utilisateur connexion <br />
 * 111 : Fond de dossier <br />
 *
 * 193 : Batch de déconnexions des utilisateurs <br />
 * 194 : Batch lanceur général <br />
 * 195 : Batch de suppression des utilisateurs <br />
 * 196 : Batch de déverrouillage de l'organigramme <br />
 * 197 : Batch de validation auto d'étapes <br />
 * 198 : Batch de détection d'incohérence dans les dossiers links <br />
 * 199 : Batch dossiers bloqués <br />
 */
public enum SSObjetsEnum implements STCodes {
    /**
     * 100 défaut
     */
    DEFAULT(100, "Objet défaut"),
    /**
     * 101 : Feuille de route
     */
    FDR(101, "Feuille de route"),
    /**
     * 102 :Étape de feuille de route (STEP)
     */
    STEP(102, "Étape de feuille de route"),
    /**
     * 103 : Groupe politique
     */
    GROUPE_POLITIQUE(103, "Groupe politique"),
    /**
     * 104 : Birt
     */
    BIRT(104, "Rapport birt"),
    /**
     * 105 : Fichier du fond de dossier
     */
    FDD_FILE(105, "Fichier du fond de dossier"),
    /**
     * 106 : Fichier du parapheur
     */
    PARAPHEUR_FILE(106, "Fichier du parapheur"),
    /**
     * 107 : Répertoire du fond de dossier
     */
    FDD_FOLDER(107, "Répertoire du fond de dossier"),
    /**
     * 108 : Modèle de feuille de route
     */
    MODELE_FDR(108, "Modèle de feuille de route"),
    /**
     * 109 : Statistiques
     */
    STATS(109, "Statistiques"),
    /**
     * 110 : InfoUtilisateurConnexion
     */
    INFO_UTILISATEUR_CO(110, "Info utilisateur connexion"),
    /**
     * 111 : Fond de dossier
     */
    FDD(111, "Fond de dossier"),
    /**
     * 112 : Note
     */
    NOTE(112, "Note"),
    /*
     * 113 : Signature
     */
    SIGNATURE(113, "Signature"),
    /*
     * 114 : Operation dossierDoneToRunning
     */
    OPERATION_RUN_STEP(114, "operation SS.FeuilleRoute.RunStep"),

    /**
     * 193 : Batch de déconnexion des utilisateurs
     */
    BATCH_CLOSE_USERS_CONNEC(193, "Batch de déconnexion des utilisateurs"),
    /**
     * 195 : Batch de suppression des utilisateurs
     */
    BATCH_USERS_DELETION(195, "Batch de suppression des utilisateurs"),
    /**
     * 196 : batch de déverrouillage de l'organigramme
     */
    BATCH_UNLOCK_ORGA(196, "Batch de déverrouillage de l'organigramme"),
    /**
     * 197 : batch de validation auto d'étapes
     */
    BATCH_VALID_AUTO_STEP(197, "Batch de validation auto d'étapes"),
    /**
     * 198 : Batch de détection d'incohérence dans les dossiers links
     */
    BATCH_DL_INCOHERENT(198, "Batch de détection d'incohérence dans les dossiers links"),
    /**
     * 199 : Batch dossiers bloqués
     */
    BATCH_DOSS_BLOQUES(199, "Batch dossiers bloqués"),
    /**
     * 489 : batch de désactivation des utilisateurs
     */
    BATCH_DEACTIVATE_USERS(489, "Désactivation utilisateurs");

    /* **** (Ne pas oublier de tenir à jour la documentation en lien avec le code) **** */

    private int codeNumber;
    private String codeText;

    SSObjetsEnum(int codeNumber, String codeText) {
        this.codeNumber = codeNumber;
        this.codeText = codeText;
    }

    @Override
    public int getCodeNumber() {
        return this.codeNumber;
    }

    @Override
    public String getCodeText() {
        return this.codeText;
    }

    @Override
    public String getCodeNumberStr() {
        return String.valueOf(this.codeNumber);
    }
}
