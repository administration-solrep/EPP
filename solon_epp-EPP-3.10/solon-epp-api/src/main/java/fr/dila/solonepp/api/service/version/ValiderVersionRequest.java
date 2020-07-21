package fr.dila.solonepp.api.service.version;

/**
 * Données de la requête pour accepter / rejeter / abandonner une version rectifiée.
 * 
 * @author jtremeaux
 */
public class ValiderVersionRequest {
    /**
     * Identifiant technique de l'événement dont on veut valider la version.
     */
    private String evenementId;
    
    /**
     * Accepte la rectification de l'événement.
     */
    private boolean accepter;
    
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
     * Getter de accepter.
     *
     * @return accepter
     */
    public boolean isAccepter() {
        return accepter;
    }

    /**
     * Setter de accepter.
     *
     * @param accepter accepter
     */
    public void setAccepter(boolean accepter) {
        this.accepter = accepter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ValiderVersionRequest: ")
            .append("{evenementId: ").append(evenementId)
            .append(", accepter: ").append(accepter)
            .append("}");
        return sb.toString();
    }
}
