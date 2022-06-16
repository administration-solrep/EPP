package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql;

/**
 * Constante utilisée pour générer les requêtes SQL
 * 
 * @author SPL 
 *
 */
public final class SQL {
	
    public static final String SELECT = "SELECT";
    public static final String FROM = "FROM";
    public static final String WHERE = "WHERE";
    public static final String ORDERBY = "ORDER BY";
    public static final String GROUPBY = "GROUP BY";
    
    public static final String DISTINCT = "DISTINCT";
    public static final String COUNT = "COUNT";
    
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String AS = "AS";
    public static final String LIKE = "LIKE";
    public static final String IN = "IN";
    public static final String NOT = "NOT";
    
    public static final String EXISTS = "EXISTS";  
    
    public static final String STARTWITH = "START WITH";
    public static final String CONNECTBYPRIOR = "CONNECT BY PRIOR";
     
    
    public static final String REC_EXPR = " " + STARTWITH + " %s " + IN + " ( %s ) " + CONNECTBYPRIOR + " %s = %s";
    
    
    /** 
     * utility class
     */
    private SQL(){
    	// do nothing
    }
    
    /**
     * Generate recursive expression
     * 
     * exemple : START WITH &lt;id&gt; IN (&lt;un ensemble d'id&gt;) CONNECT BY PRIOR id = parentId;
     * 
     * genRecExpr(&lt;un ensemble d'id&gt;, &lt;id&gt;, &lt;parentId&gt;)
     * 
     * @param clause ensemble d'id ou query
     * @param exprChild
     * @param exprParent
     * @return
     */
    public static String genRecExpr(String clause, String exprChild, String exprParent){
        return String.format(REC_EXPR, exprChild, clause, exprChild, exprParent);
    }
    
    /**
     * return [NOT] EXISTS( ... )
     * @param content contenu du exists
     * @param exist si il est faux ajout du NOT devant le exist
     * @return
     */
    public static String existPart(String content, boolean exist){
        StringBuilder strBuilder = new StringBuilder();
        if(!exist){
           strBuilder.append(NOT).append(" "); 
        }
        strBuilder.append(EXISTS).append("(").append(content).append(")");
        return strBuilder.toString();
    }
    
   
}
