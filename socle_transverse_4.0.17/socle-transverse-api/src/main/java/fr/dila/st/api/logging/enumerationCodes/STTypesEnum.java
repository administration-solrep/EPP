package fr.dila.st.api.logging.enumerationCodes;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Énumération du type des actions <br>
 * Décompte sur 3 chiffres, le premier (0) indique qu'il s'agit d'un type du ST <br>
 * 000 : Défaut <br>
 * 001 : Création <br>
 * 002 : Modification <br>
 * 003 : Suppression <br>
 * 004 : Lecture <br>
 * 005 : ActionLog <br>
 * 006 : Récupération <br>
 * 007 : Utilisation <br>
 * 008 : Début <br>
 * 009 : Fin <br>
 * 010 : Echec suppression <br>
 * 011 : Echec récupération <br>
 * 012 : Echec log <br>
 * 013 : Echec http <br>
 * 014 : Echec création <br>
 * 015 : Echec d'accès <br>
 * 016 : Echec d'enregistrement <br>
 * 017 : Echec de modification <br>
 * 018 : Anomalie d'occurrence : occurrence multiple <br>
 * 019 : Anomalie d'occurrence : aucune occurrence <br>
 * 020 : Dénormalisation <br>
 * 021 : Objet inattendu <br>
 * 022 : Envoi <br>
 * 023 : Echec d'envoi <br>
 * 024 : Echec de traitement <br>
 * 025 : Traitement <br>
 * 026 : Echec déverrouillage <br>
 * 027 : Annulation <br>
 * 028 : NPE <br>
 * 029 : Echec de notification <br>
 * 030 : Echec d'initialisation <br>
 * 031 : Echec de filtrage <br>
 * 032 : Echec de conversion <br>
 * 033 : Unregistering module <br>
 * 034 : Echec d'association <br>
 * 035 : Echec de construction <br>
 * 036 : Distribution <br>
 * 037 : Accusé de réception <br>
 * 038 : Publication <br>
 * 039 : Demande <br>
 * 040 : Echec d'ajout <br>
 * 041 : Notification <br>
 * 042 : Changement de l'état <br>
 * 043 : Echec d'execution <br>
 * 044 : Report <br>
 * 045 : Validation <br>
 * 046 : Verouillage <br>
 * 047 : Navigation <br>
 * 048 : Echec de publication <br>
 * 049 : Chargement <br>
 * 050 : Echec de navigation <br>
 * 051 : Echec d'assignation <br>
 * 052 : Echec de validation <br>
 * 053 : Echec de remplissage <br>
 * 054 : Analyse <br>
 * 055 : Migration <br>
 * 056 : Echec de migration <br>
 * 057 : Echec d'export <br>
 * 058 : Echec de compression <br>
 * 059 : Echec de diffusion <br>
 * 060 : Echec de géneration <br>
 * 061 : Echec de nettoyage <br>
 * 062 : Echec de comptage <br>
 * 063 : Sauvegarde <br>
 * 064 : Echec de commit <br>
 * 065 : Start <br>
 * 066 : Echec de fermeture <br>
 * 067 : Invalidation <br>
 * 068 : Echec d'obtention du verrou <br>
 * 069 : Dénombrement <br>
 */
