package fr.dila.solonepp.api.dao.criteria;

import java.util.Calendar;

/**
 * Critères de recherche des JetonDoc.
 *
 * @author jtremeaux
 */
public class JetonDocCriteria {
    /**
     * Type de jeton.
     */
    private String jetonType;

    /**
     * Propriétaire du jeton.
     */
    private String proprietaireId;

    /**
     * Nombre de réessai supérieur à 0.
     */
    private Boolean retryGreaterThanZero;

    /**
     * Corbeille du message lié au jeton.
     */
    private String corbeille;

    /**
     * Date de création maximum du jeton.
     */
    private Calendar dateMax;

    /**
     * Identifiant technique de l'évènement lié au jeton.
     */
    private String evenementId;

    private Long jetonId;

    /**
     * @return the evenementId
     */
    public String getEvenementId() {
        return evenementId;
    }

    /**
     * @param evenementId the evenementId to set
     */
    public void setEvenementId(String evenementId) {
        this.evenementId = evenementId;
    }

    /**
     * Getter de jetonType.
     *
     * @return jetonType
     */
    public String getJetonType() {
        return jetonType;
    }

    /**
     * Setter de jetonType.
     *
     * @param jetonType jetonType
     */
    public void setJetonType(String jetonType) {
        this.jetonType = jetonType;
    }

    /**
     * Getter de proprietaireId.
     *
     * @return proprietaireId
     */
    public String getProprietaireId() {
        return proprietaireId;
    }

    /**
     * Setter de proprietaireId.
     *
     * @param proprietaireId proprietaireId
     */
    public void setProprietaireId(String proprietaireId) {
        this.proprietaireId = proprietaireId;
    }

    /**
     * Getter de retryGreaterThanZero.
     *
     * @return retryGreaterThanZero
     */
    public Boolean getRetryGreaterThanZero() {
        return retryGreaterThanZero;
    }

    /**
     * Setter de retryGreaterThanZero.
     *
     * @param retryGreaterThanZero retryGreaterThanZero
     */
    public void setRetryGreaterThanZero(Boolean retryGreaterThanZero) {
        this.retryGreaterThanZero = retryGreaterThanZero;
    }

    /**
     * @return the corbeille
     */
    public String getCorbeille() {
        return corbeille;
    }

    /**
     * @param corbeille the corbeille to set
     */
    public void setCorbeille(String corbeille) {
        this.corbeille = corbeille;
    }

    /**
     * @return the dateMax
     */
    public Calendar getDateMax() {
        return dateMax;
    }

    /**
     * @param dateMax the dateMax to set
     */
    public void setDateMax(Calendar dateMax) {
        this.dateMax = dateMax;
    }

    public void setJetonId(Long jetonId) {
        this.jetonId = jetonId;
    }

    public Long getJetonId() {
        return jetonId;
    }
}
