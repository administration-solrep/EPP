package fr.dila.st.core.query;

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

	/**
	 * Table used only to provide getDialect() called in Column constructor
	 * 
	 * @author spesnel
	 * 
	 */
	private static class MyTableImpl implements Table {

		private static final long	serialVersionUID	= 1L;

		private Dialect				dialect;

		public MyTableImpl(Dialect dialect) {
			this.dialect = dialect;
		}

		@Override
		public Column addColumn(String arg0, ColumnType arg1, String arg2, Model arg3) {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public void addFulltextIndex(String arg0, String... arg1) {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public void addIndex(String... arg0) {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public String getAddColumnSql(Column arg0) {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public Column getColumn(String arg0) {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public Collection<Column> getColumns() {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public String getCreateSql() {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public Dialect getDialect() {
			return dialect;
		}

		@Override
		public String getDropSql() {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public List<String> getPostAddSqls(Column arg0, Model arg1) {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public List<String> getPostCreateSqls(Model arg0) {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public String getQuotedName() {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public String getQuotedSuffixedName(String arg0) {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public Table getRealTable() {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public boolean hasFulltextIndex() {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public boolean isAlias() {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public String getKey() {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public String getPhysicalName() {
			throw new UnsupportedOperationException("Not implemented");
		}

		// required for 1.6-2
		@Override
		public Column getPrimaryColumn() {
			throw new UnsupportedOperationException("Not implemented");
		}
	}

	/**
	 * Default constructor
	 */
	private ColumnUtil() {
		// do nothing
	}

	public static Column createCountColumn(Dialect dialect) {
		Table table = new MyTableImpl(dialect);
		return new Column(table, null, ColumnType.INTEGER, null);
	}

	/**
	 * cree un mapMaker pour une requete retournant un count(*) : donc une colonne, une ligne Le resultat est accessible
	 * dans la map par la clé COL_COUNT
	 * 
	 * @param dialect
	 * @return
	 */
	public static ColumnMapMaker createMapMakerForCount(Dialect dialect, String colName) {
		Column col = createCountColumn(dialect);
		List<Column> whatColumns = Collections.singletonList(col);
		List<String> whatKeys = Collections.singletonList(colName);
		return new ColumnMapMaker(whatColumns, whatKeys);
	}
}
