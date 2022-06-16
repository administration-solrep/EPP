package fr.dila.st.api.constant;

/**
 * Paramètres applicatifs administrables dans l'IHM de l'application.
 *
 * @author fesposito
 * @author jtremeaux
 */
public final class STParametreConstant {
    // Parametres de réponses

    /**
     * Paramètre 1 :Les utilisateurs passés un délai sans connexion sont désactivés de l’application.
     */
    public static final String USER_DECONNEXION_DESACTIVATION_DELAI = "delai-desactivation-utilisateur";

    public static final String USER_DECONNEXION_DESACTIVATION_TITLE =
        "Utilisateur : Délai de suppression utilisateur inactif";

    public static final String USER_DECONNEXION_DESACTIVATION_DESCRIPTION =
        "Les utilisateurs passés un délai sans connexion sont supprimés de l’application.";

    public static final String USER_DECONNEXION_DESACTIVATION_UNIT = "Mois";

    public static final String USER_DECONNEXION_DESACTIVATION_VALUE = "24";

    /**
     * Paramètre 2 : délai de non-connexion à partir duquel un mail est envoyé pour information à l'administrateur
     */
    public static final String USER_DECONNEXION_INFORMATION_DELAI = "delai-avertissement-utilisateur-inactif";

    public static final String USER_DECONNEXION_INFORMATION_TITLE =
        "Utilisateur : Délai avertissement utilisateur inactif";

    public static final String USER_DECONNEXION_INFORMATION_DESCRIPTION =
        "En cas de non connexion d’un utilisateur durant plus d’un délai signalée par l’application qui vérifie le compte de l’utilisateur pour une éventuelle mise à jour en termes de gestion des droits d’accès et de l’organigramme. La liste des utilisateurs concernés est adressée à l’administrateur.";

    public static final String USER_DECONNEXION_INFORMATION_UNIT = "Mois";

    public static final String USER_DECONNEXION_INFORMATION_VALUE = "12";

    public static final String NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_OBJET =
        "objet-mail-notification-delai-avertissement-utilisateur-inactif";

    public static final String NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_OBJET_TITLE =
        "Utilisateur : Objet mél Délai avertissement utilisateur inactif";

    public static final String NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_OBJET_DESCRIPTION =
        "« Objet » (par défaut) de la notification par mél adressée à l'administrateur lorsqu'au moins un utilisateur dépasse le délai de non connexion défini dans les paramètres.";

    public static final String NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_OBJET_UNIT = "objet";

    public static final String NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_OBJET_VALUE =
        "Utilisateurs inactifs";

    public static final String NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_TEXT =
        "text-mail-notification-delai-avertissement-utilisateur-inactif";

    public static final String NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_TEXT_TITLE =
        "Utilisateur : Texte du mél Délai avertissement utilisateur inactif";

    public static final String NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_TEXT_DESCRIPTION =
        "« Texte du message » (par défaut) de la notification par mél adressée à l'administrateur lorsqu'au moins un utilisateur dépasse le délai de non connexion défini dans les paramètres.";

    public static final String NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_TEXT_UNIT = "texte";

    public static final String NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_TEXT_VALUE =
        "Les utilisateurs suivants ne sont pas connectés depuis le délai d'avertissement des utilisateurs inactifs défini dans les paramètres.";

    /**
     * Paramètre 3 : Délai de traitement d’une question
     */
    public static final String QUESTION_DUREE_TRAITEMENT = "delai-traitement-question";

    /**
     * Paramètre 4 : Délai de conservation des éléments présents dans les favoris.
     */
    public static final String CONSERVATION_FAVORIS = "delai-conservation-favoris";

    /**
     * Paramètre 5 : Délai de conservation des données dans l’application
     */
    public static final String DELAI_CONSERVATION_DONNEES = "delai-conservation-donnees";

    /**
     * Paramètre 6 : délai maximal de publication mis en œuvre dans les activités de la feuille de route dédiées au SGG
     */
    public static final String DELAI_MAX_PUBLICATION_FEUILLEROUTE = "delai-max-publication-ancien-timbre";

