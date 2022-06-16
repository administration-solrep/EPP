package fr.sword.naiad.nuxeo.commons.core.helper.querybuilder;

/**
 * Représente un critère de tri.
 * 
 * @author fmh
 */
public class OrderDirective {
	
	private final String field;

	private final boolean ascendant;

	/**
	 * Construit le critère de tri à partir du nom du champ.
	 * 
	 * @param field
	 *            Nom du champ.
	 */
	public OrderDirective(String field) {
		this(field, true);
	}

	/**
	 * Construit le critère de tri à partir du nom du champ et du sens du tri.
	 * 
	 * @param field
	 *            Nom du champ.
	 * @param ascendant
	 *            Sens du tri.
	 */
	public OrderDirective(String field, boolean ascendant) {
		this.field = field;
		this.ascendant = ascendant;
	}

	public String getField() {
		return field;
	}

	public boolean isAscendant() {
		return ascendant;
	}

	@Override
	public String toString() {
		if (ascendant) {
			return field + " ASC";
		}
		return field + " DESC";
	}
}
