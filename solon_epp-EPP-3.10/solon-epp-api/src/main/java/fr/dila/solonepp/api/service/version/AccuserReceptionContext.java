package fr.dila.solonepp.api.service.version;

/**
 * Contexte pour effectuer un accusé de réception.
 * 
 * @author jtremeaux
 */
public class AccuserReceptionContext {
    
    /**
     * Requête de recherche de message.
     */
    private AccuserReceptionRequest accuserReceptionRequest;

    /**
     * Reponse de recherche de message.
     */
    private AccuserReceptionResponse accuserReceptionResponse;
    
    /**
     * Constructeur de AccuserReceptionContext.
     */
    public AccuserReceptionContext() {
        accuserReceptionRequest = new AccuserReceptionRequest();
        accuserReceptionResponse = new AccuserReceptionResponse();
    }

    /**
     * Getter de accuserReceptionRequest.
     *
     * @return accuserReceptionRequest
     */
    public AccuserReceptionRequest getAccuserReceptionRequest() {
        return accuserReceptionRequest;
    }

    /**
     * Setter de accuserReceptionRequest.
     *
     * @param accuserReceptionRequest accuserReceptionRequest
     */
    public void setAccuserReceptionRequest(AccuserReceptionRequest accuserReceptionRequest) {
        this.accuserReceptionRequest = accuserReceptionRequest;
    }

    /**
     * Getter de accuserReceptionResponse.
     *
     * @return accuserReceptionResponse
     */
    public AccuserReceptionResponse getAccuserReceptionResponse() {
        return accuserReceptionResponse;
    }

    /**
     * Setter de accuserReceptionResponse.
     *
     * @param accuserReceptionResponse accuserReceptionResponse
     */
    public void setAccuserReceptionResponse(AccuserReceptionResponse accuserReceptionResponse) {
        this.accuserReceptionResponse = accuserReceptionResponse;
    }
}
