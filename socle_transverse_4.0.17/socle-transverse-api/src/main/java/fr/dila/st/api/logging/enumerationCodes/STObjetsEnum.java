package fr.dila.st.api.logging.enumerationCodes;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Énumération de l'objet des actions <br>
 * Décompte sur 3 chiffres, le premier (0) indique qu'il s'agit d'un objet du ST <br>
 * 000 : défaut <br>
 * 001 : Dossier <br>
 * 002 : Case-Link <br>
 * 003 : Jeton <br>
 * 004 : Alerte <br>
 * 005 : Favoris (FAV) <br>
 * 006 : UserWorkspace <br>
 * 007 : Dossier link <br>
 * 008 : Comment <br>
 * 009 : Document <br>
 * 010 : Principal <br>
 * 011 : Requête experte <br>
 * 012 : Mandat <br>
 * 013 : Communication <br>
 * 014 : EntiteNode <br>
 * 015 : JetonService <br>
 * 016 : Poste <br>
 * 017 : Mail <br>
 * 018 : Unité structurelle <br>
 * 019 : Schéma <br>
 * 020 : Information <br>
 * 021 : Recherche <br>
 * 022 : Paramètre <br>
 * 023 : User <br>
 * 024 : Session <br>
 * 025 : NavigationContext <br>
 * 026 : UFNXQL <br>
 * 027 : Fichier excel <br>
 * 028 : Paramètre méthode <br>
 * 029 : SQL <br>
 * 030 : Corbeille <br>
 * 031 : Query Assembler <br>
 * 032 : Conversion <br>
 * 033 : LDAP <br>
 * 034 : Property <br>
 * 035 : URL <br>
 * 036 : MailBox <br>
 * 037 : Organigramme <br>
 * 038 : Pièce jointe <br>
 * 039 : Table de référence <br>
 * 040 : Event type <br>
 * 041 : MetaDonnée <br>
 * 042 : Gestionnaire de l'utilisateur <br>
 * 043 : File (fichier arborescence) <br>
 * 044 : Bean <br>
 * 045 : Paramètre d'événement <br>
 * 046 : profil utilisateur 047 : Folder (répertoire arborescence) <br>
 * 048 : File upload <br>
 * 049 : Stream <br>
 * 050 : Ministère <br>
 * 051 : Opération UpdateList <br>
 * 052 : Injection de Gouvernement <br>
 * 053 : Gouvernement <br>
 * 054 : Action de masse <br>
 * 055 : Transaction <br>
 * 056 : Doctype <br>
 * 057 : Hostname <br>
 * 058 : Password <br>
 * 059 : Identité <br>
 * 060 : Acteur <br>
 * 061 : Convertisseur <br>
 * 062 : Icon <br>
 * 063 : HTML <br>
 * 064 : ACLS <br>
 * 065 : PDF <br>
 *
 * 088 : BatchResult <br>
 * 089 : Lanceur batch général <br>
 * 090 : Batch d'envoi de mèl de prévenance de renouvellement de mot de passe <br>
 * 091 : Batch de suppression des documents <br>
 * 092 : Batch de notifications aux WS <br>
 * 093 : Solver <br>
 * 094 : Batch d'envoi des alertes <br>
 * 095 : Batch de déverrouillage des documents <br>
 * 096 : Batch de suppression des dossierslink <br>
 * 097 : Event <br>
 * 098 : Logger <br>
 * 099 : Batch <br>
 */
