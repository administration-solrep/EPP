package fr.dila.solonepp.core.domain.evenement;

import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.evenement.NumeroVersion;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation de l'objet métier version d'un événement.
 *
 * @author jtremeaux
 */
public class VersionImpl implements Version {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -843043275996496880L;
    /**
     * Modèle de document.
     */
    protected DocumentModel document;

    /**
     * Constructeur de VersionImpl.
     *
     * @param document Modèle de document
     */
    public VersionImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public DocumentModel getDocument() {
        return document;
    }

    @Override
    public List<String> getModifiedMetaList() {
        String value = PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_MODIFIED_META_LIST_PROPERTY
        );
        List<String> list = new ArrayList<String>();
        if (value != null) {
            Collections.addAll(list, value.split(";"));
        }
        return list;
    }

    @Override
    public void setModifiedMetaList(List<String> modifiedMetaList) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_MODIFIED_META_LIST_PROPERTY,
            StringUtils.join(modifiedMetaList, ";")
        );
    }

    // *************************************************************
    // Données de Dublin Core.
    // *************************************************************
    @Override
    public String getTitle() {
        return DublincoreSchemaUtils.getTitle(document);
    }

    @Override
    public void setTitle(String title) {
        DublincoreSchemaUtils.setTitle(document, title);
    }

    @Override
    public String getDescription() {
        return DublincoreSchemaUtils.getDescription(document);
    }

    @Override
    public void setDescription(String description) {
        DublincoreSchemaUtils.setDescription(document, description);
    }

    // *************************************************************
    // Données communes de la version.
    // *************************************************************
    @Override
    public String getEvenement() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_EVENEMENT_PROPERTY
        );
    }

    @Override
    public void setEvenement(String evenement) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_EVENEMENT_PROPERTY,
            evenement
        );
    }

    @Override
    public Long getNiveauLectureNumero() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_NUMERO_PROPERTY
        );
    }

    @Override
    public void setNiveauLectureNumero(Long niveauLectureNumero) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_NUMERO_PROPERTY,
            niveauLectureNumero
        );
    }

    @Override
    public String getNiveauLecture() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_PROPERTY
        );
    }

    @Override
    public void setNiveauLecture(String niveauLecture) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_PROPERTY,
            niveauLecture
        );
    }

    @Override
    public Calendar getHorodatage() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_HORODATAGE_PROPERTY
        );
    }

    @Override
    public void setHorodatage(Calendar horodatage) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_HORODATAGE_PROPERTY,
            horodatage
        );
    }

    @Override
    public Calendar getDateAr() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_AR_PROPERTY
        );
    }

    @Override
    public void setDateAr(Calendar typeVersion) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_AR_PROPERTY,
            typeVersion
        );
    }

    @Override
    public String getObjet() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_OBJET_PROPERTY
        );
    }

    @Override
    public void setObjet(String objet) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_OBJET_PROPERTY,
            objet
        );
    }

    @Override
    public String getIdentifiantMetier() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_IDENTIFIANT_METIER_PROPERTY
        );
    }

    @Override
    public void setIdentifiantMetier(String identifiantMetier) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_IDENTIFIANT_METIER_PROPERTY,
            identifiantMetier
        );
    }

    @Override
    public Long getMajorVersion() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_MAJOR_VERSION_PROPERTY
        );
    }

    @Override
    public void setMajorVersion(Long majorVersion) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_MAJOR_VERSION_PROPERTY,
            majorVersion
        );
    }

    @Override
    public Long getMinorVersion() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_MINOR_VERSION_PROPERTY
        );
    }

    @Override
    public void setMinorVersion(Long minorVersion) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_MINOR_VERSION_PROPERTY,
            minorVersion
        );
    }

    @Override
    public String getSenat() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_SENAT_PROPERTY
        );
    }

    @Override
    public void setSenat(String senat) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_SENAT_PROPERTY,
            senat
        );
    }

    @Override
    public String getModeCreation() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_MODE_CREATION_PROPERTY
        );
    }

    @Override
    public void setModeCreation(String senat) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_MODE_CREATION_PROPERTY,
            senat
        );
    }

    @Override
    public boolean isPieceJointePresente() {
        return PropertyUtil.getBooleanProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_PIECE_JOINTE_PRESENTE_PROPERTY
        );
    }

    @Override
    public void setPieceJointePresente(boolean pieceJointePresente) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_PIECE_JOINTE_PRESENTE_PROPERTY,
            pieceJointePresente
        );
    }

    @Override
    public String getNature() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NATURE_PROPERTY
        );
    }

    @Override
    public void setNature(String nature) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NATURE_PROPERTY,
            nature
        );
    }

    @Override
    public boolean isVersionCourante() {
        return PropertyUtil.getBooleanProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_VERSION_COURANTE_PROPERTY
        );
    }

    @Override
    public void setVersionCourante(boolean versionCourante) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_VERSION_COURANTE_PROPERTY,
            versionCourante
        );
    }

    // *************************************************************
    // Données de la catégorie d'événement "procédure législative".
    // *************************************************************
    @Override
    public String getNor() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NOR_PROPERTY
        );
    }

    @Override
    public void setNor(String nor) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NOR_PROPERTY,
            nor
        );
    }

    @Override
    public String getNorLoi() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NOR_LOI_PROPERTY
        );
    }

    @Override
    public void setNorLoi(String norLoi) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NOR_LOI_PROPERTY,
            norLoi
        );
    }

    @Override
    public String getNatureLoi() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NATURE_LOI_PROPERTY
        );
    }

    @Override
    public void setNatureLoi(String natureLoi) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NATURE_LOI_PROPERTY,
            natureLoi
        );
    }

    @Override
    public String getTypeLoi() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_TYPE_LOI_PROPERTY
        );
    }

    @Override
    public void setTypeLoi(String natureLoi) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_TYPE_LOI_PROPERTY,
            natureLoi
        );
    }

    @Override
    public String getAuteur() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_AUTEUR_PROPERTY
        );
    }

    @Override
    public void setAuteur(String auteur) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_AUTEUR_PROPERTY,
            auteur
        );
    }

    @Override
    public List<String> getCoauteur() {
        return PropertyUtil.getStringListProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_COAUTEUR_PROPERTY
        );
    }

    @Override
    public void setCoauteur(List<String> coAuteur) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_COAUTEUR_PROPERTY,
            coAuteur
        );
    }

    @Override
    public String getIntitule() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_INTITULE_PROPERTY
        );
    }

    @Override
    public void setIntitule(String intitule) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_INTITULE_PROPERTY,
            intitule
        );
    }

    @Override
    public String getUrlDossierAn() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_URL_DOSSIER_AN_PROPERTY
        );
    }

    @Override
    public void setUrlDossierAn(String urlDossierAn) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_URL_DOSSIER_AN_PROPERTY,
            urlDossierAn
        );
    }

    @Override
    public String getUrlDossierSenat() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_URL_DOSSIER_SENAT_PROPERTY
        );
    }

    @Override
    public void setUrlDossierSenat(String urlDossierSenat) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_URL_DOSSIER_SENAT_PROPERTY,
            urlDossierSenat
        );
    }

    @Override
    public String getCosignataire() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_COSIGNATAIRE_PROPERTY
        );
    }

    @Override
    public void setCosignataire(String cosignataire) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_COSIGNATAIRE_PROPERTY,
            cosignataire
        );
    }

    @Override
    public Calendar getDateDepotTexte() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_DEPOT_TEXTE_PROPERTY
        );
    }

    @Override
    public void setDateDepotTexte(Calendar dateDepotTexte) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_DEPOT_TEXTE_PROPERTY,
            dateDepotTexte
        );
    }

    @Override
    public String getNumeroDepotTexte() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_DEPOT_TEXTE_PROPERTY
        );
    }

    @Override
    public void setNumeroDepotTexte(String numeroDepotTexte) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_DEPOT_TEXTE_PROPERTY,
            numeroDepotTexte
        );
    }

    @Override
    public String getCommissionSaisieAuFond() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_COMMISSION_SAISIE_AU_FOND_PROPERTY
        );
    }

    @Override
    public void setCommissionSaisieAuFond(String commissionSaisieAuFond) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_COMMISSION_SAISIE_AU_FOND_PROPERTY,
            commissionSaisieAuFond
        );
    }

    @Override
    public List<String> getCommissionSaisiePourAvis() {
        return PropertyUtil.getStringListProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_COMMISSION_SAISIE_POUR_AVIS_PROPERTY
        );
    }

    @Override
    public void setCommissionSaisiePourAvis(List<String> commissionSaisiePourAvis) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_COMMISSION_SAISIE_POUR_AVIS_PROPERTY,
            commissionSaisiePourAvis
        );
    }

    @Override
    public Calendar getDate() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_PROPERTY
        );
    }

    @Override
    public void setDate(Calendar date) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_PROPERTY,
            date
        );
    }

    @Override
    public Calendar getDateSaisine() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_SAISINE_PROPERTY
        );
    }

    @Override
    public void setDateSaisine(Calendar dateSaisine) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_SAISINE_PROPERTY,
            dateSaisine
        );
    }

    @Override
    public Calendar getDateRetrait() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_RETRAIT_PROPERTY
        );
    }

    @Override
    public void setDateRetrait(Calendar dateRetrait) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_RETRAIT_PROPERTY,
            dateRetrait
        );
    }

    @Override
    public Calendar getDateDistributionElectronique() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_DISTRIBUTION_ELECTRONIQUE_PROPERTY
        );
    }

    @Override
    public void setDateDistributionElectronique(Calendar dateDistributionElectronique) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_DISTRIBUTION_ELECTRONIQUE_PROPERTY,
            dateDistributionElectronique
        );
    }

    @Override
    public String getNatureRapport() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NATURE_RAPPORT_PROPERTY
        );
    }

    @Override
    public void setNatureRapport(String natureRapport) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NATURE_RAPPORT_PROPERTY,
            natureRapport
        );
    }

    @Override
    public List<String> getRapporteurList() {
        return PropertyUtil.getStringListProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_RAPPORTEUR_LIST_PROPERTY
        );
    }

    @Override
    public void setRapporteurList(List<String> rapportteurList) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_RAPPORTEUR_LIST_PROPERTY,
            rapportteurList
        );
    }

    @Override
    public String getTitre() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_TITRE_PROPERTY
        );
    }

    @Override
    public void setTitre(String titre) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_TITRE_PROPERTY,
            titre
        );
    }

    @Override
    public Calendar getDateDepotRapport() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_DEPOT_RAPPORT_PROPERTY
        );
    }

    @Override
    public void setDateDepotRapport(Calendar dateDepotRapport) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_DEPOT_RAPPORT_PROPERTY,
            dateDepotRapport
        );
    }

    @Override
    public String getNumeroDepotRapport() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_DEPOT_RAPPORT_PROPERTY
        );
    }

    @Override
    public void setNumeroDepotRapport(String numeroDepotRepport) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_DEPOT_RAPPORT_PROPERTY,
            numeroDepotRepport
        );
    }

    @Override
    public String getCommissionSaisie() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_COMMISSION_SAISIE_PROPERTY
        );
    }

    @Override
    public void setCommissionSaisie(String commissionSaisie) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_COMMISSION_SAISIE_PROPERTY,
            commissionSaisie
        );
    }

    @Override
    public String getAttributionCommission() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_ATTRIBUTION_COMMISSION_PROPERTY
        );
    }

    @Override
    public void setAttributionCommission(String attributionCommission) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_ATTRIBUTION_COMMISSION_PROPERTY,
            attributionCommission
        );
    }

    @Override
    public Calendar getDateRefus() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_REFUS_PROPERTY
        );
    }

    @Override
    public void setDateRefus(Calendar dateRefus) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_REFUS_PROPERTY,
            dateRefus
        );
    }

    @Override
    public List<String> getLibelleAnnexe() {
        return PropertyUtil.getStringListProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_LIBELLE_ANNEXE_PROPERTY
        );
    }

    @Override
    public void setLibelleAnnexe(List<String> libelleAnnexe) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_LIBELLE_ANNEXE_PROPERTY,
            libelleAnnexe
        );
    }

    @Override
    public Calendar getDateEngagementProcedure() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_ENGAGEMENT_PROCEDURE_PROPERTY
        );
    }

    @Override
    public void setDateEngagementProcedure(Calendar dateEngagementProcedure) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_ENGAGEMENT_PROCEDURE_PROPERTY,
            dateEngagementProcedure
        );
    }

    @Override
    public Calendar getDateRefusProcedureEngagementAn() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_REFUS_PROCEDURE_ENGAGEMENT_AN_PROPERTY
        );
    }

    @Override
    public void setDateRefusProcedureEngagementAn(Calendar dateRefusProcedureEngagementAn) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_REFUS_PROCEDURE_ENGAGEMENT_AN_PROPERTY,
            dateRefusProcedureEngagementAn
        );
    }

    @Override
    public Calendar getDateRefusProcedureEngagementSenat() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_REFUS_PROCEDURE_ENGAGEMENT_SENAT_PROPERTY
        );
    }

    @Override
    public void setDateRefusProcedureEngagementSenat(Calendar dateRefusProcedureEngagementSenat) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_REFUS_PROCEDURE_ENGAGEMENT_SENAT_PROPERTY,
            dateRefusProcedureEngagementSenat
        );
    }

    @Override
    public String getNumeroTexteAdopte() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_TEXTE_ADOPTE_PROPERTY
        );
    }

    @Override
    public void setNumeroTexteAdopte(String numeroTexteAdopte) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_TEXTE_ADOPTE_PROPERTY,
            numeroTexteAdopte
        );
    }

    @Override
    public Calendar getDateAdoption() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_ADOPTION_PROPERTY
        );
    }

    @Override
    public void setDateAdoption(Calendar dateAdoption) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_ADOPTION_PROPERTY,
            dateAdoption
        );
    }

    @Override
    public String getSortAdoption() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_SORT_ADOPTION_PROPERTY
        );
    }

    @Override
    public void setSortAdoption(String sortAdoption) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_SORT_ADOPTION_PROPERTY,
            sortAdoption
        );
    }

    @Override
    public boolean isRedepot() {
        return PropertyUtil.getBooleanProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_REDEPOT_PROPERTY
        );
    }

    @Override
    public void setRedepot(boolean redepot) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_REDEPOT_PROPERTY,
            redepot
        );
    }

    @Override
    public Calendar getDatePromulgation() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_PROMULGATION_PROPERTY
        );
    }

    @Override
    public void setDatePromulgation(Calendar datePromulgation) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_PROMULGATION_PROPERTY,
            datePromulgation
        );
    }

    @Override
    public Calendar getDatePublication() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_PUBLICATION_PROPERTY
        );
    }

    @Override
    public void setDatePublication(Calendar datePublication) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_PUBLICATION_PROPERTY,
            datePublication
        );
    }

    @Override
    public String getNumeroLoi() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_LOI_PROPERTY
        );
    }

    @Override
    public void setNumeroLoi(String numeroLoi) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_LOI_PROPERTY,
            numeroLoi
        );
    }

    @Override
    public Long getNumeroJo() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_JO_PROPERTY
        );
    }

    @Override
    public void setNumeroJo(Long numeroJo) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_JO_PROPERTY,
            numeroJo
        );
    }

    @Override
    public Long getPageJo() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_PAGE_JO_PROPERTY
        );
    }

    @Override
    public void setPageJo(Long pageJo) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_PAGE_JO_PROPERTY,
            pageJo
        );
    }

    @Override
    public Calendar getDateCmp() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_CMP_PROPERTY
        );
    }

    @Override
    public void setDateCmp(Calendar dateCmp) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_CMP_PROPERTY,
            dateCmp
        );
    }

    @Override
    public String getResultatCMP() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_RESULTAT_CMP_PROPERTY
        );
    }

    @Override
    public void setResultatCMP(String resultatCMP) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_RESULTAT_CMP_PROPERTY,
            resultatCMP
        );
    }

    // *************************************************************
    // Données de la catégorie d'événement "organisation des sessions extraordinaires".
    // *************************************************************

    @Override
    public String getTypeActe() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_TYPE_ACTE_PROPERTY
        );
    }

    @Override
    public void setTypeActe(String typeActe) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_TYPE_ACTE_PROPERTY,
            typeActe
        );
    }

    @Override
    public Calendar getDateActe() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_ACTE_PROPERTY
        );
    }

    @Override
    public void setDateActe(Calendar dateActe) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_ACTE_PROPERTY,
            dateActe
        );
    }

    @Override
    public String getNumeroPublication() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_PUBLICATION_PROPERTY
        );
    }

    @Override
    public void setNumeroPublication(String numeroPublication) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_PUBLICATION_PROPERTY,
            numeroPublication
        );
    }

    // *************************************************************
    // Données de la catégorie d'événement "Révision de la constitution".
    // *************************************************************
    @Override
    public List<String> getDossierLegislatif() {
        return PropertyUtil.getStringListProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DOSSIER_LEGISLATIF_PROPERTY
        );
    }

    @Override
    public void setDossierLegislatif(List<String> dossierLegislatif) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DOSSIER_LEGISLATIF_PROPERTY,
            dossierLegislatif
        );
    }

    @Override
    public Calendar getDateConvocation() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_CONVOCATION_PROPERTY
        );
    }

    @Override
    public void setDateConvocation(Calendar dateConvocation) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_CONVOCATION_PROPERTY,
            dateConvocation
        );
    }

    @Override
    public Long getAnneeJo() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_ANNEE_JO_PROPERTY
        );
    }

    @Override
    public void setAnneeJo(Long anneeJo) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_ANNEE_JO_PROPERTY,
            anneeJo
        );
    }

    @Override
    public Calendar getDateJo() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_JO_PROPERTY
        );
    }

    @Override
    public void setDateJo(Calendar dateJo) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_JO_PROPERTY,
            dateJo
        );
    }

    // *************************************************************
    // Données de la catégorie d'événement "Consultation des assemblées sur les projets de nomination".
    // *************************************************************
    @Override
    public String getEcheance() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_ECHEANCE_PROPERTY
        );
    }

    @Override
    public void setEcheance(String echeance) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_ECHEANCE_PROPERTY,
            echeance
        );
    }

    @Override
    public Calendar getDateConsultation() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_CONSULTATION_PROPERTY
        );
    }

    @Override
    public void setDateConsultation(Calendar dateConsultation) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_CONSULTATION_PROPERTY,
            dateConsultation
        );
    }

    @Override
    public Calendar getDateVote() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_VOTE_PROPERTY
        );
    }

    @Override
    public void setDateVote(Calendar dateVote) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_VOTE_PROPERTY,
            dateVote
        );
    }

    @Override
    public String getSensAvis() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_SENS_AVIS_PROPERTY
        );
    }

    @Override
    public void setSensAvis(String sensAvis) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_SENS_AVIS_PROPERTY,
            sensAvis
        );
    }

    @Override
    public Long getSuffrageExprime() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_SUFFRAGE_EXPRIME_PROPERTY
        );
    }

    @Override
    public void setSuffrageExprime(Long suffrageExprime) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_SUFFRAGE_EXPRIME_PROPERTY,
            suffrageExprime
        );
    }

    @Override
    public Long getBulletinBlanc() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_BULLETIN_BLANC_PROPERTY
        );
    }

    @Override
    public void setBulletinBlanc(Long bulletinBlanc) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_BULLETIN_BLANC_PROPERTY,
            bulletinBlanc
        );
    }

    @Override
    public Long getVotePour() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_VOTE_POUR_PROPERTY
        );
    }

    @Override
    public void setVotePour(Long votePour) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_VOTE_POUR_PROPERTY,
            votePour
        );
    }

    @Override
    public Long getVoteContre() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_VOTE_CONTRE_PROPERTY
        );
    }

    @Override
    public void setVoteContre(Long voteContre) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_VOTE_CONTRE_PROPERTY,
            voteContre
        );
    }

    @Override
    public Long getAbstention() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_ABSTENTION_PROPERTY
        );
    }

    @Override
    public void setAbstention(Long abstention) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_ABSTENTION_PROPERTY,
            abstention
        );
    }

    @Override
    public List<String> getCommissions() {
        return PropertyUtil.getStringListProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_COMMISSIONS_PROPERTY
        );
    }

    @Override
    public void setCommissions(List<String> listCommissions) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_COMMISSIONS_PROPERTY,
            listCommissions
        );
    }

    // *************************************************************
    // Données de la catégorie d'événement "Convocation du congres au titre de l'article 18 de la constitution".
    // *************************************************************
    @Override
    public Calendar getDateCongres() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_CONGRES_PROPERTY
        );
    }

    @Override
    public void setDateCongres(Calendar dateCongres) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_CONGRES_PROPERTY,
            dateCongres
        );
    }

    // *************************************************************
    // Données de la catégorie d'événement "Demande de prolongation d'une intervention extérieure".
    // *************************************************************

    // *************************************************************
    // Données de la catégorie d'événement "Résolution de l'article 34-1 de la constitution".
    // *************************************************************
    @Override
    public Calendar getDateDemande() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_DEMANDE_PROPERTY
        );
    }

    @Override
    public void setDateDemande(Calendar dateDemande) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_DEMANDE_PROPERTY,
            dateDemande
        );
    }

    @Override
    public String getMotifIrrecevabilite() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_MOTIF_IRRECEVABILITE_PROPERTY
        );
    }

    @Override
    public void setMotifIrrecevabilite(String motifIrrecevabilite) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_MOTIF_IRRECEVABILITE_PROPERTY,
            motifIrrecevabilite
        );
    }

    // *************************************************************
    // Données de la catégorie d'événement "Dépôt de rapports au parlement".
    // *************************************************************
    @Override
    public String getRapportParlement() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_RAPPORT_PARLEMENT_PROPERTY
        );
    }

    @Override
    public void setRapportParlement(String rapportParlement) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_RAPPORT_PARLEMENT_PROPERTY,
            rapportParlement
        );
    }

    @Override
    public Long getAnneeRapport() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_ANNEE_RAPPORT_PROPERTY
        );
    }

    @Override
    public void setAnneeRapport(Long anneeRapport) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_ANNEE_RAPPORT_PROPERTY,
            anneeRapport
        );
    }

    @Override
    public String getUrlBaseLegale() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_URL_BASE_LEGALE_PROPERTY
        );
    }

    @Override
    public void setUrlBaseLegale(String urlBaseLegale) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_URL_BASE_LEGALE_PROPERTY,
            urlBaseLegale
        );
    }

    @Override
    public String getBaseLegale() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_BASE_LEGALE_PROPERTY
        );
    }

    @Override
    public void setBaseLegale(String BaseLegale) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_BASE_LEGALE_PROPERTY,
            BaseLegale
        );
    }

    // *************************************************************
    // Données de la catégorie d'événement "Insertion d'information parlementaires au JO lois et décrets".
    // *************************************************************
    @Override
    public Long getNumeroRubrique() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_RUBRIQUE_PROPERTY
        );
    }

    @Override
    public void setNumeroRubrique(Long numeroRubrique) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_NUMERO_RUBRIQUE_PROPERTY,
            numeroRubrique
        );
    }

    @Override
    public String getUrlPublication() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_URL_PUBLICATION_PROPERTY
        );
    }

    @Override
    public void setUrlPublication(String urlPublication) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_URL_PUBLICATION_PROPERTY,
            urlPublication
        );
    }

    // *************************************************************
    // Données de la catégorie d'événement "Organismes extra-parlementaires".
    // *************************************************************
    @Override
    public List<String> getParlementaireTitulaireList() {
        return PropertyUtil.getStringListProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_PARLEMENTAIRE_TITULAIRE_LIST_PROPERTY
        );
    }

    @Override
    public void setParlementaireTitulaireList(List<String> parlementaireTitulaireList) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_PARLEMENTAIRE_TITULAIRE_LIST_PROPERTY,
            parlementaireTitulaireList
        );
    }

    @Override
    public List<String> getParlementaireSuppleantList() {
        return PropertyUtil.getStringListProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_PARLEMENTAIRE_SUPPLEANT_LIST_PROPERTY
        );
    }

    @Override
    public void setParlementaireSuppleantList(List<String> parlementaireSuppleantList) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_PARLEMENTAIRE_SUPPLEANT_LIST_PROPERTY,
            parlementaireSuppleantList
        );
    }

    @Override
    public Calendar getDateDesignation() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_DESIGNATION_PROPERTY
        );
    }

    @Override
    public void setDateDesignation(Calendar dateDesignation) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_DESIGNATION_PROPERTY,
            dateDesignation
        );
    }

    // *************************************************************
    // Données de la catégorie d'événement "Autres".
    // *************************************************************
    @Override
    public boolean isPositionAlerte() {
        return PropertyUtil.getBooleanProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_POSITION_ALERTE_PROPERTY
        );
    }

    @Override
    public void setPositionAlerte(boolean positionAlerte) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_POSITION_ALERTE_PROPERTY,
            positionAlerte
        );
    }

    @Override
    public Calendar getDateCaducite() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_CADUCITE_PROPERTY
        );
    }

    @Override
    public void setDateCaducite(Calendar dateCaducite) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_CADUCITE_PROPERTY,
            dateCaducite
        );
    }

    @Override
    public String getDossierCible() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DOSSIER_CIBLE_PROPERTY
        );
    }

    @Override
    public void setDossierCible(String dossierCible) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DOSSIER_CIBLE_PROPERTY,
            dossierCible
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Calendar> getDateList() {
        Object[] calendarArray = (Object[]) document.getProperty(
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_LIST_PROPERTY
        );
        if (calendarArray == null) {
            return new ArrayList<Calendar>();
        }
        return (List<Calendar>) (List<?>) Arrays.asList(calendarArray);
    }

    @Override
    public void setDateList(List<Calendar> dateList) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_LIST_PROPERTY,
            dateList.toArray()
        );
    }

    @Override
    public boolean isRectificatif() {
        return PropertyUtil.getBooleanProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_RECTIFICATIF_PROPERTY
        );
    }

    @Override
    public void setRectificatif(boolean rectificatif) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_RECTIFICATIF_PROPERTY,
            rectificatif
        );
    }

    // *************************************************************
    // Propriétés calculées.
    // *************************************************************
    @Override
    public NumeroVersion getNumeroVersion() {
        return new NumeroVersion(getMajorVersion(), getMinorVersion());
    }

    @Override
    public void setNumeroVersion(NumeroVersion numeroVersion) {
        setMajorVersion(numeroVersion.getMajorVersion());
        setMinorVersion(numeroVersion.getMinorVersion());
    }

    @Override
    public boolean isEtatInit() {
        return SolonEppLifecycleConstant.VERSION_INIT_STATE.equals(document.getCurrentLifeCycleState());
    }

    @Override
    public boolean isEtatBrouillon() {
        return SolonEppLifecycleConstant.VERSION_BROUILLON_STATE.equals(document.getCurrentLifeCycleState());
    }

    @Override
    public boolean isEtatPublie() {
        return SolonEppLifecycleConstant.VERSION_PUBLIE_STATE.equals(document.getCurrentLifeCycleState());
    }

    @Override
    public boolean isEtatAttenteValidation() {
        return SolonEppLifecycleConstant.VERSION_ATTENTE_VALIDATION_STATE.equals(document.getCurrentLifeCycleState());
    }

    @Override
    public boolean isEtatObsolete() {
        return SolonEppLifecycleConstant.VERSION_OBSOLETE_STATE.equals(document.getCurrentLifeCycleState());
    }

    @Override
    public boolean isEtatRejete() {
        return SolonEppLifecycleConstant.VERSION_REJETE_STATE.equals(document.getCurrentLifeCycleState());
    }

    @Override
    public boolean isEtatAbandonne() {
        return SolonEppLifecycleConstant.VERSION_ABANDONNE_STATE.equals(document.getCurrentLifeCycleState());
    }

    @Override
    public String getCommentaire() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.COMMENTAIRE
        );
    }

    @Override
    public void setCommentaire(String commentaire) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.COMMENTAIRE,
            commentaire
        );
    }

    @Override
    public Calendar getDatePresentation() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_PRESENTATION_PROPERTY
        );
    }

    @Override
    public void setDatePresentation(Calendar datePresentation) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_PRESENTATION_PROPERTY,
            datePresentation
        );
    }

    @Override
    public Calendar getDateLettrePm() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_LETTRE_PM_PROPERTY
        );
    }

    @Override
    public void setDateLettrePm(Calendar dateLettrePm) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_LETTRE_PM_PROPERTY,
            dateLettrePm
        );
    }

    @Override
    public List<String> getGroupeParlementaire() {
        return PropertyUtil.getStringListProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_GROUPE_PARLEMENTAIRE
        );
    }

    @Override
    public void setGroupeParlementaire(List<String> groupeParlementaire) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_GROUPE_PARLEMENTAIRE,
            groupeParlementaire
        );
    }

    @Override
    public Calendar getDateDeclaration() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_DECLARATION_PROPERTY
        );
    }

    @Override
    public void setDateDeclaration(Calendar dateDeclaration) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_DECLARATION_PROPERTY,
            dateDeclaration
        );
    }

    @Override
    public boolean getDemandeVote() {
        return PropertyUtil.getBooleanProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DEMANDE_VOTE
        );
    }

    @Override
    public void setDemandeVote(boolean demandeDeVote) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DEMANDE_VOTE,
            demandeDeVote
        );
    }

    @Override
    public String getFonction() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.FONCTION
        );
    }

    @Override
    public void setFonction(String fonction) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.FONCTION,
            fonction
        );
    }

    @Override
    public String getPersonne() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.PERSONNE
        );
    }

    @Override
    public void setPersonne(String personne) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.PERSONNE,
            personne
        );
    }

    @Override
    public Calendar getDateAudition() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_AUDITION_PROPERTY
        );
    }

    @Override
    public void setDateAudition(Calendar dateAudition) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_AUDITION_PROPERTY,
            dateAudition
        );
    }

    @Override
    public String getOrganisme() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_ORGANISME_PROPERTY
        );
    }

    @Override
    public void setOrganisme(String organisme) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_ORGANISME_PROPERTY,
            organisme
        );
    }

    @Override
    public Calendar getDateRefusAssemblee1() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_REFUS_ASSEMBLEE_1
        );
    }

    @Override
    public void setDateRefusASsemblee1(Calendar dateRefusASsemblee1) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_REFUS_ASSEMBLEE_1,
            dateRefusASsemblee1
        );
    }

    @Override
    public Calendar getDateConferencePresidentsAssemblee2() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_CONF_PRES_ASSEMBLEE_2
        );
    }

    @Override
    public void setDateConferencePresidentsAssemblee2(Calendar dateConferencePresidentsAssemblee2) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_CONF_PRES_ASSEMBLEE_2,
            dateConferencePresidentsAssemblee2
        );
    }

    @Override
    public String getDecisionProcAcc() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DECISION_PROC_ACC
        );
    }

    @Override
    public void setDecisionProcAcc(String decisionProcAcc) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DECISION_PROC_ACC,
            decisionProcAcc
        );
    }

    @Override
    public Calendar getDateRefusEngagementProcedure() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_REFUS_ENGAGEMENT_PROCEDURE_ACC
        );
    }

    @Override
    public void setDateRefusEngagementProcedure(Calendar dateRefusEngagementProcedure) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_DATE_REFUS_ENGAGEMENT_PROCEDURE_ACC,
            dateRefusEngagementProcedure
        );
    }

    @Override
    public String getRubrique() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_RUBRIQUE
        );
    }

    @Override
    public void setRubrique(String rubrique) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.VERSION_SCHEMA,
            SolonEppSchemaConstant.VERSION_RUBRIQUE,
            rubrique
        );
    }
}
