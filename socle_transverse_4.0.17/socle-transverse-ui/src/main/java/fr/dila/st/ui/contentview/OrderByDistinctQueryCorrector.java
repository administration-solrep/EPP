package fr.dila.st.ui.contentview;

import org.nuxeo.ecm.core.api.SortInfo;

/**
 *
 * Modifie la requête pour ajouter les informations de tri
 *
 */
public class OrderByDistinctQueryCorrector {

    /**
     * Modifie la requête, et renvoie la requête incorporant les informations de tri
     *
     * @param query
     *            la requête
     * @param sortArray
     *            le tableau de tri
     * @return
     */
    public static String correct(String query, SortInfo[] sortArray) {
        if (query == null || query.isEmpty()) {
            return query;
        }

        final String lowerQuery = query.toLowerCase();

        if (!lowerQuery.contains("distinct")) {
            return query;
        }
        int pos = lowerQuery.indexOf("from");
        StringBuilder queryBeforeFrom = new StringBuilder(query.substring(0, pos));
        String queryAfterFrom = query.substring(pos, query.length());

        if (sortArray != null) {
            for (SortInfo sortInfo : sortArray) {
                queryBeforeFrom.append(", ").append(sortInfo.getSortColumn());
            }
        }

        return queryBeforeFrom.toString().trim() + " " + queryAfterFrom;
    }
}
