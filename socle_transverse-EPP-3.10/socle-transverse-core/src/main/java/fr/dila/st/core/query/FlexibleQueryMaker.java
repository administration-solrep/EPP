package fr.dila.st.core.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.query.QueryFilter;
import org.nuxeo.ecm.core.storage.StorageException;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.ModelProperty;
import org.nuxeo.ecm.core.storage.sql.Session.PathResolver;
import org.nuxeo.ecm.core.storage.sql.jdbc.NXQLQueryMaker;
import org.nuxeo.ecm.core.storage.sql.jdbc.QueryMaker;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo.ColumnMapMaker;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo.SQLInfoSelect;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Database;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.DialectOracle;

import fr.dila.st.core.query.ufnxql.UFNXQLQueryMaker;

/**
 * Enable to do count query over NXQL query or skip NX_ACCESS_ALLOWED test in query
 * 
 * Use NXQLQueryMaker to generate the SQL query. And then encapsule it in a 'select count(*) from (xxxx);' to retrieve
 * the count of element
 * 
 * Format des requetes FNXQL : (<mot cle>[<arg>]*)*<query> Exemple :
 * 
 * "count:select * from Dossier" "sql:[dc:title,count]select title, count(*) from dublincore group by title"
 * 
 * @author spesnel
 * 
 */
public abstract class FlexibleQueryMaker implements QueryMaker {

	private static final Log					LOGGER								= LogFactory
																							.getLog(FlexibleQueryMaker.class);

	protected static final Map<String, Integer>	DEFAULT_MAP_POIDS_SCHEMA_FOR_JOIN	= new HashMap<String, Integer>();

	static {
		// permet l'ordonnancement des JOIN si poids1 > poids2 alors TABLE1 sera avant TABLE2 dans les JOIN
		DEFAULT_MAP_POIDS_SCHEMA_FOR_JOIN.put(Model.HIER_TABLE_NAME, 1000);
		DEFAULT_MAP_POIDS_SCHEMA_FOR_JOIN.put(Model.VERSION_TABLE_NAME, 100);
		DEFAULT_MAP_POIDS_SCHEMA_FOR_JOIN.put(Model.HIER_READ_ACL_TABLE_NAME, -1000); // testAcl le plus loin
																						// possible...
	}

	/**
	 * mot cle pour identifier le type de language
	 */
	public static final String					QUERY_TYPE							= "FNXQL";

	public static final String					COL_COUNT							= "count";
	public static final String					COL_ID								= "id";

	/**
	 * Identifie le type de colonne spécifique "owner" pour la table LOCK
	 */
	public static final String					COL_LOCK_OWNER						= "owner";

	public enum KeyCode {

		NO_DB_ACL_CHECK_CODE("noacl:"), COUNT_CODE("count:"), SQL_CODE("sql:"), SQL_MIX_CODE("sqlmix:"), PAGE("page:"), UFXNQL(
				"ufnxql:");

		public String	key;

		private KeyCode(final String key) {
			this.key = key;
		}

		public boolean match(final String query) {
			return query != null && query.startsWith(key);
		}
	}

	/**
	 * map type de document, schema reference. la table associé au schema reference sera utilisé pour trouver les
	 * documents du type donné dans les requete UNFXQL plutot que la méthode utilisant Hierarchy et un test du
	 * PrimaryType
	 * 
	 */
	private final Map<String, String>	mapTypeSchemaForOptimUFNXQL;

	protected FlexibleQueryMaker(final Map<String, String> mapTypeSchemaForOptimUFNXQL) {
		this.mapTypeSchemaForOptimUFNXQL = mapTypeSchemaForOptimUFNXQL;
	}

	private Map<String, Integer> rebuildMapPoids(final Database database) {

		final Map<String, Integer> correctMap = new HashMap<String, Integer>();

		for (final String key : DEFAULT_MAP_POIDS_SCHEMA_FOR_JOIN.keySet()) {
			final Table table = database.getTable(key);
			final Integer value = DEFAULT_MAP_POIDS_SCHEMA_FOR_JOIN.get(key);
			if (table == null) {
				correctMap.put(key, value);
			} else {
				correctMap.put(table.getPhysicalName(), value);
			}
		}

		return correctMap;
	}

