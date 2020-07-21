package fr.dila.solonepp.api.service.version;

/**
 * Contexte pour accepter / rejeter / abandonner une version rectifiée.
 * 
 * @author jtremeaux
 */
public class ValiderVersionContext {
    
    /**
     * Requête de validation de la version.
     */
    private ValiderVersionRequest validerVersionRequest;

    /**
     * Reponse de validation de la version.
     */
    private ValiderVersionResponse validerVersionResponse;
    
    /**
     * Constructeur de ValiderVersionContext.
     */
    public ValiderVersionContext() {
        validerVersionRequest = new ValiderVersionRequest();
        validerVersionResponse = new ValiderVersionResponse();
    }

    /**
     * Getter de validerVersionRequest.
     *
     * @return validerVersionRequest
     */
    public ValiderVersionRequest getValiderVersionRequest() {
        return validerVersionRequest;
    }

    /**
     * Setter de validerVersionRequest.
     *
     * @param validerVersionRequest validerVersionRequest
     */
    public void setValiderVersionRequest(ValiderVersionRequest validerVersionRequest) {
        this.validerVersionRequest = validerVersionRequest;
    }

    /**
     * Getter de validerVersionResponse.
     *
     * @return validerVersionResponse
     */
    public ValiderVersionResponse getValiderVersionResponse() {
        return validerVersionResponse;
    }

    /**
     * Setter de validerVersionResponse.
     *
     * @param validerVersionResponse validerVersionResponse
     */
    public void setValiderVersionResponse(ValiderVersionResponse validerVersionResponse) {
        this.validerVersionResponse = validerVersionResponse;
    }
}
