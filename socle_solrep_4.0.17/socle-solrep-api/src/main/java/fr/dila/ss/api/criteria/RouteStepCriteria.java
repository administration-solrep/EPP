package fr.dila.ss.api.criteria;

/**
 * Critère de recherche des étapes de feuilles de routes.
 *
 * @author jtremeaux
 */
public class RouteStepCriteria {
    /**
     * UUID de la feuille de route.
     */
    private String documentRouteId;

    /**
     * Default constructor
     */
    public RouteStepCriteria() {
        // do nothing
    }

    /**
     * Getter de documentRouteId.
     *
     * @return documentRouteId
     */
    public String getDocumentRouteId() {
        return documentRouteId;
    }

    /**
     * Setter de documentRouteId.
     *
     * @param documentRouteId documentRouteId
     */
    public void setDocumentRouteId(String documentRouteId) {
        this.documentRouteId = documentRouteId;
    }
}
