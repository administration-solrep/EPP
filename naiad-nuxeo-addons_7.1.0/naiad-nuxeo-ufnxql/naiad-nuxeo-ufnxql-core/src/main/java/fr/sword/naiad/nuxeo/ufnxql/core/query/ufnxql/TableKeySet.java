package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Regroupe un ensemble de table nécesssaire pour l'accés aux attributs manipulés 
 * dans la requête par type de documents
 * 
 * @author spesnel
 *
 */
public final class TableKeySet {
    
    private final Map<String, TableKey> tableKeyMap;
    private final Set<TableKey> tableKeys;
    
    private final String baseKey;
    private final String referenceTableName;
    
    public TableKeySet(String baseKey, String referenceTableName){
        this.baseKey = baseKey;
        tableKeyMap = new HashMap<String, TableKey>();
        tableKeys = new HashSet<TableKey>();
        
        this.referenceTableName = referenceTableName;
        retrieveAndAddIfNotExistTable(referenceTableName);
    }
    
    public TableKey retrieveAndAddIfNotExistTable(String table){       
        if(table == null){
            return null;
        }
        TableKey tkey = tableKeyMap.get(table);
        if(tkey == null){            
            tkey = createAndAddTableKey(baseKey, table);
        }
        
        return tkey;
    }

    public TableKey retrieveReferenceTable(){
        return retrieveAndAddIfNotExistTable(referenceTableName);
    }
    
    public Set<TableKey> getTableKeySet() {
        return tableKeys;
    }    
    
    
    
    private String generateKey(String baseKey, String tableName){
        return baseKey + "_" +tableName.charAt(0)+"_"+ tableKeys.size();
    }
    
    private TableKey createAndAddTableKey(String baseKey, String tableName){
        String key = generateKey(baseKey, tableName);
        TableKey tk = new TableKey(tableName, key);
        tableKeys.add(tk);
        tableKeyMap.put(tableName, tk);
        return tk;
    }    
}
