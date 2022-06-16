package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.IntegerLiteral;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.Reference;
import org.nuxeo.ecm.core.query.sql.model.StringLiteral;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Join;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.DialectPostgreSQL;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.DocumentData;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.Field;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.QueryBuilder;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.UFNXQLConstants;

/**
 * Fonction pour permettre de faire des requetes fulltext exploitant le moteur postgresql
 * 
 *
 */
public class PgFulltextFunction implements UfnxqlFunction {
	
	private static final Log LOG = LogFactory.getLog(PgFulltextFunction.class);
	
	/**
	 * Default constructor
	 */
	public PgFulltextFunction(){
		// do nothing
	}
	
	@Override
	public String getName() {
		return UFNXQLConstants.PGFULLTEXT_FUNCTION;
	}

	@Override
	public String getExpression() {
		return UFNXQLConstants.PGFULLTEXT_FUNCTION + "(id, 'fulltext query', 'index') = 1";
	}

	@Override
	public boolean checkExpression(QueryBuilder queryBuilder, Expression expr) {
		return Operator.EQ.equals(expr.operator) && expr.rvalue instanceof IntegerLiteral && (((IntegerLiteral) expr.rvalue).value == 1);
	}

	/**
	 * Recherche fulltext spécifique à PostgreSQL.
	 * 
	 * pgfulltext(doc.ecm:uuid, 'fulltext query'[, 'fulltext_index'])
	 * 
	 * @param func
	 *          Fonction UFNXQL.
	 */
	@Override
	public void visitFunction(QueryBuilder queryBuilder, Function func) {

		if (!queryBuilder.isInWhere() || StringUtils.isEmpty(func.name)) {
			throw new QueryParseException("function " + UFNXQLConstants.PGFULLTEXT_FUNCTION + " must be in where clause");
		}

		if (func.args == null || func.args.size() < 2 || func.args.size() > 3 || func.args.get(0) == null || !(func.args.get(0) instanceof Reference)) {
			throw new QueryParseException("function " + UFNXQLConstants.PGFULLTEXT_FUNCTION + " expects 2 or 3 arguments, with first one referencing a table");
		}

		Reference fulltextTableRef = (Reference) func.args.get(0);
		Field fulltextField = queryBuilder.getFieldMap().get(fulltextTableRef.name);
		String fulltextQuery = func.args.get(1).toString();
		if (fulltextQuery.equals("?")) {
			fulltextQuery = (String) queryBuilder.getAvailableParams(queryBuilder.getNbParamsRequired());
			queryBuilder.incrNbParamsRequired(1);
		}

		String fulltextIndexSuffix = "";
		if (func.args.size() >= 3 && func.args.get(2) != null) {
			StringLiteral fulltextIndexLiteral = (StringLiteral) func.args.get(2);
			if (!"default".equals(fulltextIndexLiteral.asString())) {
				fulltextIndexSuffix = "_" + fulltextIndexLiteral.asString();
			}
		}

		DocumentData docData = queryBuilder.getDocumentDataMap().get(fulltextField.getPrefix());
		String mainColumn = docData.getReferenceTableKey().getKey() + "." + docData.getReferenceTableColumnIdName();
		String pgfulltextAlias = buildPgFulltextAlias(queryBuilder.getPgfulltextAliasNumber());
		queryBuilder.incrPgfulltextAliasNumber();

		Table fulltextTable = queryBuilder.getSqlInfo().database.getTable(Model.FULLTEXT_TABLE_NAME);
		Column fulltextMainColumn = fulltextTable.getColumn(Model.MAIN_KEY);
		String fulltextAnalyzer = (String) fulltextTable.getDialect().getSQLStatementsProperties(queryBuilder.getModel(), queryBuilder.getSqlInfo().database).get("fulltextAnalyzer");

		Column fulltextIndexColumn = fulltextTable.getColumn(Model.FULLTEXT_FULLTEXT_KEY + fulltextIndexSuffix);
		String fulltextLegacyField;
		if (fulltextIndexColumn == null) {
			LOG.warn(String.format("Unknown fulltext index [%s], using default", Model.FULLTEXT_FULLTEXT_KEY + fulltextIndexSuffix));
			fulltextIndexColumn = fulltextTable.getColumn(Model.FULLTEXT_FULLTEXT_KEY);
			fulltextLegacyField = NXQL.ECM_FULLTEXT;
		} else {
			fulltextLegacyField = NXQL.ECM_FULLTEXT + fulltextIndexSuffix;
		}

		if (!(queryBuilder.getSqlInfo().dialect instanceof DialectPostgreSQL)) {
			LOG.warn("Trying to use function " + UFNXQLConstants.PGFULLTEXT_FUNCTION + " without PostgreSQL dialect, switching to default fulltext");

			Expression expression = new Expression(null, Operator.EQ, new StringLiteral(fulltextQuery));
			Field field = new Field(fulltextField.getPrefix(), fulltextLegacyField, queryBuilder.isInSelect());
			queryBuilder.processExpressionFullText(expression, field);

			return;
		}

		docData.getJoins().add(new Join(Join.INNER, Model.FULLTEXT_TABLE_NAME, null, null, fulltextMainColumn.getFullQuotedName(), mainColumn));
		docData.getJoins().add(new Join(Join.IMPLICIT, "TO_TSQUERY('" + fulltextAnalyzer + "', ?)", pgfulltextAlias, fulltextQuery, (String) null, null));

		queryBuilder.getWherePart().append(String.format(" " + pgfulltextAlias + " @@ NX_TO_TSVECTOR(%s) ", fulltextIndexColumn.getFullQuotedName()));
	}

	public static String buildPgFulltextAlias(int nb){
		return "pgfulltext" + nb;
	}
	
}
