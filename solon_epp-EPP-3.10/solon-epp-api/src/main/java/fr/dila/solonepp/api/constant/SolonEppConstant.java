package fr.dila.solonepp.api.constant;

import java.util.regex.Pattern;

/**
 * Constantes de l'application SOLON EPP.
 * 
 * @author jtremeaux
 */
public final class SolonEppConstant {

    // *************************************************************
    // Groupes (principal)
    // *************************************************************
    /**
     * Préfixe des groupes associés aux institutions.
     */
    public static final String INSTITUTION_PREFIX = "inst-";

    // *************************************************************
    // Répertoires
    // *************************************************************
    /**
     * Répertoire des profils.
     */
    public static final String ORGANIGRAMME_INSTITUTION_DIR = "institutionDirectory";

    // *************************************************************
    // Pièce jointe
    // *************************************************************
    /**
     * Type de document pièce jointe.
     */
    public static final String PIECE_JOINTE_DOC_TYPE = "PieceJointe";

    /**
     * Variable de contexte permettant de stocker les fichiers liés à la pièce jointe.
     */
    public static final String PIECE_JOINTE_FICHIER_CONTEXT = "PieceJointe.Fichier";

    // *************************************************************
    // Fichier de pièce jointe
    // *************************************************************
    /**
     * Type de document racine des fichiers de pièce jointe.
     */
    public static final String PIECE_JOINTE_FICHIER_ROOT_DOC_TYPE = "PieceJointeFichierRoot";

    /**
     * Type de document fichier de pièce jointe.
     */
    public static final String PIECE_JOINTE_FICHIER_DOC_TYPE = "PieceJointeFichier";

    /**
     * Motif des noms des fichiers de pièce jointe autorisés.
     */
    public static final Pattern PIECE_JOINTE_FICHIER_FILENAME_PATTERN = Pattern
            .compile("^(?!^(PRN|AUX|CLOCK\\$|NUL|CON|COM\\d|LPT\\d|\\..*)(\\..+)?$)[^\\x00-\\x1f\\\\?*:\\\";|/]+$");

    // *************************************************************
    // Dossier
    // *************************************************************
    /**
     * Type de document racine des dossiers.
     */
    public static final String DOSSIER_ROOT_DOC_TYPE = "DossierRoot";

    /**
     * Type de document dossier.
     */
    public static final String DOSSIER_DOC_TYPE = "Dossier";

    // *************************************************************
    // Événement
    // *************************************************************
    /**
     * Type de document événement.
     */
    public static final String EVENEMENT_DOC_TYPE = "Evenement";

    // *************************************************************
    // Message
    // *************************************************************
    /**
     * Type de document message.
     */
    public static final String MESSAGE_DOC_TYPE = "Message";

    // *************************************************************
    // Version
    // *************************************************************
    /**
     * Type de document version.
     */
    public static final String VERSION_DOC_TYPE = "Version";

    // *************************************************************
    // Acteur
    // *************************************************************
    /**
     * Type de document racine acteur.
     */
    public static final String ACTEUR_ROOT_DOC_TYPE = "ActeurRoot";
    /**
     * Type de document acteur.
     */
    public static final String ACTEUR_DOC_TYPE = "Acteur";

    // *************************************************************
    // Circonscription
    // *************************************************************
    /**
     * Type de document racine Circonscription.
     */
    public static final String CIRCONSCRIPTION_ROOT_DOC_TYPE = "CirconscriptionRoot";
    /**
     * Type de document Circonscription.
     */
    public static final String CIRCONSCRIPTION_DOC_TYPE = "Circonscription";

    // *************************************************************
    // Gouvernement
    // *************************************************************
    /**
     * Type de document racine Gouvernement.
     */
    public static final String GOUVERNEMENT_ROOT_DOC_TYPE = "GouvernementRoot";
    /**
     * Type de document Gouvernement.
     */
    public static final String GOUVERNEMENT_DOC_TYPE = "Gouvernement";

    // *************************************************************
    // Identite
    // *************************************************************
    /**
     * Type de document racine Identite.
     */
    public static final String IDENTITE_ROOT_DOC_TYPE = "IdentiteRoot";
    /**
     * Type de document Identite.
     */
    public static final String IDENTITE_DOC_TYPE = "Identite";

    // *************************************************************
    // Mandat
    // *************************************************************
    /**
     * Type de document racine Mandat.
     */
    public static final String MANDAT_ROOT_DOC_TYPE = "MandatRoot";
    /**
     * Type de document Mandat.
     */
    public static final String MANDAT_DOC_TYPE = "Mandat";

    // *************************************************************
    // Acteur
    // *************************************************************
    /**
     * Type de document racine MembreGroupe.
     */
    public static final String MEMBRE_GROUPE_ROOT_DOC_TYPE = "MembreGroupeRoot";
    /**
     * Type de document MembreGroupe.
     */
    public static final String MEMBRE_GROUPE_DOC_TYPE = "MembreGroupe";

