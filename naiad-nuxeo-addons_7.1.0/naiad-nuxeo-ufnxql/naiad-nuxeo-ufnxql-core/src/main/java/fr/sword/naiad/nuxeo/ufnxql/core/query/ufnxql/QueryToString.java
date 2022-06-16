package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.query.sql.model.Operand;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;
import org.nuxeo.ecm.core.query.sql.model.SelectClause;
import org.nuxeo.ecm.core.query.sql.model.SelectList;
import org.nuxeo.ecm.core.query.sql.model.WhereClause;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SDefaultQueryVisitor;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SFromClause;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SFromList;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SGroupByClause;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SGroupByList;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SSQLQuery;

/**
 * Visitor utiliser pour transformer une requete parse en string
 */
public class QueryToString extends SDefaultQueryVisitor {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private final StringBuilder strBuilder;
    
    public QueryToString(){
    	super();
        strBuilder = new StringBuilder();
    }
    
    @Override
    public void visitSParamLiteral() {
        strBuilder.append('?');
    }

    @Override
    public void visitSGroupByClause(SGroupByClause clause){
        SGroupByList list = clause.getGroupByList();
        if(!list.isEmpty()){
            strBuilder.append(" GROUP BY ");
            list.get(0).accept(this);
            for(int i = 1; i < list.size(); ++i){
                strBuilder.append(',');
                list.get(i).accept(this);
            }
        }
    }

    @Override
    public void visitSelectClause(SelectClause node) {
        SelectList elements = node.elements;
        strBuilder.append("SELECT ");
        boolean first = true;
        for(Entry<String, Operand> entry : elements.entrySet()){
        	if(first){
        		first = false;
        	} else {
        		strBuilder.append(", ");	
        	}            
            strBuilder.append(entry.getValue().toString());
        }
    }
    
    @Override
    public void visitSFromClause(SFromClause node) {
        strBuilder.append(" FROM ");
    	boolean first = true;
    	for(SFromList.AliasElementList akl : node.getFromList().getFromList()){
    		if(first){
				first = false;
			} else {
				strBuilder.append(", ");
			}
    		final String alias = akl.getAlias();
    		if(akl instanceof SFromList.AliasKeyList){
                String name = ((SFromList.AliasKeyList) akl).getKeylist().toString();                
                appendFromTable(name, alias);
    		} else if(akl instanceof SFromList.AliasQueryList){
    			strBuilder.append("(");
    			((SFromList.AliasQueryList) akl).getQuery().accept(this);
    			strBuilder.append(")");
    			if(!StringUtils.isEmpty(alias)){
    				strBuilder.append(" AS ").append(alias);	
    			}    			
    		} else {
    			throw new UnsupportedOperationException("class [" + akl.getClass() + "] not supported");
    		}
        }
    }
    
    protected void appendFromTable(String name, String alias){
        if(StringUtils.isEmpty(alias) || name.equals(alias)){
            strBuilder.append(name);
        } else {
            strBuilder.append(name).append(" AS ").append(alias);
        }
    }
    
    @Override
    public void visitWhereClause(WhereClause node) {
        strBuilder.append(" WHERE ").append(node);
    }
    
    @Override
    public void visitSSQLQuery(SSQLQuery node){
    	node.getQuery1().accept(this);
    	strBuilder.append(" UNION ");
    	node.getQuery2().accept(this);
    }
    
    public static String queryToString(SQLQuery query){
        QueryToString qts = new QueryToString();
        query.accept(qts);
        return qts.strBuilder.toString();
    }
}