	@Override
	public boolean accepts(final String type) {
		return QUERY_TYPE.equals(type);
	}

	@Override
	public Query buildQuery(final SQLInfo sqlInfo, final Model model, final PathResolver pathResolver,
			final String query, final QueryFilter queryFilter, final Object... params) throws StorageException {

		final FlexibleQueryInfos infos = new FlexibleQueryInfos(query);

		Query queryToBuild = null;

		QueryFilter queryFilterToUse = queryFilter;

		if (infos.hasKeyCode(KeyCode.NO_DB_ACL_CHECK_CODE)) {
			queryFilterToUse = new QueryFilter(queryFilter.getPrincipal(), null, queryFilter.getPermissions(), null,
					queryFilter.getQueryTransformers(), 0, 0);
		}

		if (infos.hasKeyCode(KeyCode.SQL_CODE) || infos.hasKeyCode(KeyCode.SQL_MIX_CODE)) {

			if (infos.hasKeyCode(KeyCode.SQL_CODE) && infos.hasKeyCode(KeyCode.SQL_MIX_CODE)) {
				throw new QueryMakerException("INCOMPATIBLE TYPE : " + KeyCode.SQL_CODE + " and "
						+ KeyCode.SQL_MIX_CODE);
			}

			final KeyCode keyCode = infos.hasKeyCode(KeyCode.SQL_CODE) ? KeyCode.SQL_CODE : KeyCode.SQL_MIX_CODE;

			final List<String> args = infos.getArgs(keyCode);
			if (args == null || args.size() != 1) {
				throw new QueryMakerException("Unexpected args number for " + keyCode + " "
						+ (args == null ? 0 : args.size()) + " instead of 1");
			}
			final String[] colnames = args.get(0).split(",");

			queryToBuild = new Query();

			final List<Serializable> paramList = new ArrayList<Serializable>();
			if (params != null) {
				for (int i = 0; i < params.length; ++i) {
					if (params[i] instanceof Serializable) {
						paramList.add((Serializable) params[i]);
					} else {
						throw new QueryMakerException("PARAMS NOT SERIALIZABLE [" + i + "]:" + params[i].toString());
					}
				}
			}

			queryToBuild.selectParams = paramList;

			String sql = infos.getQueryWithoutCode();

			if (infos.hasKeyCode(KeyCode.SQL_MIX_CODE)) {
				if (params != null && params.length > 0) {
					throw new QueryMakerException("params not supported for SQL_MIX");
				}
				String toAnalyse = sql;
				StringBuffer strBuff = new StringBuffer();
				while (toAnalyse.length() > 0) {
					final int idxStart = toAnalyse.indexOf('{');
					if (idxStart < 0) {
						strBuff.append(toAnalyse);
						toAnalyse = "";
						break;
					}
					final int idxEnd = toAnalyse.indexOf('}');
					if (idxEnd < 0) {
						throw new QueryMakerException("{ not closed");
					}

					final String start = toAnalyse.substring(0, idxStart);
					final String end = toAnalyse.substring(idxEnd + 1);
					final String part = toAnalyse.substring(idxStart + 1, idxEnd);

					final NXQLQueryMaker maker = new NXQLQueryMaker();
					final Query tmpq = maker.buildQuery(sqlInfo, model, pathResolver, part, queryFilterToUse, params);

					queryToBuild.selectParams.addAll(tmpq.selectParams);

					strBuff.append(start).append(tmpq.selectInfo.sql);
					toAnalyse = end;
				}
				sql = strBuff.toString();
			}

			final List<Column> whatColumns = new ArrayList<Column>();
			final List<String> whatKeys = new ArrayList<String>();
			for (String colname : colnames) {
				final String name = colname.trim();
				final Column column = retrieveColumn(sqlInfo, model, name);

				whatColumns.add(column);
				whatKeys.add(name);
			}

			final ColumnMapMaker mapMaker = new ColumnMapMaker(whatColumns, whatKeys);
			queryToBuild.selectInfo = new SQLInfoSelect(sql, mapMaker);

		} else if (infos.hasKeyCode(KeyCode.UFXNQL)) {
			final UFNXQLQueryMaker maker = new UFNXQLQueryMaker(mapTypeSchemaForOptimUFNXQL,
					rebuildMapPoids(sqlInfo.getDatabase()));
			// Désactive l'optimisation des acls
			queryToBuild = maker.buildQuery(sqlInfo, model, pathResolver, infos.getQueryWithoutCode(),
					queryFilterToUse, params);
		} else {
			// build the query from the NXQL query part
			final NXQLQueryMaker maker = new NXQLQueryMaker();
			queryToBuild = maker.buildQuery(sqlInfo, model, pathResolver, infos.getQueryWithoutCode(),
					queryFilterToUse, params);
		}

		if (infos.hasKeyCode(KeyCode.PAGE)) {
			final List<String> args = infos.getArgs(KeyCode.PAGE);
			if (args.size() != 2) {
				throw new QueryMakerException("Unexpected number of arg for PAGE");
			}
			final Long limit = Long.valueOf(args.get(0));
			final Long offset = Long.valueOf(args.get(1));

			if (sqlInfo.dialect instanceof DialectOracle) {
				final long end = offset + limit;
				if (offset > 0) {
					// SPL
					final StringBuilder strBuffer = new StringBuilder(
							"select * from ( select TTSUBQ.* , rownum r FROM (").append(queryToBuild.selectInfo.sql)
							.append(") TTSUBQ where rownum <= ?) where r > ?");
					queryToBuild.selectInfo = new SQLInfoSelect(strBuffer.toString(), queryToBuild.selectInfo.mapMaker);

					queryToBuild.selectParams.add(end);
					queryToBuild.selectParams.add(offset);

				} else {

					final StringBuilder strBuffer = new StringBuilder("SELECT * FROM (").append(
							queryToBuild.selectInfo.sql).append(") where rownum <= ?");
					queryToBuild.selectInfo = new SQLInfoSelect(strBuffer.toString(), queryToBuild.selectInfo.mapMaker);

					queryToBuild.selectParams.add(end);
				}
			} else {
				final StringBuilder strBuffer = new StringBuilder()// "select * from ( select TTSUBQ.* , rownum r FROM (")
						.append(queryToBuild.selectInfo.sql).append(" LIMIT ? OFFSET ?");
				queryToBuild.selectInfo = new SQLInfoSelect(strBuffer.toString(), queryToBuild.selectInfo.mapMaker);

				queryToBuild.selectParams.add(limit);
				queryToBuild.selectParams.add(offset);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("PAGE[" + queryToBuild.selectInfo.sql + "]");
			}
		}

		if (infos.hasKeyCode(KeyCode.COUNT_CODE)) {
			final String sql = "select count(*) from (" + queryToBuild.selectInfo.sql + ")";
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("COUNT[" + sql + "]");
			}
			queryToBuild.selectInfo = new SQLInfoSelect(sql, ColumnUtil.createMapMakerForCount(sqlInfo.dialect,
					COL_COUNT));
		}

