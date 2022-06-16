package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql;

import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.IVisitor;
import org.nuxeo.ecm.core.query.sql.model.Literal;


/**
 * Boolean literal for internal use in QueryBuilder
 * @author spesnel
 *
 */
class BooleanLiteral extends Literal {

    private static final long serialVersionUID = 1L;

    private final boolean value;

    public BooleanLiteral(boolean value) {
    	super();
        this.value = value;
    }

    @Override
    public void accept(IVisitor visitor) {
    	 if(visitor instanceof QueryBuilder){
             ((QueryBuilder) visitor).visitBooleanLiteral(this);
         } else {            
             throw new QueryParseException("BooleanLiteral not supported by visitor");
         }
    }

    @Override
    public String asString() {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BooleanLiteral) {
            return value == ((BooleanLiteral) obj).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Boolean.valueOf(value).hashCode();
    }
    
    public boolean getValue(){
    	return value;
    }

}
