package fr.dila.solonepp.api.dao.criteria;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.nuxeo.common.utils.StringUtils;

/**
 * Critères de recherche des messages.
 * 
 * @author jtremeaux
 */
public class MessageCriteria {
    // *************************************************************
    // Critères de recherche sur les messages
    // *************************************************************
    
    /**
     * force l'ajout de la jointure avec Evenement pour ajouter les champs de tri à la requète 
     */
    private boolean joinEvenementForSorting = false;
    
    /**
     * force l'ajout de la jointure avec Dossier pour ajouter les champs de tri à la requète 
     */
    private boolean joinDossierForSorting = false;
    
    /**
     * force l'ajout de la jointure avec Version pour ajouter les champs de tri à la requète 
     */
    private boolean joinVersionForSorting = false;
    
    /**
     * UUID de l'événement.
     */
    private String caseDocumentId;
    
    /**
     * Identifiant technique (titre) de l'événement.
     */
    private String evenementId;
    
    /**
     * État en cours du cycle de vie du message.
     */
    private String currentLifeCycleState;

    /**
     * Type de message (EMETTEUR, DESTINATAIRE, COPIE).
     */
    private String messageType;
    
    /**
     * Type de message (EMETTEUR, DESTINATAIRE, COPIE), multivalué.
     */
    private List<String> messageTypeIn;
    
    /**
     * Identifiant technique de la corbeille.
     */
    private String corbeille;
    
    /**
     * Recherche uniquement les messages nécessitant un AR.
     */
    private Boolean arNecessaire;
    
    /**
     * Recherche uniquement les messages ayant reçu / non reçu un AR.
     */
    private Boolean arDonne;

    /**
     * Vérifie la permission de lecture sur le message.
     */
    private boolean checkReadPermission;

    /**
     * Tri par horodatage
     */
    private boolean orderByHorodatage;
    
    /**
     * Liste des tris
     */
    private List<OrderByCriteria> orderByList;
    
    /**
     * date de traitement
     */
    private Calendar dateTraitementMin;
    
    /**
     * état de message exclus des resultats
     */
    private List<String> etatMessageExclus;
    
    /**
     * états de message des resultats
     */
    private List<String> etatMessageIn;
    
    /**
     * état de message des resultats
     */
    private String etatMessage;
    
    /**
     * Ajoute la condition sur le type des messages et la date traitement
     */
    private Boolean corbeilleMessageType;
    
    // *************************************************************
    // Critères de recherche sur les dossiers
    // *************************************************************
    /**
     * Dossier en alerte.
     */
    private Boolean dossierAlerte;

    // *************************************************************
    // Critères de recherche sur les événements
    // *************************************************************
    /**
     * État en cours du cycle de vie de l'événement.
     */
    private String evenementCurrentLifeCycleState;

    /**
     * Identifiant technique (titre) du dossier.
     */
    private String dossierId;
    
    /**
     * Type d'événement.
     */
    private String evenementType;
    
    /**
     * Type d'événement (multivalué).
     */
    private List<String> evenementTypeIn;
    
    /**
     * Émetteur de l'événement.
     */
    private String evenementEmetteur;
    
    /**
     * Émetteur de l'événement (multivalué).
     */
    private List<String> evenementEmetteurIn;
    
    /**
     * Destinataire de l'événement.
     */
    private String evenementDestinataire;
    
    /**
     * Destinataire de l'événement (multivalué).
     */
    private List<String> evenementDestinataireIn;
    
    /**
     * Destinataire en copie de l'événement.
     */
    private String evenementDestinataireCopie;

    /**
     * Destinataire en copie de l'événement (multivalué).
     */
    private List<String> evenementDestinataireCopieIn;

    // *************************************************************
    // Critères de recherche sur les versions
    // *************************************************************
    /**
     * Identitiant Sénat.
     */
    private String versionSenat;

    /**
     * Objet de la version.
     */
    private String versionObjetLike;

    /**
     * Date d'horodatage de la version (min).
     */
    private Calendar versionHorodatageMin;

    /**
     * Date d'horodatage de la version (max).
     */
    private Calendar versionHorodatageMax;

    /**
     * Numéro du niveau de lecture.
     */
    private Long versionNiveauLectureNumero;

    /**
     * Identifiant technique du niveau de lecture.
     */
    private String versionNiveauLecture;

    /**
     * Présence de pièce jointe.
     */
    private Boolean versionPieceJointePresente;

