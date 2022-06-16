package fr.dila.solonepp.api.service.rechercherMessage;

/**
 * Contexte pour effectuer les règle métier de recherche de message.
 *
 * @author sly
 */
public class RechercherMessageContext {
    /**
     * Requête de recherche de message.
     */
    private final RechercherMessageRequest rechercherMessageRequest;

    /**
     * Reponse de recherche de message.
     */
    private final RechercherMessageResponse rechercherMessageResponse;

    /**
     * Constructeur de RechercherMessageContext.
     */
    public RechercherMessageContext() {
        rechercherMessageRequest = new RechercherMessageRequest();
        rechercherMessageResponse = new RechercherMessageResponse();
    }

    /**
     * Getter de rechercherMessageRequest.
     *
     * @return rechercherMessageRequest
     */
    public RechercherMessageRequest getRechercherMessageRequest() {
        return rechercherMessageRequest;
    }

    /**
     * Getter de rechercherMessageResponse.
     *
     * @return rechercherMessageResponse
     */
    public RechercherMessageResponse getRechercherMessageResponse() {
        return rechercherMessageResponse;
    }
}
