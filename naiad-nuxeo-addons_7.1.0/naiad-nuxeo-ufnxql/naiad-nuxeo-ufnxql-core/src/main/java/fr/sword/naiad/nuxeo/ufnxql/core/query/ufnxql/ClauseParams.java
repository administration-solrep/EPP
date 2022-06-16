package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains a string representing a query that can contains 
 * parameter representeed by '?'
 * and the list of parameter present in the string
 *  
 * @author spesnel
 */
public class ClauseParams {
    private String clause;
    
    private List<Serializable> params;
    
    public ClauseParams(String clause, List<Serializable> params){
        this.clause = clause;
        this.params = params;
    }
    
    public ClauseParams(){
        this("", new ArrayList<Serializable>());
    }
    
    public String getClause(){
    	return clause;
    }
    
    public List<Serializable> getParams(){
    	return params;
    }
    
    public void setClause(String clause){
    	this.clause = clause;
    }
    
    public void setParams(List<Serializable> params){
    	this.params = params;
    }
    
    public boolean hasNotEmptyClause(){
    	return !clause.isEmpty();
    }
}
