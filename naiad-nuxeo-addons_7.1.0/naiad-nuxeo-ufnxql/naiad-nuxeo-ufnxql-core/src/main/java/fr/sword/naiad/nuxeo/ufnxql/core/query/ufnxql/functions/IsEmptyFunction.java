package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions;

import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.IntegerLiteral;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.Reference;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.DocumentData;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.Field;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.QueryBuilder;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.TableKey;

/**
 * Fonction ufnxql qui permet de tester si une propriété correspondant à une liste de valeur
 * est vide ou non
 * @author SPL
 *
 */
public class IsEmptyFunction implements UfnxqlFunction {

	public static final String NAME = "isEmpty";
	
	private Boolean isEmpty = null;
	
	/**
	 * Default constructor
	 */
	public IsEmptyFunction(){
		// do nothing
	}
	
	@Override
	public boolean checkExpression(QueryBuilder builder, Expression expr) {
		isEmpty = null;
		if(Operator.EQ.equals(expr.operator) 
				&& expr.rvalue instanceof IntegerLiteral 
				&& (((IntegerLiteral) expr.rvalue).value == 0
					|| ((IntegerLiteral) expr.rvalue).value == 1)){
			isEmpty = ((IntegerLiteral) expr.rvalue).value == 1;
			return true;			
		} else { 
			return false;
		}
	}

	@Override
	public String getExpression() {
		return NAME + "(list prop) = 1/0";
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void visitFunction(QueryBuilder queryBuilder, Function func) {
		if (!((func.args.size() == 1 && func.args.get(0) instanceof Reference))){
			throw new QueryParseException("function " + NAME + " need ref");
		}
        
        Reference ref = (Reference) func.args.get(0);
        Field field = queryBuilder.getFieldMap().get(ref.name);
        final DocumentData docData = queryBuilder.getDocumentDataMap().get(field.getPrefix());
        final Table table = queryBuilder.getSqlInfo().database.getTable(field.getModelProperty().fragmentName);
        final TableKey refTk = docData.getReferenceTableKey();
        
        if(isEmpty){
        	queryBuilder.getWherePart().append("NOT ");
        }
        queryBuilder.getWherePart().append(String.format("EXISTS (SELECT 1 FROM %s WHERE %s = %s.%s)", table.getQuotedName(),
                docData.getReferenceTableColumnIdName(), refTk.getKey(), docData.getReferenceTableColumnIdName()));

	}

}
