package fr.dila.ss.api.constant;

public class SSExceptionConstants {
    /**
     * Exception pour les listes de données des exports si l'une est vide
     */
    public static final String EMPTY_REPORTS_EXC =
        "Les données d'export statistiques (noms, titres, etc.) ne peuvent être vides";

    /**
     * Exception si les tailles de listes de données pour les exports sont différentes
     */
    public static final String DIFFERENTS_SIZES_REPORTS_EXC =
        "Les données d'export statistiques (noms, titres, etc.) ne peuvent avoir une taille différente";

    /**
     * Exception si un service n'est pas disponible
     */
    public static final String SERVICE_NOT_AVAILABLE = "Impossible d'accéder au service nécessaire";
}