    /**
     * Numéro de dépot du texte.
     */
    private String versionNumeroDepotTexte;
    
    /**
     * Numéro de dépot du texte de la fiche dossier.
     */
    private String dossierNumeroDepotTexte;

    /**
     * Critères dynamiques sur les métadonnées.
     */
    private Map<String, Object> metadonneeCriteria;

    // *************************************************************
    // Critères de recherche les fichiers de pièces jointes
    // *************************************************************
    /**
     * Numéro de dépot du texte.
     */
    private String pieceJointeFichierFulltext;

    /**
     * Getter de currentLifeCycleState.
     *
     * @return currentLifeCycleState
     */
    public String getCurrentLifeCycleState() {
        return currentLifeCycleState;
    }

    /**
     * Setter de currentLifeCycleState.
     *
     * @param currentLifeCycleState currentLifeCycleState
     */
    public void setCurrentLifeCycleState(String currentLifeCycleState) {
        this.currentLifeCycleState = currentLifeCycleState;
    }

    /**
     * Getter de caseDocumentId.
     *
     * @return caseDocumentId
     */
    public String getCaseDocumentId() {
        return caseDocumentId;
    }

    /**
     * Setter de caseDocumentId.
     *
     * @param caseDocumentId caseDocumentId
     */
    public void setCaseDocumentId(String caseDocumentId) {
        this.caseDocumentId = caseDocumentId;
    }

    /**
     * Getter de evenementId.
     *
     * @return evenementId
     */
    public String getEvenementId() {
        return evenementId;
    }

    /**
     * Setter de evenementId.
     *
     * @param evenementId evenementId
     */
    public void setEvenementId(String evenementId) {
        this.evenementId = evenementId;
    }

    /**
     * Getter de messageType.
     *
     * @return messageType
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * Setter de messageType.
     *
     * @param messageType messageType
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    /**
     * Getter de messageTypeIn.
     *
     * @return messageTypeIn
     */
    public List<String> getMessageTypeIn() {
        return messageTypeIn;
    }

    /**
     * Setter de messageTypeIn.
     *
     * @param messageTypeIn messageTypeIn
     */
    public void setMessageTypeIn(List<String> messageTypeIn) {
        this.messageTypeIn = messageTypeIn;
    }

    /**
     * Getter de checkReadPermission.
     *
     * @return checkReadPermission
     */
    public boolean isCheckReadPermission() {
        return checkReadPermission;
    }

    /**
     * Setter de checkReadPermission.
     *
     * @param checkReadPermission checkReadPermission
     */
    public void setCheckReadPermission(boolean checkReadPermission) {
        this.checkReadPermission = checkReadPermission;
    }
    
    /**
     * Getter de dossierAlerte.
     *
     * @return dossierAlerte
     */
    public Boolean getDossierAlerte() {
        return dossierAlerte;
    }

    /**
     * Setter de dossierAlerte.
     *
     * @param dossierAlerte dossierAlerte
     */
    public void setDossierAlerte(Boolean dossierAlerte) {
        this.dossierAlerte = dossierAlerte;
    }

    /**
     * Getter de corbeille.
     *
     * @return corbeille
     */
    public String getCorbeille() {
        return corbeille;
    }

    /**
     * Setter de corbeille.
     *
     * @param corbeille corbeille
     */
    public void setCorbeille(String corbeille) {
        this.corbeille = corbeille;
    }

    /**
     * Getter de arNecessaire.
     *
     * @return arNecessaire
     */
    public Boolean getArNecessaire() {
        return arNecessaire;
    }

    /**
     * Setter de arNecessaire.
     *
     * @param arNecessaire arNecessaire
     */
    public void setArNecessaire(Boolean arNecessaire) {
        this.arNecessaire = arNecessaire;
    }

    /**
     * Getter de arDonne.
     *
     * @return arDonne
     */
    public Boolean getArDonne() {
        return arDonne;
    }

    /**
     * Setter de arDonne.
     *
     * @param arDonne arDonne
     */
    public void setArDonne(Boolean arDonne) {
        this.arDonne = arDonne;
    }

    /**
     * Getter de dossierId.
     *
     * @return dossierId
     */
    public String getDossierId() {
        return dossierId;
    }

    /**
     * Setter de dossierId.
     *
     * @param dossierId dossierId
     */
    public void setDossierId(String dossierId) {
        this.dossierId = dossierId;
    }

