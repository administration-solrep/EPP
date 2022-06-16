package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions;

import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.IntegerLiteral;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.ClauseParams;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.QueryBuilder;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.SQL;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.UFNXQLConstants;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SFunction;

/**
 * Fonction exist
 * 
 * exist(query) = 0 ou 1
 * 1 if exist, 0 =&gt; not exist
 *  
 */
public class ExistFunction implements UfnxqlFunction {

	/**
	 * true => appel a la fonction exist fait un EXIST
	 * false => appel a la fonction exist fait un 'NOT EXIST'
	 * 
	 * positionner par test de la valeur de la fonction exist(...) = (0 ou 1)
	 */
	private Boolean doExist = null;
	
	/**
	 * Default constructor
	 */
	public ExistFunction(){
		// do nothing
	}
	
	@Override
	public String getName() {
		return UFNXQLConstants.TEST_EXIST_FUNCTION;
	}

	@Override
	public String getExpression() {
		return UFNXQLConstants.TEST_EXIST_FUNCTION + "(id) = 0 ou 1";
	}

	@Override
	public boolean checkExpression(QueryBuilder queryBuilder, Expression expr) {
		doExist = null;
		if(Operator.EQ.equals(expr.operator) && expr.rvalue instanceof IntegerLiteral && (((IntegerLiteral) expr.rvalue).value == 1 || ((IntegerLiteral) expr.rvalue).value == 0)){
			doExist = ((IntegerLiteral) expr.rvalue).value == 1;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void visitFunction(QueryBuilder queryBuilder, Function func) {
	
		if (!(func instanceof SFunction && ((SFunction) func).getQuery() != null && (func.args == null || func.args.isEmpty()) && queryBuilder.isInWhere())) {
			throw new QueryParseException("function " + UFNXQLConstants.TEST_EXIST_FUNCTION + " expects 0 argument and a query and should be in where clause");
		}

		SQLQuery sqlQuery = ((SFunction) func).getQuery();

		ClauseParams internalClause = new ClauseParams();
		queryBuilder.incrNbParamsRequired(queryBuilder.computeInternalQuery(sqlQuery, internalClause));

		queryBuilder.getWherePart().append(SQL.existPart(internalClause.getClause(), doExist));
		queryBuilder.getWhereParams().addAll(internalClause.getParams());

	}
	
	
}