    /**
     * Paramètre 7 : Adresse URL de l’application transmise systématiquement aux utilisateurs lors des notifications par
     * mèl.
     */
    public static final String ADRESSE_URL_APPLICATION = "url-application-transmise-par-mel";
    public static final String ADRESSE_URL_APPLICATION_TITRE = "Divers : Url application transmise par courriel";
    public static final String ADRESSE_URL_APPLICATION_DESC =
        "Adresse URL de l’application transmise systématiquement aux utilisateurs lors des notifications par mél.";
    public static final String ADRESSE_URL_APPLICATION_UNIT = "url";

    /**
     * Paramètre : Adresse URL du dictaticiel
     */
    public static final String ADRESSE_URL_DIDACTICIEL = "url-didacticiel";
    public static final String ADRESSE_URL_DIDACTICIEL_TITRE = "Divers : Url didacticiel";
    public static final String ADRESSE_URL_DIDACTICIEL_DESC =
        "Adresse URL du document didacticiel utilisé pour le lien didacticiel";
    public static final String ADRESSE_URL_DIDACTICIEL_UNIT = "url";

    /**
     * Paramètre 8 : « Objet » et « Texte du message » (par défaut) de la notification par mèl suite à la création d’un
     * utilisateur.
     */
    public static final String NOTIFICATION_MAIL_CREATION_UTILISATEUR_OBJET =
        "objet-mel-notification-creation-utilisateur";

    public static final String NOTIFICATION_MAIL_CREATION_UTILISATEUR_TEXT =
        "texte-mel-notification-creation-utilisateur";

    /**
     * Paramètre 9 : « Objet » et « Texte du message » (par défaut) de la notification par mèl adressé aux membres du
     * poste à chaque création d’une tâche.
     */
    public static final String OBJET_MAIL_NOTIFICATION_CREATION_TACHE = "objet-mel-notification-creation-tache";

    /**
     * texte-mel-notification-creation-tache
     */
    public static final String TEXTE_MAIL_NOTIFICATION_CREATION_TACHE = "texte-mel-notification-creation-tache";

    /**
     * Paramètre 11 : « Objet » (par défaut) de la notification par mèl adressé aux membres du poste à expiration d’une
     * tâche (dépassement de délai dans le traitement d’une tâche).
     */
    public static final String MAIL_EXPIRATION_NO_VAL_AUTOMATIQUE_OBJET = "objet-mel-notification-expiration-tache";

    /**
     * Paramètre 11 : « Texte du message » (par défaut) de la notification par mèl adressé aux membres du poste à
     * expiration d’une tâche (dépassement de délai dans le traitement d’une tâche).
     */
    public static final String MAIL_EXPIRATION_NO_VAL_AUTOMATIQUE_TEXT = "texte-mel-notification-expiration-tache";

    /**
     * Paramètre 12 : « Objet » et « Texte du message » (par défaut) de la notification par mèl adressé aux acteurs des
     * tâches « en cours » sur les feuilles de route, suite à un message XML de modification de la question (flux XML
     * 1’’ et 4).
     */
    public static final String NOTIFICATION_MAIL_AFFECTATION_TACHE_FLUX_XML_VALIDATION_QUESTION_OBJET =
        "objet-mel-notification-affectation-tache-flux-xml";

    public static final String NOTIFICATION_MAIL_AFFECTATION_TACHE_FLUX_XML_VALIDATION_QUESTION_TEXT =
        "texte-mel-notification-affectation-tache-flux-xml";

    /**
     * Paramètre 13 : « Objet » et « Texte du message » (par défaut) contenu dans le message lors de l’envoi par mèl
     * d’un à plusieurs dossiers
     */
    public static final String DOSSIER_MAIL_OBJET = "objet-mel-dossier";

    public static final String DOSSIER_MAIL_TEXT = "texte-mel-dossier";

    /**
     * Paramètre 14 : « Objet » et « Texte du message » (par défaut) adressé aux acteurs des tâches précédemment « en
     * cours » pour les informer qu’ils sont dessaisis d’une question suite à réattribution.
     */
    public static final String NOTIFICATION_MAIL_REATTRIBUTION_DESAFFECTATION_TACHE_OBJET =
        "objet-mel-notification-desaffectation-suite-reattribution";

