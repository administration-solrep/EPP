package fr.dila.solonepp.api.domain.evenement;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface des objets version d'un événement.
 *
 * @author jtremeaux
 */
public interface Version extends Serializable {
    /**
     * Retourne le modèle de document.
     */
    DocumentModel getDocument();

    /**
     * @return modifiedMetaList
     */
    List<String> getModifiedMetaList();

    /**
     * @param modifiedMetaList set modifiedMetaList
     */
    void setModifiedMetaList(List<String> modifiedMetaList);

    // *************************************************************
    // Données de Dublin Core.
    // *************************************************************
    /**
     * Retourne le titre de la version.
     *
     * @return Titre de la version
     */
    String getTitle();

    /**
     * Renseigne le titre de la version.
     *
     * @param title Titre de la version
     */
    void setTitle(String title);

    /**
     * Retourne la description.
     *
     * @return Description
     */
    String getDescription();

    /**
     * Renseigne la description.
     *
     * @param description Description
     */
    void setDescription(String description);

    // *************************************************************
    // Données communes de la version.
    // *************************************************************
    /**
     * Retourne l'identifiant technique de l'événement.
     *
     * @return Identifiant technique de l'événement
     */
    String getEvenement();

    /**
     * Renseigne l'identifiant technique de l'événement.
     *
     * @param evenement Identifiant technique de l'événement
     */
    void setEvenement(String evenement);

    /**
     * Retourne le numéro du niveau de lecture.
     *
     * @return Numéro du niveau de lecture
     */
    Long getNiveauLectureNumero();

    /**
     * Renseigne le numéro du niveau de lecture.
     *
     * @param niveauLectureNumero Numéro du niveau de lecture
     */
    void setNiveauLectureNumero(Long niveauLectureNumero);

    /**
     * Retourne l'identifiant technique du niveau de lecture (vocabulaire).
     *
     * @return Identifiant technique du niveau de lecture (vocabulaire)
     */
    String getNiveauLecture();

    /**
     * Renseigne l'identifiant technique du niveau de lecture (vocabulaire).
     *
     * @param evenement Identifiant technique du niveau de lecture (vocabulaire)
     */
    void setNiveauLecture(String niveauLecture);

    /**
     * Retourne l'horodatage de la publication de la version.
     *
     * @return Horodatage de la publication de la version
     */
    Calendar getHorodatage();

    /**
     * Renseigne l'horodatage de la publication de la version.
     *
     * @param horodatage Horodatage de la publication de la version
     */
    void setHorodatage(Calendar horodatage);

    /**
     * Retourne la date de l'accusé de réception.
     *
     * @return Date de l'accusé de réception
     */
    Calendar getDateAr();

    /**
     * Renseigne la date de l'accusé de réception.
     *
     * @param dateAr Date de l'accusé de réception
     */
    void setDateAr(Calendar dateAr);

    /**
     * Retourne l'objet de l'événement.
     *
     * @return Objet de l'événement
     */
    String getObjet();

    /**
     * Renseigne l'objet de l'événement.
     *
     * @param objet Objet de l'événement
     */
    void setObjet(String objet);

    /**
     * Retourne identifiant Metier
     *
     * @return identifiant Metier
     */
    String getIdentifiantMetier();

    /**
     * Renseigne l'identifiant Metier
     *
     * @param identifiantMetier identifiant Metier
     */
    void setIdentifiantMetier(String identifiantMetier);

    /**
     * Retourne le numéro de version majeur.
     *
     * @return Numéro de version majeur
     */
    Long getMajorVersion();

    /**
     * Renseigne le numéro de version majeur.
     *
     * @param objet Numéro de version majeur
     */
    void setMajorVersion(Long majorVersion);

    /**
     * Retourne le numéro de version mineur.
     *
     * @return Numéro de version mineur
     */
    Long getMinorVersion();

    /**
     * Renseigne le numéro de version mineur.
     *
     * @param objet Numéro de version mineur
     */
    void setMinorVersion(Long minorVersion);

    /**
     * Retourne l'identifiant sénat (chaîne libre renseignable uniquement par le sénat).
     *
     * @return Identifiant sénat (chaîne libre renseignable uniquement par le sénat)
     */
    String getSenat();

