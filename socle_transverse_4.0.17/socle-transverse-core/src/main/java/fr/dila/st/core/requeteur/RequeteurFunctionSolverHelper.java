package fr.dila.st.core.requeteur;

import fr.dila.st.api.requeteur.RequeteurFunctionSolver;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Une classe utilitaire pour pouvoir utiliser les objets de type RequeteurFunctionSolver qui permette la résolution de
 * mot-clés spéciaux, dans le but d'étendre les requêtes UFNXQL
 *
 * @author jgomez
 *
 */
public final class RequeteurFunctionSolverHelper {
    private static final Log LOGGER = LogFactory.getLog(RequeteurFunctionSolverHelper.class);

    /**
     * utility class
     */
    private RequeteurFunctionSolverHelper() {
        // do nothing
    }

    /**
     * Applique un résolveur de mot-clés "solver" sur la requête "query" avec un environnement donné par une map "env"
     * et la session "session".
     *
     * @param solver
     * @param session
     * @param query
     * @param env
     * @return La requête transformée par le solveur
     */
    public static String apply(
        final RequeteurFunctionSolver solver,
        final CoreSession session,
        final String query,
        final Map<String, Object> env
    ) {
        final Pattern pattern = solver.getPattern();
        solver.setEnv(env);
        String solvedQuery = query;
        final Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            final String groupStr = matcher.group(1);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Détection d'une fonction : " + groupStr);
            }
            solver.setExpr(groupStr);
            solvedQuery = solvedQuery.replace(solver.getExpressionToBeMatched(groupStr), solver.solve(session));
        }
        return solvedQuery;
    }
}