    public static final String NOTIFICATION_MAIL_REATTRIBUTION_DESAFFECTATION_TACHE_TEXT =
        "texte-mel-notification-desaffectation-suite-reattribution";

    /**
     * Paramètre 15 : « Objet » et « Texte du message » (par défaut) adressé aux acteurs des tâches nouvellement « en
     * cours » pour les informer qu’ils sont saisis d’une question suite à réattribution.
     */
    public static final String NOTIFICATION_MAIL_REATTRIBUTION_AFFECTATION_TACHE_OBJET =
        "objet-mel-notification-affectation-suite-reattribution";

    public static final String NOTIFICATION_MAIL_REATTRIBUTION_AFFECTATION_TACHE_TEXT =
        "texte-mel-notification-affectation-suite-reattribution";

    /**
     * Paramètre 16 : « Objet » et « Texte du message » (par défaut) adressé aux acteurs des services du Premier
     * ministre qui sont associés à des tâches « en cours » pour les informer d’un délai maximal de publication.
     */
    public static final String NOTIFICATION_MAIL_SGG_DELAI_MAX_PUBLICATION_OBJET =
        "objet-mel-notification-sgg-info-delai-max-atteint";

    public static final String NOTIFICATION_MAIL_SGG_DELAI_MAX_PUBLICATION_TEXT =
        "texte-mel-notification-sgg-info-delai-max-atteint";

    /**
     * Paramètre 17 : Délai de réponse à une question signalée (en jours).
     */
    public static final String DELAI_QUESTION_SIGNALEE = "delai-reponse-question-signalee";

    /**
     * Paramètre 18 : Délai au-delà duquel un verrou est supprimé
     */
    public static final String DELAI_VERROU_SUPPRIME = "delai-verrou-supprime";

    /**
     * Paramètre 19 : Date de départ pour la pris en compte de l’historique des données dans le calcul des statistiques.
     */
    public static final String STATISTIQUE_DATE_DEPART = "delai-verrou-supprimé";

    /**
     * Paramètre 20 : Poids limite de la somme des pièces jointes associées à un mèl
     */
    public static final String PIECE_JOINTE_TAILLE_MAX_MAIL = "poid-limite-piece-jointe-mail";

    /**
     * Paramètre 21 : Adresse mèl de l’administrateur technique de l’application SGG
     */
    public static final String MAIL_ADMIN_TECHNIQUE = "adresse-mail-administrateur-application";

    /**
     * Paramètre 22 : « Objet » et « Texte du message » (par défaut) adressé aux acteurs des tâches précédemment « en
     * cours » pour les informer qu’ils sont dessaisis d’une question suite à une caducité.
     */
    public static final String NOTIFICATION_MAIL_CADUCITE_DESAFFECTATION_TACHE_OBJET =
        "objet-mel-notification-dessais-question-suite-caducité";

    public static final String NOTIFICATION_MAIL_CADUCITE_AFFECTATION_TACHE_TEXT =
        "texte-mel-notification-dessais-question-suite-caducité";

    /**
     * Paramètre 23 : « Objet » et « Texte du message » (par défaut) adressé aux acteurs des tâches précédemment « en
     * cours » pour les informer qu’ils sont dessaisis d’une question suite à un retrait.
     */
    public static final String NOTIFICATION_MAIL_RETRAIT_DESAFFECTATION_TACHE_OBJET =
        "objet-mel-notification-dessais-question-suite-retrait";

    public static final String NOTIFICATION_MAIL_RETRAIT_AFFECTATION_TACHE_TEXT =
        "texte-mel-notification-dessais-question-suite-retrait";

    /**
     * Paramètre 24 : Libellé de l’encart statistique apparaissant sur la page d’accueil
     */
    public static final String STATISTIQUE_ENCART_LIBELLE = "encart-stat-page-accueil";

