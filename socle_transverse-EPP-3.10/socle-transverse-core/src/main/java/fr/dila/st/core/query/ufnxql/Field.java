package fr.dila.st.core.query.ufnxql;

import org.nuxeo.ecm.core.storage.sql.ModelProperty;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;

/**
 * Contient les infos relatifs à une reference dans une requete
 * 
 * q.qu:auteur --> prefix = q, name = qu:auteur
 * 
 * @author spesnel
 * 
 */
public class Field {

	private final String	prefix;
	private final String	name;
	private boolean			isInSelect;

	/**
	 * contient la colonne associé à un champ si le champ correspond a une colonne null sinon
	 */
	private Column			column;

	/**
	 * contient les info lié à un champ, null si le champ correspond à une expression speciale ecm:xxx
	 */
	private ModelProperty	modelProperty;

	/**
	 * retourne le lien vers le type de document qu'on manipule
	 */
	private TableKey		tableKey;

	/**
	 * q.qu:auteur --> prefix = q, name = qu:auteur
	 * 
	 * @param prefix
	 * @param name
	 */
	public Field(String prefix, String name, boolean isInSelect) {
		this.prefix = prefix;
		this.name = name;
		this.isInSelect = isInSelect;
	}

	/**
	 * egalite porte sur name dans le cas d'une string
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Field) {
			Field fobj = (Field) obj;
			return name.equals(fobj.name) && prefix.equals(fobj.prefix);
		} else if (obj instanceof String) {
			return name.equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = 13;
		final int multiplier = 31;
		// Pour chaque attribut, on calcule le hashcode
		// que l'on ajoute au résultat après l'avoir multiplié
		// par le nombre "multiplieur" :
		result = multiplier * result + (name == null ? 0 : name.hashCode());
		result = multiplier * result + (prefix == null ? 0 : prefix.hashCode());

		// On retourne le résultat :
		return result;
	}

	public String getName() {
		return name;
	}

	public TableKey getTableKey() {
		return tableKey;
	}

	public void setTableKey(TableKey tableKey) {
		this.tableKey = tableKey;
	}

	public String toSql() {
		if (modelProperty != null && modelProperty.propertyType.isArray() && !isInSelect) {
			return column.getFullQuotedName();
		}
		return (tableKey == null ? "TKNULL" : tableKey.getKey()) + "."
				+ (column == null ? "COLNULL" : column.getQuotedName());
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public ModelProperty getModelProperty() {
		return modelProperty;
	}

	public void setModelProperty(ModelProperty modelProperty) {
		this.modelProperty = modelProperty;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getFullName() {
		return prefix + '.' + name;
	}

	public boolean isInSelect() {
		return isInSelect;
	}

	public void setInSelect(boolean isInSelect) {
		this.isInSelect = isInSelect;
	}

}
