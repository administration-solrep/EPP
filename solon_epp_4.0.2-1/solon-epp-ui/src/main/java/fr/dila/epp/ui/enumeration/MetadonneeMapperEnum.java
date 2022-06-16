package fr.dila.epp.ui.enumeration;

import static java.util.Optional.ofNullable;

import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.st.api.constant.ParlementSchemaConstants;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum;
import fr.dila.st.ui.enums.parlement.WidgetModeEnum;
import fr.dila.st.ui.utils.VocabularyUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public enum MetadonneeMapperEnum {
    TITLE(
        CommunicationMetadonneeEnum.TITLE,
        (evenement, version) -> evenement.getIdEvenement(),
        (object, value) -> ((Evenement) object).setIdEvenement((String) value)
    ),
    EVENEMENT_PARENT(
        CommunicationMetadonneeEnum.EVENEMENT_PARENT,
        (evenement, version) -> evenement.getEvenementParent(),
        (object, value) -> ((Evenement) object).setEvenementParent((String) value)
    ),
    DOSSIER(
        CommunicationMetadonneeEnum.DOSSIER,
        (evenement, version) -> evenement.getDossier(),
        (object, value) -> ((Evenement) object).setDossier((String) value)
    ),
    ID_DOSSIER(
        CommunicationMetadonneeEnum.ID_DOSSIER,
        (evenement, version) -> evenement.getDossier(),
        (object, value) -> ((Evenement) object).setDossier((String) value)
    ),
    DOSSIER_PRECEDENT(
        CommunicationMetadonneeEnum.DOSSIER_PRECEDENT,
        (evenement, version) -> evenement.getDossierPrecedent(),
        (object, value) -> ((Evenement) object).setDossierPrecedent((String) value)
    ),
    EMETTEUR(
        CommunicationMetadonneeEnum.EMETTEUR,
        (evenement, version) -> InstitutionsEnum.getLabelFromInstitutionKey(evenement.getEmetteur()),
        (object, value) -> ((Evenement) object).setEmetteur((String) value),
        (evenement, version) -> evenement.getEmetteur()
    ),
    DESTINATAIRE(
        CommunicationMetadonneeEnum.DESTINATAIRE,
        (evenement, version) -> InstitutionsEnum.getLabelFromInstitutionKey(evenement.getDestinataire()),
        (object, value) -> ((Evenement) object).setDestinataire((String) value),
        (evenement, version) -> evenement.getDestinataire()
    ),
    DESTINATAIRE_COPIE(
        CommunicationMetadonneeEnum.DESTINATAIRE_COPIE,
        (evenement, version) ->
            evenement
                .getDestinataireCopie()
                .stream()
                .map(InstitutionsEnum::getLabelFromInstitutionKey)
                .collect(Collectors.joining(", ")),
        (object, value) -> ((Evenement) object).setDestinataireCopie(Arrays.asList((String) value)),
        (evenement, version) -> evenement.getDestinataireCopieConcat()
    ),
    HORODATAGE(
        CommunicationMetadonneeEnum.HORODATAGE,
        (evenement, version) ->
            ofNullable(version.getHorodatage())
                .map(SolonDateConverter.DATETIME_SLASH_SECOND_COLON::format)
                .orElse(null),
        (a, b) -> {}
    ),
    SENAT(
        CommunicationMetadonneeEnum.SENAT,
        (evenement, version) -> version.getSenat(),
        (object, value) -> ((Version) object).setSenat((String) value)
    ),
    NIVEAU_LECTURE_NUMERO(
        CommunicationMetadonneeEnum.NIVEAU_LECTURE_NUMERO,
        (evenement, version) -> version.getNiveauLectureNumero(),
        (object, value) -> ((Version) object).setNiveauLectureNumero(NumberUtils.toLong((String) value))
    ),
    NIVEAU_LECTURE(
        CommunicationMetadonneeEnum.NIVEAU_LECTURE,
        (evenement, version) ->
            (
                SolonEppVocabularyConstant.NIVEAU_LECTURE_AN_VALUE.equals(version.getNiveauLecture()) ||
                    SolonEppVocabularyConstant.NIVEAU_LECTURE_SENAT_VALUE.equals(version.getNiveauLecture())
                    ? version.getNiveauLectureNumero() + " - "
                    : ""
            ) +
            SolonEppActionsServiceLocator
                .getMetadonneesActionService()
                .getNiveauLectureLabel(version.getNiveauLecture()),
        (object, value) -> ((Version) object).setNiveauLecture((String) value),
        (evenement, version) -> version.getNiveauLecture()
    ),
    DATE_AR(
        CommunicationMetadonneeEnum.DATE_AR,
        (evenement, version) ->
            ofNullable(version.getDateAr()).map(SolonDateConverter.DATETIME_SLASH_MINUTE_COLON::format).orElse(null),
        (object, value) ->
            ((Version) object).setDateAr(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    OBJET(
        CommunicationMetadonneeEnum.OBJET,
        (evenement, version) -> version.getObjet(),
        (object, value) -> ((Version) object).setObjet((String) value)
    ),
    IDENTIFIANT_METIER(
        CommunicationMetadonneeEnum.IDENTIFIANT_METIER,
        (evenement, version) -> version.getIdentifiantMetier(),
        (object, value) -> ((Version) object).setIdentifiantMetier((String) value)
    ),
    DATE_DEMANDE(
        CommunicationMetadonneeEnum.DATE_DEMANDE,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateDemande()),
        (object, value) ->
            ((Version) object).setDateDemande(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    NOR_LOI(
        CommunicationMetadonneeEnum.NOR_LOI,
        (evenement, version) -> version.getNorLoi(),
        (object, value) -> ((Version) object).setNorLoi((String) value)
    ),
    NOR(
        CommunicationMetadonneeEnum.NOR,
        (evenement, version) -> version.getNor(),
        (object, value) -> ((Version) object).setNor((String) value)
    ),
    TYPE_LOI(
        CommunicationMetadonneeEnum.TYPE_LOI,
        (evenement, version) ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.TYPE_LOI_VOCABULARY,
                version.getTypeLoi()
            ),
        (object, value) -> ((Version) object).setTypeLoi((String) value),
        (evenement, version) -> version.getTypeLoi()
    ),
    NATURE_LOI(
        CommunicationMetadonneeEnum.NATURE_LOI,
        (evenement, version) ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.NATURE_LOI_VOCABULARY,
                version.getNatureLoi()
            ),
        (object, value) -> ((Version) object).setNatureLoi((String) value),
        (evenement, version) -> version.getNatureLoi()
    ),
    AUTEUR(
        CommunicationMetadonneeEnum.AUTEUR,
        (evenement, version) -> version.getAuteur(),
        (object, value) -> ((Version) object).setAuteur((String) value)
    ),
    ORGANISME(
        CommunicationMetadonneeEnum.ORGANISME,
        (evenement, version) -> version.getOrganisme(),
        (object, value) -> ((Version) object).setOrganisme((String) value)
    ),
    COAUTEUR(
        CommunicationMetadonneeEnum.COAUTEUR,
        (evenement, version) -> new ArrayList<>(version.getCoauteur()),
        (object, value) -> ((Version) object).setCoauteur((List<String>) value)
    ),
    INTITULE(
        CommunicationMetadonneeEnum.INTITULE,
        (evenement, version) -> version.getIntitule(),
        (object, value) -> ((Version) object).setIntitule((String) value)
    ),
    DESCRIPTION(
        CommunicationMetadonneeEnum.DESCRIPTION,
        (evenement, version) -> version.getDescription(),
        (object, value) -> ((Version) object).setDescription((String) value)
    ),
    URL_DOSSIER_AN(
        CommunicationMetadonneeEnum.URL_DOSSIER_AN,
        (evenement, version) -> version.getUrlDossierAn(),
        (object, value) -> ((Version) object).setUrlDossierAn((String) value)
    ),
    URL_DOSSIER_SENAT(
        CommunicationMetadonneeEnum.URL_DOSSIER_SENAT,
        (evenement, version) -> version.getUrlDossierSenat(),
        (object, value) -> ((Version) object).setUrlDossierSenat((String) value)
    ),
    COSIGNATAIRE(
        CommunicationMetadonneeEnum.COSIGNATAIRE,
        (evenement, version) -> version.getCosignataire(),
        (object, value) -> ((Version) object).setCosignataire((String) value)
    ),
    DATE_DEPOT_TEXTE(
        CommunicationMetadonneeEnum.DATE_DEPOT_TEXTE,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateDepotTexte()),
        (object, value) ->
            ((Version) object).setDateDepotTexte(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    DATE_CMP(
        CommunicationMetadonneeEnum.DATE_CMP,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateCmp()),
        (object, value) ->
            ((Version) object).setDateCmp(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    NUMERO_DEPOT_TEXTE(
        CommunicationMetadonneeEnum.NUMERO_DEPOT_TEXTE,
        (evenement, version) -> version.getNumeroDepotTexte(),
        (object, value) -> ((Version) object).setNumeroDepotTexte((String) value)
    ),
    COMMISSION_SAISIE_AU_FOND(
        CommunicationMetadonneeEnum.COMMISSION_SAISIE_AU_FOND,
        (evenement, version) -> version.getCommissionSaisieAuFond(),
        (object, value) -> ((Version) object).setCommissionSaisieAuFond((String) value)
    ),
    COMMISSION_SAISIE_POUR_AVIS(
        CommunicationMetadonneeEnum.COMMISSION_SAISIE_POUR_AVIS,
        (evenement, version) -> new ArrayList<>(version.getCommissionSaisiePourAvis()),
        (object, value) -> ((Version) object).setCommissionSaisiePourAvis((List<String>) value)
    ),
    COMMISSIONS(
        CommunicationMetadonneeEnum.COMMISSIONS,
        (evenement, version) -> new ArrayList<>(version.getCommissions()),
        (object, value) -> ((Version) object).setCommissions((List<String>) value)
    ),
    DATE_SAISINE(
        CommunicationMetadonneeEnum.DATE_SAISINE,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateSaisine()),
        (object, value) ->
            ((Version) object).setDateSaisine(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    DATE_RETRAIT(
        CommunicationMetadonneeEnum.DATE_RETRAIT,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateRetrait()),
        (object, value) ->
            ((Version) object).setDateRetrait(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    DATE_DISTRIBUTION_ELECTRONIQUE(
        CommunicationMetadonneeEnum.DATE_DISTRIBUTION_ELECTRONIQUE,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateDistributionElectronique()),
        (object, value) ->
            ((Version) object).setDateDistributionElectronique(
                    SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value)
                )
    ),
    NATURE(
        CommunicationMetadonneeEnum.NATURE,
        (evenement, version) ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.NATURE_RAPPORT_VOCABULARY,
                version.getNature()
            ),
        (object, value) -> ((Version) object).setNature((String) value),
        (evenement, version) -> version.getNature()
    ),
    RAPPORTEUR_LIST(
        CommunicationMetadonneeEnum.RAPPORTEUR_LIST,
        (evenement, version) -> new ArrayList<>(version.getRapporteurList()),
        (object, value) -> ((Version) object).setRapporteurList((List<String>) value)
    ),
    TITRE(
        CommunicationMetadonneeEnum.TITRE,
        (evenement, version) -> version.getTitre(),
        (object, value) -> ((Version) object).setTitre((String) value)
    ),
    DATE_DEPOT_RAPPORT(
        CommunicationMetadonneeEnum.DATE_DEPOT_RAPPORT,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateDepotRapport()),
        (object, value) ->
            ((Version) object).setDateDepotRapport(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    NUMERO_DEPOT_RAPPORT(
        CommunicationMetadonneeEnum.NUMERO_DEPOT_RAPPORT,
        (evenement, version) -> version.getNumeroDepotRapport(),
        (object, value) -> ((Version) object).setNumeroDepotRapport((String) value)
    ),
    COMMISSION_SAISIE(
        CommunicationMetadonneeEnum.COMMISSION_SAISIE,
        (evenement, version) -> version.getCommissionSaisie(),
        (object, value) -> ((Version) object).setCommissionSaisie((String) value)
    ),
    ATTRIBUTION_COMMISSION(
        CommunicationMetadonneeEnum.ATTRIBUTION_COMMISSION,
        (evenement, version) ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.ATTRIBUTION_COMMISSION_VOCABULARY,
                version.getAttributionCommission()
            ),
        (object, value) -> ((Version) object).setAttributionCommission((String) value),
        (evenement, version) -> version.getAttributionCommission()
    ),
    DATE_REFUS(
        CommunicationMetadonneeEnum.DATE_REFUS,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateRefus()),
        (object, value) ->
            ((Version) object).setDateRefus(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    LIBELLE_ANNEXE(
        CommunicationMetadonneeEnum.LIBELLE_ANNEXE,
        (evenement, version) -> String.join(", ", version.getLibelleAnnexe()),
        (object, value) -> ((Version) object).setLibelleAnnexe((List<String>) value)
    ),
    DOSSIER_LEGISLATIF(
        CommunicationMetadonneeEnum.DOSSIER_LEGISLATIF,
        (evenement, version) -> String.join(", ", version.getDossierLegislatif()),
        (object, value) -> ((Version) object).setDossierLegislatif(toStringList(value))
    ),
    DATE_ENGAGEMENT_PROCEDURE(
        CommunicationMetadonneeEnum.DATE_ENGAGEMENT_PROCEDURE,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateEngagementProcedure()),
        (object, value) ->
            ((Version) object).setDateEngagementProcedure(
                    SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value)
                )
    ),
    DATE_REFUS_PROCEDURE_ENGAGEMENT_AN(
        CommunicationMetadonneeEnum.DATE_REFUS_PROCEDURE_ENGAGEMENT_AN,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateRefusProcedureEngagementAn()),
        (object, value) ->
            ((Version) object).setDateRefusProcedureEngagementAn(
                    SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value)
                )
    ),
    DATE_REFUS_PROCEDURE_ENGAGEMENT_SENAT(
        CommunicationMetadonneeEnum.DATE_REFUS_PROCEDURE_ENGAGEMENT_SENAT,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateRefusProcedureEngagementSenat()),
        (object, value) ->
            ((Version) object).setDateRefusProcedureEngagementSenat(
                    SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value)
                )
    ),
    DATE_REFUS_ENGAGEMENT_PROCEDURE(
        CommunicationMetadonneeEnum.DATE_REFUS_ENGAGEMENT_PROCEDURE,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateRefusEngagementProcedure()),
        (object, value) ->
            ((Version) object).setDateRefusEngagementProcedure(
                    SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value)
                )
    ),
    DATE_ADOPTION(
        CommunicationMetadonneeEnum.DATE_ADOPTION,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateAdoption()),
        (object, value) ->
            ((Version) object).setDateAdoption(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    NUMERO_TEXTE_ADOPTE(
        CommunicationMetadonneeEnum.NUMERO_TEXTE_ADOPTE,
        (evenement, version) -> version.getNumeroTexteAdopte(),
        (object, value) -> ((Version) object).setNumeroTexteAdopte((String) value)
    ),
    SORT_ADOPTION(
        CommunicationMetadonneeEnum.SORT_ADOPTION,
        (evenement, version) ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.SORT_ADOPTION_VOCABULARY,
                version.getSortAdoption()
            ),
        (object, value) -> ((Version) object).setSortAdoption((String) value),
        (evenement, version) -> version.getSortAdoption()
    ),
    RESULTAT_CMP(
        CommunicationMetadonneeEnum.RESULTAT_CMP,
        (evenement, version) ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.RESULTAT_CMP_VOCABULARY,
                version.getResultatCMP()
            ),
        (object, value) -> ((Version) object).setResultatCMP((String) value),
        (evenement, version) -> version.getResultatCMP()
    ),
    POSITION_ALERTE(
        CommunicationMetadonneeEnum.POSITION_ALERTE,
        (evenement, version) ->
            ResourceHelper.getString(
                version.isPositionAlerte()
                    ? "label.epp.metadonnee.positionAlerte.debut"
                    : "label.epp.metadonnee.positionAlerte.fin"
            ),
        (object, value) -> ((Version) object).setPositionAlerte(BooleanUtils.toBoolean((String) value))
    ),
    REDEPOT(
        CommunicationMetadonneeEnum.REDEPOT,
        (evenement, version) -> version.isRedepot() ? "Oui" : "Non",
        (object, value) -> ((Version) object).setRedepot(BooleanUtils.toBoolean((String) value)),
        (evenement, version) -> version.isRedepot()
    ),
    NATURE_RAPPORT(
        CommunicationMetadonneeEnum.NATURE_RAPPORT,
        (evenement, version) ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.NATURE_RAPPORT_VOCABULARY,
                version.getNatureRapport()
            ),
        (object, value) -> ((Version) object).setNatureRapport((String) value),
        (evenement, version) -> version.getNatureRapport()
    ),
    MOTIF_IRRECEVABILITE(
        CommunicationMetadonneeEnum.MOTIF_IRRECEVABILITE,
        (evenement, version) ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.MOTIF_IRRECEVABILITE_VOCABULARY,
                version.getMotifIrrecevabilite()
            ),
        (object, value) -> ((Version) object).setMotifIrrecevabilite((String) value),
        (evenement, version) -> version.getMotifIrrecevabilite()
    ),
    DATE(
        CommunicationMetadonneeEnum.DATE,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDate()),
        (object, value) ->
            ((Version) object).setDate(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    ANNEE_RAPPORT(
        CommunicationMetadonneeEnum.ANNEE_RAPPORT,
        (evenement, version) -> version.getAnneeRapport(),
        (object, value) -> ((Version) object).setAnneeRapport(NumberUtils.toLong((String) value))
    ),
    URL_BASE_LEGALE(
        CommunicationMetadonneeEnum.URL_BASE_LEGALE,
        (evenement, version) -> version.getUrlBaseLegale(),
        (object, value) -> ((Version) object).setUrlBaseLegale((String) value)
    ),
    BASE_LEGALE(
        CommunicationMetadonneeEnum.BASE_LEGALE,
        (evenement, version) -> version.getBaseLegale(),
        (object, value) -> ((Version) object).setBaseLegale((String) value)
    ),
    PARLEMENTAIRE_TITULAIRE_LIST(
        CommunicationMetadonneeEnum.PARLEMENTAIRE_TITULAIRE_LIST,
        (evenement, version) -> new ArrayList<>(version.getParlementaireTitulaireList()),
        (object, value) -> ((Version) object).setParlementaireTitulaireList((List<String>) value)
    ),
    PARLEMENTAIRE_SUPPLEANT_LIST(
        CommunicationMetadonneeEnum.PARLEMENTAIRE_SUPPLEANT_LIST,
        (evenement, version) -> new ArrayList<>(version.getParlementaireSuppleantList()),
        (object, value) -> ((Version) object).setParlementaireSuppleantList((List<String>) value)
    ),
    DATE_PROMULGATION(
        CommunicationMetadonneeEnum.DATE_PROMULGATION,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDatePromulgation()),
        (object, value) ->
            ((Version) object).setDatePromulgation(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    DATE_PUBLICATION(
        CommunicationMetadonneeEnum.DATE_PUBLICATION,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDatePublication()),
        (object, value) ->
            ((Version) object).setDatePublication(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    NUMERO_LOI(
        CommunicationMetadonneeEnum.NUMERO_LOI,
        (evenement, version) -> version.getNumeroLoi(),
        (object, value) -> ((Version) object).setNumeroLoi((String) value)
    ),
    DATE_CONGRES(
        CommunicationMetadonneeEnum.DATE_CONGRES,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateCongres()),
        (object, value) ->
            ((Version) object).setDateCongres(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    TYPE_ACTE(
        CommunicationMetadonneeEnum.TYPE_ACTE,
        (evenement, version) ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.TYPE_ACTE_VOCABULARY,
                version.getTypeActe()
            ),
        (object, value) -> ((Version) object).setTypeActe((String) value),
        (evenement, version) -> version.getTypeActe()
    ),
    DATE_ACTE(
        CommunicationMetadonneeEnum.DATE_ACTE,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateActe()),
        (object, value) ->
            ((Version) object).setDateActe(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    DATE_CONVOCATION(
        CommunicationMetadonneeEnum.DATE_CONVOCATION,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateConvocation()),
        (object, value) ->
            ((Version) object).setDateConvocation(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    DATE_DESIGNATION(
        CommunicationMetadonneeEnum.DATE_DESIGNATION,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateDesignation()),
        (object, value) ->
            ((Version) object).setDateDesignation(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    NUMERO_JO(
        CommunicationMetadonneeEnum.NUMERO_JO,
        (evenement, version) -> version.getNumeroJo(),
        (object, value) -> ((Version) object).setNumeroJo(NumberUtils.toLong((String) value))
    ),
    PAGE_JO(
        CommunicationMetadonneeEnum.PAGE_JO,
        (evenement, version) -> version.getPageJo(),
        (object, value) -> ((Version) object).setPageJo(NumberUtils.toLong((String) value))
    ),
    ANNEE_JO(
        CommunicationMetadonneeEnum.ANNEE_JO,
        (evenement, version) -> version.getAnneeJo(),
        (object, value) -> ((Version) object).setAnneeJo(NumberUtils.toLong((String) value))
    ),
    DATE_JO(
        CommunicationMetadonneeEnum.DATE_JO,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateJo()),
        (object, value) ->
            ((Version) object).setDateJo(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    NUMERO_RUBRIQUE(
        CommunicationMetadonneeEnum.NUMERO_RUBRIQUE,
        (evenement, version) -> version.getNumeroRubrique(),
        (object, value) -> ((Version) object).setNumeroRubrique(NumberUtils.toLong((String) value))
    ),
    URL_PUBLICATION(
        CommunicationMetadonneeEnum.URL_PUBLICATION,
        (evenement, version) -> version.getUrlPublication(),
        (object, value) -> ((Version) object).setUrlPublication((String) value)
    ),
    ECHEANCE(
        CommunicationMetadonneeEnum.ECHEANCE,
        (evenement, version) -> version.getEcheance(),
        (object, value) -> ((Version) object).setTypeActe((String) value)
    ),
    SENS_AVIS(
        CommunicationMetadonneeEnum.SENS_AVIS,
        (evenement, version) ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.SENS_AVIS_VOCABULARY,
                version.getSensAvis()
            ),
        (object, value) -> ((Version) object).setSensAvis((String) value),
        (evenement, version) -> version.getSensAvis()
    ),
    SUFFRAGE_EXPRIME(
        CommunicationMetadonneeEnum.SUFFRAGE_EXPRIME,
        (evenement, version) -> version.getSuffrageExprime(),
        (object, value) -> ((Version) object).setSuffrageExprime(NumberUtils.toLong((String) value))
    ),
    BULLETIN_BLANC(
        CommunicationMetadonneeEnum.BULLETIN_BLANC,
        (evenement, version) -> version.getBulletinBlanc(),
        (object, value) -> ((Version) object).setBulletinBlanc(NumberUtils.toLong((String) value))
    ),
    VOTE_POUR(
        CommunicationMetadonneeEnum.VOTE_POUR,
        (evenement, version) -> version.getVotePour(),
        (object, value) -> ((Version) object).setVotePour(NumberUtils.toLong((String) value))
    ),
    VOTE_CONTRE(
        CommunicationMetadonneeEnum.VOTE_CONTRE,
        (evenement, version) -> version.getVoteContre(),
        (object, value) -> ((Version) object).setVoteContre(NumberUtils.toLong((String) value))
    ),
    ABSTENTION(
        CommunicationMetadonneeEnum.ABSTENTION,
        (evenement, version) -> version.getAbstention(),
        (object, value) -> ((Version) object).setAbstention(NumberUtils.toLong((String) value))
    ),
    DATE_CADUCITE(
        CommunicationMetadonneeEnum.DATE_CADUCITE,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateCaducite()),
        (object, value) ->
            ((Version) object).setDateCaducite(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    DOSSIER_CIBLE(
        CommunicationMetadonneeEnum.DOSSIER_CIBLE,
        (evenement, version) -> version.getDossierCible(),
        (object, value) -> ((Version) object).setDossierCible((String) value)
    ),
    RAPPORT_PARLEMENT(
        CommunicationMetadonneeEnum.RAPPORT_PARLEMENT,
        (evenement, version) ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.RAPPORT_PARLEMENT_VOCABULARY,
                version.getRapportParlement()
            ),
        (object, value) -> ((Version) object).setRapportParlement((String) value),
        (evenement, version) -> version.getRapportParlement()
    ),
    RECTIFICATIF(
        CommunicationMetadonneeEnum.RECTIFICATIF,
        (evenement, version) -> version.isRectificatif() ? "Oui" : "Non",
        (object, value) -> ((Version) object).setRectificatif(BooleanUtils.toBoolean((String) value)),
        (evenement, version) -> version.isRectificatif()
    ),
    DATE_VOTE(
        CommunicationMetadonneeEnum.DATE_VOTE,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateVote()),
        (object, value) ->
            ((Version) object).setDateVote(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    DEMANDE_VOTE(
        CommunicationMetadonneeEnum.DEMANDE_VOTE,
        (evenement, version) -> version.getDemandeVote() ? "Oui" : "Non",
        (object, value) -> ((Version) object).setDemandeVote(BooleanUtils.toBoolean((String) value)),
        (evenement, version) -> version.getDemandeVote()
    ),
    DATE_DECLARATION(
        CommunicationMetadonneeEnum.DATE_DECLARATION,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateDeclaration()),
        (object, value) ->
            ((Version) object).setDateDeclaration(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    DATE_PRESENTATION(
        CommunicationMetadonneeEnum.DATE_PRESENTATION,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDatePresentation()),
        (object, value) ->
            ((Version) object).setDatePresentation(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    GROUPE_PARLEMENTAIRE(
        CommunicationMetadonneeEnum.GROUPE_PARLEMENTAIRE,
        (evenement, version) -> new ArrayList<>(version.getGroupeParlementaire()),
        (object, value) -> ((Version) object).setGroupeParlementaire((List<String>) value)
    ),
    PERSONNE(
        CommunicationMetadonneeEnum.PERSONNE,
        (evenement, version) -> version.getPersonne(),
        (object, value) -> ((Version) object).setPersonne((String) value)
    ),
    FONCTION(
        CommunicationMetadonneeEnum.FONCTION,
        (evenement, version) -> version.getFonction(),
        (object, value) -> ((Version) object).setFonction((String) value)
    ),
    DATE_AUDITION(
        CommunicationMetadonneeEnum.DATE_AUDITION,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateAudition()),
        (object, value) ->
            ((Version) object).setDateAudition(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    DATE_LETTRE_PM(
        CommunicationMetadonneeEnum.DATE_LETTRE_PM,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateLettrePm()),
        (object, value) ->
            ((Version) object).setDateLettrePm(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value))
    ),
    DATE_REFUS_ASSEMBLEE_1(
        CommunicationMetadonneeEnum.DATE_REFUS_ASSEMBLEE_1,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateRefusAssemblee1()),
        (object, value) ->
            ((Version) object).setDateRefusASsemblee1(
                    SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value)
                )
    ),
    DATE_CONFERENCE_ASSEMBLEE_2(
        CommunicationMetadonneeEnum.DATE_CONFERENCE_ASSEMBLEE_2,
        (evenement, version) -> SolonDateConverter.DATE_SLASH.format(version.getDateConferencePresidentsAssemblee2()),
        (object, value) ->
            ((Version) object).setDateConferencePresidentsAssemblee2(
                    SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value)
                )
    ),
    DECISION_PROC_ACC(
        CommunicationMetadonneeEnum.DECISION_PROC_ACC,
        (evenement, version) ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.DECISION_PROC_ACC,
                version.getDecisionProcAcc()
            ),
        (object, value) -> ((Version) object).setDecisionProcAcc((String) value),
        (evenement, version) -> version.getDecisionProcAcc()
    ),
    RUBRIQUE(
        CommunicationMetadonneeEnum.RUBRIQUE,
        (evenement, version) ->
            VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
                SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY,
                version.getRubrique()
            ),
        (object, value) -> ((Version) object).setRubrique((String) value),
        (evenement, version) -> version.getRubrique()
    ),
    DATE_LIST(
        CommunicationMetadonneeEnum.DATE_LIST,
        (evenement, version) ->
            version.getDateList().stream().map(SolonDateConverter.DATE_SLASH::format).collect(Collectors.joining(", ")),
        (object, value) ->
            ((Version) object).setDateList(
                    ((List<String>) value).stream()
                        .map(SolonDateConverter.DATE_SLASH::parseToCalendarOrNull)
                        .collect(Collectors.toList())
                ),
        (evenement, version) ->
            (Serializable) version
                .getDateList()
                .stream()
                .map(SolonDateConverter.DATE_SLASH::format)
                .collect(Collectors.toList())
    );

    private final CommunicationMetadonneeEnum field;
    private final BiFunction<Evenement, Version, Serializable> valueGetterFunction;
    private final BiConsumer<Object, Object> valueSetterFunction;
    private final BiFunction<Evenement, Version, Serializable> editValueGetterFunction;

    MetadonneeMapperEnum(
        CommunicationMetadonneeEnum field,
        BiFunction<Evenement, Version, Serializable> valueGetterFunction,
        BiConsumer<Object, Object> valueSetterFunction
    ) {
        this(field, valueGetterFunction, valueSetterFunction, valueGetterFunction);
    }

    MetadonneeMapperEnum(
        CommunicationMetadonneeEnum field,
        BiFunction<Evenement, Version, Serializable> valueGetterFunction,
        BiConsumer<Object, Object> valueSetterFunction,
        BiFunction<Evenement, Version, Serializable> editValueGetterFunction
    ) {
        this.field = field;
        this.valueGetterFunction = valueGetterFunction;
        this.valueSetterFunction = valueSetterFunction;
        this.editValueGetterFunction = editValueGetterFunction;
    }

    /**
     * @return fonction de récupération de la valeur pour affichage
     */
    public BiFunction<Evenement, Version, Serializable> getValueGetterFunction() {
        return valueGetterFunction;
    }

    /**
     * @return fonction de renseignement de la valeur
     */
    public BiConsumer<Object, Object> getValueSetterFunction() {
        return valueSetterFunction;
    }

    public Serializable invokeGetter(WidgetModeEnum mode, Evenement event, Version version) {
        return (
            (WidgetModeEnum.EDIT == mode && DESTINATAIRE_COPIE != this)
                ? this.getEditValueGetterFunction()
                : this.getValueGetterFunction()
        ).apply(event, version);
    }

    public void invokeSetter(Evenement curEvenement, Version curVersion, Object value) {
        valueSetterFunction.accept(
            ParlementSchemaConstants.VERSION_SCHEMA_PREFIX.equals(field.getPrefix()) ||
                CommunicationMetadonneeEnum.DESCRIPTION.equals(field)
                ? curVersion
                : curEvenement,
            value
        );
    }

    /**
     * @return fonction de récupération de la valeur pour affichage
     */
    public BiFunction<Evenement, Version, Serializable> getEditValueGetterFunction() {
        return editValueGetterFunction;
    }

    public CommunicationMetadonneeEnum getField() {
        return field;
    }

    public static MetadonneeMapperEnum getMapperFromCommunicationField(CommunicationMetadonneeEnum field) {
        return Stream.of(values()).filter(mapper -> mapper.getField() == field).findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    private static List<String> toStringList(Object value) {
        if (value instanceof List) {
            return (List<String>) value;
        }
        if (value instanceof String) {
            String strVal = (String) value;
            return StringUtils.isBlank(strVal) ? Collections.emptyList() : Arrays.asList(strVal);
        }
        return Collections.emptyList();
    }
}
