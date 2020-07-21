package fr.dila.st.core.query.ufnxql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Join;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;

public class DocumentData {
	private final String		key;
	private final String		type;
	private final Set<Field>	fields	= new HashSet<Field>();
	private final List<Join>	joins	= new ArrayList<Join>();

	/**
	 * contains the set of primary type to test empty if no primary type to test (exemple select * from Document)
	 */
	private Set<String>			subTypes;

	/**
	 * la table de reference utilisé les jointure Par défaut c'est la table hierarchy
	 */
	private Table				referenceTable;

	private TableKeySet			tableKeySet;

	public DocumentData(final String key, final String type) {
		this.key = key;
		this.type = type;
	}

	public TableKeySet getTableKeySet() {
		return tableKeySet;
	}

	public void setTableKeySet(final TableKeySet tableKeySet) {
		this.tableKeySet = tableKeySet;
	}

	public void addField(final String name, final boolean isInSelect) {

		final Field f = retrieveField(name);
		if (f == null) {
			final Field newField = new Field(key, name, isInSelect);
			fields.add(newField);
		} else {
			if (isInSelect) {
				f.setInSelect(true);
			}
		}
	}

	public Field retrieveField(final String name) {
		for (final Field f : fields) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		return null;
	}

	public boolean hasField(final String name) {
		final Field field = retrieveField(name);
		return field != null;
	}

	public String getKey() {
		return key;
	}

	public String getType() {
		return type;
	}

	public Set<Field> getFields() {
		return fields;
	}

	public Set<String> getSubTypes() {
		return subTypes;
	}

	public void setSubTypes(final Set<String> subTypes) {
		this.subTypes = subTypes;
	}

	public void addJoin(final Join j) {
		joins.add(j);
	}

	public void addJoins(final Collection<Join> cj) {
		joins.addAll(cj);
	}

	public List<Join> getOrderedJoins(final Map<String, Integer> mapPoidsSchemaForJoin) {

		Collections.sort(joins, new Comparator<Join>() {

			@Override
			public int compare(final Join join1, final Join join2) {
				Integer poids1 = 0;
				Integer poids2 = 0;
				if (mapPoidsSchemaForJoin != null) {
					Integer value = mapPoidsSchemaForJoin.get(join1.getTable());
					if (value != null) {
						poids1 = value;
					}

					value = mapPoidsSchemaForJoin.get(join2.getTable());
					if (value != null) {
						poids2 = value;
					}
				}
				return poids2.compareTo(poids1);
			}

		});

		return joins;
	}

	public Table getReferenceTable() {
		return referenceTable;
	}

	public void setReferenceTable(final Table referenceTable) {
		this.referenceTable = referenceTable;
	}

	public String getReferenceTableName() {
		return referenceTable.getPhysicalName();
	}

	public Column getReferenceTableColumnId() {
		return referenceTable.getColumn(Model.MAIN_KEY);
	}

	public String getReferenceTableColumnIdName() {
		return getReferenceTableColumnId().getQuotedName();
	}

	public TableKey getReferenceTableKey() {
		return getTableKeySet().retrieveReferenceTable();
	}
}
