package fr.dila.st.ui.services.actions.suggestion;

import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;

public final class SuggestionHelper {
    /**
     * Nombre maximal de réponses renvoyé par un provider de suggestions.
     */
    private static final int MAX_RESULTS = 10000;

    private SuggestionHelper() {}

    /**
     * Construit la liste des suggestions à partir des résultats de la requête
     * associée <code>res</code> et de <code>colname</code>, la colonne d'où
     * extraire l'information à afficher.
     */
    public static List<String> buildSuggestionsList(
        final String colname,
        CoreSession session,
        String query,
        Object[] params
    ) {
        try (
            IterableQueryResult res = QueryUtils.doSqlQuery(
                session,
                new String[] { colname },
                query,
                params,
                MAX_RESULTS,
                0
            )
        ) {
            return StreamSupport
                .stream(res.spliterator(), false)
                .map(row -> (String) row.get(colname))
                .collect(Collectors.toList());
        }
    }

    public static List<String> buildSuggestionsListUFNXQL(
        final String colname,
        CoreSession session,
        String query,
        Object[] params
    ) {
        return QueryUtils.doUFNXQLQueryAndMapping(
            session,
            query,
            params,
            MAX_RESULTS,
            0,
            (Map<String, Serializable> rowData) -> (String) rowData.get(colname)
        );
    }

    public static List<String> limitSuggestions(Stream<String> stream) {
        return stream.limit(MAX_RESULTS).collect(Collectors.toList());
    }
}
