package fr.dila.st.core.query.ufnxql.parser;

import org.nuxeo.ecm.core.query.sql.model.DefaultQueryVisitor;

/**
 * Visitor that support SParamLiteral and SGroupByClause node
 * 
 * @author spesnel
 * 
 */
public class SDefaultQueryVisitor extends DefaultQueryVisitor implements UFNXQLVisitor {

	/**
     * 
     */
	private static final long	serialVersionUID	= 4018691814694141972L;

	@Override
	public void visitSParamLiteral() {
	}

	@Override
	public void visitSGroupByClause(SGroupByClause clause) {
		SGroupByList l = clause.list;
		for (int i = 0; i < l.size(); ++i) {
			l.get(i).accept(this);
		}
	}

}