    /**
     * Renseigne l'identifiant sénat (chaîne libre renseignable uniquement par le sénat).
     *
     * @param senat Identifiant sénat (chaîne libre renseignable uniquement par le sénat)
     */
    void setSenat(String senat);

    /**
     * Retourne le mode de création de la version brouillon (INIT, POUR_COMPLETION, POUR_RECTIFICATION).
     *
     * @return Mode de création de la version brouillon (INIT, POUR_COMPLETION, POUR_RECTIFICATION)
     */
    String getModeCreation();

    /**
     * Renseigne le mode de création de la version brouillon (INIT, POUR_COMPLETION, POUR_RECTIFICATION).
     *
     * @param modeCreation Mode de création de la version brouillon (INIT, POUR_COMPLETION, POUR_RECTIFICATION)
     */
    void setModeCreation(String modeCreation);

    /**
     * Retourne la présence de pièce jointe.
     *
     * @return Présence de pièce jointe
     */
    boolean isPieceJointePresente();

    /**
     * Renseigne la présence de pièce jointe.
     *
     * @param pieceJointePresente Présence de pièce jointe
     */
    void setPieceJointePresente(boolean pieceJointePresente);

    /**
     * Retourne la nature de la version : ANNULEE, RECTIFIEE, COMPLETEE, ANNULATION_EN_COURS, RECTIFICATION_EN_COURS, ANNULATION_REJETEE, RECTIFICATION_REJETEE, ANNULATION_ABANDONNEE, RECTIFICATION_ABANDONNEE, VERSION_COURANTE
     *
     * @return nature de la version
     */
    String getNature();

    /**
     * Renseigne la nature de la version : ANNULEE, RECTIFIEE, COMPLETEE, ANNULATION_EN_COURS, RECTIFICATION_EN_COURS, ANNULATION_REJETEE, RECTIFICATION_REJETEE, ANNULATION_ABANDONNEE, RECTIFICATION_ABANDONNEE, VERSION_COURANTE
     *
     * @param nature
     */
    void setNature(String nature);

    /**
     * Version courante
     *
     * @return
     */
    boolean isVersionCourante();

    /**
     * Version courante
     *
     * @param versionCourante
     */
    void setVersionCourante(boolean versionCourante);

    // *************************************************************
    // Données de la catégorie d'événement "procédure législative".
    // *************************************************************
    String getNor();

    void setNor(String nor);

    String getNorLoi();

    void setNorLoi(String norLoi);

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

    /**
     * Retourne la date de dépôt du texte.
     *
     * @return Date de dépôt du texte
     */
    Calendar getDateDepotTexte();

    /**
     * Renseigne la date de dépôt du texte.
     *
     * @param dateDepotTexte Date de dépôt du texte
     */
    void setDateDepotTexte(Calendar dateDepotTexte);

    /**
     * Retourne le numéro de dépôt du texte.
     *
     * @return Numéro de dépôt du texte
     */
    String getNumeroDepotTexte();

    /**
     * Renseigne le numéro de dépôt du texte.
     *
     * @param numeroDepotTexte Numéro de dépôt du texte
     */
    void setNumeroDepotTexte(String numeroDepotTexte);

    String getCommissionSaisieAuFond();

    void setCommissionSaisieAuFond(String commissionSaisieAuFond);

    List<String> getCommissionSaisiePourAvis();

    void setCommissionSaisiePourAvis(List<String> commissionSaisiePourAvis);

    Calendar getDate();

    void setDate(Calendar date);

    Calendar getDateSaisine();

    void setDateSaisine(Calendar dateSaisine);

    Calendar getDateRetrait();

    void setDateRetrait(Calendar dateRetrait);

    Calendar getDateDistributionElectronique();

    void setDateDistributionElectronique(Calendar dateDistributionElectronique);

    String getNatureRapport();

    void setNatureRapport(String natureRapport);

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

    String getAttributionCommission();

    void setAttributionCommission(String attributionCommission);

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

    String getNumeroLoi();

    void setNumeroLoi(String numeroLoi);

    Long getNumeroJo();

    void setNumeroJo(Long numeroJo);

    Long getPageJo();

    void setPageJo(Long pageJo);

