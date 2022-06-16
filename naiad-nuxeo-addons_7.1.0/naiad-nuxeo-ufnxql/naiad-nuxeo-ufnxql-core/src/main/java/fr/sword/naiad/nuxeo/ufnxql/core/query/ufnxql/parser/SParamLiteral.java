package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser;

import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.IVisitor;
import org.nuxeo.ecm.core.query.sql.model.Literal;


/**
 * represent marker for parameter in query
 * 
 * @author spesnel
 */
public class SParamLiteral extends Literal {

	public static final String PARAM_MARKER = "?";
	
    /**
     * 
     */
    private static final long serialVersionUID = -8173054899922275377L;

    /**
     * Default constructor
     */
    public SParamLiteral(){
    	super();
    }
    
    @Override
    public String toString() {
        return PARAM_MARKER;
    }

    @Override
    public void accept(IVisitor visitor) {
        if(visitor instanceof UFNXQLVisitor){
            ((UFNXQLVisitor) visitor).visitSParamLiteral();    
        } else {
            throw new QueryParseException("visitor does not support SParamLiteral");
        }
    }

    @Override
    public String asString() {
        return PARAM_MARKER;
    }
    
}