    // *************************************************************
    // Acteur
    // *************************************************************
    /**
     * Type de document racine Ministere.
     */
    public static final String MINISTERE_ROOT_DOC_TYPE = "MinistereRoot";

    /**
     * Type de document Ministere.
     */
    public static final String MINISTERE_DOC_TYPE = "Ministere";

    // *************************************************************
    // Organisme
    // *************************************************************
    /**
     * Type de document racine organisme.
     */
    public static final String ORGANISME_ROOT_DOC_TYPE = "OrganismeRoot";
    /**
     * Type de document organisme.
     */
    public static final String ORGANISME_DOC_TYPE = "Organisme";

    // *************************************************************
    // Periode
    // *************************************************************
    /**
     * Type de document racine Periode.
     */
    public static final String PERIODE_ROOT_DOC_TYPE = "PeriodeRoot";
    /**
     * Type de document Periode.
     */
    public static final String PERIODE_DOC_TYPE = "Periode";

    // *************************************************************
    // Types de corbeilles
    // *************************************************************
    /**
     * Noeud de l'organigramme des corbeilles de type section (collection de corbeilles).
     */
    public static final String CORBEILLE_NODE_TYPE_SECTION = "SECTION";

    /**
     * Noeud de l'organigramme des corbeilles de type corbeille.
     */
    public static final String CORBEILLE_NODE_TYPE_CORBEILLE = "CORBEILLE";

    // *************************************************************
    // Actions possibles sur les versions
    // *************************************************************

    /**
     * Action possible sur une version : créer un événement successif.
     */
    public static final String VERSION_ACTION_CREER_EVENEMENT = "CREER_EVENEMENT";

    /**
     * Action possible sur une version : compléter l'événement.
     */
    public static final String VERSION_ACTION_COMPLETER = "COMPLETER";

    /**
     * Action possible sur une version : rectifier l'événement.
     */
    public static final String VERSION_ACTION_RECTIFIER = "RECTIFIER";

    /**
     * Action possible sur une version : annuler l'événement.
     */
    public static final String VERSION_ACTION_ANNULER = "ANNULER";

    /**
     * Action possible sur une version : modifier l'événement.
     */
    public static final String VERSION_ACTION_MODIFIER = "MODIFIER";

    /**
     * Action possible sur une version : publier l'événement.
     */
    public static final String VERSION_ACTION_PUBLIER = "PUBLIER";

    /**
     * Action possible sur une version : supprimer l'événement.
     */
    public static final String VERSION_ACTION_SUPPRIMER = "SUPPRIMER";

    /**
     * Action possible sur une version : visualiser les version.
     */
    public static final String VERSION_ACTION_VISUALISER_VERSION = "VISUALISER_VERSION";

    /**
     * Action possible sur une version : transmettre par mél.
     */
    public static final String VERSION_ACTION_TRANSMETTRE_MEL = "TRANSMETTRE_MEL";

    /**
     * Action possible sur une version : créer une alerte.
     */
    public static final String VERSION_ACTION_CREER_ALERTE = "CREER_ALERTE";

    /**
     * Action possible sur une version : lever une alerte.
     */
    public static final String VERSION_ACTION_LEVER_ALERTE = "LEVER_ALERTE";

    /**
     * Action possible sur une version : abandonner.
     */
    public static final String VERSION_ACTION_ABANDONNER = "ABANDONNER";

    /**
     * Action possible sur une version : accepter.
     */
    public static final String VERSION_ACTION_ACCEPTER = "ACCEPTER";

    /**
     * Action possible sur une version : rejeter.
     */
    public static final String VERSION_ACTION_REJETER = "REJETER";

    /**
     * Action possible sur une version : accuser réception.
     */
    public static final String VERSION_ACTION_ACCUSER_RECEPTION = "ACCUSER_RECEPTION";

    /**
     * Action possible sur une version : passer le message à l'état en cours de traitement.
     */
    public static final String VERSION_ACTION_PASSER_MESSAGE_EN_COURS_DE_TRAITEMENT = "PASSER_MESSAGE_EN_COURS_DE_TRAITEMENT";

    /**
     * Action possible sur une version : passer le message à l'état traité.
     */
    public static final String VERSION_ACTION_PASSER_MESSAGE_TRAITE = "PASSER_MESSAGE_TRAITE";
    
    /**
     * Mailbox
     */
    public static final String MAILBOX_DOC_TYPE = "Mailbox";

    public static final String MAILBOX_SCHEMA = "mailbox";
    
    /**
     * utility class
     */
    private SolonEppConstant(){
    	// do nothing
    }
}
