package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser;

import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.OperandList;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;

/**
 * @author  <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class SFunction extends Function {

	private static final int HASH_CODE_MAGIC_NUMBER = 31;
	
    private static final long serialVersionUID = -6107133982072616209L;

    private final SQLQuery query;

    public SFunction(String name) {
        this(name, null, null);
    }

    
    public SFunction(String name, OperandList args) {
        this(name, null, args);
    }
    
    public SFunction(String name, SQLQuery query) {
        this(name, query, null);
    }
    
    public SFunction(String name, SQLQuery query, OperandList args) {
        super(name, args);
        this.query = query;
    }

    @Override
    public String toString() {
    	if(args == null){
    		return name + "()";
    	} else {
    		return name + '(' + args + ')';
    	}
    }

    @Override
    public boolean equals(Object obj) {
		if (super.equals(obj) && (obj instanceof SFunction)) {
			SFunction sfunc = (SFunction) obj;
			if (query == null) {
				return sfunc.query == null;
			}
			return query.equals(sfunc.query);
		}
		return false;
    }

    @Override
    public int hashCode() {
    	int argHashCode = 0;
    	if(args != null){
    		argHashCode = args.hashCode();
    	}
        int result = name.hashCode();
        result = HASH_CODE_MAGIC_NUMBER * result + argHashCode;
        return result;
    }

    public SQLQuery getQuery(){
    	return query;
    }
}
