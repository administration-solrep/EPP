package fr.dila.solonepp.api.domain.dossier;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface des objets métiers dossier SOLON EPP.
 * 
 * @author jtremeaux
 */
public interface Dossier extends Serializable {
    /**
     * Retourne le modèle de document.
     */
    DocumentModel getDocument();

    /**
     * Retourne le titre du dossier.
     * 
     * @return Titre du dossier
     */
    String getTitle();

    /**
     * Renseigne le titre du dossier.
     * 
     * @param title Titre du dossier
     */
    void setTitle(String title);

    // *************************************************************
    // Propriétés du document dossier.
    // *************************************************************
    /**
     * Retourne le nombre d'alertes posées sur le dossier, si supérieur à 0 le dossier est "en alerte".
     * 
     * @return Nombre d'alertes posées sur le dossier
     */
    long getAlerteCount();

    /**
     * Renseigne le nombre d'alertes posées sur le dossier, si supérieur à 0 le dossier est "en alerte".
     * 
     * @param alerteCount Nombre d'alertes posées sur le dossier
     */
    void setAlerteCount(long alerteCount);

    // *************************************************************
    // Propriétés calculées du dossier.
    // *************************************************************
    /**
     * Retourne vrai si le dossier est en alerte.
     * 
     * @return Vrai si le dossier est en alerte
     */
    boolean isAlerte();

    // *************************************************************
    // Propriétés dénormalisées pour la fiche dossier.
    // *************************************************************

    Long getNiveauLectureNumero();

    void setNiveauLectureNumero(Long niveauLectureNumero);

    String getNiveauLecture();

    void setNiveauLecture(String niveauLecture);

    Calendar getHorodatage();

    void setHorodatage(Calendar horodatage);

    String getObjet();

    void setObjet(String objet);

    String getIdentifiantMetier();

    void setIdentifiantMetier(String identifiantMetier);

    String getSenat();

    void setSenat(String senat);

    String getNor();

    void setNor(String nor);

    String getNatureLoi();

    void setTypeLoi(String natureLoi);

    String getTypeLoi();

    void setNatureLoi(String natureLoi);

    String getAuteur();

    void setAuteur(String auteur);

    List<String> getCoauteur();

    void setCoauteur(List<String> coAuteur);

    String getIntitule();

    void setIntitule(String intitule);

    String getUrlDossierAn();

    void setUrlDossierAn(String urlDossierAn);

    String getUrlDossierSenat();

    void setUrlDossierSenat(String urlDossierSenat);

    String getCosignataire();

    void setCosignataire(String cosignataire);

    Calendar getDateDepotTexte();

    void setDateDepotTexte(Calendar dateDepotTexte);

    String getNumeroDepotTexte();

    void setNumeroDepotTexte(String numeroDepotTexte);

    String getCommissionSaisieAuFond();

    void setCommissionSaisieAuFond(String commissionSaisieAuFond);

    List<String> getCommissionSaisiePourAvis();

    void setCommissionSaisiePourAvis(List<String> commissionSaisiePourAvis);

    Calendar getDate();

    void setDate(Calendar date);

    Calendar getDateRetrait();

    void setDateRetrait(Calendar dateRetrait);

    Calendar getDateDistributionElectronique();

    void setDateDistributionElectronique(Calendar dateDistributionElectronique);

    List<String> getRapporteurList();

    void setRapporteurList(List<String> rapportteurList);

    String getTitre();

    void setTitre(String titre);

    Calendar getDateDepotRapport();

    void setDateDepotRapport(Calendar dateDepotRapport);

    String getNumeroDepotRapport();

    void setNumeroDepotRapport(String numeroDepotRepport);

    String getCommissionSaisie();

    void setCommissionSaisie(String commissionSaisie);

    Calendar getDateRefus();

    void setDateRefus(Calendar dateRefus);

    List<String> getLibelleAnnexe();

    void setLibelleAnnexe(List<String> libelleAnnexe);

    Calendar getDateEngagementProcedure();

    void setDateEngagementProcedure(Calendar dateEngagementProcedure);

    Calendar getDateRefusProcedureEngagementAn();

    void setDateRefusProcedureEngagementAn(Calendar dateRefusProcedureEngagementAn);

    Calendar getDateRefusProcedureEngagementSenat();

    void setDateRefusProcedureEngagementSenat(Calendar dateRefusProcedureEngagementSenat);
    
    Calendar getDateRefusEngagementProcedure();

    void setDateRefusEngagementProcedure(Calendar dateRefusEngagementProcedure);

    String getNumeroTexteAdopte();

    void setNumeroTexteAdopte(String numeroTexteAdopte);

    Calendar getDateAdoption();

    void setDateAdoption(Calendar dateAdoption);

    String getSortAdoption();

    void setSortAdoption(String sortAdoption);

    boolean isRedepot();

    void setRedepot(boolean redepot);

    Calendar getDatePromulgation();

    void setDatePromulgation(Calendar datePromulgation);

    Calendar getDatePublication();

    void setDatePublication(Calendar datePublication);

    Long getNumeroLoi();

    void setNumeroLoi(Long numeroLoi);

    Long getNumeroJo();

    void setNumeroJo(Long numeroJo);

    Long getPageJo();

    void setPageJo(Long pageJo);

    Calendar getDateCmp();

    void setDateCmp(Calendar dateCmp);

    List<Calendar> getDateList();

    void setDateList(List<Calendar> dateList);

    String getEcheance();

    void setEcheance(String echeance);

    Calendar getDateActe();

    void setDateActe(Calendar dateActe);

    String getSensAvis();

    void setSensAvis(String sensAvis);

    Long getSuffrageExprime();

    void setSuffrageExprime(Long suffrageExprime);

    Long getBulletinBlanc();

    void setBulletinBlanc(Long bulletinBlanc);

    Long getVotePour();

    void setVotePour(Long votePour);

    Long getVoteContre();

    void setVoteContre(Long voteContre);

    Long getAbstention();

    void setAbstention(Long abstention);

    List<String> getCommissions();

    void setCommissions(List<String> commissions);

    void setDocument(DocumentModel document);

    String getTypeActe();

    void setTypeActe(String typeActe);

    Calendar getDateDemande();

    void setDateDemande(Calendar dateDemande);

    String getDossierCible();

    void setDossierCible(String dossierCible);

    String getBaseLegale();

    void setBaseLegale(String BaseLegale);

    String getOrganisme();

    void setOrganisme(String organisme);

    public boolean isPositionAlerte();

    void setPositionAlerte(boolean positionAlerte);

}
