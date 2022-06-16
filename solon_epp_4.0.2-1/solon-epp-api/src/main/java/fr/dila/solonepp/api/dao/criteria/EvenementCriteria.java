package fr.dila.solonepp.api.dao.criteria;

/**
 * Critères de recherche des événements.
 *
 * @author jtremeaux
 */
public class EvenementCriteria {
    /**
     * Identifiant technique (titre) de l'événement.
     */
    private String evenementId;

    /**
     * Identifiant technique (titre) de l'événement parent.
     */
    private String evenementParentId;

    /**
     * Identifiant technique (titre) du dossier.
     */
    private String dossierId;

    /**
     * Order by idEvenement
     */
    private boolean orderByIdEvenement;

    /**
     * @return the dossierId
     */
    public String getDossierId() {
        return dossierId;
    }

    /**
     * @param dossierId the dossierId to set
     */
    public void setDossierId(String dossierId) {
        this.dossierId = dossierId;
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
     * Getter de evenementParentId.
     *
     * @return evenementParentId
     */
    public String getEvenementParentId() {
        return evenementParentId;
    }

    /**
     * Setter de evenementParentId.
     *
     * @param evenementParentId evenementParentId
     */
    public void setEvenementParentId(String evenementParentId) {
        this.evenementParentId = evenementParentId;
    }

    /**
     * @return the orderByIdEvenement
     */
    public boolean isOrderByIdEvenement() {
        return orderByIdEvenement;
    }

    /**
     * @param orderByIdEvenement the orderByIdEvenement to set
     */
    public void setOrderByIdEvenement(boolean orderByIdEvenement) {
        this.orderByIdEvenement = orderByIdEvenement;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EvenementCriteria: ")
            .append("{evenementId: ")
            .append(evenementId)
            .append(", evenementParentId: ")
            .append(evenementParentId)
            .append(", dossierId: ")
            .append(dossierId)
            .append("}");
        return sb.toString();
    }
}
