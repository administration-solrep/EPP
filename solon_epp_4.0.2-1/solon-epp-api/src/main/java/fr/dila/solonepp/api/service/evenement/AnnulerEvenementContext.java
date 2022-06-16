package fr.dila.solonepp.api.service.evenement;

/**
 * Contexte pour annuler un événement / effectuer une demande d'annulation d'un événement.
 *
 * @author jtremeaux
 */
public class AnnulerEvenementContext {
    /**
     * Requête de l'annulation de l'événement.
     */
    private AnnulerEvenementRequest annulerEvenementRequest;

    /**
     * Reponse de l'annulation de l'événement.
     */
    private AnnulerEvenementResponse annulerEvenementResponse;

    /**
     * Constructeur de AnnulerEvenementContext.
     */
    public AnnulerEvenementContext() {
        annulerEvenementRequest = new AnnulerEvenementRequest();
        annulerEvenementResponse = new AnnulerEvenementResponse();
    }

    /**
     * Getter de annulerEvenementRequest.
     *
     * @return annulerEvenementRequest
     */
    public AnnulerEvenementRequest getAnnulerEvenementRequest() {
        return annulerEvenementRequest;
    }

    /**
     * Setter de annulerEvenementRequest.
     *
     * @param annulerEvenementRequest annulerEvenementRequest
     */
    public void setAnnulerEvenementRequest(AnnulerEvenementRequest annulerEvenementRequest) {
        this.annulerEvenementRequest = annulerEvenementRequest;
    }

    /**
     * Getter de annulerEvenementResponse.
     *
     * @return annulerEvenementResponse
     */
    public AnnulerEvenementResponse getAnnulerEvenementResponse() {
        return annulerEvenementResponse;
    }

    /**
     * Setter de annulerEvenementResponse.
     *
     * @param annulerEvenementResponse annulerEvenementResponse
     */
    public void setAnnulerEvenementResponse(AnnulerEvenementResponse annulerEvenementResponse) {
        this.annulerEvenementResponse = annulerEvenementResponse;
    }
}
