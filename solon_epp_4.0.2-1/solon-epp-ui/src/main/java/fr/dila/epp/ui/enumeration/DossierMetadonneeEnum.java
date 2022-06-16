package fr.dila.epp.ui.enumeration;

import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.utils.VocabularyUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DossierMetadonneeEnum {
    TITLE(1, "title", Dossier::getTitle),
    EMETTEUR(2, "emetteur", dossier -> InstitutionsEnum.getLabelFromInstitutionKey(dossier.getEmetteur())),
    DESTINATAIRE(3, "destinataire", dossier -> InstitutionsEnum.getLabelFromInstitutionKey(dossier.getDestinataire())),
    HORODATAGE(4, "horodatage", dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getHorodatage())),
    SENAT(5, "senat", Dossier::getSenat),
    NIVEAU_LECTURE(
        6,
        "niveauLecture",
        dossier ->
            (
                SolonEppVocabularyConstant.NIVEAU_LECTURE_AN_VALUE.equals(dossier.getNiveauLecture()) ||
                    SolonEppVocabularyConstant.NIVEAU_LECTURE_SENAT_VALUE.equals(dossier.getNiveauLecture())
                    ? dossier.getNiveauLectureNumero() + " - "
                    : ""
            ) +
            SolonEppActionsServiceLocator
                .getMetadonneesActionService()
                .getNiveauLectureLabel(dossier.getNiveauLecture())
    ),
    DATE_PUBLICATION(
        7,
        "datePublication",
        dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDatePublication())
    ),
    OBJET(8, "objet", Dossier::getObjet),
    IDENTIFIANT_METIER(9, "identifiantMetier", Dossier::getIdentifiantMetier),
    NOR(10, "nor", Dossier::getNor),
    TYPE_LOI(
        11,
        "typeLoi",
        dossier ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.TYPE_LOI_VOCABULARY,
                dossier.getTypeLoi()
            )
    ),
    NATURE_LOI(
        12,
        "natureLoi",
        dossier ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.NATURE_LOI_VOCABULARY,
                dossier.getNatureLoi()
            )
    ),
    AUTEUR(13, "auteur", Dossier::getAuteur),
    ORGANISME(14, "organisme", Dossier::getOrganisme),
    COAUTEUR(15, "coauteur", dossier -> new ArrayList<>(dossier.getCoauteur())),
    INTITULE(16, "intitule", Dossier::getIntitule),
    DATE_VOTE(17, "dateVote", dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateVote())),
    DEMANDE_VOTE(18, "demandeVote", dossier -> dossier.isDemandeVote() ? "Oui" : "Non"),
    DATE_DECLARATION(
        19,
        "dateDeclaration",
        dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateDeclaration())
    ),
    DATE_PRESENTATION(
        20,
        "datePresentation",
        dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDatePresentation())
    ),
    DESCRIPTION(21, "description", Dossier::getDescription),
    URL_DOSSIER_AN(22, "urlDossierAn", Dossier::getUrlDossierAn),
    URL_DOSSIER_SENAT(23, "urlDossierSenat", Dossier::getUrlDossierSenat),
    COSIGNATAIRE(24, "cosignataire", Dossier::getCosignataire),
    DATE_DEPOT_TEXTE(
        25,
        "dateDepotTexte",
        dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateDepotTexte())
    ),
    DATE_CMP(26, "dateCMP", dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateCmp())),
    NUMERO_DEPOT_TEXTE(27, "numeroDepotTexte", Dossier::getNumeroDepotTexte),
    COMMISSION_SAISIE_AU_FOND(28, "commissionSaisieAuFond", Dossier::getCommissionSaisieAuFond),
    COMMISSION_SAISIE_POUR_AVIS(
        29,
        "commissionSaisiePourAvis",
        dossier -> new ArrayList<>(dossier.getCommissionSaisiePourAvis())
    ),
    DATE_RETRAIT(30, "dateRetrait", dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateRetrait())),
    DATE_DISTRIBUTION_ELECTRONIQUE(
        31,
        "dateDistributionElectronique",
        dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateDistributionElectronique())
    ),
    RAPPORTEUR_LIST(32, "rapporteurList", dossier -> new ArrayList<>(dossier.getRapporteurList())),
    TITRE(33, "titre", Dossier::getTitre),
    DATE_DEPOT_RAPPORT(
        34,
        "dateDepotRapport",
        dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateDepotRapport())
    ),
    NUMERO_DEPOT_RAPPORT(35, "numeroDepotRapport", Dossier::getNumeroDepotRapport),
    COMMISSION_SAISIE(36, "commissionSaisie", Dossier::getCommissionSaisie),
    DATE_REFUS(37, "dateRefus", dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateRefus())),
    LIBELLE_ANNEXE(
        38,
        "libelleAnnexe",
        dossier -> dossier.getLibelleAnnexe().stream().collect(Collectors.joining(", "))
    ),
    DATE_ENGAGEMENT_PROCEDURE(
        39,
        "dateEngagementProcedure",
        dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateEngagementProcedure())
    ),
    DATE_REFUS_PROCEDURE_ENGAGEMENT_AN(
        40,
        "dateRefusProcedureEngagementAn",
        dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateRefusProcedureEngagementAn())
    ),
    DATE_REFUS_PROCEDURE_ENGAGEMENT_SENAT(
        41,
        "dateRefusProcedureEngagementSenat",
        dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateRefusProcedureEngagementSenat())
    ),
    DATE_REFUS_ENGAGEMENT_PROCEDURE(
        42,
        "dateRefusEngagementProcedure",
        dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateRefusEngagementProcedure())
    ),
    DATE_ADOPTION(43, "dateAdoption", dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateAdoption())),
    NUMERO_TEXTE_ADOPTE(44, "numeroTexteAdopte", Dossier::getNumeroTexteAdopte),
    SORT_ADOPTION(
        45,
        "sortAdoption",
        dossier ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.SORT_ADOPTION_VOCABULARY,
                dossier.getSortAdoption()
            )
    ),
    REDEPOT(46, "redepot", dossier -> dossier.isRedepot() ? "Oui" : "Non"),
    POSITION_ALERTE(
        47,
        "positionAlerte",
        dossier ->
            ResourceHelper.getString(
                dossier.isPositionAlerte()
                    ? "label.epp.metadonnee.positionAlerte.debut"
                    : "label.epp.metadonnee.positionAlerte.fin"
            )
    ),
    ECHEANCE(48, "echeance", Dossier::getEcheance),
    SENS_AVIS(
        49,
        "sensAvis",
        dossier ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.SENS_AVIS_VOCABULARY,
                dossier.getSensAvis()
            )
    ),
    SUFFRAGE_EXPRIME(50, "suffrageExprime", Dossier::getSuffrageExprime),
    BULLETIN_BLANC(51, "bulletinBlanc", Dossier::getBulletinBlanc),
    VOTE_POUR(52, "votePour", Dossier::getVotePour),
    VOTE_CONTRE(53, "voteContre", Dossier::getVoteContre),
    ABSTENTION(54, "abstention", Dossier::getAbstention),
    DATE_ACTE(55, "dateActe", dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateActe())),
    COMMISSIONS(56, "commissions", dossier -> new ArrayList<>(dossier.getCommissions())),
    GROUPE_PARLEMENTAIRE(57, "groupeParlementaire", dossier -> new ArrayList<>(dossier.getGroupeParlementaire())),
    PERSONNE(58, "personne", Dossier::getPersonne),
    FONCTION(59, "fonction", Dossier::getFonction),
    DATE_AUDITION(60, "dateAudition", dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateAudition())),
    DATE_LETTRE_PM(61, "dateLettrePm", dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateLettrePm())),
    DATE_DEMANDE(62, "dateDemande", dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateDemande())),
    DOSSIER_CIBLE(63, "dossierCible", Dossier::getDossierCible),
    BASE_LEGALE(64, "baseLegale", Dossier::getBaseLegale),
    DATE_REFUS_ASSEMBLEE_1(
        65,
        "dateRefusAssemblee1",
        dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateRefusAssemblee1())
    ),
    DATE_CONFERENCE_ASSEMBLEE_2(
        66,
        "dateConferenceAssemblee2",
        dossier -> SolonDateConverter.DATE_SLASH.format(dossier.getDateConferenceAssemblee2())
    ),
    DECISION_PROC_ACC(
        67,
        "decisionProcAcc",
        dossier ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.DECISION_PROC_ACC,
                dossier.getDecisionProcAcc()
            )
    );

    private final int order;
    private final String name;
    private final Function<Dossier, Serializable> valueFunction;

    DossierMetadonneeEnum(int order, String name, Function<Dossier, Serializable> valueFunction) {
        this.order = order;
        this.name = name;
        this.valueFunction = valueFunction;
    }

    public int getOrder() {
        return order;
    }

    public String getName() {
        return name;
    }

    public Function<Dossier, Serializable> getValueFunction() {
        return valueFunction;
    }

    public static DossierMetadonneeEnum fromString(String name) {
        return Stream.of(values()).filter(dme -> dme.getName().equals(name)).findFirst().orElse(null);
    }

    public static boolean hasMetadonnee(String name) {
        return Stream.of(values()).anyMatch(dme -> dme.getName().equals(name));
    }
}
