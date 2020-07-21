package fr.dila.st.core.query.ufnxql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Regroupe un ensemble de table nécesssaire pour l'accés aux attributs manipuler dans la requete par type de documents
 * 
 * @author spesnel
 * 
 */
public class TableKeySet {

	private final Map<String, TableKey>	tableKeyMap;
	private final Set<TableKey>			tableKeySet;

	private final String				baseKey;
	private final String				referenceTableName;

	public TableKeySet(String baseKey, String referenceTableName) {
		this.baseKey = baseKey;
		tableKeyMap = new HashMap<String, TableKey>();
		tableKeySet = new HashSet<TableKey>();

		this.referenceTableName = referenceTableName;
		retrieveAndAddIfNotExistTable(referenceTableName);
	}

	public TableKey retrieveAndAddIfNotExistTable(String table) {
		if (table == null) {
			return null;
		}
		TableKey tk = tableKeyMap.get(table);
		if (tk == null) {
			tk = createAndAddTableKey(baseKey, table);
		}

		return tk;
	}

	public TableKey retrieveReferenceTable() {
		return retrieveAndAddIfNotExistTable(referenceTableName);
	}

	public Set<TableKey> getTableKeySet() {
		return tableKeySet;
	}

	private String generateKey(String baseKey, String tableName) {
		return baseKey + "_" + tableName.charAt(0) + "_" + tableKeySet.size();
	}

	private TableKey createAndAddTableKey(String baseKey, String tableName) {
		String key = generateKey(baseKey, tableName);
		TableKey tk = new TableKey(tableName, key);
		tableKeySet.add(tk);
		tableKeyMap.put(tableName, tk);
		return tk;
	}
}
