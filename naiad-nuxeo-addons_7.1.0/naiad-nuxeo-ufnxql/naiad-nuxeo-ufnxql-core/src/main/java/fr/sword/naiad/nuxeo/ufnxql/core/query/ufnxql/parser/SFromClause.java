package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser;

import org.nuxeo.ecm.core.query.sql.model.FromClause;

public class SFromClause extends FromClause {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3516713892587258076L;
	
	private final SFromList fromList;
	
	public SFromClause(SFromList fromList){
		super();
		this.fromList = fromList;
	}

	public SFromList getFromList() {
		return fromList;
	}
	
	
	
}