    Calendar getDateCmp();

    void setDateCmp(Calendar dateCmp);

    String getResultatCMP();

    void setResultatCMP(String resultatCMP);

    /**
     * Champ LEX40 : date de refus procédure accelerée première assemblée
     * @return
     */
    Calendar getDateRefusAssemblee1();

    /**
     * Champ LEX40 : date de refus procédure accelerée première assemblée
     */
    void setDateRefusASsemblee1(Calendar dateRefusASsemblee1);

    /**
     * Champ LEX40 : date de conférence des présidents seconde assemblée
     * @return
     */
    Calendar getDateConferencePresidentsAssemblee2();

    /**
     * Champ LEX40 : date de conférence des présidents seconde assemblée
     */
    void setDateConferencePresidentsAssemblee2(Calendar dateConferencePresidentsAssemblee2);

    /**
     * Champ LEX40 : décision procédure accelérée
     * @return
     */
    String getDecisionProcAcc();

    /**
     * Champ LEX40 : décision procédure accelérée
     * @param decisionProcAcc
     */
    void setDecisionProcAcc(String decisionProcAcc);

    // *************************************************************
    // Données de la catégorie d'événement "organisation des sessions extraordinaires".
    // *************************************************************

    String getTypeActe();

    void setTypeActe(String typeActe);

    Calendar getDateActe();

    void setDateActe(Calendar dateActe);

    String getNumeroPublication();

    void setNumeroPublication(String numeroPublication);

    // *************************************************************
    // Données de la catégorie d'événement "Révision de la constitution".
    // *************************************************************
    List<String> getDossierLegislatif();

    void setDossierLegislatif(List<String> dossierLegislatif);

    Calendar getDateConvocation();

    void setDateConvocation(Calendar dateConvocation);

    Long getAnneeJo();

    void setAnneeJo(Long anneeJo);

    Calendar getDateJo();

    void setDateJo(Calendar dateJo);

    // *************************************************************
    // Données de la catégorie d'événement "Consultation des assemblées sur les projets de nomination".
    // *************************************************************
    String getEcheance();

    void setEcheance(String echeance);

    Calendar getDateConsultation();

    void setDateConsultation(Calendar dateConsultation);

    Calendar getDateVote();

    void setDateVote(Calendar dateVote);

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

    void setCommissions(List<String> listCommissions);

    List<String> getGroupeParlementaire();

    void setGroupeParlementaire(List<String> groupeParlementaire);

    // *************************************************************
    // Données de la catégorie d'événement "Convocation du congres au titre de l'article 18 de la constitution".
    // *************************************************************
    Calendar getDateCongres();

    void setDateCongres(Calendar dateCongres);

    // *************************************************************
    // Données de la catégorie d'événement "Demande de prolongation d'une intervention extérieure".
    // *************************************************************

    // *************************************************************
    // Données de la catégorie d'événement "Résolution de l'article 34-1 de la constitution".
    // *************************************************************
    Calendar getDateDemande();

    void setDateDemande(Calendar dateDemande);

    String getMotifIrrecevabilite();

    void setMotifIrrecevabilite(String motifIrrecevabilite);

    // *************************************************************
    // Données de la catégorie d'événement "Dépôt de rapports au parlement".
    // *************************************************************
    String getRapportParlement();

    void setRapportParlement(String rapportParlement);

    Long getAnneeRapport();

    void setAnneeRapport(Long anneeRapport);

    String getUrlBaseLegale();

    void setUrlBaseLegale(String urlBaseLegale);

    String getBaseLegale();

    void setBaseLegale(String BaseLegale);

    String getFonction();

    void setFonction(String fonction);

    String getPersonne();

    void setPersonne(String personne);

    // *************************************************************
    // Données de la catégorie d'événement "Insertion d'information parlementaires au JO lois et décrets".
    // *************************************************************
    Long getNumeroRubrique();

    void setNumeroRubrique(Long numeroRubrique);

    String getRubrique();

    void setRubrique(String rubrique);

    String getUrlPublication();

    void setUrlPublication(String urlPublication);

    // *************************************************************
    // Données de la catégorie d'événement "Organismes extra-parlementaires".
    // *************************************************************
    List<String> getParlementaireTitulaireList();

