package fr.dila.st.core.query.ufnxql;

import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.TableAlias;

/**
 * Etends TableAlias pour offrir une implementation qui ne met pas de quote autour de l'alias quand on appelle
 * getQuotedName. - H2 ne supporte pas les quote autour de l'alias d'une table. - Permet de réutiliser le code de nuxeo
 * pour la gestion des expression "ecm:fulltext"
 * 
 * @author spesnel
 * 
 */
public class TableSymbolicKey extends TableAlias {

	/**
     * 
     */
	private static final long	serialVersionUID	= -324465815788265646L;

	public TableSymbolicKey(Table table, String alias) {
		super(table, alias);
	}

	/**
	 * quoted name does not return quoted name in caseor table is represented by symbolic key
	 */
	@Override
	public String getQuotedName() {
		return alias;
	}

}
