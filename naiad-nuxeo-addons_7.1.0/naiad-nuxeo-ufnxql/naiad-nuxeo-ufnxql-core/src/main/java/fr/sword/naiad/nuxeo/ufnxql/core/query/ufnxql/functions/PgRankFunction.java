package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.StringLiteral;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.DialectPostgreSQL;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.QueryBuilder;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.UFNXQLConstants;

/**
 * Ranking fulltext spécifique à PostgreSQL.
 * 
 * pgrank('fulltext query'[, 'fulltext_index'[, fulltext_alias_number]])
 * 
 * Fonction UFNXQL.
 */
public class PgRankFunction implements UfnxqlFunction {
	
	private static final Log LOG = LogFactory.getLog(PgRankFunction.class); 

	/**
	 * Default constructor
	 */
	public PgRankFunction(){
		//do nothing
	}
	
	@Override
	public String getName() {
		return UFNXQLConstants.PGRANK_FUNCTION;
	}

	@Override
	public String getExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkExpression(QueryBuilder queryBuilder, Expression expr) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void visitFunction(QueryBuilder queryBuilder, Function func) {
		if (func.args == null || func.args.size() < 1 || func.args.size() > 3 || func.args.get(0) == null) {
			throw new QueryParseException("function " + UFNXQLConstants.PGRANK_FUNCTION + " expects 1, 2 or 3 arguments");
		}

		String fulltextQuery = func.args.get(0).toString();
		if ("?".equals(fulltextQuery)) {
			queryBuilder.incrNbParamsRequired(1);
		}

		if (!(queryBuilder.getSqlInfo().dialect instanceof DialectPostgreSQL)) {
			LOG.warn("Trying to use function " + UFNXQLConstants.PGRANK_FUNCTION + " without PostgreSQL dialect, ignoring this function");
			if(queryBuilder.isInSelect()){
				queryBuilder.getSelectPart().append("1");
			}			
			return;
		}

		int pgfulltextAliasNumberSuffix;
		if (func.args.size() >= 3 && func.args.get(2) != null) {
			pgfulltextAliasNumberSuffix = Integer.parseInt(func.args.get(2).toString());
		} else {
			pgfulltextAliasNumberSuffix = 1;
		}

		if(!queryBuilder.isInSelect()){
			if (pgfulltextAliasNumberSuffix >= queryBuilder.getPgfulltextAliasNumber()) {
				LOG.warn("Unknown fulltext alias number [" + pgfulltextAliasNumberSuffix + "], skipping " + UFNXQLConstants.PGRANK_FUNCTION);
				return;
			}
		}

		String pgfulltextAlias = PgFulltextFunction.buildPgFulltextAlias(pgfulltextAliasNumberSuffix);

		String fulltextIndexSuffix = "";
		if (func.args.size() >= 2 && func.args.get(1) != null) {
			StringLiteral fulltextIndexLiteral = (StringLiteral) func.args.get(1);
			if (!"default".equals(fulltextIndexLiteral.asString())) {
				fulltextIndexSuffix = "_" + fulltextIndexLiteral.asString();
			}
		}

		Table fulltextTable = queryBuilder.getSqlInfo().database.getTable(Model.FULLTEXT_TABLE_NAME);
		Column fulltextIndexColumn = fulltextTable.getColumn(Model.FULLTEXT_FULLTEXT_KEY + fulltextIndexSuffix);
		if (fulltextIndexColumn == null) {
			LOG.warn(String.format("Unknown fulltext index [%s], using default", Model.FULLTEXT_FULLTEXT_KEY + fulltextIndexSuffix));
			fulltextIndexColumn = fulltextTable.getColumn(Model.FULLTEXT_FULLTEXT_KEY);
		}

		String generated = String.format("TS_RANK(NX_TO_TSVECTOR(%s), %s)", fulltextIndexColumn.getFullQuotedName(), pgfulltextAlias);
		
		if(queryBuilder.isInOrder()){
			if (queryBuilder.getOrderPart().length() > 0) {
				queryBuilder.getOrderPart().append(", ");
			}
			queryBuilder.getOrderPart().append(generated);
			queryBuilder.getOrderPart().append(" DESC");
		} else if(queryBuilder.isInSelect()){
			queryBuilder.getSelectPart().append(generated);
		}
	}

}
