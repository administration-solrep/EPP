package fr.dila.st.ui.enums.parlement;

import static fr.dila.st.api.constant.ParlementSchemaConstants.EVENEMENT_SCHEMA_PREFIX;
import static fr.dila.st.api.constant.ParlementSchemaConstants.VERSION_SCHEMA_PREFIX;
import static fr.dila.st.api.constant.STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX;
import static fr.dila.st.ui.enums.WidgetTypeEnum.INPUT_TEXT;
import static fr.dila.st.ui.enums.WidgetTypeEnum.MULTIPLE_DATE;
import static fr.dila.st.ui.enums.WidgetTypeEnum.MULTIPLE_INPUT_TEXT;
import static fr.dila.st.ui.enums.WidgetTypeEnum.RADIO;
import static fr.dila.st.ui.enums.WidgetTypeEnum.SELECT;
import static fr.dila.st.ui.enums.WidgetTypeEnum.TEXT;
import static fr.dila.st.ui.enums.WidgetTypeEnum.TEXT_AREA;

import fr.dila.st.ui.enums.WidgetTypeEnum;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CommunicationMetadonneeEnum {
    TITLE("title", DUBLINCORE_SCHEMA_PREFIX, INPUT_TEXT),
    EVENEMENT_PARENT("evenementParent", EVENEMENT_SCHEMA_PREFIX, INPUT_TEXT),
    DOSSIER("dossier", EVENEMENT_SCHEMA_PREFIX, INPUT_TEXT),
    ID_DOSSIER("idDossier", EVENEMENT_SCHEMA_PREFIX, INPUT_TEXT),
    DOSSIER_PRECEDENT("dossierPrecedent", EVENEMENT_SCHEMA_PREFIX, INPUT_TEXT),
    EMETTEUR("emetteur", EVENEMENT_SCHEMA_PREFIX, SELECT),
    DESTINATAIRE("destinataire", EVENEMENT_SCHEMA_PREFIX, SELECT),
    DESTINATAIRE_COPIE("destinataireCopie", EVENEMENT_SCHEMA_PREFIX, TEXT),
    HORODATAGE("horodatage", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    SENAT("senat", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    NIVEAU_LECTURE_NUMERO("niveauLectureNumero", VERSION_SCHEMA_PREFIX),
    NIVEAU_LECTURE("niveauLecture", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.NIVEAU_LECTURE),
    DATE_AR("dateAr", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    OBJET("objet", VERSION_SCHEMA_PREFIX, TEXT_AREA),
    IDENTIFIANT_METIER("identifiantMetier", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    DATE_DEMANDE("dateDemande", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    NOR_LOI("norLoi", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    NOR("nor", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    TYPE_LOI("typeLoi", VERSION_SCHEMA_PREFIX, SELECT),
    NATURE_LOI("natureLoi", VERSION_SCHEMA_PREFIX, SELECT),
    AUTEUR("auteur", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    ORGANISME("organisme", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    COAUTEUR("coauteur", VERSION_SCHEMA_PREFIX, MULTIPLE_INPUT_TEXT),
    INTITULE("intitule", VERSION_SCHEMA_PREFIX, TEXT_AREA),
    DESCRIPTION("description", DUBLINCORE_SCHEMA_PREFIX, TEXT_AREA),
    URL_DOSSIER_AN("urlDossierAn", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    URL_DOSSIER_SENAT("urlDossierSenat", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    COSIGNATAIRE("cosignataire", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    DATE_DEPOT_TEXTE("dateDepotTexte", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DATE_CMP("dateCMP", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.MULTIPLE_DATE),
    NUMERO_DEPOT_TEXTE("numeroDepotTexte", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    COMMISSION_SAISIE_AU_FOND("commissionSaisieAuFond", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    COMMISSION_SAISIE_POUR_AVIS("commissionSaisiePourAvis", VERSION_SCHEMA_PREFIX, MULTIPLE_INPUT_TEXT),
    COMMISSIONS("commissions", VERSION_SCHEMA_PREFIX, MULTIPLE_INPUT_TEXT),
    DATE_SAISINE("dateSaisine", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DATE_RETRAIT("dateRetrait", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DATE_DISTRIBUTION_ELECTRONIQUE("dateDistributionElectronique", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    NATURE("nature", VERSION_SCHEMA_PREFIX, SELECT),
    RAPPORTEUR_LIST("rapporteurList", VERSION_SCHEMA_PREFIX, MULTIPLE_INPUT_TEXT),
    TITRE("titre", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    DATE_DEPOT_RAPPORT("dateDepotRapport", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    NUMERO_DEPOT_RAPPORT("numeroDepotRapport", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    COMMISSION_SAISIE("commissionSaisie", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    ATTRIBUTION_COMMISSION("attributionCommission", VERSION_SCHEMA_PREFIX, SELECT),
    DATE_REFUS("dateRefus", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    LIBELLE_ANNEXE("libelleAnnexes", VERSION_SCHEMA_PREFIX, MULTIPLE_INPUT_TEXT),
    DOSSIER_LEGISLATIF("dossierLegislatif", VERSION_SCHEMA_PREFIX, MULTIPLE_INPUT_TEXT),
    DATE_ENGAGEMENT_PROCEDURE("dateEngagementProcedure", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DATE_REFUS_PROCEDURE_ENGAGEMENT_AN("dateRefusProcedureEngagementAn", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DATE_REFUS_PROCEDURE_ENGAGEMENT_SENAT(
        "dateRefusProcedureEngagementSenat",
        VERSION_SCHEMA_PREFIX,
        WidgetTypeEnum.DATE
    ),
    DATE_REFUS_ENGAGEMENT_PROCEDURE("dateRefusEngagementProcedure", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DATE_ADOPTION("dateAdoption", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    NUMERO_TEXTE_ADOPTE("numeroTexteAdopte", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    SORT_ADOPTION("sortAdoption", VERSION_SCHEMA_PREFIX, SELECT),
    RESULTAT_CMP("resultatCMP", VERSION_SCHEMA_PREFIX, SELECT),
    POSITION_ALERTE("positionAlerte", VERSION_SCHEMA_PREFIX),
    REDEPOT("redepot", VERSION_SCHEMA_PREFIX, RADIO),
    NATURE_RAPPORT("natureRapport", VERSION_SCHEMA_PREFIX, SELECT),
    MOTIF_IRRECEVABILITE("motifIrrecevabilite", VERSION_SCHEMA_PREFIX, SELECT),
    DATE("date", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    ANNEE_RAPPORT("anneeRapport", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    URL_BASE_LEGALE("urlBaseLegale", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    BASE_LEGALE("baseLegale", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    PARLEMENTAIRE_TITULAIRE_LIST("parlementaireTitulaireList", VERSION_SCHEMA_PREFIX, MULTIPLE_INPUT_TEXT),
    PARLEMENTAIRE_SUPPLEANT_LIST("parlementaireSuppleantList", VERSION_SCHEMA_PREFIX, MULTIPLE_INPUT_TEXT),
    DATE_PROMULGATION("datePromulgation", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DATE_PUBLICATION("datePublication", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    NUMERO_LOI("numeroLoi", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    DATE_CONGRES("dateCongres", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    TYPE_ACTE("typeActe", VERSION_SCHEMA_PREFIX, SELECT),
    DATE_ACTE("dateActe", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DATE_CONVOCATION("dateConvocation", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DATE_DESIGNATION("dateDesignation", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    NUMERO_JO("numeroJo", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    PAGE_JO("pageJo", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    ANNEE_JO("anneeJo", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    DATE_JO("dateJo", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    NUMERO_RUBRIQUE("numeroRubrique", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    URL_PUBLICATION("urlPublication", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    ECHEANCE("echeance", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    SENS_AVIS("sensAvis", VERSION_SCHEMA_PREFIX, SELECT),
    SUFFRAGE_EXPRIME("suffrageExprime", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    BULLETIN_BLANC("bulletinBlanc", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    VOTE_POUR("votePour", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    VOTE_CONTRE("voteContre", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    ABSTENTION("abstention", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    DATE_CADUCITE("dateCaducite", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DOSSIER_CIBLE("dossierCible", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    RAPPORT_PARLEMENT("rapportParlement", VERSION_SCHEMA_PREFIX, SELECT),
    RECTIFICATIF("rectificatif", VERSION_SCHEMA_PREFIX, RADIO),
    DATE_VOTE("dateVote", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DEMANDE_VOTE("demandeVote", VERSION_SCHEMA_PREFIX, RADIO),
    DATE_DECLARATION("dateDeclaration", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DATE_PRESENTATION("datePresentation", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    GROUPE_PARLEMENTAIRE("groupeParlementaire", VERSION_SCHEMA_PREFIX, MULTIPLE_INPUT_TEXT),
    PERSONNE("personne", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    FONCTION("fonction", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    DATE_AUDITION("dateAudition", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DATE_LETTRE_PM("dateLettrePm", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DATE_REFUS_ASSEMBLEE_1("dateRefusAssemblee1", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DATE_CONFERENCE_ASSEMBLEE_2("dateConferenceAssemblee2", VERSION_SCHEMA_PREFIX, WidgetTypeEnum.DATE),
    DECISION_PROC_ACC("decisionProcAcc", VERSION_SCHEMA_PREFIX, SELECT),
    RUBRIQUE("rubrique", VERSION_SCHEMA_PREFIX, INPUT_TEXT),
    DATE_LIST("dateList", VERSION_SCHEMA_PREFIX, MULTIPLE_DATE);

    private final String name;
    private final String prefix;
    private final WidgetTypeEnum editWidgetType;

    CommunicationMetadonneeEnum(String name, String prefix) {
        this(name, prefix, TEXT);
    }

    CommunicationMetadonneeEnum(String name, String prefix, WidgetTypeEnum editWidgetType) {
        this.name = name;
        this.prefix = prefix;
        this.editWidgetType = editWidgetType;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * @return type du widget pour modification
     */
    public WidgetTypeEnum getEditWidgetType() {
        return editWidgetType;
    }

    /**
     * Retourne le {@link CommunicationMetadonneeEnum} à partir de son nom
     */
    public static CommunicationMetadonneeEnum fromString(String name) {
        return Stream.of(values()).filter(cme -> cme.getName().equals(name)).findFirst().orElse(null);
    }

    public static List<String> getAllNamesFromWidgetType(WidgetTypeEnum type) {
        return Stream
            .of(values())
            .filter(cme -> cme.getEditWidgetType() == type)
            .map(CommunicationMetadonneeEnum::getName)
            .collect(Collectors.toList());
    }
}
