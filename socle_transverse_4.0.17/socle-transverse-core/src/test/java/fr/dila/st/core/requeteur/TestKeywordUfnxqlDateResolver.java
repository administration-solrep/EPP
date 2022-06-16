package fr.dila.st.core.requeteur;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.joda.time.DateTime;

/**
 * Test de KeywordUfnxqlDateResolver N'utilise pas lla session => lui passe null
 *
 * @author SPL
 *
 */
public class TestKeywordUfnxqlDateResolver extends TestCase {
    private static final String NOW = "NOW";

    public void testDateResolver() {
        Map<String, Object> env = new HashMap<String, Object>();
        env.put(NOW, new DateTime(2011, 7, 5, 10, 36, 0, 0));
        KeywordUfnxqlDateResolver keywordDateResolver = new KeywordUfnxqlDateResolver();
        keywordDateResolver.setEnv(env);
        keywordDateResolver.setExpr("(NOW-15J)");
        assertEquals("DATE '2011-06-20'", keywordDateResolver.solve(null));
        keywordDateResolver = new KeywordUfnxqlDateResolver();
        keywordDateResolver.setEnv(env);
        keywordDateResolver.setExpr("(NOW-25J)");
        assertEquals("DATE '2011-06-10'", keywordDateResolver.solve(null));
    }

    public void testMatche() {
        String expr = "((q.qu:numeroQuestion = '4' AND q.qu:datePublication < ufnxql_date:(NOW-15J)))";
        Pattern pattern = Pattern.compile("ufnxql_date:\\((.*?)\\)");
        Matcher matcher = pattern.matcher(expr);
        matcher.find();
        String groupStr = "";
        groupStr = matcher.group(1);
        assertEquals("NOW-15J", groupStr);
        KeywordUfnxqlDateResolver solver = new KeywordUfnxqlDateResolver();
        Map<String, Object> env = new HashMap<String, Object>();
        env.put(NOW, new DateTime(2011, 7, 5, 10, 36, 0, 0));
        solver.setEnv(env);
        solver.setExpr(groupStr);
        assertEquals("DATE '2011-06-20'", solver.solve(null));
        expr = expr.replace("ufnxql_date:(" + groupStr + ")", solver.solve(null));
        assertEquals("((q.qu:numeroQuestion = '4' AND q.qu:datePublication < DATE '2011-06-20'))", expr);
    }

    /**
     * Un essai avec la méthode qui va utiliser le solveur.
     */
    public void testMatcheMinusRealLife() {
        KeywordUfnxqlDateResolver date_solver = new KeywordUfnxqlDateResolver();
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("NOW", new DateTime(2011, 7, 5, 10, 36, 0, 0));
        String query =
            "ufnxql:SELECT q.ecm:uuid AS id FROM Question AS q WHERE ((q.qu:numeroQuestion = '4' AND q.qu:datePublication < ufnxql_date:(NOW-1M)))";

        String queryResult = RequeteurFunctionSolverHelper.apply(date_solver, null, query, env);
        assertEquals(
            "ufnxql:SELECT q.ecm:uuid AS id FROM Question AS q WHERE ((q.qu:numeroQuestion = '4' AND q.qu:datePublication < DATE '2011-06-05'))",
            queryResult
        );
    }

    /**
     * Un essai avec la méthode qui va utiliser le solveur.
     */
    public void testMatchePlusRealLife() {
        KeywordUfnxqlDateResolver date_solver = new KeywordUfnxqlDateResolver();
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("NOW", new DateTime(2011, 7, 5, 10, 36, 0, 0));
        String query =
            "ufnxql:SELECT q.ecm:uuid AS id FROM Question AS q WHERE ((q.qu:numeroQuestion = '4' AND q.qu:datePublication < ufnxql_date:(NOW+1M)))";

        String queryResult = RequeteurFunctionSolverHelper.apply(date_solver, null, query, env);
        assertEquals(
            "ufnxql:SELECT q.ecm:uuid AS id FROM Question AS q WHERE ((q.qu:numeroQuestion = '4' AND q.qu:datePublication < DATE '2011-08-05'))",
            queryResult
        );
    }

    /**
     * Un essai avec la méthode qui va utiliser le solveur.
     */
    public void testMultiMatchesRealLife() {
        KeywordUfnxqlDateResolver date_solver = new KeywordUfnxqlDateResolver();
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("NOW", new DateTime(2011, 7, 5, 10, 36, 0, 0));
        String query =
            "ufnxql:SELECT q.ecm:uuid AS id FROM Question AS q WHERE ((q.qu:numeroQuestion = '4' AND q.qu:datePublication BETWEEN ufnxql_date:(NOW-1M) AND ufnxql_date:(NOW-1J)))";

        String queryResult = RequeteurFunctionSolverHelper.apply(date_solver, null, query, env);
        assertEquals(
            "ufnxql:SELECT q.ecm:uuid AS id FROM Question AS q WHERE ((q.qu:numeroQuestion = '4' AND q.qu:datePublication BETWEEN DATE '2011-06-05' AND DATE '2011-07-04'))",
            queryResult
        );
    }
}
