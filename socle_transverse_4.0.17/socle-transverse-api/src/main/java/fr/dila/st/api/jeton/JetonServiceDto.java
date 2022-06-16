package fr.dila.st.api.jeton;

import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Classe de transport des résultats de GetDocuments du service JetonService
 *
 * @author sly
 *
 */
public class JetonServiceDto {
    /**
     * Défini la valeur du prochain numéro de jeton pour récupérer des données
     */
    private Long nextJetonNumber;

    /**
     * Défini si ce transport de résultat est le dernier envoi ou s'il y a d'autres jetons en attente de lecture
     */
    private Boolean lastSending;

    /**
     * la liste des documentModel contenus par les jetonDoc de résultats
     */
    private List<DocumentModel> documentList;

    /**
     * La liste des jetonsDoc correspondants aux resultats
     */
    private List<DocumentModel> jetonDocDocList;

    /**
     * Le message d'erreur à remonter si besoin
     */
    private String messageErreur;

    public JetonServiceDto() {
        // do nothing
    }

    public JetonServiceDto(Long nextJeton, Boolean lastSending, List<DocumentModel> documentList) {
        this.nextJetonNumber = nextJeton;
        this.lastSending = lastSending;
        this.documentList = documentList;
    }

    /**
     *
     * @return le prochain numéro de jeton qu'il est possible d'appeler pour obtenir des jetons
     */
    public Long getNextJetonNumber() {
        return nextJetonNumber;
    }

    /**
     * set le prochain numéro de jeton qu'il est possible d'appeler pour obtenir des jetons
     *
     * @param nextJetonNumber
     */
    public void setNextJetonNumber(Long nextJetonNumber) {
        this.nextJetonNumber = nextJetonNumber;
    }

    /**
     *
     * @return vrai s'il s'agit du dernier transport de jeton et qu'il n'y a plus de jetons en attente de lecture
     */
    public Boolean isLastSending() {
        return lastSending;
    }

    /**
     * set l'information du dernier transport : vrai s'il n'y a plus de jetons en attente de lecture
     *
     * @param lastSending
     */
    public void setLastSending(Boolean lastSending) {
        this.lastSending = lastSending;
    }

    public List<DocumentModel> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<DocumentModel> documentList) {
        this.documentList = documentList;
    }

    public List<DocumentModel> getJetonDocDocList() {
        return this.jetonDocDocList;
    }

    public void setJetonDocDocList(List<DocumentModel> jetonDocDocList) {
        this.jetonDocDocList = jetonDocDocList;
    }

    public void setMessageErreur(String messageErreur) {
        this.messageErreur = messageErreur;
    }

    public String getMessageErreur() {
        return this.messageErreur;
    }
}