		return queryToBuild;
	}

	@Override
	public String getName() {
		return QUERY_TYPE;
	}

	private Column retrieveColumn(final SQLInfo sqlInfo, final Model model, final String name) {
		Column column = null;
		final ModelProperty propertyInfo = model.getPropertyInfo(name);
		if (propertyInfo != null) {
			final Database database = sqlInfo.database;
			final Table table = database.getTable(propertyInfo.fragmentName);
			column = table.getColumn(propertyInfo.fragmentKey);
		}

		if (column == null) {
			column = retrieveParticularColumn(sqlInfo, model, name);
		}

		if (column == null) {
			throw new QueryMakerException("Unknown field: " + name);
		} else {
			return column;
		}
	}

	private Column retrieveParticularColumn(final SQLInfo sqlInfo, final Model model, final String name) {
		if (COL_COUNT.equals(name)) {
			return ColumnUtil.createCountColumn(sqlInfo.dialect);
		} else if (COL_ID.equals(name)) {
			final Database database = sqlInfo.database;
			final Table hier = database.getTable(Model.HIER_TABLE_NAME);
			return hier.getColumn(Model.MAIN_KEY);
		} else if (COL_LOCK_OWNER.equals(name)) {
			final Database database = sqlInfo.database;
			final Table hier = database.getTable(Model.LOCK_TABLE_NAME);
			return hier.getColumn(Model.LOCK_OWNER_KEY);
		} else {
			return null;
		}
	}

}
