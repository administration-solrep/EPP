package fr.dila.st.core.query.ufnxql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains a string representing a query that can contains parameter representeed by '?' and the list of parameter
 * present in the string
 * 
 * @author spesnel
 */
public class ClauseParams {
	public String				clause;
	public List<Serializable>	params;

	public ClauseParams(String clause, List<Serializable> params) {
		this.clause = clause;
		this.params = params;
	}

	public ClauseParams() {
		this("", new ArrayList<Serializable>());
	}
}
