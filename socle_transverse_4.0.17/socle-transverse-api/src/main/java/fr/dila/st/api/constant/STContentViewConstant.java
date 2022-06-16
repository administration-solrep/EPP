package fr.dila.st.api.constant;

/**
 * Content Views du socle transverse.
 *
 * @author jtremeaux
 */
public final class STContentViewConstant {
    /**
     * Content view de la liste des modèles de feuille de route.
     */
    public static final String FEUILLE_ROUTE_MODEL_FOLDER_CONTENT_VIEW = "feuille_route_model_folder_content";

    /**
     * Content view du journal de l'espace d'administration.
     */
    public static final String JOURNAL_ESPACE_ADMIN_CONTENT_VIEW = "ADMIN_JOURNAL_DOSSIER";

    /**
     * "Requete" vide pour ne pas faire une requete du genre "Select * from Dossier where dc:title = 'existePas'" gerer
     * seulement dans PaginatedPageDocumentProvider
     */
    public static final String DEFAULT_EMPTY_QUERY = "DEFAULT_EMPTY_QUERY";

    /**
     * utility class
     */
    private STContentViewConstant() {
        // do nothing
    }
}
