package fr.dila.st.core.requeteur;

import fr.dila.st.api.requeteur.RequeteurFunctionSolver;
import fr.dila.st.core.util.DateUtil;
import java.util.Map;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Permet de calculer la fonction nxql_date pour le requeteur. Renvoie une date
 * qui est la date du jour moins une certaine durée, exprimée en jour. Usage :
 * q.qu:datePublicationJO &lt; ufnxql_date:(NOW-15J)
 *
 * @author jgomez
 *
 */
public class KeywordUfnxqlDateResolver implements RequeteurFunctionSolver {
    // Le mot-clé de la fonction
    private static final String UFNXQL_DATE_KEYWORD = "ufnxql_date:";

    protected String expr;

    protected Map<String, Object> env;

    private enum Operation {
        PLUS("+") {

            @Override
            public DateTime execute(DateTime date, Period period) {
                return date.plus(period);
            }
        },

        MINUS("-") {

            @Override
            public DateTime execute(DateTime date, Period period) {
                return date.minus(period);
            }
        };

        private String symbol;

        Operation(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public abstract DateTime execute(DateTime date, Period period);
    }

    private enum PeriodSelector {
        /**
         * Jour
         */
        J() {

            @Override
            public Period getPeriod(Integer value) {
                return new Period().withDays(value);
            }
        },

        /**
         * Mois
         */
        M() {

            @Override
            public Period getPeriod(Integer value) {
                return new Period().withMonths(value);
            }
        },

        /**
         * Année
         */
        Y() {

            @Override
            public Period getPeriod(Integer value) {
                return new Period().withYears(value);
            }
        };

        public abstract Period getPeriod(Integer value);
    }

    public KeywordUfnxqlDateResolver() {
        super();
    }

    @Override
    public String getExpr() {
        return expr;
    }

    @Override
    public void setExpr(String expr) {
        this.expr = expr;
    }

    @Override
    public Map<String, Object> getEnv() {
        return env;
    }

    @Override
    public void setEnv(Map<String, Object> env) {
        this.env = env;
    }

    /**
     * L'expression est NOW-15J, NOW-1M Retourne la chaine de caractère à
     * substituer dans la requête.
     *
     * @return la date moins un certain nombre de jour.
     *
     */
    @Override
    public String solve(final CoreSession session) {
        String exprWhithoutBrace = expr.replaceAll("\\(", "").replaceAll("\\)", "");
        Operation op = findOp(exprWhithoutBrace);
        if (op != null) {
            String[] parameters = exprWhithoutBrace.split("\\" + op.getSymbol());
            String keyDate = parameters[0];
            int length = parameters[1].length();
            char timeUnit = parameters[1].charAt(length - 1);
            PeriodSelector periodSelector = PeriodSelector.valueOf(Character.toString(timeUnit));
            Integer value = Integer.parseInt(parameters[1].substring(0, length - 1));
            DateTime date = (DateTime) env.get(keyDate);
            DateTime resultDate = getDate(date, periodSelector, value, op);
            return "DATE " + DateUtil.convert(resultDate);
        }
        return "";
    }

    /**
     * Trouve l'opération - ou +
     *
     * @param exprWhithoutBrace
     * @return
     */
    private Operation findOp(String exprWhithoutBrace) {
        for (Operation op : Operation.values()) {
            if (expr.contains(op.getSymbol())) {
                return op;
            }
        }
        return null;
    }

    /**
     * Retourne le calcul de la date
     *
     * @param date
     * @param timeUnit
     *            'J' ou 'M' ou 'Y' pour jour ou mois ou année
     * @param value
     * @param op
     *            l'opération
     * @return
     */
    private DateTime getDate(DateTime date, PeriodSelector periodSelector, Integer value, Operation op) {
        Period period = periodSelector.getPeriod(value);
        DateTime resultDate = op.execute(date, period);
        return resultDate;
    }

    /**
     * Renvoie le pattern qui définit la fonction date.
     *
     * @return
     */
    @Override
    public Pattern getPattern() {
        Pattern pattern = Pattern.compile(UFNXQL_DATE_KEYWORD + "\\((.*?)\\)");
        return pattern;
    }

    /**
     * Renvoie une chaîne de caractère à matcher dans la requête.
     *
     * @param les
     *            arguments de la fonction
     * @return l'expression a repérer
     */
    @Override
    public String getExpressionToBeMatched(String groupStr) {
        return UFNXQL_DATE_KEYWORD + "(" + groupStr + ")";
    }
}
