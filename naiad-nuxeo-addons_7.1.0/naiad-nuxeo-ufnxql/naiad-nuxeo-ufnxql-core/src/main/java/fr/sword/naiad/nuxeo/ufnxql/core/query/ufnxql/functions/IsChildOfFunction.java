package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions;

import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.IntegerLiteral;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.Reference;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.DialectOracle;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.ClauseParams;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.Field;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.QueryBuilder;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.SQL;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.UFNXQLConstants;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SFunction;

/**
 * Fonction pour tester si un document est un descendant d'un autre document
 * isChilOf(d.ecm:uuid, query)
 * 
 * query : requete pour lister les ancetres
 * id l'id du doc a tester
 */
public class IsChildOfFunction implements UfnxqlFunction {

	/**
	 * Default constructor
	 */
	public IsChildOfFunction(){
		// do nothing
	}
	
	@Override
	public String getName() {
		return UFNXQLConstants.IS_CHILD_OF_FUNCTION;
	}

	@Override
	public String getExpression() {
		return UFNXQLConstants.IS_CHILD_OF_FUNCTION + "(id, query) = 1";
	}

	@Override
	public boolean checkExpression(QueryBuilder queryBuilder, Expression expr) {
		return Operator.EQ.equals(expr.operator) && expr.rvalue instanceof IntegerLiteral && ((IntegerLiteral) expr.rvalue).value == 1;
	}

	@Override
	public void visitFunction(QueryBuilder queryBuilder, Function func) {

		if (!(func instanceof SFunction
		    && ((SFunction) func).getQuery() != null
		    && ((func.args.size() == 1 && func.args.get(0) instanceof Reference) || (func.args.size() == 2 && func.args.get(0) instanceof Reference && func.args.get(1) instanceof Reference)) && queryBuilder.isInWhere())) {
			throw new QueryParseException("function " + UFNXQLConstants.IS_CHILD_OF_FUNCTION
			    + " expects one or two arguments (: the id to test, [ and the parent id to navigate in hierarchy ]) and a query and should be in where clause");
		}
		SQLQuery sqlQuery = ((SFunction) func).getQuery();

		ClauseParams internalClause = new ClauseParams();
		queryBuilder.incrNbParamsRequired(queryBuilder.computeInternalQuery(sqlQuery, internalClause));

		// extrait la réference à l'id et extrait ou construit la reference au parentId
		// meme si ne sert que sous oracle.
		// Cela evite de decouvrir certaine erreur que sous oracle, les exceptions sont aussi lancé
		// sous h2
		int len = queryBuilder.getWherePart().length();
		func.args.get(0).accept(queryBuilder);
		String refToId = queryBuilder.getWherePart().substring(len);
		queryBuilder.getWherePart().delete(len, queryBuilder.getWherePart().length());

		String refToParentId = null;
		if (func.args.size() == 2) {
			len = queryBuilder.getWherePart().length();
			func.args.get(1).accept(queryBuilder);
			refToParentId = queryBuilder.getWherePart().substring(len);
			queryBuilder.getWherePart().delete(len, queryBuilder.getWherePart().length());
		} else {
			// test que la table concerne est bien hierarchy pour s'assurer que la colonne PARENT ID existe
			Reference ref = (Reference) func.args.get(0);
			Field field = queryBuilder.getFieldMap().get(ref.name);
			final Table hierTable = queryBuilder.getSqlInfo().database.getTable(Model.HIER_TABLE_NAME);
			if (!hierTable.getPhysicalName().equals(field.getTableKey().getTableName())) {
				throw new QueryParseException("field for recursive test should be ID in HIERARCHY TABLE [" + ref.name + "]."
				    + "This problem can appear with optimisation on some document types. Try to use isChildOf with three arguments (id, parentid, query)");
			}
			if (!refToId.contains(".\"ID\"")) {
				throw new QueryParseException("field for recursive test on path should contains ID [" + refToId + "]");
			}
			refToParentId = refToId.replace(".\"ID\"", ".\"PARENTID\"");
		}

		if (queryBuilder.getSqlInfo().dialect instanceof DialectOracle) {

			queryBuilder.getWherePart().append(QueryBuilder.DUMMY_EXPRESSION); // pour que l'expression soit valide avec les ET, OU
			queryBuilder.getRecStartWithPart().append(SQL.genRecExpr(internalClause.getClause(), refToId, refToParentId));
			queryBuilder.getRecStartWithParams().addAll(internalClause.getParams());

		} else {
			queryBuilder.getWherePart().append(SQL.EXISTS).append("(").append(SQL.SELECT).append(" 1 ").append(SQL.FROM).append(" (").append(internalClause.getClause());
			queryBuilder.getWhereParams().addAll(internalClause.getParams());
			queryBuilder.getWherePart().append(") TMPTABLE WHERE NX_IN_TREE(");
			queryBuilder.getWherePart().append(refToId);
			queryBuilder.getWherePart().append(", TMPTABLE.id) )");
		}

	}

}