public enum STObjetsEnum implements STCodes {
    /**
     * 000 défaut
     */
    DEFAULT(0, "Objet défaut"),
    /**
     * 001 : Dossier
     */
    DOSSIER(1, "Dossier"),
    /**
     * 002 : Case-Link
     */
    CASE_LINK(2, "Case-Link"),
    /**
     * 003 : Jeton
     */
    JETON(3, "Jeton"),
    /**
     * 004 : Alerte
     */
    ALERT(4, "Alerte"),
    /**
     * 005 : Favoris
     */
    FAV(5, "Favoris"),
    /**
     * 006 : Userworkspace
     */
    USER_WORKSPACE(6, "UserWorkspace"),
    /**
     * 007 : Dossier link
     */
    DOSSIER_LINK(7, "DossierLink"),
    /**
     * 008 : Comment
     */
    COMMENT(8, "Comment"),
    /**
     * 009 : document
     */
    DOCUMENT(9, "Document"),
    /**
     * 010 : principal
     */
    PRINCIPAL(10, "Principal"),
    /**
     * 011 : requete experte
     */
    REQ_EXPERTE(11, "Requête experte"),
    /**
     * 012 : Mandat
     */
    MANDAT(12, "Mandat"),
    /**
     * 013 : communication
     */
    COMMUNICATION(13, "Communication"),
    /**
     * 014 : EntiteNode
     */
    ENTITE_NODE(14, "EntiteNode"),
    /**
     * 015 : JetonService
     */
    JETON_SERVICE(15, "Service de jeton"),
    /**
     * 016 : poste
     */
    POSTE(16, "Poste"),
    /**
     * 017 : mail
     */
    MAIL(17, "Mail"),
    /**
     * 018 : unité structurelle
     */
    UNITE_STRUCTURELLE(18, "Unité structurelle"),
    /**
     * 019 : schema
     */
    SCHEMA(19, "Schema"),
    /**
     * 020 : information
     */
    INFORMATION(20, "Information"),
    /**
     * 021 : recherche
     */
    RECHERCHE(21, "Recherche"),
    /**
     * 022 : paramètre (type nuxeo)
     */
    PARAM(22, "Paramètre"),
    /**
     * 023 : User
     */
    USER(23, "Utilisateur"),
    /**
     * 024 : session
     */
    SESSION(24, "Session"),
    /**
     * 025 : navigationContext
     */
    NAV_CONTEXT(25, "NavigationContext"),
    /**
     * 026 : UFNXQL
     */
    UFNXQL(26, "UFNXQL"),
    /**
     * 027 : Excel
     */
    EXCEL(27, "fichier Excel"),
    /**
     * 028 : paramètre méthode
     */
    PARAM_METHODE(28, "Paramètre méthode"),
    /**
     * 029 : SQL
     */
    SQL(29, "SQL"),
    /**
     * 030 : Corbeille
     */
    CORBEILLE(30, "Corbeille"),
    /**
     * 031 : CORBEILLE
     */
    QUERY_ASSEMBLER(31, "Query Assembler"),
    /**
     * 032 : Conversion
     */
    CONVERSION(32, "Conversion"),
    /**
     * 033 LDAP
     */
    LDAP(33, "LDAP"),
    /**
     * 034 Property
     */
    PROPERTY(34, "Property"),
    /**
     * 035 Url
     */
    URL(35, "Url"),
    /**
     * 036 MailBox
     */
    MAIL_BOX(36, "MailBox"),
    /**
     * 037 Organigramme
     */
    ORGANIGRAMME(37, "Organigramme"),
    /**
     * 038 : Pièce jointe
     */
    PIECE_JOINTE(38, "Pièce jointe"),
    /**
     * 039 : Table de référence
     */
    TABLE_REFERENCE(39, "Table de référence"),
    /**
     * 040 : Event type
     */
    EVENT_TYPE(40, "Type d'évènement"),
    /**
     * 041 Métadonnée
     */
    META_DONNEE(41, "Métadonnée"),
    /**
     * 042 Gestionnaire de l'utilisateur
     */
    USER_MANAGER(42, "Gestionnaire de l'utilisateur"),
    /**
     * 043 : Fichier
     */
    FILE(43, "Fichier"),
    /**
     * 044 : Bean
     */
    BEAN(44, "Bean"),
    /**
     * 045 : Paramètre d'événement
     */
    EVENT_PARAM(45, "Paramètre d'événement"),
    /**
     * 046 : profil utilisateur
     */
    PROFIL_UTILISATEUR(46, "profil utilisateur"),
    /**
     * 047 : Folder
     */
    FOLDER(47, "Folder"),
    /**
     * 048 : File upload
     */
    FILE_UPLOAD(48, "File upload"),
    /**
     * 049 : Stream
     */
    STREAM(49, "Stream"),
    /**
     * 050 : Ministère
     */
    MINISTERE(50, "Ministère"),
    /**
     * 051 : Opération UpdateList
     */
    OPERATION_UPDATE_LIST(51, "Opération UpdateList"),
    /**
     * 052 : Injection de Gouvernement
     */
    INJECTION_GOUVERNEMENT(52, "Injection de Gouvernement"),
    /**
     * 053 : Gouvernement
     */
    GOUVERNEMENT(53, "Gouvernement"),
    /**
     * 054 : Action de masse
     */
    MASS(54, "Action de masse"),
    /**
     * 055 : Transaction
     */
    TRANSACTION(55, "Transaction"),
    /**
     * 056 : Doctype
     */
    DOCTYPE(56, "Doctype"),
    /**
     * 057 : Hostname
     */
    HOSTNAME(57, "Hostname"),
    /**
     * 058 : Password
     */
    PASSWORD(58, "Password"),
    /**
     * 059 : Identité
     */
    IDENTITE_TDR(59, "Identité"),
    /**
     * 060 : Acteur
     */
    ACTEUR_TDR(60, "Acteur"),
    /**
     * 061 : Convertisseur
     */
    CONVERTER(61, "Convertisseur"),
    /**
     * 062 : Icon
     */
    ICON(62, "Icone"),
    /**
     * 063 : html
     */
    HTML(63, "HTML"),
    /**
     * 064 : ACLS
     */
    ACLS(64, "acls"),
    /**
     * 065 : PDFs
     */
    PDF(65, "PDF"),
    /**
     * 066 : tableau dynamique
     */
    TABLEAU_DYNAMIQUE(66, "Tableau_Dynamique"),
    /**
     * 067 : statistique
     */
    STAT(67, "Statistique"),
    /**
     * 068 : directory
     */
    DIRECTORY(68, "Directory"),
    /**
     * 069 : key
     */
    KEY(69, "Key"),
    /**
     * 070 : pan
     */
    PAN(70, "PAN"),
    /*
     * 071 : Feuille route
     */
    FDR(71, "Feuille route"),
    /*
     * 072 : étape de feuille de route
     */
    STEP(72, "Etape de feuille de route"),
    /*
     * 073 : Vocabulaire
     */
    VOC(73, "Vocabulaire"),
    /*
     * 074 : Fichier csv
     */
    CSV_FILE(73, "Fichier csv"),

