package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql;

/**
 * Associe un nom de table et un alias qui sera utilisé dans la requete SQL
 * 
 * @author spesnel
 *
 */
public class TableKey {
    
    private static final Object RESERVED_ORACLE_COMMENT = "COMMENT";
    private final String tableName;
    private final String key;
    
    
    public TableKey(String tableName, String key){
        this.tableName = tableName;
        this.key = key;
    }

    public String getKey() {
        return key.toLowerCase();
    }

    public String getTableName() {
        // Si le nom de la table est Comment, on ajoute les double guillements pour conserver la comptabilité avec Oracle.
        if (RESERVED_ORACLE_COMMENT.equals(tableName)){
            return "\"" + RESERVED_ORACLE_COMMENT + "\"";
        }
        return tableName;
    }
    
}