    /**
     * Paramètre 25 : Délai de conservation des informations du Journal (journalisation des actions réalisées par les
     * utilisateurs) en mois.
     */
    public static final String CONSERVATION_DONNEE_JOURNAL = "delai-conservation-info-journal";

    /**
     * Paramètre 26 : « Objet » et « Texte du message » (par défaut) du message adressé à l'administrateur fonctionnel
     * pour validation des modèles de feuille de route.
     */
    public static final String NOTIFICATION_MAIL_VALIDATION_FEUILLE_ROUTE_OBJET =
        "objet-mel-notification-validation-feuille-route";

    public static final String NOTIFICATION_MAIL_VALIDATION_FEUILLE_ROUTE_TEXT =
        "texte-mel-notification-validation-feuille-route";

    /**
     * Paramètre 27 : « Objet » et « Texte du message » (par défaut) du message adressé à qui de droit, lorsqu'un errata
     * de question est recu
     */

    public static final String NOTIFICATION_MAIL_ERRATUM_QUESTION_OBJET = "objet-mel-notification-erratum-question";

    public static final String NOTIFICATION_MAIL_ERRATUM_QUESTION_TEXTE = "texte-mel-notification-erratum-question";

    /**
     * « Objet » et « Texte du message » (par défaut) de la notification par mèl adressé aux destinataires d'une alerte.
     */
    public static final String NOTIFICATION_MAIL_ALERT_OBJET = "objet-mel-notification-alert";

    public static final String NOTIFICATION_MAIL_ALERT_TEXT = "texte-mel-notification-text";

    public static final String AMPLIATION_MAIL_OBJET = "objet-mail-ampliation";

    public static final String AMPLIATION_MAIL_TEXT = "text-mail-ampliation";

    /**
     * Paramètre 28 : Délai de conservation des dossier avant l'annoncer comme candidat d'elimination
     */
    public static final String DELAI_ELIMINATION_DOSSIERS = "delai-elimination-dossiers";

    /**
     * Paramètre 29 : Délai de conservation des dossier avant l'annoncer comme candidat d'abandon
     */
    public static final String DELAI_CANDIDATURE_ABANDON_DOSSIERS = "delai-candidature-abandon-dossiers";

    /**
     * Paramètre 30 : Délai pour passer les dossiers candidats a l'abandon a l'etat abandon
     */
    public static final String DELAI_ABANDON_DOSSIERS = "delai-abandon-dossiers";

    /**
     * Paramètre 31 : Délai pour verser un dossier publie dans la base d'archivage intermediaire
     */
    public static final String DELAI_VERSEMENT_BASE_INTERMEDIAIRE = "delai-versement-dossiers-base-intermediaire";

    /**
     * Paramètre 30.1 Réponses : Objet mail d'alerte lors du contrôle de publication
     */
    public static final String OBJET_ALERTE_CONTROLE_PUBLICATION = "objet-mel-alerte-controle-publi";

    /**
     * Paramètre 30.2 Réponses : Texte mail d'alerte lors du contrôle de publication
     */
    public static final String TEXTE_ALERTE_CONTROLE_PUBLICATION = "texte-mel-alerte-controle-publi";

    public static final String DUREE_UTILITE_ADMINISTRATIVE = "DUA";

    /**
     * Paramètre objet du mail de notification des créations / modifications des délégations.
     */
    public static final String NOTIFICATION_MAIL_DELEGATION_OBJET = "objet-mel-notification-delegation";

    /**
     * Paramètre texte du mail de notification des créations / modifications des délégations.
     */
    public static final String NOTIFICATION_MAIL_DELEGATION_TEXTE = "texte-mel-notification-delegation";
    /**
     * Paramètre objet du mail de notification des créations / modifications des délégations.
     */
    public static final String NOTIFICATION_MAIL_REDEMARRAGE_FDR_OBJET = "objet-mel-notification-redemarrageFdr";

    /**
     * Paramètre texte du mail de notification des créations / modifications des délégations.
     */
    public static final String NOTIFICATION_MAIL_REDEMARRAGE_FDR_TEXTE = "texte-mel-notification-redemarrageFdr";