    void setParlementaireTitulaireList(List<String> parlementaireTitulaireList);

    List<String> getParlementaireSuppleantList();

    void setParlementaireSuppleantList(List<String> parlementaireSuppleantList);

    Calendar getDateDesignation();

    void setDateDesignation(Calendar dateDesignation);

    // *************************************************************
    // Données de la catégorie d'événement "Autres".
    // *************************************************************
    /**
     * Retourne la position de l'alerte (vrai : posée, faux : levée).
     *
     * @return Position de l'alerte.
     */
    boolean isPositionAlerte();

    /**
     * Renseigne la position de l'alerte (vrai : posée, faux : levée).
     *
     * @param positionAlerte Position de l'alerte.
     */
    void setPositionAlerte(boolean positionAlerte);

    Calendar getDateCaducite();

    void setDateCaducite(Calendar dateCaducite);

    String getDossierCible();

    void setDossierCible(String dossierCible);

    List<Calendar> getDateList();

    void setDateList(List<Calendar> dateList);

    boolean isRectificatif();

    void setRectificatif(boolean rectificatif);

    // *************************************************************
    // Propriétés calculées.
    // *************************************************************
    /**
     * Renseigne le numéro de version.
     *
     * @return Numéro de version
     */
    NumeroVersion getNumeroVersion();

    /**
     * Renseigne le numéro de version.
     *
     * @param numeroVersion Numéro de version
     */
    void setNumeroVersion(NumeroVersion numeroVersion);

    // *************************************************************
    // Propriétés calculées sur l'état du cycle de vie.
    // *************************************************************
    /**
     * Retourne vrai si la version est à l'état du cycle de vie init.
     *
     * @return État du cycle de vie init
     */
    boolean isEtatInit();

    /**
     * Retourne vrai si la version est à l'état du cycle de vie brouillon.
     *
     * @return État du cycle de vie brouillon
     */
    boolean isEtatBrouillon();

    /**
     * Retourne vrai si la version est à l'état du cycle de vie publié.
     *
     * @return État du cycle de vie publié
     */
    boolean isEtatPublie();

    /**
     * Retourne vrai si la version est à l'état du cycle de vie en attente de validation.
     *
     * @return État du cycle de vie en attente de validation
     */
    boolean isEtatAttenteValidation();

    /**
     * Retourne vrai si la version est à l'état du cycle de vie obsolète.
     *
     * @return État du cycle de vie obsolète
     */
    boolean isEtatObsolete();

    /**
     * Retourne vrai si la version est à l'état du cycle de vie rejeté.
     *
     * @return État du cycle de vie rejeté
     */
    boolean isEtatRejete();

    /**
     * Retourne vrai si la version est à l'état du cycle de vie abandonné.
     *
     * @return État du cycle de vie abandonné
     */
    boolean isEtatAbandonne();

    String getCommentaire();

    void setCommentaire(String commentaire);

    /**
     * Retourne la date de la présentation.
     *
     * @return Date de la présentation
     */
    Calendar getDatePresentation();

    /**
     * Renseigne la date de la présentation.
     *
     * @param datePresentation Date de de la présentation
     */
    void setDatePresentation(Calendar datePresentation);

    /**
     * Retourne la date la lettre du premier ministere.
     *
     * @return Date la lettre du premier ministere
     */
    Calendar getDateLettrePm();

    /**
     * Renseigne la date la lettre du premier ministere.
     *
     * @param dateLettrePm la date la lettre du premier ministere
     */
    void setDateLettrePm(Calendar dateLettrePm);

    /**
     * Retourne la date declaration.
     *
     * @return Date declaration
     */
    Calendar getDateDeclaration();

    /**
     * Renseigne la date de declaration.
     *
     * @param dateDeclaration declaration
     */
    void setDateDeclaration(Calendar dateDeclaration);

    /**
     * @return demande de vote
     */
    boolean getDemandeVote();

    /**
     * @param demandeDeVote
     */
    void setDemandeVote(boolean demandeDeVote);

    Calendar getDateAudition();

    void setDateAudition(Calendar dateActe);

    String getOrganisme();

    void setOrganisme(String organisme);
}
