package fr.dila.solonepp.api.service.rechercherMessage;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.DocumentModel;

public class RechercherMessageDTO {

    
    // Message 
    private DocumentModel messageDoc;
    
    // Evenement
    
    private String idEvenement;
    
    private String etatEvenement;
    
    private String typeEvenement;
    
    private String emetteur;
    
    private String destinataire;
    
    private String destinataireCopie;
    
    
    // Dossier
    private String idDossier;
    
    private Calendar dateDepotTexte;
    
    private String numeroDepotTexte;
    
    private Long alerteDossier;
    
    
    // Version
    private String idSenat;
    
    private Calendar horodatage;
    
    private String objet;
    
    private Long niveauLectureNumero;
    
    private String niveauLecture;
    
    private boolean pieceJointePresente;

	private String rubrique;

	private String commentaire;

    
    /**
     * @return the idEvenement
     */
    public String getIdEvenement() {
        return idEvenement;
    }

    /**
     * @param idEvenement the idEvenement to set
     */
    public void setIdEvenement(String idEvenement) {
        this.idEvenement = idEvenement;
    }

    /**
     * @return the etatEvenement
     */
    public String getEtatEvenement() {
        return etatEvenement;
    }

    /**
     * @param etatEvenement the etatEvenement to set
     */
    public void setEtatEvenement(String etatEvenement) {
        this.etatEvenement = etatEvenement;
    }

    /**
     * @return the typeEvenement
     */
    public String getTypeEvenement() {
        return typeEvenement;
    }

    /**
     * @param typeEvenement the typeEvenement to set
     */
    public void setTypeEvenement(String typeEvenement) {
        this.typeEvenement = typeEvenement;
    }

    /**
     * @return the emetteur
     */
    public String getEmetteur() {
        return emetteur;
    }

    /**
     * @param emetteur the emetteur to set
     */
    public void setEmetteur(String emetteur) {
        this.emetteur = emetteur;
    }

    /**
     * @return the destinataire
     */
    public String getDestinataire() {
        return destinataire;
    }

    /**
     * @param destinataire the destinataire to set
     */
    public void setDestinataire(String destinataire) {
        this.destinataire = destinataire;
    }

    /**
     * @return the destinataireCopie
     */
    public String getDestinataireCopie() {
        return destinataireCopie;
    }

    /**
     * @param destinataireCopie the destinataireCopie to set
     */
    public void setDestinataireCopie(String destinataireCopie) {
        this.destinataireCopie = destinataireCopie;
    }

    /**
     * @return the idDossier
     */
    public String getIdDossier() {
        return idDossier;
    }

    /**
     * @param idDossier the idDossier to set
     */
    public void setIdDossier(String idDossier) {
        this.idDossier = idDossier;
    }

    /**
     * @return the dateDepotTexte
     */
    public Calendar getDateDepotTexte() {
        return dateDepotTexte;
    }

    /**
     * @param dateDepotTexte the dateDepotTexte to set
     */
    public void setDateDepotTexte(Calendar dateDepotTexte) {
        this.dateDepotTexte = dateDepotTexte;
    }

    /**
     * @return the numeroDepotTexte
     */
    public String getNumeroDepotTexte() {
        return numeroDepotTexte;
    }

    /**
     * @param numeroDepotTexte the numeroDepotTexte to set
     */
    public void setNumeroDepotTexte(String numeroDepotTexte) {
        this.numeroDepotTexte = numeroDepotTexte;
    }

    /**
     * @return the alerteDossier
     */
    public Long getAlerteDossier() {
        return alerteDossier;
    }
    
    /**
     * @param alerteDossier the alerteDossier to set
     */
    public void setAlerteDossier(Long alerteDossier) {
        this.alerteDossier = alerteDossier;
    }

    /**
     * @return the idSenat
     */
    public String getIdSenat() {
        return idSenat;
    }

    /**
     * @param idSenat the idSenat to set
     */
    public void setIdSenat(String idSenat) {
        this.idSenat = idSenat;
    }

    /**
     * @return the horodatage
     */
    public Calendar getHorodatage() {
        return horodatage;
    }

    /**
     * @param horodatage the horodatage to set
     */
    public void setHorodatage(Calendar horodatage) {
        this.horodatage = horodatage;
    }

    /**
     * @return the objet
     */
    public String getObjet() {
        return objet;
    }

    /**
     * @param objet the objet to set
     */
    public void setObjet(String objet) {
        this.objet = objet;
    }

    /**
     * @return the niveauLectureNumero
     */
    public Long getNiveauLectureNumero() {
        return niveauLectureNumero;
    }

    /**
     * @param niveauLectureNumero the niveauLectureNumero to set
     */
    public void setNiveauLectureNumero(Long niveauLectureNumero) {
        this.niveauLectureNumero = niveauLectureNumero;
    }

    /**
     * @return the niveauLecture
     */
    public String getNiveauLecture() {
        return niveauLecture;
    }

    /**
     * @param niveauLecture the niveauLecture to set
     */
    public void setNiveauLecture(String niveauLecture) {
        this.niveauLecture = niveauLecture;
    }

    /**
     * @return the messageDoc
     */
    public DocumentModel getMessageDoc() {
        return messageDoc;
    }

    /**
     * @param messageDoc the messageDoc to set
     */
    public void setMessageDoc(DocumentModel messageDoc) {
        this.messageDoc = messageDoc;
    }

    /**
     * @return the pieceJointePresente
     */
    public boolean isPieceJointePresente() {
        return pieceJointePresente;
    }

    /**
     * @param pieceJointePresente the pieceJointePresente to set
     */
    public void setPieceJointePresente(boolean pieceJointePresente) {
        this.pieceJointePresente = pieceJointePresente;
    }
    
	/**
	 * @return the rubrique
	 */
	public String getRubrique() {
		return rubrique;
	}

	/**
	 * @param rubrique the rubrique to set
	 */
	public void setRubrique(String rubrique) {
		this.rubrique = rubrique;
	}

	/**
	 * @return the commentaire
	 */
	public String getCommentaire() {
		return commentaire;
	}

	/**
	 * @param commentaire the commentaire to set
	 */
	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}
    
}