public enum STTypesEnum implements STCodes {
    /**
     * 000
     */
    DEFAULT(0, "Type défaut"),
    /**
     * 001 : Création
     */
    CREATE(1, "Création"),
    /**
     * 002 : Modification
     */
    UPDATE(2, "Modification"),
    /**
     * 003 : Suppression
     */
    DELETE(3, "Suppression"),
    /**
     * 004 : lecture
     */
    READ(4, "Lecture"),
    /**
     * 005 : actionLog
     */
    ACTION_LOG(5, "Action log"),
    /**
     * 006 : récupération
     */
    GET(6, "Récupération"),
    /**
     * 007 : utilisation
     */
    USE(7, "Utilisation"),
    /**
     * 008 : Début
     */
    INIT(8, "Initialisation"),
    /**
     * 009 : Fin
     */
    END(9, "Fin"),
    /**
     * 010 : Echec suppression
     */
    FAIL_DEL(10, "Echec suppression"),
    /**
     * 011 : Echec récupération
     */
    FAIL_GET(11, "Echec récupération"),
    /**
     * 012 : Echec log
     */
    FAIL_LOG(12, "Echec log"),
    /**
     * 013 : Echec http
     */
    FAIL_HTTP(13, "Echec connexion"),
    /**
     * 014 : echec creation
     */
    FAIL_CREATE(14, "Echec creation"),
    /**
     * 015 : echec d'accès
     */
    FAIL_ACCESS(15, "Echec d'accès"),
    /**
     * 016 : echec d'enregistrement
     */
    FAIL_SAVE(16, "Echec d'enregistrement"),
    /**
     * 017 : echec de modification
     */
    FAIL_UPDATE(17, "Echec de modification"),
    /**
     * 018 : multi d'occurrence
     */
    OCCURRENCE_MULTI(18, "Anomalie d'occurrence : multiples"),
    /**
     * 019 : aucune d'occurrence
     */
    OCCURRENCE_NONE(19, "Anomalie d'occurrence : aucune"),
    /**
     * 020 : dénormalisation
     */
    DENORMALISATION(20, "Dénormalisation"),
    /**
     * 021 : objet inattendu
     */
    UNEXPECTED(21, "Objet inattendu"),
    /**
     * 022 : envoi
     */
    SEND(22, "Envoi"),
    /**
     * 023 : echec envoi
     */
    FAIL_SEND(23, "Echec envoi"),
    /**
     * 024 : Echec de traitement
     */
    FAIL_PROCESS(24, "Echec de traitement"),
    /**
     * 025 : Traitement
     */
    PROCESS(25, "Traitement"),
    /**
     * 026 : Echec déverrouillage
     */
    FAIL_UNLOCK(26, "Echec de déverrouillage"),
    /**
     * 027 : Annulation
     */
    CANCEL(27, "Annulation"),
    /**
     * 028 : NPE
     */
    NPE(28, "NPE"),
    /**
     * 029 : Echec notification
     */
    FAIL_NOTIFICATION(29, "Echec de notification"),
    /**
     * 030 : Echec initialisation
     */
    FAIL_INITIALISATION(30, "Echec initialisation"),
    /**
     * 031 : Echec de filtrage
     */
    FAIL_FILTER(31, "Echec de filtrage"),
    /**
     * 032 : Echec de conversion
     */
    FAIL_CONVERT(32, "Echec de conversion"),
    /**
     * 033 : Unregistering de module
     */
    DESTROY(33, "Unregistering module"),
    /**
     * 034 : échec d'association
     */
    FAIL_ASSOCIATE(34, "Echec d'association"),
    /**
     * 035 : échec de construction
     */
    FAIL_BUILD(35, "Echec de construction"),
    /**
     * 036 : Distribution
     */
    DISTRIBUTION(36, "Distribution"),
    /**
     * 037 : Accusé de réception
     */
    ACKNOWLEDGMENT(37, "Accusé de réception"),
    /**
     * 038 : Publication
     */
    PUBLICATION(38, "Publication"),
    /**
     * 039 : Demande
     */
    REQUEST(39, "Demande"),
    /**
     * 040 : Echec d'ajout
     */
    FAIL_ADD(40, "Echec d'ajout"),
    /**
     * 041 : Notification
     */
    NOTIFICATION(41, "Notification"),
    /**
     * 042 : Changement de l'état
     */
    CHANGE_STATE(42, "Changement de l'état"),
    /**
     * 043 : Echec d'execution
     */
    FAIL_EXECUTE(43, "Echec d'execution"),
    /**
     * 044 : Report
     */
    POSTPONE(44, "Report"),
    /**
     * 045 : Validation
     */
    VALIDATE(45, "Validation"),
    /**
     * 046 : Verrouiller
     */
    LOCK(46, "Verrouillage"),
    /**
     * 047 : Navigation
     */
    NAVIGATE(47, "Navigation"),
    /**
     * 048 : echec de publication
     */
    FAIL_PUBLISH(48, "Echec de publication"),
    /**
     * 049 : Chargement
     */
    LOADING(49, "Chargement"),
    /**
     * 050 : Echec de Navigation
     */
    FAIL_NAVIGATE(50, "Echec de navigation"),
    /**
     * 051 : Echec d'assignation
     */
    FAIL_SET(51, "Echec d'assignation"),
    /**
     * 052 : Echec de Validation
     */
    FAIL_VALIDATE(52, "Echec de validation"),
    /**
     * 053 : Echec de Remplissage
     */
    FAIL_FILL(53, "Echec de remplissage"),
    /**
     * 054 : Analyse
     */
    PARSE(54, "Analyse"),
    /**
     * 055 : Migration
     */
    MIGRATE(55, "Migration"),
    /**
     * 056 : Echec migration
     */
    FAIL_MIGRATE(56, "Echec migration"),
    /**
     * 057 : Echec d'export
     */
    FAIL_EXPORT(57, "Echec d'export"),
    /**
     * 058 : Echec de compression
     */
    FAIL_ZIP(58, "Echec de compression"),
    /**
     * 059 : Echec de diffusion
     */
    FAIL_DIFFUSION(59, "Echec de diffusion"),
    /**
     * 060 : Echec de géneration
     */
    FAIL_GENERATE(60, "Echec de géneration"),
    /**
     * 061 : Echec de nettayage
     */
    FAIL_CLEAR(61, "Echec de nettoyage"),
    /**
     * 062 : Echec de comptage
     */
    FAIL_COUNT(62, "Echec de comptage"),
    /**
     * 063 : Sauvegarde
     */
    SAVE(63, "Sauvegarde"),
    /**
     * 064 : Echec de commit
     */
    FAIL_COMMIT(64, "Echec de commit"),
    /**
     * 065 : Démarrage
     */
    START(65, "Démarrage"),
    /**
     * 066 : Echec de fermeture
     */
    FAIL_CLOSE(66, "Echec de fermeture"),
    /**
     * 067 : Invalidation
     */
    INVALIDATE(67, "Invalidation"),
    /**
     * 068 : Echec d'obtention du verrou
     */
    FAIL_LOCK(68, "Echec d'obtention du verrou"),
    /**
     * 069 : Dénombrement
     */
    COUNT(69, "Dénombrement"),
    /**
     * 070 : Echec de login
     */
    FAIL_LOGIN(70, "Echec de login"),
    /**
     * 071 : Echec de logout
     */
    FAIL_LOGOUT(71, "Echec de logout"),
    /**
     * 072 : Échec d'ouverture
     */
    FAIL_OPEN(72, "Échec d'ouverture"),
    /**
     * 073 : Echec d'inalidation
     */
    FAIL_INVALIDATE(73, "Echec d'invalidation");

    /* **** (Ne pas oublier de tenir à jour la documentation en lien avec le code) **** */

    private int codeNumber;
    private String codeText;

    STTypesEnum(int codeNumber, String codeText) {
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
