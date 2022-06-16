package fr.sword.naiad.nuxeo.ufnxql.core.query;

import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.Function;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.QueryBuilder;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions.UfnxqlFunction;

public class TestUfnxqlFunction implements UfnxqlFunction {

	@Override
	public String getName() {
		return "test1";
	}

	@Override
	public String getExpression() {		
		return getName() + "() = 1";
	}

	@Override
	public boolean checkExpression(QueryBuilder queryBuilder, Expression expr) {
		return true;
	}

	@Override
	public void visitFunction(QueryBuilder queryBuilder, Function func) {
		queryBuilder.getWherePart().append(QueryBuilder.DUMMY_EXPRESSION);
		
	}

}