    /**
     * Paramètre objet du mail d'elimination de dossiers
     */
    public static final String OBJET_MAIL_DOSSIER_ELIMINATION = "objet-mail-dossier-elimination";
    /**
     * Paramètre text du mail d'elimination de dossiers
     */
    public static final String TEXT_MAIL_DOSSIER_ELIMINATION = "text-mail-dossier-elimination";

    public static final String OBJET_MAIL_DOSSIER_ELIMINATION_ADMIN_MIN =
        "objet-mail-dossier-elimination-admin-ministeriel";

    public static final String OBJET_MAIL_DOSSIER_ELIMINATION_ADMIN_MIN_TITRE =
        "Suppression dossiers : Objet du courriel listant les dossiers sélectionnés pour être éliminés par un administrateur ministeriel";

    public static final String OBJET_MAIL_DOSSIER_ELIMINATION_ADMIN_MIN_DESCRIPTION =
        "« Objet » (par défaut) de la notification adressée par mèl à l'administrateur minitériel lorsqu'au moins un dossier est sélectionné pour être éliminé.";

    public static final String OBJET_MAIL_DOSSIER_ELIMINATION_ADMIN_MIN_UNIT = "objet";

    public static final String OBJET_MAIL_DOSSIER_ELIMINATION_ADMIN_MIN_VALUE =
        "[S.O.L.O.N. II] Dossiers candidats à l'élimination";

    public static final String TEXT_MAIL_DOSSIER_ELIMINATION_ADMIN_MIN =
        "text-mail-dossier-elimination-admin-ministeriel";

    public static final String TEXT_MAIL_DOSSIER_ELIMINATION_ADMIN_MIN_TITRE =
        "Suppression dossiers : Texte du courriel listant les dossiers sélectionnés pour être éliminés par un administrateur ministeriel";

    public static final String TEXT_MAIL_DOSSIER_ELIMINATION_ADMIN_MIN_DESCRIPTION =
        "« Texte du message » (par défaut) de la notification adressée par mèl à l'administrateur minitériel lorsqu'au moins un dossier est sélectionné pour être éliminé.";

    public static final String TEXT_MAIL_DOSSIER_ELIMINATION_ADMIN_MIN_UNIT = "texte";

    public static final String TEXT_MAIL_DOSSIER_ELIMINATION_ADMIN_MIN_VALUE =
        "Le tableau suivant liste les dossiers validés par un administrateur fonctionnel qui répondent aux critères d'élimination. Veuillez vous connecter à l'application S.O.L.O.N. II pour valider les dossiers à supprimer.";

    /**
     * Paramètre objet du mail de codidature d'abandonne de dossiers
     */
    public static final String OBJET_MAIL_DOSSIER_CANDIDAT_ABANDONNE = "objet-mail-dossier-candidat-abandonne";
    /**
     * Paramètre text du mail de codidature d'abandonne de dossiers
     */
    public static final String TEXT_MAIL_DOSSIER_CANDIDAT_ABANDONNE = "text-mail-dossier-candidat-abandonne";
    /**
     * Paramètre objet du mail d'apres validation de transmission de dossiers
     */
    public static final String OBJET_MAIL_DOSSIER_AFTER_VALIDATION_TRANSMISSION =
        "objet-mail-dossier-after-validation-transmission";
    /**
     * Paramètre text du mail d'apres validation de transmission de dossiers
     */
    public static final String TEXT_MAIL_DOSSIER_AFTER_VALIDATION_TRANSMISSION =
        "text-mail-dossier-after-validation-transmission";
    /**
     * Paramètre objet du mail de candidature d'archivage intermediaire
     */
    public static final String OBJET_MAIL_DOSSIER_CANDIDAT_ARCHIVAGE_INTERMEDIAIRE =
        "objet-mail-dossier-candidat-archivage-intermediaire";
    /**
     * Paramètre text du mail de candidature d'archivage intermediaire
     */
    public static final String TEXT_MAIL_DOSSIER_CANDIDAT_ARCHIVAGE_INTERMEDIAIRE =
        "text-mail-dossier-candidat-archivage-intermediaire";
    /**
     * Paramètre objet du mail de candidature d'archivage definitive
     */
    public static final String OBJET_MAIL_DOSSIER_CANDIDAT_ARCHIVAGE_DEFINITIVE =
        "objet-mail-dossier-candidat-archivage-definitive";
    /**
     * Paramètre text du mail de candidature d'archivage definitive
     */
    public static final String TEXT_MAIL_DOSSIER_CANDIDAT_ARCHIVAGE_DEFINITIVE =
        "text-mail-dossier-candidat-archivage-definitive";

