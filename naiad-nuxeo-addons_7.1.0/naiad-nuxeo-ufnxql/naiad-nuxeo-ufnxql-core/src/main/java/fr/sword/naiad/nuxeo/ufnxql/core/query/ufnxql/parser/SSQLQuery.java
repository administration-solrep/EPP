package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser;

import org.nuxeo.ecm.core.query.sql.model.SQLQuery;

public class SSQLQuery extends SQLQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final SQLQuery query1;
	
	private final SQLQuery query2;
	
	public SSQLQuery(SQLQuery query1, SQLQuery query2){
		super();
		this.query1 = query1;
		this.query2 = query2;
	}

	public SQLQuery getQuery1() {
		return query1;
	}

	public SQLQuery getQuery2() {
		return query2;
	}
	
	
	
}
