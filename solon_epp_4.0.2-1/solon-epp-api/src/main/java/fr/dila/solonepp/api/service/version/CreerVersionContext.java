package fr.dila.solonepp.api.service.version;

/**
 * Contexte pour effectuer les règle métier création d'une version.
 *
 * @author jtremeaux
 */
public class CreerVersionContext {
    /**
     * Requête de création de version.
     */
    private CreerVersionRequest creerVersionRequest;

    /**
     * Réponse de création de version.
     */
    private CreerVersionResponse creerVersionResponse;

    /**
     * Crée la version à l'état publié (sinon brouillon).
     */
    private boolean publier;

    /**
     * Constructeur de CreerVersionContext.
     */
    public CreerVersionContext() {
        creerVersionRequest = new CreerVersionRequest();
        creerVersionResponse = new CreerVersionResponse();
    }

    /**
     * Getter de creerVersionRequest.
     *
     * @return creerVersionRequest
     */
    public CreerVersionRequest getCreerVersionRequest() {
        return creerVersionRequest;
    }

    /**
     * Setter de creerVersionRequest.
     *
     * @param creerVersionRequest creerVersionRequest
     */
    public void setCreerVersionRequest(CreerVersionRequest creerVersionRequest) {
        this.creerVersionRequest = creerVersionRequest;
    }

    /**
     * Getter de creerVersionResponse.
     *
     * @return creerVersionResponse
     */
    public CreerVersionResponse getCreerVersionResponse() {
        return creerVersionResponse;
    }

    /**
     * Setter de creerVersionResponse.
     *
     * @param creerVersionResponse creerVersionResponse
     */
    public void setCreerVersionResponse(CreerVersionResponse creerVersionResponse) {
        this.creerVersionResponse = creerVersionResponse;
    }

    /**
     * Getter de publier.
     *
     * @return publier
     */
    public boolean isPublier() {
        return publier;
    }

    /**
     * Setter de publier.
     *
     * @param publie publier
     */
    public void setPublie(boolean publier) {
        this.publier = publier;
    }
}
