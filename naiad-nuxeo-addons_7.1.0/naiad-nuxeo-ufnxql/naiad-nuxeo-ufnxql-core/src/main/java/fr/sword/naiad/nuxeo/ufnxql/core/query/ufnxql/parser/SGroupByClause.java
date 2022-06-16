package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser;

import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.GroupByClause;
import org.nuxeo.ecm.core.query.sql.model.IVisitor;

/**
 * Conteneur de la clause Group by
 *
 */
public class SGroupByClause extends GroupByClause{

    /**
     * 
     */
    private static final long serialVersionUID = 6270537941085810344L;
    
    private final SGroupByList list;
    
    public SGroupByClause(SGroupByList list){
    	super();
        this.list = list;
    }
    
    @Override
    public void accept(IVisitor visitor) {
        if(visitor instanceof UFNXQLVisitor){
            ((UFNXQLVisitor) visitor).visitSGroupByClause(this);
        } else {            
            throw new QueryParseException("SGroupByClause not supported by visitor");
        }
    }
    
    public SGroupByList getGroupByList(){
    	return list;
    }
    
}