    /*
     * ####################################### BATCHS
     */
    /**
     * 086 : Déverrouillage de l'organigramme
     */
    BATCH_UNLOCK_ORGA(86, "Déverrouillage de l'organigramme"),
    /**
     * 087 : Batch de purge de la table des tentatives de connexion
     */
    BATCH_PURGE_TENTATIVES_CONNEXION(87, "Batch de purge des tentatives de connexion"),
    /**
     * 088 : Batch result
     */
    BATCH_RESULT(88, "BatchResult"),
    /**
     * 089 : Batch lanceur général
     */
    BATCH_LANCEUR_GENERAL(89, "Batch lanceur général"),
    /**
     * 090 : Batch de renouvellement de mot de passe
     */
    BATCH_DAILY_REMINDER_CHANGE_PASS(90, "Batch de prévenance de renouvellement de mot de passe"),
    /**
     * 091 : Batch de suppression des documents
     */
    BATCH_DEL_DOC(91, "Batch de suppression des documents"),
    /**
     * 092 : Batch de notification aux WS
     */
    BATCH_NOTIF_WS(92, "Batch de notification aux WS"),
    /**
     * 093 : solver
     */
    SOLVER(93, "Solver de classe"),
    /**
     * 094 : Batch d'envoi des alertes
     */
    BATCH_SEND_ALERTS(94, "Batch d'envoi des alertes"),
    /**
     * 095 : Déverrouillage des documents
     */
    BATCH_UNLOCK_DOC(95, "Déverrouillage des documents"),
    /**
     * 096 : Batch suppression dossiers links
     */
    BATCH_DEL_DL(96, "Batch suppression dossiers links"),
    /**
     * 097 : event
     */
    EVENT(97, "Event"),
    /**
     * 098 : logger
     */
    LOGGER(98, "Logger"),
    /**
     * 099 : batch
     */
    BATCH(99, "Batch");

    /*
     * Rien au dela de 99 !
     */

    /* **** (Ne pas oublier de tenir à jour la documentation en lien avec le code) **** */

    private int codeNumber;
    private String codeText;

    STObjetsEnum(int codeNumber, String codeText) {
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
        NumberFormat formatter = new DecimalFormat("000");
        return formatter.format(this.codeNumber);
    }
}