    /**
     * Getter de evenementType.
     *
     * @return evenementType
     */
    public String getEvenementType() {
        return evenementType;
    }

    /**
     * Setter de evenementType.
     *
     * @param evenementType evenementType
     */
    public void setEvenementType(String evenementType) {
        this.evenementType = evenementType;
    }

    /**
     * Getter de evenementTypeIn.
     *
     * @return evenementTypeIn
     */
    public List<String> getEvenementTypeIn() {
        return evenementTypeIn;
    }

    /**
     * Setter de evenementTypeIn.
     *
     * @param evenementTypeIn evenementTypeIn
     */
    public void setEvenementTypeIn(List<String> evenementTypeIn) {
        this.evenementTypeIn = evenementTypeIn;
    }

    /**
     * Getter de evenementCurrentLifeCycleState.
     *
     * @return evenementCurrentLifeCycleState
     */
    public String getEvenementCurrentLifeCycleState() {
        return evenementCurrentLifeCycleState;
    }

    /**
     * Setter de evenementCurrentLifeCycleState.
     *
     * @param evenementCurrentLifeCycleState evenementCurrentLifeCycleState
     */
    public void setEvenementCurrentLifeCycleState(String evenementCurrentLifeCycleState) {
        this.evenementCurrentLifeCycleState = evenementCurrentLifeCycleState;
    }

    /**
     * Getter de evenementEmetteur.
     *
     * @return evenementEmetteur
     */
    public String getEvenementEmetteur() {
        return evenementEmetteur;
    }

    /**
     * Setter de evenementEmetteur.
     *
     * @param evenementEmetteur evenementEmetteur
     */
    public void setEvenementEmetteur(String evenementEmetteur) {
        this.evenementEmetteur = evenementEmetteur;
    }

    /**
     * Getter de evenementDestinataire.
     *
     * @return evenementDestinataire
     */
    public String getEvenementDestinataire() {
        return evenementDestinataire;
    }

    /**
     * Setter de evenementDestinataire.
     *
     * @param evenementDestinataire evenementDestinataire
     */
    public void setEvenementDestinataire(String evenementDestinataire) {
        this.evenementDestinataire = evenementDestinataire;
    }

    /**
     * Getter de evenementDestinataireCopie.
     *
     * @return evenementDestinataireCopie
     */
    public String getEvenementDestinataireCopie() {
        return evenementDestinataireCopie;
    }

    /**
     * Setter de evenementDestinataireCopie.
     *
     * @param evenementDestinataireCopie evenementDestinataireCopie
     */
    public void setEvenementDestinataireCopie(String evenementDestinataireCopie) {
        this.evenementDestinataireCopie = evenementDestinataireCopie;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MessageCriteria: ")
            .append("{caseDocumentId: ").append(caseDocumentId)
            .append(", evenementId: ").append(evenementId)
            .append(", messageType: ").append(messageType)
            .append(", messageTypeIn: ").append(messageTypeIn == null ? "null" : StringUtils.join(messageTypeIn, ", "))
            .append(", corbeille: ").append(corbeille)
            .append(", checkReadPermission: ").append(checkReadPermission)
            .append(", dossierAlerte: ").append(dossierAlerte)
            .append(", dossierId: ").append(dossierId)
            .append(", evenementType: ").append(evenementType)
            .append(", evenementEmetteur: ").append(evenementEmetteur)
            .append(", evenementDestinataire: ").append(evenementDestinataire)
            .append(", evenementDestinataireCopie: ").append(evenementDestinataireCopie)
            .append("}");
        return sb.toString();
    }

    /**
     * Getter de versionSenat.
     *
     * @return versionSenat
     */
    public String getVersionSenat() {
        return versionSenat;
    }

    /**
     * Setter de versionSenat.
     *
     * @param versionSenat versionSenat
     */
    public void setVersionSenat(String versionSenat) {
        this.versionSenat = versionSenat;
    }

    /**
     * Getter de versionObjetLike.
     *
     * @return versionObjetLike
     */
    public String getVersionObjetLike() {
        return versionObjetLike;
    }

    /**
     * Setter de versionObjetLike.
     *
     * @param versionObjetLike versionObjetLike
     */
    public void setVersionObjetLike(String versionObjetLike) {
        this.versionObjetLike = versionObjetLike;
    }

