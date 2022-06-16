package fr.sword.naiad.nuxeo.ufnxql.core.query;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.nuxeo.ecm.core.storage.sql.ColumnType;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo.ColumnMapMaker;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.Dialect;

/**
 * utility class used to provide fake column
 * 
 * @author spesnel
 * 
 */
public final class ColumnUtil {

	private static final String NOT_IMPL_MSG = "Not implemented";
	
	/**
	 * Table used only to provide getDialect() called in Column constructor
	 * 
	 * @author spesnel
	 * 
	 */
	private static class MyTableImpl implements Table {

		private static final long serialVersionUID = 1L;

		private final Dialect dialect;

		public MyTableImpl(final Dialect dialect) {
			this.dialect = dialect;
		}

		@Override
		public Column addColumn(final String arg0, final ColumnType arg1, final String arg2, final Model arg3) {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public void addIndex(final String... arg0) {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public String getAddColumnSql(final Column arg0) {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public Column getColumn(final String arg0) {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public Collection<Column> getColumns() {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public String getCreateSql() {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public Dialect getDialect() {
			return dialect;
		}

		@Override
		public String getDropSql() {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public List<String> getPostAddSqls(final Column arg0, final Model arg1) {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public List<String> getPostCreateSqls(final Model arg0) {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public String getQuotedName() {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public String getQuotedSuffixedName(final String arg0) {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public Table getRealTable() {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public boolean hasFulltextIndex() {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public boolean isAlias() {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public String getKey() {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public String getPhysicalName() {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		// required for 1.6-2
		@Override
		public Column getPrimaryColumn() {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);
		}

		@Override
		public void addIndex(final String arg0, final IndexType arg1, final String... arg2) {
			throw new UnsupportedOperationException(NOT_IMPL_MSG);

		}
	}
	
	/**
	 * Classe utilitaire
	 */
	private ColumnUtil() {
		// do nothing
	}

	public static Column createCountColumn(final Dialect dialect) {
		final Table table = new MyTableImpl(dialect);
		return new Column(table, null, ColumnType.INTEGER, null);
	}

	/**
	 * cree un mapMaker pour une requete retournant un count(*) : donc une
	 * colonne, une ligne Le resultat est accessible dans la map par la clé
	 * COL_COUNT
	 * 
	 * @param dialect
	 * @return
	 */
	public static ColumnMapMaker createMapMakerForCount(final Dialect dialect, final String colName) {
		final Column col = createCountColumn(dialect);
		final List<Column> whatColumns = Collections.singletonList(col);
		final List<String> whatKeys = Collections.singletonList(colName);
		return new ColumnMapMaker(whatColumns, whatKeys);
	}

	
}
