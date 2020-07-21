package fr.dila.solonepp.api.service.evenement;

/**
 * Données de la requête pour accepter / rejeter / abandonner une version rectifiée.
 * 
 * @author jtremeaux
 */
public class AnnulerEvenementRequest {
    /**
     * Identifiant technique de l'événement à annuler.
     */
    private String evenementId;
    
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AnnulerEvenementRequest: ")
            .append("{evenementId: ").append(evenementId)
            .append("}");
        return sb.toString();
    }
}