    /**
     * Getter de versionHorodatageMin.
     *
     * @return versionHorodatageMin
     */
    public Calendar getVersionHorodatageMin() {
        return versionHorodatageMin;
    }

    /**
     * Setter de versionHorodatageMin.
     *
     * @param versionHorodatageMin versionHorodatageMin
     */
    public void setVersionHorodatageMin(Calendar versionHorodatageMin) {
        this.versionHorodatageMin = versionHorodatageMin;
    }

    /**
     * Getter de versionHorodatageMax.
     *
     * @return versionHorodatageMax
     */
    public Calendar getVersionHorodatageMax() {
        return versionHorodatageMax;
    }

    /**
     * Setter de versionHorodatageMax.
     *
     * @param versionHorodatageMax versionHorodatageMax
     */
    public void setVersionHorodatageMax(Calendar versionHorodatageMax) {
        this.versionHorodatageMax = versionHorodatageMax;
    }

    /**
     * Getter de versionNiveauLectureNumero.
     *
     * @return versionNiveauLectureNumero
     */
    public Long getVersionNiveauLectureNumero() {
        return versionNiveauLectureNumero;
    }

    /**
     * Setter de versionNiveauLectureNumero.
     *
     * @param versionNiveauLectureNumero versionNiveauLectureNumero
     */
    public void setVersionNiveauLectureNumero(Long versionNiveauLectureNumero) {
        this.versionNiveauLectureNumero = versionNiveauLectureNumero;
    }

    /**
     * Getter de versionNiveauLecture.
     *
     * @return versionNiveauLecture
     */
    public String getVersionNiveauLecture() {
        return versionNiveauLecture;
    }

    /**
     * Setter de versionNiveauLecture.
     *
     * @param versionNiveauLecture versionNiveauLecture
     */
    public void setVersionNiveauLecture(String versionNiveauLecture) {
        this.versionNiveauLecture = versionNiveauLecture;
    }

    /**
     * Getter de versionPieceJointePresente.
     *
     * @return versionPieceJointePresente
     */
    public Boolean getVersionPieceJointePresente() {
        return versionPieceJointePresente;
    }

    /**
     * Setter de versionPieceJointePresente.
     *
     * @param versionPieceJointePresente versionPieceJointePresente
     */
    public void setVersionPieceJointePresente(Boolean versionPieceJointePresente) {
        this.versionPieceJointePresente = versionPieceJointePresente;
    }

    /**
     * Getter de versionNumeroDepotTexte.
     *
     * @return versionNumeroDepotTexte
     */
    public String getVersionNumeroDepotTexte() {
        return versionNumeroDepotTexte;
    }

    /**
     * Setter de versionNumeroDepotTexte.
     *
     * @param versionNumeroDepotTexte versionNumeroDepotTexte
     */
    public void setVersionNumeroDepotTexte(String versionNumeroDepotTexte) {
        this.versionNumeroDepotTexte = versionNumeroDepotTexte;
    }
    
    /**
     * Getter de dossierNumeroDepotTexte.
     *
     * @return dossierNumeroDepotTexte
     */
    public String getDossierNumeroDepotTexte() {
      return dossierNumeroDepotTexte;
    }

    /**
     * Setter de dossierNumeroDepotTexte.
     *
     * @param dossierNumeroDepotTexte dossierNumeroDepotTexte
     */
    public void setDossierNumeroDepotTexte(String dossierNumeroDepotTexte) {
        this.dossierNumeroDepotTexte = dossierNumeroDepotTexte;
    }
    /**
     * Getter de pieceJointeFichierFulltext.
     *
     * @return pieceJointeFichierFulltext
     */
    public String getPieceJointeFichierFulltext() {
        return pieceJointeFichierFulltext;
    }

    /**
     * Setter de pieceJointeFichierFulltext.
     *
     * @param pieceJointeFichierFulltext pieceJointeFichierFulltext
     */
    public void setPieceJointeFichierFulltext(String pieceJointeFichierFulltext) {
        this.pieceJointeFichierFulltext = pieceJointeFichierFulltext;
    }

    /**
     * Getter de metadonneeCriteria.
     *
     * @return metadonneeCriteria
     */
    public Map<String, Object> getMetadonneeCriteria() {
        return metadonneeCriteria;
    }

    /**
     * Setter de metadonneeCriteria.
     *
     * @param metadonneeCriteria metadonneeCriteria
     */
    public void setMetadonneeCriteria(Map<String, Object> metadonneeCriteria) {
        this.metadonneeCriteria = metadonneeCriteria;
    }
    
