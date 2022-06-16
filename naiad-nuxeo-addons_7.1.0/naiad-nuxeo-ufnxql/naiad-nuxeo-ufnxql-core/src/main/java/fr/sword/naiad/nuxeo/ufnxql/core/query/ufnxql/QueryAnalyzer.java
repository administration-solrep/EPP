package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.DateLiteral;
import org.nuxeo.ecm.core.query.sql.model.DoubleLiteral;
import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.GroupByClause;
import org.nuxeo.ecm.core.query.sql.model.HavingClause;
import org.nuxeo.ecm.core.query.sql.model.IntegerLiteral;
import org.nuxeo.ecm.core.query.sql.model.Literal;
import org.nuxeo.ecm.core.query.sql.model.LiteralList;
import org.nuxeo.ecm.core.query.sql.model.MultiExpression;
import org.nuxeo.ecm.core.query.sql.model.Operand;
import org.nuxeo.ecm.core.query.sql.model.OperandList;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.OrderByClause;
import org.nuxeo.ecm.core.query.sql.model.OrderByExpr;
import org.nuxeo.ecm.core.query.sql.model.OrderByList;
import org.nuxeo.ecm.core.query.sql.model.Reference;
import org.nuxeo.ecm.core.query.sql.model.ReferenceList;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;
import org.nuxeo.ecm.core.query.sql.model.SelectClause;
import org.nuxeo.ecm.core.query.sql.model.StringLiteral;
import org.nuxeo.ecm.core.query.sql.model.WhereClause;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SDefaultQueryVisitor;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SFromClause;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SFromKeyList;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SFromList;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SGroupByClause;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SOrderByExpr;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SSQLQuery;

/**
 * Parcours la requete UFNXQL pour recuperer
 * les type de document manipulés et leur alias ainsi que les champs referencés
 * 
 * @author spesnel
 */
public class QueryAnalyzer extends SDefaultQueryVisitor {

    private static final Log LOG = LogFactory.getLog(QueryAnalyzer.class);
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private boolean isInSelect = false;
    
    private Map<String, DocumentData> documentDataMap;
    
    private Map<String, DocumentData> documentDataMapContext;
    
    public QueryAnalyzer(Map<String, DocumentData> documentDataMapContext){
    	super();
        this.documentDataMapContext = documentDataMapContext;
        documentDataMap = new HashMap<String, DocumentData>();
    }
    
    public QueryAnalyzer(){
        this(null);
    }

