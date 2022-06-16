package fr.dila.solonepp.api.service.evenement;

/**
 * Contexte pour effectuer une initialisation d'evenement.
 *
 * @author asatre
 */
public class InitialiserEvenementContext {
    /**
     * Requête d'initialisation d'evenement.
     */
    private InitialiserEvenementRequest initialiserEvenementRequest;

    /**
     * Reponse d'initialisation d'evenement.
     */
    private InitialiserEvenementResponse initialiserEvenementResponse;

    /**
     * Constructeur de AccuserReceptionContext.
     */
    public InitialiserEvenementContext() {
        initialiserEvenementRequest = new InitialiserEvenementRequest();
        initialiserEvenementResponse = new InitialiserEvenementResponse();
    }

    public InitialiserEvenementRequest getInitialiserEvenementRequest() {
        return initialiserEvenementRequest;
    }

    public void setInitialiserEvenementRequest(InitialiserEvenementRequest initialiserEvenementRequest) {
        this.initialiserEvenementRequest = initialiserEvenementRequest;
    }

    public InitialiserEvenementResponse getInitialiserEvenementResponse() {
        return initialiserEvenementResponse;
    }

    public void setInitialiserEvenementResponse(InitialiserEvenementResponse initialiserEvenementResponse) {
        this.initialiserEvenementResponse = initialiserEvenementResponse;
    }
}
