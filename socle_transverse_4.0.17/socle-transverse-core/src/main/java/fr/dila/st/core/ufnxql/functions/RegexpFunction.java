package fr.dila.st.core.ufnxql.functions;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.Field;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.QueryBuilder;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions.UfnxqlFunction;
import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.IntegerLiteral;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.Reference;
import org.nuxeo.ecm.core.query.sql.model.StringLiteral;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.DialectOracle;

/**
 * Fonction pour permettre d'utiliser la fonction ORACLE REGEXP_LIKE
 * <p>
 * S'utilise comme la fonction ORACLE <br>
 * regexpLike(f.dc:title, '^[a-z0-9].$') <br>
 * regexpLike(f.dc:title, '^[a-z0-9].$', 'x')
 *
 * @author SCE
 *
 */
public class RegexpFunction implements UfnxqlFunction {
    private static final String REGEXP_LIKE_FUNCTION = "regexpLike";

    @Override
    public String getName() {
        return REGEXP_LIKE_FUNCTION;
    }

    @Override
    public String getExpression() {
        return REGEXP_LIKE_FUNCTION + "(xpath, 'regexp') = 1";
    }

    @Override
    public boolean checkExpression(final QueryBuilder queryBuilder, final Expression expr) {
        return (
            Operator.EQ.equals(expr.operator) &&
            expr.rvalue instanceof IntegerLiteral &&
            ((IntegerLiteral) expr.rvalue).value == 1
        );
    }

    @Override
    public void visitFunction(final QueryBuilder queryBuilder, final Function func) {
        if (!(has2CorrectArgs(func) || has3CorrectArgs(func)) && queryBuilder.isInWhere()) {
            throw new QueryParseException(
                "function regexpLike expects 2 or 3 arguments : regexpLike(xpath, 'regexp'), regexpLike(xpath, 'regexp', 'x')"
            );
        }

        if (!(queryBuilder.getSqlInfo().dialect instanceof DialectOracle)) {
            queryBuilder.getWherePart().append(QueryBuilder.DUMMY_EXPRESSION);
            return;
        }

        final Reference ref = (Reference) func.args.get(0);
        final Field field = queryBuilder.getFieldMap().get(ref.name);
        final String id = field.toSql();

        int nbArgs = func.args.size();
        if (nbArgs == 2) {
            queryBuilder.getWherePart().append(String.format("REGEXP_LIKE(%s, %s)", id, func.args.get(1)));
        } else {
            queryBuilder
                .getWherePart()
                .append(String.format("REGEXP_LIKE(%s, %s, %s)", id, func.args.get(1), func.args.get(2)));
        }
    }

    private boolean has2CorrectArgs(final Function func) {
        return (
            func.args.size() == 2 && func.args.get(0) instanceof Reference && func.args.get(1) instanceof StringLiteral
        );
    }

    private boolean has3CorrectArgs(final Function func) {
        return (
            func.args.size() == 3 &&
            func.args.get(0) instanceof Reference &&
            func.args.get(1) instanceof StringLiteral &&
            func.args.get(2) instanceof StringLiteral
        );
    }
}
