package fr.dila.st.core.query.ufnxql.parser;

import java.util.ArrayList;

import org.nuxeo.ecm.core.query.sql.model.Reference;

public class SGroupByList extends ArrayList<Reference> {

	/**
     * 
     */
	private static final long	serialVersionUID	= 1L;

	public SGroupByList(Reference r) {
		add(r);
	}

}
