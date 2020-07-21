package fr.dila.solonepp.core.domain.dossier;

import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

import java.util.Arrays;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation de l'objet métier dossier SOLON EPP.
 * 
 * @author jtremeaux
 */
public class DossierImpl implements Dossier {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1548969193555310065L;
	/**
     * Modèle de document.
     */
    protected DocumentModel document;

    /**
     * Constructeur de DossierImpl.
     * 
     * @param document Modèle de document
     */
    public DossierImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public DocumentModel getDocument() {
        return document;
    }

    @Override
    public String getTitle() {
    	return DublincoreSchemaUtils.getTitle(document);
    }

    @Override
    public void setTitle(String title) {
    	DublincoreSchemaUtils.setTitle(document, title);
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.ID_DOSSIER, title);
    }

    // *************************************************************
    // Propriétés du document dossier.
    // *************************************************************
    @Override
    public long getAlerteCount() {
        return PropertyUtil.getLongProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_ALERTE_COUNT_PROPERTY);
    }

    @Override
    public void setAlerteCount(long alerteCount) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_ALERTE_COUNT_PROPERTY, alerteCount);
    }

    // *************************************************************
    // Propriétés calculées du dossier.
    // *************************************************************
    @Override
    public boolean isAlerte() {
        return getAlerteCount() > 0;
    }

    @Override
    public Long getNiveauLectureNumero() {
        return PropertyUtil.getLongProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_NIVEAU_LECTURE_NUMERO_PROPERTY);
    }

    @Override
    public void setNiveauLectureNumero(Long niveauLectureNumero) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NIVEAU_LECTURE_NUMERO_PROPERTY,
                niveauLectureNumero);
    }

    @Override
    public String getNiveauLecture() {
        return PropertyUtil
                .getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NIVEAU_LECTURE_PROPERTY);
    }

    @Override
    public void setNiveauLecture(String niveauLecture) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NIVEAU_LECTURE_PROPERTY,
                niveauLecture);
    }

    @Override
    public Calendar getHorodatage() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_HORODATAGE_PROPERTY);
    }

    @Override
    public void setHorodatage(Calendar horodatage) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_HORODATAGE_PROPERTY, horodatage);
    }

    @Override
    public String getObjet() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_OBJET_PROPERTY);
    }

    @Override
    public void setObjet(String objet) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_OBJET_PROPERTY, objet);
    }

    @Override
    public String getIdentifiantMetier() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_IDENTIFIANT_METIER_PROPERTY);
    }

    @Override
    public void setIdentifiantMetier(String identifiantMetier) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_IDENTIFIANT_METIER_PROPERTY, identifiantMetier);
    }
    
    @Override
    public String getSenat() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_SENAT_PROPERTY);
    }

    @Override
    public void setSenat(String senat) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_SENAT_PROPERTY, senat);
    }

    @Override
    public String getNor() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NOR_PROPERTY);
    }

    @Override
    public void setNor(String nor) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NOR_PROPERTY, nor);
    }

    @Override
    public String getNatureLoi() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NATURE_LOI_PROPERTY);
    }

    @Override
    public void setNatureLoi(String natureLoi) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NATURE_LOI_PROPERTY, natureLoi);
    }

    @Override
    public String getTypeLoi() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_TYPE_LOI_PROPERTY);
    }

    @Override
    public void setTypeLoi(String natureLoi) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_TYPE_LOI_PROPERTY, natureLoi);
    }

    @Override
    public String getAuteur() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_AUTEUR_PROPERTY);
    }

    @Override
    public void setAuteur(String auteur) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_AUTEUR_PROPERTY, auteur);
    }

    @Override
    public List<String> getCoauteur() {
        return PropertyUtil.getStringListProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_COAUTEUR_PROPERTY);
    }

    @Override
    public void setCoauteur(List<String> coAuteur) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_COAUTEUR_PROPERTY, coAuteur);
    }

    @Override
    public String getIntitule() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_INTITULE_PROPERTY);
    }

    @Override
    public void setIntitule(String intitule) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_INTITULE_PROPERTY, intitule);
    }

    @Override
    public String getUrlDossierAn() {
        return PropertyUtil
                .getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_URL_DOSSIER_AN_PROPERTY);
    }

    @Override
    public void setUrlDossierAn(String urlDossierAn) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_URL_DOSSIER_AN_PROPERTY,
                urlDossierAn);
    }

    @Override
    public String getUrlDossierSenat() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_URL_DOSSIER_SENAT_PROPERTY);
    }

    @Override
    public void setUrlDossierSenat(String urlDossierSenat) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_URL_DOSSIER_SENAT_PROPERTY,
                urlDossierSenat);
    }

    @Override
    public String getCosignataire() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_COSIGNATAIRE_PROPERTY);
    }

    @Override
    public void setCosignataire(String cosignataire) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_COSIGNATAIRE_PROPERTY, cosignataire);
    }

    @Override
    public Calendar getDateDepotTexte() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATE_DEPOT_TEXTE_PROPERTY);
    }

    @Override
    public void setDateDepotTexte(Calendar dateDepotTexte) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_DEPOT_TEXTE_PROPERTY,
                dateDepotTexte);
    }

    @Override
    public String getNumeroDepotTexte() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_NUMERO_DEPOT_TEXTE_PROPERTY);
    }

    @Override
    public void setNumeroDepotTexte(String numeroDepotTexte) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NUMERO_DEPOT_TEXTE_PROPERTY,
                numeroDepotTexte);
    }

    @Override
    public String getCommissionSaisieAuFond() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_COMMISSION_SAISIE_AU_FOND_PROPERTY);
    }

    @Override
    public void setCommissionSaisieAuFond(String commissionSaisieAuFond) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_COMMISSION_SAISIE_AU_FOND_PROPERTY,
                commissionSaisieAuFond);
    }

    @Override
    public List<String> getCommissionSaisiePourAvis() {
        return PropertyUtil.getStringListProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_COMMISSION_SAISIE_POUR_AVIS_PROPERTY);
    }

    @Override
    public void setCommissionSaisiePourAvis(List<String> commissionSaisiePourAvis) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_COMMISSION_SAISIE_POUR_AVIS_PROPERTY, commissionSaisiePourAvis);
    }

    @Override
    public Calendar getDate() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_PROPERTY);
    }

    @Override
    public void setDate(Calendar date) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_PROPERTY, date);
    }

    @Override
    public Calendar getDateRetrait() {
        return PropertyUtil
                .getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_RETRAIT_PROPERTY);
    }

    @Override
    public void setDateRetrait(Calendar dateRetrait) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_RETRAIT_PROPERTY, dateRetrait);
    }

    @Override
    public Calendar getDateDistributionElectronique() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATE_DISTRIBUTION_ELECTRONIQUE_PROPERTY);
    }

    @Override
    public void setDateDistributionElectronique(Calendar dateDistributionElectronique) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATE_DISTRIBUTION_ELECTRONIQUE_PROPERTY, dateDistributionElectronique);
    }

    @Override
    public List<String> getRapporteurList() {
        return PropertyUtil.getStringListProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_RAPPORTEUR_LIST_PROPERTY);
    }

    @Override
    public void setRapporteurList(List<String> rapportteurList) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_RAPPORTEUR_LIST_PROPERTY,
                rapportteurList);
    }

    @Override
    public String getTitre() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_TITRE_PROPERTY);
    }

    @Override
    public void setTitre(String titre) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_TITRE_PROPERTY, titre);
    }

    @Override
    public Calendar getDateDepotRapport() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATE_DEPOT_RAPPORT_PROPERTY);
    }

    @Override
    public void setDateDepotRapport(Calendar dateDepotRapport) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_DEPOT_RAPPORT_PROPERTY,
                dateDepotRapport);
    }

    @Override
    public String getNumeroDepotRapport() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_NUMERO_DEPOT_RAPPORT_PROPERTY);
    }

    @Override
    public void setNumeroDepotRapport(String numeroDepotRepport) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NUMERO_DEPOT_RAPPORT_PROPERTY,
                numeroDepotRepport);
    }

    @Override
    public String getCommissionSaisie() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_COMMISSION_SAISIE_PROPERTY);
    }

    @Override
    public void setCommissionSaisie(String commissionSaisie) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_COMMISSION_SAISIE_PROPERTY,
                commissionSaisie);
    }

    @Override
    public Calendar getDateRefus() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_REFUS_PROPERTY);
    }

    @Override
    public void setDateRefus(Calendar dateRefus) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_REFUS_PROPERTY, dateRefus);
    }

    @Override
    public List<String> getLibelleAnnexe() {
        return PropertyUtil.getStringListProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_LIBELLE_ANNEXE_PROPERTY);
    }

    @Override
    public void setLibelleAnnexe(List<String> libelleAnnexe) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_LIBELLE_ANNEXE_PROPERTY,
                libelleAnnexe);
    }

    @Override
    public Calendar getDateEngagementProcedure() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATE_ENGAGEMENT_PROCEDURE_PROPERTY);
    }

    @Override
    public void setDateEngagementProcedure(Calendar dateEngagementProcedure) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_ENGAGEMENT_PROCEDURE_PROPERTY,
                dateEngagementProcedure);
    }

    @Override
    public Calendar getDateRefusProcedureEngagementAn() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATE_REFUS_PROCEDURE_ENGAGEMENT_AN_PROPERTY);
    }

    @Override
    public void setDateRefusProcedureEngagementAn(Calendar dateRefusProcedureEngagementAn) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATE_REFUS_PROCEDURE_ENGAGEMENT_AN_PROPERTY, dateRefusProcedureEngagementAn);
    }

    @Override
    public Calendar getDateRefusProcedureEngagementSenat() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATE_REFUS_PROCEDURE_ENGAGEMENT_SENAT_PROPERTY);
    }

    @Override
    public void setDateRefusProcedureEngagementSenat(Calendar dateRefusProcedureEngagementSenat) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATE_REFUS_PROCEDURE_ENGAGEMENT_SENAT_PROPERTY, dateRefusProcedureEngagementSenat);
    }

    @Override
    public String getNumeroTexteAdopte() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_NUMERO_TEXTE_ADOPTE_PROPERTY);
    }

    @Override
    public void setNumeroTexteAdopte(String numeroTexteAdopte) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NUMERO_TEXTE_ADOPTE_PROPERTY,
                numeroTexteAdopte);
    }

    @Override
    public Calendar getDateAdoption() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATE_ADOPTION_PROPERTY);
    }

    @Override
    public void setDateAdoption(Calendar dateAdoption) {
        PropertyUtil
                .setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_ADOPTION_PROPERTY, dateAdoption);
    }

    @Override
    public String getSortAdoption() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_SORT_ADOPTION_PROPERTY);
    }

    @Override
    public void setSortAdoption(String sortAdoption) {
        PropertyUtil
                .setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_SORT_ADOPTION_PROPERTY, sortAdoption);
    }

    @Override
    public boolean isRedepot() {
        return PropertyUtil.getBooleanProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_REDEPOT_PROPERTY);
    }
    
    @Override
    public boolean isPositionAlerte() {
        return PropertyUtil.getBooleanProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_POSITION_ALERTE_PROPERTY);
    }

    @Override
    public void setPositionAlerte(boolean positionAlerte) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_POSITION_ALERTE_PROPERTY, positionAlerte);
    }

    @Override
    public void setRedepot(boolean redepot) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_REDEPOT_PROPERTY, redepot);
    }

    @Override
    public Calendar getDatePromulgation() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATE_PROMULGATION_PROPERTY);
    }

    @Override
    public void setDatePromulgation(Calendar datePromulgation) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_PROMULGATION_PROPERTY,
                datePromulgation);
    }

    @Override
    public Calendar getDatePublication() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATE_PUBLICATION_PROPERTY);
    }

    @Override
    public void setDatePublication(Calendar datePublication) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_PUBLICATION_PROPERTY,
                datePublication);
    }

    @Override
    public Long getNumeroLoi() {
        return PropertyUtil.getLongProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NUMERO_LOI_PROPERTY);
    }

    @Override
    public void setNumeroLoi(Long numeroLoi) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NUMERO_LOI_PROPERTY, numeroLoi);
    }

    @Override
    public Long getNumeroJo() {
        return PropertyUtil.getLongProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NUMERO_JO_PROPERTY);
    }

    @Override
    public void setNumeroJo(Long numeroJo) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_NUMERO_JO_PROPERTY, numeroJo);
    }

    @Override
    public Long getPageJo() {
        return PropertyUtil.getLongProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_PAGE_JO_PROPERTY);
    }

    @Override
    public void setPageJo(Long pageJo) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_PAGE_JO_PROPERTY, pageJo);
    }

    @Override
    public Calendar getDateCmp() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_CMP_PROPERTY);
    }

    @Override
    public void setDateCmp(Calendar dateCmp) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_CMP_PROPERTY, dateCmp);
    }

    @Override
    public List<Calendar> getDateList() {
        Object[] calendarArray = (Object[]) PropertyUtil.getProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATE_LIST_PROPERTY);
        return Arrays.asList((Calendar[]) calendarArray);
    }

    @Override
    public void setDateList(List<Calendar> dateList) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_LIST_PROPERTY,
                dateList.toArray());
    }

    @Override
    public String getEcheance() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_ECHEANCE_PROPERTY);
    }

    @Override
    public void setEcheance(String echeance) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_ECHEANCE_PROPERTY,
                echeance);
    }

    @Override
    public String getTypeActe() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_TYPE_ACTE_PROPERTY);
    }

    @Override
    public void setTypeActe(String typeActe) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_TYPE_ACTE_PROPERTY,
                typeActe);
    }
    
    @Override
    public Calendar getDateActe() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_ACTE_PROPERTY);
    }

    @Override
    public void setDateActe(Calendar dateActe) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_ACTE_PROPERTY, dateActe);
    }

    @Override
    public String getSensAvis() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_SENS_AVIS_PROPERTY);
    }

    @Override
    public void setSensAvis(String sensAvis) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_SENS_AVIS_PROPERTY,
                sensAvis);
    }

    @Override
    public Long getSuffrageExprime() {
        return PropertyUtil.getLongProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_SUFFRAGE_EXPRIME_PROPERTY);
    }

    @Override
    public void setSuffrageExprime(Long suffrageExprime) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_SUFFRAGE_EXPRIME_PROPERTY,
                suffrageExprime);
    }

    @Override
    public Long getBulletinBlanc() {
        return PropertyUtil.getLongProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_BULLETIN_BLANC_PROPERTY);
    }

    @Override
    public void setBulletinBlanc(Long bulletinBlanc) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_BULLETIN_BLANC_PROPERTY,
                bulletinBlanc);
    }

    @Override
    public Long getVotePour() {
        return PropertyUtil.getLongProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_VOTE_POUR_PROPERTY);
    }

    @Override
    public void setVotePour(Long votePour) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_VOTE_POUR_PROPERTY,
                votePour);
    }

    @Override
    public Long getVoteContre() {
        return PropertyUtil.getLongProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_VOTE_CONTRE_PROPERTY);
    }

    @Override
    public void setVoteContre(Long voteContre) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_VOTE_CONTRE_PROPERTY,
                voteContre);
    }

    @Override
    public Long getAbstention() {
        return PropertyUtil.getLongProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_ABSTENTION_PROPERTY);
    }

    @Override
    public void setAbstention(Long abstention) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_ABSTENTION_PROPERTY,
                abstention);
    }

    @Override
    public List<String> getCommissions() {
        return PropertyUtil.getStringListProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_COMMISSIONS_PROPERTY);
    }

    @Override
    public void setCommissions(List<String> commissions) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_COMMISSIONS_PROPERTY,
                commissions);
    }

    @Override
    public void setDocument(DocumentModel document) {
        this.document = document;
    }

    @Override
    public Calendar getDateDemande() {
        return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DATEDEMANDE_PROPERTY);
    }

    @Override
    public void setDateDemande(Calendar dateDemande) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATEDEMANDE_PROPERTY,
                dateDemande);

    }

    @Override
    public void setDossierCible(String dossierCible) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DOSSIERCIBLE_PROPERTY,
                dossierCible);
    }    
    @Override
    public String getDossierCible() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_DOSSIERCIBLE_PROPERTY) ;
        
    }

    @Override
    public String getBaseLegale() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA,
                SolonEppSchemaConstant.DOSSIER_BASELEGALE_PROPERTY) ;
        
    }

    @Override
    public void setBaseLegale(String BaseLegale) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_BASELEGALE_PROPERTY ,
                BaseLegale);
    }
    
    @Override
    public String getOrganisme() {
        return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_ORGANISME_PROPERTY);
    }

    @Override
    public void setOrganisme(String organisme) {
        PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_ORGANISME_PROPERTY, organisme);
    }

	@Override
	public Calendar getDateRefusEngagementProcedure() {
		return PropertyUtil.getCalendarProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_REFUS_ENGAGEMENT_PROCEDURE_PROPERTY);
	}

	@Override
	public void setDateRefusEngagementProcedure(Calendar dateRefusEngagementProcedure) {
		PropertyUtil.setProperty(document, SolonEppSchemaConstant.DOSSIER_SCHEMA, SolonEppSchemaConstant.DOSSIER_DATE_REFUS_ENGAGEMENT_PROCEDURE_PROPERTY, dateRefusEngagementProcedure);
	}
    
}