    /**
     * Paramètre objet du mail de notification d'erreur de création de jeton
     */
    public static final String OBJET_MAIL_ERREUR_CREATION_JETON = "objet-mel-notification-erreur-jeton";
    /**
     * Paramètre texte du mail de notification d'erreur de création de jeton
     */
    public static final String TEXTE_MAIL_ERREUR_CREATION_JETON = "texte-mel-notification-erreur-jeton";

    /**
     * Paramètre objet mail des résultats d'alerte
     */
    public static final String OBJET_MAIL_NOTIFICATION_ALERTE = "objet-mel-notification-alerte";

    /**
     * Paramètre texte mail des résultats d'alerte
     */
    public static final String TEXTE_MAIL_NOTIFICATION_ALERTE = "texte-mel-notification-alerte";

    /**
     * Paramètre objet mail des alertes dossiers bloqués
     */
    public static final String OBJET_MAIL_ALERTE_DOSSIERS_BLOQUES = "objet-mel-alerte-dossiers-bloques";

    /**
     * Paramètre texte mail des alertes dossiers bloqués
     */
    public static final String TEXTE_MAIL_ALERTE_DOSSIERS_BLOQUES = "texte-mel-alerte-dossiers-bloques";

    /**
     * Paramètre objet du mail de l'export d'archivage definitive
     */
    public static final String OBJET_MAIL_DOSSIERS_EXPORT_ARCHIVAGE_DEFINITIVE =
        "objet-mail-dossiers-export-archivage-definitive";
    /**
     * Paramètre text du mail de l'export d'archivage definitive
     */
    public static final String TEXT_MAIL_DOSSIERS_EXPORT_ARCHIVAGE_DEFINITIVE =
        "text-mail-dossiers-export-archivage-definitive";

    public static final String BATCH_MENSUEL_DOSSIER_CANDIDAT_ARCHIVAGE_DEFINITIVE =
        "batch-mensuel-dossierCandidatToArchivageDefinitive";

    public static final String BATCH_MENSUEL_DOSSIER_CANDIDAT_ARCHIVAGE_INTERMEDIAIRE =
        "batch-mensuel-dossierCandidatToArchivageIntermediaire";

    public static final String EXTRACTION_REPERTOIRE = "extraction-repertoire";

    /**
     * Paramètre texte du mail de dossiers link incohérents (batch)
     */
    public static final String TEXTE_MAIL_DOSSIERS_LINK_INCOHERENT = "texte-mel-dossiers-link-incoherent";

    /**
     * Paramètre objet du mail de dossiers link incohérents (batch)
     */
    public static final String OBJET_MAIL_DOSSIERS_LINK_INCOHERENT = "objet-mel-dossiers-link-incoherent";

    /**
     * Paramètre : délai de renouvellement des mots de passe
     */
    public static final String DELAI_RENOUVELLEMENT_MOTS_DE_PASSE = "delai-renouvellement-mots-de-passe";

    /**
     * Paramètre : délai de prévenance de renouvellement du mot de passe
     */
    public static final String DELAI_PREVENANCE_RENOUVELLEMENT_MOT_DE_PASSE =
        "delai-prevenance-renouvellement-mot-de-passe";

    /**
     * Paramètre objet du mail d'arrivée à expiration du mot de passe
     */
    public static final String OBJET_MAIL_PREVENANCE_RENOUVELLEMENT_MOT_DE_PASSE =
        "objet-mel-prevenance-renouvellement-mot-de-passe";

