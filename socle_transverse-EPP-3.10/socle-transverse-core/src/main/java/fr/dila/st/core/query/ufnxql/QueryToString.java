package fr.dila.st.core.query.ufnxql;

import org.nuxeo.ecm.core.query.sql.model.FromClause;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;
import org.nuxeo.ecm.core.query.sql.model.SelectClause;
import org.nuxeo.ecm.core.query.sql.model.SelectList;
import org.nuxeo.ecm.core.query.sql.model.WhereClause;

import fr.dila.st.core.query.ufnxql.parser.SDefaultQueryVisitor;
import fr.dila.st.core.query.ufnxql.parser.SGroupByClause;
import fr.dila.st.core.query.ufnxql.parser.SGroupByList;

/**
 * Visitor utiliser pour transformer une requete parse en string
 */
public class QueryToString extends SDefaultQueryVisitor {

	/**
     * 
     */
	private static final long	serialVersionUID	= 1L;

	private StringBuilder		strBuilder;

	public QueryToString() {
		super();
		strBuilder = new StringBuilder();
	}

	@Override
	public void visitSParamLiteral() {
		strBuilder.append('?');
	}

	@Override
	public void visitSGroupByClause(SGroupByClause clause) {
		SGroupByList l = clause.list;
		if (!l.isEmpty()) {
			strBuilder.append(" GROUP BY ");
			l.get(0).accept(this);
			for (int i = 1; i < l.size(); ++i) {
				strBuilder.append(',');
				l.get(i).accept(this);
			}
		}
	}

	@Override
	public void visitSelectClause(SelectClause node) {
		SelectList elements = node.elements;
		strBuilder.append("SELECT ");
		strBuilder.append(elements.get(0).toString());
		for (int i = 1; i < elements.size(); i++) {
			strBuilder.append(", ");
			strBuilder.append(elements.get(i).toString());
		}
	}

	@Override
	public void visitFromClause(FromClause node) {
		strBuilder.append(" FROM ");
		if (node.count() > 0) {
			appendFromTable(node.get(0), node.getAlias(0));
			for (int i = 1; i < node.count(); ++i) {
				strBuilder.append(", ");
				String name = node.get(i);
				String alias = node.getAlias(i);
				appendFromTable(name, alias);
			}
		}
	}

	protected void appendFromTable(String name, String alias) {
		if (name.equals(alias)) {
			strBuilder.append(name);
		} else {
			strBuilder.append(name).append(" AS ").append(alias);
		}
	}

	@Override
	public void visitWhereClause(WhereClause node) {
		strBuilder.append(" WHERE ").append(node);
	}

	public static String queryToString(SQLQuery q) {
		QueryToString qts = new QueryToString();
		q.accept(qts);
		return qts.strBuilder.toString();
	}
}