    /**
     * @return the joinEvenementForSorting
     */
    public boolean isJoinEvenementForSorting() {
        return joinEvenementForSorting;
    }

    /**
     * @param joinEvenementForSorting the joinEvenementForSorting to set
     */
    public void setJoinEvenementForSorting(boolean joinEvenementForSorting) {
        this.joinEvenementForSorting = joinEvenementForSorting;
    }

    /**
     * @return the joinDossierForSorting
     */
    public boolean isJoinDossierForSorting() {
        return joinDossierForSorting;
    }

    /**
     * @param joinDossierForSorting the joinDossierForSorting to set
     */
    public void setJoinDossierForSorting(boolean joinDossierForSorting) {
        this.joinDossierForSorting = joinDossierForSorting;
    }

    /**
     * @return the joinVersionForSorting
     */
    public boolean isJoinVersionForSorting() {
        return joinVersionForSorting;
    }

    /**
     * @param joinVersionForSorting the joinVersionForSorting to set
     */
    public void setJoinVersionForSorting(boolean joinVersionForSorting) {
        this.joinVersionForSorting = joinVersionForSorting;
    }

    /**
     * @return the orderByHorodatage
     */
    public boolean isOrderByHorodatage() {
        return orderByHorodatage;
    }

    /**
     * @param orderByHorodatage the orderByHorodatage to set
     */
    public void setOrderByHorodatage(boolean orderByHorodatage) {
        this.orderByHorodatage = orderByHorodatage;
    }

    /**
     * @return the orderByList
     */
    public List<OrderByCriteria> getOrderByList() {
        return orderByList;
    }

    /**
     * @param orderByList the orderByList to set
     */
    public void setOrderByList(List<OrderByCriteria> orderByList) {
        this.orderByList = orderByList;
    }

    /**
     * @return the dateTraitementMin
     */
    public Calendar getDateTraitementMin() {
        return dateTraitementMin;
    }

    /**
     * @param dateTraitementMin the dateTraitementMin to set
     */
    public void setDateTraitementMin(Calendar dateTraitementMin) {
        this.dateTraitementMin = dateTraitementMin;
    }

    /**
     * @return the etatMessageExclus
     */
    public List<String> getEtatMessageExclus() {
        return etatMessageExclus;
    }

    /**
     * @param etatMessageExclus the etatMessageExclus to set
     */
    public void setEtatMessageExclus(List<String> etatMessageExclus) {
        this.etatMessageExclus = etatMessageExclus;
    }

    /**
     * @return the corbeilleMessageType
     */
    public Boolean getCorbeilleMessageType() {
        return corbeilleMessageType;
    }

    /**
     * @param corbeilleMessageType the corbeilleMessageType to set
     */
    public void setCorbeilleMessageType(Boolean corbeilleMessageType) {
        this.corbeilleMessageType = corbeilleMessageType;
    }

    /**
     * @return the etatMessageIn
     */
    public List<String> getEtatMessageIn() {
        return etatMessageIn;
    }

    /**
     * @param etatMessageIn the etatMessageIn to set
     */
    public void setEtatMessageIn(List<String> etatMessageIn) {
        this.etatMessageIn = etatMessageIn;
    }

    /**
     * @return the etatMessage
     */
    public String getEtatMessage() {
        return etatMessage;
    }

    /**
     * @param etatMessage the etatMessage to set
     */
    public void setEtatMessage(String etatMessage) {
        this.etatMessage = etatMessage;
    }

    public List<String> getEvenementEmetteurIn() {
      return evenementEmetteurIn;
    }

    public void setEvenementEmetteurIn(List<String> evenementEmetteurIn) {
      this.evenementEmetteurIn = evenementEmetteurIn;
    }

    public List<String> getEvenementDestinataireIn() {
      return evenementDestinataireIn;
    }

    public void setEvenementDestinataireIn(List<String> evenementDestinataireIn) {
      this.evenementDestinataireIn = evenementDestinataireIn;
    }

    public List<String> getEvenementDestinataireCopieIn() {
      return evenementDestinataireCopieIn;
    }

    public void setEvenementDestinataireCopieIn(List<String> evenementDestinataireCopieIn) {
      this.evenementDestinataireCopieIn = evenementDestinataireCopieIn;
    }
    
}