    /**
     * Paramètre texte du mail d'arrivée à expiration du mot de passe
     */
    public static final String TEXTE_MAIL_PREVENANCE_RENOUVELLEMENT_MOT_DE_PASSE =
        "texte-mel-prevenance-renouvellement-mot-de-passe";

    /**
     * Paramètre objet du mail d'erreur lors de l'exécution d'un batch
     */
    public static final String OBJET_MAIL_SUIVI_BATCH_NOTIFICATION = "objet-mel-suivi-batch-notification";

    /**
     * Paramètre texte du mail d'erreur lors de l'exécution d'un batch
     */
    public static final String TEXTE_MAIL_SUIVI_BATCH_NOTIFICATION = "texte-mel-suivi-batch-notification";

    public static final String PARAMETRE_TAILLE_PIECES_JOINTES = "parametre-taille-pieces-jointes";

    /**
     * Paramètre : « Objet » et « Texte du message » (par défaut) de la notification par mèl pour les dossiers en
     * attente d'archivage.
     */
    public static final String NOTIFICATION_MAIL_DOSSIER_ATTENTE_ELIMINATION_OBJET =
        "objet-mel-notification-attente-elimination";

    public static final String NOTIFICATION_MAIL_DOSSIER_ATTENTE_ELIMINATION_TEXT =
        "texte-mel-notification-attente-elimination";

    /**
     * Paramètre demande d'un nouveau mot de passe
     */
    public static final String OBJET_MAIL_NOUVEAU_MDP = "objet-mel-nouveau-mdp";

    public static final String TEXTE_MAIL_NOUVEAU_MDP = "texte-mel-nouveau-mdp";

    /**
     * Lien vers la page de renseignement
     */
    public static final String PAGE_RENSEIGNEMENTS_ID = "page-renseignements";
    public static final String PAGE_RENSEIGNEMENTS_VALUE = "http://extraqual.pm.rie.gouv.fr/demat/index.html";

    /*
     * Paramètre contenant la liste des émetteurs possibles de mails (si non présente, on utilise le paramètre mail.from).
     */
    public static final String ADRESSE_MEL_EMISSION_PARAMETER_NAME = "adresse-emission-mel";
    public static final String ADRESSE_MEL_EMISSION_PARAMETER_NAME_TITRE = "Divers : Adresse d'émission des mèls";
    public static final String ADRESSE_MEL_EMISSION_PARAMETER_NAME_DESCRIPTION =
        "Adresses d'émission des mèls, séparées par des point-virgules";
    public static final String ADRESSE_MEL_EMISSION_PARAMETER_NAME_UNIT = "adresse mèl";
    public static final String ADRESSE_MEL_EMISSION_PARAMETER_NAME_VALUE = "ne-pas-repondre@dila.gouv.fr";

    /*
     * Paramètre retour VITAM : Répertoire to do
     */
    public static final String RETOUR_VITAM_TODO_REP_PARAMETER_NAME = "vitam-todo-repertoire";
    public static final String RETOUR_VITAM_TODO_REP_PARAMETER_NAME_TITRE =
        "Retour VITAM - Repertoire racine de dépot des retours vitam";
    public static final String RETOUR_VITAM_TODO_REP_PARAMETER_NAME_DESCRIPTION =
        "Repertoire de dépot des retours vitam";
    public static final String RETOUR_VITAM_TODO_REP_PARAMETER_NAME_UNIT = "texte";
    public static final String RETOUR_VITAM_TODO_REP_PARAMETER_NAME_VALUE = "/EPG/ARCHIVAGE/VITAM/todo";

    /*
     * Paramètre retour VITAM : Répertoire done
     */
    public static final String RETOUR_VITAM_DONE_REP_PARAMETER_NAME = "vitam-done-repertoire";
    public static final String RETOUR_VITAM_DONE_REP_PARAMETER_NAME_TITRE =
        "Retour VITAM - Repertoire racine des retours vitam traités avec succès";
    public static final String RETOUR_VITAM_DONE_REP_PARAMETER_NAME_DESCRIPTION =
        "Repertoire racine des retours vitam traités avec succès";
    public static final String RETOUR_VITAM_DONE_REP_PARAMETER_NAME_UNIT = "texte";
    public static final String RETOUR_VITAM_DONE_REP_PARAMETER_NAME_VALUE = "/EPG/ARCHIVAGE/VITAM/done";

