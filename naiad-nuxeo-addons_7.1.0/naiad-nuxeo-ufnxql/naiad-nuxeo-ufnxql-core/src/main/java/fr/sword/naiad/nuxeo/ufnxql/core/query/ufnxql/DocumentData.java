package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Join;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SFromKeyList;

public class DocumentData {
    private final String key;
    private final SFromKeyList typeKeyList;
    private final Set<Field> fields = new HashSet<Field>();
    private final List<Join> joins = new ArrayList<Join>();
    private boolean subquery = false;
    
    /**
     * contains the set of primary type to test
     * empty if no primary type to test (exemple select * from Document)
     */
    private Set<String> subTypes;
    
    /**
     * la table de reference utilisé les jointure
     * Par défaut c'est la table hierarchy
     */
    private Table referenceTable;
    
    private TableKeySet tableKeySet;
    
    public DocumentData(String key, SFromKeyList typeKeyList){
        this.key = key;
        this.typeKeyList = typeKeyList;
        this.referenceTable = null;
    }

    public DocumentData(String key){
    	this.key = key;
    	this.typeKeyList = null;
        this.referenceTable = null;
        this.subquery = true;
    }
    
    public TableKeySet getTableKeySet() {
        return tableKeySet;
    }

    public void setTableKeySet(TableKeySet tableKeySet) {
        this.tableKeySet = tableKeySet;
    }
    
    public void addField(String name, boolean isInSelect){
        
        Field field = retrieveField(name);
        if(field == null){        
            Field newField = new Field(key, name, isInSelect);
            fields.add(newField);
        } else {
            if(isInSelect){
                field.setInSelect(true);
            }
        }
    }
    
    public Field retrieveField(String name){
        for(Field f : fields){
            if(f.getName().equals(name)){
                return f;
            }
        }
        return null;
    }
    
    public boolean hasField(String name){
        Field field = retrieveField(name);
        return field != null;
    }

    public String getKey() {
        return key;
    }

    public SFromKeyList getType() {
        return typeKeyList;
    }

    public Set<Field> getFields() {
        return fields;
    }

    public Set<String> getSubTypes() {
        return subTypes;
    }

    public void setSubTypes(Set<String> subTypes) {
        this.subTypes = subTypes;
    }
 
    public void addJoin(Join join){
        joins.add(join);
    }
    public void addJoins(Collection<Join> cjoins){
        joins.addAll(cjoins);
    }

    public List<Join> getJoins() {
        return joins;
    }

    public Table getReferenceTable() {
        return referenceTable;
    }

    public void setReferenceTable(Table referenceTable) {
        this.referenceTable = referenceTable;
    }
    
    public String getReferenceTableName() {
        return referenceTable.getPhysicalName();
    }
    
    public Column getReferenceTableColumnId(){
        return referenceTable.getColumn(Model.MAIN_KEY);
    }
    
    public String getReferenceTableColumnIdName(){
        return getReferenceTableColumnId().getQuotedName();
    }
    
    public TableKey getReferenceTableKey(){
        return getTableKeySet().retrieveReferenceTable();
    }
    
    public boolean isSubquery(){
    	return subquery;
    }
}
