package fr.dila.st.core.query.ufnxql.parser;

import org.nuxeo.ecm.core.query.sql.model.GroupByClause;
import org.nuxeo.ecm.core.query.sql.model.IVisitor;
import org.nuxeo.ecm.core.storage.sql.jdbc.QueryMaker.QueryMakerException;

public class SGroupByClause extends GroupByClause {

	/**
     * 
     */
	private static final long	serialVersionUID	= 6270537941085810344L;

	public SGroupByList			list;

	public SGroupByClause(SGroupByList l) {
		this.list = l;
	}

	@Override
	public void accept(IVisitor visitor) {
		if (visitor instanceof UFNXQLVisitor) {
			((UFNXQLVisitor) visitor).visitSGroupByClause(this);
		} else {
			throw new QueryMakerException("SGroupByClause not supported by visitor");
		}
	}

}