    /*
     * Paramètre retour VITAM : Répertoire error
     */
    public static final String RETOUR_VITAM_ERROR_REP_PARAMETER_NAME = "vitam-error-repertoire";
    public static final String RETOUR_VITAM_ERROR_REP_PARAMETER_NAME_TITRE =
        "Retour VITAM - Repertoire racine des retours vitam en erreur de traitement";
    public static final String RETOUR_VITAM_ERROR_REP_PARAMETER_NAME_DESCRIPTION =
        "Repertoire racine des retours vitam en erreur de traitement";
    public static final String RETOUR_VITAM_ERROR_REP_PARAMETER_NAME_UNIT = "texte";
    public static final String RETOUR_VITAM_ERROR_REP_PARAMETER_NAME_VALUE = "/EPG/ARCHIVAGE/VITAM/error";

    public static final String MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_NAME = "message-accessibilite-login";
    public static final String MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_TITRE = "Divers : Taux accessibilité (RGAA)";
    public static final String MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_DESCRIPTION =
        "Information sur le taux d'accessibilité de l'application indiquée dans la page de connexion.";
    public static final String MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_UNIT = "texte";
    public static final String MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_VALUE =
        "Cette application est partiellement accessible au sens du RGAA en vigueur (Taux d'accessibilité supérieur à 68%).";

    public static final String ARCHIVAGE_TYPE = "archivage";

    public static final String PAGE_INTERNET_DILA_PARAMETER_NAME = "page-internet-dila";
    public static final String PAGE_INTERNET_DILA_PARAMETER_DESCRIPTION =
        "Page internet permettant d'accéder au programme d'amélioration de l'accessibilité de la DILA.";
    public static final String PAGE_INTERNET_DILA_PARAMETER_TITRE = "Divers : URL accessibilité des sites de la DILA";
    public static final String PAGE_INTERNET_DILA_PARAMETER_VALUE =
        "https://www.dila.premier-ministre.gouv.fr/informations-sur-le-site/accessibilite-des-sites-de-la-dila";
    public static final String PAGE_INTERNET_DILA_PARAMETER_UNIT = "URL";

    public static final String OBJET_MAIL_EXPORT_USERS_NAME = "object-mel-export-users-result";
    public static final String OBJET_MAIL_EXPORT_USERS_NAME_TITRE =
        "Export : Objet du courriel de résultats de recherche utilisateurs";
    public static final String OBJET_MAIL_EXPORT_USERS_NAME_DESCRIPTION =
        "« Objet » du courriel de résultats de recherche utilisateurs";
    public static final String OBJET_MAIL_EXPORT_USERS_NAME_UNIT = "objet";
    public static final String OBJET_MAIL_EXPORT_USERS_NAME_VALUE = "Votre demande d'export d'utilisateurs";

    public static final String CORPS_SUCCESS_MAIL_EXPORT_USERS_NAME = "corps-success-mel-export-users";
    public static final String CORPS_SUCCESS_MAIL_EXPORT_USERS_NAME_TITRE =
        "Export : Texte de succès du courriel de résultats de recherche utilisateurs";
    public static final String CORPS_SUCCESS_MAIL_EXPORT_NAME_DESCRIPTION =
        "« Texte » de succès du courriel de résultats de recherche utilisateurs";
    public static final String CORPS_SUCCESS_MAIL_EXPORT_USERS_NAME_UNIT = "text";
    public static final String CORPS_SUCCESS_MAIL_EXPORT_USERS_NAME_VALUE =
        "Bonjour, veuillez trouver en pièce jointe l'export de la liste des résultats pour la recherche utilisateurs demandée le ${date_demande}.";

    /**
     * utility class
     */
    private STParametreConstant() {
        // do nothing
    }
}
