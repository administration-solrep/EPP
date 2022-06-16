package fr.sword.naiad.nuxeo.commons.core.helper.querybuilder;

import org.apache.commons.lang3.StringUtils;

/**
 * Représente un champ et son éventuel alias.
 * 
 * @author fmh
 */
public class Field extends AbstractAliasElement {
	private final String fieldName;

	/**
	 * Construit le champ à partir de son nom, sans alias.
	 * 
	 * @param fieldName
	 *            Nom du champ.
	 */
	public Field(String fieldName) {		
		this(fieldName, null);
	}

	/**
	 * Construit le champ à partir de son nom et d'un éventuel alias.
	 * 
	 * @param fieldName
	 *            Nom du champ.
	 * @param alias
	 *            Alias du champ.
	 */
	public Field(String fieldName, String alias) {
		super(alias);
		this.fieldName = fieldName;
	}

	public String getField() {
		return fieldName;
	}

	@Override
	public String toString() {
		if (StringUtils.isNotEmpty(getAlias())) {
			return fieldName + " AS " + getAlias();
		} else {
			return fieldName;
		}
	}
	
	@Override
	public Object[] getParams(){
		return null;
	}
}
