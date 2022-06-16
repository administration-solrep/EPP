package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser;

import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.DefaultQueryVisitor;
import org.nuxeo.ecm.core.query.sql.model.FromClause;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;

/**
 * Visitor that support SParamLiteral and SGroupByClause node
 * @author spesnel
 *
 */
public class SDefaultQueryVisitor extends DefaultQueryVisitor implements UFNXQLVisitor{

    /**
     * 
     */
    private static final long serialVersionUID = 4018691814694141972L;  

    /**
     * Default constructor
     */
    public SDefaultQueryVisitor(){
    	super();
    }
    
    @Override
    public void visitSParamLiteral() {
    	// rien à faire : visit de ?
    }

    @Override
    public void visitSGroupByClause(SGroupByClause clause){
        SGroupByList list = clause.getGroupByList();
        for(int i = 0; i < list.size(); ++i){
            list.get(i).accept(this);
        }
    }
    
    
    @Override
    public void visitFromClause(FromClause clause){
    	if(clause instanceof SFromClause){
    		visitSFromClause((SFromClause) clause);
    	} else {
    		throw new QueryParseException("Internal Error : No support for FromClause : expect SFromClause");
    	}
    }
    
    @Override
    public void visitSFromClause(SFromClause node) {
    	// no default action
    }
    
    @Override
    public void visitQuery(SQLQuery query){
    	if(query instanceof SSQLQuery){
    		visitSSQLQuery((SSQLQuery) query);
    	} else {
    		super.visitQuery(query);
    	}
    }
    
    @Override
    public void visitSSQLQuery(SSQLQuery query){
    	// no default action
    }
    
    
}
