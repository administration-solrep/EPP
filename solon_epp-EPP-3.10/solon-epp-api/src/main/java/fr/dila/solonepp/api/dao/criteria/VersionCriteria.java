package fr.dila.solonepp.api.dao.criteria;

import java.util.List;

import org.nuxeo.common.utils.StringUtils;

import fr.dila.solonepp.api.domain.evenement.NumeroVersion;

/**
 * Critères de recherche des versions.
 * 
 * @author jtremeaux
 */
public class VersionCriteria {
    /**
     * Identifiant technique (titre) de l'événement.
     */
    private String evenementId;
    
    /**
     * Numéro de version (égalité).
     */
    private NumeroVersion numeroVersionEquals;
    
    /**
     * Numéro de version (non égalité).
     */
    private NumeroVersion numeroVersionNotEquals;
    
    /**
     * Numéro de version majeur (égalité).
     */
    private Long majorVersionEquals;
    
    /**
     * Numéro de version mineur (strictement supérieur).
     */
    private Long minorVersionStrictlyGreater;
    
    /**
     * État en cours du cycle de vie.
     */
    private String currentLifeCycleState;
    
    /**
     * État en cours du cycle de vie (n'importe quel état parmis les états spécifiés).
     */
    private List<String> currentLifeCycleStateIn;
    
    /**
     * Tri par n° de version descendante.
     */
    private boolean orderVersionDesc;
    
    /**
     * si true, ramène les versions avec dateAr null
     */
    private boolean dateArNull;
    
    /**
     * Si true, ramene la version courante
     */
    private boolean versionCourante;
    
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
     * Getter de numeroVersionEquals.
     *
     * @return numeroVersionEquals
     */
    public NumeroVersion getNumeroVersionEquals() {
        return numeroVersionEquals;
    }

    /**
     * Setter de numeroVersionEquals.
     *
     * @param numeroVersionEquals numeroVersionEquals
     */
    public void setNumeroVersionEquals(NumeroVersion numeroVersionEquals) {
        this.numeroVersionEquals = numeroVersionEquals;
    }

    /**
     * Getter de numeroVersionNotEquals.
     *
     * @return numeroVersionNotEquals
     */
    public NumeroVersion getNumeroVersionNotEquals() {
        return numeroVersionNotEquals;
    }

    /**
     * Setter de numeroVersionNotEquals.
     *
     * @param numeroVersionNotEquals numeroVersionNotEquals
     */
    public void setNumeroVersionNotEquals(NumeroVersion numeroVersionNotEquals) {
        this.numeroVersionNotEquals = numeroVersionNotEquals;
    }

    /**
     * Getter de majorVersionEquals.
     *
     * @return majorVersionEquals
     */
    public Long getMajorVersionEquals() {
        return majorVersionEquals;
    }

    /**
     * Setter de majorVersionEquals.
     *
     * @param majorVersionEquals majorVersionEquals
     */
    public void setMajorVersionEquals(Long majorVersionEquals) {
        this.majorVersionEquals = majorVersionEquals;
    }

    /**
     * Getter de minorVersionStrictlyGreater.
     *
     * @return minorVersionStrictlyGreater
     */
    public Long getMinorVersionStrictlyGreater() {
        return minorVersionStrictlyGreater;
    }

    /**
     * Setter de minorVersionStrictlyGreater.
     *
     * @param minorVersionStrictlyGreater minorVersionStrictlyGreater
     */
    public void setMinorVersionStrictlyGreater(Long minorVersionStrictlyGreater) {
        this.minorVersionStrictlyGreater = minorVersionStrictlyGreater;
    }

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
     * Getter de currentLifeCycleStateIn.
     *
     * @return currentLifeCycleStateIn
     */
    public List<String> getCurrentLifeCycleStateIn() {
        return currentLifeCycleStateIn;
    }

    /**
     * Setter de currentLifeCycleStateIn.
     *
     * @param currentLifeCycleStateIn currentLifeCycleStateIn
     */
    public void setCurrentLifeCycleStateIn(List<String> currentLifeCycleStateIn) {
        this.currentLifeCycleStateIn = currentLifeCycleStateIn;
    }

    /**
     * Getter de orderVersionDesc.
     *
     * @return orderVersionDesc
     */
    public boolean isOrderVersionDesc() {
        return orderVersionDesc;
    }

    /**
     * Setter de orderVersionDesc.
     *
     * @param orderVersionDesc orderVersionDesc
     */
    public void setOrderVersionDesc(boolean orderVersionDesc) {
        this.orderVersionDesc = orderVersionDesc;
    }
    
    public void setDateArNull(boolean dateArNull) {
        this.dateArNull = dateArNull;
    }
    
    public boolean isDateArNull() {
        return dateArNull;
    }
    
    /**
     * @return the versionCourante
     */
    public boolean isVersionCourante() {
        return versionCourante;
    }

    /**
     * @param versionCourante the versionCourante to set
     */
    public void setVersionCourante(boolean versionCourante) {
        this.versionCourante = versionCourante;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("VersionCriteria: ")
            .append("{evenementId: ").append(evenementId)
            .append(", numeroVersionEquals: ").append(numeroVersionEquals)
            .append(", numeroVersionNotEquals: ").append(numeroVersionNotEquals)
            .append(", currentLifeCycleStateIn: ").append(currentLifeCycleStateIn != null ? StringUtils.join(currentLifeCycleStateIn, ", ") : "null")
            .append(", orderVersionDesc: ").append(orderVersionDesc)
            .append(", dateArNull: ").append(dateArNull)
            .append("}");
        return sb.toString();
    }

}