    @Override
    public void visitDateLiteral(DateLiteral l) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitDateLiteral("+l+")");
        }
        super.visitDateLiteral(l);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitDateLiteral("+l+")");
        }
    }

    @Override
    public void visitDoubleLiteral(DoubleLiteral l) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitDoubleLiteral("+l+")");
        }
        super.visitDoubleLiteral(l);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitDoubleLiteral("+l+")");
        }
        
    }

    @Override
    public void visitExpression(Expression expr) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitExpression("+expr+")");
        }
        super.visitExpression(expr);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitExpression("+expr+")");
        }
    }

    @Override
    public void visitSFromClause(SFromClause clause) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitSFromClause("+clause+")");
        }
        
        final List<SFromList.AliasElementList> fromkeylist = clause.getFromList().getFromList();
      
        for(SFromList.AliasElementList alk : fromkeylist){
        	if(alk instanceof SFromList.AliasKeyList){
        		addDocumentData(alk.getAlias(), ((SFromList.AliasKeyList) alk).getKeylist());
        	} else {
        		addSubqueryAsDocumentData(alk.getAlias());
        	}
        }
        
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitSFromClause("+clause+")");
        }
    }

    @Override
    public void visitFunction(Function func) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitFunction("+func+")");
        }
        if(func.args != null){
            for (Operand operand : func.args) {
                operand.accept(this);
            }
        }
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitFunction("+func+")");
        }
    }

    @Override
    public void visitGroupByClause(GroupByClause clause) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitGroupByClause("+clause+")");
        }
        super.visitGroupByClause(clause);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitGroupByClause("+clause+")");
        }
    }

    @Override
    public void visitHavingClause(HavingClause clause) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitHavingClause("+clause+")");
        }
        super.visitHavingClause(clause);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitHavingClause("+clause+")");
        }
    }

    @Override
    public void visitIntegerLiteral(IntegerLiteral l) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitIntegerLiteral("+l+")");
        }
        super.visitIntegerLiteral(l);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitIntegerLiteral("+l+")");
        }
        
    }

    @Override
    public void visitLiteral(Literal l) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitLiteral("+l+")");
        }
        super.visitLiteral(l);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitLiteral("+l+")");
        }
    }

    @Override
    public void visitLiteralList(LiteralList l) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitLiteralList("+l+")");
        }
        super.visitLiteralList(l);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitLiteralList("+l+")");
        }
    }

    @Override
    public void visitMultiExpression(MultiExpression expr) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitMultiExpression("+expr+")");
        }
        super.visitMultiExpression(expr);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitMultiExpression("+expr+")");
        }
    }

    @Override
    public void visitOperandList(OperandList opList) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitOperandList("+opList+")");
        }
        super.visitOperandList(opList);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitOperandList("+opList+")");
        }
    }

    @Override
    public void visitOperator(Operator op) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitOperator("+op+")");
        }
        super.visitOperator(op);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitOperator("+op+")");
        }
        
    }

    @Override
    public void visitOrderByClause(OrderByClause clause) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitOrderByClause("+clause+")");
        }
        super.visitOrderByClause(clause);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitOrderByClause("+clause+")");
        }
        
    }

    @Override
    public void visitOrderByExpr(OrderByExpr expr) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitOrderByExpr("+expr+")");
        }
        if (expr instanceof SOrderByExpr) {
        	((SOrderByExpr) expr).getOperand().accept(this);
        } else {
        	super.visitOrderByExpr(expr);
        }
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitOrderByExpr("+expr+")");
        }
    }

    @Override
    public void visitOrderByList(OrderByList list) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitOrderByList("+list+")");
        }
        super.visitOrderByList(list);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitOrderByList("+list+")");
        }
    }

    @Override
    public void visitQuery(SQLQuery query) {
    	
    	
    	
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitQuery("+query+")");
        }
        
        if(query instanceof SSQLQuery){
    		
        	//skip
        	
    	} else {
        
	        //  PROCESS FROM CLAUSE FIRST
	        // IN ORDER TO CHECK REFERENCE IN SELECT
	        query.from.accept(this);
	        
	        query.select.accept(this);
	        
	        if (query.where != null) {
	            query.where.accept(this);
	        }
	        if (query.orderBy != null) {
	            query.orderBy.accept(this);
	        }
	        if (query.groupBy != null) {
	            query.groupBy.accept(this);
	        }
	        if (query.having != null) {
	            query.having.accept(this);
	        }
    	}
        
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitQuery("+query+")");
        }
    }

    @Override
    public void visitReference(Reference ref) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitReference("+ref+")");
        }
        
        String name = ref.name;
        if(!name.contains(".")){
            throw new QueryParseException("Reference without document specification ["+name+"]");
        } else {
            String[] parts = name.split("\\.");
            if(parts.length <= 1){
                throw new QueryParseException("No support for less than one part. in reference ["+name+"] ("+parts.length+")");
            }
            String prefix = parts[0];
            // ecm:fulltext peut etre suivi par '.<field name>'
            // si il n'est pas prefixé le prefix sera ecm:fulltext au lieu de la reference a un document
            if(prefix.startsWith("ecm:fulltext")){
                throw new QueryParseException("ecm:fulltext should be prefixed by document reference ["+name+"]");
            }
            
            addField(prefix, name.substring(prefix.length() + 1 /* for the '.'*/), isInSelect);
        }
        
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitReference("+ref+")");
        }
    }

    @Override
    public void visitReferenceList(ReferenceList refList) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitReferenceList("+refList+")");
        }
        super.visitReferenceList(refList);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitReferenceList("+refList+")");
        }
        
    }

    @Override
    public void visitSelectClause(SelectClause clause) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitSelectClause("+clause+")");
        }
        isInSelect = true;
        super.visitSelectClause(clause);
        isInSelect = false;
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitSelectClause("+clause+")");
        }
        
    }

    @Override
    public void visitStringLiteral(StringLiteral l) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitStringLiteral("+l+")");
        }
        super.visitStringLiteral(l);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitStringLiteral("+l+")");
        }
        
    }

    @Override
    public void visitWhereClause(WhereClause clause) {
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitWhereClause("+clause+")");
        }
        super.visitWhereClause(clause);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitWhereClause("+clause+")");
        }
    }
    
    @Override
    public void visitSParamLiteral(){
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitSParamLiteral()");
        }
    }
    
    @Override
    public void visitSGroupByClause(SGroupByClause clause){
        if(LOG.isTraceEnabled()){
            LOG.trace("in visitSGroupByClause("+clause+")");
        }
        super.visitSGroupByClause(clause);
        if(LOG.isTraceEnabled()){
            LOG.trace("out visitSGroupByClause("+clause+")");
        }
    }
    
    
    protected void checkAlias(String key){
    	if(documentDataMapContext != null && documentDataMapContext.containsKey(key)){
            throw new QueryParseException("duplicate key ["+key+"] in parent query");
        }
    	if(documentDataMap.containsKey(key)){
            throw new QueryParseException("duplicate key ["+key+"]");            
        }
    }
    
    /**
     * Ajoute un nouvel objet DocumentData pour une clé donné.
     * Si un objet est deja associé à cette clé : léve une exception (signifie que cet clé référence deja un
     * objet dans la requete)
     * @param key
     * @param typeKeyList
     */
    protected void addDocumentData(String key, SFromKeyList typeKeyList){
    	checkAlias(key);
        documentDataMap.put(key, new DocumentData(key, typeKeyList));        
    }
    
    protected void addSubqueryAsDocumentData(String key){
    	checkAlias(key);
        documentDataMap.put(key, new DocumentData(key));
    }
    
    /**
     * Ajoute un nouvel objet Field a un DocumentData ou verifie qu'il est present dans un documentData d'une requete parente.
     * 
     * throw exception si ne peut trouver DocumentData ou si documentData est dans la requete parente mais que 
     * @param key
     * @param fieldName
     * @param isInSelect
     */
    protected void addField(String key, String fieldName, boolean isInSelect){
        DocumentData d = documentDataMap.get(key);
        if(d != null){
            d.addField(fieldName, isInSelect);
            return;
        } 
        
        if(documentDataMapContext != null) {
            d = documentDataMapContext.get(key);
            if(d.hasField(fieldName)){
                return;
            } else {
                throw new QueryParseException("Cannot manage inexisting field in parent query ["+key+"."+fieldName+"]");                
            } 
        }
        throw new QueryParseException("Cannot retrieve data for key ["+key+"]");       
    }
        
        
    public Map<String, DocumentData> getDocumentDataMap(){
    	return documentDataMap;
    }
}
